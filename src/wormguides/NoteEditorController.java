package wormguides;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class NoteEditorController implements Initializable{
	
	/*
	private AnchorPane parentPane;
	private Scene scene;
	private Stage stage;
	*/
	
	@FXML private TextField titleField;
	@FXML private TextArea contentArea;
	@FXML private ComboBox<String> structuresComboBox;
	@FXML private CheckBox calloutTick;
	@FXML private CheckBox timeTick;
	@FXML private TextField startTimeField;
	@FXML private TextField endTimeField;
	
	@FXML private ToggleGroup attachmentToggle;
	@FXML private ToggleGroup calloutToggle;
	@FXML private ToggleGroup displayToggle;
	
	
	//public NoteEditorController() { }
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		assertFXMLNodes();
	}
	
	
	private void assertFXMLNodes() {
		
	}
	
	
	// ----- Begin button listeners -----
	@FXML protected void newStory() {
		
	}
	
	
	@FXML protected void loadStory() {
		
	}
	
	
	@FXML protected void saveStory() {
		
	}
	
	
	@FXML protected void newNote() {
		
	}
	
	
	@FXML protected void deleteNote() {
		
	}
	// ----- End button listeners -----


}
