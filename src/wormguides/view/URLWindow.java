package wormguides.view;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import wormguides.URLGenerator;
import wormguides.model.ColorRule;

public class URLWindow extends AnchorPane {
	
	Window3DSubScene scene;
	
	private Label iOSLabel;
	private Label androidLabel;
	private Label webLabel;
	
	private TextField iOSField;
	private TextField androidField;
	private TextField webField;
	
	private String iOSURL;
	private String androidURL;
	private String webURL;
	
	private Font font;
	private Button resetBtn;
	
	public URLWindow() {
		super();
		setPrefHeight(200);
		setPrefWidth(400);
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		AnchorPane.setTopAnchor(vBox, 10.0);
		AnchorPane.setLeftAnchor(vBox, 10.0);
		AnchorPane.setRightAnchor(vBox, 10.0);
		AnchorPane.setBottomAnchor(vBox, 10.0);
		getChildren().add(vBox);
		
		font = new Font(14);
		
		iOSLabel = new Label("iOS:");
		iOSLabel.setFont(font);
		iOSLabel.setPrefHeight(22);
		androidLabel = new Label("Android:");
		androidLabel.setFont(font);
		androidLabel.setPrefHeight(22);
		webLabel = new Label("Web browser:");
		webLabel.setFont(font);
		webLabel.setPrefHeight(22);
		
		iOSField = new TextField();
		iOSField.setFont(font);
		iOSField.setEditable(false);
		iOSField.setPrefHeight(22);
		iOSField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		androidField = new TextField();
		androidField.setFont(font);
		androidField.setEditable(false);
		androidField.setPrefHeight(22);
		androidField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		webField = new TextField();
		webField.setFont(font);
		webField.setEditable(false);
		webField.setPrefHeight(22);
		webField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		
		resetBtn = new Button("Reset");
		resetBtn.setFont(font);
		resetBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resetURLs();
			}
		});
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().add(resetBtn);
		
		Region r = new Region();
		r.setPrefHeight(5);
		
		vBox.getChildren().addAll(iOSLabel, iOSField, androidLabel, 
						androidField, webLabel, webField, r, hBox);
	}
	
	
	public void setScene(Window3DSubScene window3D) {
		this.scene = window3D;
		resetURLs();
	}
	
	public void resetURLs() {
		if (scene != null) {
			ArrayList<ColorRule> list = scene.getRulesList();
			iOSURL = URLGenerator.generateIOSURL(list, scene.getTime(), 
							scene.getRotationX(), scene.getRotationY(), scene.getRotationZ(), 
							scene.getTranslationX(), scene.getTranslationY(), 
							scene.getScale(), scene.getOthersVisibility());
			androidURL = URLGenerator.generateAndroidURL(list, scene.getTime(), 
							scene.getRotationX(), scene.getRotationY(), scene.getRotationZ(), 
							scene.getTranslationX(), scene.getTranslationY(), 
							scene.getScale(), scene.getOthersVisibility());
			webURL = URLGenerator.generateWebURL(list, scene.getTime(), 
							scene.getRotationX(), scene.getRotationY(), scene.getRotationZ(), 
							scene.getTranslationX(), scene.getTranslationY(), 
							scene.getScale(), scene.getOthersVisibility());
			
			iOSField.setText(iOSURL);
			androidField.setText(androidURL);
			webField.setText(webURL);
		}
	}
}
