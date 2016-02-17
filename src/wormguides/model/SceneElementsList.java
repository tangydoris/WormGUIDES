package wormguides.model;
/*
 * Reference class for Scene Elements over life of embryo
 * Data structure which contains SceneElements
 *
 * Created: 0ct. 30, 2015
 * Author: Braden Katzman
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class SceneElementsList {
	
	public ArrayList<SceneElement> elementsList;
	public HashMap<String, ArrayList<String>> nameCellsMap;
	public HashMap<String, String> nameCommentsMap;
	//private ArrayList<File> objEntries;
	
	
	//this will eventually be constructed using a .txt file that contains the Scene Element information for the embryo
	public SceneElementsList() {
		elementsList = new ArrayList<SceneElement>();
		//objEntries = new ArrayList<File>();
		nameCellsMap = new HashMap<String, ArrayList<String>>();
		nameCommentsMap = new HashMap<String, String>();
		
		buildListFromConfig();
	}
	
	
	private void buildListFromConfig() {
		
		URL url = SceneElementsList.class.getResource("/wormguides/model/shapes_file/" + CELL_CONFIG_FILE_NAME);
		
		try {
			if (url != null) {
				InputStream stream = url.openStream();
				processStreamString(stream);
				processCells();
			}
			
			
			/*
			 * when/how is this used? --> can't open folders using getResource
			 */
			
			//add obj entries
//			URL objFilesDir = SceneElementsList.class.getResource("wormguides/model/obj_files/");
//			if (objFilesDir != null) {
//				File[] contents = new File(objFilesDir.toURI()).listFiles();
//				for (File file : contents) {
//					objEntries.add(file);
//				}
//			}
 		} catch (FileNotFoundException e) {
			System.out.println("The config file '" + CELL_CONFIG_FILE_NAME + "' wasn't found on the system.");
		} catch (IOException e) {
			System.out.println("The config file '" + CELL_CONFIG_FILE_NAME + "' wasn't found on the system.");
		}
	}

	private void processStreamString(InputStream stream) {
		InputStreamReader streamReader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(streamReader);
		
		try {
			reader.readLine();
			
			String line;
			// process each line
			while ((line = reader.readLine()) != null) {
				String[] splits =  line.split(",", 8);
				
				//BUIILD SCENE ELEMENT			
				// vector of cell names
				ArrayList<String> cellNames = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(splits[1]);
				while (st.hasMoreTokens()) {
					cellNames.add(st.nextToken());
				}
				
				try {
					SceneElement se = new SceneElement(//objEntries,
							splits[0], cellNames,
							splits[2], splits[3], splits[4],
							Integer.parseInt(splits[5]), Integer.parseInt(splits[6]),
							splits[7]);
					
					// add scene element to list
					elementsList.add(se);
					addComments(se);
					
				} catch (NumberFormatException e) {
					System.out.println("error in reading scene element time for line "+line);
				}
			}
			
			reader.close();
		} catch (IOException e) {
			System.out.println("Invalid file: '" + CELL_CONFIG_FILE_NAME);
			return;
		}
	}
	
	/*
	 * the start of our recursive algorithm to unpack entries 
	 * in cell names which reference other scene elements' cell list
	 */
	private void processCells() {
		if (elementsList == null) return;
		
		for (SceneElement se : elementsList) {
			ArrayList<String> cells = se.getAllCellNames();
			for (int i = 0; i < cells.size(); i++) {
				if (cells.get(i).startsWith(asterisk)) {
					se.setNewCellNames(unpackCells(cells));
				}
			}
		}
	}
	
	private ArrayList<String> unpackCells(ArrayList<String> cells) {
		ArrayList<String> unpackedCells = new ArrayList<String>();
		
		for (int i = 0; i < cells.size(); i++) {
			String cell = cells.get(i);
			//if cell starts with asterisk, recurse. else, add cel
			if (cell.startsWith(asterisk)) {
				for (int j = 0; j < elementsList.size(); j++) {
					SceneElement se = elementsList.get(j);
					
					//find the matching resource location
					if (se.getResourceLocation().endsWith(cell.substring(1))) {
						//recursively unpack matching location's cell list
						unpackedCells.addAll(unpackCells(se.getAllCellNames()));
					}
				}
			} else {
				//only add cell name entry if not already added
				if (!unpackedCells.contains(cell)) {
					unpackedCells.add(cell);
				}
			}
		}
		
		return unpackedCells;
	}
	
	
	/*
	 * Returns the biological time (without frame offset) of the 
	 * first occurrence of element with scene name, name
	 */
	public int getFirstOccurrenceOf(String name) {
		int time = Integer.MIN_VALUE;
		
		for (SceneElement element : elementsList) {
			if (element.getSceneName().equalsIgnoreCase(name))
				time = element.getStartTime();
		}

		return time+1;
	}
	
	
	/*
	 * Returns the biological time (without frame offset) of the 
	 * last occurrence of element with scene name, name
	 */
	public int getLastOccurrenceOf(String name) {
		int time = Integer.MIN_VALUE;
		
		for (SceneElement element : elementsList) {
			if (element.getSceneName().equalsIgnoreCase(name))
				time = element.getEndTime();
		}

		return time+1;
	}
	
	private void addCells(SceneElement element) {
		if (element != null && element.isMulticellular()) {
			nameCellsMap.put(element.getSceneName().toLowerCase(), element.getAllCellNames());
		}
	}
	
	private void addComments(SceneElement element) {
		if (element!=null && element.isMulticellular())
			nameCommentsMap.put(element.getSceneName().toLowerCase(), element.getComments());
	}
	
	
	public void addSceneElement(SceneElement element) {
		if (element!=null)
			elementsList.add(element);
		
		addComments(element);
	}
	
	
	public String[] getSceneElementNamesAtTime(int time) {		
		// Add lineage names of all structures at time
		ArrayList<String> list = new ArrayList<String>();
		for (SceneElement se : elementsList) {
			if (se.existsAtTime(time))
				if (se.isMulticellular())
					list.add(se.getSceneName());
				else
					list.add(se.getAllCellNames().get(0));
		}
		
		return list.toArray(new String[list.size()]);
	}

	
	public ArrayList<SceneElement> getSceneElementsAtTime(int time) {
		ArrayList<SceneElement> sceneElements = new ArrayList<SceneElement>();
		for (int i = 0; i < elementsList.size(); i++) {
			SceneElement se = elementsList.get(i);
			if (se.existsAtTime(time)) {
				sceneElements.add(se);
			}
		}
		return sceneElements;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder("scene elements list:\n");
		for (SceneElement se : elementsList) {
			sb.append(se.getSceneName()).append("\n");
		}
		return sb.toString();
	}
	
	
	public ArrayList<SceneElement> getList() {
		return elementsList;
	}
	
	
	public ArrayList<String> getAllMulticellSceneNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (SceneElement se : elementsList) {
			if (se.isMulticellular() && !names.contains(se))
				names.add(se.getSceneName());
		}
		return names;
	}
	
	
	public ArrayList<SceneElement> getMulticellSceneElements() {
		ArrayList<SceneElement> elements = new ArrayList<SceneElement>();
		for (SceneElement se : elementsList) {
			if (se.isMulticellular() && !elements.contains(se))
				elements.add(se);
		}
		return elements;
	}
	
	
	public boolean isMulticellStructureName(String name) {
		for (String cellName : getAllMulticellSceneNames()) {
			if (cellName.equalsIgnoreCase(name.trim()))
				return true;
		}
		return false;
	}
	
	
	public String getCommentByName(String name) {
		String comment = nameCommentsMap.get(name.trim().toLowerCase());
		if (comment==null)
			return "";
		return comment;
	}
	
	
	public HashMap<String, String> getNameToCommentsMap() {
		return nameCommentsMap;
	}
	
	public HashMap<String, ArrayList<String>> getNameToCellsMap() {
		return this.nameCellsMap;
	}
	
	
	private final static String CELL_CONFIG_FILE_NAME = "CellShapesConfig.csv";
	private final static String asterisk = "*";
}