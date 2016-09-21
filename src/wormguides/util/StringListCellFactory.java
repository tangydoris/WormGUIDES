/*
 * Bao Lab 2016
 */

package wormguides.util;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import wormguides.view.AppFont;

/*
 * Callback for ListCell<String> so that fonts are uniform
 */
public class StringListCellFactory implements Callback<ListView<String>, ListCell<String>> {

	private final double UI_HEIGHT = 26.0;

	@Override
	public ListCell<String> call(ListView<String> param) {
		return new StringListCell();
	}

	public ListCell<String> getNewStringListCell() {
		return new StringListCell();
	}

	// Creates the graphic for a ListCell in structures ListView's
	private HBox makeListCellGraphic(String name) {
		HBox hbox = new HBox();
		Label label = new Label(name);
		label.setFont(AppFont.getFont());
		label.setPrefHeight(UI_HEIGHT);
		label.setMinHeight(UI_HEIGHT);
		label.setStyle("-fx-fill-color: black;");
		label.setTextFill(Color.BLACK);

		hbox.getChildren().add(label);
		return hbox;
	}

	public class StringListCell extends ListCell<String> {
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setFocusTraversable(false);

			if (item != null)
				setGraphic(makeListCellGraphic(item));
			else
				setGraphic(null);
		}
	}
}
