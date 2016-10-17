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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

import acetree.LineageData;
import connectome.Connectome;
import search.SearchType;
import search.SearchUtil;
import wormguides.models.AnatomyTerm;
import wormguides.models.CasesLists;
import wormguides.models.ProductionInfo;
import wormguides.models.Rule;
import wormguides.models.SceneElementsList;
import wormguides.models.SearchOption;
import wormguides.util.GeneSearchService;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static java.util.Objects.requireNonNull;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.paint.Color.DARKSEAGREEN;
import static javafx.scene.paint.Color.web;

import static partslist.PartsList.getFunctionalNameByLineageName;
import static partslist.PartsList.getLineageNameByFunctionalName;
import static partslist.PartsList.isLineageName;
import static search.SearchType.CONNECTOME;
import static search.SearchType.DESCRIPTION;
import static search.SearchType.FUNCTIONAL;
import static search.SearchType.GENE;
import static search.SearchType.LINEAGE;
import static search.SearchType.MULTICELLULAR_CELL_BASED;
import static search.SearchUtil.getAncestorsList;
import static search.SearchUtil.getCellsInMulticellularStructure;
import static search.SearchUtil.getCellsWithConnectivity;
import static search.SearchUtil.getCellsWithFunctionalDescription;
import static search.SearchUtil.getCellsWithFunctionalName;
import static search.SearchUtil.getCellsWithLineageName;
import static search.SearchUtil.getDescendantsList;
import static search.SearchUtil.getNeighboringCells;
import static search.SearchUtil.isGeneFormat;
import static wormguides.models.AnatomyTerm.AMPHID_SENSILLA;
import static wormguides.models.LineageTree.getCaseSensitiveName;
import static wormguides.models.SearchOption.ANCESTOR;
import static wormguides.models.SearchOption.CELL_BODY;
import static wormguides.models.SearchOption.CELL_NUCLEUS;
import static wormguides.models.SearchOption.DESCENDANT;
import static wormguides.models.SearchOption.MULTICELLULAR_NAME_BASED;

public class SearchLayer {

    private final Service<Void> resultsUpdateService;
    private final GeneSearchService geneSearchService;
    private final Service<Void> showLoadingService;

    private final ObservableList<Rule> rulesList;

    private final ObservableList<String> searchResultsList;

    // gui components
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

    /**
     * Tells the {@link wormguides.controllers.Window3DController} to update the subscene according to the fetched
     * gene results
     */
    private final BooleanProperty geneResultsUpdated;
    private final Queue<String> geneSearchQueue;

    // queried databases
    private Connectome connectome;
    private CasesLists casesLists;
    private ProductionInfo productionInfo;
    private WiringService wiringService;

    public SearchLayer(
            final ObservableList<Rule> rulesList,
            final TextField searchTextField,
            final RadioButton systematicRadioButton,
            final RadioButton functionalRadioButton,
            final RadioButton descriptionRadioButton,
            final RadioButton geneRadioButton,
            final RadioButton connectomeRadioButton,
            final RadioButton multicellRadioButton,
            final Label descendantLabel,
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

        final ChangeListener<Boolean> connectomeCheckBoxListener = getConnectomeCheckBoxListener();

        this.presynapticCheckBox = requireNonNull(presynapticCheckBox);
        this.presynapticCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        this.postsynapticCheckBox = requireNonNull(postsynapticCheckBox);
        this.postsynapticCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        this.neuromuscularCheckBox = requireNonNull(neuromuscularCheckBox);
        this.neuromuscularCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

        this.electricalCheckBox = requireNonNull(electricalCheckBox);
        this.electricalCheckBox.selectedProperty().addListener(connectomeCheckBoxListener);

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

        geneSearchQueue = new LinkedList<>();

        geneResultsUpdated = new SimpleBooleanProperty(false);

        geneSearchService = new GeneSearchService();
        geneSearchService.setOnScheduled(event -> {
//            System.out.println("search service scheduled");
            showLoadingService.restart();
        });
        geneSearchService.setOnCancelled(event -> {
//            System.out.println("search service cancelled");
            geneSearchService.resetSearchedGene();
            showLoadingService.cancel();
        });
        geneSearchService.setOnSucceeded(event -> {
//            System.out.println("search service succeeded");
            showLoadingService.cancel();
            searchResultsList.clear();

            final String searchedGene = geneSearchService.getSearchedGene();

            updateGeneResults(searchedGene);

            // set cells for any old gene rules
            geneSearchQueue.remove(searchedGene);

            final String searchedQuoted = "'" + searchedGene + "'";
            rulesList.stream()
                    .filter(rule -> rule.getSearchedText().contains(searchedQuoted))
                    .forEach(rule -> rule.setCells(geneSearchService.getValue()));
            geneResultsUpdated.set(!geneResultsUpdated.get());

            if (!geneSearchQueue.isEmpty()) {
                geneSearchService.setSearchedGene(geneSearchQueue.remove());
            }
        });

        // search type toggle
        this.searchTypeToggleGroup = new ToggleGroup();
        initSearchTypeToggleGroup(
                requireNonNull(systematicRadioButton),
                requireNonNull(functionalRadioButton),
                requireNonNull(descriptionRadioButton),
                requireNonNull(geneRadioButton),
                requireNonNull(connectomeRadioButton),
                requireNonNull(multicellRadioButton),
                requireNonNull(descendantLabel));
    }

    private void initSearchTypeToggleGroup(
            final RadioButton systematicRadioButton,
            final RadioButton functionalRadioButton,
            final RadioButton descriptionRadioButton,
            final RadioButton geneRadioButton,
            final RadioButton connectomeRadioButton,
            final RadioButton multicellRadioButton,
            final Label descendantLabel) {

        systematicRadioButton.setToggleGroup(searchTypeToggleGroup);
        systematicRadioButton.setUserData(LINEAGE);

        functionalRadioButton.setToggleGroup(searchTypeToggleGroup);
        functionalRadioButton.setUserData(FUNCTIONAL);

        descriptionRadioButton.setToggleGroup(searchTypeToggleGroup);
        descriptionRadioButton.setUserData(DESCRIPTION);

        geneRadioButton.setToggleGroup(searchTypeToggleGroup);
        geneRadioButton.setUserData(GENE);

        connectomeRadioButton.setToggleGroup(searchTypeToggleGroup);
        connectomeRadioButton.setUserData(CONNECTOME);

        multicellRadioButton.setToggleGroup(searchTypeToggleGroup);
        multicellRadioButton.setUserData(MULTICELLULAR_CELL_BASED);

        searchTypeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            // if toggle was previously on 'gene' then cancel whatever wormbase search was issued
            if (oldValue != null && oldValue.getUserData() == GENE) {
                geneSearchService.cancel();
            }

            final SearchType type = (SearchType) newValue.getUserData();

            // disable descendant options for terminal cell searches
            if (type == FUNCTIONAL || type == DESCRIPTION) {
                descendantCheckBox.setSelected(false);
                descendantCheckBox.disableProperty().set(true);
                descendantLabel.disableProperty().set(true);
            } else {
                descendantCheckBox.disableProperty().set(false);
                descendantLabel.disableProperty().set(false);
            }

            // re-search whatever is in the search field with this new search type
            refreshSearchResultsList(
                    type,
                    getSearchedText(),
                    cellNucleusCheckBox.isSelected(),
                    descendantCheckBox.isSelected(),
                    ancestorCheckBox.isSelected());
        });

        // select lineage search on start
        systematicRadioButton.setSelected(true);
    }

    /**
     * Adds a giant connectome rule that contains all the cell results retrieved based on the input query parameters
     *
     * @param linegeName
     *         the searched lineage name
     * @param color
     *         color to apply to cell entities
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

    private void updateGeneResults(final String searchedGene) {
        final List<String> results = new ArrayList<>(geneSearchService.getResultsIfPreviouslyFetched(searchedGene));

        if (results.isEmpty()) {
            return;
        }

        if (descendantCheckBox.isSelected()) {
            getDescendantsList(results, searchedGene)
                    .stream()
                    .filter(name -> !results.contains(name))
                    .forEachOrdered(results::add);
        }
        if (ancestorCheckBox.isSelected()) {
            getAncestorsList(results, searchedGene)
                    .stream()
                    .filter(name -> !results.contains(name))
                    .forEachOrdered(results::add);
        }
        if (!cellNucleusCheckBox.isSelected()) {
            final Iterator<String> iterator = results.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equalsIgnoreCase(searchedGene)) {
                    iterator.remove();
                    break;
                }
            }
        }

        sort(results);
        appendFunctionalToLineageNames(results);
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
        addColorRule(FUNCTIONAL, "ash", DARKSEAGREEN, CELL_BODY);
        addColorRule(FUNCTIONAL, "rib", web("0x663366"), CELL_BODY);
        addColorRule(FUNCTIONAL, "avg", web("0xb41919"), CELL_BODY);

        addColorRule(FUNCTIONAL, "dd", web("0x4a24c1", 0.60), CELL_BODY);
        addColorRule(FUNCTIONAL, "da", web("0xc56002"), CELL_BODY);

        addColorRule(FUNCTIONAL, "rivl", web("0xff9966"), CELL_BODY);
        addColorRule(FUNCTIONAL, "rivr", web("0xffe6b4"), CELL_BODY);
        addColorRule(FUNCTIONAL, "sibd", web("0xe6ccff"), CELL_BODY);
        addColorRule(FUNCTIONAL, "siav", web("0x99b3ff"), CELL_BODY);

        addColorRule(FUNCTIONAL, "dd1", web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "dd2", web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "dd3", web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "dd4", web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "dd5", web("0xb30a95"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "dd6", web("0xb30a95"), CELL_NUCLEUS);

        addColorRule(FUNCTIONAL, "da2", web("0xe6b34d"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "da3", web("0xe6b34d"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "da4", web("0xe6b34d"), CELL_NUCLEUS);
        addColorRule(FUNCTIONAL, "da5", web("0xe6b34d"), CELL_NUCLEUS);
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
        return addColorRule(type, searched, color, new ArrayList<>(asList(options)));
    }

    public Rule addColorRule(
            final SearchType searchType,
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
        if (searchType != null) {
            if (searchType == LINEAGE) {
                label.append(getCaseSensitiveName(searched));
                if (label.toString().isEmpty()) {
                    label.append(searched);
                }
            } else {
                label.append("'").append(searched).append("' ").append(searchType.toString());
            }
        } else {
            label.append(searched);
        }

        final Rule rule = new Rule(label.toString(), color, searchType, options);
        rule.setCells(getCellsList(searchType, searched));
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
                    switch (geneSearchService.getState()) {
                        case RUNNING:
                            geneSearchService.cancel();
                        case CANCELLED:
                        case SUCCEEDED:
                            geneSearchService.reset();
                            break;
                    }
                    if (isGeneFormat(searched)) {
                        geneSearchService.setSearchedGene(searched);
                        geneSearchService.start();
                    }
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
                updateGeneResults(getSearchedText());
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
            String searchedTerm,
            final boolean isCellNucleusFetched,
            final boolean areDescendantsFetched,
            final boolean areAncestorsFetched) {

        if (!searchedTerm.isEmpty()) {
            searchedTerm = searchedTerm.trim().toLowerCase();

            final List<String> cells = getCellsList(searchType, searchedTerm);

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
                    if (iterator.next().equalsIgnoreCase(searchedTerm)) {
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
                    final String lineage = getLineageNameByFunctionalName(searched);
                    if (lineage != null) {
                        searched = lineage;
                    }

                    // GENERATE CELL TAB ON CLICK
                    if (searched != null && !searched.isEmpty()) {
                        if (casesLists == null || productionInfo == null) {
                            return null; // error check
                        }

                        if (isLineageName(searched)) {
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

    /**
     * Service that shows when gene results are being fetched by the {@link GeneSearchService} so that the user does
     * not think that the application is not responding.
     */
    private final class ShowLoadingService extends Service<Void> {

        /** Time between changes in the number of ellipses periods during loading */
        private final long WAIT_TIME_MILLIS = 1000;

        /** Maximum number of ellipses periods to show, plus 1 */
        private final int MODULUS = 5;

        /** Changing number of ellipses periods to display during loading */
        private int count = 0;

        @Override
        protected final Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        if (isCancelled()) {
                            break;
                        }
                        runLater(() -> {
                            String loadingString = "Fetching data from WormBase";
                            int num = count % MODULUS;
                            for (int i = 0; i < num; i++) {
                                loadingString += ".";
                            }
                            searchResultsList.clear();
                            searchResultsList.add(loadingString);
                        });
                        try {
                            sleep(WAIT_TIME_MILLIS);
                            count++;
                            count %= MODULUS;
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