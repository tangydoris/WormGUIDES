package wormguides;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;


public class StructuresLayer {
	private ObservableList<String> allStructuresList;
	private ObservableList<String> searchResultsList;
	private static Color selectedColor;
	private String selectedStructure;
	private String searchText;
	
	public StructuresLayer(SceneElementsList sceneElementsList) {
		selectedColor = Color.WHITE; //default color
		allStructuresList = FXCollections.observableArrayList();
		searchResultsList = FXCollections.observableArrayList();
		
		for (int i = 0; i < sceneElementsList.sceneElementsList.size(); i++) {
			SceneElement current = sceneElementsList.sceneElementsList.get(i);
			//check if the scene element is a multicellular structure
			if (current.getAllCellNames().size() > 1)
				allStructuresList.add(current.getSceneName());
		}
	}
	
	
	public ObservableList<String> getAllStructuresList() {
		return allStructuresList;
	}
	
	
	public void setSelectedStructure(String structure) {
		selectedStructure = structure;
	}
	
	
	public void setSelectedColor(Color color) {
		selectedColor = color;
	}
	
	
	public ChangeListener<String> getStructuresTextFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
										String oldValue, String newValue) {
				searchText = newValue.toLowerCase();
				if (searchText.isEmpty())
					searchResultsList.clear();
				else
					searchStructures(newValue.toLowerCase());
			}
		};
	}
	
	
	/*
	 * Not sure what this text field this is for
	public ChangeListener<String> getTextFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
											String oldValue, String newValue) {
				searchText = newValue.toLowerCase();
				System.out.println(searchText);
				if (searchText.isEmpty())
					structuresSearchResultsList.clear();
				else
					searchStructures();
			}
		};
	}
	*/

	
	public EventHandler<ActionEvent> getAddStructureRuleButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Search.addShapeRule(selectedStructure, selectedColor);
			}
		};
	}
	
	
	public EventHandler<ActionEvent> getColorPickerListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectedColor = ((ColorPicker)event.getSource()).getValue();
			}
		};
	}
	
	
	// Only searches names for now
	// TODO extend search to comments
	public void searchStructures(String searched) {
		searchResultsList.clear();
		for (String name : allStructuresList) {
			if (!searchResultsList.contains(name) && name.toLowerCase().startsWith(searched))
				searchResultsList.add(name);
		}
	}
	
	
	public String getSearchText() {
		return searchText;
	}
	
	
	public ObservableList<String> getStructuresSearchResultsList() {
		return searchResultsList;
	}
	
}
