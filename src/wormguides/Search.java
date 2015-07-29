package wormguides;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
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
	
	private ArrayList<String> cellNames;
	private ObservableList<String> searchResults;
	private TextField searchField;
	private ListView<String> searchResultsList;
	
	private ToggleGroup searchType;
	
	private boolean cellTicked;
	private boolean ancestorTicked;
	private boolean descendantTicked;
	
	private ObservableList<ColorRule> rulesList;
	
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
				Color color = Color.RED;
				
				ColorRule rule = new ColorRule(cellName, color, options);
				rulesList.add(rule);
			}
		};
	}
	
	/*
	public void addAddButtonListener(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
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
				Color color = Color.RED;
				
				ColorRule rule = new ColorRule(cellName, color, options);
				rulesList.add(rule);
			}
		});
	}
	*/
	
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
