package wormguides;

import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import wormguides.view.Window3DSubScene;

public class Search {
	
	private ArrayList<String> cellNames;
	private ObservableList<String> searchResults;
	private TextField searchField;
	private ListView<String> searchResultsList;
	
	private Window3DSubScene window3D;

	public Search(Window3DSubScene window3D, TextField searchField, ListView<String> searchResultsList) {
		this.window3D = window3D;
		this.searchField = searchField;
		this.searchResultsList = searchResultsList;
		addListeners();
	}
	
	public void setCellNames(ArrayList<String> cellNames) {
		this.cellNames = cellNames;
	}
	
	private void addListeners() {
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
