/*
 * Bao Lab 2016
 */

package wormguides.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

import wormguides.AnatomyTerm;
import wormguides.SearchOption;
import wormguides.models.CasesLists;
import wormguides.models.Connectome;
import wormguides.models.LineageTree;
import wormguides.models.ProductionInfo;
import wormguides.models.Rule;
import wormguides.models.SceneElementsList;

import acetree.lineagedata.LineageData;
import partslist.PartsList;
import search.SearchType;
import search.SearchUtil;
import search.WormBaseQuery;

import static search.SearchType.CONNECTOME;
import static wormguides.SearchOption.ANCESTOR;
import static wormguides.SearchOption.CELL_BODY;
import static wormguides.SearchOption.CELL_NUCLEUS;
import static wormguides.SearchOption.DESCENDANT;
import static wormguides.SearchOption.MULTICELLULAR_NAME_BASED;

public class SearchLayer {

    private static final Service<Void> resultsUpdateService;
    private static final Service<List<String>> geneSearchService;
    private static final Service<Void> showLoadingService;

    /** Time between changes in the number of ellipses periods during loading */
    private static final long WAIT_TIME_MS = 750;

    /** Changing number of ellipses periods to display during loading */
    private static int count;

    private static List<String> activeLineageNames;
    private static List<String> functionalNames;
    private static List<String> descriptions;

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
    private static Queue<String> geneSearchQueue;

    // for connectome searching
    private static Connectome connectome;
    private static boolean presynapticTicked;
    private static boolean postsynapticTicked;
    private static boolean electricalTicked;
    private static boolean neuromuscularTicked;

    // for cell cases searching
    private static CasesLists casesLists;

    // for wiring partner click handling
    private static ProductionInfo productionInfo;

    private static WiringService wiringService;

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
                        WormBaseQuery.doSearch(geneSearchQueue.remove());
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

        final Rule rule = new Rule(sb.toString(), color, CONNECTOME, CELL_NUCLEUS);
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

        Rule rule = addColorRule(CONNECTOME, searched, color, CELL_NUCLEUS);

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
        List<String> results = geneSearchService.getValue();
        List<String> cellsForListView = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return;
        }

        cellsForListView.addAll(results);
        if (ancestorTicked) {
            List<String> ancestors = SearchUtil.getAncestorsList(results);
            ancestors.stream()
                    .filter(name -> !cellsForListView.contains(name))
                    .forEachOrdered(cellsForListView::add);
        }
        if (descendantTicked) {
            List<String> descendants = SearchUtil.getDescendantsList(results, getSearchedText());
            descendants.stream()
                    .filter(name -> !cellsForListView.contains(name))
                    .forEachOrdered(cellsForListView::add);
        }

        searchResultsList.sort(nameComparator);
        appendFunctionalToLineageNames(cellsForListView);
        geneResultsUpdated.set(!geneResultsUpdated.get());
    }

    private static void appendFunctionalToLineageNames(List<String> list) {
        searchResultsList.clear();
        for (String result : list) {
            if (PartsList.getFunctionalNameByLineageName(result) != null) {
                result += " (" + PartsList.getFunctionalNameByLineageName(result) + ")";
            }
            searchResultsList.add(result);
        }
    }

    private static String getSearchedText() {
        final String searched = searchedText;
        return searched;
    }

    public static void setRulesList(ObservableList<Rule> list) {
        rulesList = list;
    }

    public static void addDefaultColorRules() {
        addColorRule(SearchType.FUNCTIONAL, "ash", Color.DARKSEAGREEN, CELL_BODY);
        addColorRule(SearchType.FUNCTIONAL, "rib", Color.web("0x663366"), CELL_BODY);
        addColorRule(SearchType.FUNCTIONAL, "avg", Color.web("0xb41919"), CELL_BODY);

        addColorRule(SearchType.FUNCTIONAL, "dd", Color.web("0x4a24c1", 0.60), CELL_BODY);
        addColorRule(SearchType.FUNCTIONAL, "da", Color.web("0xc56002"), CELL_BODY);

        addColorRule(SearchType.FUNCTIONAL, "rivl", Color.web("0xff9966"), CELL_BODY);
        addColorRule(SearchType.FUNCTIONAL, "rivr", Color.web("0xffe6b4"), CELL_BODY);
        addColorRule(SearchType.FUNCTIONAL, "sibd", Color.web("0xe6ccff"), CELL_BODY);
        addColorRule(SearchType.FUNCTIONAL, "siav", Color.web("0x99b3ff"), CELL_BODY);

        addColorRule(SearchType.FUNCTIONAL, "dd1", Color.web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "dd2", Color.web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "dd3", Color.web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "dd4", Color.web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "dd5", Color.web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "dd6", Color.web("0xb30a95"), CELL_NUCLEUS);

        addColorRule(SearchType.FUNCTIONAL, "da2", Color.web("0xe6b34d"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "da3", Color.web("0xe6b34d"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "da4", Color.web("0xe6b34d"), CELL_NUCLEUS);
        addColorRule(SearchType.FUNCTIONAL, "da5", Color.web("0xe6b34d"), CELL_NUCLEUS);
    }

    public static ObservableList<Rule> getRules() {
        return rulesList;
    }

    public static Rule addMulticellularStructureRule(String searched, Color color) {
        return addColorRule(null, searched, color, MULTICELLULAR_NAME_BASED);
    }

    public static Rule addColorRule(SearchType type, String searched, Color color, SearchOption... options) {
        final ArrayList<SearchOption> optionsArray = new ArrayList<>(Arrays.asList(options));
        return addColorRule(type, searched, color, optionsArray);
    }

    public static Rule addColorRule(
            final SearchType searchType,
            String searched,
            final Color color,
            final List<SearchOption> options) {

        SearchType tempType = type;
        type = searchType;
        Rule rule = addColorRule(searched, color, options);
        type = tempType;
        return rule;
    }

    private static Rule addColorRule(String searched, final Color color, List<SearchOption> options) {
        // default search options is cell
        if (options == null) {
            options = new ArrayList<>();
            options.add(CELL_NUCLEUS);
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

    private static List<String> getCellsList(final String searched) {
        List<String> cells = new ArrayList<>();

        if (type != null) {
            switch (type) {
                case LINEAGE:
                    cells = SearchUtil.getCellsWithLineageName(searched);
                    break;

                case FUNCTIONAL:
                    cells = SearchUtil.getCellsWithFunctionalName(searched);
                    break;

                case DESCRIPTION:
                    cells = SearchUtil.getCellsWithFunctionalDescription(searched);
                    break;

                case GENE:
                    showLoadingService.restart();
                    cells = SearchUtil.getCellsWithGene(getSearchedText());
                    break;

                case MULTICELLULAR_CELL_BASED:
                    cells = SearchUtil.getCellsInMulticellularStructure(searched);
                    break;

                case CONNECTOME:
                    cells = SearchUtil.getCellsWithConnectivity(
                            searched,
                            presynapticTicked,
                            postsynapticTicked,
                            neuromuscularTicked,
                            electricalTicked);
                    break;

                case NEIGHBOR:
                    cells = SearchUtil.getNeighboringCells(searched);
            }
        }
        return cells;
    }

    public static EventHandler<ActionEvent> getAddButtonListener() {
        return event -> {
            // do not add new ColorRule if search has no matches
            if (searchResultsList.isEmpty()) {
                return;
            }

            ArrayList<SearchOption> options = new ArrayList<>();
            if (cellNucleusTicked) {
                options.add(CELL_NUCLEUS);
            }
            if (cellBodyTicked) {
                options.add(CELL_BODY);
            }
            if (ancestorTicked) {
                options.add(ANCESTOR);
            }
            if (descendantTicked) {
                options.add(DESCENDANT);
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

    private static void refreshSearchResultsList(final String newSearchedTerm) {
        String searched = newSearchedTerm.toLowerCase();
        if (!searched.isEmpty()) {
            List<String> cells = getCellsList(searched);

            if (cells == null) {
                return;
            }

            List<String> cellsForListView = new ArrayList<>();
            cellsForListView.addAll(cells);

            if (descendantTicked) {
                List<String> descendants = SearchUtil.getDescendantsList(cells, getSearchedText());
                descendants.stream()
                        .filter(name -> !cellsForListView.contains(name))
                        .forEachOrdered(cellsForListView::add);
            }
            if (ancestorTicked) {
                List<String> ancestors = SearchUtil.getAncestorsList(cells);
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

    public static void initDatabases(
            final LineageData inputLineageData,
            final SceneElementsList inputSceneElementsList,
            final Connectome inputConnectome,
            final CasesLists inputCasesLists,
            final ProductionInfo inputProductionInfo) {

        SearchUtil.initDatabases(inputLineageData, inputSceneElementsList, inputConnectome, inputCasesLists);

        if (inputLineageData != null) {
            activeLineageNames = inputLineageData.getAllCellNames();
        }
        if (inputConnectome != null) {
            connectome = inputConnectome;
        }
        if (inputCasesLists != null) {
            casesLists = inputCasesLists;
        }
        if (inputProductionInfo != null) {
            productionInfo = inputProductionInfo;
        }

    }

    public static boolean hasCellCase(String cellName) {
        return casesLists != null && casesLists.hasCellCase(cellName);
    }

    /*
     * TODO WHERE ELSE CAN WE PUT THIS --> ONLY PLACE TO REFERENCE CELL CASES IN STATIC WAY
     */
    public static void removeCellCase(String cellName) {
        if (casesLists != null && cellName != null) {
            casesLists.removeCellCase(cellName);
        }
    }

    public static void addToInfoWindow(AnatomyTerm term) {
        if (term.equals(AnatomyTerm.AMPHID_SENSILLA)) {
            if (!casesLists.containsAnatomyTermCase(term.getTerm())) {
                casesLists.makeAnatomyTermCase(term);
            }
        }
    }

    /**
     * Method taken from RootLayoutController --> how can InfoWindowLinkController generate page without pointer to
     * RootLayoutController?
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
                        if (casesLists == null || productionInfo == null) {
                            return null; // error check
                        }

                        if (PartsList.isLineageName(searched)) {
                            if (casesLists.containsCellCase(searched)) {
                                // show the tab
                            } else {
                                // translate the name if necessary
                                String funcName = connectome.checkQueryCell(searched).toUpperCase();
                                // add a terminal case --> pass the wiring partners
                                casesLists.makeTerminalCase(
                                        searched,
                                        funcName,
                                        connectome.queryConnectivity(funcName, true, false, false, false, false),
                                        connectome.queryConnectivity(funcName, false, true, false, false, false),
                                        connectome.queryConnectivity(funcName, false, false, true, false, false),
                                        connectome.queryConnectivity(funcName, false, false, false, true, false),
                                        productionInfo.getNuclearInfo(),
                                        productionInfo.getCellShapeData(searched));
                            }
                        } else { // not in connectome --> non terminal case
                            if (casesLists.containsCellCase(searched)) {

                                // show tab
                            } else {
                                // add a non terminal case
                                casesLists.makeNonTerminalCase(
                                        searched,
                                        productionInfo.getNuclearInfo(),
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