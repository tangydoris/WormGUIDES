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
import javafx.concurrent.WorkerStateEvent;
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
	
	private final Service<Void> resultsUpdateService;
	private final Service<ArrayList<String>> geneSearchService;
	
	private final Service<Void> showLoadingService;
	private int count;
	 
	public Search() {
		this(new TextField(), new ListView<String>());
	}
	
	public Search(TextField searchField, ListView<String> searchResultsList) {
		allLineageNames = new ArrayList<String>(Arrays.asList(
							AceTreeLoader.getAllCellNames()));
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
				return task;
			}
		};
		
		geneSearchService = WormBaseQuery.getSearchService();
		if (geneSearchService != null) {
			geneSearchService.setOnScheduled(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					showLoadingService.restart();
				}
			});
			geneSearchService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					showLoadingService.cancel();
					updateGeneResults(geneSearchService.getValue());
				}
			});
		}
		
		showLoadingService = new ShowLoadingService();
		count = 0;
	}
	
	private void updateGeneResults(ArrayList<String> results) {
		searchResultsList.clear();
		if (results.isEmpty())
			searchResultsList.add("No results found from WormBase");
		else {
			for (String result : results) {
				if (!searchResultsList.contains(result))
					searchResultsList.add(result);
			}		
		}
		System.out.println("updated list with gene results");
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
		addColorRule("ABa", Color.RED, SearchOption.CELL, SearchOption.DESCENDANT);
		addColorRule("ABp", Color.BLUE, SearchOption.CELL, SearchOption.DESCENDANT);
		addColorRule("EMS", Color.GREEN, SearchOption.CELL, SearchOption.DESCENDANT);
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
		
		String label = "";
		searched = searched.toLowerCase();
		switch (type) {
			case SYSTEMATIC:
						label = LineageTree.getCaseSensitiveName(searched);
						break;
			case FUNCTIONAL:
						label = "'"+searched+"' functional";
						break;
			case DESCRIPTION:
						label = "'"+searched+"' description";
						break;
			case GENE:
						label = "'"+searched+"' gene";
						break;
		}
		
		ColorRule rule = new ColorRule(label, color, options);
		
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
							for (String text : descriptions) {
								String textLowerCase = text.toLowerCase();
								String[] keywords = searched.split(" ");
								boolean found = true;
								for (String keyword : keywords) {
									if (textLowerCase.indexOf(keyword)==-1) {
										found = false;
										break;
									}
								}
								if (found)
									cells.add(PartsList.getLineageNameByIndex(
													descriptions.indexOf(text)));
							}
							break;
						
			case GENE:	
							if (geneSearchService != null) {
								cells = geneSearchService.getValue();
							}
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
							for (String text : descriptions) {
								String textLowerCase = text.toLowerCase();
								String[] keywords = searched.split(" ");
								boolean found = true;
								for (String keyword : keywords) {
									if (textLowerCase.indexOf(keyword)==-1) {
										found = false;
										break;
									}
								}
								if (found) {
									String name = PartsList
											.getLineageNameByIndex(descriptions.indexOf(text))
											+" ("
											+PartsList
											.getFunctionalNameByIndex(descriptions.indexOf(text))
											+")";
									if (!searchResultsList.contains(name))
										searchResultsList.add(name);
								}
							}
							break;
						
					case GENE:
							if (geneSearchService != null)
								WormBaseQuery.doSearch(searched);
							break;

				}
			}
			else {
				ArrayList<String> cells = getCellsList(searched);
				if (cells==null)
					return;
				
				if (descendantTicked) {
					ArrayList<String> descendants = getDescendantsList(cells);
					for (int i=0; i<descendants.size(); i++) {
						String name = descendants.get(i);
						if (PartsList.getFunctionalNameByLineageName(name)!=null)
							descendants.set(i, name+" ("+
									PartsList.getFunctionalNameByLineageName(name)+
									")");
					}
					for (String name : descendants) {
						if (!searchResultsList.contains(name))
							searchResultsList.add(name);
					}
				}
				
				if (cellTicked) {
					ArrayList<String> cellsCopy = new ArrayList<String>();
					for (String name : cells) {
						if (PartsList.getFunctionalNameByLineageName(name)!=null)
							name += " ("+
									PartsList.getFunctionalNameByLineageName(name)+
									")";
						cellsCopy.add(name);
					}
					for (String name : cellsCopy) {
						if (!searchResultsList.contains(name))
							searchResultsList.add(name);
					}
				}
				
				if (ancestorTicked) {
					ArrayList<String> ancestors = getAncestorsList(cells);
					for (int i=0; i<ancestors.size(); i++) {
						String name = ancestors.get(i);
						if (PartsList.getFunctionalNameByLineageName(name)!=null)
							ancestors.set(i, name+" ("+
									PartsList.getFunctionalNameByLineageName(name)+
									")");
					}
					for (String name : ancestors) {
						if (!searchResultsList.contains(name))
							searchResultsList.add(name);
					}
				}
			}
		}
	}
	
	public Service<Void> getResultsUpdateService() {
		return resultsUpdateService;
	}
	
	private int getCountFinal(int count) {
		final int out = count;
		return out;
	}
	
	private final class ShowLoadingService extends Service<Void>{
		@Override
		protected final Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					final int modulus = 5;
					while (true) {
						if (isCancelled()) {
							break;
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								searchResultsList.clear();
								String loading = "Fetching data from WormBase";
								int num = getCountFinal(count)%modulus;
								switch (num) {
									case 1:		loading+=".";
												break;
									case 2:		loading+="..";
												break;
									case 3:		loading+="...";
												break;
									case 4:		loading+="....";
												break;
									default:	break;
								}
								searchResultsList.add(loading);
							}
						});
						try {
							Thread.sleep(WAIT_TIME_MILLI);
							count++;
							if (count<0)
								count=0;
						} catch (InterruptedException ie) {
							break;
						}
					}
					return null;
				}
			};
		}
	}
	
	private static final long WAIT_TIME_MILLI = 750;
}
