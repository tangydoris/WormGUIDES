/*
 * Bao Lab 2016
 */

/*
 * Bao Lab 2016
 */

package wormguides.layers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import wormguides.models.subscenegeometry.SceneElementsList;
import wormguides.models.subscenegeometry.StructureTreeNode;

import static java.lang.Double.MAX_VALUE;
import static java.util.Objects.requireNonNull;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;
import static javafx.scene.paint.Color.WHITE;

import static partslist.PartsList.getLineageNamesByFunctionalName;
import static wormguides.util.AppFont.getBolderFont;
import static wormguides.util.AppFont.getFont;

public class StructuresLayer {

    private final SearchLayer searchLayer;
    private final SceneElementsList sceneElementsList;

    private final ObservableList<String> searchStructuresResultsList;

    private final TreeView<StructureTreeNode> allStructuresTreeView;

    private final Map<String, List<String>> nameToCellsMap;
    private final Map<String, String> nameToCommentsMap;
    private final Map<String, StructureCellGraphic> structureNameToTreeCellMap;

    private final StringProperty selectedStructureNameProperty;

    private final TextField searchField;

    private Color selectedColor;
    private String searchText;

    public StructuresLayer(
            final SearchLayer searchLayer,
            final SceneElementsList sceneElementsList,
            final StringProperty selectedEntityNameProperty,
            final TextField searchField,
            final ListView<String> structuresSearchResultsListView,
            final TreeView<StructureTreeNode> allStructuresTreeView,
            final Button addStructureRuleButton,
            final ColorPicker colorPicker,
            final BooleanProperty rebuildSceneFlag) {

        selectedColor = WHITE;

        searchStructuresResultsList = observableArrayList();

        structureNameToTreeCellMap = new HashMap<>();

        requireNonNull(selectedEntityNameProperty);
        selectedStructureNameProperty = new SimpleStringProperty("");
        selectedStructureNameProperty.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                selectedEntityNameProperty.set(newValue);
            }
        });

        this.searchLayer = requireNonNull(searchLayer);

        this.sceneElementsList = requireNonNull(sceneElementsList);
        nameToCellsMap = this.sceneElementsList.getNameToCellsMap();
        nameToCommentsMap = this.sceneElementsList.getNameToCommentsMap();

        this.searchField = requireNonNull(searchField);
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue.toLowerCase();
            if (searchText.isEmpty()) {
                searchStructuresResultsList.clear();
            } else {
                selectedStructureNameProperty.set("");
                deselectAllStructures();
                searchAndUpdateResults(newValue.toLowerCase());
            }
        });

        requireNonNull(structuresSearchResultsListView).setItems(searchStructuresResultsList);

        this.allStructuresTreeView = requireNonNull(allStructuresTreeView);
        this.allStructuresTreeView.setShowRoot(false);
        this.allStructuresTreeView.setRoot(sceneElementsList.getTreeRoot());
        this.allStructuresTreeView.setCellFactory(new StructureTreeCellFactory());
        this.allStructuresTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        final StructureTreeNode selectedNode = newValue.getValue();
                        if (!selectedNode.isCategoryNode()) {
                            searchField.clear();
                            selectedStructureNameProperty.set(selectedNode.getNodeText());
                        }
                    }
                });

        requireNonNull(rebuildSceneFlag);
        requireNonNull(addStructureRuleButton).setOnAction(event -> {
            final String name = selectedStructureNameProperty.get();
            if (!name.isEmpty()) {
                addStructureRule(name, selectedColor);
                deselectAllStructures();
            } else {
                // if no name is selected, add all results from search
                for (String string : searchStructuresResultsList) {
                    addStructureRule(string, selectedColor);
                }
                searchField.clear();
            }
            rebuildSceneFlag.set(true);
        });
        requireNonNull(colorPicker).setOnAction(event -> selectedColor = ((ColorPicker) event.getSource()).getValue());
    }

    /**
     * Deselects any structure in the tree that was active
     */
    private void deselectAllStructures() {
        allStructuresTreeView.getSelectionModel().clearSelection();
    }

    public void addStructureRule(String name, Color color) {
        if (name == null || color == null) {
            return;
        }
        // check for validity of name
        name = name.trim();
        if (sceneElementsList.getAllSceneNames().contains(name)) {
            searchLayer.addStructureRuleBySceneName(name, color);
        }
    }

    /**
     * Searches for scene elements (single-celled and multicellular) whose scene name or comment is specified by the
     * searched term. The search results list is updated with those structure scene names.
     *
     * @param searched
     *         the searched term
     */
    public void searchAndUpdateResults(String searched) {
        if (searched == null || searched.isEmpty()) {
            return;
        }

        String[] terms = searched.toLowerCase().split(" ");
        searchStructuresResultsList.clear();

        for (String name : sceneElementsList.getAllSceneNames()) {

            if (!searchStructuresResultsList.contains(name)) {
                // search in structure scene names
                String nameLower = name.toLowerCase();

                boolean appliesToName = true;
                boolean appliesToCell = false;
                boolean appliesToComment = false;

                for (String term : terms) {
                    if (!nameLower.contains(term)) {
                        appliesToName = false;
                        break;
                    }
                }

                // search in cells
                List<String> cells = nameToCellsMap.get(nameLower);
                if (cells != null) {
                    for (String cell : cells) {
                        // use the first term
                        if (terms.length > 0) {
                            // check if search term is a functional name
                            final List<String> lineageNames = new ArrayList<>(
                                    getLineageNamesByFunctionalName(terms[0]));
                            for (String lineageName : lineageNames) {
                                if (lineageName != null) {
                                    if (cell.toLowerCase().startsWith(lineageName.toLowerCase())) {
                                        appliesToCell = true;
                                        break;
                                    }
                                } else {
                                    if (cell.toLowerCase().startsWith(terms[0].toLowerCase())) {
                                        appliesToCell = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                // search in comments if name does not already apply
                if (nameToCommentsMap.containsKey(nameLower)) {
                    final String commentLowerCase = nameToCommentsMap.get(nameLower).toLowerCase();
                    for (String term : terms) {
                        if (commentLowerCase.contains(term)) {
                            appliesToComment = true;
                        } else {
                            appliesToComment = false;
                            break;
                        }
                    }
                }

                if (appliesToName || appliesToCell || appliesToComment) {
                    searchStructuresResultsList.add(name);
                }
            }
        }
    }

    public StringProperty getSelectedStructureNameProperty() {
        return selectedStructureNameProperty;
    }

    public String getSearchText() {
        return searchText;
    }

    /**
     * Callback for TreeCell<String> so that fonts are uniform
     */
    private class StructureTreeCellFactory
            implements Callback<TreeView<StructureTreeNode>, TreeCell<StructureTreeNode>> {

        @Override
        public TreeCell<StructureTreeNode> call(TreeView<StructureTreeNode> param) {
            return new StructureTreeCell();
        }
    }

    private class StructureTreeCell extends TreeCell<StructureTreeNode> {

        @Override
        protected void updateItem(final StructureTreeNode item, final boolean empty) {
            super.updateItem(item, empty);
            setContentDisplay(GRAPHIC_ONLY);
            setFocusTraversable(false);
            if (item != null && !empty) {
                final StructureCellGraphic graphic = new StructureCellGraphic(item);
                setGraphic(graphic);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Graphical representation of a structure list cell (not including the expansion arrow)
     */
    private class StructureCellGraphic extends HBox {

        private Label label;

        public StructureCellGraphic(final StructureTreeNode treeNode) {
            super();
            label = new Label(requireNonNull(treeNode).getNodeText());
            if (treeNode.isCategoryNode()) {
                label.setFont(getBolderFont());
            } else {
                label.setFont(getFont());
            }
            getChildren().add(label);
            setMaxWidth(MAX_VALUE);
            setPickOnBounds(false);
        }
    }
}
