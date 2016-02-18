package wormguides;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.view.AppFont;
import wormguides.model.MulticellularStructureRule;
import wormguides.model.PartsList;


public class StructuresLayer {
	
	private ObservableList<Rule> rulesList;
	
	private ObservableList<String> allStructuresList;
	private ObservableList<String> searchResultsList;
	
	private Color selectedColor;
	private String searchText;
	
	private HashMap<String, ArrayList<String>> nameToCellsMap;
	private HashMap<String, String> nameToCommentsMap;
	private HashMap<String, StructureListCellGraphic> nameListCellMap;
	
	private StringProperty selectedNameProperty;
	
	private TextField searchField;
	
	public StructuresLayer(SceneElementsList sceneElementsList, TextField searchField) {
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
							nameListCellMap.put(string, graphic);
						}
					}
				}
			}
		});
		
		allStructuresList.addAll(sceneElementsList.getAllMulticellSceneNames());
		nameToCellsMap = sceneElementsList.getNameToCellsMap();
		nameToCommentsMap = sceneElementsList.getNameToCommentsMap();
		
		setSearchField(searchField);
	}
	
	
	public ObservableList<String> getAllStructuresList() {
		return allStructuresList;
	}
	
	
	public void setSelectedStructure(String structure) {
		// unhighlight previous selected structure
		if (!selectedNameProperty.get().isEmpty())
			nameListCellMap.get(selectedNameProperty.get()).setSelected(false);
		
		selectedNameProperty.set(structure);
		
		// highlight new selected structure
		if (!selectedNameProperty.get().isEmpty())
			nameListCellMap.get(selectedNameProperty.get()).setSelected(true);
	}
	
	
	public void setSelectedColor(Color color) {
		selectedColor = color;
	}
	
	
	private void setSearchField(TextField field) {
		if (field!=null) {
			searchField = field;
			
			searchField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, 
						String oldValue, String newValue) {
					searchText = newValue.toLowerCase();
					
					if (searchText.isEmpty())
						searchResultsList.clear();
					
					else {
						setSelectedStructure("");
						deselectAllStructures();
						searchAndUpdateResults(newValue.toLowerCase());
					}
				}
			});
		}
	}
	
	
	private void deselectAllStructures() {
		for (String name : nameListCellMap.keySet()) {
			nameListCellMap.get(name).setSelected(false);
		}
	}
	
	
	public EventHandler<ActionEvent> getAddStructureRuleButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String name = selectedNameProperty.get();
				
				if (!name.isEmpty()) {
					addStructureRule(name, selectedColor);
					deselectAllStructures();
				}
				
				// if no name is selected, add all results from search
				else {
					for (String string : searchResultsList)
						addStructureRule(string, selectedColor);
					searchField.clear();
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
				boolean appliesToCell = false;
				boolean appliesToComment = true;
				
				for (String term : terms) {
					if (!nameLower.contains(term)) {
						appliesToName = false;
						break;
					}
				}
				
				//search in cells
				ArrayList<String> cells = nameToCellsMap.get(nameLower);
				if (cells!=null) {
					for (String cell : cells) {
						//we'll use the first term
						if (terms.length > 0) {
							//check if search term is a functional name
							String lineageName = PartsList.getLineageNameByFunctionalName((terms[0].toUpperCase()));
							if (lineageName != null) {
								if (cell.toLowerCase().startsWith(lineageName.toLowerCase())) {
									appliesToCell = true;
									break;
								}
							} else {
								if (cell.toLowerCase().startsWith(terms[0].toLowerCase())) {
									appliesToCell = true;
									break;
								}
							}
						}
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
				
				if (appliesToName || appliesToCell || appliesToComment)
					searchResultsList.add(name);
			}
		}
	}
	
	//search in cells
//			for (SceneElement se : sceneElementsList.elementsList) {
//				if (se.getSceneName().toLowerCase().equals(nameLower)) {
//					for (String cell : se.getAllCellNames()) {
//						if (cell.toLowerCase().contains(searched.toLowerCase())) {
//							appliesToCell = true;
//						}
//					}
//				}
//			}
	
	
	public StringProperty getSelectedNameProperty() {
		return selectedNameProperty;
	}
	

	// Called by RootLayourController to set reference to global rules list
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
	
	
	// Graphical representation of a structure list cell
	private class StructureListCellGraphic extends HBox{
		
		private BooleanProperty isSelected;
		private Label label;
		
		public StructureListCellGraphic(String name) {
			super();
			
			label = new Label(name);
	    	label.setFont(AppFont.getFont());
	    	
	    	label.setPrefHeight(UI_HEIGHT);
	    	label.setMinHeight(USE_PREF_SIZE);
	    	label.setMaxHeight(USE_PREF_SIZE);
	    	
	    	getChildren().add(label);
	    	
	    	setMaxWidth(Double.MAX_VALUE);
	    	setPadding(new Insets(5, 5, 5, 5));
	    	
	    	setPickOnBounds(false);
	    	isSelected = new SimpleBooleanProperty(false);
	    	setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					isSelected.set(!isSelected());
					searchField.clear();
				}
	    	});
	    	isSelected.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, 
						Boolean oldValue, Boolean newValue) {
					if (newValue) {
						setSelectedStructure(label.getText());
						highlightCell(true);
					}
					else {
						setSelectedStructure("");
						highlightCell(false);
					}
				}
	    	});
	    	highlightCell(isSelected());
		}
		
		
		public boolean isSelected() {
			return isSelected.get();
		}
		
		
		public void setSelected(boolean selected) {
			isSelected.set(selected);
		}
		
		
		private void highlightCell(boolean highlight) {
			if (highlight) {
				setStyle("-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar;"
						+ "-fx-background: -fx-accent;");
				label.setTextFill(Color.WHITE);
			}
			else {
				setStyle("-fx-background-color: white;");
				label.setTextFill(Color.BLACK);
			}
		}
		
		
		private final double UI_HEIGHT = 28.0;
	}
	
}
