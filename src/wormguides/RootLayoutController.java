package wormguides;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
	public BorderPane displayPanel;
	@FXML public AnchorPane modelAnchorPane;
	@FXML public ScrollPane infoPane;
	
	// Subscene controls
	@FXML public Button backwardButton, forwardButton, playButton;
	@FXML public Label timeLabel, totalNucleiLabel;
	@FXML public Slider timeSlider;
	
	// Search tab
	private Search search;
	private String[] allCellNames;
	@FXML public TextField searchField;
	@FXML public ListView<String> searchResultsList;
	@FXML public RadioButton sysRadioBtn, funRadioBtn, desRadioBtn, genRadioBtn;
	@FXML public CheckBox cellTick, ancestorTick, descendantTick;
	@FXML public AnchorPane colorPickerPane;
	@FXML public ColorPicker colorPicker;
	
	// Lineage tree
	private LineageTree lineageTree;
	private TreeItem<String> lineageTreeRoot;
	
	// Cell selection
	private StringProperty selectedName;
	
	// Layers tab
	private Layers layers;
	@FXML public ListView<ColorRule> colorRulesList;
	@FXML public Button addSearchBtn;
	
	// Cell information
	@FXML public Text cellName;
	@FXML public Text cellDescription;
	
	private ImageView playIcon, pauseIcon;
	
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private BooleanProperty playingMovie;
	
	private PartsList partsList;
	
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
		try {
			window3D = new Window3DSubScene(modelAnchorPane.prefWidth(-1), 
					modelAnchorPane.prefHeight(-1), data, lineageTree);
			subscene = window3D.getSubScene();
			modelAnchorPane.getChildren().add(subscene);
			
			//window3D.setSlider(timeSlider);
		} catch (NullPointerException npe) {
			System.out.println("Cannot insatntiate 3D view.");
			npe.printStackTrace();
		}
	}
	
	private void getPropertiesFrom3DWindow() {
		time = window3D.getTimeProperty();
		totalNuclei = window3D.getTotalNucleiProperty();
		playingMovie = window3D.getPlayingMovieProperty();
		selectedName = window3D.getSelectedName();
	}
	
	private void addListeners() {
		try {
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
					if (t >= 1 && t < window3D.getEndTime())
						time.set(t+1);
				}
			});
			
			searchField.textProperty().addListener(window3D.getSearchFieldListener());
			searchResultsList.getSelectionModel().selectedItemProperty().addListener(
					new ChangeListener<String>() {
				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					setSelectedInfo(newValue);
					selectedName.set(newValue);
				}
			});
			
			// TODO
			/*
			 * Should we be able to select a cell from the search results
			 * and have the name show up in the search text field?
			 */
			
			searchResultsList.selectionModelProperty().addListener(
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
			
		} catch (NullPointerException npe) {
			System.out.println("cannot add listener for one or more UI components");
		}
	}
	
	private void setSelectedInfo(String sulston) {
		String proper = partsList.getProperName(sulston);
		if (proper == null) {
			cellName.setText(sulston);
			cellDescription.setText("");
		}
		else {
			cellName.setText(sulston+" ("+proper+")");
			cellDescription.setText(partsList.getDescription(sulston));
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
		try {
			infoPane.prefHeightProperty().bind(displayPanel.heightProperty().divide(6));
			
			cellName.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
			cellDescription.wrappingWidthProperty().bind(infoPane.widthProperty().subtract(15));
		} catch (Exception e) {
			System.out.println("canoot size information panel");
		}
	}
	
	
	private void setLabels() {
		try {
			time.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					timeLabel.setText("Time "+makePaddedTime(time.get()));
				}
			});
			timeLabel.setText("Time "+makePaddedTime(time.get()));
			timeLabel.toFront();
		} catch (NullPointerException npe) {
			System.out.println("Cannot set time label");
		}
		
		try {
			totalNuclei.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					totalNucleiLabel.setText(newValue.intValue()+" Nuclei");
				}
			});
			totalNucleiLabel.setText(totalNuclei.get()+" Nuclei");
			totalNucleiLabel.toFront();
		} catch (NullPointerException npe) {
			System.out.println("Cannot set total nuclei label");
		}
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
		try {
			timeSlider.setMin(1);
			timeSlider.setMax(window3D.getEndTime());
			timeSlider.setValue(window3D.getStartTime());
		} catch (NullPointerException npe) {
			System.out.println("null time slider");
		}
	}
	
	private void initSearch() {
		search = new Search(searchField, searchResultsList);
		search.setCellNames(allCellNames);
		
		ToggleGroup typeGroup = search.getTypeToggleGroup();
		sysRadioBtn.setToggleGroup(typeGroup);
		sysRadioBtn.setUserData(Search.Type.SYSTEMATIC);
		funRadioBtn.setToggleGroup(typeGroup);
		funRadioBtn.setUserData(Search.Type.FUNCTIONAL);
		desRadioBtn.setToggleGroup(typeGroup);
		desRadioBtn.setUserData(Search.Type.DESCRIPTION);
		genRadioBtn.setToggleGroup(typeGroup);
		genRadioBtn.setUserData(Search.Type.GENE);
		typeGroup.selectedToggleProperty().addListener(search.getTypeToggleListener());
		
		cellTick.selectedProperty().addListener(search.getCellTickListner());
		ancestorTick.selectedProperty().addListener(search.getAncestorTickListner());
		descendantTick.selectedProperty().addListener(search.getDescendantTickListner());
		colorPicker.setOnAction(search.getColorPickerListener());
		addSearchBtn.setOnAction(search.getAddButtonListener());
	}
	
	private void initLayers() {
		layers = new Layers(colorRulesList);
	}
	
	private void initLineageTree() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lineageTree = new LineageTree(allCellNames);
				lineageTreeRoot = lineageTree.getRoot();
			}
		});
		
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		partsList = new PartsList();
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		allCellNames = AceTreeLoader.getAllCellNames();
		
		init3DWindow(data);
		getPropertiesFrom3DWindow();
		
		setSliderProperties();
		initLineageTree();
		initSearch();
		initLayers();
		search.setRulesList(layers.getRulesList());
		
        addListeners();
        
        setIcons();
        setLabels();
        
        sizeSubscene();
        sizeInfoPane();
        
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	
}
