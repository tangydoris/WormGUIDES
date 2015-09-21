package wormguides.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class URLLoadWindow extends AnchorPane {
	
	private Label label;
	private TextField field;
	private Button loadBtn;
	private Button cancelBtn;
	
	public URLLoadWindow() {
		super();
		setPrefWidth(430);
		
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		AnchorPane.setTopAnchor(vBox, 10.0);
		AnchorPane.setLeftAnchor(vBox, 10.0);
		AnchorPane.setRightAnchor(vBox, 10.0);
		AnchorPane.setBottomAnchor(vBox, 10.0);
		
		label = new Label("Paste URL here:");
		label.setFont(AppFont.getFont());
		field = new TextField();
		field.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		field.setFont(AppFont.getFont());
		
		loadBtn = new Button("Load");
		loadBtn.setPrefWidth(70);
		loadBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		cancelBtn = new Button("Cancel");
		cancelBtn.setPrefWidth(70);
		cancelBtn.setStyle("-fx-focus-color: -fx-outer-border; "+
						"-fx-faint-focus-color: transparent;");
		
		HBox hBox = new HBox();
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(loadBtn, cancelBtn);
		
		vBox.getChildren().addAll(label, field, hBox);
		getChildren().add(vBox);
	}
	
	public String getInputURL() {
		return field.getText();
	}
	
	public Button getLoadButton() {
		return loadBtn;
	}
	
	public Button getCancelButton() {
		return cancelBtn;
	}
}
