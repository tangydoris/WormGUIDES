package wormguides.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * This class is a popup dialog {@link AnchorPane} that contains some prompt
 * text and two buttons, yes and no. The Strings for these three components are
 * set upon initialization.<br>
 * <br>
 * Initialized from {@link RootLayoutController} before application exit to save
 * the active story.
 * 
 * @author Doris Tang
 */
public class YesNoDialogPane extends AnchorPane {

	private Button yesBtn;
	private Button noBtn;

	private Text promptText;

	public YesNoDialogPane(String prompt, String yesButtonText, String noButonText) {
		super();

		VBox mainVBox = new VBox(10);
		AnchorPane.setTopAnchor(mainVBox, 10.0);
		AnchorPane.setLeftAnchor(mainVBox, 10.0);
		AnchorPane.setRightAnchor(mainVBox, 10.0);
		AnchorPane.setBottomAnchor(mainVBox, 10.0);

		// initialize prompt text
		promptText = new Text();
		promptText.setFont(AppFont.getFont());
		promptText.wrappingWidthProperty().bind(mainVBox.widthProperty().subtract(10));
		promptText.setTextAlignment(TextAlignment.CENTER);
		promptText.setText(prompt);

		mainVBox.getChildren().add(promptText);

		// initialize buttons
		yesBtn = new Button();
		yesBtn.setText(yesButtonText);
		yesBtn.setFont(AppFont.getFont());
		yesBtn.setPrefWidth(70);
		yesBtn.setMaxHeight(Integer.MAX_VALUE);

		noBtn = new Button();
		noBtn.setText(noButonText);
		noBtn.setFont(AppFont.getFont());
		noBtn.setPrefWidth(70);
		noBtn.setMaxHeight(Integer.MAX_VALUE);
		
		Region r1 = new Region();
		sizeRegion(r1);
		Region r2 = new Region();
		sizeRegion(r2);
		Region r3 = new Region();
		sizeRegion(r3);
		
		HBox btnHBox = new HBox(10);
		btnHBox.getChildren().addAll(r1, yesBtn, r2, noBtn, r3);
		
		mainVBox.getChildren().add(btnHBox);

		getChildren().add(mainVBox);
	}
	
	private void sizeRegion(Region r) {
		HBox.setHgrow(r, Priority.ALWAYS);
	}

	public void setYesButtonAction(EventHandler<ActionEvent> handler) {
		yesBtn.setOnAction(handler);
	}

	public void setNoButtonAction(EventHandler<ActionEvent> handler) {
		noBtn.setOnAction(handler);
	}
}
