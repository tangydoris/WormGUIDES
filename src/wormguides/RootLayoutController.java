package wormguides;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wormguides.model.PartsList;
import wormguides.model.TableLineageData;
import wormguides.view.Window3DSubScene;

public class RootLayoutController implements Initializable{
	
	// About popup dialog
	private Stage aboutStage;
	private Parent aboutRoot;
	
	// 3D subscene stuff
	private Window3DSubScene window3D;
	private SubScene subscene;
	private DoubleProperty subsceneWidth;
	private DoubleProperty subsceneHeight;
	private DoubleProperty infoPanelHeight;
	
	// Panels stuff
	@FXML public BorderPane displayPanel;
	@FXML public AnchorPane modelAnchorPane;
	@FXML public ScrollPane infoPane;
	
	// Subscene controls
	@FXML public Button backwardButton, forwardButton, playButton;
	@FXML public Label timeLabel, totalNucleiLabel;
	@FXML public Slider timeSlider;
	
	// Search controls
	@FXML public TextField searchField;
	private ArrayList<String> allCellNames;
	private ObservableList<String> searchResults;
	@FXML public ListView<String> searchResultsList;
	
	// Cell selection
	private StringProperty selectedName;
	
	// Layers controls
	@FXML public Button editAbaButton, editAbpButton, editEmsButton;
	@FXML public Button abaEyeButton, abpEyeButton, emsEyeButton;
	@FXML public Button abaCloseButton, abpCloseButton, emsCloseButton;
	@FXML public Button vncEyeButton, dd1EyeButton, nerveRingEyeButton;
	@FXML public Button musEyeButton, bodEyeButton, phaEyeButton, neuEyeButton, aliEyeButton;
	@FXML public Button tagVncEyeButton, tagNerEyeButton, tagGasEyeButton;
	
	// Cell information
	@FXML public Label cellName;
	@FXML public Label cellDescription;
	
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
		if (aboutStage == null) {
			aboutStage = new Stage();
			try {
				aboutRoot = FXMLLoader.load(getClass().getResource("view/About.fxml"));
				aboutStage.setScene(new Scene(aboutRoot));
				aboutStage.setTitle("About WormGUIDES");
				aboutStage.initModality(Modality.APPLICATION_MODAL);
				
				aboutStage.show();
			} catch (IOException e) {
				System.out.println("cannot load about page.");
				e.printStackTrace();
			}
		}
		else
			aboutStage.show();
	}
	
	public void init3DWindow(TableLineageData data) {
		try {
			window3D = new Window3DSubScene(modelAnchorPane.prefWidth(-1), 
					modelAnchorPane.prefHeight(-1), data);
			subscene = window3D.getSubScene();
			modelAnchorPane.getChildren().add(subscene);
			
			window3D.setSlider(timeSlider);
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
			timeSlider.valueProperty().addListener(window3D.getSliderListener());
			backwardButton.setOnAction(window3D.getBackwardButtonListener());
			forwardButton.setOnAction(window3D.getForwardButtonListener());
			
			searchField.textProperty().addListener(window3D.getSearchFieldListener());
			searchResults = FXCollections.observableArrayList();
			searchField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					String searched = newValue.toLowerCase();
					searchResults.clear();
					if (!searched.isEmpty()) {
						for (String name : allCellNames) {
							if (name.toLowerCase().startsWith(searched))
								searchResults.add(name);
						}
						searchResults.sort(new Comparator<String>() {
							@Override
							public int compare(String s0, String s1) {
								return s0.compareTo(s1);
							}
						});
					}
				}
			});
			searchResultsList.setItems(searchResults);
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
			
			// Add selected index manipulation of cell name/description here
			selectedName.addListener(new ChangeListener<String> () {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					String sulston = selectedName.get();
					setSelectedInfo(sulston);
				}
			});
			
		} catch (NullPointerException npe) {
			System.out.println("cannot add listener for oen or more UI components");
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
			this.infoPanelHeight = new SimpleDoubleProperty();
			infoPanelHeight.bind(displayPanel.heightProperty().divide(5));
			infoPane.maxHeightProperty().bind(infoPanelHeight);
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
					int newTime = newValue.intValue();
					if (newTime < 1)
						newTime = 1;
					else if (newTime > window3D.getEndTime())
						newTime = window3D.getEndTime();
					time.set(newTime);
					timeSlider.setValue(newTime);
					timeLabel.setText("Time "+makePaddedTime(newTime));
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
		ImageLoader loader = new ImageLoader(JAR_NAME);
		
		backwardButton.setGraphic(loader.getBackwardIcon());
		forwardButton.setGraphic(loader.getForwardIcon());
		
		this.playIcon = loader.getPlayIcon();
		this.pauseIcon = loader.getPauseIcon();
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
		
		// Add edit icon
		try {
			editAbaButton.setGraphic(loader.getEditIcon());
			editAbpButton.setGraphic(loader.getEditIcon());
			editEmsButton.setGraphic(loader.getEditIcon());
		} catch (NullPointerException npe) {
			System.out.println("cannot set layers edit icon");
		}
		
		// Add close icon
				try {
					abaCloseButton.setGraphic(loader.getCloseIcon());
					abpCloseButton.setGraphic(loader.getCloseIcon());
					emsCloseButton.setGraphic(loader.getCloseIcon());
				} catch (NullPointerException npe) {
					System.out.println("cannot set layers close icon");
				}
		
		// Add eye icon
		try {
			abaEyeButton.setGraphic(loader.getEyeIcon());
			abpEyeButton.setGraphic(loader.getEyeIcon());
			emsEyeButton.setGraphic(loader.getEyeIcon());
			
			vncEyeButton.setGraphic(loader.getEyeIcon());
			dd1EyeButton.setGraphic(loader.getEyeIcon());
			nerveRingEyeButton.setGraphic(loader.getEyeIcon());
			
			musEyeButton.setGraphic(loader.getEyeIcon());
			bodEyeButton.setGraphic(loader.getEyeIcon());
			phaEyeButton.setGraphic(loader.getEyeIcon());
			neuEyeButton.setGraphic(loader.getEyeIcon());
			aliEyeButton.setGraphic(loader.getEyeIcon());
			
			tagVncEyeButton.setGraphic(loader.getEyeIcon());
			tagNerEyeButton.setGraphic(loader.getEyeIcon());
			tagGasEyeButton.setGraphic(loader.getEyeIcon());
			
		} catch (NullPointerException npe) {
			System.out.println("cannot set layers visibility icon");
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		partsList = new PartsList();
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		allCellNames = data.getAllCellNames();
		
		init3DWindow(data);
		getPropertiesFrom3DWindow();
		
        addListeners();
        
        setIcons();
        setLabels();
        
        sizeSubscene();
        sizeInfoPane();
        
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	
}
