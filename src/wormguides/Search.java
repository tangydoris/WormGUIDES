package wormguides;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class Search {
	
	private ArrayList<String> cellNames;
	private ObservableList<String> searchResults;
	private TextField searchField;
	private ListView<String> searchResultsList;
	
	private RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn;
	private ToggleGroup typeGroup;

	public Search(TextField searchField, ListView<String> searchResultsList) {
		this.searchField = searchField;
		this.searchResultsList = searchResultsList;
		addTextListener();
		addRadioButtonsListener();
	}
	
	public void setRadioButons(RadioButton sysRadioBtn, RadioButton funRadioBtn,
			RadioButton desRadioBtn, RadioButton genRadioBtn) {
		this.sysRadioBtn = sysRadioBtn;
		sysRadioBtn.setSelected(true);
		this.funRadioBtn = funRadioBtn;
		this.desRadioBtn = desRadioBtn;
		this.genRadioBtn = genRadioBtn;
		
		typeGroup = sysRadioBtn.getToggleGroup();
	}
	
	public void setCellNames(ArrayList<String> cellNames) {
		this.cellNames = cellNames;
	}
	
	private void addRadioButtonsListener() {
		typeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable,
					Toggle oldValue, Toggle newValue) {
				if (typeGroup.getSelectedToggle() != null) {
					System.out.println("radio toggled");
				}
			}
		});
	}
	
	private void addTextListener() {
		searchResults = FXCollections.observableArrayList();
		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				String searched = newValue.toLowerCase();
				searchResults.clear();
				if (!searched.isEmpty()) {
					try {
						for (String name : cellNames) {
							if (name.toLowerCase().startsWith(searched))
								searchResults.add(name);
						}

					} catch (NullPointerException npe) {
						System.out.println("cannot set cell names for search");
					}
				}
			}
		});
		searchResultsList.setItems(searchResults);
		
	}
	
	
	
}
