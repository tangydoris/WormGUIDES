/*
 * Bao Lab 2017
 */

package wormguides.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

import acetree.LineageData;
import wormguides.stories.Note;
import wormguides.stories.Note.Display;
import wormguides.stories.Note.Type;
import wormguides.stories.Story;
import wormguides.util.StringCellFactory;

import static java.lang.Integer.MIN_VALUE;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import static javafx.collections.FXCollections.observableArrayList;

import static wormguides.controllers.StoryEditorController.Time.CURRENT;
import static wormguides.controllers.StoryEditorController.Time.RANGE;
import static wormguides.stories.Note.Display.BILLBOARD_FRONT;
import static wormguides.stories.Note.Display.OVERLAY;
import static wormguides.stories.Note.Display.SPRITE;
import static wormguides.stories.Note.Type.BLANK;
import static wormguides.stories.Note.Type.CELL;
import static wormguides.stories.Note.Type.STRUCTURE;

public class StoryEditorController extends AnchorPane implements Initializable {

    private static final String NEW_NOTE_TITLE = "New Note";
    private static final String NEW_NOTE_CONTENTS = "New note contents here";

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

    @FXML
    private RadioButton axonRadioBtn;

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

    private final ObservableList<String> structureComboItems;
    private StringCellFactory.StringListCellFactory listCellFactory;

    private Note activeNote;

    private final LineageData cellData;
    private final int frameOffset;

    /**
     * Constructor
     *
     * @param timeOffset
     *         time offset (in number of frames). This was loaded from production info on startup.
     * @param data
     *         underlying lineage data
     * @param multiCellStructuresList
     *         list of multicellular structures
     * @param nameProperty
     *         string property that changes when an entity is clicked in the subscene
     * @param cellClickedProperty
     *         true if a cell is clicked on, false otherwise
     * @param sceneTimeProperty
     *         the subscene time
     */
    public StoryEditorController(
            final int timeOffset,
            final LineageData data,
            final List<String> multiCellStructuresList,
            final StringProperty nameProperty,
            final BooleanProperty cellClickedProperty,
            final IntegerProperty sceneTimeProperty) {

        super();

        cellData = data;

        frameOffset = timeOffset;

        noteCreated = new SimpleBooleanProperty(false);

        activeStory = null;
        activeNote = null;

        timeProperty = requireNonNull(sceneTimeProperty);
        timeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Toggle selected = timeToggle.getSelectedToggle();
                if (selected == null || selected.getUserData() != CURRENT) {
                    setCurrentTimeLabel(timeProperty.get() + frameOffset);
                }
            }
        });

        activeCellProperty = new SimpleStringProperty();

        sceneActiveCellProperty = requireNonNull(nameProperty);
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

        structureComboItems = observableArrayList();
        structureComboItems.addAll(requireNonNull(multiCellStructuresList));
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

        // note fields
        updateNoteFields();
        titleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // if field was previously focused and is not anymore
            if (!newValue && activeNote != null) {
                activeNote.setTagName(titleField.getText());
                activeNote.setChanged(true);
            }
        });
        contentArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && activeNote != null) {
                activeNote.setTagContents(contentArea.getText());
                activeNote.setChanged(true);
            }
        });

        // story fields
        updateStoryFields();
        storyTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && activeStory != null) {
                activeStory.setName(newValue);
                activeStory.setChanged(true);
            }
        });
        storyDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && activeStory != null) {
                activeStory.setDescription(newValue);
                activeStory.setChanged(true);
            }
        });
        author.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && activeStory != null) {
                activeStory.setAuthor(newValue);
            }
        });
        date.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && activeStory != null) {
                activeStory.setDate(newValue);
            }
        });

        // attachment type/note display
        initToggleData();

        updateType();
        updateTime();
        updateDisplay();

        timeToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (activeNote != null && newValue != null) {
                int start = MIN_VALUE;
                int end = MIN_VALUE;
                switch ((Time) newValue.getUserData()) {
                    case CURRENT:
                        start = timeProperty.get();
                        end = start;
                        break;

                    case RANGE:
                        try {
                            if (!startTimeField.getText().isEmpty()) {
                                start = parseInt(startTimeField.getText()) - frameOffset;
                            }
                            if (!endTimeField.getText().isEmpty()) {
                                end = parseInt(endTimeField.getText()) - frameOffset;
                            }
                        } catch (NumberFormatException e) {
                            // silently fail
                        }
                        break;

                    case GLOBAL: // fall to default case

                    default:
                        break;
                }
                activeNote.setStartAndEndTimes(start, end);
            }
        });
        startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (activeNote != null) {
                Toggle selected = timeToggle.getSelectedToggle();
                if (selected != null && selected.getUserData() == RANGE) {
                    try {
                        activeNote.setStartTime(parseInt(newValue) - frameOffset);
                    } catch (NumberFormatException e) {
                        // silently fail
                    }
                }
            }
        });
        endTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (activeNote != null) {
                Toggle selected = timeToggle.getSelectedToggle();
                if (selected != null && selected.getUserData() == RANGE) {
                    try {
                        activeNote.setEndTime(parseInt(newValue) - frameOffset);
                    } catch (NumberFormatException e) {
                        // silently fail
                    }
                }
            }
        });

        attachmentToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (activeNote != null) {
                if (newValue != null) {
                    switch ((Type) newValue.getUserData()) {
                        case CELL:
                            setActiveNoteAttachmentType(CELL);
                            setActiveNoteCellName(activeCellProperty.get());
                            setActiveNoteDisplay(SPRITE);
                            structuresComboBox.setDisable(true);
                            updateDisplay();
                            break;

                        case BLANK:
                            setActiveNoteAttachmentType(BLANK);
                            setActiveNoteDisplay(OVERLAY);
                            structuresComboBox.setDisable(true);
                            updateDisplay();
                            break;

                        case STRUCTURE:
                            setActiveNoteAttachmentType(STRUCTURE);
                            setActiveNoteCellName(structuresComboBox.getSelectionModel().getSelectedItem());
                            setActiveNoteDisplay(SPRITE);
                            structuresComboBox.setDisable(false);
                            updateDisplay();
                            break;

                        default:
                            break;

                    }
                } else {
                    setActiveNoteAttachmentType(BLANK);
                }
            }
        });
        displayToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (activeNote != null) {
                if (newValue != null) {
                    switch ((Display) newValue.getUserData()) {
                        case OVERLAY:
                            setActiveNoteDisplay(OVERLAY);
                            break;
                        case SPRITE:
                            setActiveNoteDisplay(SPRITE);
                            break;
                        case BILLBOARD_FRONT:
                            setActiveNoteDisplay(BILLBOARD_FRONT);
                            break;
                        default:
                            break;
                    }
                } else {
                    setActiveNoteDisplay(Display.BLANK);
                }
            }
        });

        listCellFactory = new StringCellFactory.StringListCellFactory();
        structuresComboBox.setItems(structureComboItems);
        structuresComboBox.setButtonCell(listCellFactory.getNewStringListCell());
        structuresComboBox.setCellFactory(listCellFactory);
        structuresComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Toggle selected = attachmentToggle.getSelectedToggle();
            if (activeNote != null && selected != null && selected.equals(structureRadioBtn)) {
                activeNote.setCellName(newValue);
            }
        });

        activeCellProperty.addListener((observable, oldValue, newValue) -> {
            // only change when active cell toggle is not selected
            Toggle selected = attachmentToggle.getSelectedToggle();
            if (selected == null || !((Type) selected.getUserData()).equals(CELL)) {
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

            if (type.equals(CELL) && !activeCellProperty.get().isEmpty()) {
                activeNote.setCellName(activeCellProperty.get());
            }
        }
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
        cellRadioBtn.setUserData(CELL);
        globalRadioBtn.setUserData(BLANK);
        structureRadioBtn.setUserData(STRUCTURE);

        // sub structure

        // time
        globalTimeRadioBtn.setUserData(Time.GLOBAL);
        currentTimeRadioBtn.setUserData(CURRENT);
        rangeTimeRadioBtn.setUserData(RANGE);

        // display
        infoPaneRadioBtn.setUserData(OVERLAY);
        locationRadioBtn.setUserData(SPRITE);
        upLeftRadioBtn.setUserData(SPRITE);
        upRightRadioBtn.setUserData(SPRITE);
        lowLeftRadioBtn.setUserData(SPRITE);
        lowRightRadioBtn.setUserData(SPRITE);
        billboardRadioBtn.setUserData(BILLBOARD_FRONT);
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

                if (start == MIN_VALUE || end == MIN_VALUE) {
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
                                structuresComboBox.getSelectionModel().select(structure);
                                break;
                            }
                        }
                        attachmentToggle.selectToggle(structureRadioBtn);
                        // TODO read substructure toggle enum from note (to be added)
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

    private void setCurrentTimeLabel(final int time) {
        currentTimeLabel.setText("Current Time (" + time + ")");
    }

    private void setCellLabelName(final String name) {
        if (activeCellLabel != null) {
            if (name == null || name.isEmpty()) {
                activeCellLabel.setText("Active Cell (none)");
            } else {
                activeCellLabel.setText("Active Cell (" + removeFunctionalName(name) + ")");
            }
        }
    }

    /**
     * Resets a toggle group so that all toggle are unselected
     *
     * @param group
     *         the toggle group to reset
     */
    private void resetToggle(final ToggleGroup group) {
        if (group != null) {
            final Toggle current = group.getSelectedToggle();
            if (current != null) {
                current.setSelected(false);
            }
        }
    }

    /**
     * Updates display radio button toggle with the display type of the active note
     */
    private void updateDisplay() {
        if (displayToggle != null && activeNote != null) {
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
}