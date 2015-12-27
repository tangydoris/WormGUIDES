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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SceneElementsList {
	
	public ArrayList<SceneElement> elementsList;
	public HashMap<String, String> multicellNamesToCommentsMap;
	private JarFile jarFile;
	private ArrayList<JarEntry> objEntries;

	
	//this will eventually be constructed using a .txt file that contains the Scene Element information for the embryo
	public SceneElementsList() {
		elementsList = new ArrayList<SceneElement>();
		objEntries = new ArrayList<JarEntry>();
		multicellNamesToCommentsMap = new HashMap<String, String>();
	}
	
	
	public void buildListFromConfig() {
		try {
			jarFile = new JarFile(new File("WormGUIDES.jar"));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				
				if (entry.getName().equals("wormguides/model/shapes_file/"+CELL_CONFIG_FILE_NAME)) {
					InputStream stream = jarFile.getInputStream(entry);
					processStreamString(stream);
				}
				else if (entry.getName().startsWith("wormguides/model/obj_file/"))
					objEntries.add(entry);
				
			}
			
			jarFile.close();
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
				
				boolean billboardFlag = false;
				if (splits[4].contains("BILLBOARD")) {
					billboardFlag = true;
				}
				
				//vector of cell names
				ArrayList<String> cellNames = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(splits[1]);
				while (st.hasMoreTokens()) {
					cellNames.add(st.nextToken());
				}
				
				//check if complete resource location
				boolean completeResourceFlag = true;
				String resourceLocation = splits[4];
				int idx = resourceLocation.lastIndexOf(".");
				if (idx != -1) {
					String extCheck = resourceLocation.substring(++idx); //substring after "."
					for (int i = 0 ; i < extCheck.length(); i++) {
						if (!Character.isLetter(extCheck.charAt(i))) {
							completeResourceFlag = false;
						}
					}
				} else {
					completeResourceFlag = false;
				}
				
				SceneElement se = new SceneElement(//objEntries,
						splits[0], cellNames,
						splits[2], splits[3], splits[4],
						Integer.parseInt(splits[5]), Integer.parseInt(splits[6]),
						splits[7], completeResourceFlag, billboardFlag);
				
				//add scene element to list
				elementsList.add(se);
			}
			
			reader.close();
		} catch (IOException e) {
			System.out.println("Invalid file: '" + CELL_CONFIG_FILE_NAME);
			return;
		}
		
		pickOutMulticellNames();
	}
	
	
	public void addSceneElement(SceneElement element) {
		if (element!=null)
			elementsList.add(element);
	}
	
	
	// Pick out all multicellular names and map them to their comments
	private void pickOutMulticellNames() {
		for (int i = 0; i < elementsList.size(); i++) {
			SceneElement current = elementsList.get(i);
			//check if the scene element is a multicellular structure
			if (current.isMulticellular()) {
				multicellNamesToCommentsMap.put(current.getSceneName(), current.getComments());
			}
		}
	}
	
	
	public String[] getSceneElementNamesAtTime(int time) {
		time++;
		
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
		time++;
		ArrayList<SceneElement> sceneElements = new ArrayList<SceneElement>();
		for (int i = 0; i < elementsList.size(); i++) {
			SceneElement se = elementsList.get(i);
			if (se.existsAtTime(time)) {
				sceneElements.add(se);
			}
		}
		return sceneElements;
	}
	
	
	public ArrayList<SceneElement> getList() {
		return elementsList;
	}
	
	
	public Set<String> getAllMulticellNames() {
		return multicellNamesToCommentsMap.keySet();
	}
	
	
	public HashMap<String, String> getMulticellNamesToCommentsMap() {
		return multicellNamesToCommentsMap;
	}
	
	
	private final String CELL_CONFIG_FILE_NAME = "CellShapesConfig.csv";
}