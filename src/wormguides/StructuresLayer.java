package wormguides;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.view.StructureListCellGraphic;
import wormguides.model.MulticellularStructureRule;


public class StructuresLayer {
	private ObservableList<String> allStructuresList;
	private ObservableList<String> searchResultsList;
	private ObservableList<Rule> rulesList;
	private Color selectedColor;
	private String selectedStructure;
	private String searchText;
	private HashMap<String, String> nameToCommentsMap;
	
	private HashMap<String, StructureListCellGraphic> nameListCellMap;
	private StringProperty selectedNameProperty;
	
	public StructuresLayer(SceneElementsList sceneElementsList) {
		selectedColor = Color.WHITE; //default color
		
		allStructuresList = FXCollections.observableArrayList();
		searchResultsList = FXCollections.observableArrayList();
		
		nameListCellMap = new HashMap<String, StructureListCellGraphic>();
		selectedNameProperty = new SimpleStringProperty("");
		
		allStructuresList.addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends String> change) {
				while (change.next()) {
					if (!change.wasUpdated()) {
						for (String string : change.getAddedSubList()) {
							StructureListCellGraphic graphic = new StructureListCellGraphic(string);
							graphic.setOnMouseClicked(new EventHandler<Event>() {
					    		@Override
					    		public void handle(Event event) {
					    			if (graphic.isSelected()) {
					    				graphic.deselect();
					    				selectedNameProperty.set("");
					    			}
					    			else {
					    				deselectAllExcept(graphic);
					    				selectedNameProperty.set(string);
					    			}
					    		}
					    	});
							nameListCellMap.put(string, graphic);
						}
					}
				}
			}
		});
		
		allStructuresList.addAll(sceneElementsList.getAllMulticellSceneNames());
		nameToCommentsMap = sceneElementsList.getNameToCommentsMap();
	}

	
	/*
	 * Un-hilights all cells except for input cell graphic
	 */
	private void deselectAllExcept(StructureListCellGraphic graphic) {
		for (StructureListCellGraphic g : nameListCellMap.values())
			g.deselect();
		graphic.select();
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
					searchAndUpdateResults(newValue.toLowerCase());
			}
		};
	}
	
	
	public EventHandler<ActionEvent> getAddStructureRuleButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String name = selectedNameProperty.get();
				
				if (!name.isEmpty())
					addStructureRule(name, selectedColor);
				
				// if no name is selected, add all results from search
				else {
					for (String string : searchResultsList)
						addStructureRule(string, selectedColor);
				}
			}
		};
	}
	
	
	public void addStructureRule(String name, Color color) {
		if (name==null || color==null)
			return;
		
		// Check for validity of name
		if (allStructuresList.contains(name)) {
			name = name.trim();
			ArrayList<SearchOption> optionsArray = new ArrayList<SearchOption>();
			optionsArray.add(SearchOption.MULTICELLULAR);
			rulesList.add(new MulticellularStructureRule(name, color, optionsArray));
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
	public void searchAndUpdateResults(String searched) {
		if (searched==null || searched.isEmpty())
			return;
		
		String[] terms = searched.toLowerCase().split(" ");
		searchResultsList.clear();
		
		for (String name : allStructuresList) {
			
			if (!searchResultsList.contains(name)) {
				// search in structure scene names
				String nameLower = name.toLowerCase();
				
				boolean appliesToName = true;
				boolean appliesToComment = true;
				
				for (String term : terms) {
					if (!nameLower.contains(term)) {
						appliesToName = false;
						break;
					}
				}
				
				// search in comments if name does not already apply
				String comment = nameToCommentsMap.get(nameLower);
				String commentLower = comment.toLowerCase();
				for (String term : terms) {
					if (!commentLower.contains(term)) {
						appliesToComment = false;
						break;
					}
				}
				
				if (appliesToName || appliesToComment)
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
	 * Resets the selected name so the change can be detected by
	 * the root layout controller
	 */
	/*
	private void resetSelectedNameProperty(String name) {
		selectedNameProperty.set("");
		selectedNameProperty.set(name);
	}
	*/
	

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
	
	
	public void addSelectedNameListener(ChangeListener<String> listener) {
		if (listener!=null)
			selectedNameProperty.addListener(listener);
	}
	
	
	public Callback<ListView<String>, ListCell<String>> getCellFactory() {
		return new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				ListCell<String> cell = new ListCell<String>() {
					@Override
		            protected void updateItem(String name, boolean empty) {
		                super.updateItem(name, empty);
		                if (name != null)
		                	setGraphic(nameListCellMap.get(name));
		            	else
		            		setGraphic(null);

		                setStyle("-fx-focus-color: transparent;"
	            				+ "-fx-background-color: transparent;");
		                setPadding(Insets.EMPTY);
		        	}
				};
				return cell;
			}
		};
	}
	
}
