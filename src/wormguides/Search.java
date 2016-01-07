package wormguides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

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
import wormguides.model.Connectome;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;
import wormguides.model.Rule;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;

public class Search {

	private static ArrayList<String> activeLineageNames;
	private static ArrayList<String> functionalNames;
	private static ArrayList<String> descriptions;
	
	private static ObservableList<String> searchResultsList;
	private static Comparator<String> nameComparator;
	private static String searchedText;
	private BooleanProperty clearSearchFieldProperty;
	
	private static SearchType type;
	
	private static boolean cellNucleusTicked;
	private static boolean cellBodyTicked;
	private static boolean ancestorTicked;
	private static boolean descendantTicked;
	private static ObservableList<Rule> rulesList;
	private static Color selectedColor;
	
	private final static Service<Void> resultsUpdateService;
	private final static Service<ArrayList<String>> geneSearchService;
	
	private static BooleanProperty geneResultsUpdated;
	
	private final static Service<Void> showLoadingService;
	// count used to display ellipsis when gene search is running
	private static int count;
	private static LinkedList<String> geneSearchQueue;
	
	//used for adding shape rules
	private static SceneElementsList sceneElementsList;
	
	//for connectome searching
	private static Connectome connectome;
	private static boolean presynapticTicked;
	private static boolean postsynapticTicked;
	private static boolean electricalTicked;
	private static boolean neuromuscularTicked;

		
	static {
		activeLineageNames = new ArrayList<String>();
		functionalNames = PartsList.getFunctionalNames();
		descriptions = PartsList.getDescriptions();
		
		type = SearchType.SYSTEMATIC;
		
		selectedColor = Color.WHITE;
		
		searchResultsList = FXCollections.observableArrayList();
		nameComparator = new CellNameComparator();
		searchedText = "";
		
		// cell nucleus search type default to true
		cellNucleusTicked = true;
		cellBodyTicked = false;
		ancestorTicked = false;
		descendantTicked = false;	
		
		//connectome synapse types all unchecked at init
		presynapticTicked = false;
		postsynapticTicked = false;
		electricalTicked = false;
		neuromuscularTicked = false;
		
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
			geneSearchService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					showLoadingService.cancel();
					searchResultsList.clear();
					updateGeneResults();
					
					String searched = WormBaseQuery.getSearchedText();
					geneSearchQueue.remove(searched);
					for (Rule rule : rulesList) {
						if (rule instanceof ColorRule) {
							if (rule.getSearchedText().contains("'"+searched+"'")) {
								rule.setCells(geneSearchService.getValue());
							}
						}
					}
					geneResultsUpdated.set(!geneResultsUpdated.get());
					
					if (!geneSearchQueue.isEmpty())
						WormBaseQuery.doSearch(geneSearchQueue.pop());
				}
			});
		}
		
		geneSearchQueue = new LinkedList<String>();
		count = 0;	
		geneResultsUpdated = new SimpleBooleanProperty(false);
		
	}
	
	
	public static void setActiveLineageNames(ArrayList<String> names) {
		activeLineageNames = names;
	}
	
	
	private static void updateGeneResults() {
		ArrayList<String> results = geneSearchService.getValue();
		ArrayList<String> cellsForListView = new ArrayList<String>();
		
		if (results==null || results.isEmpty())
			return;
		
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
		
		searchResultsList.sort(nameComparator);
		appendFunctionalToLineageNames(cellsForListView);
		geneResultsUpdated.set(!geneResultsUpdated.get());
	}
	
	private static void appendFunctionalToLineageNames(ArrayList<String> list) {
		searchResultsList.clear();
		for (String result : list) {
			if (PartsList.getFunctionalNameByLineageName(result)!=null)
				result += " ("+
						PartsList.getFunctionalNameByLineageName(result)+
						")";
			searchResultsList.add(result);
		}
	}
	
	
	public static boolean isLineageName(String name) {
		return activeLineageNames.contains(name);
	}
	
	
	private static String getSearchedText() {
		final String searched = searchedText;
		return searched;
	}
	
	
	public void setRulesList(ObservableList<Rule> list) {
		rulesList = list;
	}
	
	
	public boolean containsColorRule(ColorRule other) {
		for (Rule rule : rulesList) {
			if (rule instanceof ColorRule) {
				if (rule.equals(other))
					return true;
			}
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
	
	
	public void addDefaultColorRules() {
		addColorRule(SearchType.FUNCTIONAL, "ash", Color.DARKSEAGREEN, SearchOption.CELL, SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "rib", Color.web("0x663366"), SearchOption.CELL, SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "avg", Color.web("0xb31a1a"), SearchOption.CELL, SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "dd",  Color.web("0x4a24c1", 0.60), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "da", Color.web("0xe6b34d"), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "dd",  Color.web("0x4a24c1"), SearchOption.CELL);
		addColorRule(SearchType.FUNCTIONAL, "da", Color.web("0xe6b34d"), SearchOption.CELL);
	}
	
	
	public void clearRules() {
		rulesList.clear();
	}
	
	
	private void addColorRule(String searched, Color color, SearchOption...options) {
		addColorRule(searched, color, new ArrayList<SearchOption>(Arrays.asList(options)));
	}
	
	
	public static void addColorRule(SearchType type, String searched, Color color, SearchOption...options) {
		ArrayList<SearchOption> optionsArray = new ArrayList<SearchOption>();
		for (SearchOption option : options)
			optionsArray.add(option);
		addColorRule(type, searched, color, optionsArray);
	}
	
	
	public static void addColorRule(SearchType type, String searched, Color color, 
													ArrayList<SearchOption> options) {
		SearchType tempType = Search.type;
		Search.type = type;
		addColorRule(searched, color, options);
		Search.type = tempType;
	}
	
	
	private static void addColorRule(String searched, Color color, ArrayList<SearchOption> options) {
		// default search options is cell and descendant
		if (options==null)
			options = new ArrayList<SearchOption>();
		
		String label = "";
		searched = searched.toLowerCase();
		searched = searched.trim();
		switch (type) {
			case SYSTEMATIC:
							label = LineageTree.getCaseSensitiveName(searched);
							if (label.isEmpty())
								label = searched;
							break;
							
			case FUNCTIONAL:
							label = "'"+searched+"' Functional Name";
							break;
							
			case DESCRIPTION:
							label = "'"+searched+"' \"PartsList\" Description";
							break;
							
			case GENE:		
							geneSearchQueue.add(searched);
							label = "'"+searched+"' Gene";
							break;
							
			case CONNECTOME:
							label = "'"+searched+"' Connectome";
							break;
							
			case MULTICELL:
							label = "'"+searched+"' Multicellular Structure";
							break;
							
			default:
							label = searched;
							break;
		}
		
		ColorRule rule = new ColorRule(label, color, options, type);
		
		ArrayList<String> cells;
		if (type==SearchType.GENE) {
			WormBaseQuery.doSearch(searched);
		} else if (type == SearchType.CONNECTOME) { //separate case for name translation purposes (see setConnectomeRuleCells())
			//cells = setConnectomeRuleCells(searched);
			cells = getCellsList(searched);
			rule.setCells(cells);
		} else {
			cells = getCellsList(searched);
			rule.setCells(cells);
		}
		
		rulesList.add(rule);
		searchResultsList.clear();
	}
	
	/*
	 * Method which returns the cells which pertain to a connectome rule
	 * If a systematic name is searched in the connectome e.g. ABa, the query is translated to a functional name prior to adding cells
	 * If a functional name is searched in the connectome e.g. ASAL, no translation is done
	 */
	// TODO Look into this
//	private static ArrayList<String> setConnectomeRuleCells(String searched) {
//		ArrayList<String> cells = new ArrayList<String>();
//		
//		//if a systematic name is searched, translate to functional before searching connectome
//		if (PartsList.containsLineageName(searched)) {
//			searched = PartsList.getFunctionalNameByLineageName(searched).toLowerCase();
//		}
//		searched = searched.toLowerCase();
//		for (String name : functionalNames) {
//			if (name.toLowerCase().startsWith(searched)) {
//				cells.add(PartsList.getLineageNameByFunctionalName(name));
//			}
//		}
//		return cells;
//	}
	
	
	private static ArrayList<String> getCellsList(String searched) {
		ArrayList<String> cells = new ArrayList<String> ();
		searched = searched.toLowerCase();
		switch (type) {
			case SYSTEMATIC:
							for (String name : activeLineageNames) {
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
							// TODO some cells with the searched term are not showing up in results list
							// this is because some cells have the same description and it 
							// gives the first one found
							//System.out.println("\nShowing found description names:");
							for (int i=0; i<descriptions.size(); i++) {
								String textLowerCase = descriptions.get(i).toLowerCase();
								String[] keywords = searched.split(" ");
								boolean found = true;
								for (String keyword : keywords) {
									if (textLowerCase.indexOf(keyword)<0) {
										found = false;
										break;
									}
								}
								
								if (found && !cells.contains(PartsList.getLineageNameByIndex(i)))
									cells.add(PartsList.getLineageNameByIndex(i));
							}
							
							break;
						 
			case GENE:		
							if (isGeneFormat(getSearchedText())) {
								showLoadingService.restart();
								WormBaseQuery.doSearch(getSearchedText());
							}
							break;
							
			case MULTICELL:	
							if (sceneElementsList != null) {
								for (SceneElement se : sceneElementsList.getList()) {
									if (se.isMulticellular()) {
										if (isNameSearched(se.getSceneName(), searched))
											cells.addAll(se.getAllCellNames());
									}
								}
							}
							break;
							
			case CONNECTOME:
							if (connectome != null) {
								//if a systematic name is searched, translate to functional before searching connectome
								if (PartsList.containsLineageName(searched)) {
									searched = PartsList.getFunctionalNameByLineageName(searched).toLowerCase();
								}
								cells.addAll(connectome.querryConnectivity(searched, presynapticTicked,
										postsynapticTicked, electricalTicked, neuromuscularTicked));
							}
							break;
		}
		return cells;
	}
	
	
	// Tests if name contains all parts of a search string
	// returns true if it does, false otherwise
	// TODO extend functionality to search comments
	private static boolean isNameSearched(String name, String searched) {
		if (name==null || searched==null)
			return false;
		
		// search in structure scene names
		String nameLower = name.toLowerCase();
		
		boolean appliesToName = true;
		boolean appliesToComment = true;
		
		String[] terms = searched.trim().toLowerCase().split(" ");
		
		for (String term : terms) {
			if (!nameLower.contains(term)) {
				appliesToName = false;
				break;
			}
		}
		
		// search in comments if name does not already apply
		String comment = sceneElementsList.nameCommentsMap.get(nameLower);
		String commentLower = comment.toLowerCase();
		for (String term : terms) {
			if (!commentLower.contains(term)) {
				appliesToComment = false;
				break;
			}
		}
		
		return appliesToName || appliesToComment;
	}
	
	
	// Returns true if name is a gene name, false otherwise
	// (some string, -, some number)
	private static boolean isGeneFormat(String name) {
		if (name.indexOf("-")!=-1) {
			try {
				Integer.parseInt(name.substring(name.indexOf("-")+1));
				return true;
			} catch (NumberFormatException e) {
				// Don't do anything if the suffix is not a number
			}
		}
		return false;
	}
	
	
	// Generates a list of descendants of all cells in input
	private static ArrayList<String> getDescendantsList(ArrayList<String> cells) {
		ArrayList<String> descendants = new ArrayList<String>();
		
		if (cells==null)
			return descendants;
		
		// Special cases for 'ab' and 'p0' because the input list of cells would be empty
		String searched = searchedText.toLowerCase();
		if (searched.equals("ab") || searched.equals("p0")) {
			for (String name : activeLineageNames) {
				if (!descendants.contains(name) && LineageTree.isDescendant(name, searched))
					descendants.add(name);
			}
		}
		
		for (String cell : cells) {
			for (String name : activeLineageNames) {
				if (!descendants.contains(name) && LineageTree.isDescendant(name, cell))
					descendants.add(name);
			}
		}
		
		return descendants;
	}
	
	
	// generates a list of ancestors of all cells in input
	private static ArrayList<String> getAncestorsList(ArrayList<String> cells) {
		ArrayList<String> ancestors = new ArrayList<String>();
		
		if (cells==null)
			return ancestors;
		
		for (String cell : cells) {
			for (String name : activeLineageNames) {
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
				if (cellNucleusTicked)
					options.add(SearchOption.CELL);
				if (cellBodyTicked)
					options.add(SearchOption.CELLBODY);
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
	
	
	public ChangeListener<Boolean> getCellNucleusTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellNucleusTicked = newValue;
			}
		};
	}
	
	
	public ChangeListener<Boolean> getCellBodyTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellBodyTicked = newValue;
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
	
	
	private static void refreshSearchResultsList(String newValue) {
		String searched = newValue.toLowerCase();
		if (!searched.isEmpty()) {
			ArrayList<String> cells;
			cells = getCellsList(searched);
			
			if (cells==null)
				return;
			
			ArrayList<String> cellsForListView = new ArrayList<String>();
			cellsForListView.addAll(cells);

			if (descendantTicked) {
				ArrayList<String> descendants = getDescendantsList(cells);
				for (String name : descendants) {
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

			cellsForListView.sort(nameComparator);
			appendFunctionalToLineageNames(cellsForListView);
		}
	}
	
	
	public Service<Void> getResultsUpdateService() {
		return resultsUpdateService;
	}
	
	
	private static int getCountFinal(int count) {
		final int out = count;
		return out;
	}
	
	
	private static final class CellNameComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			return s1.compareTo(s2);
		}
	}
	
	
	private static final class ShowLoadingService extends Service<Void> {
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
	
	
	public static void setSceneElementsList(SceneElementsList list) {
		if (list!=null)
			sceneElementsList = list;
	}
	
	
	public static String getStructureComment(String name) {
		return sceneElementsList.getCommentByName(name);
	}
	
	
	public static boolean isStructureWithComment(String name) {
		if (sceneElementsList!=null && (sceneElementsList.isMulticellStructureName(name)
				|| sceneElementsList.isNoteStructureName(name)))
			return true;
		
		return false;
	}
	
	public static void setConnectome(Connectome con) {
		if (con != null) {
			connectome = con;
		}
	}
	
	//connectome checkbox listeners
	public ChangeListener<Boolean> getPresynapticTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				presynapticTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}
	
	public ChangeListener<Boolean> getPostsynapticTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				postsynapticTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}
	
	public ChangeListener<Boolean> getElectricalTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				electricalTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}
	
	public ChangeListener<Boolean> getNeuromuscularTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				neuromuscularTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}


	private static final long WAIT_TIME_MILLI = 750;
}
