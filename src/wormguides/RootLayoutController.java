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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import wormguides.model.ColorRule;
import wormguides.model.LineageData;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;
import wormguides.model.Rule;
import wormguides.model.SceneElementsList;
import wormguides.view.AboutPane;
import wormguides.view.TreePane;
import wormguides.view.URLLoadWarningDialog;
import wormguides.view.URLLoadWindow;
import wormguides.view.URLWindow;
import wormguides.view.Window3DSubScene;

public class RootLayoutController implements Initializable{
	
	// popup windows
	private Stage aboutStage;
	private Stage treeStage;
	private Stage urlStage;
	private Stage urlLoadStage;
	
	// URL generation/loading
	private URLWindow urlWindow;
	private URLLoadWindow urlLoadWindow;
	private URLLoadWarningDialog warning;
	
	// 3D subscene stuff
	private Window3DSubScene window3D;
	private SubScene subscene;
	private DoubleProperty subsceneWidth;
	private DoubleProperty subsceneHeight;
	
	// panels stuff
	@FXML private BorderPane rootBorderPane;
	@FXML private BorderPane displayPanel;
	@FXML private AnchorPane modelAnchorPane;
	@FXML private ScrollPane infoPane;
	
	// subscene controls
	@FXML private Button backwardButton, forwardButton, playButton;
	@FXML private Label timeLabel, totalNucleiLabel;
	@FXML private Slider timeSlider;
	@FXML private Button zoomInButton, zoomOutButton;
	
	// search tab
	private Search search;
	@FXML private TextField searchField;
	private BooleanProperty clearSearchField;
	@FXML private ListView<String> searchResultsListView;
	@FXML private RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn;
	private ToggleGroup typeToggleGroup;
	
	@FXML private CheckBox cellTick, ancestorTick, descendantTick;
	@FXML private Label descendantLabel;
	@FXML private AnchorPane colorPickerPane;
	@FXML private ColorPicker colorPicker;
	
	// lineage tree
	private TreeItem<String> lineageTreeRoot;
	
	// cell selection
	private StringProperty selectedName;
	
	// layers tab
	private Layers colorLayers;
	private Layers shapeLayers;
	@FXML private ListView<Rule> colorRulesListView;
	@FXML private Button addSearchBtn;
	@FXML private Slider opacitySlider;
	@FXML private ListView<Rule> shapeRulesListView;
	
	// cell information
	@FXML private Text cellName;
	@FXML private Text cellDescription;
	
	// url stuff
	private URLLoader urlLoader;
	
	private ImageView playIcon, pauseIcon;
	
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private BooleanProperty playingMovie;
	
	@FXML
	public void menuCloseAction() {
		System.out.println("exiting...");
		System.exit(0);
	}
	
	@FXML
	public void menuAboutAction() {
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
	
	@FXML
	public void viewTreeAction() {
		if (treeStage==null) {
			treeStage = new Stage();
			treeStage.setScene(new Scene(new TreePane(lineageTreeRoot)));
			treeStage.setTitle("LineageTree");
			treeStage.initModality(Modality.NONE);
		}
		treeStage.show();
	}
	
	@FXML
	public void generateURLAction() {
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
	
	@FXML
	public void loadURLAction() {
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
	
	public void init3DWindow(LineageData data) {
		window3D = new Window3DSubScene(modelAnchorPane.prefWidth(-1), 
										modelAnchorPane.prefHeight(-1), data);
		subscene = window3D.getSubScene();
		modelAnchorPane.getChildren().add(subscene);
	}
	
	private void getPropertiesFrom3DWindow() {
		time = window3D.getTimeProperty();
		window3D.getZoomProperty();
		totalNuclei = window3D.getTotalNucleiProperty();
		playingMovie = window3D.getPlayingMovieProperty();
		selectedName = window3D.getSelectedName();
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
		
		backwardButton.setOnAction(window3D.getBackwardButtonListener());
		forwardButton.setOnAction(window3D.getForwardButtonListener());
		zoomOutButton.setOnAction(window3D.getZoomOutButtonListener());
		zoomInButton.setOnAction(window3D.getZoomInButtonListener());
		
		searchField.textProperty().addListener(window3D.getSearchFieldListener());
		searchResultsListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				setSelectedInfo(newValue);
				selectedName.set(newValue);
			}
		});
		
		searchResultsListView.selectionModelProperty().addListener(
				new ChangeListener<MultipleSelectionModel<String>>() {
			@Override
			public void changed(
					ObservableValue<? extends MultipleSelectionModel<String>> observable,
					MultipleSelectionModel<String> oldValue,
					MultipleSelectionModel<String> newValue) {
				String sulston = newValue.getSelectedItem();
				System.out.println(sulston);
				setSelectedInfo(sulston);
			}
		});
		
		
		// selectedName string property that has the name of the clicked sphere
		selectedName.addListener(new ChangeListener<String> () {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				setSelectedInfo(selectedName.get());
			}
		});
		
		// slider has to listen to 3D window's opacity value
		// 3d window's opacity value has to listen to opacity slider's value
		opacitySlider.valueProperty().addListener(window3D.getOthersOpacityListener());
		window3D.addListenerToOpacitySlider(opacitySlider);
		// set to 50% transparency as default
		opacitySlider.setValue(50);
	}
	
	private void setSelectedInfo(String name) {
		if (name==null || name.isEmpty())
			return;
		
		if (name.indexOf("(")!=-1) 
			name = name.substring(0, name.indexOf(" "));
		
		String functionalName = PartsList.getFunctionalNameByLineageName(name);
		if (functionalName==null) {
			cellName.setText(name);
			cellDescription.setText("");
		}
		else {
			cellName.setText(name+" ("+functionalName+")");
			cellDescription.setText(PartsList.getDescriptionByFunctionalName(functionalName));
		}
	}
	
	private void sizeSubscene() {
		this.subsceneWidth = new SimpleDoubleProperty();
		subsceneWidth.bind(modelAnchorPane.widthProperty());
		this.subsceneHeight = new SimpleDoubleProperty();
		subsceneHeight.bind(modelAnchorPane.heightProperty().subtract(33));
		
		AnchorPane.setTopAnchor(subscene, 0.0);
		AnchorPane.setLeftAnchor(subscene, 0.0);
		AnchorPane.setRightAnchor(subscene, 0.0);
		AnchorPane.setBottomAnchor(subscene, 33.0);
		
		subscene.widthProperty().bind(subsceneWidth);
		subscene.heightProperty().bind(subsceneHeight);
		subscene.setManaged(false);
	}
	
	private void sizeInfoPane() {
		infoPane.prefHeightProperty().bind(displayPanel.heightProperty().divide(7));
		cellName.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
		cellDescription.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
	}
	
	
	private void setLabels() {
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				timeLabel.setText("Time "+makePaddedTime(time.get()));
			}
		});
		timeLabel.setText("Time "+makePaddedTime(time.get()));
		timeLabel.toFront();
		
		totalNuclei.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				totalNucleiLabel.setText(newValue.intValue()+" Nuclei");
			}
		});
		totalNucleiLabel.setText(totalNuclei.get()+" Nuclei");
		totalNucleiLabel.toFront();
	}
	
	private String makePaddedTime(int time) {
		if (time < 10)
			return "00"+time;
		else if (time < 100)
			return "0"+time;
		else
			return ""+time;
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
		cellTick.selectedProperty().addListener(search.getCellTickListner());
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
	
	private void initLayers() {
		LayersListViewCallback callback = new LayersListViewCallback();
		
		// color rules layers
		colorLayers = new Layers(colorRulesListView);
		colorRulesListView.setCellFactory(callback);
		
		// shape rules layers
		shapeLayers = new Layers(shapeRulesListView);
		shapeRulesListView.setCellFactory(callback);
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
		assert (displayPanel != null);
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
		
		assert (cellTick != null);
		assert (ancestorTick != null);
		assert (descendantTick != null);
		assert (descendantLabel != null);
		assert (colorPickerPane != null);
		assert (colorPicker != null);
		
		assert (colorRulesListView != null);
		assert (shapeRulesListView != null);
		assert (addSearchBtn != null);
		
		assert (cellName != null);
		assert (cellDescription != null);
		
		assert (opacitySlider != null);
	}

	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		initPartsList();
		LineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		initLineageTree(data.getAllCellNames());
		
		assertFXMLNodes();
		
		initToggleGroup();
		initLayers();
		
		initializeWithLineageData(data);
	}
	
	
	@SuppressWarnings("unchecked")
	public void initializeWithLineageData(LineageData data) {
		init3DWindow(data);
		getPropertiesFrom3DWindow();
		
		setSlidersProperties();
		
		initSearch();
		Search.setActiveLineageNames(data.getAllCellNames());
		
		// unchecked cast
		ObservableList<ColorRule> tempList = (ObservableList<ColorRule>)((ObservableList<? extends Rule>)colorLayers.getRulesList());
		search.setRulesList(tempList);
		window3D.setRulesList(tempList);
		window3D.setSceneElementsList(new SceneElementsList());
		
		window3D.setSearchResultsList(search.getSearchResultsList());
		searchResultsListView.setItems(search.getSearchResultsList());
		
		window3D.setResultsUpdateService(search.getResultsUpdateService());
		window3D.setGeneResultsUpdated(search.getGeneResultsUpdated());
		search.addDefaultRules();
		
        addListeners();
        
        setIcons();
        setLabels();
        
        sizeSubscene();
        sizeInfoPane();
	}
	
	
	/*
	 * Renderer for rules in ListView's in Layers tab
	 */
	private class LayersListViewCallback implements Callback<ListView<Rule>, ListCell<Rule>> {
		@Override
		public ListCell<Rule> call(ListView<Rule> param) {
			ListCell<Rule> cell = new ListCell<Rule>(){
                @Override
                protected void updateItem(Rule item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) 
                    	setGraphic(item.getHBox());
                	else
                		setGraphic(null);
            	}
        	};
        	return cell;
		}
	}
	
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	
}
