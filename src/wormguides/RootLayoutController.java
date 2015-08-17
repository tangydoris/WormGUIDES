package wormguides;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
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
import wormguides.model.ColorRule;
import wormguides.model.LineageTree;
import wormguides.model.PartsList;
import wormguides.model.TableLineageData;
import wormguides.view.AboutPane;
import wormguides.view.TreePane;
import wormguides.view.Window3DSubScene;

public class RootLayoutController implements Initializable{
	
	// popup windows
	private Stage aboutStage;
	private Stage treeStage;
	
	// 3D subscene stuff
	private Window3DSubScene window3D;
	private SubScene subscene;
	private DoubleProperty subsceneWidth;
	private DoubleProperty subsceneHeight;
	
	// Panels stuff
	@FXML public BorderPane rootBorderPane;
	@FXML public BorderPane displayPanel;
	@FXML public AnchorPane modelAnchorPane;
	@FXML public ScrollPane infoPane;
	
	// Subscene controls
	@FXML public Button backwardButton, forwardButton, playButton;
	@FXML public Label timeLabel, totalNucleiLabel;
	@FXML public Slider timeSlider;
	@FXML public Button zoomInButton, zoomOutButton;
	
	// Search tab
	private Search search;
	@FXML public TextField searchField;
	@FXML public ListView<String> searchResultsListView;
	@FXML public RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn;
	private ToggleGroup typeToggleGroup;
	
	@FXML public CheckBox cellTick, ancestorTick, descendantTick;
	@FXML public AnchorPane colorPickerPane;
	@FXML public ColorPicker colorPicker;
	
	// Lineage tree
	//private LineageTree lineageTree;
	private TreeItem<String> lineageTreeRoot;
	
	// Cell selection
	private StringProperty selectedName;
	
	// Layers tab
	private Layers layers;
	@FXML public ListView<ColorRule> colorRulesListView;
	@FXML public Button addSearchBtn;
	
	// Cell information
	@FXML public Text cellName;
	@FXML public Text cellDescription;
	
	private ImageView playIcon, pauseIcon;
	
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private BooleanProperty playingMovie;
	private DoubleProperty zoom;
	
	//private PartsList partsList;
	
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
	
	public void init3DWindow(TableLineageData data) {
		window3D = new Window3DSubScene(modelAnchorPane.prefWidth(-1), 
				modelAnchorPane.prefHeight(-1), data);
		subscene = window3D.getSubScene();
		modelAnchorPane.getChildren().add(subscene);
	}
	
	private void getPropertiesFrom3DWindow() {
		time = window3D.getTimeProperty();
		zoom = window3D.getZoomProperty();
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
		
		backwardButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int t = time.get();
				if (t > 1 && t <= window3D.getEndTime())
					time.set(t-1);
			}
		});
		
		forwardButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int t = time.get();
				if (t >= 1 && t < window3D.getEndTime()-1)
					time.set(t+1);
			}
		});
		
		zoomOutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double z = zoom.get();
				if (z < 5 && z >= 0.25)
					zoom.set(z+.25);
			}
		});
		
		zoomInButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double z = zoom.get();
				if (z <= 5 && z > 0.25)
					zoom.set(z-.25);
			}
		});
		
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
				if (playingMovie.get())
					playButton.setGraphic(playIcon);
				else
					playButton.setGraphic(pauseIcon);
				playingMovie.set(!playingMovie.get());
			}
		});
	}
	
	private void setSliderProperties() {
		timeSlider.setMin(1);
		timeSlider.setMax(window3D.getEndTime());
		timeSlider.setValue(window3D.getStartTime());
	}
	
	private void initSearch() {
		search = new Search(searchField, searchResultsListView);
		typeToggleGroup.selectedToggleProperty().addListener(search.getTypeToggleListener());
		cellTick.selectedProperty().addListener(search.getCellTickListner());
		ancestorTick.selectedProperty().addListener(search.getAncestorTickListner());
		descendantTick.selectedProperty().addListener(search.getDescendantTickListner());
		colorPicker.setOnAction(search.getColorPickerListener());
		addSearchBtn.setOnAction(search.getAddButtonListener());
	}
	
	private void initLayers() {
		layers = new Layers(colorRulesListView);
	}
	
	private void initPartsList() {
		new PartsList();
	}
	
	private void initLineageTree() {
		new LineageTree(AceTreeLoader.getAllCellNames());
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
				}
				else
					descendantTick.disableProperty().set(false);
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
		assert (colorPickerPane != null);
		assert (colorPicker != null);
		
		assert (colorRulesListView != null);
		assert (addSearchBtn != null);
		
		assert (cellName != null);
		assert (cellDescription != null);
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		initPartsList();
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		initLineageTree();
		
		assertFXMLNodes();
		
		initToggleGroup();
		initLayers();
		init3DWindow(data);
		getPropertiesFrom3DWindow();
		
		setSliderProperties();
		
		initSearch();
		
		search.setRulesList(layers.getRulesList());
		window3D.setRulesList(layers.getRulesList());
		window3D.setSearchResultsList(search.getSearchResultsList());
		window3D.setResultsUpdateService(search.getResultsUpdateService());
		window3D.setGeneResultsUpdated(search.getGeneResultsUpdated());
		search.addDefaultRules();
		
        addListeners();
        
        setIcons();
        setLabels();
        
        sizeSubscene();
        sizeInfoPane();  
	}
	
	
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	
}
