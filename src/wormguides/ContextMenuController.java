package wormguides;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ContextMenuController extends AnchorPane implements Initializable{
	
	@FXML Text nameText;
	
	
	public ContextMenuController() {
		super();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
	}
	
	private void assertFXMLNodes() {
		assert (nameText!=null);
	}
	
	public void setName(String name) {
		nameText.setText(name);
	}
}
