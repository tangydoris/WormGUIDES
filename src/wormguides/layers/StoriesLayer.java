package wormguides.layers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import wormguides.MainApp;
import wormguides.URLGenerator;
import wormguides.controllers.StoryEditorController;
import wormguides.controllers.Window3DController;
import wormguides.loaders.StoriesLoader;
import wormguides.loaders.StoryFileUtil;
import wormguides.loaders.URLLoader;
import wormguides.model.LineageData;
import wormguides.model.Note;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.model.Story;
import wormguides.view.AppFont;

/**
 * This class is the controller of the {@link ListView} in the 'Stories' tab.
 * The Constructor is called by the main application controller
 * {@link RootLayoutController} on initialization.
 * 
 * @author Doris Tang
 */
public class StoriesLayer {

	private Stage parentStage;

	private SceneElementsList sceneElementsList;

	private ObservableList<Story> stories;

	private int timeOffset;

	private double width;
	private Stage editStage;

	private BooleanProperty rebuildSceneFlag;

	private StoryEditorController editController;

	private Note activeNote;
	private Story activeStory;

	private StringProperty activeStoryProperty;
	private StringProperty activeCellProperty;
	private BooleanProperty cellClickedProperty;

	private IntegerProperty timeProperty;

	private LineageData cellData;

	private Comparator<Note> noteComparator;

	private ObservableList<Rule> currentRules;
	private BooleanProperty useInternalRules;
	private Window3DController window3DController;

	private BooleanProperty update3D;
	
	private boolean defaultEmbryoFlag;

	/**
	 * Constructure called by {@link RootLayoutController}.
	 * 
	 * @param parent
	 *            The {@link Stage} to which the main application belongs to.
	 *            Used for initializing modality of the story editor popup
	 *            window.
	 * @param elementsList
	 *            The {@link SceneElementsList} that contains all
	 *            {@link SceneElement}s loaded on application startup
	 * @param cellNameProperty
	 *            The {@link StringProperty} whose value contains the String of
	 *            the currently active cell, cell body, or multiceulluar
	 *            structure name
	 * @param data
	 *            The {@link LineageData} that contains information about the
	 *            nucleus lineage data loaded on application startup
	 * @param sceneController
	 *            The {@link Window3DController} that controls the 3D subscene
	 * @param useInternalRulesFlag
	 *            The {@link BooleanProperty} that is set to TRUE when the
	 *            application should use the program's internal color rules,
	 *            FALSE otherwise (use the story's rules instead)
	 * @param movieTimeOffset
	 *            The integer set the the value of the movie's time offset from
	 *            the internal program's start time of 1
	 * @param newStoryButton
	 *            The reference to the {@link Button} whose functionality is to
	 *            create a new story (located in the 'Stories' tab)
	 */
	public StoriesLayer(Stage parent, SceneElementsList elementsList, StringProperty cellNameProperty, LineageData data,
			Window3DController sceneController, BooleanProperty useInternalRulesFlag, int movieTimeOffset,
			Button newStoryButton, Button deleteStoryButton, boolean defaultEmbryoFlag) {

		parentStage = parent;
		
		this.defaultEmbryoFlag = defaultEmbryoFlag;

		newStoryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Story story = new Story(NEW_STORY_TITLE, NEW_STORY_DESCRIPTION, "");
				stories.add(story);
				setActiveStory(story);
				setActiveNote(null);

				bringUpEditor();
			}
		});

		deleteStoryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (activeStory != null) {
					stories.remove(activeStory);
					setActiveStory(null);
					setActiveNote(null);
				}
			}
		});

		window3DController = sceneController;
		useInternalRules = useInternalRulesFlag;
		currentRules = window3DController.getObservableColorRulesList();

		sceneElementsList = elementsList;

		timeOffset = movieTimeOffset;

		stories = FXCollections.observableArrayList(
				story -> new Observable[] { story.getChangedProperty(), story.getActiveProperty() });
		stories.addListener(new ListChangeListener<Story>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Story> c) {
				while (c.next()) {
					// need this listener to detect change for some reason
					// leave this empty
				}
			}
		});

		timeProperty = window3DController.getTimeProperty();
		rebuildSceneFlag = new SimpleBooleanProperty(false);
		cellData = data;

		width = 0;

		activeStoryProperty = new SimpleStringProperty("");

		activeCellProperty = cellNameProperty;
		cellClickedProperty = window3DController.getCellClicked();

		if (defaultEmbryoFlag) {
			StoriesLoader.loadConfigFile(stories, timeOffset);
		}
		
		addBlankStory();

		noteComparator = new Comparator<Note>() {
			@Override
			public int compare(Note o1, Note o2) {
				Integer t1 = getEffectiveStartTime(o1);
				Integer t2 = getEffectiveStartTime(o2);
				if (t1.equals(t2))
					return o1.getTagName().compareTo(o2.getTagName());

				return t1.compareTo(t2);
			}
		};
		
		for (Story story : stories) {
			story.setComparator(noteComparator);
			story.sortNotes();
		}

		setActiveStory(stories.get(0)); // makes lim-4 story on default embryo, template otherwise
	}

	/**
	 * Adds a blank story upon initialization.
	 */
	private void addBlankStory() {
		Story blankStory = new Story("Template to Make Your Own Story",
				"Shows all segmented neurons without " + "further annotation.",
				"http://scene.wormguides.org/wormguides/testurlscript?/set/ash-n$@+#ff8"
						+ "fbc8f/rib-n$@+#ff663366/avg-n$@+#ffb41919/dd-n@+#ff4a24c1/da-"
						+ "n@+#ffc56002/dd-n$+#ffb30a95/da-n$+#ffe6b34d/rivl-n@+#ffffb366/"
						+ "rivr-n@+#ffffe6b3/sibd-n@+#ffe6ccff/siav-n@+#ff8099ff/view/"
						+ "time=393/rX=51.625/rY=-2.125/rZ=0.0/tX=0.0/tY=0.0/scale=2.25/dim=0.25/browser/");
		stories.add(blankStory);
	}

	/**
	 * Loades story from file and sets it as active story. Uses a
	 * {@link FileChooser} to allow the user to pick a load location.
	 */
	public void loadStory() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save Story");
		chooser.setInitialFileName("WormGUIDES Story.csv");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));

		File file = chooser.showOpenDialog(parentStage);
		if (file != null) {
			StoryFileUtil.loadFromCSVFile(stories, file, timeOffset);
		}
	}

	/**
	 * Saves active story to file. Uses a {@link FileChooser} to allow the user
	 * to pick a save location.
	 */
	public void saveActiveStory() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save Story");
		chooser.setInitialFileName("WormGUIDES Story.csv");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));

		File file = chooser.showSaveDialog(parentStage);
		// if user clicks save
		if (file != null) {
			try {
				if (activeStory != null) {
					updateColorURL();
					StoryFileUtil.saveToCSVFile(activeStory, file, timeOffset);
				} else
					System.out.println("no active story to save");
				// TODO make error pop up
			} catch (IOException e) {
				// TODO make error pop up
				System.out.println("error occurred while saving story");
				return;
			}
			System.out.println("file saved");
		}
	}

	/**
	 * Because the Color URL is set on New Note, we need to update the color URL
	 * before saving to account for additions and deletions
	 */
	private void updateColorURL() {
		if (activeStory != null) {
			activeStory.setActive(false);
			ArrayList<Rule> rulesCopy = new ArrayList<Rule>();
			for (Rule rule : currentRules) {
				rulesCopy.add(rule);
			}

			activeStory.setColorURL(
					URLGenerator.generateInternal(rulesCopy, timeProperty.get(), window3DController.getRotationX(),
							window3DController.getRotationY(), window3DController.getRotationZ(),
							window3DController.getTranslationX(), window3DController.getTranslationY(),
							window3DController.getScaleInternal(), window3DController.getOthersVisibility()));
		}
	}

	/**
	 * @return The {@link StringProperty} activeStoryProperty that changes when
	 *         the active story changes. The value of the String is the name of
	 *         the currently active story.
	 */
	public StringProperty getActiveStoryProperty() {
		return activeStoryProperty;
	}

	/**
	 * @return The description of the current active story
	 */
	public String getActiveStoryDescription() {
		if (activeStory != null)
			return activeStory.getDescription();
		return "";
	}

	/**
	 * @return Effective start time of currently active story
	 */
	public int getActiveStoryStartTime() {
		if (activeStory != null && activeStory.hasNotes())
			return getEffectiveStartTime(activeStory.getNotes().get(0));
		return Integer.MIN_VALUE;
	}

	/**
	 * Ultimately sets the active story to the input {@link Story} parameter.
	 * Sets the currently active story to be inactive if it is not null, then
	 * sets the input story to active.
	 * 
	 * @param story
	 *            The story that needs to be made active
	 */
	public void setActiveStory(Story story) {
		// disable previous active story
		// copy current rules changes back to story
		if (activeStory != null) {
			activeStory.setActive(false);
			ArrayList<Rule> rulesCopy = new ArrayList<Rule>();
			rulesCopy.addAll(currentRules);

			activeStory.setColorURL(
					URLGenerator.generateInternal(rulesCopy, timeProperty.get(), window3DController.getRotationX(),
							window3DController.getRotationY(), window3DController.getRotationZ(),
							window3DController.getTranslationX(), window3DController.getTranslationY(),
							window3DController.getScaleInternal(), window3DController.getOthersVisibility()));
		}

		setActiveNote(null);
		useInternalRules.set(true);

		activeStory = story;
		int startTime = timeProperty.get();

		if (activeStory != null) {
			activeStory.setActive(true);
			activeStoryProperty.set(activeStory.getName());

			// if story does not come with a url, set its url to the
			// program's internal rules
			if (activeStory.getColorURL().isEmpty()) {
				ArrayList<Rule> rulesCopy = new ArrayList<Rule>();
				rulesCopy.addAll(currentRules);

				activeStory.setColorURL(
						URLGenerator.generateInternal(rulesCopy, timeProperty.get(), window3DController.getRotationX(),
								window3DController.getRotationY(), window3DController.getRotationZ(),
								window3DController.getTranslationX(), window3DController.getTranslationY(),
								window3DController.getScaleInternal(), window3DController.getOthersVisibility()));
			} else { // if story does come with url, use it
				useInternalRules.set(false);
			}
			URLLoader.process(activeStory.getColorURL(), window3DController, true);

			if (activeStory.hasNotes()) {
				startTime = getEffectiveStartTime(activeStory.getNotes().get(0));
				if (startTime < 1)
					startTime = 1;
			}
		} else {
			activeStoryProperty.set("");
			useInternalRules.set(true);
		}

		if (editController != null)
			editController.setActiveStory(activeStory);

		if (timeProperty.get() != startTime)
			timeProperty.set(startTime);
		else {
			rebuildSceneFlag.set(true);
			rebuildSceneFlag.set(false);
		}
	}

	/**
	 * Sets the active note to the input note parameter. Makes the current note
	 * inactive, then makes the input note active.
	 * 
	 * @param note
	 *            The {@link Note} that should become active
	 */
	public void setActiveNote(Note note) {
		// deactivate the previous active note
		if (activeNote != null)
			activeNote.setActive(false);

		activeNote = note;
		if (activeNote != null) {
			activeNote.setActive(true);

			// set time property to be read by 3d window
			if (!activeNote.getTagName().equals("New Note")) {
				int startTime = getEffectiveStartTime(activeNote);
				if (startTime < 1)
					startTime = 1;

				timeProperty.set(startTime);
			}
		}

		if (editController != null)
			editController.setActiveNote(activeNote);
	}

	/**
	 * Sets the flag that tells the {@link Window3DController} whether to update
	 * the subscene.
	 * 
	 * @param update3D
	 *            The {@link BooleanProperty} flag that is set to TRUE when the
	 *            3D subscene should be udpated, FALSE otherwise
	 */
	public void setUpdate3DProperty(BooleanProperty update3D) {
		this.update3D = update3D;
	}

	/**
	 * Retrieve the effective end time of the input note parameter, whether it
	 * is the one explicitly stated by the 'end time' field or the one
	 * implicitly specified by the cell, cell body, or multicellular structure.
	 * 
	 * @param note
	 *            The {@link Note} whose effective end time is queried
	 * @return {@link Integer} that contains the value of the effective end time
	 *         of the input note. An Integer object is returned instead of the
	 *         primitive int so that it can be passed into the
	 *         {@link Comparator} for notes.
	 */
	private Integer getEffectiveEndTime(Note note) {
		int time = Integer.MIN_VALUE;

		if (note != null) {
			if (note.attachedToCell() || note.attachedToStructure()) {

				int entityStartTime;
				int entityEndTime;

				if (note.attachedToCell()) {
					entityStartTime = cellData.getFirstOccurrenceOf(note.getCellName());
					entityEndTime = cellData.getLastOccurrenceOf(note.getCellName());
				} else {
					entityStartTime = sceneElementsList.getFirstOccurrenceOf(note.getCellName());
					entityEndTime = sceneElementsList.getLastOccurrenceOf(note.getCellName());
				}

				// attached to cell/structure and time is specified
				if (note.isTimeSpecified()) {
					int noteStartTime = note.getStartTime();
					int noteEndTime = note.getEndTime();

					// make sure times actually overlap
					if (noteStartTime <= entityEndTime && entityEndTime <= noteEndTime)
						time = entityEndTime;
					else if (entityStartTime <= noteEndTime && noteEndTime < entityEndTime)
						time = noteEndTime;
				}

				// attached to cell/structure and time not specified
				else
					time = entityEndTime;
			}

			else if (note.isTimeSpecified()) {
				time = note.getEndTime();
			}

		}

		return new Integer(time);
	}

	/**
	 * Retrieve the effective start time of the input note parameter, whether it
	 * is the one explicitly stated by the 'start time' field or the one
	 * implicitly specified by the cell, cell body, or multicellular structure.
	 * 
	 * @param note
	 *            The {@link Note} whose effective start time is queried
	 * @return {@link Integer} that contains the value of the effective start
	 *         time of the input note. An Integer object is returned instead of
	 *         the primitive int so that it can be passed into the
	 *         {@link Comparator} for notes.
	 */
	private Integer getEffectiveStartTime(Note note) {
		int time = Integer.MIN_VALUE;

		if (note != null) {
			if (note.attachedToCell() || note.attachedToStructure()) {

				int entityStartTime;
				int entityEndTime;

				if (note.attachedToCell()) {
					entityStartTime = cellData.getFirstOccurrenceOf(note.getCellName());
					entityEndTime = cellData.getLastOccurrenceOf(note.getCellName());
				} else {
					entityStartTime = sceneElementsList.getFirstOccurrenceOf(note.getCellName());
					entityEndTime = sceneElementsList.getLastOccurrenceOf(note.getCellName());
				}

				// attached to cell/structure and time is specified
				if (note.isTimeSpecified()) {
					int noteStartTime = note.getStartTime();
					int noteEndTime = note.getEndTime();

					// make sure times actually overlap
					if (noteStartTime <= entityStartTime && entityStartTime <= noteEndTime)
						time = entityStartTime;
					else if (entityStartTime <= noteStartTime && noteStartTime < entityEndTime)
						time = noteStartTime;
				}

				// attached to cell/structure and time not specified
				else
					time = entityStartTime;
			}

			else if (note.isTimeSpecified()) {
				time = note.getStartTime();
			}

		}

		return new Integer(time);
	}

	/**
	 * @return The currently active {@link Story}.
	 */
	public Story getActiveStory() {
		return activeStory;
	}

	/**
	 * @param tagName
	 *            The tag name of that note whose comments the user wants to
	 *            retrieve
	 * @return A {@link String} with the comments of the note whose tag name is
	 *         specified by the input parameter
	 */
	public String getNoteComments(String tagName) {
		String comments = "";
		for (Story story : stories) {
			if (!story.getNoteComment(tagName).isEmpty()) {
				comments = story.getNoteComment(tagName);
				break;
			}
		}
		return comments;
	}

	public ArrayList<Note> getNotesWithEntity() {
		ArrayList<Note> notes = new ArrayList<Note>();
		for (Story story : stories) {
			if (story.isActive())
				notes.addAll(story.getNotesWithEntity());
		}
		return notes;
	}

	/**
	 * 
	 * @param time
	 *            An integer whose value is the queried time
	 * @return An {@link ArrayList} of all notes that can exist at the at input
	 *         time. This includes notes attached to an entity if entity is
	 *         present at input time. These notes are later filtered out.
	 */
	public ArrayList<Note> getNotesAtTime(int time) {
		ArrayList<Note> notes = new ArrayList<Note>();

		for (Story story : stories) {
			if (story.isActive())
				notes.addAll(story.getPossibleNotesAtTime(time));
		}

		if (!notes.isEmpty()) {
			Iterator<Note> iter = notes.iterator();
			Note note;
			while (iter.hasNext()) {
				note = iter.next();

				int effectiveStart = getEffectiveStartTime(note);
				int effectiveEnd = getEffectiveEndTime(note);
				if (effectiveStart != Integer.MIN_VALUE && effectiveEnd != Integer.MIN_VALUE
						&& (time < effectiveStart || effectiveEnd < time)) {
					iter.remove();
				}
			}
		}

		return notes;
	}

	/**
	 * @return A {@link String} representation of all stories visible in the
	 *         'Stories' tab
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("Stories:\n");
		for (int i = 0; i < stories.size(); i++) {
			Story story = stories.get(i);
			sb.append(story.getName()).append(": ").append(story.getNumberOfNotes()).append(" notes\n");
			for (Note note : story.getNotes()) {
				sb.append("\t").append(note.getTagName()).append(": times ").append(note.getStartTime()).append(" ")
						.append(note.getEndTime()).append("\n");
			}
			if (i < stories.size() - 1)
				sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * @return The {@link ObservableList} containing all stories that are
	 *         visible in the 'Stories' tab
	 */
	public ObservableList<Story> getStories() {
		return stories;
	}

	/**
	 * Used for sizing the widths each story item in the list view (May not be
	 * used/ is deprecated)
	 * 
	 * @return A {@link ChangeListener} that listens to the change in the
	 *         'Stories' tab {@link ListView} viewport
	 */
	public ChangeListener<Number> getListViewWidthListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// subtract off list view border and/or padding
				width = observable.getValue().doubleValue() - 2;
			}
		};
	}

	/**
	 * @return The {@link EventHandler} for 'Edit Story' button's clicked
	 *         {@link ActionEvent} in the 'Stories' tab
	 */
	public EventHandler<ActionEvent> getEditButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				bringUpEditor();
			}
		};
	}

	/**
	 * Brings up the story/notes editor window, controlled by the
	 * {@link StoryEditorController}. Upon the editor's initialization/fxml
	 * load, listenable properties are passed to the editor so that the it can
	 * rename labels/change the UI according to changes in time and active cell
	 * name.<br>
	 * <br>
	 * The editor is initialized so that it always lives on top of the main
	 * application window and moves when the main window is moved. This is to
	 * ensure that the user can always edit a story when the window is opened
	 * even when he/she is clicking around in the 3D subscene to change the
	 * time/active cell.
	 */
	private void bringUpEditor() {
		if (editStage == null) {
			editController = new StoryEditorController(timeOffset, cellData,
					sceneElementsList.getAllMulticellSceneNames(), activeCellProperty, cellClickedProperty,
					timeProperty, update3D);

			editController.setUpdate3DProperty(update3D);

			editController.setActiveNote(activeNote);
			editController.setActiveStory(activeStory);

			editStage = new Stage();

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/layouts/StoryEditorLayout.fxml"));

			loader.setController(editController);
			loader.setRoot(editController);

			try {
				editStage.setScene(new Scene((AnchorPane) loader.load()));

				editStage.setTitle("Story/Note Editor");
				editStage.initOwner(parentStage);
				editStage.initModality(Modality.NONE);
				editStage.setResizable(true);

				editStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						rebuildSceneFlag.set(true);
						rebuildSceneFlag.set(false);
					}
				});

				editController.getNoteCreatedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						if (newValue) {
							Note newNote = editController.getActiveNote();
							editController.setNoteCreated(false);
							activeStory.addNote(newNote);
							setActiveNote(newNote);

							rebuildSceneFlag.set(true);
							rebuildSceneFlag.set(false);
						}
					}
				});

				editController.addDeleteButtonListener(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (activeNote != null)
							activeStory.removeNote(activeNote);

						setActiveNote(null);
					}
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
	 * @return The {@link BooleanProperty} whose value is TRUE when the
	 *         {@link Window3DController} should rebuild the subscene, and FALSE
	 *         otherwise
	 */
	public BooleanProperty getRebuildSceneFlag() {
		return rebuildSceneFlag;
	}

	/**
	 * @return The {@link Callback} that is the renderer for a {@link Story}
	 *         item. It graphically renders an active story with black text and
	 *         an inactive one with grey text. For an active story, its notes
	 *         are also rendered beneath the story title and description.
	 */
	public Callback<ListView<Story>, ListCell<Story>> getStoryCellFactory() {
		return new Callback<ListView<Story>, ListCell<Story>>() {
			@Override
			public ListCell<Story> call(ListView<Story> param) {
				ListCell<Story> cell = new ListCell<Story>() {
					@Override
					protected void updateItem(Story story, boolean empty) {
						super.updateItem(story, empty);

						if (!empty) {
							// Create story graphic
							StoryListCellGraphic storyGraphic = new StoryListCellGraphic(story, width);

							// Add list view for notes inside story graphic
							if (story.isActive()) {
								for (Note note : story.getNotes()) {
									NoteListCellGraphic noteGraphic = new NoteListCellGraphic(note);
									storyGraphic.getChildren().add(noteGraphic);

								}
							}

							Separator s = new Separator(Orientation.HORIZONTAL);
							s.setFocusTraversable(false);
							s.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
							storyGraphic.getChildren().add(s);

							setGraphic(storyGraphic);
						} else
							setGraphic(null);

						setStyle("-fx-background-color: transparent;");
						setPadding(Insets.EMPTY);
					}
				};
				return cell;
			}
		};
	}

	/**
	 * Changes the color of the input {@link Text} items by modifying the
	 * java-fx css attribute '-fx-fill' to the specified input color. Used by
	 * {@link StoryListCellGraphic} and {@link NoteListCellGraphic} items.
	 * 
	 * @param color
	 *            The {@link Color} to change the texts to
	 * @param texts
	 *            The listing of {@link Text} items whose color is to be changed
	 */
	public void colorTexts(Color color, Text... texts) {
		for (Text text : texts)
			text.setStyle("-fx-fill:" + color.toString().toLowerCase().replace("0x", "#"));
	}

	/**
	 * This private class is the graphical representation of a {@link Story}
	 * item and a subclass of the JavaFX class {@link VBox}. It makes an
	 * inactive story become active when the title/description is clicked, and
	 * makes an active story become inactive when it is clicked. This graphical
	 * item is rendered in the {@link ListCell} of the {@link ListView} in the
	 * 'Stories' tab.
	 */
	private class StoryListCellGraphic extends VBox {

		private Text title;
		private Text description;

		public StoryListCellGraphic(Story story, double width) {
			super();

			setPadding(Insets.EMPTY);

			setPrefWidth(width);
			setMaxWidth(width);
			setMinWidth(width);

			VBox container = new VBox(3);
			container.setPickOnBounds(false);
			container.setPadding(new Insets(5));

			title = new Text(story.getName());
			title.setFont(AppFont.getBolderFont());
			title.setWrappingWidth(width - 15);
			title.setFontSmoothingType(FontSmoothingType.LCD);

			description = new Text(story.getDescription());
			description.setFont(AppFont.getFont());
			description.setWrappingWidth(width - 15);
			description.setFontSmoothingType(FontSmoothingType.LCD);

			container.getChildren().addAll(title, description);
			getChildren().addAll(container);

			container.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					story.setActive(!story.isActive());

					if (story.isActive())
						setActiveStory(story);
					else
						setActiveStory(null);
				}
			});

			story.getActiveProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue)
						makeDisabled(false);
					else
						makeDisabled(true);
				}
			});

			if (story.isActive())
				makeDisabled(false);
			else
				makeDisabled(true);
		}

		public void makeDisabled(boolean disabled) {
			if (!disabled)
				colorTexts(Color.BLACK, title, description);
			else
				colorTexts(Color.GREY, title, description);
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
		public NoteListCellGraphic(Note note) {
			super();

			note.getParent();
			setPadding(new Insets(3));

			// title heading graphics
			HBox titleContainer = new HBox(0);
			titleContainer.setPrefWidth(width);
			titleContainer.setMaxWidth(width);
			titleContainer.setMinWidth(width);

			expandIcon = new Text("� ");
			expandIcon.setPickOnBounds(true);
			expandIcon.setFont(AppFont.getBolderFont());
			expandIcon.setFontSmoothingType(FontSmoothingType.LCD);
			expandIcon.toFront();
			expandIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					note.setListExpanded(!note.isListExpanded());
					expandNote(note.isListExpanded());
				}
			});

			Region r1 = new Region();
			r1.setPrefWidth(5);
			r1.setMinWidth(USE_PREF_SIZE);
			r1.setMaxWidth(USE_PREF_SIZE);

			title = new Text(note.getTagName());
			title.setWrappingWidth(width - 15 - r1.prefWidth(-1) - expandIcon.prefWidth(-1));
			title.setFont(AppFont.getBolderFont());
			title.setFontSmoothingType(FontSmoothingType.LCD);

			titleContainer.getChildren().addAll(expandIcon, r1, title);
			titleContainer.setAlignment(Pos.CENTER_LEFT);

			getChildren().add(titleContainer);

			// contents graphics
			contentsContainer = new HBox(0);

			Region r2 = new Region();
			r2.setPrefWidth(15);
			r2.setMinWidth(USE_PREF_SIZE);
			r2.setMaxWidth(USE_PREF_SIZE);

			contents = new Text(note.getTagContents());
			contents.setWrappingWidth(width - 15 - r2.prefWidth(-1));
			contents.setFont(AppFont.getFont());
			contents.setFontSmoothingType(FontSmoothingType.LCD);

			contentsContainer.getChildren().addAll(r2, contents);
			expandNote(note.isListExpanded());

			setPickOnBounds(false);
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getPickResult().getIntersectedNode() != expandIcon) {
						note.setActive(!note.isActive());
						if (note.isActive())
							setActiveNote(note);
						else
							setActiveNote(null);
					}
				}
			});
			note.getActiveProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue)
						highlightCell(true);
					else
						highlightCell(false);
				}
			});
			highlightCell(note.isActive());

			// render note changes
			note.getChangedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						title.setText(note.getTagName());
						contents.setText(note.getTagContents());
					}
				}
			});
		}

		/**
		 * Highlights/un-highlights a cell according to the input parameter.
		 * When a cell is highlighted/un-highighted, its text and background
		 * colors change.
		 * 
		 * @param highlight
		 *            The boolean whose value is TRUE when this
		 *            {@link NoteListCellGraphic} is to be highlighted, FALSE
		 *            when it is to be un-highlighted
		 */
		private void highlightCell(boolean highlight) {
			if (highlight) {
				setStyle("-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar; "
						+ "-fx-background: -fx-accent;");
				colorTexts(Color.WHITE, expandIcon, title, contents);
			} else {
				setStyle("-fx-background-color: white;");
				colorTexts(Color.BLACK, expandIcon, title, contents);
			}
		}

		/**
		 * Expands/hides a notes description according to the input parameter.
		 * 
		 * @param expanded
		 *            The boolean whose value is TRUE when this
		 *            {@link NoteListCellGraphic} is to be expanded, FALSE when
		 *            it should only show the note title
		 */
		private void expandNote(boolean expanded) {
			if (expanded) {
				getChildren().add(contentsContainer);
				expandIcon.setText(expandIcon.getText().replace("▸", "▾"));
			} else {
				getChildren().remove(contentsContainer);
				expandIcon.setText(expandIcon.getText().replace("▾", "▸"));
			}
		}
	}

	private final String NEW_STORY_TITLE = "New Story";
	private final String NEW_STORY_DESCRIPTION = "New story description here";

}
