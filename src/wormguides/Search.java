package wormguides;

import java.util.ArrayList;
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
import wormguides.model.CasesLists;
import wormguides.model.Connectome;
import wormguides.model.LineageData;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;
import wormguides.model.ProductionInfo;
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

	// used for adding shape rules
	private static SceneElementsList sceneElementsList;

	// for connectome searching
	private static Connectome connectome;
	private static boolean presynapticTicked;
	private static boolean postsynapticTicked;
	private static boolean electricalTicked;
	private static boolean neuromuscularTicked;

	// for cell cases searching
	private static CasesLists cases;

	// for production info searching
	private static ProductionInfo productionInfo;

	// for lineage searching
	private static LineageData lineageData;

	static {
		activeLineageNames = new ArrayList<String>();
		functionalNames = PartsList.getFunctionalNames();
		descriptions = PartsList.getDescriptions();

		type = SearchType.LINEAGE;

		selectedColor = Color.WHITE;

		searchResultsList = FXCollections.observableArrayList();
		nameComparator = new CellNameComparator();
		searchedText = "";

		// cell nucleus search type default to true
		cellNucleusTicked = true;
		cellBodyTicked = false;
		ancestorTicked = false;
		descendantTicked = false;

		// connectome synapse types all unchecked at init
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
			geneSearchService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					showLoadingService.cancel();
					searchResultsList.clear();
				}
			});

			geneSearchService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					showLoadingService.cancel();
					searchResultsList.clear();
					updateGeneResults();

					String searched = WormBaseQuery.getSearchedText();
					geneSearchQueue.remove(searched);
					for (Rule rule : rulesList) {
						if (rule.getSearchedText().contains("'" + searched + "'")) {
							rule.setCells(geneSearchService.getValue());
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

	/*
	 * Context menu controller always uses lineageName
	 */
	public static Rule addGiantConnectomeColorRule(String cellName, Color color, boolean isPresynaptic,
			boolean isPostsynaptic, boolean isElectrical, boolean isNeuromuscular) {

		StringBuilder sb = new StringBuilder("'");
		sb.append(cellName).append("' Connectome");

		ArrayList<String> types = new ArrayList<String>();
		if (isPresynaptic)
			types.add("presynaptic");
		if (isPostsynaptic)
			types.add("postsynaptic");
		if (isElectrical)
			types.add("electrical");
		if (isNeuromuscular)
			types.add("neuromuscular");
		if (!types.isEmpty()) {
			sb.append(" - ");

			for (int i = 0; i < types.size(); i++) {
				sb.append(types.get(i));

				if (i != types.size() - 1)
					sb.append(", ");
			}
		}

		Rule tempRule = new Rule(sb.toString(), color, SearchType.CONNECTOME, SearchOption.CELLNUCLEUS);
		tempRule.setCells(connectome.queryConnectivity(cellName, isPresynaptic, isPostsynaptic, isElectrical,
				isNeuromuscular, true));
		tempRule.setSearchedText(cellName);
		tempRule.resetLabel(sb.toString());

		rulesList.add(tempRule);
		return tempRule;
	}

	public static Rule addConnectomeColorRule(String searched, Color color, boolean isPresynaptic,
			boolean isPostsynaptic, boolean isElectrical, boolean isNeuromuscular) {
		boolean tempPresyn = presynapticTicked;
		boolean tempPostsyn = postsynapticTicked;
		boolean tempElectr = electricalTicked;
		boolean tempNeuro = neuromuscularTicked;

		presynapticTicked = isPresynaptic;
		postsynapticTicked = isPostsynaptic;
		electricalTicked = isElectrical;
		neuromuscularTicked = isNeuromuscular;

		Rule rule = addColorRule(SearchType.CONNECTOME, searched, color, SearchOption.CELLNUCLEUS);

		presynapticTicked = tempPresyn;
		postsynapticTicked = tempPostsyn;
		electricalTicked = tempElectr;
		neuromuscularTicked = tempNeuro;

		return rule;
	}

	public static void setActiveLineageNames(ArrayList<String> names) {
		activeLineageNames = names;
	}

	private static void updateGeneResults() {
		ArrayList<String> results = geneSearchService.getValue();
		ArrayList<String> cellsForListView = new ArrayList<String>();

		if (results == null || results.isEmpty())
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
			if (PartsList.getFunctionalNameByLineageName(result) != null)
				result += " (" + PartsList.getFunctionalNameByLineageName(result) + ")";
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

	public static void setRulesList(ObservableList<Rule> list) {
		rulesList = list;
	}

	public boolean containsColorRule(Rule other) {
		for (Rule rule : rulesList) {
			if (rule.equals(other))
				return true;
		}
		return false;
	}

	public EventHandler<ActionEvent> getColorPickerListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectedColor = ((ColorPicker) event.getSource()).getValue();
			}
		};
	}

	public static void addDefaultColorRules() {
		addColorRule(SearchType.FUNCTIONAL, "ash", Color.DARKSEAGREEN, SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "rib", Color.web("0x663366"), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "avg", Color.web("0xb41919"), SearchOption.CELLBODY);

		addColorRule(SearchType.FUNCTIONAL, "dd", Color.web("0x4a24c1", 0.60), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "da", Color.web("0xc56002"), SearchOption.CELLBODY);

		addColorRule(SearchType.FUNCTIONAL, "rivl", Color.web("0xff9966"), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "rivr", Color.web("0xffe6b4"), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "sibd", Color.web("0xe6ccff"), SearchOption.CELLBODY);
		addColorRule(SearchType.FUNCTIONAL, "siav", Color.web("0x99b3ff"), SearchOption.CELLBODY);

		addColorRule(SearchType.FUNCTIONAL, "dd1", Color.web("0xb30a95"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "dd2", Color.web("0xb30a95"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "dd3", Color.web("0xb30a95"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "dd4", Color.web("0xb30a95"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "dd5", Color.web("0xb30a95"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "dd6", Color.web("0xb30a95"), SearchOption.CELLNUCLEUS);

		addColorRule(SearchType.FUNCTIONAL, "da2", Color.web("0xe6b34d"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "da3", Color.web("0xe6b34d"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "da4", Color.web("0xe6b34d"), SearchOption.CELLNUCLEUS);
		addColorRule(SearchType.FUNCTIONAL, "da5", Color.web("0xe6b34d"), SearchOption.CELLNUCLEUS);
	}

	public void clearRules() {
		rulesList.clear();
	}

	public static ObservableList<Rule> getRules() {
		return rulesList;
	}

	public static Rule addMulticellularStructureRule(String searched, Color color) {
		return addColorRule(null, searched, color, SearchOption.MULTICELLULAR_NAME_BASED);
	}

	public static Rule addColorRule(SearchType type, String searched, Color color, SearchOption... options) {
		ArrayList<SearchOption> optionsArray = new ArrayList<SearchOption>();
		for (SearchOption option : options)
			optionsArray.add(option);
		return addColorRule(type, searched, color, optionsArray);
	}

	public static Rule addColorRule(SearchType searchType, String searched, Color color,
			ArrayList<SearchOption> options) {
		SearchType tempType = type;
		type = searchType;
		Rule rule = addColorRule(searched, color, options);
		type = tempType;
		return rule;
	}

	private static Rule addColorRule(String searched, Color color, ArrayList<SearchOption> options) {
		// default search options is cell
		if (options == null) {
			options = new ArrayList<SearchOption>();
			options.add(SearchOption.CELLNUCLEUS);
		}

		String label = "";
		searched = searched.toLowerCase();
		searched = searched.trim();
		if (type != null) {
			switch (type) {

			case LINEAGE:
				label = LineageTree.getCaseSensitiveName(searched);
				if (label.isEmpty())
					label = searched;
				break;

			case FUNCTIONAL:
				label = "'" + searched + "' Functional";
				break;

			case DESCRIPTION:
				label = "'" + searched + "' \"PartsList\" Description";
				break;

			case GENE:
				geneSearchQueue.add(searched);
				label = "'" + searched + "' Gene";
				break;

			case CONNECTOME:
				label = "'" + searched + "' Connectome";
				break;

			case MULTICELLULAR_CELL_BASED:
				label = "'" + searched + "' Multicellular Structure";
				break;

			case NEIGHBOR:
				label = "'" + searched + "' Neighbors";
				break;

			default:
				label = searched;
				break;
			}
		} else { // if search type is null, then rule was a multicellular
					// structure rule
			label = searched;
		}

		Rule rule = new Rule(label, color, type, options);

		ArrayList<String> cells;

		/**
		 * TODO Why is the search done twice? If the color rule is being added,
		 * the gene search results have populated the list view
		 */
		// if (type == SearchType.GENE) {
		// if (searchResultsList.isEmpty()) {
		// WormBaseQuery.doSearch(searched);
		// }
		// }
		// else {
		cells = getCellsList(searched);
		rule.setCells(cells);
		// }

		rulesList.add(rule);
		searchResultsList.clear();

		return rule;
	}

	private static ArrayList<String> getCellsList(String searched) {
		ArrayList<String> cells = new ArrayList<String>();
		searched = searched.toLowerCase();

		if (type != null) {
			switch (type) {
			case LINEAGE:
				for (String name : activeLineageNames) {
					if (name.toLowerCase().equals(searched))
						cells.add(name);
				}
				break;

			case FUNCTIONAL:
				String name;
				for (int i = 0; i < functionalNames.size(); i++) {
					name = functionalNames.get(i);
					if (name.toLowerCase().startsWith(searched))
						cells.add(PartsList.getLineageNameByIndex(i));
				}
				break;

			case DESCRIPTION:
				// TODO some cells with the searched term are not showing up in
				// results list
				// this is because some cells have the same description and it
				// gives the first one found

				// FIXED ^^ ???

				// for searching with multiple terms, perform individual
				// searches
				// and return the intersection of the hits
				ArrayList<ArrayList<String>> hits = new ArrayList<ArrayList<String>>();
				String[] keywords = searched.split(" ");
				for (String keyword : keywords) {
					ArrayList<String> results = new ArrayList<String>();
					for (int i = 0; i < descriptions.size(); i++) {
						String textLowerCase = descriptions.get(i).toLowerCase();

						// look for match
						if (textLowerCase.indexOf(keyword.toLowerCase()) >= 0) {
							// get cell name that corresponds to matching
							// description
							String cell = PartsList.getLineageNameByIndex(i);

							// only add new entries
							if (!results.contains(cell)) {
								results.add(cell);
							}
						}
					}
					// add the results to the hits
					hits.add(results);
				}

				// find the intersection among the results --> using the first
				// list
				// to find matches
				if (hits.size() > 0) {
					ArrayList<String> results = hits.get(0);
					for (int k = 0; k < results.size(); k++) {
						String cell = results.get(k);

						// look for a match in rest of the hits
						boolean intersection = true;
						for (int i = 1; i < hits.size(); i++) {
							if (!hits.get(i).contains(cell))
								intersection = false;
						}

						if (intersection && !cells.contains(cell))
							cells.add(cell);
					}
				}

				break;

			case GENE:
				if (isGeneFormat(getSearchedText())) {
					showLoadingService.restart();
					WormBaseQuery.doSearch(getSearchedText());
					cells = new ArrayList<String>(searchResultsList);
				}
				break;

			case MULTICELLULAR_CELL_BASED:
				if (sceneElementsList != null) {
					for (SceneElement se : sceneElementsList.getList()) {
						if (se.isMulticellular()) {
							if (isNameSearched(se.getSceneName(), searched)) {
								for (String cellName : se.getAllCellNames()) {
									if (!cells.contains(cellName))
										cells.add(cellName);
								}
							}
						}
					}
				}
				break;

			case CONNECTOME:
				if (connectome != null) {
					cells.addAll(connectome.queryConnectivity(searched, presynapticTicked, postsynapticTicked,
							electricalTicked, neuromuscularTicked, true));
					// TODO is the cell itself ever in the wiring results?
					// cells.remove(searched);
				}
				break;

			case NEIGHBOR:
				cells.addAll(getNeighbors(searched));
			}
		}
		return cells;
	}

	// Tests if name contains all parts of a search string
	// returns true if it does, false otherwise
	private static boolean isNameSearched(String name, String searched) {
		if (name == null || searched == null)
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
		if (name.indexOf("-") != -1) {
			try {
				Integer.parseInt(name.substring(name.indexOf("-") + 1));
				return true;
			} catch (NumberFormatException e) {
				// Don't do anything if the suffix is not a number
			}
		}
		return false;
	}

	/*
	 * non terminal cell case will use this search to find the terminal
	 * descendants for a given cell
	 */
	public static ArrayList<String> getDescendantsList(String queryCell) {
		ArrayList<String> descendants = new ArrayList<String>();
		if (queryCell != null) {
			for (String name : PartsList.getLineageNames()) {
				if (!descendants.contains(name) && LineageTree.isDescendant(name, queryCell)) {

					/*
					 * 
					 */

					descendants.add(name);
				}
			}
		}
		return descendants;
	}

	// Generates a list of descendants of all cells in input
	private static ArrayList<String> getDescendantsList(ArrayList<String> cells) {
		ArrayList<String> descendants = new ArrayList<String>();

		if (cells == null)
			return descendants;

		// Special cases for 'ab' and 'p0' because the input list of cells would
		// be empty
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

		if (cells == null)
			return ancestors;

		for (String cell : cells) {
			for (String name : activeLineageNames) {
				if (!ancestors.contains(name) && LineageTree.isAncestor(name, cell))
					ancestors.add(name);
			}
		}

		return ancestors;
	}

	/**
	 * Neighbor search mode: given cell: - find time range of cell - for each
	 * time point - find its nearest neighbor and compute d = distance to
	 * neighbor - d = root((x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2) - multiple d by
	 * 1.5 - for position in positionsAtTime - compute d1 = distance from query
	 * cell position to position - if d1 is <= d - if cell is not in results -
	 * add cell - highlight results over lifetime of cell.
	 * 
	 * @param cellName
	 *            The String containing the lineage name of the queried cell
	 * @return An {@link ArrayList} of Strings containing the cell lineage names
	 *         of neighboring cells to cell with input cell lineage name
	 */
	public static ArrayList<String> getNeighbors(String cellName) {
		ArrayList<String> results = new ArrayList<String>();

		if (cellName == null || !lineageData.isCellName(cellName))
			return results;

		// get time range for cell
		int firstOccurence = lineageData.getFirstOccurrenceOf(cellName);
		int lastOccurence = lineageData.getLastOccurrenceOf(cellName);

		for (int i = firstOccurence; i <= lastOccurence; i++) {
			String[] names = lineageData.getNames(i);
			Integer[][] positions = lineageData.getPositions(i);

			// find the coordinates of the query cell
			int queryIDX = -1;
			int x = -1;
			int y = -1;
			int z = -1;
			for (int j = 0; j < names.length; j++) {
				if (names[j].toLowerCase().equals(cellName.toLowerCase())) {
					queryIDX = j;
					x = positions[j][0];
					y = positions[j][1];
					z = positions[j][2];
					// System.out.println(x + ", " + y + ", " + z);
				}
			}

			// find nearest neighbor
			if (x != -1 && y != -1 && z != -1) {
				// int nearestNeighborIDX = -1; --> debugging
				double distance = Double.MAX_VALUE;
				for (int k = 0; k < positions.length; k++) {
					if (k != queryIDX) {
						double distanceFromQuery = distance(x, positions[k][0], y, positions[k][1], z, positions[k][2]);
						if (distanceFromQuery < distance) {
							distance = distanceFromQuery;
							// nearestNeighborIDX = k; --> debugging
						}
					}
				}

				// multiple distance by 1.5
				if (distance != Double.MAX_VALUE) {
					distance *= 1.5;
				}

				// find all cells within d*1.5 range
				for (int n = 0; n < positions.length; n++) {
					// compute distance from each cell to query cell
					if (distance(x, positions[n][0], y, positions[n][1], z, positions[n][2]) <= distance) {
						// only add new entries
						if (!results.contains(names[n]) && !names[n].equalsIgnoreCase(cellName))
							results.add(names[n]);
					}
				}
			}
		}

		return results;
	}

	private static double distance(int x1, int x2, int y1, int y2, int z1, int z2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) + Math.pow((z2 - z1), 2));
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
					options.add(SearchOption.CELLNUCLEUS);
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
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				cellNucleusTicked = newValue;
			}
		};
	}

	public ChangeListener<Boolean> getCellBodyTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				cellBodyTicked = newValue;
			}
		};
	}

	public ChangeListener<Boolean> getAncestorTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ancestorTicked = newValue;
				if (type == SearchType.GENE)
					updateGeneResults();
				else
					resultsUpdateService.restart();
			}
		};
	}

	public ChangeListener<Boolean> getDescendantTickListner() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				descendantTicked = newValue;
				if (type == SearchType.GENE)
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
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
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
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
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

			if (cells == null)
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
								int num = getCountFinal(count) % modulus;
								switch (num) {
								case 1:
									loading += ".";
									break;
								case 2:
									loading += "..";
									break;
								case 3:
									loading += "...";
									break;
								case 4:
									loading += "....";
									break;
								default:
									break;
								}
								searchResultsList.add(loading);
							}
						});
						try {
							Thread.sleep(WAIT_TIME_MILLI);
							count++;
							if (count < 0)
								count = 0;
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
		if (list != null)
			sceneElementsList = list;
	}

	public static String getStructureComment(String name) {
		return sceneElementsList.getCommentByName(name);
	}

	public static boolean isStructureWithComment(String name) {
		if (sceneElementsList != null && (sceneElementsList.isMulticellStructureName(name)))
			return true;

		return false;
	}

	public static int getFirstOccurenceOf(String name) {
		if (lineageData != null && lineageData.isCellName(name))
			return lineageData.getFirstOccurrenceOf(name);

		else if (sceneElementsList != null && sceneElementsList.isSceneElementName(name))
			return sceneElementsList.getFirstOccurrenceOf(name);

		return -1;
	}

	public static int getLastOccurenceOf(String name) {
		if (lineageData != null && lineageData.isCellName(name))
			return lineageData.getLastOccurrenceOf(name);

		else if (sceneElementsList != null && sceneElementsList.isSceneElementName(name))
			return sceneElementsList.getLastOccurrenceOf(name);

		return -1;
	}

	public static void setConnectome(Connectome con) {
		if (con != null) {
			connectome = con;
		}
	}

	public static void setCases(CasesLists c) {
		if (c != null) {
			cases = c;
		}
	}

	public static void setProductionInfo(ProductionInfo pi) {
		if (pi != null) {
			productionInfo = pi;
		}
	}

	public static void setLineageData(LineageData ld) {
		if (ld != null) {
			lineageData = ld;
		}
	}

	public static boolean hasCellCase(String cellName) {
		if (cases != null) {
			return cases.hasCellCase(cellName);
		}
		return false;
	}

	/*
	 * WHERE ELSE CAN WE PUT THIS --> ONLY PLACE TO REFERENCE CELL CASES IN
	 * STATIC WAY
	 */
	public static void removeCellCase(String cellName) {
		if (cases != null && cellName != null) {
			cases.removeCellCase(cellName);
		}
	}

	public static void addToInfoWindow(AnatomyTerm term) {
		if (term.equals(AnatomyTerm.AMPHID_SENSILLA)) {
			if (!cases.containsAnatomyTermCase(term.getTerm())) {
				cases.makeAnatomyTermCase(term);
			}
		}
	}

	/*
	 * Method taken from RootLayoutController --> how can
	 * InfoWindowLinkController generate page without ptr to
	 * RootLayoutController
	 */
	public static void addToInfoWindow(String name) {
		// service.restart();

		// update to lineage name if function
		String lineage = PartsList.getLineageNameByFunctionalName(name);
		if (lineage != null) {
			name = lineage;
		}

		// GENERATE CELL TAB ON CLICK
		if (name != null && !name.isEmpty()) {
			if (cases == null || productionInfo == null) {
				return; // error check
			}

			if (PartsList.containsLineageName(name)) {
				if (cases.containsCellCase(name)) {

					// show the tab
				} else {
					// translate the name if necessary
					String funcName = connectome.checkQueryCell(name).toUpperCase();
					// add a terminal case --> pass the wiring partners
					cases.makeTerminalCase(name, funcName,
							connectome.queryConnectivity(funcName, true, false, false, false, false),
							connectome.queryConnectivity(funcName, false, true, false, false, false),
							connectome.queryConnectivity(funcName, false, false, true, false, false),
							connectome.queryConnectivity(funcName, false, false, false, true, false),
							productionInfo.getNuclearInfo(), productionInfo.getCellShapeData(name));
				}
			} else { // not in connectome --> non terminal case
				if (cases.containsCellCase(name)) {

					// show tab
				} else {
					// add a non terminal case
					cases.makeNonTerminalCase(name, productionInfo.getNuclearInfo(),
							productionInfo.getCellShapeData(name));
				}
			}
		}
	}

	// connectome checkbox listeners
	public ChangeListener<Boolean> getPresynapticTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				presynapticTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}

	public ChangeListener<Boolean> getPostsynapticTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				postsynapticTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}

	public ChangeListener<Boolean> getElectricalTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				electricalTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}

	public ChangeListener<Boolean> getNeuromuscularTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				neuromuscularTicked = newValue;
				resultsUpdateService.restart();
			}
		};
	}

	private static final long WAIT_TIME_MILLI = 750;
}
