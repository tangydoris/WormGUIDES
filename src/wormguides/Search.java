package wormguides;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class Search {
	
	public enum Type {
		SYSTEMATIC, FUNCTIONAL, DESCRIPTION, GENE;
	}
	
	public enum Option {
		CELL("cell"),
		ANCESTOR("ancestor"),
		DESCENDANT("descendants");
		
		private String description;
		
		Option() {
			this("");
		}
		Option(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
	}
	
	private ArrayList<String> cellNames;
	private ObservableList<String> searchResults;
	private TextField searchField;
	private ListView<String> searchResultsList;
	
	private ToggleGroup searchType;
	
	private CheckBox cellTick;
	private CheckBox ancestorTick;
	private CheckBox descendantTick;
	
	public Search() {
		this(new TextField(), new ListView<String>());
	}
	
	public Search(TextField searchField, ListView<String> searchResultsList) {
		if (searchField==null)
			searchField = new TextField();
		if (searchResultsList==null)
			searchResultsList = new ListView<String>();
		
		this.searchField = searchField;
		this.searchResultsList = searchResultsList;
		
		searchType = new ToggleGroup();
		
		addTextListener();
	}
	
	public ToggleGroup getTypeToggleGroup() {
		return searchType;
	}
	
	public void addTypeToggleGroupListener(ToggleGroup group) {
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable,
					Toggle arg1, Toggle arg2) {	
				switch ((Type) group.getSelectedToggle().getUserData()) {
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
	
	/*
	public void setRadioButons(RadioButton sysRadioBtn, RadioButton funRadioBtn,
			RadioButton desRadioBtn, RadioButton genRadioBtn) {
		if (sysRadioBtn==null || funRadioBtn==null || desRadioBtn==null || genRadioBtn==null)
			throw new IllegalArgumentException("cannot set radio buttons in Search");
		
		
		sysRadioBtn.setToggleGroup(searchType);
		sysRadioBtn.setUserData(Type.SYSTEMATIC);
		funRadioBtn.setToggleGroup(searchType);
		funRadioBtn.setUserData(Type.FUNCTIONAL);
		desRadioBtn.setToggleGroup(searchType);
		desRadioBtn.setUserData(Type.DESCRIPTION);
		genRadioBtn.setToggleGroup(searchType);
		genRadioBtn.setUserData(Type.GENE);
		
		sysRadioBtn.setSelected(true);
	}
	*/
	
	public void setCellNames(ArrayList<String> cellNames) {
		this.cellNames = cellNames;
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
