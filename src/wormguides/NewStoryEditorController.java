package wormguides;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


/*
 * Controller for NewStoryEditorLayout.fxml in the view package
 * Responsible for creation of a new story
 */
public class NewStoryEditorController extends AnchorPane implements Initializable {
	
	@FXML private TextField title;
	@FXML private TextArea description;
	@FXML private Button submit;
	@FXML private Button cancel;

	
	public NewStoryEditorController() {
		super();
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
	}
	
	
	private void assertFXMLNodes() {
		assert (title!=null);
		assert (description!=null);
		assert (submit!=null);
		assert (cancel!=null);
	}
	
	
	public String getTitle() {
		return title.getText();
	}
	
	
	public String getDescription() {
		return description.getText();
	}
	
	
	public void addSubmitButtonListener(EventHandler<ActionEvent> handler) {
		submit.setOnAction(handler);
	}
	
	
	public void addCancelButtonListener(EventHandler<ActionEvent> handler) {
		cancel.setOnAction(handler);
	}
	
}
