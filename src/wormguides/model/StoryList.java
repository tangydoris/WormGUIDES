package wormguides.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class StoryList {
	
	ArrayList<Story> stories;
	private final String configFile;
	
	public StoryList(String configFile) {
		stories = new ArrayList<Story>();
		this.configFile = configFile;
	}
	
	public void buildStories() {
		int storyCounter = -1; //used for accessing the current story for adding scene elements
		File obj = new File(this.configFile);
		try {
			Scanner scanner = new Scanner(obj);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] splits =  line.split(",", 8); //split the line up by commas
				
				//check if story line or scene element line
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
			scanner.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("The config file '" + configFile + "' wasn't found on the system.");
			//e.printStackTrace(); 
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
}