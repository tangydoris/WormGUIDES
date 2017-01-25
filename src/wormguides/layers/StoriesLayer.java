/*
 * Bao Lab 2017
 */

package wormguides.layers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

import acetree.LineageData;
import wormguides.MainApp;
import wormguides.controllers.StoryEditorController;
import wormguides.models.colorrule.Rule;
import wormguides.models.subscenegeometry.SceneElementsList;
import wormguides.stories.Note;
import wormguides.stories.Story;

import static java.lang.Integer.MIN_VALUE;
import static java.util.Objects.requireNonNull;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.geometry.Insets.EMPTY;
import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.GREY;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.text.FontSmoothingType.LCD;
import static javafx.stage.Modality.NONE;

import static wormguides.loaders.URLLoader.process;
import static wormguides.stories.StoriesLoader.loadConfigFile;
import static wormguides.stories.StoryFileUtil.loadFromCSVFile;
import static wormguides.stories.StoryFileUtil.saveToCSVFile;
import static wormguides.util.AppFont.getBolderFont;
import static wormguides.util.AppFont.getFont;
import static wormguides.util.URLGenerator.generateInternal;

/**
 * Controller of the list view in the 'Stories' tab
 */
public class StoriesLayer {

    private final String NEW_STORY_TITLE = "New Story";
    private final String NEW_STORY_DESCRIPTION = "New story description here";
    private final String TEMPLATE_STORY_NAME = "Template to Make Your Own Story";
    private final String TEMPLATE_STORY_DESCRIPTION = "Shows all segmented neurons without further annotation.";

    private final Stage parentStage;

    private final LineageData lineageData;
    private final SceneElementsList sceneElementsList;
    private final SearchLayer searchLayer;

    private final IntegerProperty timeProperty;
    private final DoubleProperty rotateXAngleProperty;
    private final DoubleProperty rotateYAngleProperty;
    private final DoubleProperty rotateZAngleProperty;
    private final DoubleProperty translateXProperty;
    private final DoubleProperty translateYProperty;
    private final DoubleProperty zoomProperty;
    private final DoubleProperty othersOpacityProperty;
    private final BooleanProperty useInternalRulesFlag;
    private final BooleanProperty rebuildSubsceneFlag;
    private final BooleanProperty cellClickedFlag;
    private final StringProperty activeCellNameProperty;
    private final StringProperty activeStoryProperty;

    private final ObservableList<Rule> activeRulesList;
    private final ObservableList<Story> stories;

    private final int startTime;
    private final int endTime;
    private int movieTimeOffset;

    private Stage editStage;
    private StoryEditorController editController;

    private Story activeStory;
    private Note activeNote;
    private Comparator<Note> noteComparator;
    private double width;

    public StoriesLayer(
            final Stage parentStage,
            final SearchLayer searchLayer,
            final SceneElementsList elementsList,
            final ListView<Story> storiesListView,
            final ObservableList<Rule> rulesList,
            final StringProperty activeCellNameProperty,
            final StringProperty activeStoryProperty,
            final BooleanProperty cellClickedFlag,
            final IntegerProperty timeProperty,
            final DoubleProperty rotateXAngleProperty,
            final DoubleProperty rotateYAngleProperty,
            final DoubleProperty rotateZAngleProperty,
            final DoubleProperty translateXProperty,
            final DoubleProperty translateYProperty,
            final DoubleProperty zoomProperty,
            final DoubleProperty othersOpacityProperty,
            final BooleanProperty useInternalRulesFlag,
            final BooleanProperty rebuildSubsceneFlag,
            final LineageData lineageData,
            final Button newStoryButton,
            final Button deleteStoryButton,
            final Button editNoteButton,
            final int startTime,
            final int endTime,
            final int movieTimeOffset,
            final boolean defaultEmbryoFlag) {

        this.parentStage = requireNonNull(parentStage);
        this.searchLayer = requireNonNull(searchLayer);
        this.lineageData = requireNonNull(lineageData);
        this.sceneElementsList = requireNonNull(elementsList);

        this.activeRulesList = requireNonNull(rulesList);

        this.cellClickedFlag = requireNonNull(cellClickedFlag);
        this.rebuildSubsceneFlag = requireNonNull(rebuildSubsceneFlag);

        this.timeProperty = requireNonNull(timeProperty);
        this.rotateXAngleProperty = requireNonNull(rotateXAngleProperty);
        this.rotateYAngleProperty = requireNonNull(rotateYAngleProperty);
        this.rotateZAngleProperty = requireNonNull(rotateZAngleProperty);
        this.translateXProperty = requireNonNull(translateXProperty);
        this.translateYProperty = requireNonNull(translateYProperty);
        this.zoomProperty = requireNonNull(zoomProperty);
        this.othersOpacityProperty = requireNonNull(othersOpacityProperty);

        this.activeCellNameProperty = requireNonNull(activeCellNameProperty);
        this.activeStoryProperty = requireNonNull(activeStoryProperty);

        this.useInternalRulesFlag = requireNonNull(useInternalRulesFlag);

        this.startTime = startTime;
        this.endTime = endTime;
        this.movieTimeOffset = movieTimeOffset;

        stories = observableArrayList(story -> new Observable[]{
                story.getChangedProperty(),
                story.getActiveProperty()});
        stories.addListener((ListChangeListener<Story>) c -> {
            while (c.next()) {
                // need this listener to detect change for some reason leave this empty
            }
        });

        newStoryButton.setOnAction(event -> {
            Story story = new Story(NEW_STORY_TITLE, NEW_STORY_DESCRIPTION, "");
            stories.add(story);
            setActiveStory(story);
            setActiveNoteWithSubsceneRebuild(null);
            bringUpEditor();
        });

        deleteStoryButton.setOnAction(event -> {
            if (activeStory != null) {
                stories.remove(activeStory);
                setActiveStory(null);
                setActiveNoteWithSubsceneRebuild(null);
            }
        });

        editNoteButton.setOnAction(event -> bringUpEditor());

        width = 0;

        if (defaultEmbryoFlag) {
            loadConfigFile(stories, this.movieTimeOffset);
        }

        addBlankStory();

        noteComparator = (o1, o2) -> {
            final Integer t1 = getEffectiveStartTime(o1);
            final Integer t2 = getEffectiveStartTime(o2);
            if (t1.equals(t2)) {
                return o1.getTagName().compareTo(o2.getTagName());
            }
            return t1.compareTo(t2);
        };

        for (Story story : stories) {
            story.setComparator(noteComparator);
            story.sortNotes();
        }

        // makes lim-4 story on default embryo, template otherwise
        setActiveStory(stories.get(0));

        requireNonNull(storiesListView);
        storiesListView.setItems(stories);
        storiesListView.setCellFactory(getStoryCellFactory());
        storiesListView.widthProperty().addListener(
                (observable, oldValue, newValue) -> width = newValue.doubleValue() - 20);
        storiesListView.setOnScrollStarted(event -> {
            // ignore horizontal scrolls
            if (event != null && event.getDeltaX() != 0) {
                event.consume();
            }
        });
    }

    /**
     * @return the callback that is the renderer for a {@link Story} item. It graphically renders an active story
     * with black text and an inactive one with grey text. For an active story, its notes are also rendered beneath
     * the story title and description.
     */
    private Callback<ListView<Story>, ListCell<Story>> getStoryCellFactory() {
        return new Callback<ListView<Story>, ListCell<Story>>() {
            @Override
            public ListCell<Story> call(ListView<Story> param) {
                final ListCell<Story> cell = new ListCell<Story>() {
                    @Override
                    protected void updateItem(Story story, boolean empty) {
                        super.updateItem(story, empty);
                        if (!empty) {
                            // create story graphic
                            final StoryListCellGraphic storyGraphic = new StoryListCellGraphic(story, width);
                            // add list view for notes inside story graphic
                            if (story.isActive()) {
                                for (Note note : story.getNotes()) {
                                    storyGraphic.getChildren().add(new NoteListCellGraphic(note, width));
                                }
                            }
                            final Separator s = new Separator(HORIZONTAL);
                            s.setFocusTraversable(false);
                            s.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
                            storyGraphic.getChildren().add(s);

                            setGraphic(storyGraphic);
                        } else {
                            setGraphic(null);
                        }
                        setStyle("-fx-background-color: transparent;");
                        setPadding(EMPTY);
                    }
                };
                return cell;
            }
        };
    }

    /**
     * Adds a blank story
     */
    private void addBlankStory() {
        stories.add(new Story(
                TEMPLATE_STORY_NAME,
                TEMPLATE_STORY_DESCRIPTION,
                "http://scene.wormguides.org/wormguides/testurlscript?/set/ash-n$@+#ff8"
                        + "fbc8f/rib-n$@+#ff663366/avg-n$@+#ffb41919/dd-n@+#ff4a24c1/da-"
                        + "n@+#ffc56002/dd-n$+#ffb30a95/da-n$+#ffe6b34d/rivl-n@+#ffffb366/"
                        + "rivr-n@+#ffffe6b3/sibd-n@+#ffe6ccff/siav-n@+#ff8099ff/view/"
                        + "time=393/rX=51.625/rY=-2.125/rZ=0.0/tX=0.0/tY=0.0/scale=2.25/dim=0.25/browser/"));
    }

    /**
     * Loades story from file and sets it as active story. Uses a
     * {@link FileChooser} to allow the user to pick a load location.
     */
    public void loadStory() {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Story");
        chooser.setInitialFileName("WormGUIDES Story.csv");
        chooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
        final File file = chooser.showOpenDialog(parentStage);
        if (file != null) {
            loadFromCSVFile(stories, file, movieTimeOffset);
        }
    }

    /**
     * Saves active story to a file. {@link FileChooser} is used to allow the user to specify a save location and
     * file name.
     *
     * @return true if story is successfully saved, false otherwise
     */
    public boolean saveActiveStory() {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Story");
        chooser.setInitialFileName("WormGUIDES Story.csv");
        chooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
        final File file = chooser.showSaveDialog(parentStage);
        // if user clicks save
        if (file != null) {
            if (activeStory != null) {
                updateColorURL();
                saveToCSVFile(activeStory, file, movieTimeOffset);
                System.out.println("File saved to " + file.getAbsolutePath());
            } else {
                System.out.println("No active story to save");
            }
            return true;
        }
        return false;
    }

    /**
     * Because the Color URL is set on New Note, we need to update the color URL before saving to account for
     * additions and deletions
     */
    private void updateColorURL() {
        if (activeStory != null) {
            activeStory.setActive(false);
            activeStory.setColorURL(generateInternal(
                    new ArrayList<>(activeRulesList),
                    timeProperty.get(),
                    rotateXAngleProperty.get(),
                    rotateYAngleProperty.get(),
                    rotateZAngleProperty.get(),
                    translateXProperty.get(),
                    translateYProperty.get(),
                    zoomProperty.get(),
                    othersOpacityProperty.get()));
        }
    }

    /**
     * @return The {@link StringProperty} activeStoryProperty that changes when
     * the active story changes. The value of the String is the name of
     * the currently active story.
     */
    public StringProperty getActiveStoryProperty() {
        return activeStoryProperty;
    }

    /**
     * @return The description of the current active story
     */
    public String getActiveStoryDescription() {
        if (activeStory != null) {
            return activeStory.getDescription();
        }
        return "";
    }

    /**
     * @return Effective start time of currently active story
     */
    public int getActiveStoryStartTime() {
        if (activeStory != null && activeStory.hasNotes()) {
            return getEffectiveStartTime(activeStory.getNotes().get(0));
        }
        return MIN_VALUE;
    }

    /**
     * Sets the active note to the input note parameter. Makes the current note inactive, then makes the input note
     * active.
     *
     * @param note
     *         the note that should become active
     */
    public void setActiveNoteWithSubsceneRebuild(final Note note) {
        // deactivate the previous active note
        if (activeNote != null) {
            activeNote.setActive(false);
        }
        activeNote = note;
        if (activeNote != null) {
            activeNote.setActive(true);
            // set time property to be read by 3d window
            if (!activeNote.getTagName().equals("New Note")) {
                int startTime = getEffectiveStartTime(activeNote);
                if (startTime < 1) {
                    startTime = 1;
                }
                if (timeProperty != null) {
                    timeProperty.set(startTime);
                }
            }
        }
        if (editController != null) {
            editController.setActiveNote(activeNote);
        }
    }

    /**
     * Retrieve the effective end time of the input note parameter, whether it is the one explicitly stated by the
     * 'end time' field or the one implicitly specified by the cell, cell body, or multicellular structure.
     *
     * @param note
     *         the note queried
     *
     * @return the effective end time of the input note. An Integer object is returned instead of the primitive int
     * so that it can be passed into the note comparator
     */
    private Integer getEffectiveEndTime(Note note) {
        int time = MIN_VALUE;

        if (note != null) {
            if (note.attachedToCell() || note.attachedToStructure()) {

                int entityStartTime;
                int entityEndTime;

                if (note.attachedToCell()) {
                    entityStartTime = lineageData.getFirstOccurrenceOf(note.getCellName());
                    entityEndTime = lineageData.getLastOccurrenceOf(note.getCellName());
                } else {
                    entityStartTime = sceneElementsList.getFirstOccurrenceOf(note.getCellName());
                    entityEndTime = sceneElementsList.getLastOccurrenceOf(note.getCellName());
                }

                // attached to cell/structure and time is specified
                if (note.isTimeSpecified()) {
                    int noteStartTime = note.getStartTime();
                    int noteEndTime = note.getEndTime();

                    // make sure times actually overlap
                    if (noteStartTime <= entityEndTime && entityEndTime <= noteEndTime) {
                        time = entityEndTime;
                    } else if (entityStartTime <= noteEndTime && noteEndTime < entityEndTime) {
                        time = noteEndTime;
                    }
                }

                // attached to cell/structure and time not specified
                else {
                    time = entityEndTime;
                }
            } else if (note.isTimeSpecified()) {
                time = note.getEndTime();
            }

        }

        return time;
    }

    /**
     * Retrieve the effective start time of the input note parameter, whether it is the one explicitly stated by the
     * 'start time' field or the one implicitly specified by the cell, cell body, or multicellular structure.
     *
     * @param note
     *         The {@link Note} whose effective start time is queried
     *
     * @return the effective start time of the input note. An Integer object is returned instead of the primitive int
     * so that it can be passed into the note comparator
     */
    private Integer getEffectiveStartTime(Note note) {
        int time = MIN_VALUE;

        if (note != null) {
            if (note.attachedToCell() || note.attachedToStructure()) {

                int entityStartTime;
                int entityEndTime;

                if (note.attachedToCell()) {
                    entityStartTime = lineageData.getFirstOccurrenceOf(note.getCellName());
                    entityEndTime = lineageData.getLastOccurrenceOf(note.getCellName());
                } else {
                    entityStartTime = sceneElementsList.getFirstOccurrenceOf(note.getCellName());
                    entityEndTime = sceneElementsList.getLastOccurrenceOf(note.getCellName());
                }

                // attached to cell/structure and time is specified
                if (note.isTimeSpecified()) {
                    int noteStartTime = note.getStartTime();
                    int noteEndTime = note.getEndTime();

                    // make sure times actually overlap
                    if (noteStartTime <= entityStartTime && entityStartTime <= noteEndTime) {
                        time = entityStartTime;
                    } else if (entityStartTime <= noteStartTime && noteStartTime < entityEndTime) {
                        time = noteStartTime;
                    }
                }

                // attached to cell/structure and time not specified
                else {
                    time = entityStartTime;
                }
            } else if (note.isTimeSpecified()) {
                time = note.getStartTime();
            }

        }

        return time;
    }

    /**
     * @return the currently active story
     */
    public Story getActiveStory() {
        return activeStory;
    }

    /**
     * Ultimately sets the active story to the input story. Sets the currently active story to be inactive if it is
     * not null, then sets the input story to active.
     *
     * @param story
     *         story to make active
     */
    public void setActiveStory(final Story story) {
        // disable previous active story, copy current rules changes back to story
        if (activeStory != null) {
            activeStory.setActive(false);
            activeStory.setColorURL(generateInternal(
                    new ArrayList<>(activeRulesList),
                    timeProperty.get(),
                    rotateXAngleProperty.get(),
                    rotateYAngleProperty.get(),
                    rotateZAngleProperty.get(),
                    translateXProperty.get(),
                    translateYProperty.get(),
                    zoomProperty.get(),
                    othersOpacityProperty.get()));
        }

        activeNote = null;
        useInternalRulesFlag.set(true);

        activeStory = story;
        int newTime = startTime;
        newTime = timeProperty.get();

        if (activeStory != null) {
            activeStory.setActive(true);
            activeStoryProperty.set(activeStory.getName());
            // if story does not come with a url, set its url to the program's internal color rules
            if (activeStory.getColorURL().isEmpty()) {
                activeStory.setColorURL(generateInternal(
                        new ArrayList<>(activeRulesList),
                        timeProperty.get(),
                        rotateXAngleProperty.get(),
                        rotateYAngleProperty.get(),
                        rotateZAngleProperty.get(),
                        translateXProperty.get(),
                        translateYProperty.get(),
                        zoomProperty.get(),
                        othersOpacityProperty.get()));
            } else { // if story does come with url, use it
                useInternalRulesFlag.set(false);
            }

            process(
                    activeStory.getColorURL(),
                    activeRulesList,
                    searchLayer,
                    timeProperty,
                    rotateXAngleProperty,
                    rotateYAngleProperty,
                    rotateZAngleProperty,
                    translateXProperty,
                    translateYProperty,
                    zoomProperty,
                    othersOpacityProperty,
                    rebuildSubsceneFlag);

            if (activeStory.hasNotes()) {
                newTime = getEffectiveStartTime(activeStory.getNotes().get(0));
                if (newTime < startTime) {
                    newTime = startTime;
                }
            }
        } else {
            activeStoryProperty.set("");
            useInternalRulesFlag.set(true);
            rebuildSubsceneFlag.set(true);
        }

        if (editController != null) {
            editController.setActiveStory(activeStory);
        }
    }

    /**
     * @param tagName
     *         tag name of that note whose comments the user wants to retrieve
     *
     * @return the comments of the note whose tag name is specified by the input parameter
     */
    public String getNoteComments(final String tagName) {
        String comments = "";
        for (Story story : stories) {
            if (!story.getNoteComment(tagName).isEmpty()) {
                comments = story.getNoteComment(tagName);
                break;
            }
        }
        return comments;
    }

    public List<Note> getNotesWithEntity() {
        ArrayList<Note> notes = new ArrayList<>();
        stories.stream()
                .filter(Story::isActive)
                .forEachOrdered(story -> notes.addAll(story.getNotesWithEntity()));
        return notes;
    }

    /**
     * @param time
     *         the queried time
     *
     * @return all notes that can exist at the at input time. This includes notes attached to an entity if entity is
     * present at input time. These notes are later filtered out.
     */
    public List<Note> getNotesAtTime(final int time) {
        final List<Note> notes = new ArrayList<>();
        stories.stream()
                .filter(Story::isActive)
                .forEachOrdered(story -> notes.addAll(story.getPossibleNotesAtTime(time)));

        if (!notes.isEmpty()) {
            final Iterator<Note> iter = notes.iterator();
            Note note;
            while (iter.hasNext()) {
                note = iter.next();
                int effectiveStart = getEffectiveStartTime(note);
                int effectiveEnd = getEffectiveEndTime(note);
                if (effectiveStart != MIN_VALUE
                        && effectiveEnd != MIN_VALUE
                        && (time < effectiveStart || effectiveEnd < time)) {
                    iter.remove();
                }
            }
        }
        return notes;
    }

    /**
     * @return list of stories that are visible in the 'Stories' tab
     */
    public ObservableList<Story> getStories() {
        return stories;
    }

    /**
     * Brings up the story/notes editor window, controlled by the {@link StoryEditorController}. Upon the editor's
     * initialization/fxml load, listenable properties are passed to the editor so that the it can rename
     * labels/change the UI according to changes in time and active cell name.
     * <p>
     * The editor is initialized so that it always lives on top of the main application window and moves when the
     * main window is moved. This is to ensure that the user can always edit a story when the window is opened even
     * when he/she is clicking around in the 3D subscene to change the time/active cell.
     */
    private void bringUpEditor() {
        if (editStage == null) {
            editController = new StoryEditorController(
                    movieTimeOffset,
                    lineageData,
                    sceneElementsList.getAllMulticellSceneNames(),
                    activeCellNameProperty,
                    cellClickedFlag,
                    timeProperty,
                    rebuildSubsceneFlag);

            editController.setActiveNote(activeNote);
            editController.setActiveStory(activeStory);

            editStage = new Stage();

            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/layouts/StoryEditorLayout.fxml"));

            loader.setController(editController);
            loader.setRoot(editController);

            try {
                editStage.setScene(new Scene(loader.load()));

                editStage.setTitle("Story/Note Editor");
                editStage.initOwner(parentStage);
                editStage.initModality(NONE);
                editStage.setResizable(true);

                editStage.setOnCloseRequest(event -> rebuildSubsceneFlag.set(true));

                editController.getNoteCreatedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        Note newNote = editController.getActiveNote();
                        editController.setNoteCreated(false);
                        activeStory.addNote(newNote);
                        setActiveNoteWithSubsceneRebuild(newNote);
                        rebuildSubsceneFlag.set(true);
                    }
                });

                editController.addDeleteButtonListener(event -> {
                    if (activeNote != null) {
                        activeStory.removeNote(activeNote);
                    }
                    setActiveNoteWithSubsceneRebuild(null);
                });

                for (Node node : editStage.getScene().getRoot().getChildrenUnmodifiable()) {
                    node.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
                }

            } catch (IOException e) {
                System.out.println("error in initializing note editor.");
                e.printStackTrace();
            }
        }

        editStage.show();
        editStage.toFront();
    }

    /**
     * Changes the color of the input {@link Text} items by modifying the
     * java-fx css attribute '-fx-fill' to the specified input color. Used by
     * {@link StoryListCellGraphic} and {@link NoteListCellGraphic} items.
     *
     * @param color
     *         The {@link Color} to change the texts to
     * @param texts
     *         The listing of {@link Text} items whose color is to be changed
     */
    public void colorTexts(Color color, Text... texts) {
        for (Text text : texts) {
            text.setStyle("-fx-fill:" + color.toString().toLowerCase().replace("0x", "#"));
        }
    }

    /**
     * @return stories visible in the 'Stories' tab
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stories:\n");
        Story story;
        for (int i = 0; i < stories.size(); i++) {
            story = stories.get(i);
            sb.append(story.getName())
                    .append(": ")
                    .append(story.getNumberOfNotes())
                    .append(" notes\n");
            for (Note note : story.getNotes()) {
                sb.append("\t")
                        .append(note.getTagName())
                        .append(": times ")
                        .append(note.getStartTime())
                        .append(" ")
                        .append(note.getEndTime())
                        .append("\n");
            }
            if (i < stories.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Graphical representation of a {@link Story}. It makes an inactive story become active when the title/description
     * is clicked, and makes an active story become inactive when it is clicked. This graphical item is rendered in
     * the {@link ListCell} of the {@link ListView} in the 'Stories' tab.
     */
    private class StoryListCellGraphic extends VBox {

        private Text title;
        private Text description;

        public StoryListCellGraphic(final Story story, final double width) {
            super();

            setPadding(EMPTY);

            setPrefWidth(width);
            setMaxWidth(width);
            setMinWidth(width);

            VBox container = new VBox(5);
            container.setPickOnBounds(false);
            container.setPadding(new Insets(5));

            title = new Text(story.getName());
            title.setFont(getBolderFont());
            title.setWrappingWidth(width - 15);
            title.setFontSmoothingType(LCD);

            description = new Text(story.getDescription());
            description.setFont(getFont());
            description.setWrappingWidth(width - 30);
            description.setFontSmoothingType(LCD);

            container.getChildren().addAll(title, description);
            getChildren().addAll(container);

            container.setOnMouseClicked(event -> {
                story.setActive(!story.isActive());

                if (story.isActive()) {
                    setActiveStory(story);
                } else {
                    setActiveStory(null);
                }
            });

            story.getActiveProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // if story is active
                    makeDisabled(false);
                    // disable any active notes in the newly active story to get rid of old highlighting
                    for (Note note : story.getNotes()) {
                        note.setActive(false);
                    }
                } else {
                    // is story is inactive
                    makeDisabled(true);
                }
            });

            if (story.isActive()) {
                makeDisabled(false);
            } else {
                makeDisabled(true);
            }
        }

        public void makeDisabled(boolean disabled) {
            if (!disabled) {
                colorTexts(BLACK, title, description);
            } else {
                colorTexts(GREY, title, description);
            }
        }
    }

    /**
     * This private class is the graphical representation of a {@link Note} item
     * and a subclass of the JavaFX class {@link VBox}. When a note is clicked,
     * the time property is changed so that the 3D subscene navigates to the
     * note's effective start time. This graphical item is rendered in the
     * {@link ListCell} of an active story in the {@link ListView} in the
     * 'Stories' tab. Note titles are also expandable (making the notes
     * description visible) by clicking on the triangle rendered to the left of
     * the note's title.
     */
    public class NoteListCellGraphic extends VBox {

        private HBox contentsContainer;
        private Text expandIcon;
        private Text title;

        private Text contents;

        // Input note is the note to which this graphic belongs to
        public NoteListCellGraphic(final Note note, final double width) {
            super();

            setPrefWidth(width);
            setMaxWidth(USE_PREF_SIZE);
            setMinWidth(USE_PREF_SIZE);

            setPadding(new Insets(3));

            // note heading (its title) graphics
            final HBox titleContainer = new HBox(0);

            expandIcon = new Text("▶");
            expandIcon.setPickOnBounds(true);
            expandIcon.setFont(getFont());
            expandIcon.setFontSmoothingType(LCD);
            expandIcon.toFront();
            expandIcon.setOnMouseClicked(event -> {
                note.setListExpanded(!note.isListExpanded());
                expandNote(note.isListExpanded());
            });

            final Region r1 = new Region();
            r1.setPrefWidth(5);
            r1.setMinWidth(USE_PREF_SIZE);
            r1.setMaxWidth(USE_PREF_SIZE);

            title = new Text(note.getTagName());
            title.setWrappingWidth(width - 30 - r1.prefWidth(-1) - expandIcon.prefWidth(-1));
            title.setFont(getBolderFont());
            title.setFontSmoothingType(LCD);

            titleContainer.getChildren().addAll(expandIcon, r1, title);
            titleContainer.setAlignment(CENTER_LEFT);

            getChildren().add(titleContainer);

            // note contents graphics
            contentsContainer = new HBox(0);

            final Region r2 = new Region();
            r2.setPrefWidth(r1.prefWidth(-1) + expandIcon.prefWidth(-1));
            r2.setMinWidth(USE_PREF_SIZE);
            r2.setMaxWidth(USE_PREF_SIZE);

            contents = new Text(note.getTagContents());
            contents.setWrappingWidth(width - 30 - r2.prefWidth(-1));
            contents.setFont(getFont());
            contents.setFontSmoothingType(LCD);

            contentsContainer.getChildren().addAll(r2, contents);
            expandNote(note.isListExpanded());

            setPickOnBounds(false);
            setOnMouseClicked(event -> {
                if (event.getPickResult().getIntersectedNode() != expandIcon) {
                    note.setActive(!note.isActive());
                    if (note.isActive()) {
                        setActiveNoteWithSubsceneRebuild(note);
                    } else {
                        setActiveNoteWithSubsceneRebuild(null);
                    }
                }
            });
            note.getActiveProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    highlightCell(true);
                } else {
                    highlightCell(false);
                }
            });
            highlightCell(note.isActive());

            // render note changes
            note.getChangedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    title.setText(note.getTagName());
                    contents.setText(note.getTagContents());
                }
            });
        }

        /**
         * Highlights/un-highlights a cell according to the input parameter.
         * When a cell is highlighted/un-highighted, its text and background
         * colors change.
         *
         * @param highlight
         *         The boolean whose value is TRUE when this
         *         {@link NoteListCellGraphic} is to be highlighted, FALSE
         *         when it is to be un-highlighted
         */
        private void highlightCell(boolean highlight) {
            if (highlight) {
                setStyle("-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar; "
                        + "-fx-background: -fx-accent;");
                colorTexts(WHITE, expandIcon, title, contents);
            } else {
                setStyle("-fx-background-color: white;");
                colorTexts(BLACK, expandIcon, title, contents);
            }
        }

        /**
         * Expands/hides a notes description according to the input parameter.
         *
         * @param expanded
         *         true when the note should be expanded (showing the description), false otherwise
         */
        private void expandNote(boolean expanded) {
            if (expanded) {
                getChildren().add(contentsContainer);
                expandIcon.setText(expandIcon.getText().replace("▶", "▼"));
            } else {
                getChildren().remove(contentsContainer);
                expandIcon.setText(expandIcon.getText().replace("▼", "▶"));
            }
        }
    }
}