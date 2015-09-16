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
	
	private Button resetBtn;
	private Button closeBtn;
	
	public URLWindow() {
		super();
		setPrefWidth(430);
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		AnchorPane.setTopAnchor(vBox, 10.0);
		AnchorPane.setLeftAnchor(vBox, 10.0);
		AnchorPane.setRightAnchor(vBox, 10.0);
		AnchorPane.setBottomAnchor(vBox, 10.0);
		getChildren().add(vBox);
		
		iOSLabel = new Label("iOS:");
		iOSLabel.setFont(AppFont.getFont());
		iOSLabel.setPrefHeight(22);
		androidLabel = new Label("Android:");
		androidLabel.setFont(AppFont.getFont());
		androidLabel.setPrefHeight(22);
		webLabel = new Label("Web browser:");
		webLabel.setFont(AppFont.getFont());
		webLabel.setPrefHeight(22);
		
		iOSField = new TextField();
		iOSField.setFont(AppFont.getFont());
		iOSField.setEditable(false);
		iOSField.setPrefHeight(22);
		iOSField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		androidField = new TextField();
		androidField.setFont(AppFont.getFont());
		androidField.setEditable(false);
		androidField.setPrefHeight(22);
		androidField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		webField = new TextField();
		webField.setFont(AppFont.getFont());
		webField.setEditable(false);
		webField.setPrefHeight(22);
		webField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		
		resetBtn = new Button("Reset");
		resetBtn.setPrefWidth(70);
		resetBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		resetBtn.setFont(AppFont.getFont());
		resetBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resetURLs();
			}
		});
		
		closeBtn = new Button("Close");
		closeBtn.setPrefWidth(70);
		closeBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		closeBtn.setFont(AppFont.getFont());
		HBox hBox = new HBox();
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(resetBtn, closeBtn);
		
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
	
	public Button getCloseButton() {
		return closeBtn;
	}
}
