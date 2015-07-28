package wormguides.model;

import wormguides.ImageLoader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class ColorRuleCell extends ListCell<ColorRule>{
	
	private HBox hbox = new HBox();
	private Label label = new Label("empty");
	private Region region = new Region();
	private Button colorBtn = new Button();
	private Button editBtn = new Button();
	private Button visibleBtn = new Button();
	private Button deleteBtn = new Button("X");
	private ColorRule rule;
	
	public ColorRuleCell() {
		super();
		
		rule = getItem();
		if (rule==null)
			System.out.println("rule is null");
		
		// format UI elements
		DoubleProperty height = new SimpleDoubleProperty(HEIGHT);
		
		hbox.setSpacing(2);	
		label.setFont(new Font(14));
		label.setText(toString());
		label.prefHeightProperty().bind(height);
		
		colorBtn.prefHeightProperty().bind(height);
		colorBtn.prefWidthProperty().bind(height);
		colorBtn.setGraphic(new Rectangle(HEIGHT, HEIGHT, rule.getColor()));
		colorBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("color button pressed");
			}
		});
		
		editBtn.prefHeightProperty().bind(height);
		editBtn.prefWidthProperty().bind(height);
		editBtn.setGraphic(ImageLoader.getEditIcon());
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("edit button pressed");
			}
		});
		
		visibleBtn.prefHeightProperty().bind(height);
		visibleBtn.prefWidthProperty().bind(height);
		visibleBtn.setGraphic(ImageLoader.getEyeIcon());
		visibleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("visible button pressed");
			}
		});
		
		deleteBtn.prefHeightProperty().bind(height);
		deleteBtn.prefWidthProperty().bind(height);
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("delete button pressed");
			}
		});
		
		HBox.setHgrow(region, Priority.ALWAYS);
		hbox.getChildren().addAll(label, region, colorBtn, editBtn, visibleBtn, deleteBtn);
		hbox.setPrefHeight(22);
	}
	
	@Override
	protected void updateItem(ColorRule item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		if (empty) {
			rule = null;
			setGraphic(null);
		}
		else {
			rule = item;
			label.setText(rule.toString());
			colorBtn.setGraphic(new Rectangle(HEIGHT, HEIGHT, rule.getColor()));
			setGraphic(hbox);
		}
	}
	
	private static final int HEIGHT = 22;
}
