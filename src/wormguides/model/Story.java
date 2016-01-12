package wormguides.model;

import java.util.ArrayList;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Story {
	
	private String name;
	private String description;
	private ObservableList<Note> notes;
	private BooleanProperty activeBooleanProperty;
	private BooleanProperty changedBooleanProperty;
		
	
	public Story(String name, String description) {
		this.name = name;
		this.description = description;
		
		activeBooleanProperty = new SimpleBooleanProperty(false);
		
		changedBooleanProperty = new SimpleBooleanProperty(false);
		changedBooleanProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				if (newValue)
					setChanged(false);
			}
		});
		
		notes = FXCollections.observableArrayList(
				note -> new Observable[]{note.getChangedProperty()});
		notes.addListener(new ListChangeListener<Note>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Note> c) {
				while (c.next()) {
					// note was edited
					if (c.wasUpdated()) {
						setChanged(true);
					}
					
					/*
					// note was added
					for (Note note : c.getAddedSubList()) {
						//System.out.println("added note - "+note);
					}
					
					// note was deleted
					for (Note note : c.getRemoved()) {
						//System.out.println("removed note - "+note);
					}
					*/
				}
			}
		});
	}
	
	
	public BooleanProperty getChangedProperty() {
		return changedBooleanProperty;
	}
	
	
	public boolean isActive() {
		return activeBooleanProperty.get();
	}
	
	
	public void setActive(boolean isActive) {
		activeBooleanProperty.set(isActive);
	}
	
	
	public BooleanProperty getActiveProperty() {
		return activeBooleanProperty;
	}
	
	
	public ArrayList<Note> getNotesWithCell() {
		ArrayList<Note> list = new ArrayList<Note>();
		for (Note note : notes) {
			if (note.isAttachedToCell() || note.isAttachedToCellTime())
				list.add(note);
		}
		return list;
	}
	
	
	public ArrayList<Note> getNotesAtTime(int time) {
		ArrayList<Note> list = new ArrayList<Note>();
		for (Note note : notes) {
			if (note.existsAtTime(time))
				list.add(note);
		}
		return list;
	}
	
	
	public String getNoteComment(String tagName) {
		for (Note note : notes) {
			if (note.getTagName().equalsIgnoreCase(tagName.trim()))
				return note.getComments();
		}
		return "";
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
		if (note!=null)
			notes.add(note);
	}
	
	
	public void setChanged(boolean changed) {
		changedBooleanProperty.set(changed);
	}
	
	
	public void removeNote(Note note) {
		if (note!=null)
			notes.remove(note);
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public ObservableList<Note> getNotes() {
		return notes;
	}
	
	
	public String toString() {
		return name+" - contains "+notes.size()+" notes";
	}
	
	
	/*
	private class NoteChangeListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, 
				Boolean oldValue, Boolean newValue) {
			if (newValue) {
				// this get propagated to listview extractor
				//System.out.println("story changed due to note change");
				changedBooleanProperty.set(true);
			}
		}
	}
	*/

}
