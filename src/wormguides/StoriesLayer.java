package wormguides;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import wormguides.model.Story;

/*
 * Controller of the ListView in the 'Stories' tab
 */
public class StoriesLayer {

	private ObservableList<Story> stories;
	private double width;
	
	private Stage editStage;
	private NoteEditorController editController;
	
	
	public StoriesLayer(ObservableList<Story> list) {
		if (list!=null)
			stories = list;
		else
			stories = FXCollections.observableArrayList();
		
		width = 0;
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
				width = observable.getValue().doubleValue() - 20;
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
	 * Renderer for Story items
	 */
	public Callback<ListView<Story>, ListCell<Story>> getStoryCellFactory() {
		return new Callback<ListView<Story>, ListCell<Story>>() {
			@Override
			public ListCell<Story> call(ListView<Story> param) {
				ListCell<Story> cell = new ListCell<Story>(){
	                @Override
	                protected void updateItem(Story story, boolean empty) {
	                    super.updateItem(story, empty);
	                    if (story != null)  {
	                    	VBox graphic = story.getGraphic();
	                    	graphic.setPrefWidth(width);
	                    	graphic.setMinWidth(width);
	                    	graphic.setMaxWidth(width);
	                    	
	                    	setGraphic(graphic);
	                    }
	                	else
	                		setGraphic(null);
	            	}
	        	};
	        	return cell;
			}
		};
	}
	
	
}
