package wormguides.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import wormguides.SearchOption;

public class RuleEditorController extends AnchorPane implements Initializable{
	
	@FXML Text heading;
	@FXML CheckBox cellTick;
	@FXML Label cellLabel;
	@FXML CheckBox cellBodyTick;
	@FXML Label cellBodyLabel;
	@FXML CheckBox ancTick;
	@FXML Label ancLabel;
	@FXML CheckBox desTick;
	@FXML Label desLabel;
	@FXML ColorPicker picker;
	@FXML Button submit;

	
	public RuleEditorController() {
		super();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
	}
	
	private void assertFXMLNodes() {
		assert (heading!=null);
		assert (cellTick!=null);
		assert (cellBodyTick!=null);
		assert (ancTick!=null);
		assert (desTick!=null);
		assert (picker!=null);
		assert (submit!=null);
	}
	
	
	public void disableDescendantOption() {
		desLabel.setDisable(true);
		desTick.setDisable(true);
	}
	
	
	public void disableOptionsForStructureRule() {
		cellBodyTick.setSelected(true);
		
		cellLabel.setDisable(true);
		cellTick.setDisable(true);
		ancLabel.setDisable(true);
		ancTick.setDisable(true);
		desLabel.setDisable(true);
		desTick.setDisable(true);
	}
	
	
	public void setHeading(String name) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				heading.setText(name);
			}
		});
	}
	
	
	public void setSubmitHandler(EventHandler<ActionEvent> handler) {
		submit.setOnAction(handler);
	}
	
	
	public boolean isCellTicked() {
		return cellTick.isSelected();
	}
	
	
	public void setCellTicked(boolean ticked) {
		cellTick.setSelected(ticked);
	}
	
	
	public boolean isCellBodyTicked() {
		return cellBodyTick.isSelected();
	}
	
	
	public void setCellBodyTicked(boolean ticked) {
		cellBodyTick.setSelected(ticked);
	}
	
	
	public boolean isAncestorsTicked() {
		return ancTick.isSelected();
	}
	
	
	public void setAncestorsTicked(boolean ticked) {
		ancTick.setSelected(ticked);
	}
	
	
	public boolean isDescendantsTicked() {
		return desTick.isSelected();
	}
	
	
	public void setDescendantsTicked(boolean ticked) {
		desTick.setSelected(ticked);
	}
	
	
	public Color getColor() {
		return picker.getValue();
	}
	
	
	public void setColor(Color color) {
		picker.setValue(color);
	}
	
	
	public ArrayList<SearchOption> getOptions() {
		ArrayList<SearchOption> options = new ArrayList<SearchOption>();
		if (isCellTicked())
			options.add(SearchOption.CELL);
		if (isCellBodyTicked())
			options.add(SearchOption.CELLBODY);
		if (isAncestorsTicked())
			options.add(SearchOption.ANCESTOR);
		if (isDescendantsTicked())
			options.add(SearchOption.DESCENDANT);
		return options;
	}
	
}
