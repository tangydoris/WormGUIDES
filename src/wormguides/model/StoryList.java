package wormguides.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class StoryList {
	
	ArrayList<Story> stories;
	
	public StoryList() {
		stories = new ArrayList<Story>();
		buildStories();
	}
	
	public void buildStories() {
		try {
			JarFile jarFile = new JarFile(new File("WormGUIDES.jar"));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				
				if (entry.getName().equals("wormguides/model/story_file/"+STORY_CONFIG_FILE_NAME)) {
					InputStream stream = jarFile.getInputStream(entry);
					processStreamString(stream);
				}
			}
			
			jarFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("The config file '" + STORY_CONFIG_FILE_NAME + "' wasn't found on the system.");
		} catch (IOException e) {
			System.out.println("The config file '" + STORY_CONFIG_FILE_NAME + "' wasn't found on the system.");
		}
	}
	
	public void processStreamString(InputStream stream) {
		int storyCounter = -1; //used for accessing the current story for adding scene elements
		//File obj = new File(this.configFile);
		try {
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);
			
			String line;
			while ((line = reader.readLine()) != null) {
				String[] splits =  line.split(",", 8); //split the line up by commas
				
				//check if story line or scene element line - CHANGE THIS CHECK EMPTYNESS OF ENTIRE END OF LINE
				if (splits[2].length() == 0) { //story line
					Story story = new Story(splits[0], splits[1]);
					stories.add(story);
					storyCounter++;
				} else { //scene element line
					boolean billboardFlag = false;
					if (splits[4].contains("BILLBOARD")) {
						billboardFlag = true;
					}
						
					//vector of cell names
					Vector<String> cellNames = new Vector<String>();
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

					SceneElement se = new SceneElement(splits[0], cellNames,
							splits[2], splits[3], splits[4],
							Integer.parseInt(splits[5]), Integer.parseInt(splits[6]),
							splits[7], completeResourceFlag, billboardFlag);
							
					stories.get(storyCounter).addSceneElement(se);
				}
			}
			
			reader.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unable to process file '" + STORY_CONFIG_FILE_NAME + "'.");
		} catch (NumberFormatException e) {
			System.out.println("Number Format Error in file '" + STORY_CONFIG_FILE_NAME + "'.");
		} catch (IOException e) {
			System.out.println("The config file '" + STORY_CONFIG_FILE_NAME + "' wasn't found on the system.");
		}
	}
	
	public ArrayList<SceneElement> getSceneElementsAtTime(int time) {
		ArrayList<SceneElement> sceneElementsAtTime = new ArrayList<SceneElement>();
		for (int i = 0; i < stories.size(); i++) {
			ArrayList<SceneElement> currStorySceneElements = stories.get(i).getStorySceneElements();
			for (int j = 0; j < currStorySceneElements.size(); j++) {
				SceneElement currSE = currStorySceneElements.get(j);
				if (currSE.getStartTime() <= time && currSE.getEndTime() >= time) {
					sceneElementsAtTime.add(currSE);
				}
			}
		}
		return sceneElementsAtTime;
	}
	
	private final String STORY_CONFIG_FILE_NAME = "StoryListConfig.csv";
}