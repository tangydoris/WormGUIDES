package wormguides;

import java.util.ArrayList;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;

public class StructuresLayer {
	private SceneElementsList sceneElementsList;
	@FXML private ListView<String> structuresSearchResultsListView;
	@FXML private ListView<String> allStructuresListView;
	@FXML private Button addStructureRuleBtn;
	private ObservableList<String> allStructuresList;
	private static Color selectedColor;
	private String selectedStructure;
	private String searchText;
	
	private static ObservableList<String> structuresSearchResultsList;
	
	public StructuresLayer(SceneElementsList sceneElementsList, 
			ListView<String> structuresSearchResultsListView,
			ListView<String> allStructuresListView, Search search,
			Button addStructureRuleBtn) {
		this.sceneElementsList = sceneElementsList;
		this.structuresSearchResultsListView = structuresSearchResultsListView;
		this.allStructuresListView = allStructuresListView;
		this.allStructuresList = FXCollections.observableArrayList();
		this.addStructureRuleBtn = addStructureRuleBtn;
		selectedColor = Color.WHITE; //default color
		
		structuresSearchResultsList = FXCollections.observableArrayList();
	}

	public void setStructuresLayer() {
		setStructuresList();
		allStructuresListView.setItems(this.allStructuresList);
		//how to set font of listview to 14 -- appfont.get
		
		//add listener for list view
		allStructuresListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						setSelectedInfo(newValue);
					}
		});
		

		structuresSearchResultsListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				System.out.println("HEY");
			}
		});
	}
	
	private void setSelectedInfo(String newValue) {
		selectedStructure = newValue;
	}
	
	public void setStructuresList() {	
		for (int i = 0; i < sceneElementsList.sceneElementsList.size(); i++) {
			SceneElement currSE = sceneElementsList.sceneElementsList.get(i);
			
			ArrayList<String> allCellNames = currSE.getAllCellNames();
			
			//check if the scene element is a multicellular structure
			if (allCellNames.size() > 1) {
				//add the scene name to the structures list
				allStructuresList.add(currSE.getSceneName());
			}
		}
	}
	
	public ChangeListener<String> getStructuresSearchFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
										String oldValue, String newValue) {
				searchText = newValue;
			}
		};
	}
	
	public ChangeListener<String> getStructuresTextFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
											String oldValue, String newValue) {
				searchText = newValue.toLowerCase();
				if (searchText.isEmpty())
					structuresSearchResultsList.clear();
				else
					searchStructures();
			}
		};
	}

	public EventHandler<ActionEvent> getAddStructureRuleButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//check search name

				Search.addShapeRule(selectedStructure, selectedColor);
			}
		};
	}
	
	public EventHandler<ActionEvent> getStructureRuleColorPickerListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectedColor = ((ColorPicker)event.getSource()).getValue();
			}
		};
	}
	
	public void searchStructures() {
		System.out.println("searching structures for matches");
		structuresSearchResultsList.add("HELLO");
	}
	
	public String getSearchText() {
		return this.searchText;
	}
	
	public ObservableList<String> getStructuresSearchResultsList() {
		return this.structuresSearchResultsList;
	}
}
