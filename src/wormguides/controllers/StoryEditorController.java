package wormguides.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import wormguides.StringListCellFactory;
import wormguides.model.LineageData;
import wormguides.model.Note;
import wormguides.model.Note.Display;
import wormguides.model.Note.TimeStringFormatException;
import wormguides.model.Note.Type;
import wormguides.model.Story;

public class StoryEditorController extends AnchorPane implements Initializable {
	
	private LineageData cellData;
	private int frameOffset;
	
	@FXML private TextField author;
	@FXML private TextField date;
	
	@FXML private Label activeCellLabel;
	private StringProperty activeCellProperty;
	private StringProperty sceneActiveCellProperty;
	private IntegerProperty timeProperty;
	
	@FXML private Button delete;
	
	// time stuff
	@FXML private ToggleGroup timeToggle;
	@FXML private RadioButton globalTimeRadioBtn;
	@FXML private RadioButton currentTimeRadioBtn;
	@FXML private RadioButton rangeTimeRadioBtn;
	@FXML private TextField startTimeField;
	@FXML private TextField endTimeField;
	@FXML private Label currentTimeLabel;
	
	// attachment type stuff
	@FXML private ToggleGroup attachmentToggle;
	@FXML private RadioButton cellRadioBtn;
	@FXML private RadioButton globalRadioBtn;
	@FXML private ComboBox<String> structuresComboBox;
	private ObservableList<String> structureComboItems;
	private StringListCellFactory factory;
	@FXML private RadioButton axonRadioBtn;
	@FXML private RadioButton dendriteRadioBtn;
	@FXML private RadioButton cellBodyRadioBtn;
	private Type type;
	
	// callout stuff
	//@FXML private CheckBox calloutTick;
	//@FXML private ToggleGroup calloutToggle;
	
	// display type stuff
	@FXML private ToggleGroup displayToggle;
	@FXML private RadioButton infoPaneRadioBtn;
	@FXML private RadioButton locationRadioBtn;
	@FXML private RadioButton billboardRadioBtn;
	@FXML private RadioButton upLeftRadioBtn;
	@FXML private RadioButton upRightRadioBtn;
	@FXML private RadioButton lowLeftRadioBtn;
	@FXML private RadioButton lowRightRadioBtn;
	private Display display;
	
	private BooleanProperty storyCreated;
	private BooleanProperty noteCreated;
	
	@FXML private TextField storyTitle;
	@FXML private TextArea storyDescription;
	private Runnable storyRunnable;
	private Story activeStory;
	
	@FXML private TextField titleField;
	@FXML private TextArea contentArea;
	private Note activeNote;
	
	private Stage editStage;
	
	
	// Input nameProperty is the string property that changes with clicking
	// on an entity in the 3d window
	public StoryEditorController(int timeOffset, LineageData data, ArrayList<String> multiCellStructuresList, 
			StringProperty nameProperty, BooleanProperty cellClickedProperty, 
			IntegerProperty sceneTimeProperty) {
		
		super();
		
		cellData = data;
		
		frameOffset = timeOffset;
		
		storyCreated = new SimpleBooleanProperty(false);
		noteCreated = new SimpleBooleanProperty(false);
		
		activeStory = null;
		activeNote = null;
		
		timeProperty = sceneTimeProperty;
		timeProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				if (newValue!=null) {
					Toggle selected = timeToggle.getSelectedToggle();
					if (selected==null || ((Time)selected.getUserData())!=Time.CURRENT)
						setCurrentTimeLabel(timeProperty.get()+frameOffset);
				}
			}
		});
		
		activeCellProperty = new SimpleStringProperty();
		
		sceneActiveCellProperty = nameProperty;
		sceneActiveCellProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				if (newValue!=null && !newValue.isEmpty() && cellData.isCellName(newValue))
					activeCellProperty.set(newValue);
			}
		});
		
		cellClickedProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					activeCellProperty.set(sceneActiveCellProperty.get());
					//System.out.println("clicked - "+sceneActiveCellProperty.get());
					cellClickedProperty.set(false);
				}
			}
		});
		
		structureComboItems = FXCollections.observableArrayList();
		structureComboItems.addAll(multiCellStructuresList);
		//structureComboItems.addAll("Axon", "Dendrite", "Cell Body");
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
		
		// for story title field unselection/caret position
		storyRunnable = new Runnable() {
			@Override
			public void run() {
				storyTitle.setText(activeStory.getName());
				storyDescription.setText(activeStory.getDescription());
				author.setText(activeStory.getAuthor());
				date.setText(activeStory.getDate());
				
				storyTitle.positionCaret(storyTitle.getText().length());
			}
		};
		
		// text fields
		updateNoteFields();
		titleField.textProperty().addListener(new TitleFieldListener());
		contentArea.textProperty().addListener(new ContentAreaListener());
		
		updateStoryFields();
		storyTitle.textProperty().addListener(new StoryTitleFieldListener());
		storyDescription.textProperty().addListener(new StoryDescriptionAreaListener());
		author.textProperty().addListener(new AuthorFieldListener());
		date.textProperty().addListener(new DateFieldListener());
		
		// attachment type/note display
		initToggleData();
		
		updateType();
		updateDisplay();
		
		timeToggle.selectedToggleProperty().addListener(new TimeToggleListener());
		startTimeField.textProperty().addListener(new StartTimeFieldListener());
		endTimeField.textProperty().addListener(new EndTimeFieldListener());
		
		attachmentToggle.selectedToggleProperty().addListener(new AttachmentToggleListener());
		displayToggle.selectedToggleProperty().addListener(new DisplayToggleListener());
		
		factory = new StringListCellFactory();
		structuresComboBox.setItems(structureComboItems);
		structuresComboBox.setButtonCell(factory.getNewStringListCell());
		// TODO make note attach on here
		structuresComboBox.setCellFactory(factory);
		structuresComboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
			@Override
			public void changed(ObservableValue<? extends SingleSelectionModel<String>> observable,
					SingleSelectionModel<String> oldValue, SingleSelectionModel<String> newValue) {
				// TODO
				System.out.println(newValue.getSelectedItem());
			}
		});
		
		activeCellProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				// only change when active cell toggle is not selected
				Toggle selected = attachmentToggle.getSelectedToggle();
				if (selected==null || !((Type)selected.getUserData()).equals(Type.CELL))
					setCellLabelName(newValue);
			}
		});
		if (activeNote!=null && activeNote.existsWithCell())
			activeCellProperty.set(activeNote.getCellName());
		else
			activeCellProperty.set(sceneActiveCellProperty.get());
	}
	
	
	private void setActiveNoteCellName(String name) {
		if (activeNote!=null)
			activeNote.setCellName(name);
	}
	
	
	private void setActiveNoteTimes(String start, String end) {
		start = start.trim();
		end = end.trim();
		
		if (activeNote!=null) {
			if (!start.isEmpty() && !end.isEmpty()) {
				try {
					activeNote.setStartAndEndTimes(Integer.parseInt(start), Integer.parseInt(end));
				} catch (NumberFormatException e) {
					//System.out.println("invalid start/end time - must be integer");
				}
			}
			else
				activeNote.setStartAndEndTimes(Integer.MIN_VALUE, Integer.MIN_VALUE);
		}
	}
	
	
	private void setActiveNoteTagDisplay(Display display) {
		if (activeNote!=null)
			activeNote.setTagDisplay(display);
	}
	
	
	private void setActiveNoteAttachmentType(Type type) {
		if (activeNote!=null) {
			activeNote.setAttachmentType(type);
			
			if (type.equals(Type.CELL) && !activeCellProperty.get().isEmpty())
				activeNote.setCellName(activeCellProperty.get());
		}
	}
	
	
	private String removeFunctionalName(String name) {
		if (name.indexOf("(") > -1)
			name = name.substring(0, name.indexOf("("));
		name.trim();
		
		return name;
	}
	
	
	private void initToggleData() {
		// attachment type
		cellRadioBtn.setUserData(Type.CELL);
		globalRadioBtn.setUserData(Type.BLANK);
		
		// time
		globalTimeRadioBtn.setUserData(Time.GLOBAL);
		currentTimeRadioBtn.setUserData(Time.CURRENT);
		rangeTimeRadioBtn.setUserData(Time.RANGE);
		
		// display
		infoPaneRadioBtn.setUserData(Display.OVERLAY);
		locationRadioBtn.setUserData(Display.SPRITE);
		upLeftRadioBtn.setUserData(Display.SPRITE);
		upRightRadioBtn.setUserData(Display.SPRITE);
		lowLeftRadioBtn.setUserData(Display.SPRITE);
		lowRightRadioBtn.setUserData(Display.SPRITE);
		billboardRadioBtn.setUserData(Display.BILLBOARD_FRONT);
	}
	
	
	private void assertFXMLNodes() {
		assert (activeCellLabel!=null);
		
		assert (author!=null);
		assert (date!=null);
		
		assert (titleField!=null);
		assert (contentArea!=null);
		
		assert (storyTitle!=null);
		assert (storyDescription!=null);
		
		assert (delete!=null);
		
		assert (startTimeField!=null);
		assert (endTimeField!=null);
		assert (currentTimeLabel!=null);
		
		assert (attachmentToggle!=null);
		assert (cellRadioBtn!=null);
		assert (globalRadioBtn!=null);
		assert (structuresComboBox!=null);
		assert (axonRadioBtn!=null);
		assert (dendriteRadioBtn!=null);
		assert (cellBodyRadioBtn!=null);
		
		//assert (calloutToggle!=null);
		//assert (calloutTick!=null);
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
		
		updateNoteFields();
		updateType();
		updateTime();
		updateDisplay();
	}
	
	
	private void updateTime() {
		if (timeToggle!=null) {
			resetToggle(timeToggle);
			
			startTimeField.setText("");
			endTimeField.setText("");
			setCurrentTimeLabel(timeProperty.get()+frameOffset);
			
			if (activeNote!=null) {
				int start = activeNote.getStartTime();
				int end = activeNote.getEndTime();
				
				if (start==Integer.MIN_VALUE || end==Integer.MIN_VALUE)
					timeToggle.selectToggle(globalTimeRadioBtn);
				
				else if (start==end)
					timeToggle.selectToggle(currentTimeRadioBtn);
				
				else if (start<end) {
					timeToggle.selectToggle(rangeTimeRadioBtn);
					startTimeField.setText(Integer.toString(start+frameOffset));
					endTimeField.setText(Integer.toString(end+frameOffset));
				}	
			}
		}
	}
	
	
	// TODO
	private void updateType() {
		if (attachmentToggle!=null) {
			resetToggle(attachmentToggle);
			
			if (activeNote!=null) {
				switch (activeNote.getAttachmentType()) {
				case CELL:
								String cellName = activeNote.getCellName();
								activeCellProperty.set(cellName);
								setCellLabelName(cellName);
								attachmentToggle.selectToggle(cellRadioBtn);
								break;
								
				case BLANK:		// fall to default case
								
				default:
								globalRadioBtn.setSelected(true);
								activeCellProperty.set(sceneActiveCellProperty.get());
								break;
				}
			}
		}
	}
	
	
	private void setCurrentTimeLabel(int time) {
		currentTimeLabel.setText("Current Time ("+time+")");
	}
	
	
	private void setCellLabelName(String name) {
		if (activeCellLabel!=null) {
			if (name==null || name.isEmpty())
				activeCellLabel.setText("Active Cell (none)");
			
			else {
				String lineageName = removeFunctionalName(name);
				activeCellLabel.setText("Active Cell ("+lineageName+")");
			}
		}
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
	
	
	private void updateStoryFields() {
		if (storyTitle!=null && storyDescription!=null) {
			if (activeStory!=null)
				Platform.runLater(storyRunnable);
			
			else {
				storyTitle.clear();
				storyDescription.clear();
				author.clear();
				date.clear();
			}
		}
	}
	
	
	private void updateNoteFields() {
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
		updateStoryFields();
	}
	
	
	public Story getActiveStory() {
		return activeStory;
	}
	
	
	public void addDeleteButtonListener(EventHandler<ActionEvent> handler) {
		delete.setOnAction(handler);
	}
	
	
	public enum Time {GLOBAL, CURRENT, RANGE};
	
	
	// ----- Begin listeners ----
	private class AuthorFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldValue, String newValue) {
			if (activeStory!=null)
				activeStory.setAuthor(newValue);
		}
	}
	
	
	private class DateFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldValue, String newValue) {
			if (activeStory!=null)
				activeStory.setDate(newValue);
		}
	}
	
	
	private class StoryTitleFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, 
				String oldValue, String newValue) {
			if (activeStory!=null) {
				activeStory.setName(newValue);
				activeStory.setChanged(true);
			}
		}
	}
	
	
	private class StoryDescriptionAreaListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, 
				String oldValue, String newValue) {
			if (activeStory!=null) {
				activeStory.setDescription(newValue);
				activeStory.setChanged(true);
			}
		}
	}
	
	
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
	
	
	private class TimeToggleListener implements ChangeListener<Toggle> {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable, 
				Toggle oldValue, Toggle newValue) {
			if (activeNote!=null && newValue!=null) {
				int start = Integer.MIN_VALUE;
				int end = start;
				
				switch ((Time)newValue.getUserData()) {
				case GLOBAL:
								break;
								
				case CURRENT:
								start = timeProperty.get();
								end = start;
								break;
				
				case RANGE:		
								try {
									if (!startTimeField.getText().isEmpty())
										start = Integer.parseInt(startTimeField.getText())-frameOffset;
									if (!endTimeField.getText().isEmpty())
										end = Integer.parseInt(endTimeField.getText())-frameOffset;
								} catch (NumberFormatException e) {
									System.out.println("Invalid time - must be integer.");
									//e.printStackTrace();
								}
								break;
				
				default:
								break;
				}
				
				activeNote.setStartAndEndTimes(start, end);
			}
		}
	}
	
	
	private class StartTimeFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, 
				String oldValue, String newValue) {
			if (activeNote!=null) {
				try {
					activeNote.setStartTime(newValue);
				} catch (TimeStringFormatException e) {
					//System.out.println(e.toString());
				}
			}
		}
	}
	
	
	private class EndTimeFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, 
				String oldValue, String newValue) {
			if (activeNote!=null) {
				try {
					activeNote.setEndTime(newValue);
				} catch (TimeStringFormatException e) {
					//System.out.println(e.toString());
				}
			}
		}
	}
	
	
	private class DisplayToggleListener implements ChangeListener<Toggle> {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable, 
				Toggle oldValue, Toggle newValue) {
			if (activeNote!=null) {
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
				else
					setActiveNoteTagDisplay(Display.BLANK);
			}
		}
	}
	
	
	private class AttachmentToggleListener implements ChangeListener<Toggle> {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable, 
				Toggle oldValue, Toggle newValue) {
			if (activeNote!=null) {
				if (newValue!=null) {
					switch ((Type) newValue.getUserData()) {
					
					case CELL:
									setActiveNoteAttachmentType(Type.CELL);
									setActiveNoteCellName(activeCellProperty.get());
									break;
									
					case BLANK:
									setActiveNoteAttachmentType(Type.BLANK);
									break;
									
					default:
									break;
					
					}
				}
			}
		}
	}
	// ----- End listeners ----
	
	
	// ----- Begin button actions -----
	@FXML protected void newStory() {
		activeStory = new Story(NEW_STORY_TITLE, NEW_STORY_DESCRIPTION);
		setStoryCreated(true);
	}
	
	
	@FXML protected void newNote() {
		if (activeStory!=null) {
			setActiveNote(new Note(activeStory, NEW_NOTE_TITLE, NEW_NOTE_CONTENTS));
			setNoteCreated(true);
		}
	}
	// ----- End button actions -----
	
	
	private final String NEW_NOTE_TITLE = "New Note";
	private final String NEW_NOTE_CONTENTS = "New note contents here";
	
	private final String NEW_STORY_TITLE = "New Story";
	private final String NEW_STORY_DESCRIPTION = "New story description here";
	
}
