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
import wormguides.model.LineageTree;

public class Search {
	
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
				
				ArrayList<SearchOption> options = new ArrayList<SearchOption>();
				if (cellTicked)
					options.add(SearchOption.CELL);
				if (ancestorTicked)
					options.add(SearchOption.ANCESTOR);
				if (descendantTicked)
					options.add(SearchOption.DESCENDANT);
				// first element should be string with correct capitalization
				String cellName = LineageTree.getName(searchField.getText());
				
				// default search options is cell and descendant
				if (options.isEmpty()) {
					options.add(SearchOption.CELL);
					options.add(SearchOption.DESCENDANT);
				}
				ColorRule rule = new ColorRule(SearchType.SYSTEMATIC, cellName, selectedColor, options);
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
				refreshSearchResultsList(searchField.getText());
			}
		};
	}
	
	public ChangeListener<Boolean> getAncestorTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				ancestorTicked = newValue;
				refreshSearchResultsList(searchField.getText());
			}
		};
	}
	
	public ChangeListener<Boolean> getDescendantTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				descendantTicked = newValue;
				refreshSearchResultsList(searchField.getText());
			}
		};
	}
	
	public ChangeListener<Toggle> getTypeToggleListener() {
		return new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, 
					Toggle oldValue, Toggle newValue) {
				switch ((SearchType) observable.getValue()
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
	
	public ObservableList<String> getSearchResultsList() {
		return searchResults;
	}
	
	private void addTextListener() {
		searchResults = FXCollections.observableArrayList();
		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				refreshSearchResultsList(newValue);
			}
		});
		searchResultsList.setItems(searchResults);
	}
	
	private void refreshSearchResultsList(String newValue) {
		String searched = newValue.toLowerCase();
		searchResults.clear();
		if (!searched.isEmpty()) {
			for (String name : allCellNames) {
				String nameLowerCase = name.toLowerCase();
				if (!cellTicked && !descendantTicked && !ancestorTicked) {
					if (nameLowerCase.startsWith(searched))
						searchResults.add(name);
				}
				else {
					if (descendantTicked) {
						if (LineageTree.isDescendant(name, searched))
							searchResults.add(name);
					}
					if (cellTicked) {
						if (nameLowerCase.equals(searched))
							searchResults.add(name);
					}
					if (ancestorTicked) {
						if (LineageTree.isAncestor(name, searched))
							searchResults.add(name);
					}
				}
			}
		}
	}
	
}
