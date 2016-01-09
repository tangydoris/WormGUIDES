package wormguides;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NoteEditorController extends AnchorPane implements Initializable{
	
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
	
	private NewStoryEditorController editController;
	private Stage editStage;
	
	
	public NoteEditorController() {
		super();
		editController = new NewStoryEditorController();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
	}
	
	
	private void assertFXMLNodes() {
		assert (titleField!=null);
		assert (contentArea!=null);
		assert (structuresComboBox!=null);
		assert (calloutTick!=null);
		
		assert (timeTick!=null);
		assert (startTimeField!=null);
		assert (endTimeField!=null);
		
		assert (attachmentToggle!=null);
		assert (calloutToggle!=null);
		assert (displayToggle!=null);
	}
	
	
	// ----- Begin button listeners -----
	@FXML protected void newStory() {
		if (editStage==null) {
			editStage = new Stage();
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/NewStoryEditorLayout.fxml"));
			
			loader.setController(editController);
			loader.setRoot(editController);
			
			try {
				editStage.setScene(new Scene((AnchorPane) loader.load()));
				
				editStage.setTitle("New Storys");
				editStage.initModality(Modality.NONE);
				
				for (Node node : editStage.getScene().getRoot().getChildrenUnmodifiable()) {
	            	node.setStyle("-fx-focus-color: -fx-outer-border; "+
	            					"-fx-faint-focus-color: transparent;");
	            }
				
				editController.addCancelButtonListener(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						editStage.hide();
					}
				});
				
				editController.addSubmitButtonListener(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// TODO create new story on click
						
						editStage.hide();
					}
				});
				
			} catch (IOException e) {
				System.out.println("error in initializing new story editor.");
				e.printStackTrace();
			}
		}
		
		editStage.show();
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
