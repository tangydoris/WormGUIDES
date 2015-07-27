package wormguides;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	
	//private RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn;
	private ToggleGroup searchType;

	public Search(TextField searchField, ListView<String> searchResultsList) {
		this.searchField = searchField;
		this.searchResultsList = searchResultsList;
		//searchResultsList.setFocusTraversable(false);
		addTextListener();
	}
	
	public void setRadioButons(RadioButton sysRadioBtn, RadioButton funRadioBtn,
			RadioButton desRadioBtn, RadioButton genRadioBtn) {
		searchType = new ToggleGroup();
		sysRadioBtn.setToggleGroup(searchType);
		sysRadioBtn.setUserData(SYSTEMATIC);
		funRadioBtn.setToggleGroup(searchType);
		funRadioBtn.setUserData(FUNCTIONAL);
		desRadioBtn.setToggleGroup(searchType);
		desRadioBtn.setUserData(DESCRIPTION);
		genRadioBtn.setToggleGroup(searchType);
		genRadioBtn.setUserData(GENE);
		
		sysRadioBtn.setSelected(true);
		
		addRadioButtonsListener();
	}
	
	public void setCellNames(ArrayList<String> cellNames) {
		this.cellNames = cellNames;
	}
	
	private void addRadioButtonsListener() {
		searchType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable,
					Toggle arg1, Toggle arg2) {	
				// TODO implement type functionality
				switch ((String) searchType.getSelectedToggle().getUserData()) {
					case SYSTEMATIC:
						System.out.println("systematic search selected");
						break;
					case FUNCTIONAL:
						System.out.println("functional search selected");
						break;
					case DESCRIPTION:
						System.out.println("description search selected");
						break;
					case GENE:
						System.out.println("gene search selected");
						break;
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
	
	private static final String SYSTEMATIC = "systemastic",
			FUNCTIONAL = "functional",
			DESCRIPTION = "description",
			GENE = "gene";
	
}
