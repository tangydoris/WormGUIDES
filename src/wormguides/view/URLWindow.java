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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import wormguides.URLGenerator;
import wormguides.controllers.Window3DController;
import wormguides.loaders.ImageLoader;
import wormguides.models.Rule;

public class URLWindow extends AnchorPane {

	private Window3DController scene;

	private TextField urlField;

	private String urlString;

	private Button resetBtn;
	private Button closeBtn;

	private Clipboard cb;

	public URLWindow() {
		super();
		setPrefWidth(430);

		cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		Tooltip tooltip = new Tooltip("copy");

		VBox vBox = new VBox();
		vBox.setSpacing(10);
		AnchorPane.setTopAnchor(vBox, 10.0);
		AnchorPane.setLeftAnchor(vBox, 10.0);
		AnchorPane.setRightAnchor(vBox, 10.0);
		AnchorPane.setBottomAnchor(vBox, 10.0);
		getChildren().add(vBox);

		HBox androidHBox = new HBox(10);
		urlField = new TextField();
		urlField.setFont(AppFont.getFont());
		urlField.setPrefHeight(28);
		urlField.setEditable(false);
		urlField.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
		HBox.setHgrow(urlField, Priority.ALWAYS);
		Button androidCopyBtn = new Button();
		androidCopyBtn.setPrefSize(28, 28);
		androidCopyBtn.setMinSize(28, 28);
		androidCopyBtn.setMaxSize(28, 28);
		androidCopyBtn.setTooltip(tooltip);
		androidCopyBtn.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
		androidCopyBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		androidCopyBtn.setGraphic(ImageLoader.getCopyIcon());
		androidCopyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				StringSelection ss = new StringSelection(urlField.getText());
				cb.setContents(ss, null);
			}
		});
		androidHBox.getChildren().addAll(urlField, androidCopyBtn);

		resetBtn = new Button("Generate");
		resetBtn.setPrefWidth(100);
		resetBtn.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
		resetBtn.setFont(AppFont.getFont());
		resetBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resetURLs();
			}
		});

		closeBtn = new Button("Close");
		closeBtn.setPrefWidth(100);
		closeBtn.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
		closeBtn.setFont(AppFont.getFont());
		HBox hBox = new HBox();
		hBox.setSpacing(20);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(resetBtn, closeBtn);

		vBox.getChildren().addAll(androidHBox, hBox);
	}

	public void setScene(Window3DController window3D) {
		this.scene = window3D;
	}

	public void resetURLs() {
		if (scene != null) {
			ArrayList<Rule> list = scene.getColorRulesList();
			urlString = URLGenerator.generateAndroid(list, scene.getTime(), scene.getRotationX(), scene.getRotationY(),
					scene.getRotationZ(), scene.getTranslationX(), scene.getTranslationY(), scene.getScale(),
					scene.getOthersVisibility());

			urlField.setText(urlString);
		}
	}

	public Button getCloseButton() {
		return closeBtn;
	}
}
