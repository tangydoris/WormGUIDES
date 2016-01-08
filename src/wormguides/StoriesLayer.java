package wormguides;

import java.io.IOException;

import javafx.beans.Observable;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import wormguides.model.Note;
import wormguides.model.Story;
import wormguides.view.AppFont;
import wormguides.view.NoteGraphic;

/*
 * Controller of the ListView in the 'Stories' tab
 */
public class StoriesLayer {

	private ObservableList<Story> stories;
	private double width;
	
	private Stage editStage;
	private ObservableList<NoteGraphic> noteGraphics;
	
	private NoteEditorController editController;
	
	
	public StoriesLayer(ObservableList<Story> list) {
		if (list!=null)
			stories = list;
		else
			stories = FXCollections.observableArrayList();
		
		width = 0;
		
		noteGraphics = FXCollections.observableArrayList(new Callback<NoteGraphic, Observable[]>() {
			@Override
			public Observable[] call(NoteGraphic graphic) {
				return new Observable[]{graphic.getSelectedBooleanProperty()};
			}
		});
		
		editController = new NoteEditorController();
	}
	
	
	public ObservableList<Story> getStories() {
		return stories;
	}
	
	
	/*
	 * Used for sizing the widths each story item in the list view
	 */
	public ChangeListener<Number> getListViewWidthListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
								Number oldValue, Number newValue) {
				width = observable.getValue().doubleValue()-20;
			}
		};
	}
	
	
	/*
	 * Listener for edit button under stories list view
	 */
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
	
	
	/*
	 * make vbox graphic to show a story
	 */
	private StoryListCellGraphic makeStoryGraphic(Story story, double width) {
		return new StoryListCellGraphic(story, width);
	}
	
	
	/*
	 * Un-highlight all notes except for the input note
	 */
	private void deselectAllExcept(NoteGraphic graphic) {
		for (NoteGraphic g : noteGraphics)
			g.deselect();
		graphic.select();
	}
	
	
	/*
	 * Un-highlights all note graphics
	 */
	private void deselectAllNoteGraphics() {
		for (NoteGraphic graphic : noteGraphics) {
			graphic.deselect();
		}
	}
	
	
	/*
	 * Grey out (disable) child nodes of input parent box
	 */
	private void disableContents(VBox box, boolean disable) {
		//System.out.println("disable is "+disable);
		for (Node node : box.getChildren())
			node.setDisable(disable);
	}
	
	
	/*
	 * Renderer for story item
	 */
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
	                    	StoryListCellGraphic box = makeStoryGraphic(story, width);
	                    	
	                    	// Add list view for notes inside story graphic
	                    	for (Note note : story.getNotesObservable()) {
	                    		NoteGraphic graphic = note.getGraphic(width);
	                    		box.getChildren().add(graphic);
	                    		
	                    		// Add graphic to observable list for note selection
	                    		// TODO fix text wrapping in note graphic
	                    		noteGraphics.add(graphic);
	                    		graphic.setOnMouseClicked(new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent event) {
										graphic.setExpanded(!graphic.isExpanded());
										if (graphic.isSelected())
											graphic.deselect();
										else
											deselectAllExcept(graphic);
									}
	                    		});
	                    	}
	                    	
	                    	Separator s = new Separator(Orientation.HORIZONTAL);
	                    	box.getChildren().add(s);
	                    	
	                    	// TODO disable (grey out) story
	                    	// Grey out story cell when
	                    	story.getIsActiveBooleanProperty().addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, 
													Boolean oldValue, Boolean newValue) {
									// corrective activeness is being set here, just need to grey out children nodes
									if (newValue) {
										box.enable();
									}
									else {
										box.disable();
									}
								}
	                    	});
	                    	
	                    	setGraphic(box);
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
	
	
	private class StoryListCellGraphic extends VBox {
		
		private Label title;
		private Text description;
		
		public StoryListCellGraphic(Story story, double width) {
			super(0);
			setPadding(new Insets(10));
			setPrefWidth(width);
			setMaxWidth(width);
			setMinWidth(width);
			
			title = new Label(story.getName());
			title.setFont(AppFont.getBolderFont());
			title.setWrapText(true);
			title.setStyle("-fx-text-fill: black");
			
			description = new Text(story.getDescription());
			description.setFont(AppFont.getFont());
			description.wrappingWidthProperty().bind(widthProperty());
			
			getChildren().addAll(title, description);
		}
		
		public void disable() {
			title.setDisable(true);
			title.setStyle("-fx-fill-color: grey");
			description.setDisable(true);
			description.setStyle("-fx-fill-color: grey");
		}
		
		public void enable() {
			title.setDisable(false);
			title.setStyle("-fx-fill-color: black");
			description.setDisable(false);
			description.setStyle("-fx-fill-color: black");
		}
	}
	
}
