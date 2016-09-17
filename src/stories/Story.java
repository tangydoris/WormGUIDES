/*
 * Bao Lab 2016
 */

package stories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Collection of {@link Note}s. In the application, each story is associated with a list of
 * {@link wormguides.models.Rule}s. A story is either created during runtime by the user or loaded from one CSV line
 * of a story config file.
 */
public class Story {

    private String name;
    private String description;

    private String author;
    private String date;

    private ObservableList<Note> notes;
    private BooleanProperty activeBooleanProperty;
    private BooleanProperty changedBooleanProperty;
    private Comparator<Note> comparator;

    private String colorURL;

    public Story(String name, String description, String url) {
        this(name, description, "", "", url);
    }

    public Story(String name, String description, String author, String date, String url) {
        this.name = name;
        this.description = description;

        this.author = author;
        this.date = date;

        this.activeBooleanProperty = new SimpleBooleanProperty(false);
        this.changedBooleanProperty = new SimpleBooleanProperty(false);
        this.changedBooleanProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setChanged(false);
            }
        });

        this.notes = FXCollections.observableArrayList(note -> new Observable[]{note.getChangedProperty()});
        this.notes.addListener(new ListChangeListener<Note>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Note> c) {
                while (c.next()) {
                    // note was edited
                    if (c.wasUpdated()) {
                        setChanged(true);

                    } else if (c.wasAdded()) {
                        setChanged(true);

                    } else if (c.wasRemoved()) {
                        setChanged(true);
                    }
                }
            }
        });

        this.colorURL = url;
    }

    public String getColorURL() {
        return colorURL;
    }

    public void setColorURL(String url) {
        colorURL = url;
    }

    // Sorts notes by start time
    public void sortNotes() {
        if (comparator != null) {
            setChanged(true);
        }
    }

    public void setComparator(final Comparator<Note> comparator) {
        this.comparator = comparator;
    }

    public BooleanProperty getChangedProperty() {
        return changedBooleanProperty;
    }

    public boolean isActive() {
        return activeBooleanProperty.get();
    }

    public void setActive(final boolean isActive) {
        activeBooleanProperty.set(isActive);
    }

    public BooleanProperty getActiveProperty() {
        return activeBooleanProperty;
    }

    public ArrayList<Note> getNotesWithEntity() {
        ArrayList<Note> list = notes.stream()
                .filter(note -> note != null && note.attachedToCell())
                .collect(Collectors.toCollection(ArrayList::new));
        return list;
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }

    public ArrayList<Note> getPossibleNotesAtTime(final int time) {
        return notes.stream()
                .filter(note -> note.mayExistAtTime(time))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getNoteComment(String tagName) {
        for (Note note : notes) {
            if (note.getTagName().equalsIgnoreCase(tagName.trim())) {
                return note.getComments();
            }
        }
        return "";
    }

    public int getNumberOfNotes() {
        return notes.size();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addNote(Note note) {
        if (note != null) {
            notes.add(note);
            setChanged(true);

        }
    }

    public void setChanged(boolean changed) {
        if (comparator != null) {
            Collections.sort(notes, comparator);
        }
        changedBooleanProperty.set(changed);
    }

    public void removeNote(Note note) {
        if (note != null) {
            notes.remove(note);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    @Override
	public String toString() {
        return name + " - contains " + notes.size() + " notes";
    }

}
