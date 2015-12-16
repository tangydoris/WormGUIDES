package wormguides;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import wormguides.view.AppFont;


/*
 * Callback for ListCell for String so that fonts are uniform
 */
public class StringCellCallback implements Callback<ListView<String>, ListCell<String>> {
	
	@Override
	public ListCell<String> call(ListView<String> param) {
		ListCell<String> cell = new ListCell<String>() {
			@Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (name != null)
                	setGraphic(makeStructuresListCellGraphic(name));
            	else
            		setGraphic(null);
        	}
		};
		return cell;
	}
	
	
	// Creates the graphic for a ListCell in structures ListView's
	private HBox makeStructuresListCellGraphic(String name) {
		HBox hbox = new HBox();
    	Label label = new Label(name);
    	label.setFont(AppFont.getFont());
    	label.setPrefHeight(22);
    	hbox.getChildren().add(label);
    	return hbox;
	}
}
