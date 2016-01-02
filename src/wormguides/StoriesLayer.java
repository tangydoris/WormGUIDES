package wormguides;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import wormguides.model.Story;

/*
 * Controller of the ListView in the 'Stories' tab
 */
public class StoriesLayer {

	private ObservableList<Story> stories;
	private double width;
	
	
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
