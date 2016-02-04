package wormguides.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	
	private String author;
	private String date;
	
	private ObservableList<Note> notes;
	private BooleanProperty activeBooleanProperty;
	private BooleanProperty changedBooleanProperty;
	private Comparator<Note> comparator;
	
	
	public Story(String name, String description) {
		this(name, description, "", "");
	}
	
	
	public Story(String name, String description, String author, String date) {
		this.name = name;
		this.description = description;
		
		this.author = author;
		this.date = date;
		
		activeBooleanProperty = new SimpleBooleanProperty(false);
		activeBooleanProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				changedBooleanProperty.set(true);
			}
		});
		
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
					if (c.wasUpdated())
						setChanged(true);
					
					else {
						if (c.wasAdded())
							setChanged(true);
						
						else if (c.wasRemoved())
							setChanged(true);
					}
				}
			}
		});
	}
	
	
	// Sorts notes by start time
	public void sortNotes() {
		if (comparator!=null)
			setChanged(true);
	}
	
	
	public void setComparator(Comparator<Note> comparator) {
		this.comparator = comparator;
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
	
	
	public ArrayList<Note> getNotesWithEntity() {
		ArrayList<Note> list = new ArrayList<Note>();
		for (Note note : notes) {
			if (note!=null && note.attachedToCell())
				list.add(note);
		}
		return list;
	}
	
	
	public ArrayList<Note> getPossibleNotesAtTime(int time) {
		ArrayList<Note> list = new ArrayList<Note>();
		for (Note note : notes) {
			if (note.mayExistAtTime(time))
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
	
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	
	public String getAuthor() {
		return author;
	}
	
	
	public void setDate(String date) {
		this.date = date;
	}
	
	
	public String getDate() {
		return date;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public void addNote(Note note) {
		if (note!=null) {
			notes.add(note);
			setChanged(true);
			
		}
	}
	
	
	public void setChanged(boolean changed) {
		if (comparator!=null)
			Collections.sort(notes, comparator);

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

}
