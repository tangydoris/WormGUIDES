package wormguides.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class URLLoadWindow extends AnchorPane {
	
	private Label label;
	private TextField field;
	private Button submitBtn;
	private Font font;
	
	public URLLoadWindow() {
		super();
		setPrefHeight(10);
		setPrefWidth(400);
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		AnchorPane.setTopAnchor(vBox, 10.0);
		AnchorPane.setLeftAnchor(vBox, 10.0);
		AnchorPane.setRightAnchor(vBox, 10.0);
		AnchorPane.setBottomAnchor(vBox, 10.0);
		getChildren().add(vBox);
		
		font = new Font(14);
		label = new Label("Paste URL here:");
		label.setFont(font);
		field = new TextField();
		field.setFont(font);
		submitBtn = new Button("Submit");
		
		HBox hBox = new HBox();
		
	}
	
	public Button getSubmitButton() {
		return submitBtn;
	}
}
