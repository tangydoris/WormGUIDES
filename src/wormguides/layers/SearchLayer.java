/*
 * Bao Lab 2016
 */

package wormguides.layers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

import acetree.LineageData;
import connectome.Connectome;
import partslist.PartsList;
import search.SearchType;
import search.SearchUtil;
import search.WormBaseQuery;
import wormguides.models.AnatomyTerm;
import wormguides.models.CasesLists;
import wormguides.models.ProductionInfo;
import wormguides.models.Rule;
import wormguides.models.SceneElementsList;
import wormguides.models.SearchOption;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static java.util.Objects.requireNonNull;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;

import static partslist.PartsList.getFunctionalNameByLineageName;
import static partslist.PartsList.getLineageNameByFunctionalName;
import static search.SearchType.CONNECTOME;
import static search.SearchType.GENE;
import static search.SearchType.LINEAGE;
import static search.SearchUtil.getAncestorsList;
import static search.SearchUtil.getCellsInMulticellularStructure;
import static search.SearchUtil.getCellsWithConnectivity;
import static search.SearchUtil.getCellsWithFunctionalDescription;
import static search.SearchUtil.getCellsWithFunctionalName;
import static search.SearchUtil.getCellsWithGene;
import static search.SearchUtil.getCellsWithLineageName;
import static search.SearchUtil.getDescendantsList;
import static search.SearchUtil.getNeighboringCells;
import static search.WormBaseQuery.getSearchService;
import static wormguides.models.AnatomyTerm.AMPHID_SENSILLA;
import static wormguides.models.LineageTree.getCaseSensitiveName;
import static wormguides.models.SearchOption.ANCESTOR;
import static wormguides.models.SearchOption.CELL_BODY;
import static wormguides.models.SearchOption.CELL_NUCLEUS;
import static wormguides.models.SearchOption.DESCENDANT;
import static wormguides.models.SearchOption.MULTICELLULAR_NAME_BASED;

public class SearchLayer {

    private final Service<Void> resultsUpdateService;
    private final Service<List<String>> geneSearchService;
    private final Service<Void> showLoadingService;

    private final ObservableList<Rule> rulesList;

    private final ObservableList<String> searchResultsList;

    // GUI components
    private final TextField searchTextField;
    private final ToggleGroup searchTypeToggleGroup;
    private final CheckBox presynapticCheckBox;
    private final CheckBox postsynapticCheckBox;
    private final CheckBox neuromuscularCheckBox;
    private final CheckBox electricalCheckBox;
    private final CheckBox cellNucleusCheckBox;
    private final CheckBox cellBodyCheckBox;
    private final CheckBox ancestorCheckBox;
    private final CheckBox descendantCheckBox;
    private final ColorPicker colorPicker;
    private final Button addRuleButton;

    private BooleanProperty geneResultsUpdated;
    private Queue<String> geneSearchQueue;

    // queried databases
    private Connectome connectome;
    private CasesLists casesLists;
    private ProductionInfo productionInfo;
    private WiringService wiringService;

    public SearchLayer(
            final ObservableList<Rule> rulesList,
            final TextField searchTextField,
            final ToggleGroup searchTypeToggleGroup,
            final CheckBox presynapticCheckBox,
            final CheckBox postsynapticCheckBox,
            final CheckBox neuromuscularCheckBox,
            final CheckBox electricalCheckBox,
            final CheckBox cellNucleusCheckBox,
            final CheckBox cellBodyCheckBox,
            final CheckBox ancestorCheckBox,
            final CheckBox descendantCheckBox,
            final ColorPicker colorPicker,
            final Button addRuleButton) {

        this.rulesList = requireNonNull(rulesList);

        // text field
        this.searchTextField = requireNonNull(searchTextField);
        this.searchTextField.textProperty().addListener(getTextFieldListener());

        // search type
        this.searchTypeToggleGroup = requireNonNull(searchTypeToggleGroup);

        final ChangeListener<Boolean> connectomeCheckBoxListener = getConnectomeCheckBoxListener();

        this.presynapticCheckBox = requireNonNull(presynapticCheckBox);
        this.presynapticCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        this.postsynapticCheckBox = requireNonNull(postsynapticCheckBox);
        this.postsynapticCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        this.neuromuscularCheckBox = requireNonNull(neuromuscularCheckBox);
        this.neuromuscularCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        this.electricalCheckBox = requireNonNull(electricalCheckBox);
        this.electricalCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        // search options
        final ChangeListener<Boolean> optionsCheckBoxListener = getOptionsCheckBoxListener();

        this.cellNucleusCheckBox = requireNonNull(cellNucleusCheckBox);
        this.cellNucleusCheckBox.selectedProperty().addListener(optionsCheckBoxListener);

        this.cellBodyCheckBox = requireNonNull(cellBodyCheckBox);
        this.cellBodyCheckBox.selectedProperty().addListener(optionsCheckBoxListener);

        this.ancestorCheckBox = requireNonNull(ancestorCheckBox);
        this.ancestorCheckBox.selectedProperty().addListener(optionsCheckBoxListener);

        this.descendantCheckBox = requireNonNull(descendantCheckBox);
        this.descendantCheckBox.selectedProperty().addListener(optionsCheckBoxListener);

        // color
        this.colorPicker = requireNonNull(colorPicker);

        // add rule button
        this.addRuleButton = requireNonNull(addRuleButton);
        this.addRuleButton.setOnAction(getAddButtonClickHandler());

        this.searchResultsList = observableArrayList();

        this.resultsUpdateService = new Service<Void>() {
            @Override
            protected final Task<Void> createTask() {
                final Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        runLater(() -> refreshSearchResultsList(
                                (SearchType) searchTypeToggleGroup.getSelectedToggle().getUserData(),
                                getSearchedText(),
                                cellNucleusCheckBox.isSelected(),
                                descendantCheckBox.isSelected(),
                                ancestorCheckBox.isSelected()));
                        return null;
                    }
                };
                return task;
            }
        };

        showLoadingService = new ShowLoadingService();

        geneSearchService = getSearchService();
        geneSearchService.setOnCancelled(event -> {
            showLoadingService.cancel();
            searchResultsList.clear();
        });
        geneSearchService.setOnSucceeded(event -> {
            showLoadingService.cancel();
            searchResultsList.clear();
            updateGeneResults();

            final String searched = WormBaseQuery.getSearchedText();
            geneSearchQueue.remove(searched);

            final String searchedQuoted = "'" + searched + "'";
            rulesList.stream()
                    .filter(rule -> rule.getSearchedText().contains(searchedQuoted))
                    .forEachOrdered(rule -> rule.setCells(geneSearchService.getValue()));
            geneResultsUpdated.set(!geneResultsUpdated.get());

            if (!geneSearchQueue.isEmpty()) {
                WormBaseQuery.doSearch(geneSearchQueue.remove());
            }
        });

        geneSearchQueue = new LinkedList<>();
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
    public Rule addGiantConnectomeColorRule(
            final String linegeName,
            final Color color,
            final boolean isPresynapticTicked,
            final boolean isPostsynapticTicked,
            final boolean isElectricalTicked,
            final boolean isNeuromuscularTicked) {

        final StringBuilder sb = new StringBuilder("'");
        sb.append(linegeName.toLowerCase()).append("' Connectome");

        final List<String> types = new ArrayList<>();
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

    public Rule addConnectomeColorRule(
            final String searched,
            final Color color,
            final boolean isPresynaptic,
            final boolean isPostsynaptic,
            final boolean isElectrical,
            final boolean isNeuromuscular) {
        return addColorRule(CONNECTOME, searched, color, CELL_NUCLEUS);
    }

    private void updateGeneResults() {
        final List<String> results = geneSearchService.getValue();
        final List<String> cellsForListView = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return;
        }

        cellsForListView.addAll(results);
        final String searchedText = getSearchedText();
        if (ancestorCheckBox.isSelected()) {
            List<String> ancestors = getAncestorsList(results, searchedText);
            ancestors.stream()
                    .filter(name -> !cellsForListView.contains(name))
                    .forEachOrdered(cellsForListView::add);
        }
        if (descendantCheckBox.isSelected()) {
            List<String> descendants = getDescendantsList(results, searchedText);
            descendants.stream()
                    .filter(name -> !cellsForListView.contains(name))
                    .forEachOrdered(cellsForListView::add);
        }

        sort(searchResultsList);
        appendFunctionalToLineageNames(cellsForListView);
        geneResultsUpdated.set(!geneResultsUpdated.get());
    }

    private void appendFunctionalToLineageNames(final List<String> list) {
        searchResultsList.clear();
        for (String result : list) {
            if (getFunctionalNameByLineageName(result) != null) {
                result += " (" + getFunctionalNameByLineageName(result) + ")";
            }
            searchResultsList.add(result);
        }
    }

    private String getSearchedText() {
        final String searched = searchTextField.getText().toLowerCase();
        return searched;
    }

    public void addDefaultColorRules() {
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

    public ObservableList<Rule> getRules() {
        return rulesList;
    }

    public Rule addMulticellularStructureRule(String searched, Color color) {
        return addColorRule(null, searched, color, MULTICELLULAR_NAME_BASED);
    }

    public Rule addColorRule(
            final SearchType type,
            String searched,
            final Color color,
            final SearchOption... options) {
        final List<SearchOption> optionsArray = new ArrayList<>(asList(options));
        return addColorRule(type, searched, color, optionsArray);
    }

    public Rule addColorRule(
            final SearchType searchTykpe,
            String searched,
            final Color color,
            List<SearchOption> options) {

        // default search options is cell
        if (options == null) {
            options = new ArrayList<>();
            options.add(CELL_NUCLEUS);
        }

        searched = searched.trim().toLowerCase();
        final StringBuilder label = new StringBuilder();
        if (searchTykpe != null) {
            if (searchTykpe == LINEAGE) {
                label.append(getCaseSensitiveName(searched));
                if (label.toString().isEmpty()) {
                    label.append(searched);
                }
            } else {
                label.append("'").append(searched).append("' ").append(searchTykpe.toString());
            }
        } else {
            label.append(searched);
        }

        final Rule rule = new Rule(label.toString(), color, searchTykpe, options);
        rule.setCells(getCellsList(searchTykpe, searched));
        rulesList.add(rule);

        searchResultsList.clear();

        return rule;
    }

    private List<String> getCellsList(final SearchType type, final String searched) {
        List<String> cells = new ArrayList<>();

        if (type != null) {
            switch (type) {
                case LINEAGE:
                    cells = getCellsWithLineageName(searched);
                    break;

                case FUNCTIONAL:
                    cells = getCellsWithFunctionalName(searched);
                    break;

                case DESCRIPTION:
                    cells = getCellsWithFunctionalDescription(searched);
                    break;

                case GENE:
                    showLoadingService.restart();
                    cells = getCellsWithGene(getSearchedText());
                    break;

                case MULTICELLULAR_CELL_BASED:
                    cells = getCellsInMulticellularStructure(searched);
                    break;

                case CONNECTOME:
                    cells = getCellsWithConnectivity(
                            searched,
                            presynapticCheckBox.isSelected(),
                            postsynapticCheckBox.isSelected(),
                            neuromuscularCheckBox.isSelected(),
                            electricalCheckBox.isSelected());
                    break;

                case NEIGHBOR:
                    cells = getNeighboringCells(searched);
            }
        }
        return cells;
    }

    public EventHandler<ActionEvent> getAddButtonClickHandler() {
        return event -> {
            // do not add new ColorRule if search has no matches
            if (searchResultsList.isEmpty()) {
                return;
            }

            final List<SearchOption> options = new ArrayList<>();
            if (cellNucleusCheckBox.isSelected()) {
                options.add(CELL_NUCLEUS);
            }
            if (cellBodyCheckBox.isSelected()) {
                options.add(CELL_BODY);
            }
            if (ancestorCheckBox.isSelected()) {
                options.add(ANCESTOR);
            }
            if (descendantCheckBox.isSelected()) {
                options.add(DESCENDANT);
            }

            addColorRule(
                    (SearchType) searchTypeToggleGroup.getSelectedToggle().getUserData(),
                    getSearchedText(),
                    colorPicker.getValue(),
                    options);

            searchResultsList.clear();
            searchTextField.clear();
        };
    }

    public ChangeListener<Boolean> getOptionsCheckBoxListener() {
        return (observableValue, oldValud, newValue) -> {
            if (searchTypeToggleGroup.getSelectedToggle().getUserData() == GENE) {
                updateGeneResults();
            } else {
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

    private ChangeListener<String> getTextFieldListener() {
        return (observable, oldValue, newValue) -> {
            if (searchTextField.getText().isEmpty()) {
                searchResultsList.clear();
            } else {
                resultsUpdateService.restart();
            }
        };
    }

    private void refreshSearchResultsList(
            final SearchType searchType,
            final String newSearchedTerm,
            final boolean isCellNucleusFetched,
            final boolean areDescendantsFetched,
            final boolean areAncestorsFetched) {

        final String searched = newSearchedTerm.trim().toLowerCase();
        if (!searched.isEmpty()) {
            final List<String> cells = getCellsList(searchType, searched);
            if (cells == null) {
                return;
            }

            final String searchedText = getSearchedText();
            final List<String> cellsForListView = new ArrayList<>(cells);
            if (areDescendantsFetched) {
                getDescendantsList(cells, searchedText)
                        .stream()
                        .filter(name -> !cellsForListView.contains(name))
                        .forEachOrdered(cellsForListView::add);
            }
            if (areAncestorsFetched) {
                getAncestorsList(cells, searchedText)
                        .stream()
                        .filter(name -> !cellsForListView.contains(name))
                        .forEachOrdered(cellsForListView::add);
            }
            if (!isCellNucleusFetched) {
                final Iterator<String> iterator = cellsForListView.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().equalsIgnoreCase(searched)) {
                        iterator.remove();
                        break;
                    }
                }
            }

            sort(cellsForListView);
            appendFunctionalToLineageNames(cellsForListView);
        }
    }

    public void initDatabases(
            final LineageData inputLineageData,
            final SceneElementsList inputSceneElementsList,
            final Connectome inputConnectome,
            final CasesLists inputCasesLists,
            final ProductionInfo inputProductionInfo) {

        SearchUtil.initDatabases(inputLineageData, inputSceneElementsList, inputConnectome, inputCasesLists);

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

    public boolean hasCellCase(String cellName) {
        return casesLists != null && casesLists.hasCellCase(cellName);
    }

    public void removeCellCase(final String cellName) {
        if (casesLists != null && cellName != null) {
            casesLists.removeCellCase(cellName);
        }
    }

    public void addToInfoWindow(final AnatomyTerm term) {
        if (term.equals(AMPHID_SENSILLA)) {
            if (!casesLists.containsAnatomyTermCase(term.getTerm())) {
                casesLists.makeAnatomyTermCase(term);
            }
        }
    }

    /**
     * Method taken from RootLayoutController --> how can InfoWindowLinkController generate page without pointer to
     * RootLayoutController?
     */
    public void addToInfoWindow(final String name) {
        if (wiringService == null) {
            wiringService = new WiringService();
        }
        wiringService.setSearchString(name);
        wiringService.restart();
    }

    public void clearRules() {
        rulesList.clear();
    }

    private ChangeListener<Boolean> getConnectomeCheckBoxListener() {
        return (observable, oldValue, newValue) -> resultsUpdateService.restart();
    }

    public Service<Void> getResultsUpdateService() {
        return resultsUpdateService;
    }

    private final class WiringService extends Service<Void> {

        private String searchString;

        public String getSearchString() {
            final String searched = searchString;
            return searched;
        }

        public void setSearchString(final String searchString) {
            this.searchString = searchString;
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String searched = getSearchString();
                    // update to lineage name if function
                    String lineage = getLineageNameByFunctionalName(searched);
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

    private final class ShowLoadingService extends Service<Void> {

        /** Time between changes in the number of ellipses periods during loading */
        private final long WAIT_TIME_MS = 750;

        /** Changing number of ellipses periods to display during loading */
        private int count = 0;

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
                        runLater(() -> {
                            searchResultsList.clear();
                            String loading = "Fetching data from WormBase";
                            int num = count % modulus;
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
                            sleep(WAIT_TIME_MS);
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