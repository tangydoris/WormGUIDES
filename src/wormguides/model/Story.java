package wormguides.model;

import java.util.ArrayList;

public class Story {

	private ArrayList<SceneElement> story;
	private String StoryName;
	private String StoryTags;
	
	public Story(String storyName, String storyTags) {
		story = new ArrayList<SceneElement>();
		this.StoryName = storyName;
		this.StoryTags = storyTags;
	}
	
	public void addSceneElement(SceneElement se) {
		if (se != null) {
			story.add(se);
		}
	}
	
	public ArrayList<SceneElement> getStorySceneElements() {
		return this.story;
	}
	
	public String getStoryName() {
		return this.StoryName;
	}
	
	public String getStoryTags() {
		return this.StoryTags;
	}
}
