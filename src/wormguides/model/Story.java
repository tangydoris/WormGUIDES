package wormguides.model;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import wormguides.view.AppFont;

public class Story {
	
	private String name;
	private String description;
	private ArrayList<Note> notes;
	private VBox vBox;
	
	
	public Story(String name, String description) {
		this.name = name;
		this.description = description;
		notes = new ArrayList<Note>();
		
		makeGraphic();
	}
	
	
	private void makeGraphic() {
		vBox = new VBox(5);
		
		Label titleLabel = new Label(name);
		titleLabel.setFont(AppFont.getBoldFont());
		titleLabel.setWrapText(true);
		
		Text descriptionText = new Text(description);
		descriptionText.setFont(AppFont.getFont());
		descriptionText.wrappingWidthProperty().bind(vBox.widthProperty());
		
		vBox.getChildren().addAll(titleLabel, descriptionText);
		
		// TODO Add graphic for Note in this class?
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
		if (note!=null) {
			notes.add(note);
			vBox.getChildren().add(note.getGraphic());
		}
	}
	
	
	public void removeNote(Note note) {
		if (note!=null) {
			notes.remove(note);
			vBox.getChildren().remove(note.getGraphic());
		}
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public ArrayList<Note> getNotes() {
		return notes;
	}
	
	
	public VBox getGraphic() {
		return vBox;
	}
	
	
	public String toString() {
		return name+" - contains "+notes.size()+" notes";
	}

}
