package wormguides;

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
 * The Constructor is called by {@link RootLayoutController} on initialization.
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

	public StoriesLayer(Stage parent, SceneElementsList elementsList, StringProperty cellNameProperty, LineageData data,
			Window3DController sceneController, BooleanProperty useInternalRulesFlag, int movieTimeOffset,
			Button newStoryButton) {

		parentStage = parent;

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

		StoriesLoader.loadConfigFile(stories, timeOffset);
		addDefaultStory();

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
	}

	/**
	 * Adds a blank default story upon initialization.
	 */
	private void addDefaultStory() {
		Story defaultStory = new Story("Blank Story", "This is a blank story. Create notes and set custom color rules!",
				"");
		stories.add(defaultStory);
		setActiveStory(defaultStory);
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
			//URLLoader.process("", window3DController);
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

	public void setUpdate3DProperty(BooleanProperty update3D) {
		this.update3D = update3D;
	}

	private Integer getEffectiveEndTime(Note activeNote) {
		int time = Integer.MIN_VALUE;

		if (activeNote != null) {
			if (activeNote.attachedToCell() || activeNote.attachedToStructure()) {

				int entityStartTime;
				int entityEndTime;

				if (activeNote.attachedToCell()) {
					entityStartTime = cellData.getFirstOccurrenceOf(activeNote.getCellName());
					entityEndTime = cellData.getLastOccurrenceOf(activeNote.getCellName());
				} else {
					entityStartTime = sceneElementsList.getFirstOccurrenceOf(activeNote.getCellName());
					entityEndTime = sceneElementsList.getLastOccurrenceOf(activeNote.getCellName());
				}

				// attached to cell/structure and time is specified
				if (activeNote.isTimeSpecified()) {
					int noteStartTime = activeNote.getStartTime();
					int noteEndTime = activeNote.getEndTime();

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

			else if (activeNote.isTimeSpecified()) {
				time = activeNote.getEndTime();
			}

		}

		return new Integer(time);
	}

	private Integer getEffectiveStartTime(Note activeNote) {
		int time = Integer.MIN_VALUE;

		if (activeNote != null) {
			if (activeNote.attachedToCell() || activeNote.attachedToStructure()) {

				int entityStartTime;
				int entityEndTime;

				if (activeNote.attachedToCell()) {
					entityStartTime = cellData.getFirstOccurrenceOf(activeNote.getCellName());
					entityEndTime = cellData.getLastOccurrenceOf(activeNote.getCellName());
				} else {
					entityStartTime = sceneElementsList.getFirstOccurrenceOf(activeNote.getCellName());
					entityEndTime = sceneElementsList.getLastOccurrenceOf(activeNote.getCellName());
				}

				// attached to cell/structure and time is specified
				if (activeNote.isTimeSpecified()) {
					int noteStartTime = activeNote.getStartTime();
					int noteEndTime = activeNote.getEndTime();

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

			else if (activeNote.isTimeSpecified()) {
				time = activeNote.getStartTime();
			}

		}

		return new Integer(time);
	}

	public Story getActiveStory() {
		return activeStory;
	}

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

	/*
	 * Returns array list of all active notes at input time Includes notes
	 * attached to an entity if entity is present at input time
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

	public ObservableList<Story> getStories() {
		return stories;
	}

	// Used for sizing the widths each story item in the list view
	public ChangeListener<Number> getListViewWidthListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// subtract off list view border and/or padding
				width = observable.getValue().doubleValue() - 2;
			}
		};
	}

	// Listener for edit button under stories list view
	public EventHandler<ActionEvent> getEditButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				bringUpEditor();
			}
		};
	}

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

			// loader.setRoot(editController);

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
					node.setStyle("-fx-focus-color: -fx-outer-border; " + "-fx-faint-focus-color: transparent;");
				}

			} catch (IOException e) {
				System.out.println("error in initializing note editor.");
				e.printStackTrace();
			}
		}

		editStage.show();
		editStage.toFront();
	}

	public BooleanProperty getRebuildSceneFlag() {
		return rebuildSceneFlag;
	}

	// Renderer for story item
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
							s.setStyle("-fx-focus-color: -fx-outer-border; " + "-fx-faint-focus-color: transparent;");
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

	// Used by story and note list cell graphics
	public void colorTexts(Color color, Text... texts) {
		for (Text text : texts)
			text.setStyle("-fx-fill:" + color.toString().toLowerCase().replace("0x", "#"));
	}

	// Graphical representation of a story
	// story becomes active when the title/description is clicked
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

	// Graphical representation of a note
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

			expandIcon = new Text("▸");
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
