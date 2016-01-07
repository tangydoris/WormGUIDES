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
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
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
	//private NoteEditorController editController;
	
	
	public StoriesLayer(ObservableList<Story> list) {
		if (list!=null)
			stories = list;
		else
			stories = FXCollections.observableArrayList();
		
		width = 0;
		
		//noteCellFactory = new NoteCellFactory();
		
		noteGraphics = FXCollections.observableArrayList(new Callback<NoteGraphic, Observable[]>() {
			@Override
			public Observable[] call(NoteGraphic graphic) {
				return new Observable[]{graphic.getSelectedBooleanProperty()};
			}
		});
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
					
					try {
						FXMLLoader loader = new FXMLLoader();
						loader.setLocation(getClass().getResource("view/NoteEditorLayout.fxml"));
						
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
	private VBox makeStoryGraphic(Story story, double width) {
		VBox box = new VBox(0);
		box.setPrefWidth(width);
		box.setMaxWidth(width);
		box.setMinWidth(width);
		
		Label title = new Label(story.getName());
		title.setFont(AppFont.getBolderFont());
		title.setWrapText(true);
		title.setStyle("-fx-text-fill: black");
		
		Text desription = new Text(story.getDescription());
		desription.setFont(AppFont.getFont());
		desription.wrappingWidthProperty().bind(box.widthProperty());
		
		box.getChildren().addAll(title, desription);
		return box;
	}
	
	
	/*
	 * Un-highlight all notes except for the input note
	 */
	private void deselectAllExcept(NoteGraphic graphic) {
		for (NoteGraphic g : noteGraphics) {
			if (g!=graphic)
				graphic.select();
			else
				graphic.deselect();
		}
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
	 * Highlight input note graphic
	 */
	private void selectNoteGraphic(NoteGraphic graphic) {
		for (NoteGraphic g : noteGraphics) {
			if (g==graphic)
				g.select();
		}
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
	                    	VBox box = makeStoryGraphic(story, width);
	                    	
	                    	Region r1 = new Region();
	                    	r1.setPrefHeight(10);
	                    	r1.setMinHeight(USE_PREF_SIZE);
	                    	r1.setMaxHeight(USE_PREF_SIZE);
	                    	box.getChildren().add(r1);
	                    	
	                    	// Add list view for notes inside story graphic
	                    	for (Note note : story.getNotesObservable()) {
	                    		NoteGraphic graphic = note.getGraphic(width);
	                    		box.getChildren().add(graphic);
	                    		
	                    		// Add graphic to observable list for note selection
	                    		noteGraphics.add(graphic);
	                    		graphic.setOnMouseClicked(new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent event) {
										graphic.setExpanded(!graphic.isExpanded());
										deselectAllNoteGraphics();
										selectNoteGraphic(graphic);
									}
	                    		});
	                    	}
	                    	
	                    	Region r2 = new Region();
	                    	r2.setPrefHeight(10);
	                    	r2.setMinHeight(USE_PREF_SIZE);
	                    	r2.setMaxHeight(USE_PREF_SIZE);
	                    	
	                    	box.getChildren().add(r2);
	                    	box.getChildren().add(new Separator(Orientation.HORIZONTAL));
	                    	
	                    	setGraphic(box);
	                    }
	                	else
	                		setGraphic(null);
	                    setStyle("-fx-focus-color: transparent;"
	                    		+ "-fx-background-color: transparent;");
	            	}
	        	};
	        	return cell;
			}
		};
	}
	
}
