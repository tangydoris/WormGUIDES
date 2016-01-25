package wormguides;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ContextMenuController extends AnchorPane implements Initializable{
	
	@FXML private Text nameText;
	
	private BooleanProperty bringUpInfoProperty;
	
	public ContextMenuController(BooleanProperty bringUpInfoProperty) {
		super();
		this.bringUpInfoProperty = bringUpInfoProperty;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
	}
	
	@FXML public void showNeightborsAction() {
		// TODO
	}
	
	@FXML public void sendToSearchAction() {
		// TODO
	}
	
	@FXML public void showInfoAction() {
		if (bringUpInfoProperty!=null)
			bringUpInfoProperty.set(true);
	}
	
	public void setName(String name) {
		nameText.setText(name);
	}
	
	private void assertFXMLNodes() {
		assert (nameText!=null);
	}
}
