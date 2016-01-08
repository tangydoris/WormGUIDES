package wormguides.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;

public class StructureListCellGraphic extends HBox{
	
	private Label label;
	private BooleanProperty selected;
	
	public StructureListCellGraphic(String name) {
		super();
		label = new Label(name);
    	label.setFont(AppFont.getFont());
    	
    	label.setPrefHeight(UI_HEIGHT);
    	label.setMinHeight(UI_HEIGHT);
    	label.setMaxHeight(UI_HEIGHT);
    	
    	getChildren().add(label);
    	
    	setMaxWidth(Double.MAX_VALUE);
    	setPadding(new Insets(5, 5, 5, 5));
    	
    	setPickOnBounds(false);
    	
    	setBackground(Background.EMPTY);
    	
    	selected = new SimpleBooleanProperty(false);
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
	}
	
	
	public boolean isSelected() {
		return selected.get();
	}
	
	
	public void deselect() {
		selected.set(false);
	}
	
	
	public void select() {
		selected.set(true);
	}
	
	
	private void highlightCell(boolean highlight) {
		if (highlight) {
			setStyle("-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar; "
					+ "-fx-background-insets: 0, 1, 2; "
					+ "-fx-background: -fx-accent;"
					+ "-fx-text-fill: -fx-selection-bar-text;");
		}
		else
			setStyle("-fx-background-color: white;");
	}
	
	
	private final double UI_HEIGHT = 28.0;
}
