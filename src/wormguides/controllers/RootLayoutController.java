package wormguides.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wormguides.DisplayLayer;
import wormguides.MainApp;
import wormguides.Search;
import wormguides.SearchType;
import wormguides.StoriesLayer;
import wormguides.StringListCellFactory;
import wormguides.StructuresLayer;
import wormguides.loaders.AceTreeLoader;
import wormguides.loaders.ImageLoader;
import wormguides.loaders.URLLoader;
import wormguides.model.Anatomy;
import wormguides.model.CellCasesLists;
import wormguides.model.Connectome;
import wormguides.model.LineageData;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;
import wormguides.model.CellDeaths;
import wormguides.model.ProductionInfo;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.model.Story;
import wormguides.view.AboutPane;
import wormguides.view.InfoWindow;
import wormguides.view.SulstonTreePane;
import wormguides.view.URLLoadWarningDialog;
import wormguides.view.URLLoadWindow;
import wormguides.view.URLWindow;

import javafx.scene.web.WebView;
import javafx.scene.Cursor;
import javafx.scene.Group;

public class RootLayoutController extends BorderPane implements Initializable {

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
	private Tab colorAndDisplayTab;
	@FXML
	private TabPane colorAndDisplayTabPane;
	@FXML
	private Tab cellsTab;
	@FXML
	private Tab displayTab;
	@FXML
	private Tab storiesTab;

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

	// production information
	private ProductionInfo productionInfo;

	// info window Stuff
	private CellCasesLists cellCases;
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
	RotationController rotationController;

	// movie capture
	@FXML
	private MenuItem captureVideoMenuItem;
	@FXML
	private MenuItem stopCaptureVideoMenuItem;
	private BooleanProperty captureVideo;

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
		if (storiesLayer != null)
			storiesLayer.saveActiveStory();
	}

	@FXML
	public void menuSaveImageAction() {
		if (window3DController != null) {
			window3DController.stillscreenCapture();
		}
	}

	@FXML
	public void menuCloseAction() {
		System.out.println("exiting...");
		System.exit(0);
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
					window3DController.getSelectedNameLabeled());

			treeStage.setScene(new Scene(sp));
			treeStage.setTitle("LineageTree");
			treeStage.initModality(Modality.NONE);
			treeStage.show();
			mainStage.show();
		} else {
			treeStage.show();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((Stage) treeStage.getScene().getWindow()).toFront();
				}
			});
		}
	}

	@FXML
	public void generateURLAction() {
		if (urlStage == null) {
			urlStage = new Stage();

			urlWindow = new URLWindow();
			urlWindow.setScene(window3DController);
			urlWindow.getCloseButton().setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					urlStage.hide();
				}
			});

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
			urlLoadWindow.getLoadButton().setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
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
				}
			});
			urlLoadWindow.getCancelButton().setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					urlLoadStage.hide();
				}
			});

			urlLoadStage.setScene(new Scene(urlLoadWindow));
			urlLoadStage.setTitle("Load Scene");
			urlLoadStage.setResizable(false);
			urlLoadStage.initModality(Modality.NONE);
		}

		urlLoadWindow.clearField();
		urlLoadStage.show();
	}

	@FXML
	public void openInfoWindow() {
		if (infoWindow == null) {
			initInfoWindow();

			if (cellCases == null)
				initCellCases();
			else
				cellCases.setInfoWindow(infoWindow);
		}

		infoWindow.showWindow();
	}

	@FXML
	public void viewCellShapesIndex() {
		if (elementsList == null)
			return;

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
			partsListWebView.getEngine().loadContent(PartsList.partsListDOM().DOMtoString());

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
			// loader.setRoot(rotationController); --> what does this line do in
			// StoriesLayer?

			try {
				rotationControllerStage.setScene(new Scene((AnchorPane) loader.load()));

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
		captureVideo.set(true);

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
	// ----- End menu items and buttons listeners -----

	public void init3DWindow(LineageData data) {
		if (cellCases == null)
			initCellCases();
		if (productionInfo == null)
			initProductionInfo();
		if (connectome == null)
			initConnectome();

		// for context menu
		// info window
		bringUpInfoProperty = new SimpleBooleanProperty(false);

		window3DController = new Window3DController(mainStage, modelAnchorPane, data, cellCases, productionInfo,
				connectome, bringUpInfoProperty);
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
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				timeSlider.setValue(time.get());
				if (time.get() >= window3DController.getEndTime() - 1) {
					playButton.setGraphic(playIcon);
					playingMovie.set(false);
				}
			}
		});

		timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int newTime = newValue.intValue();
				if (window3DController != null) // removed newTime != timeSlider.getValue() && --> to use arrow keys b/c arrows automatically update timeSlider.value
					window3DController.setTime(newTime);
			}
		});

		// search stuff
		searchResultsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				selectedName.set(newValue);
			}
		});

		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty()) {
					mainTabPane.getSelectionModel().select(colorAndDisplayTab);
					colorAndDisplayTabPane.getSelectionModel().select(cellsTab);
				}
			}
		});

		// selectedName string property that has the name of the clicked sphere
		selectedName.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					if (!newValue.isEmpty())
						setSelectedEntityInfo(selectedName.get());
				}
			}
		});

		// Multicellular structure stuff
		structuresSearchListView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
			}
		});

		// Modify font for ListView's of String's
		structuresSearchListView.setCellFactory(new StringListCellFactory());
		allStructuresListView.setCellFactory(structuresLayer.getCellFactory());
		searchResultsListView.setCellFactory(new StringListCellFactory());

		// 'Others' opacity
		opacitySlider.setValue(DEFAULT_TRANSPARENCY);

		// Uniform nuclei size
		uniformSizeCheckBox.setSelected(true);

		// Cell Nucleus search option
		cellNucleusTick.setSelected(true);

		// More info clickable text
		moreInfoClickableText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				openInfoWindow();
				infoWindow.addName(selectedName.get());
			}
		});
		moreInfoClickableText.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				moreInfoClickableText.setCursor(Cursor.HAND);
			}
		});
		moreInfoClickableText.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				moreInfoClickableText.setCursor(Cursor.DEFAULT);
			}
		});

		// More info in context menu
		bringUpInfoProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					openInfoWindow();
					infoWindow.addName(selectedName.get());
					infoWindow.showWindow();
				}
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

		if (name.indexOf("(") > -1)
			name = name.substring(0, name.indexOf("("));
		name = name.trim();

		displayedName.setText("Active Cell: " + name);
		moreInfoClickableText.setVisible(true);
		displayedDescription.setText("");

		// Note
		if (storiesLayer != null)
			displayedDescription.setText(storiesLayer.getNoteComments(name));

		// Cell body/structue
		if (Search.isStructureWithComment(name))
			displayedDescription.setText(Search.getStructureComment(name));

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
		int timeOffset = productionInfo.getMovieTimeOffset();

		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				timeLabel.setText("~" + (time.get() + timeOffset) + " min p.f.c.");
			}
		});
		timeLabel.setText("~" + (time.get() + timeOffset) + " min p.f.c.");
		timeLabel.toFront();

		totalNuclei.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				String suffix = " Nuclei";
				if (newValue.intValue() == 1)
					suffix = " Nucleus";
				totalNucleiLabel.setText(newValue.intValue() + suffix);
			}
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
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				playingMovie.set(!playingMovie.get());

				if (playingMovie.get())
					playButton.setGraphic(playIcon);
				else
					playButton.setGraphic(pauseIcon);
			}
		});
	}

	private void setSlidersProperties() {
		timeSlider.setMin(1);
		timeSlider.setMax(window3DController.getEndTime());

		opacitySlider.setMin(0);
		opacitySlider.setMax(100);
		opacitySlider.setValue(25);
	}

	private void initSearch() {
		search = new Search();

		typeToggleGroup.selectedToggleProperty().addListener(search.getTypeToggleListener());

		// connectome checkboxes
		presynapticTick.selectedProperty().addListener(search.getPresynapticTickListener());
		postsynapticTick.selectedProperty().addListener(search.getPostsynapticTickListener());
		electricalTick.selectedProperty().addListener(search.getElectricalTickListener());
		neuromuscularTick.selectedProperty().addListener(search.getNeuromuscularTickListener());

		cellNucleusTick.selectedProperty().addListener(search.getCellNucleusTickListener());
		cellBodyTick.selectedProperty().addListener(search.getCellBodyTickListener());
		ancestorTick.selectedProperty().addListener(search.getAncestorTickListner());
		descendantTick.selectedProperty().addListener(search.getDescendantTickListner());
		colorPicker.setOnAction(search.getColorPickerListener());
		addSearchBtn.setOnAction(search.getAddButtonListener());

		clearSearchField = new SimpleBooleanProperty(false);
		search.setClearSearchFieldProperty(clearSearchField);
		clearSearchField.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					searchField.clear();
					clearSearchField.set(false);
				}
			}
		});

		searchField.textProperty().addListener(search.getTextFieldListener());
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
		new LineageTree(allCellNames.toArray(new String[allCellNames.size()]));
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

		typeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				SearchType type = (SearchType) observable.getValue().getToggleGroup().getSelectedToggle().getUserData();
				if (type == SearchType.FUNCTIONAL || type == SearchType.DESCRIPTION) {
					descendantTick.setSelected(false);
					descendantTick.disableProperty().set(true);
					descendantLabel.disableProperty().set(true);
				} else {
					descendantTick.disableProperty().set(false);
					descendantLabel.disableProperty().set(false);
				}
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
		assert (opacitySlider != null);

		assert (addStructureRuleBtn != null);
		assert (structureRuleColorPicker != null);
		assert (structuresSearchListView != null);
		assert (allStructuresListView != null);

		assert (storiesListView != null);
		assert (noteEditorBtn != null);
		assert (newStory != null);
	}

	private void initStructuresLayer() {
		structuresLayer = new StructuresLayer(elementsList, structuresSearchField);
		structuresSearchListView.setItems(structuresLayer.getStructuresSearchResultsList());
		allStructuresListView.setItems(structuresLayer.getAllStructuresList());
		structuresLayer.setRulesList(displayLayer.getRulesList());

		addStructureRuleBtn.setOnAction(structuresLayer.getAddStructureRuleButtonListener());
		structureRuleColorPicker.setOnAction(structuresLayer.getColorPickerListener());

		structuresLayer.addSelectedNameListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty())
					selectedName.set(newValue);
			}
		});
	}

	private void initStoriesLayer() {
		if (structuresLayer == null)
			initStructuresLayer();

		storiesLayer = new StoriesLayer(mainStage, elementsList, selectedName, lineageData, window3DController,
				useInternalRules, productionInfo.getMovieTimeOffset(), newStory);

		window3DController.setStoriesLayer(storiesLayer);

		storiesListView.setItems(storiesLayer.getStories());
		storiesListView.setCellFactory(storiesLayer.getStoryCellFactory());
		storiesListView.widthProperty().addListener(storiesLayer.getListViewWidthListener());

		noteEditorBtn.setOnAction(storiesLayer.getEditButtonListener());

		window3DController.addListenerToRebuildSceneFlag(storiesLayer.getRebuildSceneFlag());

		storiesLayer.getActiveStoryProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.isEmpty()) {
					displayedStory.setText("Active Story: none");
					displayedStoryDescription.setText("");
				} else {
					displayedStory.setText("Active Story: " + newValue);
					displayedStoryDescription.setText(storiesLayer.getActiveStoryDescription());
				}
			}
		});
		displayedStory.setText("ActiveStory: " + storiesLayer.getActiveStory().getName());
		displayedStoryDescription.setText(storiesLayer.getActiveStoryDescription());
	}

	private void initSceneElementsList() {
		elementsList = new SceneElementsList();

		if (window3DController != null)
			window3DController.setSceneElementsList(elementsList);

		Search.setSceneElementsList(elementsList);
	}

	private void initConnectome() {
		connectome = new Connectome();
		Search.setConnectome(connectome);
	}

	private void initInfoWindow() {
		if (window3DController != null) {

			if (connectome == null)
				initConnectome();
			if (productionInfo == null)
				initProductionInfo();
			if (cellCases == null)
				initCellCases();

			infoWindow = new InfoWindow(window3DController.getStage(), window3DController.getSelectedNameLabeled(),
					cellCases, productionInfo, connectome);
		}
	}

	private void initCellCases() {
		cellCases = new CellCasesLists(infoWindow);
		Search.setCellCases(cellCases);
	}

	private void initProductionInfo() {
		productionInfo = new ProductionInfo();
		Search.setProductionInfo(productionInfo);
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		initPartsList();
		initCellDeaths();
		initAnatomy();
		lineageData = AceTreeLoader.loadNucFiles();
		initLineageTree(lineageData.getAllCellNames());

		assertFXMLNodes();

		initToggleGroup();
		initDisplayLayer();

		initializeWithLineageData();

		mainTabPane.getSelectionModel().select(storiesTab);
	}

	public void initializeWithLineageData() {
		initProductionInfo();

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

		window3DController.setSearchResultsList(search.getSearchResultsList());
		searchResultsListView.setItems(search.getSearchResultsList());

		window3DController.setSearchResultsUpdateService(search.getResultsUpdateService());
		window3DController.setGeneResultsUpdated(search.getGeneResultsUpdated());

		addListeners();

		setIcons();
		setLabels();

		sizeSubscene();
		sizeInfoPane();

		/**
		 * TODO
		 * 	refactor: why didn't the second line of code automatically update the time slider?
		 */
		timeSlider.setValue(256.);
		window3DController.setTime(lim4StoryStartTime);

		viewTreeAction();

		captureVideo = new SimpleBooleanProperty(false);
		if (window3DController != null) {
			window3DController.setCaptureVideo(captureVideo);
		}
	}
	
	/**
	 * lim 4 story start time
	 * 	TODO
	 * 	actual start time is 256 --> 237 is with +19 offset
	 */
	private final static int lim4StoryStartTime = 237;

	/** Default transparency of 'other' entities on startup */
	private final double DEFAULT_TRANSPARENCY = 25;
	/**
	 * Delay time in seconds before the application updates to the new time on
	 * the slider
	 */
	// private final double TIME_SLIDER_TIME_DELAY = 0.5;
}
