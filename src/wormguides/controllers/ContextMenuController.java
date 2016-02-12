package wormguides.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wormguides.SearchOption;

public class ContextMenuController extends AnchorPane implements Initializable{
	
	@FXML private Text nameText;
	@FXML private Button color;
	
	private SearchOption searchOption;
	private Stage parentStage;
	
	private BooleanProperty bringUpInfoProperty;
	
	public ContextMenuController(Stage stage, BooleanProperty bringUpInfoProperty) {
		super();
		
		this.bringUpInfoProperty = bringUpInfoProperty;
		parentStage = stage;
		
		searchOption = SearchOption.CELL;
	}
	
	public void setColorButtonListener(EventHandler<ActionEvent> handler) {
		color.setOnAction(handler);
	}
	
	public void setSearchOption(SearchOption option) {
		searchOption = option;
	}
	
	public String getName() {
		return nameText.getText();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
	}
	
	@FXML public void showNeightborsAction() {
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
