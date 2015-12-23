package wormguides.model;

import java.util.ArrayList;

public class Story {

	private ArrayList<SceneElement> elements;
	private String StoryName;
	private String StoryTags;
	
	
	public Story(String storyName, String storyTags) {
		elements = new ArrayList<SceneElement>();
		this.StoryName = storyName;
		this.StoryTags = storyTags;
	}
	
	
	public void addSceneElement(SceneElement se) {
		if (se != null) {
			elements.add(se);
		}
	}
	
	
	public ArrayList<SceneElement> getSceneElements() {
		return elements;
	}
	
	
	public String getStoryName() {
		return StoryName;
	}
	
	
	public String getStoryTags() {
		return StoryTags;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder(getStoryName());
		sb.append(" applies to\n");
		for (SceneElement e : elements)
			sb.append("\t"+e.getSceneName()+"\n");
		return sb.toString();
	}
	
}
