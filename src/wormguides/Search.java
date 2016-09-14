/*
 * Bao Lab 2016
 */

package wormguides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
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

import wormguides.layers.SearchType;
import wormguides.models.CasesLists;
import wormguides.models.Connectome;
import wormguides.models.LineageTree;
import wormguides.models.ProductionInfo;
import wormguides.models.Rule;
import wormguides.models.SceneElement;
import wormguides.models.SceneElementsList;

import acetree.lineagedata.LineageData;
import partslist.PartsList;

public class Search {

    private static final Service<Void> resultsUpdateService;
    private static final Service<ArrayList<String>> geneSearchService;
    private static final Service<Void> showLoadingService;

    /** Time between changes in the number of ellipses periods during loading */
    private static final long WAIT_TIME_MS = 750;
    /** Changing number of ellipses periods to display during loading */
    private static int count;

    private static ArrayList<String> activeLineageNames;
    private static ArrayList<String> functionalNames;
    private static ArrayList<String> descriptions;

    private static SceneElementsList sceneElementsList;

    private static ObservableList<String> searchResultsList;

    private static Comparator<String> nameComparator;

    private static String searchedText;
    private static BooleanProperty clearSearchFieldProperty;

    private static SearchType type;

    private static Color selectedColor;

    private static boolean cellNucleusTicked;
    private static boolean cellBodyTicked;
    private static boolean ancestorTicked;
    private static boolean descendantTicked;

    private static ObservableList<Rule> rulesList;

    private static BooleanProperty geneResultsUpdated;
    private static LinkedList<String> geneSearchQueue;

    // for connectome searching
    private static Connectome connectome;
    private static boolean presynapticTicked;
    private static boolean postsynapticTicked;
    private static boolean electricalTicked;
    private static boolean neuromuscularTicked;

    // for cell cases searching
    private static CasesLists cases;

    // for wiring partner click handling
    private static ProductionInfo productionInfo;

    private static WiringService wiringService;

    // for lineage searching
    private static LineageData lineageData;

    static {
        activeLineageNames = new ArrayList<>();
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
                        Platform.runLater(() -> refreshSearchResultsList(getSearchedText()));
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
                    rulesList.stream()
                            .filter(rule -> rule.getSearchedText().contains("'" + searched + "'"))
                            .forEachOrdered(rule -> rule.setCells(geneSearchService.getValue()));
                    geneResultsUpdated.set(!geneResultsUpdated.get());

                    if (!geneSearchQueue.isEmpty()) {
                        WormBaseQuery.doSearch(geneSearchQueue.pop());
                    }
                }
            });
        }

        geneSearchQueue = new LinkedList<>();
        count = 0;
        geneResultsUpdated = new SimpleBooleanProperty(false);
    }

    /**
     * Adds a giant connectome rule that contains all the cell results retrieved based on the input query parameters
     *
     * @param linegeName
     *         lineage name searched
     * @param color
     *         color to make the cells in the search result
     * @param isPresynapticTicked
     *         true if the presynaptic option was ticked, false otherwise
     * @param isPostsynapticTicked
     *         true if the postsynaptic option was ticked, false otherwise
     * @param isElectricalTicked
     *         true if the electrical option was ticked, false otherwise
     * @param isNeuromuscularTicked
     *         true if the neuromuscular option was ticked, false otherwise
     *
     * @return the rule that was added to the internal list
     */
    public static Rule addGiantConnectomeColorRule(
            final String linegeName,
            final Color color,
            final boolean isPresynapticTicked,
            final boolean isPostsynapticTicked,
            final boolean isElectricalTicked,
            final boolean isNeuromuscularTicked) {

        StringBuilder sb = new StringBuilder("'");
        sb.append(linegeName.toLowerCase()).append("' Connectome");

        ArrayList<String> types = new ArrayList<>();
        if (isPresynapticTicked) {
            types.add("presynaptic");
        }
        if (isPostsynapticTicked) {
            types.add("postsynaptic");
        }
        if (isElectricalTicked) {
            types.add("electrical");
        }
        if (isNeuromuscularTicked) {
            types.add("neuromuscular");
        }
        if (!types.isEmpty()) {
            sb.append(" - ");

            for (int i = 0; i < types.size(); i++) {
                sb.append(types.get(i));
                if (i != types.size() - 1) {
                    sb.append(", ");
                }
            }
        }

        final Rule rule = new Rule(sb.toString(), color, SearchType.CONNECTOME, SearchOption.CELLNUCLEUS);
        rule.setCells(connectome.queryConnectivity(
                linegeName,
                isPresynapticTicked,
                isPostsynapticTicked,
                isElectricalTicked,
                isNeuromuscularTicked,
                true));
        rule.setSearchedText(sb.toString());
        rule.resetLabel(sb.toString());

        rulesList.add(rule);
        return rule;
    }

    public static Rule addConnectomeColorRule(
            String searched,
            Color color,
            boolean isPresynaptic,
            boolean isPostsynaptic,
            boolean isElectrical,
            boolean isNeuromuscular) {

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
        ArrayList<String> cellsForListView = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return;
        }

        cellsForListView.addAll(results);
        if (ancestorTicked) {
            ArrayList<String> ancestors = getAncestorsList(results);
            ancestors.stream()
                    .filter(name -> !cellsForListView.contains(name))
                    .forEachOrdered(cellsForListView::add);
        }
        if (descendantTicked) {
            ArrayList<String> descendants = getDescendantsList(results);
            descendants.stream()
                    .filter(name -> !cellsForListView.contains(name))
                    .forEachOrdered(cellsForListView::add);
        }

        searchResultsList.sort(nameComparator);
        appendFunctionalToLineageNames(cellsForListView);
        geneResultsUpdated.set(!geneResultsUpdated.get());
    }

    private static void appendFunctionalToLineageNames(ArrayList<String> list) {
        searchResultsList.clear();
        for (String result : list) {
            if (PartsList.getFunctionalNameByLineageName(result) != null) {
                result += " (" + PartsList.getFunctionalNameByLineageName(result) + ")";
            }
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

    public static ObservableList<Rule> getRules() {
        return rulesList;
    }

    public static Rule addMulticellularStructureRule(String searched, Color color) {
        return addColorRule(null, searched, color, SearchOption.MULTICELLULAR_NAME_BASED);
    }

    public static Rule addColorRule(SearchType type, String searched, Color color, SearchOption... options) {
        final ArrayList<SearchOption> optionsArray = new ArrayList<>(Arrays.asList(options));
        return addColorRule(type, searched, color, optionsArray);
    }

    public static Rule addColorRule(
            SearchType searchType,
            String searched,
            Color color,
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
            options = new ArrayList<>();
            options.add(SearchOption.CELLNUCLEUS);
        }

        String label = "";
        searched = searched.trim().toLowerCase();

        if (type != null) {

            switch (type) {
                case LINEAGE:
                    label = LineageTree.getCaseSensitiveName(searched);
                    if (label.isEmpty()) {
                        label = searched;
                    }
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
        } else {
            // if search type is null, then rule was a multicellular structure rule
            label = searched;
        }

        final Rule rule = new Rule(label, color, type, options);
        rule.setCells(getCellsList(searched));

        rulesList.add(rule);
        searchResultsList.clear();

        return rule;
    }

    private static ArrayList<String> getCellsList(String searched) {
        ArrayList<String> cells = new ArrayList<>();
        searched = searched.toLowerCase();

        if (type != null) {
            switch (type) {
                case LINEAGE:
                    for (String name : activeLineageNames) {
                        if (name.equalsIgnoreCase(searched)) {
                            cells.add(name);
                        }
                    }
                    break;

                case FUNCTIONAL:
                    String name;
                    for (int i = 0; i < functionalNames.size(); i++) {
                        name = functionalNames.get(i);
                        if (name.equalsIgnoreCase(searched)) {
                            cells.add(PartsList.getLineageNameByIndex(i));
                        }
                    }
                    break;

                case DESCRIPTION:
                    // for searching with multiple terms, perform individual searches and return the intersection of
                    // the hits
                    ArrayList<ArrayList<String>> hits = new ArrayList<>();
                    String[] keywords = searched.split(" ");
                    for (String keyword : keywords) {
                        ArrayList<String> results = new ArrayList<>();
                        for (int i = 0; i < descriptions.size(); i++) {
                            String textLowerCase = descriptions.get(i).toLowerCase();

                            // look for match
                            if (textLowerCase.contains(keyword.toLowerCase())) {
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

                    // find the intersection among the results --> using the first list to find matches
                    if (hits.size() > 0) {
                        ArrayList<String> results = hits.get(0);
                        for (String cell : results) {
                            // look for a match in rest of the hits
                            boolean intersection = true;
                            for (int i = 1; i < hits.size(); i++) {
                                if (!hits.get(i).contains(cell)) {
                                    intersection = false;
                                }
                            }

                            if (intersection && !cells.contains(cell)) {
                                cells.add(cell);
                            }
                        }
                    }
                    break;

                case GENE:
                    if (isGeneFormat(getSearchedText())) {
                        showLoadingService.restart();
                        WormBaseQuery.doSearch(getSearchedText());
                        cells = new ArrayList<>(searchResultsList);
                    }
                    break;

                case MULTICELLULAR_CELL_BASED:
                    if (sceneElementsList != null) {
                        for (SceneElement se : sceneElementsList.getElementsList()) {
                            if (se.isMulticellular()) {
                                if (isNameSearched(se.getSceneName(), searched)) {
                                    for (String cellName : se.getAllCellNames()) {
                                        if (!cells.contains(cellName)) {
                                            cells.add(cellName);
                                        }
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
                        cells.remove(searched);
                    }
                    break;

                case NEIGHBOR:
                    cells.addAll(getNeighbors(searched));
            }
        }
        return cells;
    }

    /**
     * Tests if a structure name was searched based on its scene name and its comment.
     *
     * @param structureName
     *         structure name
     * @param searched
     *         string of all search terms
     *
     * @return true the structure's scene name or comment contains all search terms, false otherwise
     */
    private static boolean isNameSearched(String structureName, String searched) {
        if (structureName == null || searched == null) {
            return false;
        }

        // search in structure scene names
        structureName = structureName.trim().toLowerCase();
        final String[] terms = searched.trim().toLowerCase().split(" ");

        boolean appliesToName = true;
        boolean appliesToComment = true;

        for (String term : terms) {
            if (!structureName.contains(term)) {
                appliesToName = false;
                break;
            }
        }

        // search in comments if name does not already apply
        final String comment = sceneElementsList.getNameToCommentsMap()
                .get(structureName)
                .toLowerCase();
        for (String term : terms) {
            if (!comment.contains(term)) {
                appliesToComment = false;
                break;
            }
        }

        return appliesToName || appliesToComment;
    }

    /**
     * Checks whether a name is a gene name
     *
     * @param name
     *         the name checked
     *
     * @return true if the name is a gene name, false otherwise. A gene name has the format SOME_STRING-SOME_NUMBER.
     */
    private static boolean isGeneFormat(String name) {
        name = name.trim();
        final int hyphenIndex = name.indexOf("-");
        // check that there is a hyphen and there is a string preceeding it
        if (hyphenIndex > 1 && name.substring(hyphenIndex + 1).matches("\\d+")) {
            return true;
        }
        return false;
    }

    /**
     * Retrieves the terminal descendants for a cell. This is called by {@link wormguides.models.NonTerminalCellCase}.
     *
     * @param queryCell
     *         the cell queried
     *
     * @return list of terminal descendants for the query cell
     */
    public static ArrayList<String> getDescendantsList(String queryCell) {
        ArrayList<String> descendants = new ArrayList<>();
        if (queryCell != null) {
            PartsList.getLineageNames()
                    .stream()
                    .filter(name -> !descendants.contains(name) && LineageTree.isDescendant(name, queryCell))
                    .forEachOrdered(descendants::add);
        }
        return descendants;
    }

    /**
     * Retrieves the descendants for all the cells in the input list
     *
     * @param cells
     *         list of cells to check
     *
     * @return list of descendants of all the cells, with no repeats
     */
    private static ArrayList<String> getDescendantsList(final ArrayList<String> cells) {
        ArrayList<String> descendants = new ArrayList<>();

        if (cells == null) {
            return descendants;
        }

        // special cases for 'ab' and 'p0' because the input list of cells would be empty
        String searched = searchedText.toLowerCase();
        if (searched.equals("ab") || searched.equals("p0")) {
            activeLineageNames.stream()
                    .filter(name -> !descendants.contains(name) && LineageTree.isDescendant(name, searched))
                    .forEachOrdered(descendants::add);
        }

        for (String cell : cells) {
            activeLineageNames.stream()
                    .filter(name -> !descendants.contains(name) && LineageTree.isDescendant(name, cell))
                    .forEachOrdered(descendants::add);
        }

        return descendants;
    }

    /**
     * Retrieves the ancestors for all the cells in the input list
     *
     * @param cells
     *         list of cells to check
     *
     * @return list of ancestors of all the cells, with no repeats
     */
    private static ArrayList<String> getAncestorsList(final ArrayList<String> cells) {
        ArrayList<String> ancestors = new ArrayList<>();

        if (cells == null) {
            return ancestors;
        }

        for (String cell : cells) {
            activeLineageNames.stream()
                    .filter(name -> !ancestors.contains(name) && LineageTree.isAncestor(name, cell))
                    .forEachOrdered(ancestors::add);
        }

        return ancestors;
    }

    /**
     * Neighbor search mode: given cell: - find time range of cell - for each time point - find its nearest neighbor
     * and compute d = distance to neighbor - d = root((x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2) - multiple d by 1.5 - for
     * position in positionsAtTime - compute d1 = distance from query cell position to position - if d1 is <= d - if
     * cell is not in results - add cell - highlight results over lifetime of cell.
     *
     * @param cellName
     *         string containing the lineage name of the queried cell
     *
     * @return list containing the cell lineage names of neighboring cells to cell with input cell lineage name
     */
    public static ArrayList<String> getNeighbors(String cellName) {
        ArrayList<String> results = new ArrayList<>();

        if (cellName == null || !lineageData.isCellName(cellName)) {
            return results;
        }

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
                double distance = Double.MAX_VALUE;
                for (int k = 0; k < positions.length; k++) {
                    if (k != queryIDX) {
                        double distanceFromQuery = distance(x, positions[k][0], y, positions[k][1], z, positions[k][2]);
                        if (distanceFromQuery < distance) {
                            distance = distanceFromQuery;
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
                        if (!results.contains(names[n]) && !names[n].equalsIgnoreCase(cellName)) {
                            results.add(names[n]);
                        }
                    }
                }
            }
        }

        return results;
    }

    private static double distance(int x1, int x2, int y1, int y2, int z1, int z2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) + Math.pow((z2 - z1), 2));
    }

    public static EventHandler<ActionEvent> getAddButtonListener() {
        return event -> {
            // do not add new ColorRule if search has no matches
            if (searchResultsList.isEmpty()) {
                return;
            }

            ArrayList<SearchOption> options = new ArrayList<>();
            if (cellNucleusTicked) {
                options.add(SearchOption.CELLNUCLEUS);
            }
            if (cellBodyTicked) {
                options.add(SearchOption.CELLBODY);
            }
            if (ancestorTicked) {
                options.add(SearchOption.ANCESTOR);
            }
            if (descendantTicked) {
                options.add(SearchOption.DESCENDANT);
            }

            addColorRule(getSearchedText(), selectedColor, options);

            searchResultsList.clear();

            if (clearSearchFieldProperty != null) {
                clearSearchFieldProperty.set(true);
            }
        };
    }

    public static ChangeListener<Boolean> getCellNucleusTickListener() {
        return (observable, oldValue, newValue) -> cellNucleusTicked = newValue;
    }

    public static ChangeListener<Boolean> getCellBodyTickListener() {
        return (observable, oldValue, newValue) -> cellBodyTicked = newValue;
    }

    public static ChangeListener<Boolean> getAncestorTickListner() {
        return (observable, oldValue, newValue) -> {
            ancestorTicked = newValue;
            if (type == SearchType.GENE) {
                updateGeneResults();
            } else {
                resultsUpdateService.restart();
            }
        };
    }

    public static ChangeListener<Boolean> getDescendantTickListner() {
        return (observable, oldValue, newValue) -> {
            descendantTicked = newValue;
            if (type == SearchType.GENE) {
                updateGeneResults();
            } else {
                resultsUpdateService.restart();
            }
        };
    }

    public static void setClearSearchFieldProperty(BooleanProperty property) {
        clearSearchFieldProperty = property;
    }

    public static ChangeListener<Toggle> getTypeToggleListener() {
        return (observable, oldValue, newValue) -> {
            type = (SearchType) newValue.getUserData();
            resultsUpdateService.restart();
        };
    }

    public static BooleanProperty getGeneResultsUpdated() {
        return geneResultsUpdated;
    }

    public static ObservableList<String> getSearchResultsList() {
        return searchResultsList;
    }

    public static ChangeListener<String> getTextFieldListener() {
        return (observable, oldValue, newValue) -> {
            searchedText = newValue.toLowerCase();
            if (searchedText.isEmpty()) {
                searchResultsList.clear();
            } else {
                resultsUpdateService.restart();
            }
        };
    }

    private static void refreshSearchResultsList(String newValue) {
        String searched = newValue.toLowerCase();
        if (!searched.isEmpty()) {
            ArrayList<String> cells;
            cells = getCellsList(searched);

            if (cells == null) {
                return;
            }

            ArrayList<String> cellsForListView = new ArrayList<>();
            cellsForListView.addAll(cells);

            if (descendantTicked) {
                ArrayList<String> descendants = getDescendantsList(cells);
                descendants.stream()
                        .filter(name -> !cellsForListView.contains(name))
                        .forEachOrdered(cellsForListView::add);
            }
            if (ancestorTicked) {
                ArrayList<String> ancestors = getAncestorsList(cells);
                ancestors.stream()
                        .filter(name -> !cellsForListView.contains(name))
                        .forEachOrdered(cellsForListView::add);
            }

            cellsForListView.sort(nameComparator);
            appendFunctionalToLineageNames(cellsForListView);
        }
    }

    private static int getCountFinal(int count) {
        final int out = count;
        return out;
    }

    public static void setSceneElementsList(SceneElementsList list) {
        if (list != null) {
            sceneElementsList = list;
        }
    }

    public static String getStructureComment(String name) {
        return sceneElementsList.getCommentByName(name);
    }

    public static boolean isStructureWithComment(String name) {
        return sceneElementsList != null && (sceneElementsList.isMulticellStructureName(name));

    }

    public static int getFirstOccurenceOf(String name) {
        if (lineageData != null && lineageData.isCellName(name)) {
            return lineageData.getFirstOccurrenceOf(name);

        } else if (sceneElementsList != null && sceneElementsList.isSceneElementName(name)) {
            return sceneElementsList.getFirstOccurrenceOf(name);
        }

        return -1;
    }

    public static int getLastOccurenceOf(String name) {
        if (lineageData != null && lineageData.isCellName(name)) {
            return lineageData.getLastOccurrenceOf(name);
        } else if (sceneElementsList != null && sceneElementsList.isSceneElementName(name)) {
            return sceneElementsList.getLastOccurrenceOf(name);
        }

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
        return cases != null && cases.hasCellCase(cellName);
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

    /**
     * Method taken from RootLayoutController --> how can
     * InfoWindowLinkController generate page without ptr to
     * RootLayoutController<br>
     * <br>
     * Called by InfoWindowLinkController
     */
    public static void addToInfoWindow(String name) {
        if (wiringService == null) {
            wiringService = new WiringService();
        }

        searchedText = name;
        wiringService.restart();
    }

    // connectome checkbox listeners
    public static ChangeListener<Boolean> getPresynapticTickListener() {
        return (observable, oldValue, newValue) -> {
            presynapticTicked = newValue;
            resultsUpdateService.restart();
        };
    }

    public static ChangeListener<Boolean> getPostsynapticTickListener() {
        return (observable, oldValue, newValue) -> {
            postsynapticTicked = newValue;
            resultsUpdateService.restart();
        };
    }

    public static ChangeListener<Boolean> getElectricalTickListener() {
        return (observable, oldValue, newValue) -> {
            electricalTicked = newValue;
            resultsUpdateService.restart();
        };
    }

    public static ChangeListener<Boolean> getNeuromuscularTickListener() {
        return (observable, oldValue, newValue) -> {
            neuromuscularTicked = newValue;
            resultsUpdateService.restart();
        };
    }

    public boolean containsColorRule(Rule other) {
        for (Rule rule : rulesList) {
            if (rule.equals(other)) {
                return true;
            }
        }
        return false;
    }

    public EventHandler<ActionEvent> getColorPickerListener() {
        return event -> selectedColor = ((ColorPicker) event.getSource()).getValue();
    }

    public void clearRules() {
        rulesList.clear();
    }

    public Service<Void> getResultsUpdateService() {
        return resultsUpdateService;
    }

    private static final class CellNameComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }

    private static final class WiringService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String searched = searchedText;
                    // update to lineage name if function
                    String lineage = PartsList.getLineageNameByFunctionalName(searched);
                    if (lineage != null) {
                        searched = lineage;
                    }

                    // GENERATE CELL TAB ON CLICK
                    if (searched != null && !searched.isEmpty()) {
                        if (cases == null || productionInfo == null) {
                            return null; // error check
                        }

                        if (PartsList.isLineageName(searched)) {
                            if (cases.containsCellCase(searched)) {

                                // show the tab
                            } else {
                                // translate the name if necessary
                                String funcName = connectome.checkQueryCell(searched).toUpperCase();
                                // add a terminal case --> pass the wiring
                                // partners
                                cases.makeTerminalCase(searched, funcName,
                                        connectome.queryConnectivity(funcName, true, false, false, false, false),
                                        connectome.queryConnectivity(funcName, false, true, false, false, false),
                                        connectome.queryConnectivity(funcName, false, false, true, false, false),
                                        connectome.queryConnectivity(funcName, false, false, false, true, false),
                                        productionInfo.getNuclearInfo(), productionInfo.getCellShapeData(searched));
                            }
                        } else { // not in connectome --> non terminal case
                            if (cases.containsCellCase(searched)) {

                                // show tab
                            } else {
                                // add a non terminal case
                                cases.makeNonTerminalCase(searched, productionInfo.getNuclearInfo(),
                                        productionInfo.getCellShapeData(searched));
                            }
                        }
                    }
                    return null;
                }
            };
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
                        Platform.runLater(() -> {
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
                        });
                        try {
                            Thread.sleep(WAIT_TIME_MS);
                            count++;
                            if (count < 0) {
                                count = 0;
                            }
                        } catch (InterruptedException ie) {
                            break;
                        }
                    }
                    return null;
                }
            };
        }
    }
}
