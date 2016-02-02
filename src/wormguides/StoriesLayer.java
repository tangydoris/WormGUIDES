package wormguides;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import wormguides.controllers.StoryEditorController;
import wormguides.loaders.StoriesLoader;
import wormguides.model.LineageData;
import wormguides.model.Note;
import wormguides.model.Story;
import wormguides.view.AppFont;

/*
 * Controller of the ListView in the 'Stories' tab
 */
public class StoriesLayer {
	
	private Stage parentStage;

	private ObservableList<Story> stories;
	
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
	
	
	public StoriesLayer(Stage parent, StringProperty cellNameProperty, 
			IntegerProperty sceneTimeProperty, BooleanProperty cellClicked, LineageData data) {
		parentStage = parent;
		
		stories = FXCollections.observableArrayList(
				story -> new Observable[]{story.getChangedProperty()});
		stories.addListener(new ListChangeListener<Story>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Story> c) {
				while (c.next()) {
					if (c.wasUpdated()) {
					}
					else {
						for (Story story : c.getAddedSubList()) {
						}
						for (Story story : c.getRemoved()) {
						}
					}
				}
			}
		});
		
		timeProperty = sceneTimeProperty;
		rebuildSceneFlag = new SimpleBooleanProperty(false);
		cellData = data;
		
		width = 0;
		
		activeStoryProperty = new SimpleStringProperty("");
		
		activeCellProperty = cellNameProperty;
		cellClickedProperty = cellClicked;
		
		StoriesLoader.load(STORY_CONFIG_FILE_NAME, stories);
		
		noteComparator = new Comparator<Note>() {
			@Override
			public int compare(Note o1, Note o2) {
				Integer t1 = getEffectiveStartTime(o1, cellData);
				Integer t2 = getEffectiveStartTime(o2, cellData);
				if (t1.equals(t2))
					return o1.getTagName().compareTo(o2.getTagName());
				else
					return t1.compareTo(t2);
			}
		};
		
		for (Story story : stories) {
			story.setComparator(noteComparator);
			story.sortNotes();
		}
	}
	
	
	public StringProperty getActiveStoryProperty() {
		return activeStoryProperty;
	}
	
	
	public String getActiveStoryDescription() {
		if (activeStory!=null)
			return activeStory.getDescription();
		else
			return "";
	}
	
	
	public void setActiveStory(Story story) {
		// disable previous active story
		if (activeStory!=null)
			activeStory.setActive(false);
		
		setActiveNote(null);
		
		activeStory = story;
		if (activeStory!=null) {
			activeStory.setActive(true);
			activeStoryProperty.set(activeStory.getName());
		}
		else
			activeStoryProperty.set("");
		
		if (editController!=null)
			editController.setActiveStory(activeStory);
		
		rebuildSceneFlag.set(true);
		rebuildSceneFlag.set(false);
	}
	
	
	public void setActiveNote(Note note) {
		// deactivate the previous active note
		if (activeNote!=null)
			activeNote.setActive(false);
		
		activeNote = note;
		if (activeNote!=null) {
			activeNote.setActive(true);
			
			// set time property to be read by 3d window
			timeProperty.set(getEffectiveStartTime(activeNote, cellData));
		}
		
		if (editController!=null)
			editController.setActiveNote(activeNote);
	}
	
	
	private Integer getEffectiveStartTime(Note activeNote, LineageData cellData) {
		int time = 1;
		
		if (activeNote!=null && activeNote.isTimeSpecified()) {
			if (activeNote.existsWithCell()) {
				int cellStartTime = cellData.getFirstOccurrenceOf(activeNote.getCellName());
				int cellEndTime = cellData.getLastOccurrenceOf(activeNote.getCellName());
				int noteStartTime = activeNote.getStartTime();
				int noteEndTime = activeNote.getEndTime();
				
				// make sure times actually overlap
				if (noteStartTime<=cellStartTime && cellStartTime<=noteEndTime)
					time = cellStartTime;
				else if (cellStartTime<=noteStartTime && noteStartTime<cellEndTime)
					time = noteStartTime;
			}
			
			else {
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
	
	
	public ArrayList<Note> getNotesWithCell() {
		ArrayList<Note> notes = new ArrayList<Note>();
		for (Story story : stories) {
			if (story.isActive())
				notes.addAll(story.getNotesWithCell());
		}
		return notes;
	}
	
	
	public ArrayList<Note> getActiveNotes(int time) {
		ArrayList<Note> notes = new ArrayList<Note>();
		
		for (Story story : stories) {
			if (story.isActive())
				notes.addAll(story.getNotesAtTime(time));
		}
		
		return notes;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Stories:\n");
		for (int i=0; i<stories.size(); i++) {
			Story story = stories.get(i);
			sb.append(story.getName()).append(": ")
				.append(story.getNumberOfNotes()).append(" notes\n");
			for (Note note : story.getNotes()) {
				sb.append("\t").append(note.getTagName()).append(": times ")
					.append(note.getStartTime()).append(" ")
					.append(note.getEndTime()).append("\n");
			}
			if (i<stories.size()-1)
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
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				// subtract off list view border and/or padding
				width = observable.getValue().doubleValue()-2;
			}
		};
	}
	
	
	// Listener for edit button under stories list view
	public EventHandler<ActionEvent> getEditButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (editStage==null) {
					editController = new StoryEditorController(cellData, activeCellProperty, 
							cellClickedProperty, timeProperty);
					
					editController.setActiveNote(activeNote);
					editController.setActiveStory(activeStory);
					
					editStage = new Stage();
					
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(MainApp.class.getResource("view/StoryEditorLayout.fxml"));
					
					loader.setController(editController);
					loader.setRoot(editController);
					
					try {
						editStage.setScene(new Scene((AnchorPane) loader.load()));
						
						editStage.setTitle("Story/Note Editor");
						editStage.initOwner(parentStage);
						editStage.initModality(Modality.NONE);
						editStage.setResizable(true);
						
						editController.getStoryCreatedProperty().addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
								if (newValue) {
									Story newStory = editController.getActiveStory();
									stories.add(newStory);
									setActiveStory(newStory);
									setActiveNote(null);
									
									editController.setStoryCreated(false);
								}
							}
						});
						
						editController.getNoteCreatedProperty().addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
								if (newValue) {
									Note newNote = editController.getActiveNote();
									editController.setNoteCreated(false);
									activeStory.addNote(newNote);
									setActiveNote(newNote);
								}
							}
						});
						
						editController.addDeleteButtonListener(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (activeNote!=null)
									activeStory.removeNote(activeNote);
								
								setActiveNote(null);
							}
						});
						
						for (Node node : editStage.getScene().getRoot().getChildrenUnmodifiable()) {
			            	node.setStyle("-fx-focus-color: -fx-outer-border; "+
			            					"-fx-faint-focus-color: transparent;");
			            }
						
					} catch (IOException e) {
						System.out.println("error in initializing note editor.");
						e.printStackTrace();
					}
				}
				
				editStage.show();
				editStage.toFront();
			}
		};
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
	                    
	                    if (!empty)  {
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
	                    	s.setStyle("-fx-focus-color: -fx-outer-border; "+
	            					"-fx-faint-focus-color: transparent;");
	                    	storyGraphic.getChildren().add(s);
	                    	
	                    	setGraphic(storyGraphic);
	                    }
	                	else
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
			text.setStyle("-fx-fill:"+color.toString().toLowerCase().replace("0x", "#"));
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
			title.setWrappingWidth(width-5);
			title.setFontSmoothingType(FontSmoothingType.LCD);

			description = new Text(story.getDescription());
			description.setFont(AppFont.getFont());
			description.setWrappingWidth(width-5);
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
				public void changed(ObservableValue<? extends Boolean> observable, 
						Boolean oldValue, Boolean newValue) {
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
	public class NoteListCellGraphic extends VBox{
		
		private Story story;
		private Note note;
		
		private HBox contentsContainer;
		private Text expandIcon;
		private Text title;

		private Text contents;
		
		
		// Input note is the note to which this graphic belongs to
		public NoteListCellGraphic(Note note) {
			super();
			
			story = note.getParent();
			this.note = note;

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
			title.setWrappingWidth(width-5-r1.prefWidth(-1)-expandIcon.prefWidth(-1));
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
			contents.setWrappingWidth(width-5-r2.prefWidth(-1));
			contents.setFont(AppFont.getFont());
			contents.setFontSmoothingType(FontSmoothingType.LCD);
			
			contentsContainer.getChildren().addAll(r2, contents);
			expandNote(note.isListExpanded());
			
			setPickOnBounds(false);
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getPickResult().getIntersectedNode()!=expandIcon) {
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
				public void changed(ObservableValue<? extends Boolean> observable, 
						Boolean oldValue, Boolean newValue) {
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
				public void changed(ObservableValue<? extends Boolean> observable, 
						Boolean oldValue, Boolean newValue) {
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
			}
			else {
				setStyle("-fx-background-color: white;");
				colorTexts(Color.BLACK, expandIcon, title, contents);
			}
		}
		
		
		private void expandNote(boolean expanded) {
			if (expanded) {
				getChildren().add(contentsContainer);
				expandIcon.setText(expandIcon.getText().replace("▸", "▾"));
			}
			else {
				getChildren().remove(contentsContainer);
				expandIcon.setText(expandIcon.getText().replace("▾", "▸"));
			}
		}	
	}

	private static final String STORY_CONFIG_FILE_NAME = "StoryListConfig.csv";
	
}
