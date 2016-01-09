package wormguides.model;

import java.util.ArrayList;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class Story {
	
	private String name;
	private String description;
	private ObservableList<Note> notes;
	private BooleanProperty activeBooleanProperty;
		
	
	public Story(String name, String description) {
		this.name = name;
		this.description = description;
		activeBooleanProperty = new SimpleBooleanProperty(false);
		notes = FXCollections.observableArrayList(new Callback<Note, Observable[]>() {
			@Override
			public Observable[] call(Note note) {
				return new Observable[]{note.getChangedProperty()};
			}
		});
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
	
	
	public ObservableList<Note> getNotesObservable() {
		return notes;
	}
	
	
	public ArrayList<Note> getNotes() {
		ArrayList<Note> array = new ArrayList<Note>();
		for (Note note : notes)
			array.add(note);
		return array;
	}
	
	
	public String toString() {
		return name+" - contains "+notes.size()+" notes";
	}

}
