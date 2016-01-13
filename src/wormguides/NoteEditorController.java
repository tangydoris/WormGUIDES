package wormguides;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wormguides.model.Note;
import wormguides.model.Story;

public class NoteEditorController extends AnchorPane implements Initializable{
	
	@FXML private Label activeStoryLabel;
	
	@FXML private TextField titleField;
	@FXML private TextArea contentArea;
	
	@FXML private Button delete;
	
	@FXML private ComboBox<String> structuresComboBox;
	
	@FXML private CheckBox calloutTick;
	
	@FXML private CheckBox timeTick;
	@FXML private TextField startTimeField;
	@FXML private TextField endTimeField;
	
	@FXML private ToggleGroup attachmentToggle;
	@FXML private ToggleGroup calloutToggle;
	@FXML private ToggleGroup displayToggle;
	
	private NewStoryEditorController editController;
	private BooleanProperty storyCreated;
	private BooleanProperty noteCreated;
	
	private Story activeStory;
	private Note activeNote;
	
	private ChangeListener<String> titleFieldListener;
	private ChangeListener<String> contentAreaListener;
	
	private Stage editStage;
	
	
	public NoteEditorController() {
		super();
		
		storyCreated = new SimpleBooleanProperty(false);
		
		noteCreated = new SimpleBooleanProperty(false);
		/*
		noteCreated.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				if (newValue)
					setNoteCreated(false);
			}
		});
		*/
		
		activeStory = null;
		activeNote = null;
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
		
		titleFieldListener = new TitleFieldListener();
		contentAreaListener = new ContentAreaListener();
		
		updateFields();
		
		titleField.textProperty().addListener(titleFieldListener);
		contentArea.textProperty().addListener(contentAreaListener);
		
		changeActiveStoryLabel();
	}
	
	
	private void assertFXMLNodes() {
		assert (activeStoryLabel!=null);
		
		assert (titleField!=null);
		assert (contentArea!=null);
		
		assert (delete!=null);
		
		assert (structuresComboBox!=null);
		assert (calloutTick!=null);
		
		assert (timeTick!=null);
		assert (startTimeField!=null);
		assert (endTimeField!=null);
		
		assert (attachmentToggle!=null);
		assert (calloutToggle!=null);
		assert (displayToggle!=null);
	}
	
	
	public void setStoryCreated(boolean created) {
		storyCreated.set(created);
	}
	
	
	public BooleanProperty getStoryCreatedProperty() {
		return storyCreated;
	}
	
	
	public void setNoteCreated(boolean created) {
		noteCreated.set(created);
	}
	
	
	public BooleanProperty getNoteCreatedProperty() {
		return noteCreated;
	}
	
	
	public void setActiveNote(Note note) {
		activeNote = note;
		
		if (titleField!=null && contentArea!=null) {
			titleField.textProperty().removeListener(titleFieldListener);
			contentArea.textProperty().removeListener(contentAreaListener);
			
			updateFields();
			
			titleField.textProperty().addListener(titleFieldListener);
			contentArea.textProperty().addListener(contentAreaListener);
		}
	}
	
	
	private void updateFields() {
		if (titleField!=null && contentArea!=null) {
			if (activeNote!=null) {
				titleField.setText(activeNote.getTagName());
				contentArea.setText(activeNote.getTagContents());
			}
			else {
				titleField.clear();
				contentArea.clear();
			}
		}
	}
	
	
	public Note getActiveNote() {
		return activeNote;
	}
	
	
	public void setActiveStory(Story story) {
		activeStory = story;
		
		changeActiveStoryLabel();
	}
	
	
	public Story getActiveStory() {
		return activeStory;
	}
	
	
	private void changeActiveStoryLabel() {
		if (activeStoryLabel!=null) {
			if (activeStory!=null)
				activeStoryLabel.setText("Active Story: "+activeStory.getName());
			else
				activeStoryLabel.setText("No Active Story");
		}
	}
	
	
	public void addDeleteButtonListener(EventHandler<ActionEvent> handler) {
		delete.setOnAction(handler);
	}
	
	
	// ----- Begin text listeners ----
	private class TitleFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, 
				String oldValue, String newValue) {
			if (activeNote!=null) {
				activeNote.setTagName(newValue);
				activeNote.setChanged(true);
			}
		}
	}
	
	
	private class ContentAreaListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, 
				String oldValue, String newValue) {
			if (activeNote!=null) {
				activeNote.setTagContents(newValue);
				activeNote.setChanged(true);
			}
		}
	}
	// ----- End text listeners ----
	
	
	// ----- Begin button actions -----
	@FXML protected void newStory() {
		if (editStage==null) {
			editController = new NewStoryEditorController();
			
			editStage = new Stage();
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/NewStoryEditorLayout.fxml"));
			
			loader.setController(editController);
			loader.setRoot(editController);
			
			try {
				editStage.setScene(new Scene((AnchorPane) loader.load()));
				
				editStage.setTitle("New Story");
				editStage.initModality(Modality.NONE);
				
				for (Node node : editStage.getScene().getRoot().getChildrenUnmodifiable()) {
	            	node.setStyle("-fx-focus-color: -fx-outer-border; "+
	            					"-fx-faint-focus-color: transparent;");
	            }
				
				editController.addCancelButtonListener(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						editController.clearFields();
						editStage.hide();
					}
				});
				
				editController.addSubmitButtonListener(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						activeStory = new Story(editController.getTitle(), editController.getDescription());
						setStoryCreated(true);
						
						editController.clearFields();
						editStage.hide();
					}
				});
				
			} catch (IOException e) {
				System.out.println("error in initializing new story editor.");
				e.printStackTrace();
			}
		}
		
		editStage.show();
		editStage.toFront();
	}
	
	
	@FXML protected void loadStory() {
		// TODO
	}
	
	
	@FXML protected void saveStory() {
		// TODO
	}
	
	
	@FXML protected void newNote() {
		if (activeStory!=null) {
			setActiveNote(new Note(activeStory, NEW_NOTE_TITLE, NEW_NOTE_CONTENTS));
			setNoteCreated(true);
		}
		else {
			System.out.println("no story to add note to");
		}
	}
	// ----- End button actions -----
	
	
	private final String NEW_NOTE_TITLE = "New Note";
	private final String NEW_NOTE_CONTENTS = "New Note Contents";
	
}
