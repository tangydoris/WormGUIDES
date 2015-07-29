package wormguides;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import wormguides.model.ColorRule;

public class Search {
	
	public enum Type {
		SYSTEMATIC, FUNCTIONAL, DESCRIPTION, GENE;
	}
	
	public enum Option {
		CELL("cell"),
		ANCESTOR("ancestor"),
		DESCENDANT("descendant");
		
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
	
	private ArrayList<String> allCellNames;
	private ObservableList<String> searchResults;
	private TextField searchField;
	private ListView<String> searchResultsList;
	
	private ToggleGroup searchType;
	
	private boolean cellTicked;
	private boolean ancestorTicked;
	private boolean descendantTicked;
	
	private ObservableList<ColorRule> rulesList;
	private Color selectedColor;
	
	public Search() {
		this(new TextField(), new ListView<String>());
	}
	
	public Search(TextField searchField, ListView<String> searchResultsList) {
		if (searchField==null)
			searchField = new TextField();
		if (searchResultsList==null)
			searchResultsList = new ListView<String>();
		
		selectedColor = Color.WHITE;
		
		this.searchField = searchField;
		this.searchResultsList = searchResultsList;
		
		searchType = new ToggleGroup();
		
		cellTicked = false;
		ancestorTicked = false;
		descendantTicked = false;
		
		addTextListener();
	}
	
	public ToggleGroup getTypeToggleGroup() {
		return searchType;
	}
	
	public void setRulesList(ObservableList<ColorRule> rulesList) {
		this.rulesList = rulesList;
	}
	
	public boolean containsRule(ColorRule other) {
		for (ColorRule rule : rulesList) {
			if (rule.equals(other))
				return true;
		}
		return false;
	}
	
	public EventHandler<ActionEvent> getColorPickerListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectedColor = ((ColorPicker)event.getSource()).getValue();
			}
		};
	}
	
	public EventHandler<ActionEvent> getAddButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// do not add new ColorRule if search has no matches
				if (searchResults.isEmpty())
					return;
				
				ArrayList<Option> options = new ArrayList<Option>();
				if (cellTicked)
					options.add(Option.CELL);
				if (ancestorTicked)
					options.add(Option.ANCESTOR);
				if (descendantTicked)
					options.add(Option.DESCENDANT);
				// first element should be string with correct capitalization
				String cellName = searchResults.get(0);
				
				ColorRule rule = new ColorRule(cellName, selectedColor, options);
				
				if (!containsRule(rule))
					rulesList.add(rule);
			}
		};
	}
	
	public ChangeListener<Boolean> getCellTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellTicked = newValue;
			}
		};
	}
	
	public ChangeListener<Boolean> getAncestorTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				ancestorTicked = newValue;
			}
		};
	}
	
	public ChangeListener<Boolean> getDescendantTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				descendantTicked = newValue;
			}
		};
	}
	
	public ChangeListener<Toggle> getTypeToggleListener() {
		return new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, 
					Toggle oldValue, Toggle newValue) {
				switch ((Type) observable.getValue()
						.getToggleGroup()
						.getSelectedToggle()
						.getUserData()) {
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
		};
	}
	
	public void setCellNames(String[] cellNamesArr) {
		allCellNames = new ArrayList<String>(Arrays.asList(cellNamesArr));
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
						for (String name : allCellNames) {
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
