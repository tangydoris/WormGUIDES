package wormguides;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.model.ShapeRule;


public class StructuresLayer {
	private ObservableList<String> allStructuresList;
	private ObservableList<String> searchResultsList;
	private ObservableList<Rule> rulesList;
	private Color selectedColor;
	private String selectedStructure;
	private String searchText;
	private HashMap<String, String> nameToCommentsMap;
	
	
	public StructuresLayer(SceneElementsList sceneElementsList) {
		selectedColor = Color.WHITE; //default color
		
		allStructuresList = FXCollections.observableArrayList();
		searchResultsList = FXCollections.observableArrayList();
		
		allStructuresList.addAll(sceneElementsList.getAllMulticellSceneNames());
		nameToCommentsMap = sceneElementsList.getNameToCommentsMap();
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

	
	public EventHandler<ActionEvent> getAddStructureRuleButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addShapeRule(selectedStructure, selectedColor);
			}
		};
	}
	
	
	public void addShapeRule(String name, Color color) {
		if (name==null || color==null)
			return;
		
		// Check for validity of name
		if (allStructuresList.contains(name)) {
			name = name.trim();
			ArrayList<SearchOption> optionsArray = new ArrayList<SearchOption>();
			optionsArray.add(SearchOption.MULTICELLULAR);
			ShapeRule rule = new ShapeRule(name, color, optionsArray);
			rulesList.add(rule);
		}
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
	public void searchStructures(String searched) {
		String[] terms = searched.toLowerCase().split(" ");
		searchResultsList.clear();
		for (String name : allStructuresList) {
			if (!searchResultsList.contains(name)) {
				// Look at scene names
				boolean nameSearched = true;
				boolean commentSearched = true;
				
				String comment = nameToCommentsMap.get(name).toLowerCase();
				name = name.toLowerCase();
				
				for (String word : terms) {
					if (nameSearched && !name.contains(word))
						nameSearched = false;
					if (comment!=null && commentSearched && !comment.contains(word))
						commentSearched = false;
				}
				
				if (nameSearched || commentSearched)
					searchResultsList.add(name);
			}
		}
	}
	
	
	public ChangeListener<String> getSelectionListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, 
										String oldValue, String newValue) {
				setSelectedStructure(newValue);
			}
		};
	}
	

	/*
	 * Called by RootLayourController to set reference to global rules list
	 */
	public void setRulesList(ObservableList<Rule> list) {
		if (list!=null)
			rulesList = list;
	}
	
	
	public String getSearchText() {
		return searchText;
	}
	
	
	public ObservableList<String> getStructuresSearchResultsList() {
		return searchResultsList;
	}
	
}
