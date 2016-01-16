package wormguides;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wormguides.model.Note;
import wormguides.model.Note.Display;
import wormguides.model.Note.Type;
import wormguides.model.Story;

public class NoteEditorController extends AnchorPane implements Initializable{
	
	@FXML private Label activeStoryLabel;
	@FXML private Label activeCellLabel;
	private StringProperty activeCellProperty;
	
	@FXML private TextField titleField;
	@FXML private TextArea contentArea;
	
	@FXML private Button delete;
	
	// time stuff
	@FXML private CheckBox timeTick;
	@FXML private TextField startTimeField;
	@FXML private TextField endTimeField;
	
	// attachment type stuff
	@FXML private ToggleGroup attachmentToggle;
	@FXML private RadioButton cellRadioBtn;
	@FXML private RadioButton globalRadioBtn;
	@FXML private ComboBox<String> structuresComboBox;
	@FXML private RadioButton axonRadioBtn;
	@FXML private RadioButton dendriteRadioBtn;
	@FXML private RadioButton cellBodyRadioBtn;
	private Type type;
	
	// callout stuff
	@FXML private CheckBox calloutTick;
	@FXML private ToggleGroup calloutToggle;
	@FXML private RadioButton upLeftRadioBtn;
	@FXML private RadioButton upRightRadioBtn;
	@FXML private RadioButton lowLeftRadioBtn;
	@FXML private RadioButton lowRightRadioBtn;
	
	// display type stuff
	@FXML private ToggleGroup displayToggle;
	@FXML private RadioButton infoPaneRadioBtn;
	@FXML private RadioButton locationRadioBtn;
	@FXML private RadioButton billboardRadioBtn;
	private Display display;
	
	private NewStoryEditorController editController;
	private BooleanProperty storyCreated;
	private BooleanProperty noteCreated;
	
	private Story activeStory;
	private Note activeNote;
	
	private ChangeListener<String> titleFieldListener;
	private ChangeListener<String> contentAreaListener;
	
	private ChangeListener<Boolean> timeTickListener;
	private ChangeListener<Toggle> attachmentToggleListener;
	private ChangeListener<Toggle> displayToggleListener;
	
	private Stage editStage;
	
	
	public NoteEditorController() {
		super();
		
		storyCreated = new SimpleBooleanProperty(false);
		noteCreated = new SimpleBooleanProperty(false);
		
		activeStory = null;
		activeNote = null;
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
		
		// text fields
		titleFieldListener = new TitleFieldListener();
		contentAreaListener = new ContentAreaListener();
		
		updateFields();
		
		titleField.textProperty().addListener(titleFieldListener);
		contentArea.textProperty().addListener(contentAreaListener);
		
		// attachment type/note display
		initToggleData();
		
		timeTickListener = new TimeTickListener();
		attachmentToggleListener = new AttachmentToggleListener();
		displayToggleListener = new DisplayToggleListener();
		
		changeActiveStoryLabel();
		
		updateType();
		updateDisplay();
		
		timeTick.selectedProperty().addListener(timeTickListener);
		attachmentToggle.selectedToggleProperty().addListener(attachmentToggleListener);
		displayToggle.selectedToggleProperty().addListener(displayToggleListener);
	}
	
	
	private void setTime(int start, int end) {
		if (activeNote.isTimeSpecified()) {
			startTimeField.setText(Integer.toString(activeNote.getStartTime()));
			endTimeField.setText(Integer.toString(activeNote.getEndTime()));
		}
	}
	
	
	/*
	private void turnOnCellType(String cellName) {
		attachmentToggle.selectToggle(cellRadioBtn);
		setCellLabelName(cellName);
	}
	*/
	
	
	private void setActiveNoteTimes(String start, String end) {
		start = start.trim();
		end = end.trim();
		
		if (activeNote!=null && !start.isEmpty() && !end.isEmpty()) {
			try {
				activeNote.setStartAndEndTimes(Integer.parseInt(start), Integer.parseInt(end));
			} catch (NumberFormatException e) {
				System.out.println("invalid start/end time - must be integer");
			}
		}
	}
	
	
	private void setActiveNoteTagDisplay(Display display) {
		if (activeNote!=null)
			activeNote.setTagDisplay(display);
	}
	
	
	private void setActiveNoteAttachmentType(Type type) {
		if (activeNote!=null) {
			activeNote.setAttachmentType(type);
			
			if (type.equals(Type.CELL) || type.equals(Type.CELLTIME))
				activeNote.setCellName(activeCellProperty.get());
		}
	}
	
	
	private String removeFunctionalName(String name) {
		if (name.indexOf("(") > -1)
			name = name.substring(0, name.indexOf("("));
		name.trim();
		
		return name;
	}
	
	
	public void setActiveCellNameProperty(StringProperty nameProperty) {
		activeCellProperty = nameProperty;
		activeCellProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				if (!newValue.isEmpty())
					setCellLabelName(newValue);
			}
		});
		
		if (activeNote!=null && activeNote.existsWithCell())
			setCellLabelName(activeNote.getCellName());
		else
			setCellLabelName(activeCellProperty.get());
	}
	
	
	private void initToggleData() {
		// attachment type
		cellRadioBtn.setUserData(Type.CELL);
		globalRadioBtn.setUserData(Type.BLANK);
		
		// display
		infoPaneRadioBtn.setUserData(Display.OVERLAY);
		locationRadioBtn.setUserData(Display.SPRITE);
		billboardRadioBtn.setUserData(Display.BILLBOARD_FRONT);
	}
	
	
	private void assertFXMLNodes() {
		assert (activeStoryLabel!=null);
		assert (activeCellLabel!=null);
		
		assert (titleField!=null);
		assert (contentArea!=null);
		
		assert (delete!=null);
		
		assert (timeTick!=null);
		assert (startTimeField!=null);
		assert (endTimeField!=null);
		
		assert (attachmentToggle!=null);
		assert (cellRadioBtn!=null);
		assert (globalRadioBtn!=null);
		assert (structuresComboBox!=null);
		assert (axonRadioBtn!=null);
		assert (dendriteRadioBtn!=null);
		assert (cellBodyRadioBtn!=null);
		
		assert (calloutToggle!=null);
		assert (calloutTick!=null);
		assert (upLeftRadioBtn!=null);
		assert (upRightRadioBtn!=null);
		assert (lowLeftRadioBtn!=null);
		assert (lowRightRadioBtn!=null);
		
		assert (displayToggle!=null);
		assert (infoPaneRadioBtn!=null);
		assert (locationRadioBtn!=null);
		assert (billboardRadioBtn!=null);
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
		
		if (activeNote!=null) {
			System.out.println(activeNote);
			System.out.println(activeNote.getAttachmentType());
			System.out.println(activeNote.getTagDisplay());
			System.out.println("cell - "+activeNote.getCellName());
		}
		
		updateFields();
		updateType();
		updateDisplay();
	}
	
	
	private void updateType() {
		if (attachmentToggle!=null) {
			
			if (activeNote!=null) {
				switch (activeNote.getAttachmentType()) {
				case CELL:
								attachmentToggle.selectToggle(cellRadioBtn);
								setCellLabelName(activeNote.getCellName());
								resetTime();
								break;
								
				case TIME:		
								timeTick.setSelected(true);
								setTime(activeNote.getStartTime(), activeNote.getEndTime());
								setCellLabelName(null);
								break;
								
				case CELLTIME:
								attachmentToggle.selectToggle(cellRadioBtn);
								setCellLabelName(activeNote.getCellName());
								setTime(activeNote.getStartTime(), activeNote.getEndTime());
								break;
								
				case BLANK:
								globalRadioBtn.setSelected(true);
								setCellLabelName(null);
								resetTime();
								break;
								
				default:
								resetTime();
								resetToggle(attachmentToggle);
								setCellLabelName(null);
								break;
				}	
			}
			
			else {
				resetTime();
				resetToggle(attachmentToggle);
				setCellLabelName(null);
			}
		}
	}
	
	
	private void setCellLabelName(String name) {
		if (name==null || name.isEmpty())
			activeCellLabel.setText("Active Cell (none)");
		else
			activeCellLabel.setText("Active Cell ("+removeFunctionalName(name)+")");
	}

	
	private void updateTimeFields(int start, int end) {
		startTimeField.setText(Integer.toString(start));
		endTimeField.setText(Integer.toString(end));
	}
	
	
	private void resetTime() {
		timeTick.setSelected(false);
		startTimeField.setText("");
		endTimeField.setText("");
	}
	
	
	private void resetToggle(ToggleGroup group) {
		Toggle current = group.getSelectedToggle();
		if (current!=null)
			current.setSelected(false);
	}
	
	
	private void updateDisplay() {
		if (displayToggle!=null) {
			if (activeNote!=null) {
				switch (activeNote.getTagDisplay()) {
				case OVERLAY:
								infoPaneRadioBtn.setSelected(true);
								break;
								
				case SPRITE:
								locationRadioBtn.setSelected(true);
								break;
								
				case BILLBOARD_FRONT:
								billboardRadioBtn.setSelected(true);
								break;
								
				default:
								resetToggle(displayToggle);
								break;
				}
			}
			
			else
				resetToggle(displayToggle);
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
	
	
	private class TimeTickListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, 
				Boolean oldValue, Boolean newValue) {
			/*
			if (newValue) {
				Toggle current = attachmentToggle.getSelectedToggle();
				if (current!=null && ((Type) current.getUserData()).equals(Type.CELL))
					setActiveNoteAttachmentType(Type.CELLTIME);
				else
					setActiveNoteAttachmentType(Type.TIME);
				
				setActiveNoteTimes(startTimeField.getText(), endTimeField.getText());
			}
			*/
		}
	}
	
	
	private class DisplayToggleListener implements ChangeListener<Toggle> {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable, 
				Toggle oldValue, Toggle newValue) {
			/*
			if (newValue!=null) {
				switch ((Display) newValue.getUserData()) {
				
				case OVERLAY:
								setActiveNoteTagDisplay(Display.OVERLAY);
								break;
								
				case SPRITE:
								setActiveNoteTagDisplay(Display.SPRITE);
								break;
								
				case BILLBOARD_FRONT:
								setActiveNoteTagDisplay(Display.BILLBOARD_FRONT);
								break;
								
				default:
								break;
				}
			}
			*/
		}
	}
	
	
	private class AttachmentToggleListener implements ChangeListener<Toggle> {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable, 
				Toggle oldValue, Toggle newValue) {
			/*
			if (newValue!=null) {
				switch ((Type) newValue.getUserData()) {
				
				case CELL:
								setActiveNoteAttachmentType(Type.CELL);
								break;
								
				case BLANK:		setActiveNoteAttachmentType(Type.BLANK);
								break;
								
				default:
								break;
				
				}
			}
			*/
		}
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
	}
	// ----- End button actions -----
	
	
	private final String NEW_NOTE_TITLE = "New Note";
	private final String NEW_NOTE_CONTENTS = "New Note Contents";
	
}
