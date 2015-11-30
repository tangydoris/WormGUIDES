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
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SceneElementsList {
	
	public ArrayList<SceneElement> sceneElementsList;
	private JarFile jarFile;
	
	// TODO Maybe use this for optimization when reading jarfile from GeometryLoader
	// may not need this at all
	private ArrayList<JarEntry> objEntries;

	//this will eventually be constructed using a .txt file that contains the Scene Element information for the embryo
	public SceneElementsList() {
		sceneElementsList = new ArrayList<SceneElement>();
		objEntries = new ArrayList<JarEntry>();
		//buildListFromConfig();
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
				sceneElementsList.add(se);
			}
			
			reader.close();
		} catch (IOException e) {
			System.out.println("Invalid file: '" + CELL_CONFIG_FILE_NAME);
			return;
		}
	}
	
	public String[] getSceneElementNamesAtTime(int time) {
		time++;
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < sceneElementsList.size(); i++) {
			SceneElement curr = sceneElementsList.get(i);
			if (curr.getStartTime() <= time && curr.getEndTime() >= time) {
				for (String name : curr.getAllCellNames())
					names.add(name);
			}
		}
		return names.toArray(new String[names.size()]);
	}

	public ArrayList<SceneElement> getSceneElementsAtTime(int time) {
		time++;
		ArrayList<SceneElement> sceneElements = new ArrayList<SceneElement>();
		for (int i = 0; i < sceneElementsList.size(); i++) {
			SceneElement curr = sceneElementsList.get(i);
			if (curr.getStartTime() <= time && curr.getEndTime() >= time) {
				sceneElements.add(curr);
			}
		}
		return sceneElements;
	}
	
	private final String CELL_CONFIG_FILE_NAME = "CellShapesConfig.csv";
}