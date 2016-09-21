/*
 * Bao Lab 2016
 */

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import wormguides.stories.Note;
import wormguides.stories.Note.Display;
import wormguides.stories.Note.Type;
import wormguides.stories.Story;
import wormguides.util.StringListCellFactory;

import acetree.LineageData;

public class StoryEditorController extends AnchorPane implements Initializable {

    private final String NEW_NOTE_TITLE = "New Note";
    private final String NEW_NOTE_CONTENTS = "New note contents here";
    private LineageData cellData;
    private int frameOffset;
    @FXML
    private TextField author;
    @FXML
    private TextField date;
    @FXML
    private Label activeCellLabel;
    private StringProperty activeCellProperty;
    private StringProperty sceneActiveCellProperty;
    private IntegerProperty timeProperty;
    @FXML
    private Button delete;
    // time stuff
    @FXML
    private ToggleGroup timeToggle;
    @FXML
    private RadioButton globalTimeRadioBtn;
    @FXML
    private RadioButton currentTimeRadioBtn;
    @FXML
    private RadioButton rangeTimeRadioBtn;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private Label currentTimeLabel;
    // attachment type stuff
    @FXML
    private ToggleGroup attachmentToggle;
    @FXML
    private RadioButton cellRadioBtn;
    @FXML
    private RadioButton globalRadioBtn;
    @FXML
    private RadioButton structureRadioBtn;
    @FXML
    private ToggleGroup subStructureToggle;
    @FXML
    private ComboBox<String> structuresComboBox;
    private ObservableList<String> structureComboItems;
    private StringListCellFactory factory;
    @FXML
    private RadioButton axonRadioBtn;

    // callout stuff
    // @FXML private CheckBox calloutTick;
    // @FXML private ToggleGroup calloutToggle;
    @FXML
    private RadioButton dendriteRadioBtn;
    @FXML
    private RadioButton cellBodyRadioBtn;
    // display type stuff
    @FXML
    private ToggleGroup displayToggle;
    @FXML
    private RadioButton infoPaneRadioBtn;
    @FXML
    private RadioButton locationRadioBtn;
    @FXML
    private RadioButton billboardRadioBtn;
    @FXML
    private RadioButton upLeftRadioBtn;
    @FXML
    private RadioButton upRightRadioBtn;
    @FXML
    private RadioButton lowLeftRadioBtn;
    @FXML
    private RadioButton lowRightRadioBtn;
    private BooleanProperty noteCreated;
    @FXML
    private TextField storyTitle;
    @FXML
    private TextArea storyDescription;
    private Runnable storyRunnable;
    private Story activeStory;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;
    private Note activeNote;
    private BooleanProperty update3D;

    // Input nameProperty is the string property that changes with clicking
    // on an entity in the 3d window
    public StoryEditorController(
            int timeOffset, LineageData data, ArrayList<String> multiCellStructuresList,
            StringProperty nameProperty, BooleanProperty cellClickedProperty, IntegerProperty sceneTimeProperty,
            BooleanProperty update3D) {

        super();

        cellData = data;

        frameOffset = timeOffset;

        new SimpleBooleanProperty(false);
        noteCreated = new SimpleBooleanProperty(false);

        activeStory = null;
        activeNote = null;

        timeProperty = sceneTimeProperty;
        timeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Toggle selected = timeToggle.getSelectedToggle();
                if (selected == null || selected.getUserData() != Time.CURRENT) {
                    setCurrentTimeLabel(timeProperty.get() + frameOffset);
                }
            }
        });

        activeCellProperty = new SimpleStringProperty();

        sceneActiveCellProperty = nameProperty;
        sceneActiveCellProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && cellData.isCellName(newValue)) {
                activeCellProperty.set(newValue);
            }
        });

        cellClickedProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                activeCellProperty.set(sceneActiveCellProperty.get());
                // System.out.println("clicked -
                // "+sceneActiveCellProperty.get());
                cellClickedProperty.set(false);
            }
        });

        structureComboItems = FXCollections.observableArrayList();
        structureComboItems.addAll(multiCellStructuresList);
        // structureComboItems.addAll("Axon", "Dendrite", "Cell Body");

        this.update3D = update3D;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assertFXMLNodes();

        // for story title field unselection/caret position
        storyRunnable = () -> {
            storyTitle.setText(activeStory.getName());
            storyDescription.setText(activeStory.getDescription());
            author.setText(activeStory.getAuthor());
            date.setText(activeStory.getDate());

            storyTitle.positionCaret(storyTitle.getText().length());
        };

        // text fields
        updateNoteFields();
        titleField.textProperty().addListener(new TitleFieldListener());
        titleField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) { // i.e. out of focus, now refresh the
                // scene
                update3D.set(false);
            } else {
                update3D.set(true);
            }
        });

        contentArea.textProperty().addListener(new ContentAreaListener());

        updateStoryFields();
        storyTitle.textProperty().addListener(new StoryTitleFieldListener());
        storyDescription.textProperty().addListener(new StoryDescriptionAreaListener());
        author.textProperty().addListener(new AuthorFieldListener());
        date.textProperty().addListener(new DateFieldListener());

        // attachment type/note display
        initToggleData();

        updateType();
        updateTime();
        updateDisplay();

        timeToggle.selectedToggleProperty().addListener(new TimeToggleListener());
        startTimeField.textProperty().addListener(new StartTimeFieldListener());
        endTimeField.textProperty().addListener(new EndTimeFieldListener());

        attachmentToggle.selectedToggleProperty().addListener(new AttachmentToggleListener());
        displayToggle.selectedToggleProperty().addListener(new DisplayToggleListener());

        factory = new StringListCellFactory();
        structuresComboBox.setItems(structureComboItems);
        structuresComboBox.setButtonCell(factory.getNewStringListCell());
        structuresComboBox.setCellFactory(factory);
        structuresComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Toggle selected = attachmentToggle.getSelectedToggle();
            if (activeNote != null && selected != null && selected.equals(structureRadioBtn)) {
                activeNote.setCellName(newValue);
            }
        });

        activeCellProperty.addListener((observable, oldValue, newValue) -> {
            // only change when active cell toggle is not selected
            Toggle selected = attachmentToggle.getSelectedToggle();
            if (selected == null || !((Type) selected.getUserData()).equals(Type.CELL)) {
                setCellLabelName(newValue);
            }
        });
        if (activeNote != null && activeNote.attachedToCell()) {
            activeCellProperty.set(activeNote.getCellName());
        } else {
            activeCellProperty.set(sceneActiveCellProperty.get());
        }
    }

    private void setActiveNoteCellName(String name) {
        if (activeNote != null) {
            activeNote.setCellName(name);
        }
    }

    private void setActiveNoteDisplay(Display display) {
        if (activeNote != null) {
            activeNote.setTagDisplay(display);
        }
    }

    private void setActiveNoteAttachmentType(Type type) {
        if (activeNote != null) {
            activeNote.setAttachmentType(type);

            if (type.equals(Type.CELL) && !activeCellProperty.get().isEmpty()) {
                activeNote.setCellName(activeCellProperty.get());
            }
        }
    }

    public void setUpdate3DProperty(BooleanProperty update3D) {
        this.update3D = update3D;
    }

    private String removeFunctionalName(String name) {
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("("));
        }
        name.trim();

        return name;
    }

    private void initToggleData() {
        // attachment type
        cellRadioBtn.setUserData(Type.CELL);
        globalRadioBtn.setUserData(Type.BLANK);
        structureRadioBtn.setUserData(Type.STRUCTURE);

        // sub structure

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
        assert (activeCellLabel != null);

        assert (author != null);
        assert (date != null);

        assert (titleField != null);
        assert (contentArea != null);

        assert (storyTitle != null);
        assert (storyDescription != null);

        assert (delete != null);

        assert (startTimeField != null);
        assert (endTimeField != null);
        assert (currentTimeLabel != null);

        assert (attachmentToggle != null);
        assert (cellRadioBtn != null);
        assert (globalRadioBtn != null);
        assert (structureRadioBtn != null);
        assert (structuresComboBox != null);
        assert (axonRadioBtn != null);
        assert (dendriteRadioBtn != null);
        assert (cellBodyRadioBtn != null);

        // assert (calloutToggle!=null);
        // assert (calloutTick!=null);
        assert (upLeftRadioBtn != null);
        assert (upRightRadioBtn != null);
        assert (lowLeftRadioBtn != null);
        assert (lowRightRadioBtn != null);

        assert (displayToggle != null);
        assert (infoPaneRadioBtn != null);
        assert (locationRadioBtn != null);
        assert (billboardRadioBtn != null);
    }

    public void setNoteCreated(boolean created) {
        noteCreated.set(created);
    }

    public BooleanProperty getNoteCreatedProperty() {
        return noteCreated;
    }

    private void updateTime() {
        if (timeToggle != null) {
            setCurrentTimeLabel(timeProperty.get() + frameOffset);

            if (activeNote != null) {
                int start = activeNote.getStartTime();
                int end = activeNote.getEndTime();
                // System.out.println(activeNote.getTagName()+" time -
                // "+start+", "+end);

                if (start == Integer.MIN_VALUE || end == Integer.MIN_VALUE) {
                    timeToggle.selectToggle(globalTimeRadioBtn);
                    startTimeField.setText("");
                    endTimeField.setText("");
                } else if (start == end) {
                    timeToggle.selectToggle(currentTimeRadioBtn);
                    startTimeField.setText("");
                    endTimeField.setText("");
                } else if (start < end) {
                    timeToggle.selectToggle(rangeTimeRadioBtn);
                    startTimeField.setText(Integer.toString(start + frameOffset));
                    endTimeField.setText(Integer.toString(end + frameOffset));
                }
            } else {
                resetToggle(timeToggle);

                startTimeField.setText("");
                endTimeField.setText("");
            }
        }
    }

    private void updateType() {
        if (attachmentToggle != null) {
            if (activeNote != null) {
                switch (activeNote.getAttachmentType()) {
                    case CELL:
                        String cellName = activeNote.getCellName();
                        activeCellProperty.set(cellName);
                        setCellLabelName(cellName);
                        attachmentToggle.selectToggle(cellRadioBtn);
                        resetToggle(subStructureToggle);
                        break;

                    case STRUCTURE:
                        String name = activeNote.getCellName();
                        for (String structure : structureComboItems) {
                            if (structure.equalsIgnoreCase(name)) {
                                name = structure;
                                structuresComboBox.getSelectionModel().select(structure);
                                break;
                            }
                        }
                        attachmentToggle.selectToggle(structureRadioBtn);
                        // TODO read substructure toggle enum from note (to be
                        // added)
                        break;

                    case BLANK: // fall to default case

                    default:
                        globalRadioBtn.setSelected(true);
                        activeCellProperty.set(sceneActiveCellProperty.get());
                        resetToggle(subStructureToggle);
                        break;
                }
            } else {
                resetToggle(attachmentToggle);
                resetToggle(subStructureToggle);
                structuresComboBox.getSelectionModel().clearSelection();
            }
        }
    }

    private void setCurrentTimeLabel(int time) {
        currentTimeLabel.setText("Current Time (" + time + ")");
    }

    private void setCellLabelName(String name) {
        if (activeCellLabel != null) {
            if (name == null || name.isEmpty()) {
                activeCellLabel.setText("Active Cell (none)");
            } else {
                String lineageName = removeFunctionalName(name);
                activeCellLabel.setText("Active Cell (" + lineageName + ")");
            }
        }
    }

    private void resetToggle(ToggleGroup group) {
        Toggle current = group.getSelectedToggle();
        if (current != null) {
            current.setSelected(false);
        }
    }

    private void updateDisplay() {
        if (displayToggle != null) {
            if (activeNote != null) {

                switch (activeNote.getTagDisplay()) {
                    case BLANK: // fall to overlay case

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
            } else {
                resetToggle(displayToggle);
            }
        }
    }

    private void updateStoryFields() {
        if (storyTitle != null && storyDescription != null) {
            if (activeStory != null) {
                Platform.runLater(storyRunnable);
            } else {
                storyTitle.clear();
                storyDescription.clear();
                author.clear();
                date.clear();
            }
        }
    }

    private void updateNoteFields() {
        if (titleField != null && contentArea != null) {
            if (activeNote != null) {
                titleField.setText(activeNote.getTagName());
                contentArea.setText(activeNote.getTagContents());
            } else {
                titleField.clear();
                contentArea.clear();
            }
        }
    }

    public Note getActiveNote() {
        return activeNote;
    }

    public void setActiveNote(Note note) {
        activeNote = note;

        updateNoteFields();
        updateType();
        updateTime();
        updateDisplay();
    }

    public Story getActiveStory() {
        return activeStory;
    }

    public void setActiveStory(Story story) {
        activeStory = story;
        updateStoryFields();
    }

    public void addDeleteButtonListener(EventHandler<ActionEvent> handler) {
        delete.setOnAction(handler);
    }

    // ----- Begin button actions -----
    @FXML
    protected void newNote() {
        if (activeStory != null) {
            setActiveNote(new Note(activeStory, NEW_NOTE_TITLE, NEW_NOTE_CONTENTS));
            setNoteCreated(true);
        }
    }

    public enum Time {
        GLOBAL, CURRENT, RANGE
    }

    // ----- Begin listeners ----
    private class AuthorFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeStory != null) {
                activeStory.setAuthor(newValue);
            }
        }
    }

    private class DateFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeStory != null) {
                activeStory.setDate(newValue);
            }
        }
    }

    private class StoryTitleFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeStory != null) {
                activeStory.setName(newValue);
                activeStory.setChanged(true);
            }
        }
    }

    private class StoryDescriptionAreaListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeStory != null) {
                activeStory.setDescription(newValue);
                activeStory.setChanged(true);
            }
        }
    }

    private class TitleFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeNote != null) {
                activeNote.setTagName(newValue);
                activeNote.setChanged(true);
            }
        }
    }

    private class ContentAreaListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeNote != null) {
                activeNote.setTagContents(newValue);
                activeNote.setChanged(true);
            }
        }
    }

    private class TimeToggleListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (activeNote != null && newValue != null) {
                int start = Integer.MIN_VALUE;
                int end = Integer.MIN_VALUE;

                switch ((Time) newValue.getUserData()) {

                    case CURRENT:
                        start = timeProperty.get();
                        end = start;
                        break;

                    case RANGE:
                        try {
                            if (!startTimeField.getText().isEmpty()) {
                                start = Integer.parseInt(startTimeField.getText()) - frameOffset;
                            }
                            if (!endTimeField.getText().isEmpty()) {
                                end = Integer.parseInt(endTimeField.getText()) - frameOffset;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        break;

                    case GLOBAL: // fall to default case

                    default:
                        break;
                }

                activeNote.setStartAndEndTimes(start, end);
            }
        }
    }

    private class StartTimeFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeNote != null) {
                Toggle selected = timeToggle.getSelectedToggle();
                if (selected != null && selected.getUserData() == Time.RANGE) {
                    try {
                        activeNote.setStartTime(Integer.parseInt(newValue) - frameOffset);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    // ----- End listeners ----

    private class EndTimeFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (activeNote != null) {
                Toggle selected = timeToggle.getSelectedToggle();
                if (selected != null && selected.getUserData() == Time.RANGE) {
                    try {
                        activeNote.setEndTime(Integer.parseInt(newValue) - frameOffset);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    // ----- End button actions -----

    private class DisplayToggleListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (activeNote != null) {
                if (newValue != null) {
                    switch ((Display) newValue.getUserData()) {

                        case OVERLAY:
                            setActiveNoteDisplay(Display.OVERLAY);
                            break;

                        case SPRITE:
                            setActiveNoteDisplay(Display.SPRITE);
                            break;

                        case BILLBOARD_FRONT:
                            setActiveNoteDisplay(Display.BILLBOARD_FRONT);
                            break;

                        default:
                            break;
                    }
                } else {
                    setActiveNoteDisplay(Display.BLANK);
                }
            }
        }
    }

    private class AttachmentToggleListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (activeNote != null) {
                if (newValue != null) {
                    switch ((Type) newValue.getUserData()) {

                        case CELL:
                            setActiveNoteAttachmentType(Type.CELL);
                            setActiveNoteCellName(activeCellProperty.get());
                            setActiveNoteDisplay(Display.SPRITE);
                            updateDisplay();
                            break;

                        case BLANK:
                            setActiveNoteAttachmentType(Type.BLANK);
                            setActiveNoteDisplay(Display.OVERLAY);
                            updateDisplay();
                            break;

                        case STRUCTURE:
                            setActiveNoteAttachmentType(Type.STRUCTURE);
                            setActiveNoteDisplay(Display.SPRITE);
                            updateDisplay();
                            break;

                        default:
                            break;

                    }
                } else {
                    setActiveNoteAttachmentType(Type.BLANK);
                }
            }
        }
    }

}