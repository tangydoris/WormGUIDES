package wormguides.model;

import java.util.ArrayList;

public class Story {
	
	private String name;
	private String description;
	private ArrayList<Note> notes;
	
	
	public Story(String name, String description) {
		this.name = name;
		this.description = description;
		notes = new ArrayList<Note>();
	}
	
	
	public ArrayList<SceneElement> getSceneElementsAtTime(int time) {
		//System.out.println("getting scene elements for story "+name+" at time "+time);
		ArrayList<SceneElement> elements = new ArrayList<SceneElement>();
		for (Note note : notes) {
			if (note.hasSceneElements()) {
				for (SceneElement element : note.getSceneElements()) {
					if (element.existsAtTime(time) && !elements.contains(element)) {
						//System.out.println("Got scene element from "+note.getTagName());
						elements.add(element);
					}
				}
			}
		}
		return elements;
	}
	
	
	public int getNumberOfNotes() {
		return notes.size();
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public void addNote(Note note) {
		notes.add(note);
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public Note[] getNotes() {
		return notes.toArray(new Note[notes.size()]);
	}

}
