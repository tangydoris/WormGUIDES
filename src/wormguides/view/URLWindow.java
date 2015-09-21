package wormguides.view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import wormguides.ImageLoader;
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
	
	private Clipboard cb;
	
	public URLWindow() {
		super();
		setPrefWidth(430);
		
		cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		Tooltip tooltip = new Tooltip("copy");
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		AnchorPane.setTopAnchor(vBox, 15.0);
		AnchorPane.setLeftAnchor(vBox, 15.0);
		AnchorPane.setRightAnchor(vBox, 15.0);
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
		
		HBox iOSHBox = new HBox(10);
		iOSField = new TextField();
		iOSField.setFont(AppFont.getFont());
		iOSField.setEditable(false);
		iOSField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		HBox.setHgrow(iOSField, Priority.ALWAYS);
		Button iOSCopyBtn = new Button();
		iOSCopyBtn.setTooltip(tooltip);
		iOSCopyBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
							"-fx-faint-focus-color: transparent;");
		iOSCopyBtn.maxWidthProperty().bind(iOSCopyBtn.heightProperty());
		iOSCopyBtn.prefWidthProperty().bind(iOSCopyBtn.heightProperty());
		iOSCopyBtn.minWidthProperty().bind(iOSCopyBtn.heightProperty());
		iOSCopyBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		iOSCopyBtn.setGraphic(ImageLoader.getCopyIcon());
		iOSCopyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				StringSelection ss = new StringSelection(iOSField.getText());
				cb.setContents(ss, null);
			}
		});
		iOSHBox.getChildren().addAll(iOSField, iOSCopyBtn);
		
		HBox androidHBox = new HBox(10);
		androidField = new TextField();
		androidField.setFont(AppFont.getFont());
		androidField.setEditable(false);
		androidField.setPrefHeight(22);
		androidField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		HBox.setHgrow(androidField, Priority.ALWAYS);
		Button androidCopyBtn = new Button();
		androidCopyBtn.setTooltip(tooltip);
		androidCopyBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
								"-fx-faint-focus-color: transparent;");
		androidCopyBtn.maxWidthProperty().bind(iOSCopyBtn.heightProperty());
		androidCopyBtn.prefWidthProperty().bind(iOSCopyBtn.heightProperty());
		androidCopyBtn.minWidthProperty().bind(iOSCopyBtn.heightProperty());
		androidCopyBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		androidCopyBtn.setGraphic(ImageLoader.getCopyIcon());
		androidCopyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				StringSelection ss = new StringSelection(androidField.getText());
				cb.setContents(ss, null);
			}
		});
		androidHBox.getChildren().addAll(androidField, androidCopyBtn);
		
		HBox webHBox = new HBox(10);
		webField = new TextField();
		webField.setFont(AppFont.getFont());
		webField.setEditable(false);
		webField.setPrefHeight(22);
		webField.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;"); 
		HBox.setHgrow(webField, Priority.ALWAYS);
		Button webCopyBtn = new Button();
		webCopyBtn.setTooltip(tooltip);
		webCopyBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
							"-fx-faint-focus-color: transparent;");
		webCopyBtn.maxWidthProperty().bind(webCopyBtn.heightProperty());
		webCopyBtn.prefWidthProperty().bind(webCopyBtn.heightProperty());
		webCopyBtn.minWidthProperty().bind(webCopyBtn.heightProperty());
		webCopyBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		webCopyBtn.setGraphic(ImageLoader.getCopyIcon());
		webCopyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				StringSelection ss = new StringSelection(webField.getText());
				cb.setContents(ss, null);
			}
		});
		webHBox.getChildren().addAll(webField, webCopyBtn);
		
		resetBtn = new Button("Regenerate");
		resetBtn.setPrefWidth(100);
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
		closeBtn.setPrefWidth(100);
		closeBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		closeBtn.setFont(AppFont.getFont());
		HBox hBox = new HBox();
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(resetBtn, closeBtn);
		
		Region r = new Region();
		r.setPrefHeight(10);
		
		vBox.getChildren().addAll(iOSLabel, iOSHBox, androidLabel, androidHBox, 
													webLabel, webHBox, r, hBox);
	}
	
	
	public void setScene(Window3DSubScene window3D) {
		this.scene = window3D;
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
