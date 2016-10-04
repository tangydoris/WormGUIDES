/*
 * Bao Lab 2016
 */

package wormguides.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import acetree.LineageData;
import connectome.Connectome;
import partslist.PartsList;
import search.SearchType;
import search.SearchUtil;
import wormguides.MainApp;
import wormguides.layers.DisplayLayer;
import wormguides.layers.SearchLayer;
import wormguides.layers.StoriesLayer;
import wormguides.layers.StructuresLayer;
import wormguides.loaders.ImageLoader;
import wormguides.models.CasesLists;
import wormguides.models.CellDeaths;
import wormguides.models.LineageTree;
import wormguides.models.ProductionInfo;
import wormguides.models.Rule;
import wormguides.models.SceneElementsList;
import wormguides.stories.Story;
import wormguides.util.StringListCellFactory;
import wormguides.view.DraggableTab;
import wormguides.view.infowindow.InfoWindow;
import wormguides.view.popups.AboutPane;
import wormguides.view.popups.StorySavePane;
import wormguides.view.popups.SulstonTreePane;
import wormguides.view.urlwindow.URLLoadWarningDialog;
import wormguides.view.urlwindow.URLLoadWindow;
import wormguides.view.urlwindow.URLWindow;

import static java.util.Collections.sort;

import static javafx.application.Platform.runLater;

import static acetree.tablelineagedata.AceTreeLineageTableLoader.getAvgXOffsetFromZero;
import static acetree.tablelineagedata.AceTreeLineageTableLoader.getAvgYOffsetFromZero;
import static acetree.tablelineagedata.AceTreeLineageTableLoader.getAvgZOffsetFromZero;
import static acetree.tablelineagedata.AceTreeLineageTableLoader.loadNucFiles;
import static acetree.tablelineagedata.AceTreeLineageTableLoader.setOriginToZero;
import static search.SearchType.CONNECTOME;
import static search.SearchType.DESCRIPTION;
import static search.SearchType.FUNCTIONAL;
import static search.SearchType.GENE;
import static search.SearchType.LINEAGE;
import static search.SearchType.MULTICELLULAR_CELL_BASED;
import static wormguides.loaders.URLLoader.process;

public class RootLayoutController extends BorderPane implements Initializable {

    private final static String UNLINEAGED_START = "Nuc";

    private final static String ROOT = "ROOT";

    /** Default transparency of 'other' entities on startup */
    private final double DEFAULT_OTHERS_OPACITY = 25;

    private RotationController rotationController;

    // Root layout's own stage
    private Stage mainStage;

    // Popup windows
    private Stage aboutStage;
    private Stage sulstonTreeStage;
    private Stage urlDisplayStage;
    private Stage urlLoadStage;
    private Stage connectomeStage;
    private Stage partsListStage;
    private Stage cellShapesIndexStage;
    private Stage cellDeathsStage;
    private Stage productionInfoStage;

    // URL generation/loading
    private URLWindow urlWindow;
    private URLLoadWindow urlLoadWindow;
    private URLLoadWarningDialog warning;

    // 3D subscene stuff
    private Window3DController window3DController;
    private SubScene subscene;
    private DoubleProperty subsceneWidth;
    private DoubleProperty subsceneHeight;

    // Panels stuff
    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private VBox displayVBox;
    @FXML
    private AnchorPane modelAnchorPane;
    @FXML
    private ScrollPane infoPane;
    @FXML
    private HBox sceneControlsBox;
    // Subscene controls
    @FXML
    private Button backwardButton, forwardButton, playButton;
    @FXML
    private Label timeLabel, totalNucleiLabel;
    @FXML
    private Slider timeSlider;
    @FXML
    private Button zoomInButton, zoomOutButton;
    // Tab
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab storiesTab;
    @FXML
    private Tab colorAndDisplayTab;
    @FXML
    private TabPane colorAndDisplayTabPane;
    @FXML
    private Tab cellsTab;
    @FXML
    private Tab structuresTab;
    @FXML
    private Tab displayTab;

    // Cells tab
    private SearchLayer searchLayer;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> searchResultsListView;
    @FXML
    private RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn, conRadioBtn, multiRadioBtn;
    private ToggleGroup typeToggleGroup;
    @FXML
    private CheckBox cellNucleusCheckBox, cellBodyCheckBox, ancestorCheckBox, descendantCheckBox;
    @FXML
    private Label descendantLabel;
    @FXML
    private AnchorPane colorPickerPane;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button addSearchBtn;

    // Connectome stuff
    private Connectome connectome;
    @FXML
    private CheckBox presynapticCheckBox, postsynapticCheckBox, electricalCheckBox, neuromuscularCheckBox;

    // Cell selection
    private StringProperty selectedName;
    // Display Layer stuff
    private DisplayLayer displayLayer;
    private BooleanProperty useInternalRules;
    @FXML
    private ListView<Rule> rulesListView;
    @FXML
    private CheckBox uniformSizeCheckBox;
    @FXML
    private Button clearAllLabelsButton;
    @FXML
    private Slider opacitySlider;
    // Structures tab
    private StructuresLayer structuresLayer;
    @FXML
    private TextField structuresSearchField;
    @FXML
    private ListView<String> structuresSearchListView;
    @FXML
    private ListView<String> allStructuresListView;
    @FXML
    private Button addStructureRuleBtn;
    @FXML
    private ColorPicker structureRuleColorPicker;
    // Cell information
    @FXML
    private Text displayedName;
    @FXML
    private Text moreInfoClickableText;
    @FXML
    private Text displayedDescription;
    // scene elements stuff
    // average x-, y- and z-coordinate offsets of nuclei from zero
    private SceneElementsList sceneElementsList;
    // Story stuff
    @FXML
    private Text displayedStory;
    @FXML
    private Text displayedStoryDescription;
    private StoriesLayer storiesLayer;
    @FXML
    private ListView<Story> storiesListView;
    @FXML
    private Button editNoteButton;
    @FXML
    private Button newStoryButton;
    @FXML
    private Button deleteStoryButton;
    private Popup exitSavePopup;

    // production information
    private ProductionInfo productionInfo;
    private int movieTimeOffset;

    // info window Stuff
    private CasesLists cases;
    private InfoWindow infoWindow;
    private BooleanProperty bringUpInfoProperty;
    private ImageView playIcon, pauseIcon;
    private IntegerProperty time;
    private IntegerProperty totalNuclei;
    private BooleanProperty playingMovie;
    // Lineage tree
    private TreeItem<String> lineageTreeRoot;
    private LineageData lineageData;
    // rotation controller
    private Stage rotationControllerStage;
    // movie capture
    @FXML
    private MenuItem captureVideoMenuItem;
    @FXML
    private MenuItem stopCaptureVideoMenuItem;
    private BooleanProperty captureVideo;
    private boolean defaultEmbryoFlag;

    // ----- Begin menu items and buttons listeners -----
    @FXML
    public void productionInfoAction() {
        if (productionInfoStage == null) {
            productionInfoStage = new Stage();
            productionInfoStage.setTitle("Experimental Data");

            if (productionInfo == null) {
                initProductionInfo();
            }

            final WebView productionInfoWebView = new WebView();
            productionInfoWebView.getEngine().loadContent(productionInfo.getProductionInfoDOM().DOMtoString());
            productionInfoWebView.setContextMenuEnabled(false);

            final VBox root = new VBox();
            root.getChildren().addAll(productionInfoWebView);
            final Scene scene = new Scene(new Group());
            scene.setRoot(root);

            productionInfoStage.setScene(scene);
            productionInfoStage.setResizable(false);
        }
        productionInfoStage.show();
    }

    @FXML
    public void menuLoadStory() {
        if (storiesLayer != null) {
            storiesLayer.loadStory();
        }
    }

    @FXML
    public void menuSaveStory() {
        if (storiesLayer != null) {
            storiesLayer.saveActiveStory();
        }
    }

    @FXML
    public void menuSaveImageAction() {
        if (window3DController != null) {
            window3DController.stillscreenCapture();
        }
    }

    @FXML
    public void menuCloseAction() {
        initCloseApplication();
    }

    @FXML
    public void menuAboutAction() {
        if (aboutStage == null) {
            aboutStage = new Stage();
            aboutStage.setScene(new Scene(new AboutPane()));
            aboutStage.setTitle("About WormGUIDES");
            aboutStage.initModality(Modality.NONE);

            aboutStage.setHeight(400.0);
            aboutStage.setWidth(300.0);
            aboutStage.setResizable(false);
        }
        aboutStage.show();
    }

    @FXML
    public void viewTreeAction() {
        if (sulstonTreeStage == null) {
            sulstonTreeStage = new Stage();
            final SulstonTreePane sp = new SulstonTreePane(
                    sulstonTreeStage,
                    searchLayer,
                    lineageData,
                    movieTimeOffset,
                    lineageTreeRoot,
                    displayLayer.getRulesList(),
                    window3DController.getColorHash(),
                    window3DController.getTimeProperty(),
                    window3DController.getContextMenuController(),
                    window3DController.getSelectedNameLabeled(),
                    defaultEmbryoFlag);

            sulstonTreeStage.setScene(new Scene(sp));
            sulstonTreeStage.setTitle("LineageTree");
            sulstonTreeStage.initModality(Modality.NONE);
            sulstonTreeStage.show();
            mainStage.show();

        } else {
            sulstonTreeStage.show();
            runLater(() -> ((Stage) sulstonTreeStage.getScene().getWindow()).toFront());
        }
    }

    @FXML
    public void generateURLAction() {
        if (urlDisplayStage == null) {
            urlDisplayStage = new Stage();

            urlWindow = new URLWindow();
            urlWindow.setScene(window3DController);
            urlWindow.getCloseButton().setOnAction(event -> urlDisplayStage.hide());

            urlDisplayStage.setScene(new Scene(urlWindow));
            urlDisplayStage.setTitle("Share Scene");
            urlDisplayStage.setResizable(false);
            urlDisplayStage.initModality(Modality.NONE);
        }

        urlWindow.resetURLs();
        urlDisplayStage.show();
    }

    @FXML
    public void loadURLAction() {
        if (urlLoadStage == null) {
            urlLoadStage = new Stage();

            urlLoadWindow = new URLLoadWindow();
            urlLoadWindow.getLoadButton().setOnAction(event -> {
                if (warning == null) {
                    warning = new URLLoadWarningDialog();
                }
                if (!warning.doNotShowAgain()) {
                    final Optional<ButtonType> result = warning.showAndWait();
                    if (result.get() == warning.getButtonTypeOkay()) {
                        urlLoadStage.hide();
                        process(urlLoadWindow.getInputURL(), window3DController, false, searchLayer);
                    }
                } else {
                    urlLoadStage.hide();
                    process(urlLoadWindow.getInputURL(), window3DController, false, searchLayer);
                }
            });
            urlLoadWindow.getCancelButton().setOnAction(event -> urlLoadStage.hide());

            urlLoadStage.setScene(new Scene(urlLoadWindow));
            urlLoadStage.setTitle("Load Scene");
            urlLoadStage.setResizable(false);
            urlLoadStage.initModality(Modality.NONE);
        }

        urlLoadWindow.clearField();
        urlLoadStage.show();
    }

    @FXML
    public void saveSearchResultsAction() {
        ObservableList<String> items = searchResultsListView.getItems();
        if (!(items.size() > 0)) {
            System.out.println("no searchLayer results to write to file");
        }

        Stage fileChooserStage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Save Location");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("TXT File", "*.txt"));

        try {
            File output = fileChooser.showSaveDialog(fileChooserStage);

            // check
            if (output == null) {
                System.out.println("error creating file to write searchLayer results");
                return;
            }

            FileWriter writer = new FileWriter(output);

            for (String s : items) {
                writer.write(s);
                writer.write(System.lineSeparator());
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("IOException thrown writing searchLayer results to file");
        }
    }

    @FXML
    public void openInfoWindow() {
        if (infoWindow == null) {
            initInfoWindow();

            if (cases == null) {
                initCases();
            } else {
                cases.setInfoWindow(infoWindow);
            }
        }

        infoWindow.showWindow();
    }

    @FXML
    public void viewCellShapesIndex() {
        if (sceneElementsList == null) {
            return;
        }

        if (cellShapesIndexStage == null) {
            cellShapesIndexStage = new Stage();
            cellShapesIndexStage.setTitle("Cell Shapes Index");

            if (sceneElementsList == null) {
                initSceneElementsList();
            }

            // webview to render cell shapes list i.e. sceneElementsList
            WebView cellShapesIndexWebView = new WebView();
            cellShapesIndexWebView.getEngine().loadContent(sceneElementsList.sceneElementsListDOM().DOMtoString());

            VBox root = new VBox();
            root.getChildren().addAll(cellShapesIndexWebView);
            Scene scene = new Scene(new Group());
            scene.setRoot(root);

            cellShapesIndexStage.setScene(scene);
            cellShapesIndexStage.setResizable(false);
        }
        cellShapesIndexStage.show();
    }

    @FXML
    public void viewCellDeaths() {
        if (cellDeathsStage == null) {
            cellDeathsStage = new Stage();
            cellDeathsStage.setWidth(400.);
            cellDeathsStage.setTitle("Cell Deaths");

            WebView cellDeathsWebView = new WebView();
            cellDeathsWebView.getEngine().loadContent(CellDeaths.getCellDeathsDOMAsString());

            VBox root = new VBox();
            root.getChildren().addAll(cellDeathsWebView);
            Scene scene = new Scene(new Group());
            scene.setRoot(root);

            cellDeathsStage.setScene(scene);
            cellDeathsStage.setResizable(false);
        }
        cellDeathsStage.show();
    }

    @FXML
    public void viewPartsList() {
        if (partsListStage == null) {
            partsListStage = new Stage();
            partsListStage.setTitle("Parts List");

            // build webview scene to render parts list
            WebView partsListWebView = new WebView();
            partsListWebView.getEngine().loadContent(PartsList.createPartsListDOM().DOMtoString());

            VBox root = new VBox();
            root.getChildren().addAll(partsListWebView);
            Scene scene = new Scene(new Group());
            scene.setRoot(root);

            partsListStage.setScene(scene);
            partsListStage.setResizable(false);
        }
        partsListStage.show();
    }

    @FXML
    public void viewConnectome() {
        if (connectomeStage == null) {
            connectomeStage = new Stage();
            connectomeStage.setTitle("Connectome");

            // build webview scene to render html
            WebView connectomeHTML = new WebView();
            connectomeHTML.getEngine().loadContent(connectome.connectomeDOM().DOMtoString());

            VBox root = new VBox();
            root.getChildren().addAll(connectomeHTML);
            Scene scene = new Scene(new Group());
            scene.setRoot(root);

            connectomeStage.setScene(scene);
            connectomeStage.setResizable(false);
        }
        connectomeStage.show();
    }
    // ----- End menu items and buttons listeners -----

    @FXML
    public void openRotationController() {
        if (rotationControllerStage == null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/layouts/RotationControllerLayout.fxml"));

            if (rotationController == null) {
                rotationController = new RotationController(window3DController.getRotateXAngleProperty(),
                        window3DController.getRotateYAngleProperty(), window3DController.getRotateZAngleProperty());
            }

            rotationControllerStage = new Stage();

            loader.setController(rotationController);

            try {
                rotationControllerStage.setScene(new Scene(loader.load()));

                rotationControllerStage.setTitle("Rotation Controller");
                rotationControllerStage.initOwner(mainStage);
                rotationControllerStage.initModality(Modality.NONE);
                rotationControllerStage.setResizable(true);

            } catch (IOException e) {
                System.out.println("error in initializing note editor.");
                e.printStackTrace();
            }
        }

        rotationControllerStage.show();
        rotationControllerStage.toFront();
    }

    @FXML
    public void captureVideo() {
        captureVideoMenuItem.setDisable(true);
        stopCaptureVideoMenuItem.setDisable(false);

        // start the image capture
        if (window3DController != null) {
            if (!window3DController.captureImagesForMovie()) {
                // error saving movie, update UI
                captureVideoMenuItem.setDisable(false);
                stopCaptureVideoMenuItem.setDisable(true);
                captureVideo.set(false);
            }
        }
    }

    @FXML
    public void stopCaptureAndSave() {
        captureVideoMenuItem.setDisable(false);
        stopCaptureVideoMenuItem.setDisable(true);
        captureVideo.set(false);

        // convert captured images to movie
        if (window3DController != null) {
            window3DController.convertImagesToMovie();
        }

    }

    public void initCloseApplication() {
        // check if there is an active story to prompt save dialog
        if (storiesLayer.getActiveStory() != null) {
            promptStorySave();
        } else {
            exitApplication();
        }

    }

    private void promptStorySave() {
        if (storiesLayer != null && storiesLayer.getActiveStory() != null) {
            if (exitSavePopup == null) {
                final StorySavePane saveDialog = new StorySavePane(
                        "Would you like to save the current active story before exiting WormGUIDES?",
                        "Yes",
                        "No",
                        "Cancel");

                exitSavePopup = new Popup();
                exitSavePopup.getContent().add(saveDialog);

                saveDialog.setYesButtonAction(event -> {
                    exitSavePopup.hide();
                    storiesLayer.saveActiveStory();
                    exitApplication();
                });

                saveDialog.setNoButtonAction(event -> {
                    exitSavePopup.hide();
                    exitApplication();
                });

                saveDialog.setCancelButtonAction(event -> exitSavePopup.hide());

                exitSavePopup.setAutoFix(true);
            }

            exitSavePopup.show(mainStage);
            exitSavePopup.centerOnScreen();
        }
    }

    private void exitApplication() {
        System.out.println("Exiting...");
        if (!defaultEmbryoFlag) {
            sulstonTreeStage.hide();
            mainStage.hide();
            return;
        }
        System.exit(0);
    }

    public void init3DWindow(LineageData data) {
        if (cases == null) {
            initCases();
        }
        if (productionInfo == null) {
            initProductionInfo();
        }
        if (connectome == null) {
            initConnectome();
        }

        // for context menu
        // info window
        bringUpInfoProperty = new SimpleBooleanProperty(false);

        double[] xyzScale = lineageData.getXYZScale();
        window3DController = new Window3DController(
                mainStage,
                modelAnchorPane,
                data,
                cases,
                productionInfo,
                connectome,
                bringUpInfoProperty,
                getAvgXOffsetFromZero(),
                getAvgYOffsetFromZero(),
                getAvgZOffsetFromZero(),
                defaultEmbryoFlag,
                xyzScale[0],
                xyzScale[1],
                xyzScale[2],
                modelAnchorPane,
                backwardButton,
                forwardButton,
                zoomOutButton,
                zoomInButton,
                clearAllLabelsButton,
                searchField,
                opacitySlider,
                uniformSizeCheckBox,
                cellNucleusCheckBox,
                cellBodyCheckBox,
                multiRadioBtn);

        subscene = window3DController.getSubScene();
        setPropertiesFrom3DWindow();
    }

    private void setPropertiesFrom3DWindow() {
        time = window3DController.getTimeProperty();
        window3DController.getZoomProperty();
        totalNuclei = window3DController.getTotalNucleiProperty();
        playingMovie = window3DController.getPlayingMovieProperty();
        selectedName = window3DController.getSelectedName();
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    private void addListeners() {
        // time integer property that dictates the current time point
        time.addListener((observable, oldValue, newValue) -> {
            timeSlider.setValue(time.get());
            if (time.get() >= window3DController.getEndTime() - 1) {
                playButton.setGraphic(playIcon);
                playingMovie.set(false);
            }
        });

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newTime = newValue.intValue();
            if (window3DController != null) {
                window3DController.setTime(newTime);
            }
        });

        // searchLayer stuff
        searchResultsListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> selectedName.set(newValue));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                mainTabPane.getSelectionModel().select(colorAndDisplayTab);
                colorAndDisplayTabPane.getSelectionModel().select(cellsTab);
            }
        });

        // selectedName string property that has the name of the clicked sphere
        selectedName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.isEmpty()) {
                    setSelectedEntityInfo(selectedName.get());
                }
            }
        });

        // Multicellular structure stuff
        structuresSearchListView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);

        // Modify font for ListView's of String's
        structuresSearchListView.setCellFactory(new StringListCellFactory());
        allStructuresListView.setCellFactory(structuresLayer.getCellFactory());
        searchResultsListView.setCellFactory(new StringListCellFactory());

        timeSlider.setValue(0);

        // 'Others' opacity
        opacitySlider.setValue(DEFAULT_OTHERS_OPACITY);

        // Uniform nuclei size
        uniformSizeCheckBox.setSelected(true);

        // Cell Nucleus searchLayer option
        cellNucleusCheckBox.setSelected(true);

        // More info clickable text
        moreInfoClickableText.setOnMouseClicked(event -> {
            openInfoWindow();
            infoWindow.addName(selectedName.get());
        });
        moreInfoClickableText.setOnMouseEntered(event -> moreInfoClickableText.setCursor(Cursor.HAND));
        moreInfoClickableText.setOnMouseExited(event -> moreInfoClickableText.setCursor(Cursor.DEFAULT));

        // More info in context menu
        bringUpInfoProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                openInfoWindow();
                infoWindow.addName(selectedName.get());
                infoWindow.showWindow();
            }
        });
    }

    private void setSelectedEntityInfo(String name) {
        if (name == null || name.isEmpty()) {
            displayedName.setText("Active Cell: none");
            moreInfoClickableText.setVisible(false);
            displayedDescription.setText("");
            return;
        }

        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("("));
        }
        name = name.trim();

        displayedName.setText("Active Cell: " + name);
        moreInfoClickableText.setVisible(true);
        displayedDescription.setText("");

        // Note
        if (storiesLayer != null) {
            displayedDescription.setText(storiesLayer.getNoteComments(name));
        }

        // Cell body/structue
        if (SearchUtil.isStructureWithComment(name)) {
            displayedDescription.setText(SearchUtil.getStructureComment(name));
        }

        // Cell lineage name
        else {
            String functionalName = PartsList.getFunctionalNameByLineageName(name);

            if (functionalName != null) {
                displayedName.setText("Active Cell: " + name + " (" + functionalName + ")");
                displayedDescription.setText(PartsList.getDescriptionByFunctionalName(functionalName));
            } else if (CellDeaths.containsCell(name)) {
                displayedName.setText("Active Cell: " + name);
                displayedDescription.setText("Cell Death");
            }
        }
    }

    private void sizeSubscene() {
        this.subsceneWidth = new SimpleDoubleProperty();
        subsceneWidth.bind(modelAnchorPane.widthProperty());
        this.subsceneHeight = new SimpleDoubleProperty();
        subsceneHeight.bind(modelAnchorPane.heightProperty());

        AnchorPane.setTopAnchor(subscene, 0.0);
        AnchorPane.setLeftAnchor(subscene, 0.0);
        AnchorPane.setRightAnchor(subscene, 0.0);
        AnchorPane.setBottomAnchor(subscene, 0.0);

        subscene.widthProperty().bind(subsceneWidth);
        subscene.heightProperty().bind(subsceneHeight);
        subscene.setManaged(false);
    }

    private void sizeInfoPane() {
        infoPane.prefHeightProperty().bind(displayVBox.heightProperty().divide(6.5));
        displayedDescription.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
        displayedStory.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
        displayedStoryDescription.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
    }

    private void setLabels() {
        time.addListener((observable, oldValue, newValue) -> {
            if (defaultEmbryoFlag) {
                timeLabel.setText("~" + (time.get() + movieTimeOffset) + " min p.f.c.");
            } else {
                timeLabel.setText("~" + (time.get()) + " min");
            }

        });
        timeLabel.setText("~" + (time.get() + movieTimeOffset) + " min p.f.c.");
        timeLabel.toFront();

        totalNuclei.addListener((observable, oldValue, newValue) -> {
            String suffix = " Nuclei";
            if (newValue.intValue() == 1) {
                suffix = " Nucleus";
            }
            totalNucleiLabel.setText(newValue.intValue() + suffix);
        });
        totalNucleiLabel.setText(totalNuclei.get() + " Nuclei");
        totalNucleiLabel.toFront();
    }

    public void setIcons() {
        backwardButton.setGraphic(ImageLoader.getBackwardIcon());
        forwardButton.setGraphic(ImageLoader.getForwardIcon());
        zoomInButton.setGraphic(new ImageView(ImageLoader.getPlusIcon()));
        zoomOutButton.setGraphic(new ImageView(ImageLoader.getMinusIcon()));

        playIcon = ImageLoader.getPlayIcon();
        pauseIcon = ImageLoader.getPauseIcon();
        playButton.setGraphic(playIcon);
        playButton.setOnAction(event -> {
            playingMovie.set(!playingMovie.get());

            if (playingMovie.get()) {
                playButton.setGraphic(pauseIcon);
            } else {
                playButton.setGraphic(playIcon);
            }
        });
    }

    private void setSlidersProperties() {
        if (defaultEmbryoFlag) {
            timeSlider.setMin(1);
        } else {
            timeSlider.setMin(0);
        }

        timeSlider.setMax(window3DController.getEndTime());

        opacitySlider.setMin(0);
        opacitySlider.setMax(100);
        opacitySlider.setValue(DEFAULT_OTHERS_OPACITY);
    }

    private void initSearchLayer(final ObservableList<Rule> rulesList) {
        searchLayer = new SearchLayer(
                rulesList,
                searchField,
                typeToggleGroup,
                presynapticCheckBox,
                postsynapticCheckBox,
                neuromuscularCheckBox,
                electricalCheckBox,
                cellNucleusCheckBox,
                cellBodyCheckBox,
                ancestorCheckBox,
                descendantCheckBox,
                colorPicker,
                addSearchBtn);
        searchLayer.addDefaultColorRules();
        window3DController.setSearchLayer(searchLayer);
    }

    private void initDisplayLayer() {
        useInternalRules = new SimpleBooleanProperty(true);
        displayLayer = new DisplayLayer(useInternalRules);
        rulesListView.setItems(displayLayer.getRulesList());
        rulesListView.setCellFactory(displayLayer.getRuleCellFactory());
    }

    private void initPartsList() {
        new PartsList();
    }

    private void initCellDeaths() {
        new CellDeaths();
    }

    private void initLineageTree(List<String> allCellNames) {
        if (!defaultEmbryoFlag) {
            // remove unlineaged cells
            for (int i = 0; i < allCellNames.size(); i++) {
                if (allCellNames.get(i).toLowerCase().startsWith(UNLINEAGED_START.toLowerCase())
                        || allCellNames.get(i).toLowerCase().startsWith(ROOT.toLowerCase())) {
                    allCellNames.remove(i--);
                }
            }

            //sort the lineage names that remain
            sort(allCellNames);
        }

        final LineageTree lineageTree = new LineageTree(
                allCellNames.toArray(new String[allCellNames.size()]),
                lineageData.isSulstonMode());
        lineageTreeRoot = lineageTree.getRoot();
    }

    private void initToggleGroup() {
        typeToggleGroup = new ToggleGroup();
        sysRadioBtn.setToggleGroup(typeToggleGroup);
        sysRadioBtn.setUserData(LINEAGE);
        funRadioBtn.setToggleGroup(typeToggleGroup);
        funRadioBtn.setUserData(FUNCTIONAL);
        desRadioBtn.setToggleGroup(typeToggleGroup);
        desRadioBtn.setUserData(DESCRIPTION);
        genRadioBtn.setToggleGroup(typeToggleGroup);
        genRadioBtn.setUserData(GENE);
        conRadioBtn.setToggleGroup(typeToggleGroup);
        conRadioBtn.setUserData(CONNECTOME);
        multiRadioBtn.setToggleGroup(typeToggleGroup);
        multiRadioBtn.setUserData(MULTICELLULAR_CELL_BASED);

        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            SearchType type = (SearchType) observable.getValue().getToggleGroup().getSelectedToggle().getUserData();
            if (type == FUNCTIONAL || type == DESCRIPTION) {
                descendantCheckBox.setSelected(false);
                descendantCheckBox.disableProperty().set(true);
                descendantLabel.disableProperty().set(true);
            } else {
                descendantCheckBox.disableProperty().set(false);
                descendantLabel.disableProperty().set(false);
            }
        });
        sysRadioBtn.setSelected(true);
    }

    private void initStructuresLayer(final ObservableList<Rule> rulesList) {
        structuresLayer = new StructuresLayer(
                searchLayer,
                sceneElementsList,
                structuresSearchField,
                structuresSearchListView,
                allStructuresListView,
                addStructureRuleBtn,
                structureRuleColorPicker);

        structuresLayer.addSelectedNameListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                selectedName.set(newValue);
            }
        });
    }

    private void initStoriesLayer(final ObservableList<Rule> rulesList) {
        if (structuresLayer == null) {
            initStructuresLayer(rulesList);
        }

        storiesLayer = new StoriesLayer(
                mainStage,
                searchLayer,
                sceneElementsList,
                selectedName,
                lineageData,
                window3DController,
                useInternalRules,
                productionInfo.getMovieTimeOffset(),
                newStoryButton,
                deleteStoryButton,
                editNoteButton,
                defaultEmbryoFlag);

        window3DController.setStoriesLayer(storiesLayer);

        storiesListView.setItems(storiesLayer.getStories());
        storiesListView.setCellFactory(storiesLayer.getStoryCellFactory());
        storiesListView.widthProperty().addListener(storiesLayer.getListViewWidthListener());

        window3DController.addListenerToRebuildSceneFlag(storiesLayer.getRebuildSceneFlag());

        storiesLayer.getActiveStoryProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                displayedStory.setText("Active Story: none");
                displayedStoryDescription.setText("");
            } else {
                displayedStory.setText("Active Story: " + newValue);
                displayedStoryDescription.setText(storiesLayer.getActiveStoryDescription());
            }
        });
        displayedStory.setText("Active Story: " + storiesLayer.getActiveStory().getName());
        displayedStoryDescription.setText(storiesLayer.getActiveStoryDescription());
    }

    /**
     * Initializes the {@link SceneElementsList} that contains all the {@link wormguides.models.SceneElement} objects
     * visible in all time frames.
     */
    private void initSceneElementsList() {
        sceneElementsList = new SceneElementsList();

        if (window3DController != null) {
            window3DController.setSceneElementsList(sceneElementsList);
        }
    }

    private void initConnectome() {
        connectome = new Connectome();
    }

    private void initInfoWindow() {
        if (window3DController != null) {

            if (connectome == null) {
                initConnectome();
            }
            if (productionInfo == null) {
                initProductionInfo();
            }
            if (cases == null) {
                initCases();
            }

            infoWindow = new InfoWindow(
                    window3DController.getStage(),
                    searchLayer,
                    window3DController.getSelectedNameLabeled(),
                    cases,
                    productionInfo,
                    connectome,
                    defaultEmbryoFlag,
                    lineageData);
        }
    }

    private void initCases() {
        cases = new CasesLists(infoWindow);
    }

    private void initProductionInfo() {
        productionInfo = new ProductionInfo();

        if (defaultEmbryoFlag) {
            movieTimeOffset = productionInfo.getMovieTimeOffset();
        } else {
            movieTimeOffset = 0;
        }
    }

    /**
     * Replaces all application tabs with dockable ones ({@link DraggableTab})
     */
    private void replaceTabsWithDraggableTabs() {
        DraggableTab cellsDragTab = new DraggableTab(cellsTab.getText());
        cellsDragTab.setCloseable(false);
        cellsDragTab.setContent(cellsTab.getContent());

        DraggableTab structuresDragTab = new DraggableTab(structuresTab.getText());
        structuresDragTab.setCloseable(false);
        structuresDragTab.setContent(structuresTab.getContent());

        DraggableTab displayDragTab = new DraggableTab(displayTab.getText());
        displayDragTab.setCloseable(false);
        displayDragTab.setContent(displayTab.getContent());

        colorAndDisplayTabPane.getTabs().clear();
        cellsTab = cellsDragTab;
        structuresTab = structuresDragTab;
        displayTab = displayDragTab;

        colorAndDisplayTabPane.getTabs().addAll(cellsTab, structuresTab, displayTab);

        DraggableTab storiesDragTab = new DraggableTab(storiesTab.getText());
        storiesDragTab.setCloseable(false);
        storiesDragTab.setContent(storiesTab.getContent());

        DraggableTab colorAndDisplayDragTab = new DraggableTab(colorAndDisplayTab.getText());
        colorAndDisplayDragTab.setCloseable(false);
        colorAndDisplayDragTab.setContent(colorAndDisplayTab.getContent());

        mainTabPane.getTabs().clear();
        storiesTab = storiesDragTab;
        colorAndDisplayTab = colorAndDisplayDragTab;

        mainTabPane.getTabs().addAll(storiesTab, colorAndDisplayTab);
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        initProductionInfo();

        if (bundle != null) {
            lineageData = (LineageData) bundle.getObject("lineageData");
            defaultEmbryoFlag = false;
            setOriginToZero(lineageData, defaultEmbryoFlag);

        } else {
            lineageData = loadNucFiles(productionInfo);
            defaultEmbryoFlag = true;
            lineageData.setIsSulstonModeFlag(productionInfo.getIsSulstonFlag());
        }

        // takes about 58ms
        replaceTabsWithDraggableTabs();

        // takes about 10ms
        initPartsList();

        // takes about 6ms
        initCellDeaths();

        // takes about 5ms
        initToggleGroup();

        // takes about 3ms
        initDisplayLayer();

        // takes about 1050ms
        initializeWithLineageData();

        mainTabPane.getSelectionModel().select(storiesTab);
    }

    public void initializeWithLineageData() {
        initLineageTree(lineageData.getAllCellNames());

        init3DWindow(lineageData);

        setSlidersProperties();

        ObservableList<Rule> rulesList = displayLayer.getRulesList();
        initSearchLayer(rulesList);
        window3DController.setRulesList(rulesList);

        initSceneElementsList();
        initConnectome();
        initStoriesLayer(rulesList);

        searchLayer.initDatabases(lineageData, sceneElementsList, connectome, cases, productionInfo);

        final ObservableList<String> searchRsultsList = searchLayer.getSearchResultsList();
        window3DController.setSearchResultsList(searchRsultsList);
        searchResultsListView.setItems(searchRsultsList);

        window3DController.setSearchResultsUpdateService(searchLayer.getResultsUpdateService());
        window3DController.setGeneResultsUpdated(searchLayer.getGeneResultsUpdated());

        addListeners();

        setIcons();
        setLabels();

        sizeSubscene();
        sizeInfoPane();

        timeSlider.setValue(window3DController.getEndTime());

        viewTreeAction();

        captureVideo = new SimpleBooleanProperty(false);
        window3DController.setCaptureVideo(captureVideo);
    }
}