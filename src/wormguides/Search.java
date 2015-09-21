package wormguides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Toggle;
import javafx.scene.paint.Color;
import wormguides.model.ColorRule;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;

public class Search {

	private static ArrayList<String> lineageNames;
	private static ArrayList<String> functionalNames;
	private static ArrayList<String> descriptions;
	
	private ObservableList<String> searchResultsList;
	private static String searchedText;
	private BooleanProperty clearSearchFieldProperty;
	
	private static SearchType type;
	
	private boolean cellTicked;
	private boolean ancestorTicked;
	private boolean descendantTicked;
	
	private static ObservableList<ColorRule> rulesList;
	private Color selectedColor;
	
	private final Service<Void> resultsUpdateService;
	private final Service<ArrayList<String>> geneSearchService;
	
	private BooleanProperty geneResultsUpdated;
	
	private final Service<Void> showLoadingService;
	private int count;
	
	public Search() {
		lineageNames = new ArrayList<String>(Arrays.asList(
							AceTreeLoader.getAllCellNames()));
		functionalNames = PartsList.getFunctionalNames();
		descriptions = PartsList.getDescriptions();
		
		type = SearchType.SYSTEMATIC;
		
		selectedColor = Color.WHITE;
		
		searchResultsList = FXCollections.observableArrayList();
		searchedText = "";
		
		cellTicked = false;
		ancestorTicked = false;
		descendantTicked = false;
		
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
		
		showLoadingService = new ShowLoadingService();
		
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
					updateGeneResults();
					geneResultsUpdated.set(!geneResultsUpdated.get());
				}
			});
		}
		
		count = 0;
		
		geneResultsUpdated = new SimpleBooleanProperty(false);
	}
	
	private void updateGeneResults() {
		if (geneSearchService.getValue()==null)
			return;
		
		ArrayList<String> results = geneSearchService.getValue();
		ArrayList<String> cellsForListView = new ArrayList<String>();
		
		if (results.isEmpty())
			searchResultsList.add("No results found from WormBase");
		
		else {
			if (!cellTicked && !ancestorTicked && !descendantTicked)
				cellsForListView.addAll(results);
			else {
				if (cellTicked)
					cellsForListView.addAll(results);
				if (ancestorTicked) {
					ArrayList<String> ancestors = getAncestorsList(results);
					for (String name : ancestors) {
						if (!cellsForListView.contains(name))
							cellsForListView.add(name);
					}
				}
				if (descendantTicked) {
					ArrayList<String> descendants = getDescendantsList(results);
					for (String name : descendants) {
						if (!cellsForListView.contains(name))
							cellsForListView.add(name);
					}
				}
			}
		}
		
		searchResultsList.sort(new CellNameComparator());
		addFunctionalNamesToList(cellsForListView);
		geneResultsUpdated.set(!geneResultsUpdated.get());
	}
	
	private void addFunctionalNamesToList(ArrayList<String> list) {
		searchResultsList.clear();
		for (String result : list) {
			if (PartsList.getFunctionalNameByLineageName(result)!=null)
				result += " ("+
						PartsList.getFunctionalNameByLineageName(result)+
						")";
			searchResultsList.add(result);
		}
	}
	
	private static String getSearchedText() {
		final String searched = searchedText;
		return searched;
	}
	
	public void setRulesList(ObservableList<ColorRule> rulesListArg) {
		rulesList = rulesListArg;
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
	
	public static void setColorRulesCells(ArrayList<ColorRule> rules) {
		
	}
	
	public void addDefaultRules() {
		addColorRule("ABa", Color.RED, SearchOption.CELL, SearchOption.DESCENDANT);
		addColorRule("ABp", Color.BLUE, SearchOption.CELL, SearchOption.DESCENDANT);
		addColorRule("EMS", Color.GREEN, SearchOption.CELL, SearchOption.DESCENDANT);
		addColorRule("P2", Color.YELLOW, SearchOption.ANCESTOR, 
					SearchOption.CELL, SearchOption.DESCENDANT);
	}
	
	public void clearColorRules() {
		// TODO add an 'are you sure?' popup window
		rulesList.clear();
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
		
		ColorRule rule = new ColorRule(label, color, options, type);
		
		ArrayList<String> cells;
		if (type==SearchType.GENE) {
			// TODO make sure correct cells are received when called from urlloader
			cells = geneSearchService.getValue();
		}
		else
			cells = getCellsList(searched);
		
		rule.setCells(cells);
		rulesList.add(rule);
	}
	
	public static void addCellsToEmptyRules() {
		SearchType prevType = type;
		for (ColorRule rule : rulesList) {
			if (!rule.areCellsSet()) {
				type = rule.getSearchType();
				rule.setCells(getCellsList(rule.getSearchedText()));
			}
		}
		type = prevType;
	}
	
	private static ArrayList<String> getCellsList(String searched) {
		ArrayList<String> cells = new ArrayList<String> ();
		searched = searched.toLowerCase();
		switch (type) {
			case SYSTEMATIC:
							for (String name : lineageNames) {
								if (name.toLowerCase().equals(searched))
									cells.add(name);
							}
							break;
						
			case FUNCTIONAL:
							for (String name : functionalNames) {
								if (name.toLowerCase().startsWith(searched))
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
								if (found && !cells.contains(PartsList.getLineageNameByIndex(
																descriptions.indexOf(text)))) {
									cells.add(PartsList.getLineageNameByIndex(
													descriptions.indexOf(text)));
								}
							}
							break;
						
			case GENE:		
							WormBaseQuery.doSearch(getSearchedText());
							return null;
		}
		
		return cells;
	}
	
	// generates a list of descendants of all cells in input
	private ArrayList<String> getDescendantsList(ArrayList<String> cells) {
		ArrayList<String> descendants = new ArrayList<String>();
		if (cells==null)
			return descendants;
		for (String cell : cells) {
			for (String name : lineageNames) {
				if (!descendants.contains(name) && LineageTree.isDescendant(name, cell))
					descendants.add(name);
			}
		}
		return descendants;
	}
	
	// generates a list of ancestors of all cells in input
	private ArrayList<String> getAncestorsList(ArrayList<String> cells) {
		ArrayList<String> ancestors = new ArrayList<String>();
		if (cells==null)
			return ancestors;
		for (String cell : cells) {
			for (String name : lineageNames) {
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
				
				addColorRule(getSearchedText(), selectedColor, options);
				
				searchResultsList.clear();
				if (clearSearchFieldProperty != null)
					clearSearchFieldProperty.set(true);
			}
		};
	}
	
	public ChangeListener<Boolean> getCellTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellTicked = newValue;
				if (type==SearchType.GENE)
					updateGeneResults();
				else
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
				if (type==SearchType.GENE)
					updateGeneResults();
				else
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
				if (type==SearchType.GENE)
					updateGeneResults();
				else
					resultsUpdateService.restart();
			}
		};
	}
	
	public void setClearSearchFieldProperty(BooleanProperty property) {
		clearSearchFieldProperty = property;
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
	
	public BooleanProperty getGeneResultsUpdated() {
		return geneResultsUpdated;
	}
	
	public ObservableList<String> getSearchResultsList() {
		return searchResultsList;
	}
	
	public ChangeListener<String> getTextFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				searchedText = newValue.toLowerCase();
				if (searchedText.isEmpty())
					searchResultsList.clear();
				else
					resultsUpdateService.restart();
			}
		};
	}
	
	private void refreshSearchResultsList(String newValue) {
		String searched = newValue.toLowerCase();
		if (!searched.isEmpty()) {
			ArrayList<String> cells;
			cells = getCellsList(searched);
			
			if (cells==null)
				return;
			
			ArrayList<String> cellsForListView = new ArrayList<String>();
			if (!cellTicked && !descendantTicked && !ancestorTicked) {
				if (type==SearchType.SYSTEMATIC) {
					for (String name : lineageNames) {
						if (name.toLowerCase().startsWith(searched))
							cellsForListView.add(name);
					}
				}					
				else
					cellsForListView.addAll(cells);
			}
			else {
				if (descendantTicked) {
					ArrayList<String> descendants = getDescendantsList(cells);
					for (String name : descendants) {
						if (!cellsForListView.contains(name))
							cellsForListView.add(name);
					}
				}
				if (cellTicked) {
					for (String name : cells) {
						if (!cellsForListView.contains(name))
							cellsForListView.add(name);
					}
				}
				if (ancestorTicked) {
					ArrayList<String> ancestors = getAncestorsList(cells);
					for (String name : ancestors) {
						if (!cellsForListView.contains(name))
							cellsForListView.add(name);
					}
				}
			}
			cellsForListView.sort(new CellNameComparator());
			addFunctionalNamesToList(cellsForListView);
		}
	}
	
	public Service<Void> getResultsUpdateService() {
		return resultsUpdateService;
	}
	
	private int getCountFinal(int count) {
		final int out = count;
		return out;
	}
	
	private final class CellNameComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			return s1.compareTo(s2);
		}
	}
	
	private final class ShowLoadingService extends Service<Void> {
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
