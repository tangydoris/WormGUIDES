/*
 * Bao Lab 2017
 */

package wormguides.stories;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import wormguides.models.colorrule.Rule;

import static java.util.stream.Collectors.toCollection;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Collection of {@link Note}s. In the application, each story is associated with a list of
 * {@link Rule}s. A story is either created during runtime by the user or loaded from one CSV line
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

    private String colorUrl;

    public Story(String name, String description, String url) {
        this(name, description, "", "", url);
    }

    public Story(String name, String description, String author, String date, String colorUrl) {
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

        this.notes = observableArrayList(note -> new Observable[]{note.getChangedProperty()});
        this.notes.addListener((ListChangeListener<Note>) c -> {
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
        });

        this.colorUrl = colorUrl;
    }

    public String getColorUrl() {
        return colorUrl;
    }

    public boolean hasColorScheme() {
        return colorUrl != null && !colorUrl.isEmpty();
    }

    /**
     * @return the active note if there is one, null otherwise
     */
    public Note getActiveNore() {
        for (Note note : notes) {
            if (note.isActive()) {
                return note;
            }
        }
        return null;
    }

    public void setColorUrl(final String colorUrl) {
        this.colorUrl = colorUrl;
    }

    public void setComparator(final Comparator<Note> comparator) {
        this.comparator = comparator;
        if (this.comparator != null) {
            notes.sort(this.comparator);
        }
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

    public List<Note> getNotesWithEntity() {
        List<Note> list = notes.stream()
                .filter(note -> note != null && note.attachedToCell())
                .collect(toCollection(ArrayList::new));
        return list;
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }

    public List<Note> getPossibleNotesAtTime(final int time) {
        return notes.stream()
                .filter(note -> note.mayExistAtTime(time))
                .collect(toCollection(ArrayList::new));
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

    public void sortNotes() {
        if (comparator != null) {
            notes.sort(comparator);
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        if (author != null) {
            this.author = author;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        if (date != null) {
            this.date = date;
        }
    }

    public void addNote(final Note note) {
        if (note != null) {
            notes.add(note);
        }
    }

    public void setChanged(final boolean changed) {
        changedBooleanProperty.set(changed);
    }

    public void removeNote(final Note note) {
        if (note != null) {
            notes.remove(note);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        if (description != null) {
            this.description = description;
        }
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return name + " - contains " + notes.size() + " notes";
    }

}
