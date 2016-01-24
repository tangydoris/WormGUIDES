package wormguides;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
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
import wormguides.model.CellCases;
import wormguides.model.Connectome;
import wormguides.model.LineageData;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.model.Story;
import wormguides.view.AboutPane;
import wormguides.view.InfoWindow;
import wormguides.view.TreePane;
import wormguides.view.URLLoadWarningDialog;
import wormguides.view.URLLoadWindow;
import wormguides.view.URLWindow;

import javafx.scene.web.WebView;
import javafx.scene.Cursor;
import javafx.scene.Group;

public class RootLayoutController extends BorderPane implements Initializable{
	
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
	
	// URL generation/loading
	private URLWindow urlWindow;
	private URLLoadWindow urlLoadWindow;
	private URLLoadWarningDialog warning;
	
	// 3D subscene stuff
	private Window3DController window3D;
	private SubScene subscene;
	private DoubleProperty subsceneWidth;
	private DoubleProperty subsceneHeight;
	
	// Panels stuff
	@FXML private BorderPane rootBorderPane;
	@FXML private VBox displayVBox;
	@FXML private AnchorPane modelAnchorPane;
	@FXML private ScrollPane infoPane;
	@FXML private HBox sceneControlsBox;
	
	// Subscene controls
	@FXML private Button backwardButton, forwardButton, playButton;
	@FXML private Label timeLabel, totalNucleiLabel;
	@FXML private Slider timeSlider;
	@FXML private Button zoomInButton, zoomOutButton;
	
	// Cells tab
	private Search search;
	@FXML private TextField searchField;
	private BooleanProperty clearSearchField;
	@FXML private ListView<String> searchResultsListView;
	@FXML private RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn, conRadioBtn, multiRadioBtn;
	private ToggleGroup typeToggleGroup;
	
	@FXML private CheckBox cellNucleusTick, cellBodyTick, ancestorTick, descendantTick;
	@FXML private Label descendantLabel;
	@FXML private AnchorPane colorPickerPane;
	@FXML private ColorPicker colorPicker;
	@FXML private Button addSearchBtn;
	
	// Connectome stuff
	private Connectome connectome;
	@FXML private CheckBox presynapticTick, postsynapticTick, electricalTick, neuromuscularTick;
	
	// Lineage tree
	private TreeItem<String> lineageTreeRoot;
	
	// Cell selection
	private StringProperty selectedName;
	
	// Layers tab
	private DisplayLayer displayLayer;
	@FXML private ListView<Rule> rulesListView;
	@FXML private CheckBox uniformSizeCheckBox;
	@FXML private Slider opacitySlider;
	
	//structures tab
	private StructuresLayer structuresLayer;
	@FXML private TextField structuresSearchField;
	@FXML private ListView<String> structuresSearchListView;
	@FXML private ListView<String> allStructuresListView;
	@FXML private Button addStructureRuleBtn;
	@FXML private ColorPicker structureRuleColorPicker;
	private Service<Void> structuresSearchListDeselect;
	private Service<Void> allStructuresListDeselect;
	
	// cell information
	@FXML private Text displayedName;
	@FXML private Text moreInfoClickableText;
	@FXML private Text displayedDescription;
	
	// story information
	@FXML private Text displayedStory;
	@FXML private Text displayedStoryDescription;
	
	//scene elements stuff
	private SceneElementsList elementsList;
	
	// story stuff
	private StoriesLayer storiesLayer;
	@FXML private ListView<Story> storiesListView;
	@FXML private Button noteEditorBtn;
	
	// url stuff
	private URLLoader urlLoader;
	
	//InfoWindow Stuff
	CellCases cellCases;
	InfoWindow infoWindow;
	
	private ImageView playIcon, pauseIcon;
	
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private BooleanProperty playingMovie;
	
	
	// ----- Begin menu items and buttons listeners -----
	@FXML public void menuLoadStory() {
		// TODO
	}
	
	@FXML public void menuSaveStory() {
		// TODO
	}
	
	@FXML public void menuCloseAction() {
		System.out.println("exiting...");
		System.exit(0);
	}
	
	@FXML public void menuAboutAction() {
		if (aboutStage==null) {
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
	
	@FXML public void viewTreeAction() {
		if (treeStage==null) {
			treeStage = new Stage();
			treeStage.setScene(new Scene(new TreePane(lineageTreeRoot)));
			treeStage.setTitle("LineageTree");
			treeStage.initModality(Modality.NONE);
		}
		treeStage.show();
	}
	
	@FXML public void generateURLAction() {
		if (urlStage==null) {
			urlStage = new Stage();
			
			urlWindow = new URLWindow();
			urlWindow.setScene(window3D);
			urlWindow.getCloseButton().setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					urlStage.hide();
				}
			});
			
			urlStage.setScene(new Scene(urlWindow));
			urlStage.setTitle("URLs");
			urlStage.setResizable(false);
			urlStage.initModality(Modality.NONE);
		}
		
		urlWindow.resetURLs();
		urlStage.show();
	}
	
	@FXML public void loadURLAction() {
		if (urlLoadStage==null) {
			urlLoadStage = new Stage();
			
			urlLoadWindow = new URLLoadWindow();
			urlLoadWindow.getLoadButton().setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (warning == null) {
						warning = new URLLoadWarningDialog();
					}
					if (!warning.doNotShowAgain()){
						Optional<ButtonType> result = warning.showAndWait();
						if (result.get() == warning.getButtonTypeOkay()) {
							urlLoadStage.hide();
							if (urlLoader==null)
								urlLoader = new URLLoader(window3D);
							urlLoader.parseURL(urlLoadWindow.getInputURL());
						}
					}
					else {
						urlLoadStage.hide();
						if (urlLoader==null)
							urlLoader = new URLLoader(window3D);
						urlLoader.parseURL(urlLoadWindow.getInputURL());
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
			urlLoadStage.setTitle("Load URL");
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
		}
		infoWindow.showWindow();
	}
	
	@FXML
	public void viewCellShapesIndex() {
		if (elementsList == null) return;
		
		if (cellShapesIndexStage == null) {
			cellShapesIndexStage = new Stage();
			cellShapesIndexStage.setTitle("Cell Shapes Index");
			
			CellShapesIndexToHTML cellShapesToHTML = new CellShapesIndexToHTML(elementsList);
			
			//webview to render cell shapes list i.e. elementsList
			WebView cellShapesIndexWebView = new WebView();
			cellShapesIndexWebView.getEngine().loadContent(cellShapesToHTML.buildCellShapesIndexAsHTML());
			
			VBox root = new VBox();
			root.getChildren().addAll(cellShapesIndexWebView);
			Scene scene = new Scene(new Group());
			scene.setRoot(root);
			
			cellShapesIndexStage.setScene(scene);
			cellShapesIndexStage.setResizable(false);
		}
		cellShapesIndexStage.show();
	}
	
	@FXML public void viewPartsList() {
		if (partsListStage == null) {
			partsListStage = new Stage();
			partsListStage.setTitle("Parts List");	
			
			//build webview scene to render parts list
			WebView partsListWebView = new WebView();
			partsListWebView.getEngine().loadContent(PartsList.getPartsListAsHTMLTable());
			
			VBox root = new VBox();
			root.getChildren().addAll(partsListWebView);
			Scene scene = new Scene(new Group());
			scene.setRoot(root);
			
			partsListStage.setScene(scene);
			partsListStage.setResizable(false);
		}
		partsListStage.show();
	}
	
	@FXML public void viewConnectome() {
		if (connectomeStage == null) {
			connectomeStage = new Stage();
			connectomeStage.setTitle("Connectome");
			
			//build webview scene to render html
			WebView connectomeHTML = new WebView();
			connectomeHTML.getEngine().loadContent(connectome.connectomeAsHTML());
			
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
	
	
	public void init3DWindow(LineageData data) {
		window3D = new Window3DController(modelAnchorPane, data);
		subscene = window3D.getSubScene();
		
		modelAnchorPane.setOnMouseClicked(window3D.getNoteClickHandler());
		
		backwardButton.setOnAction(window3D.getBackwardButtonListener());
		forwardButton.setOnAction(window3D.getForwardButtonListener());
		zoomOutButton.setOnAction(window3D.getZoomOutButtonListener());
		zoomInButton.setOnAction(window3D.getZoomInButtonListener());
		
		searchField.textProperty().addListener(window3D.getSearchFieldListener());
		
		// slider has to listen to 3D window's opacity value
		// 3d window's opacity value has to listen to opacity slider's value
		opacitySlider.valueProperty().addListener(window3D.getOthersOpacityListener());
		window3D.addListenerToOpacitySlider(opacitySlider);
		
		uniformSizeCheckBox.selectedProperty().addListener(window3D.getUniformSizeCheckBoxListener());
		
		cellNucleusTick.selectedProperty().addListener(window3D.getCellNucleusTickListener());
		cellBodyTick.selectedProperty().addListener(window3D.getCellBodyTickListener());
		
		multiRadioBtn.selectedProperty().addListener(window3D.getMulticellModeListener());
	}
	
	private void setPropertiesFrom3DWindow() {
		time = window3D.getTimeProperty();
		window3D.getZoomProperty();
		totalNuclei = window3D.getTotalNucleiProperty();
		playingMovie = window3D.getPlayingMovieProperty();
		selectedName = window3D.getSelectedName();
	}
	
	
	public void setStage(Stage stage) {
		mainStage = stage;
	}
	
	
	private void addListeners() {
		// time integer property that dictates the current time point
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				timeSlider.setValue(time.get());
				if (time.get()>=window3D.getEndTime()-1) {
					playButton.setGraphic(playIcon);
					playingMovie.set(false);
				}
			}
		});
		
		timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				if (newValue.intValue() != timeSlider.getValue())
					time.set(newValue.intValue());
			}
		});
		
		searchResultsListView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				selectedName.set(newValue);
			}
		});
		
		
		// selectedName string property that has the name of the clicked sphere
		selectedName.addListener(new ChangeListener<String> () {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!newValue.isEmpty())
					setSelectedEntityInfo(selectedName.get());
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
		structuresSearchListView.setCellFactory(new StringCellCallback());
		allStructuresListView.setCellFactory(structuresLayer.getCellFactory());
		searchResultsListView.setCellFactory(new StringCellCallback());
		
		// 'Others' opacity
		opacitySlider.setValue(50);
		
		// Uniform nuclei size
		uniformSizeCheckBox.setSelected(true);
		
		// Cell Nucleus search option
		cellNucleusTick.setSelected(true);
		
		// More info clickable text
		// TODO
		moreInfoClickableText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				openInfoWindow();
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
	}
	
	
	private void setSelectedEntityInfo(String name) {
		if (name==null || name.isEmpty()) {
			displayedName.setText("Active Cell: none");
			moreInfoClickableText.setVisible(false);
			displayedDescription.setText("");
			return;
		}
		
		if (name.indexOf("(") > -1) 
			name = name.substring(0, name.indexOf("("));
		name = name.trim();
		
		displayedName.setText("Active Cell: "+name);
		moreInfoClickableText.setVisible(true);
		displayedDescription.setText("");
		
		// Note
		if (storiesLayer!=null)
			displayedDescription.setText(storiesLayer.getNoteComments(name));
		
		// Cell body/structue
		if (Search.isStructureWithComment(name))
			displayedDescription.setText(Search.getStructureComment(name));
		
		// Cell lineage name
		else {
			String functionalName = PartsList.getFunctionalNameByLineageName(name);
			
			if (functionalName!=null) {
				displayedName.setText("Active Cell: "+name+" ("+functionalName+")");
				displayedDescription.setText(PartsList.getDescriptionByFunctionalName(functionalName));
			}
		}
		
		//GENERATE CELL TAB ON CLICK
		if (cellCases == null) return; //error check
						
		if (connectome.containsCell(name)) { //in connectome --> terminal case (neuron)
			if (cellCases.containsTerminalCase(name)) {
				
				//show the tab
			} else {
				//translate the name if necessary
				String tabTitle = connectome.checkQueryCell(name).toUpperCase();
				//add a terminal case --> pass the wiring partners
				cellCases.makeTerminalCase(tabTitle, 
						connectome.querryConnectivity(name, true, false, false, false, false),
						connectome.querryConnectivity(name, false, true, false, false, false),
						connectome.querryConnectivity(name, false, false, true, false, false),
						connectome.querryConnectivity(name, false, false, false, true, false));
			}
		} else { //not in connectome --> non terminal case
			if (cellCases.containsNonTerminalCase(name)) {

				//show tab
			} else {
				//add a non terminal case
				cellCases.makeNonTerminalCase(name);
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
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				timeLabel.setText("~"+(time.get()+19)+" min p.f.c.");
			}
		});
		timeLabel.setText("~"+(time.get()+19)+" min p.f.c.");
		timeLabel.toFront();
		
		totalNuclei.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				String suffix = " Nuclei";
				if (newValue.intValue()==1)
					suffix = " Nucleus";
				totalNucleiLabel.setText(newValue.intValue()+suffix);
			}
		});
		
		totalNucleiLabel.setText(totalNuclei.get()+" Nuclei");
		totalNucleiLabel.toFront();
	}
	
	public void setIcons() {
		backwardButton.setGraphic(ImageLoader.getBackwardIcon());
		forwardButton.setGraphic(ImageLoader.getForwardIcon());
		zoomInButton.setGraphic(ImageLoader.getPlusIcon());
		zoomOutButton.setGraphic(ImageLoader.getMinusIcon());
		
		this.playIcon = ImageLoader.getPlayIcon();
		this.pauseIcon = ImageLoader.getPauseIcon();
		playButton.setGraphic(playIcon);
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (playingMovie.get()) {
					playButton.setGraphic(playIcon);
				}
				else {
					playButton.setGraphic(pauseIcon);
				}
				playingMovie.set(!playingMovie.get());
			}
		});
	}
	
	private void setSlidersProperties() {
		timeSlider.setMin(1);
		timeSlider.setMax(window3D.getEndTime());
		timeSlider.setValue(window3D.getStartTime());
		
		opacitySlider.setMin(0);
		opacitySlider.setMax(100);
		opacitySlider.setValue(100);
	}
	
	private void initSearch() {
		search = new Search();
		
		typeToggleGroup.selectedToggleProperty().addListener(search.getTypeToggleListener());
		
		//connectome checkboxes
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
			public void changed(ObservableValue<? extends Boolean> observable, 
										Boolean oldValue, Boolean newValue) {
				if (newValue) {
					searchField.clear();
					clearSearchField.set(false);
				}
			}
		});
		
		searchField.textProperty().addListener(search.getTextFieldListener());
	}
	
	
	private void initDisplayLayer() {
		displayLayer = new DisplayLayer();
		
		rulesListView.setItems(displayLayer.getRulesList());
		rulesListView.setCellFactory(displayLayer.getRuleCellFactory());
	}
	
	
	private void initPartsList() {
		new PartsList();
	}
	
	
	private void initLineageTree(ArrayList<String> allCellNames) {
		new LineageTree(allCellNames.toArray(new String[allCellNames.size()]));
		lineageTreeRoot = LineageTree.getRoot();
	}
	
	
	private void initToggleGroup() {
		typeToggleGroup = new ToggleGroup();
		sysRadioBtn.setToggleGroup(typeToggleGroup);
		sysRadioBtn.setUserData(SearchType.SYSTEMATIC);
		funRadioBtn.setToggleGroup(typeToggleGroup);
		funRadioBtn.setUserData(SearchType.FUNCTIONAL);
		desRadioBtn.setToggleGroup(typeToggleGroup);
		desRadioBtn.setUserData(SearchType.DESCRIPTION);
		genRadioBtn.setToggleGroup(typeToggleGroup);
		genRadioBtn.setUserData(SearchType.GENE);
		conRadioBtn.setToggleGroup(typeToggleGroup);
		conRadioBtn.setUserData(SearchType.CONNECTOME);
		multiRadioBtn.setToggleGroup(typeToggleGroup);
		multiRadioBtn.setUserData(SearchType.MULTICELL);
		
		typeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable,
								Toggle oldValue, Toggle newValue) {
				SearchType type = (SearchType) observable.getValue().getToggleGroup()
					.getSelectedToggle().getUserData();
				if (type==SearchType.FUNCTIONAL || type==SearchType.DESCRIPTION) {
					descendantTick.setSelected(false);
					descendantTick.disableProperty().set(true);
					descendantLabel.disableProperty().set(true);
				}
				else {
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
			public void changed(ObservableValue<? extends String> observable, 
								String oldValue, String newValue) {
				if (!newValue.isEmpty())
					selectedName.set(newValue);
			}
		});
	}
	
	
	private void initStoriesLayer(LineageData data) {
		storiesLayer = new StoriesLayer(mainStage, selectedName, window3D.getCellClicked(), data);
		window3D.setStoriesLayer(storiesLayer);
		
		storiesListView.setItems(storiesLayer.getStories());
		storiesListView.setCellFactory(storiesLayer.getStoryCellFactory());
		storiesListView.widthProperty().addListener(storiesLayer.getListViewWidthListener());
		
		noteEditorBtn.setOnAction(storiesLayer.getEditButtonListener());
		
		storiesLayer.getRebuildSceneFlag().addListener(window3D.getRebuildFlagListener());
		storiesLayer.getTimeProperty().addListener(window3D.getStoriesTimeListener());
		storiesLayer.getActiveStoryProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				if (newValue.isEmpty()) {
					displayedStory.setText("Active Story: none");
					displayedStoryDescription.setText("");
				}
				else {
					displayedStory.setText("Active Story: "+newValue);
					displayedStoryDescription.setText(storiesLayer.getActiveStoryDescription());
				}
			}
		});
	}
	
	
	private void initSceneElementsList() {
		elementsList = new SceneElementsList();
		
		if (window3D!=null)
			window3D.setSceneElementsList(elementsList);
		
		Search.setSceneElementsList(elementsList);
	}
	
	
	private void initConnectome() {
		connectome = new Connectome();
		Search.setConnectome(connectome);
	}
	
	private void initInfoWindow() {
		infoWindow = new InfoWindow();
	}
	
	private void initCellCases() {
		if (infoWindow == null) {
			initInfoWindow();
		}
		
		cellCases = new CellCases(infoWindow);
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		initPartsList();
		LineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		initLineageTree(data.getAllCellNames());
		
		assertFXMLNodes();
		
		initToggleGroup();
		initDisplayLayer();
		
		initializeWithLineageData(data);
	}
	
	
	public void initializeWithLineageData(LineageData data) {
		init3DWindow(data);
		setPropertiesFrom3DWindow();
		
		setSlidersProperties();
		
		initSearch();
		Search.setActiveLineageNames(data.getAllCellNames());
		
		ObservableList<Rule> list = displayLayer.getRulesList();
		search.setRulesList(list);
		window3D.setRulesList(list);
		
		initSceneElementsList();
		
		// connectome
		initConnectome();
		
		//info window
		initInfoWindow();
		
		//init cell cases
		initCellCases();
		
		// structures layer
		initStructuresLayer();
		
		// stories layer
		initStoriesLayer(data);
		
		window3D.setSearchResultsList(search.getSearchResultsList());
		searchResultsListView.setItems(search.getSearchResultsList());
		
		window3D.setSearchResultsUpdateService(search.getResultsUpdateService());
		window3D.setGeneResultsUpdated(search.getGeneResultsUpdated());
		
		search.addDefaultColorRules();
		
        addListeners();
        
        setIcons();
        setLabels();
        
        sizeSubscene();
        sizeInfoPane();
	}

	private static final String JAR_NAME = "WormGUIDES.jar";
	
}
