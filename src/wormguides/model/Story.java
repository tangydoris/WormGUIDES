package wormguides.model;

import java.util.ArrayList;

public class Story {
	
	private String name;
	private String description;
	private ArrayList<Note> notesList;
	
	
	public Story(String name, String description) {
		this.name = name;
		this.description = description;
		notesList = new ArrayList<Note>();
	}
	
	
	public ArrayList<Note> getNotesWithCell() {
		ArrayList<Note> notes = new ArrayList<Note>();
		for (Note note : notesList) {
			if (note.isAttachedToCell() || note.isAttachedToCellTime())
				notes.add(note);
		}
		return notes;
	}
	
	
	public ArrayList<Note> getNotesAtTime(int time) {
		ArrayList<Note> notes = new ArrayList<Note>();
		for (Note note : notesList) {
			if (note.existsAtTime(time)) {
				//System.out.println("Story: "+note.getTagName()+" exists at time "+time);
				notes.add(note);
			}
		}
		return notes;
	}
	
	
	public ArrayList<SceneElement> getSceneElementsAtTime(int time) {
		//System.out.println("getting scene elements for story "+name+" at time "+time);
		ArrayList<SceneElement> elements = new ArrayList<SceneElement>();
		for (Note note : notesList) {
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
		return notesList.size();
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public void addNote(Note note) {
		notesList.add(note);
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public Note[] getNotes() {
		return notesList.toArray(new Note[notesList.size()]);
	}

}
