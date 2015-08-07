package wormguides;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.paint.Color;
import wormguides.model.ColorRule;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;

public class Search {
	
	// list of every cell name that exists
	private ArrayList<String> allLineageNames;
	
	// list of cells with functional names
	//private ArrayList<String> linageNames;
	private ArrayList<String> functionalNames;
	private ArrayList<String> descriptions;
	
	private ObservableList<String> searchResultsList;
	private TextField searchField;
	private ListView<String> searchResultsListView;
	
	private SearchType type;
	
	private boolean cellTicked;
	private boolean ancestorTicked;
	private boolean descendantTicked;
	
	private ObservableList<ColorRule> rulesList;
	private Color selectedColor;
	
	//private final Task<Void> resultsUpdateTask;
	private final Service<Void> resultsUpdateService;
	 
	public Search() {
		this(new TextField(), new ListView<String>());
	}
	
	public Search(TextField searchField, ListView<String> searchResultsList) {
		allLineageNames = new ArrayList<String>(Arrays.asList(
							AceTreeLoader.getAllCellNames()));
		//linageNames = PartsList.getLineageNames();
		functionalNames = PartsList.getFunctionalNames();
		descriptions = PartsList.getDescriptions();
		
		type = SearchType.SYSTEMATIC;
		
		if (searchField==null)
			searchField = new TextField();
		if (searchResultsList==null)
			searchResultsList = new ListView<String>();
		
		selectedColor = Color.WHITE;
		
		this.searchField = searchField;
		this.searchResultsListView = searchResultsList;
		
		cellTicked = false;
		ancestorTicked = false;
		descendantTicked = false;
		
		addTextListener();
		
		resultsUpdateService = new Service<Void>() {
			@Override
			protected final Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								refreshSearchResultsList(getSearchedText());
							}
						});
						return null;
					}
				};
				/*
				task.setOnScheduled(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("search results refresh service scheduled");
					}
				});
				task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("search results refresh service cancelled");
					}
				});
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("search results refresh service succeeded");
					}
				});
				*/
				return task;
			}
		};
		
		/*
		resultsUpdateTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				refreshSearchResultsList(getSearchedText());
				return null;
			}
		};
		resultsUpdateTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println("finished updating results in search class");
			}
		});
		*/
	}
	
	private String getSearchedText() {
		final String searched = searchField.getText();
		return searched;
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
	
	public void addDefaultRules() {
		addColorRule("ABa", Color.RED);
		addColorRule("ABp", Color.BLUE);
		addColorRule("EMS", Color.GREEN);
		addColorRule("P2", Color.YELLOW, SearchOption.ANCESTOR, 
					SearchOption.CELL, SearchOption.DESCENDANT);
	}
	
	private void addColorRule(String searched, Color color, SearchOption...options) {
		addColorRule(searched, color, new ArrayList<SearchOption>(Arrays.asList(options)));
	}
	
	private void addColorRule(String searched, Color color, ArrayList<SearchOption> options) {
		// default search options is cell and descendant
		if (options==null)
			options = new ArrayList<SearchOption>();
		if (options.isEmpty()) {
			options.add(SearchOption.CELL);
			options.add(SearchOption.DESCENDANT);
		}
		
		searched = searched.toLowerCase();
		switch (type) {
			case SYSTEMATIC:
						searched = LineageTree.getCaseSensitiveName(searched);
						break;
			case FUNCTIONAL:
						searched = "'"+searched+"' functional";
						break;
			case DESCRIPTION:
						searched = "'"+searched+"' description";
						break;
			case GENE:
						searched = "'"+searched+"' gene";
						break;
		}
		
		ColorRule rule = new ColorRule(searched, color, options);
		
		ArrayList<String> cells = getCellsList(searched);
		rule.setCells(cells);
		rule.setAncestors(getAncestorsList(cells));
		rule.setDescendants(getDescendantsList(cells));
		
		if (!containsRule(rule))
			rulesList.add(rule);
	}
	
	private ArrayList<String> getCellsList(String searched) {
		ArrayList<String> cells = new ArrayList<String> ();
		searched = searched.toLowerCase();
		switch (type) {
			case SYSTEMATIC:
						for (String name : allLineageNames) {
							if (name.toLowerCase().equals(searched))
								cells.add(name);
						}
						break;
						
			case FUNCTIONAL:
						for (String name : functionalNames) {
							if (name.toLowerCase().equals(searched))
								cells.add(PartsList.getLineageNameByFunctionalName(name));
						}
						break;
						
			case DESCRIPTION:
						break;
						
			case GENE:
						break;
		}
		
		return cells;
	}
	
	// generates a list of descendants of all cells in input
	private ArrayList<String> getDescendantsList(ArrayList<String> cells) {
		ArrayList<String> descendants = new ArrayList<String>();
		for (String cell : cells) {
			for (String name : allLineageNames) {
				if (!descendants.contains(name) && LineageTree.isDescendant(name, cell))
					descendants.add(name);
			}
		}
		return descendants;
	}
	
	// generates a list of ancestors of all cells in input
	private ArrayList<String> getAncestorsList(ArrayList<String> cells) {
		ArrayList<String> ancestors = new ArrayList<String>();
		for (String cell : cells) {
			for (String name : allLineageNames) {
				if (!ancestors.contains(name) && LineageTree.isAncestor(name, cell))
					ancestors.add(name);
			}
		}
		return ancestors;
	}
	
	public EventHandler<ActionEvent> getAddButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// do not add new ColorRule if search has no matches
				if (searchResultsList.isEmpty())
					return;
				
				ArrayList<SearchOption> options = new ArrayList<SearchOption>();
				if (cellTicked)
					options.add(SearchOption.CELL);
				if (ancestorTicked)
					options.add(SearchOption.ANCESTOR);
				if (descendantTicked)
					options.add(SearchOption.DESCENDANT);
				
				addColorRule(searchField.getText(), selectedColor, options);
				
				searchField.clear();
			}
		};
	}
	
	public ChangeListener<Boolean> getCellTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellTicked = newValue;
				//refreshSearchResultsList(searchField.getText());
				//Platform.runLater(resultsUpdateTask);
				resultsUpdateService.restart();
			}
		};
	}
	
	public ChangeListener<Boolean> getAncestorTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				ancestorTicked = newValue;
				//refreshSearchResultsList(searchField.getText());
				//Platform.runLater(resultsUpdateTask);
				resultsUpdateService.restart();
			}
		};
	}
	
	public ChangeListener<Boolean> getDescendantTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				descendantTicked = newValue;
				//refreshSearchResultsList(searchField.getText());
				//Platform.runLater(resultsUpdateTask);
				resultsUpdateService.restart();
			}
		};
	}
	
	public ChangeListener<Toggle> getTypeToggleListener() {
		return new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, 
					Toggle oldValue, Toggle newValue) {
				type = (SearchType) newValue.getUserData();
				//refreshSearchResultsList(searchField.getText());
				//Platform.runLater(resultsUpdateTask);
				resultsUpdateService.restart();
			}
		};
	}
	
	public ObservableList<String> getSearchResultsList() {
		return searchResultsList;
	}
	
	private void addTextListener() {
		searchResultsList = FXCollections.observableArrayList();
		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				//refreshSearchResultsList(newValue);
				//Platform.runLater(resultsUpdateTask);
				resultsUpdateService.restart();
			}
		});
		searchResultsListView.setItems(searchResultsList);
	}
	
	private void refreshSearchResultsList(String newValue) {
		String searched = newValue.toLowerCase();
		searchResultsList.clear();
		if (!searched.isEmpty()) {
			if (!cellTicked && !descendantTicked && !ancestorTicked) {
				switch (type) {
					case SYSTEMATIC:
							for (String name : allLineageNames) {
							String nameLowerCase = name.toLowerCase();
								if (nameLowerCase.startsWith(searched)) {
									String functionalName = PartsList
											.getFunctionalNameByLineageName(name);
									if (functionalName==null)
										searchResultsList.add(name);
									else
										searchResultsList.add(name+" ("+functionalName+")");
								}
							}
							break;
							
					case FUNCTIONAL:
							for (String name : functionalNames) {
								String nameLowerCase = name.toLowerCase();
								if (nameLowerCase.startsWith(searched)) {
									String lineageName = PartsList
											.getLineageNameByFunctionalName(name);
									searchResultsList.add(lineageName+" ("+name+")");
								}
							}
							break;
			
					case DESCRIPTION:
							break;
						
					case GENE:
							break;

				}
			}
			else {
				ArrayList<String> cells = getCellsList(searched);
				if (descendantTicked) {
					searchResultsList.addAll(getDescendantsList(cells));
				}
				if (cellTicked) {
					searchResultsList.addAll(cells);
				}
				if (ancestorTicked) {
					searchResultsList.addAll(getAncestorsList(cells));
				}
			}
		}
	}
	
	public Service<Void> getResultsUpdateService() {
		return resultsUpdateService;
	}
	
	/*
	public Task<Void> getResultsUpdateTask() {
		return resultsUpdateTask;
	}
	*/
	
}
