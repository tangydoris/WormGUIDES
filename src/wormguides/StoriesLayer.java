package wormguides;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import wormguides.model.Note;
import wormguides.model.Story;
import wormguides.model.Note.AttachmentTypeEnumException;
import wormguides.model.Note.LocationStringFormatException;
import wormguides.model.Note.TagDisplayEnumException;
import wormguides.model.Note.TimeStringFormatException;
import wormguides.view.AppFont;

/*
 * Controller of the ListView in the 'Stories' tab
 */
public class StoriesLayer {

	private ObservableList<Story> stories;
	
	private HashMap<Story, StoryListCellGraphic> storyGraphicMap;
	
	private ObservableList<NoteListCellGraphic> noteGraphics;
	
	private double width;
	
	private Stage editStage;
	
	private BooleanProperty rebuildSceneFlag;
	
	private NoteEditorController editController;
	
	// Story/note being edited in note editor
	private Story currentStory;
	private Note currentNote;
	// Story being displayed
	private Story activeStory;
	
	
	public StoriesLayer() {
		stories = FXCollections.observableArrayList();
		
		storyGraphicMap = new HashMap<Story, StoryListCellGraphic>();
		
		width = 0;
		
		noteGraphics = FXCollections.observableArrayList();
		
		rebuildSceneFlag = new SimpleBooleanProperty(false);
		
		editController = new NoteEditorController();
		currentStory = editController.getCurrentStory();
		
		buildStories();
	}
	
	
	public Story getCurrentStory() {
		return currentStory;
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
	
	
	public void buildStories() {
		try {
			JarFile jarFile = new JarFile(new File("WormGUIDES.jar"));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				
				if (entry.getName().equals("wormguides/model/story_file/"+STORY_CONFIG_FILE_NAME)) {
					InputStream stream = jarFile.getInputStream(entry);
					processStream(stream);
				}
			}
			
			jarFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("The config file '" + STORY_CONFIG_FILE_NAME + "' wasn't found on the system.");
		} catch (IOException e) {
			System.out.println("The config file '" + STORY_CONFIG_FILE_NAME + "' wasn't found on the system.");
		}
	}
	
	
	public void processStream(InputStream stream) {
		int storyCounter = -1; //used for accessing the current story for adding scene elements

		try {
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);
			
			String line;
			
			// Skip heading line
			reader.readLine();
			
			while ((line = reader.readLine()) != null) {
				String[] split =  line.split(",", NUMBER_OF_CSV_FIELDS); //split the line up by commas
				
				int len = split.length;

				if (len!=NUMBER_OF_CSV_FIELDS) {
					System.out.println("Missing fields in CSV file.");
					continue;
				}
				
				if (isStory(split)) {
					Story story = new Story(split[STORY_NAME_INDEX], split[STORY_DESCRIPTION_INDEX]);
					stories.add(story);
					storyCounter++;
				}
				else {
					Note note = new Note(split[NAME_INDEX], split[CONTENTS_INDEX]);
					try {
						note.setTagDisplay(split[DISPLAY_INDEX]);
						note.setAttachmentType(split[TYPE_INDEX]);
						note.setLocation(split[LOCATION_INDEX]);
						note.setCellName(split[CELLNAME_INDEX]);
						
						note.setImagingSource(split[IMG_SOURCE_INDEX]);
						note.setResourceLocation(split[RESOURCE_LOCATION_INDEX]);
						
						note.setStartTime(split[START_TIME_INDEX]);
						note.setEndTime(split[END_TIME_INDEX]);
						
						note.setComments(split[COMMENTS_INDEX]);
						
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println(e.toString());
						System.out.println(line);
					} catch (TagDisplayEnumException e) {
						System.out.println(e.toString());
						System.out.println(line);
					} catch (AttachmentTypeEnumException e) {
						System.out.println(e.toString());
						System.out.println(line);
					} catch (LocationStringFormatException e) {
						System.out.println(e.toString());
						System.out.println(line);
					} catch (TimeStringFormatException e) {
						System.out.println(e.toString());
						System.out.println(line);
					} finally {
						Story story = stories.get(storyCounter);
						story.addNote(note);
						note.setParent(story);
					}
				}	
			}
			
			reader.close();
			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unable to process file '" + STORY_CONFIG_FILE_NAME + "'.");
		} catch (NumberFormatException e) {
			System.out.println("Number Format Error in file '" + STORY_CONFIG_FILE_NAME + "'.");
		} catch (IOException e) {
			System.out.println("The config file '" + STORY_CONFIG_FILE_NAME + "' wasn't found on the system.");
		}
	}
	
	
	public boolean isStory(String[] csvLine) {
		try {
			if (csvLine[DISPLAY_INDEX].isEmpty())
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return false;
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
					editStage = new Stage();
					
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("view/NoteEditorLayout.fxml"));
					
					loader.setController(editController);
					loader.setRoot(editController);
					
					try {
						editStage.setScene(new Scene((AnchorPane) loader.load()));
						
						editStage.setTitle("Note Editor");
						editStage.initModality(Modality.NONE);
						editStage.setResizable(true);
						
						editController.getStoryCreatedProperty().addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
								if (newValue) {
									Story newStory = editController.getCurrentStory();
									stories.add(newStory);
									editController.getStoryCreatedProperty().set(false);
								}
							}
						});
						
						editController.setCurrentNote(currentNote);
						
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
	
	
	// Un-highlight all notes except for the input note
	private void deselectAllNotesExcept(NoteListCellGraphic graphic) {
		for (NoteListCellGraphic g : noteGraphics)
			g.setSelected(false);
		
		graphic.setSelected(true);
	}
	
	
	public BooleanProperty getRebuildSceneFlag() {
		return rebuildSceneFlag;
	}
	
	
	// Make all stories inactive except for the input story
	private void disableAllStoriesExcept(Story story) {
		for (Story s : stories)
			s.setActive(false);
		
		story.setActive(true);
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
	                    	storyGraphicMap.put(story, storyGraphic);
	                    	
	                    	// Add list view for notes inside story graphic
	                    	for (Note note : story.getNotesObservable()) {
	                    		NoteListCellGraphic noteGraphic = new NoteListCellGraphic(note);
	                    		storyGraphic.getChildren().add(noteGraphic);
	                    		
	                    		// Add graphic to observable list for note selection
	                    		noteGraphics.add(noteGraphic);
	                    		noteGraphic.setOnMouseClicked(new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent event) {
										if (!(event.getPickResult().getIntersectedNode()
												==noteGraphic.getExpandIcon())) {
											if (noteGraphic.isSelected())
												noteGraphic.setSelected(false);
											else
												deselectAllNotesExcept(noteGraphic);
										}
									}
	                    		});
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
			title.setWrappingWidth(width-4);
			title.setFontSmoothingType(FontSmoothingType.LCD);

			description = new Text(story.getDescription());
			description.setFont(AppFont.getFont());
			description.setWrappingWidth(width-4);
			description.setFontSmoothingType(FontSmoothingType.LCD);
			
			container.getChildren().addAll(title, description);
			getChildren().addAll(container);
			
			container.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					story.setActive(!story.isActive());
					if (story.isActive()) {
						activeStory = story;
						disableAllStoriesExcept(story);
					}
					else
						activeStory = null;
					
					rebuildSceneFlag.set(true);
					rebuildSceneFlag.set(false);
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
		
		private Text expandIcon;
		private Text title;
		
		private HBox contentsContainer;
		private Text contents;
		
		private boolean expanded;
		private BooleanProperty selected;
		
		
		// note is the note to which this graphic belongs to
		public NoteListCellGraphic(Note note) {
			super();
			
			story = note.getParent();
			
			setPadding(new Insets(3));
			
			// title heading graphics
			HBox titleContainer = new HBox(0);
			titleContainer.setPrefWidth(width);
			titleContainer.setMaxWidth(width);
			titleContainer.setMinWidth(width);
			
			expandIcon = new Text("▸");
			expandIcon.setPickOnBounds(true);
			expandIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setExpanded(!isExpanded());
				}
			});
			expandIcon.setFont(AppFont.getBoldFont());
			expandIcon.setFontSmoothingType(FontSmoothingType.LCD);
			expandIcon.toFront();
			
			Region r1 = new Region();
			r1.setPrefWidth(5);
			r1.setMinWidth(USE_PREF_SIZE);
			r1.setMaxWidth(USE_PREF_SIZE);
			
			title = new Text(note.getTagName());
			title.setWrappingWidth(width-3-r1.prefWidth(-1)-expandIcon.prefWidth(-1));
			title.setFont(AppFont.getBoldFont());
			title.setFontSmoothingType(FontSmoothingType.LCD);
			
			titleContainer.getChildren().addAll(expandIcon, r1, title);
			titleContainer.setAlignment(Pos.CENTER_LEFT);
			
			// contents graphics
			contentsContainer = new HBox(0);
			
			Region r2 = new Region();
			r2.setPrefWidth(15);
			r2.setMinWidth(USE_PREF_SIZE);
			r2.setMaxWidth(USE_PREF_SIZE);
			
			contents = new Text(note.getTagContents());
			contents.setWrappingWidth(width-3-r2.prefWidth(-1));
			contents.setFont(AppFont.getFont());
			contents.setFontSmoothingType(FontSmoothingType.LCD);
			contentsContainer.getChildren().addAll(r2, contents);
			
			getChildren().add(titleContainer);
			
			setPickOnBounds(false);
			
			expanded = false;
			
			selected = new SimpleBooleanProperty(true);
			selected.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
					if (newValue) {
						highlightCell(true);
						currentNote = note;
						
						if (editController!=null)
							editController.updateFields();
					}
					else
						highlightCell(false);
				}
			});
			selected.set(false);
			
			story.getActiveProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
					if (!isSelected()) {
						if (observable.getValue())
							colorTexts(Color.BLACK, expandIcon, title, contents);
						else
							colorTexts(Color.GREY, expandIcon, title, contents);
					}
				}
			});
		}
		
		
		public Text getExpandIcon() {
			return expandIcon;
		}
		
		
		private void highlightCell(boolean highlight) {
			if (highlight) {
				setStyle("-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar; "
						+ "-fx-background: -fx-accent;");
				colorTexts(Color.WHITE, expandIcon, title, contents);
			}
			else {
				setStyle("-fx-background-color: white;");
				
				if (story.isActive())
					colorTexts(Color.BLACK, expandIcon, title, contents);
				else
					colorTexts(Color.GREY, expandIcon, title, contents);
			}
		}

		
		public void setSelected(boolean isSelected) {
			selected.set(isSelected);
		}
		
		
		public BooleanProperty getSelectedBooleanProperty() {
			return selected;
		}
		
		
		public boolean isSelected() {
			return selected.get();
		}
		
		
		// When the graphic is 'expanded' we can view the note contents
		// otherwise, we just see the note title
		public boolean isExpanded() {
			return expanded;
		}
		
		
		public void setExpanded(boolean expanded) {
			this.expanded = expanded;
			
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
	
	
	private final String STORY_CONFIG_FILE_NAME = "StoryListConfig.csv";
	private final int NUMBER_OF_CSV_FIELDS = 12;
	private final int STORY_NAME_INDEX = 0,
					STORY_DESCRIPTION_INDEX = 1;
	private final int NAME_INDEX = 0,
					CONTENTS_INDEX = 1,
					DISPLAY_INDEX = 2,
					TYPE_INDEX = 3,
					LOCATION_INDEX = 4,
					CELLNAME_INDEX = 5,
					MARKER_INDEX = 6,
					IMG_SOURCE_INDEX = 7,
					RESOURCE_LOCATION_INDEX = 8,
					START_TIME_INDEX = 9,
					END_TIME_INDEX = 10,
					COMMENTS_INDEX = 11;
	
}
