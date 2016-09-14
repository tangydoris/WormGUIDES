/*
 * Bao Lab 2016
 */

package wormguides.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
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

import wormguides.MainApp;
import wormguides.Search;
import wormguides.StringListCellFactory;
import wormguides.layers.DisplayLayer;
import wormguides.layers.SearchType;
import wormguides.layers.StoriesLayer;
import wormguides.layers.StructuresLayer;
import wormguides.loaders.ImageLoader;
import wormguides.loaders.URLLoader;
import wormguides.models.Anatomy;
import wormguides.models.CasesLists;
import wormguides.models.CellDeaths;
import wormguides.models.Connectome;
import wormguides.models.LineageTree;
import wormguides.models.ProductionInfo;
import wormguides.models.Rule;
import wormguides.models.SceneElementsList;
import wormguides.models.Story;
import wormguides.view.AboutPane;
import wormguides.view.DraggableTab;
import wormguides.view.InfoWindow;
import wormguides.view.SulstonTreePane;
import wormguides.view.URLLoadWarningDialog;
import wormguides.view.URLLoadWindow;
import wormguides.view.URLWindow;
import wormguides.view.YesNoCancelDialogPane;

import acetree.AceTreeLoader;
import acetree.lineagedata.LineageData;
import partslist.PartsList;

public class RootLayoutController extends BorderPane implements Initializable {

    private final static String unLineagedStart = "Nuc";
    private final static String ROOT = "ROOT";
    /** Default transparency of 'other' entities on startup */
    private final double DEFAULT_OTHERS_OPACITY = 25;
    RotationController rotationController;
    // Root layout's own stage
    private Stage mainStage;
    // Popup windows
    private Stage aboutStage;
    private Stage treeStage;
    private Stage urlStage;
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
    private Search search;
    @FXML
    private TextField searchField;
    private BooleanProperty clearSearchField;
    @FXML
    private ListView<String> searchResultsListView;
    @FXML
    private RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn, conRadioBtn, multiRadioBtn;
    private ToggleGroup typeToggleGroup;
    @FXML
    private CheckBox cellNucleusTick, cellBodyTick, ancestorTick, descendantTick;
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
    private CheckBox presynapticTick, postsynapticTick, electricalTick, neuromuscularTick;
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
    private SceneElementsList elementsList;
    // Story stuff
    @FXML
    private Text displayedStory;
    @FXML
    private Text displayedStoryDescription;
    private StoriesLayer storiesLayer;
    @FXML
    private ListView<Story> storiesListView;
    @FXML
    private Button noteEditorBtn;
    @FXML
    private Button newStory;
    @FXML
    private Button deleteStory;
    private Popup exitSavePopup;
    // production information
    private ProductionInfo productionInfo;
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

            WebView productionInfoWebView = new WebView();
            productionInfoWebView.getEngine().loadContent(productionInfo.getProductionInfoDOM().DOMtoString());
            productionInfoWebView.setContextMenuEnabled(false);

            VBox root = new VBox();
            root.getChildren().addAll(productionInfoWebView);
            Scene scene = new Scene(new Group());
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
        if (treeStage == null) {
            treeStage = new Stage();
            SulstonTreePane sp = new SulstonTreePane(treeStage, lineageData, lineageTreeRoot,
                    displayLayer.getRulesList(), window3DController.getColorHash(),
                    window3DController.getTimeProperty(), window3DController.getContextMenuController(),
                    window3DController.getSelectedNameLabeled(), defaultEmbryoFlag);

            treeStage.setScene(new Scene(sp));
            treeStage.setTitle("LineageTree");
            treeStage.initModality(Modality.NONE);
            treeStage.show();
            mainStage.show();
        } else {
            treeStage.show();
            Platform.runLater(() -> ((Stage) treeStage.getScene().getWindow()).toFront());
        }
    }

    @FXML
    public void generateURLAction() {
        if (urlStage == null) {
            urlStage = new Stage();

            urlWindow = new URLWindow();
            urlWindow.setScene(window3DController);
            urlWindow.getCloseButton().setOnAction(event -> urlStage.hide());

            urlStage.setScene(new Scene(urlWindow));
            urlStage.setTitle("Share Scene");
            urlStage.setResizable(false);
            urlStage.initModality(Modality.NONE);
        }

        urlWindow.resetURLs();
        urlStage.show();
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
                    Optional<ButtonType> result = warning.showAndWait();
                    if (result.get() == warning.getButtonTypeOkay()) {
                        urlLoadStage.hide();
                        URLLoader.process(urlLoadWindow.getInputURL(), window3DController, false);
                    }
                } else {
                    urlLoadStage.hide();
                    URLLoader.process(urlLoadWindow.getInputURL(), window3DController, false);
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
            System.out.println("no search results to write to file");
        }

        Stage fileChooserStage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Save Location");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("TXT File", "*.txt"));

        try {
            File output = fileChooser.showSaveDialog(fileChooserStage);

            // check
            if (output == null) {
                System.out.println("error creating file to write search results");
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
            System.out.println("IOException thrown writing search results to file");
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
        if (elementsList == null) {
            return;
        }

        if (cellShapesIndexStage == null) {
            cellShapesIndexStage = new Stage();
            cellShapesIndexStage.setTitle("Cell Shapes Index");

            if (elementsList == null) {
                initSceneElementsList();
            }

            // webview to render cell shapes list i.e. elementsList
            WebView cellShapesIndexWebView = new WebView();
            cellShapesIndexWebView.getEngine().loadContent(elementsList.sceneElementsListDOM().DOMtoString());

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

    public void promptStorySave() {
        if (storiesLayer != null && storiesLayer.getActiveStory() != null) {
            if (exitSavePopup == null) {

                YesNoCancelDialogPane saveDialog = new YesNoCancelDialogPane(
                        "Would you like to save the current active story before exiting WormGUIDES?", "Yes", "No",
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

    /*
     * TODO
     * refactor defaultEmbryoFlag --> default model, not where application was opened from
     */
    private void exitApplication() {
        System.out.println("exiting...");
        if (!defaultEmbryoFlag) {
            treeStage.hide();
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

        window3DController = new Window3DController(mainStage, modelAnchorPane, data, cases, productionInfo, connectome,
                bringUpInfoProperty, AceTreeLoader.getAvgXOffsetFromZero(), AceTreeLoader.getAvgYOffsetFromZero(),
                AceTreeLoader.getAvgZOffsetFromZero(), defaultEmbryoFlag);
        subscene = window3DController.getSubScene();

        modelAnchorPane.setOnMouseClicked(window3DController.getNoteClickHandler());

        backwardButton.setOnAction(window3DController.getBackwardButtonListener());
        forwardButton.setOnAction(window3DController.getForwardButtonListener());
        zoomOutButton.setOnAction(window3DController.getZoomOutButtonListener());
        zoomInButton.setOnAction(window3DController.getZoomInButtonListener());

        window3DController.setSearchField(searchField);

        // slider has to listen to 3D window's opacity value
        // 3d window's opacity value has to listen to opacity slider's value
        opacitySlider.valueProperty().addListener(window3DController.getOthersOpacityListener());
        window3DController.addListenerToOpacitySlider(opacitySlider);

        uniformSizeCheckBox.selectedProperty().addListener(window3DController.getUniformSizeCheckBoxListener());
        clearAllLabelsButton.setOnAction(window3DController.getClearAllLabelsButtonListener());

        cellNucleusTick.selectedProperty().addListener(window3DController.getCellNucleusTickListener());
        cellBodyTick.selectedProperty().addListener(window3DController.getCellBodyTickListener());

        multiRadioBtn.selectedProperty().addListener(window3DController.getMulticellModeListener());
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
            if (window3DController != null) // removed newTime !=
            // timeSlider.getValue() && -->
            // to use arrow keys b/c arrows
            // automatically update
            // timeSlider.value
            {
                window3DController.setTime(newTime);
            }
        });

        // search stuff
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

        // Cell Nucleus search option
        cellNucleusTick.setSelected(true);

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
        if (Search.isStructureWithComment(name)) {
            displayedDescription.setText(Search.getStructureComment(name));
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
        int timeOffset;
        if (defaultEmbryoFlag) {
            timeOffset = productionInfo.getMovieTimeOffset();
        } else {
            timeOffset = 0;
        }

        time.addListener((observable, oldValue, newValue) -> {
            if (defaultEmbryoFlag) {
                timeLabel.setText("~" + (time.get() + timeOffset) + " min p.f.c.");
            } else {
                timeLabel.setText("~" + (time.get()) + " min");
            }

        });
        timeLabel.setText("~" + (time.get() + timeOffset) + " min p.f.c.");
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

    private void initSearch() {
        search = new Search();

        typeToggleGroup.selectedToggleProperty().addListener(Search.getTypeToggleListener());

        // connectome checkboxes
        presynapticTick.selectedProperty().addListener(Search.getPresynapticTickListener());
        postsynapticTick.selectedProperty().addListener(Search.getPostsynapticTickListener());
        electricalTick.selectedProperty().addListener(Search.getElectricalTickListener());
        neuromuscularTick.selectedProperty().addListener(Search.getNeuromuscularTickListener());

        cellNucleusTick.selectedProperty().addListener(Search.getCellNucleusTickListener());
        cellBodyTick.selectedProperty().addListener(Search.getCellBodyTickListener());
        ancestorTick.selectedProperty().addListener(Search.getAncestorTickListner());
        descendantTick.selectedProperty().addListener(Search.getDescendantTickListner());
        colorPicker.setOnAction(search.getColorPickerListener());
        addSearchBtn.setOnAction(Search.getAddButtonListener());

        clearSearchField = new SimpleBooleanProperty(false);
        Search.setClearSearchFieldProperty(clearSearchField);
        clearSearchField.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchField.clear();
                clearSearchField.set(false);
            }
        });

        searchField.textProperty().addListener(Search.getTextFieldListener());
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

    private void initAnatomy() {
        new Anatomy();
    }

    private void initLineageTree(ArrayList<String> allCellNames) {
        if (!defaultEmbryoFlag) {
            // remove unlineaged cells
            for (int i = 0; i < allCellNames.size(); i++) {
                if (allCellNames.get(i).toLowerCase().startsWith(unLineagedStart.toLowerCase()) ||
                        allCellNames.get(i).toLowerCase().startsWith(ROOT.toLowerCase())) {
                    allCellNames.remove(i--);
                }
            }

            //sort the lineage names that remain
            Collections.sort(allCellNames);
        }

        new LineageTree(allCellNames.toArray(new String[allCellNames.size()]), lineageData.isSulstonMode());
        lineageTreeRoot = LineageTree.getRoot();
    }

    private void initToggleGroup() {
        typeToggleGroup = new ToggleGroup();
        sysRadioBtn.setToggleGroup(typeToggleGroup);
        sysRadioBtn.setUserData(SearchType.LINEAGE);
        funRadioBtn.setToggleGroup(typeToggleGroup);
        funRadioBtn.setUserData(SearchType.FUNCTIONAL);
        desRadioBtn.setToggleGroup(typeToggleGroup);
        desRadioBtn.setUserData(SearchType.DESCRIPTION);
        genRadioBtn.setToggleGroup(typeToggleGroup);
        genRadioBtn.setUserData(SearchType.GENE);
        conRadioBtn.setToggleGroup(typeToggleGroup);
        conRadioBtn.setUserData(SearchType.CONNECTOME);
        multiRadioBtn.setToggleGroup(typeToggleGroup);
        multiRadioBtn.setUserData(SearchType.MULTICELLULAR_CELL_BASED);

        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            SearchType type = (SearchType) observable.getValue().getToggleGroup().getSelectedToggle().getUserData();
            if (type == SearchType.FUNCTIONAL || type == SearchType.DESCRIPTION) {
                descendantTick.setSelected(false);
                descendantTick.disableProperty().set(true);
                descendantLabel.disableProperty().set(true);
            } else {
                descendantTick.disableProperty().set(false);
                descendantLabel.disableProperty().set(false);
            }
        });
        sysRadioBtn.setSelected(true);
    }

    private void assertFXMLNodes() {
        assert (rootBorderPane != null);
        assert (modelAnchorPane != null);
        assert (sceneControlsBox != null);
        assert (displayVBox != null);
        assert (infoPane != null);

        assert (timeSlider != null);
        assert (backwardButton != null);
        assert (forwardButton != null);
        assert (playButton != null);
        assert (timeLabel != null);
        assert (totalNucleiLabel != null);
        assert (zoomInButton != null);
        assert (zoomOutButton != null);

        assert (mainTabPane != null);
        assert (colorAndDisplayTab != null);
        assert (colorAndDisplayTabPane != null);
        assert (cellsTab != null);
        assert (structuresTab != null);
        assert (displayTab != null);
        assert (storiesTab != null);

        assert (searchField != null);
        assert (searchResultsListView != null);
        assert (sysRadioBtn != null);
        assert (desRadioBtn != null);
        assert (genRadioBtn != null);
        assert (conRadioBtn != null);
        assert (multiRadioBtn != null);

        assert (cellNucleusTick != null);
        assert (cellBodyTick != null);
        assert (ancestorTick != null);
        assert (descendantTick != null);
        assert (descendantLabel != null);
        assert (colorPickerPane != null);
        assert (colorPicker != null);

        assert (presynapticTick != null);
        assert (postsynapticTick != null);
        assert (electricalTick != null);
        assert (neuromuscularTick != null);

        assert (rulesListView != null);
        assert (addSearchBtn != null);

        assert (displayedName != null);
        assert (moreInfoClickableText != null);
        assert (displayedDescription != null);

        assert (displayedStory != null);
        assert (displayedStoryDescription != null);

        assert (uniformSizeCheckBox != null);
        assert (clearAllLabelsButton != null);
        assert (opacitySlider != null);

        assert (addStructureRuleBtn != null);
        assert (structureRuleColorPicker != null);
        assert (structuresSearchListView != null);
        assert (allStructuresListView != null);

        assert (storiesListView != null);
        assert (noteEditorBtn != null);
        assert (newStory != null);
        assert (deleteStory != null);
    }

    private void initStructuresLayer() {
        structuresLayer = new StructuresLayer(elementsList, structuresSearchField);
        structuresSearchListView.setItems(structuresLayer.getStructuresSearchResultsList());
        allStructuresListView.setItems(structuresLayer.getAllStructuresList());
        structuresLayer.setRulesList(displayLayer.getRulesList());

        addStructureRuleBtn.setOnAction(structuresLayer.getAddStructureRuleButtonListener());
        structureRuleColorPicker.setOnAction(structuresLayer.getColorPickerListener());

        structuresLayer.addSelectedNameListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                selectedName.set(newValue);
            }
        });

    }

    private void initStoriesLayer() {
        if (structuresLayer == null) {
            initStructuresLayer();
        }

        storiesLayer = new StoriesLayer(mainStage, elementsList, selectedName, lineageData, window3DController,
                useInternalRules, productionInfo.getMovieTimeOffset(), newStory, deleteStory, defaultEmbryoFlag);

        window3DController.setStoriesLayer(storiesLayer);

        storiesListView.setItems(storiesLayer.getStories());
        storiesListView.setCellFactory(storiesLayer.getStoryCellFactory());
        storiesListView.widthProperty().addListener(storiesLayer.getListViewWidthListener());

        noteEditorBtn.setOnAction(storiesLayer.getEditButtonListener());

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
        elementsList = new SceneElementsList();

        if (window3DController != null) {
            window3DController.setSceneElementsList(elementsList);
        }

        Search.setSceneElementsList(elementsList);
    }

    private void initConnectome() {
        connectome = new Connectome();
        Search.setConnectome(connectome);
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

            infoWindow = new InfoWindow(window3DController.getStage(), window3DController.getSelectedNameLabeled(),
                    cases, productionInfo, connectome, defaultEmbryoFlag, lineageData);
        }
    }

    private void initCases() {
        cases = new CasesLists(infoWindow);
        Search.setCases(cases);
    }

    private void initProductionInfo() {
        productionInfo = new ProductionInfo();
        Search.setProductionInfo(productionInfo);
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
            AceTreeLoader.setOriginToZero(lineageData);
        } else {
            lineageData = AceTreeLoader.loadNucFiles(productionInfo.getTotalTimePoints());
            defaultEmbryoFlag = true;
            lineageData.setIsSulstonModeFlag(productionInfo.getIsSulstonFlag());
        }

        replaceTabsWithDraggableTabs();

        initPartsList();
        initCellDeaths();
        initAnatomy();

        assertFXMLNodes();

        initToggleGroup();
        initDisplayLayer();

        initializeWithLineageData();

        mainTabPane.getSelectionModel().select(storiesTab);
    }

    public void initializeWithLineageData() {

        initLineageTree(lineageData.getAllCellNames());

        init3DWindow(lineageData);
        setPropertiesFrom3DWindow();

        setSlidersProperties();

        initSearch();
        ObservableList<Rule> list = displayLayer.getRulesList();
        Search.setRulesList(list);
        Search.addDefaultColorRules();
        Search.setActiveLineageNames(lineageData.getAllCellNames());
        Search.setLineageData(lineageData);

        window3DController.setRulesList(list);

        initSceneElementsList();

        // connectome
        initConnectome();

        // structures layer
        initStructuresLayer();

        // stories layer
        initStoriesLayer();

        window3DController.setSearchResultsList(Search.getSearchResultsList());
        searchResultsListView.setItems(Search.getSearchResultsList());

        window3DController.setSearchResultsUpdateService(search.getResultsUpdateService());
        window3DController.setGeneResultsUpdated(Search.getGeneResultsUpdated());

        addListeners();

        setIcons();
        setLabels();

        sizeSubscene();
        sizeInfoPane();

        timeSlider.setValue(window3DController.getEndTime());

        //window3DController.initializeWithCannonicalOrientation();

        viewTreeAction();

        captureVideo = new SimpleBooleanProperty(false);
        if (window3DController != null) {
            window3DController.setCaptureVideo(captureVideo);
        }

    }
}