package wormguides;

import java.io.IOException;
import java.util.HashMap;

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
	
	
	public StoriesLayer(ObservableList<Story> list) {
		if (list!=null)
			stories = list;
		else
			stories = FXCollections.observableArrayList();
		
		storyGraphicMap = new HashMap<Story, StoryListCellGraphic>();
		
		width = 0;
		
		noteGraphics = FXCollections.observableArrayList();
		
		rebuildSceneFlag = new SimpleBooleanProperty(false);
		
		editController = new NoteEditorController();
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
	                    
	                    if (story!=null && !empty)  {
	                    	
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
	                    	s.setStyle("-fx-focus-color: transparent; "
	            						+ "-fx-faint-focus-color: transparent;");
	                    	storyGraphic.getChildren().add(s);
	                    	
	                    	setGraphic(storyGraphic);
	                    }
	                	else
	                		setGraphic(null);
	                    
	                    setStyle("-fx-focus-color: transparent;"
	                    		+ "-fx-background-color: transparent;");
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
			setMaxWidth(USE_PREF_SIZE);
			setMinWidth(USE_PREF_SIZE);
			
			VBox container = new VBox(3);
			title = new Text(story.getName());
			title.setFont(AppFont.getBolderFont());
			title.wrappingWidthProperty().bind(widthProperty().subtract(5));
			title.setFontSmoothingType(FontSmoothingType.LCD);
			
			container.getChildren().add(title);
			container.setPickOnBounds(false);
			container.setPadding(new Insets(5));

			description = new Text(story.getDescription());
			description.setFont(AppFont.getFont());
			description.wrappingWidthProperty().bind(widthProperty().subtract(5));
			description.setFontSmoothingType(FontSmoothingType.LCD);
			
			container.getChildren().add(description);
			
			getChildren().addAll(container);
			
			container.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					story.setActive(!story.isActive());
					if (story.isActive())
						disableAllStoriesExcept(story);
					
					rebuildSceneFlag.set(!rebuildSceneFlag.get());
				}
			});
			
			story.getActiveProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
						if (observable.getValue())
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
			
			expandIcon = new Text("▸");
			expandIcon.setPickOnBounds(true);
			expandIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setExpanded(!isExpanded());
				}
			});
			expandIcon.setFont(AppFont.getExpandIconFont());
			expandIcon.setFontSmoothingType(FontSmoothingType.LCD);
			expandIcon.toFront();
			
			Region r1 = new Region();
			r1.setPrefWidth(5);
			r1.setMinWidth(USE_PREF_SIZE);
			r1.setMaxWidth(USE_PREF_SIZE);
			
			title = new Text(note.getTagName());
			title.wrappingWidthProperty().bind(widthProperty().subtract(5));
			title.setFont(AppFont.getBoldFont());
			title.setFontSmoothingType(FontSmoothingType.LCD);
			
			titleContainer.getChildren().addAll(expandIcon, r1, title);
			
			// contents graphics
			contentsContainer = new HBox(0);
			
			Region r2 = new Region();
			r2.setPrefWidth(15);
			r2.setMinWidth(USE_PREF_SIZE);
			r2.setMaxWidth(USE_PREF_SIZE);
			
			contents = new Text(note.getTagContents());
			contents.wrappingWidthProperty().bind(widthProperty().subtract(5));
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
					if (newValue)
						highlightCell(true);
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
	
}
