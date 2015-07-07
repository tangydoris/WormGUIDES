package wormguides;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wormguides.model.TableLineageData;
import wormguides.view.Window3DSubScene;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {
	
	private Scene scene;
	
	private Stage primaryStage;
	
	private BorderPane rootLayout;
	private AnchorPane modelContainer;
	private Slider timeSlider;
	private Button backwardButton;
	private Button forwardButton;
	private Button playButton;
	private TextField searchTextField;
	private Label timeLabel;
	private Label totalNucleiLabel;
	
	private Window3DSubScene window3D;
	private SubScene subscene;
	private DoubleProperty subsceneWidth;
	private DoubleProperty subsceneHeight;
	
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private BooleanProperty playingMovie;
	
	private ImageView playIcon;
	private ImageView pauseIcon;

	public MainApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		System.out.println("start");
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WormGUIDES");

		initRootLayout();
		
		primaryStage.setResizable(true);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("exiting...");
				System.exit(0);
			}
		});
		
	}
	
	public void initRootLayout() {
		try {
            // Load root layout from FXML file.
            FXMLLoader loader = new FXMLLoader();
            //loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            
            // Try this for applet loading
            //loader.setLocation(MainApp.class.getResource("RootLayout.fxml"));
            //rootLayout = (BorderPane) loader.load();
            //InputStream input = XMLLoader.loadFXML(JAR_NAME);
            
            InputStream stream = null;
			JarFile jarFile = new JarFile(new File(JAR_NAME));

			Enumeration<JarEntry> entries = jarFile.entries();
			
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
				if (entry.getName().equals(FXML_ENTRY_NAME)) {
					stream = jarFile.getInputStream(entry);
				}
			}
	
            if (stream == null)
            	System.out.println("null input stream for fxml");
            rootLayout = (BorderPane) loader.load(stream);
            
            this.scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            //primaryStage.minWidthProperty().bind(scene.heightProperty());
            //primaryStage.minHeightProperty().bind(scene.widthProperty());
            primaryStage.setResizable(true);
            
            fetchUIComponents();
            init3DWindow();
            addListenersFrom3DWindow();
            getPropertiesFrom3DWindow();
            setLabels();
            
            jarFile.close();
            
        } catch (IOException e) {
        	System.out.println("Could not initialize root layout.");
            e.printStackTrace();
        }
	}
	
	private void getPropertiesFrom3DWindow() {
		this.time = window3D.getTimeProperty();
		this.totalNuclei = window3D.getTotalNucleiProperty();
		this.playingMovie = window3D.getPlayingMovieProperty();
	}
	
	private void fetchUIComponents() {
		this.modelContainer = (AnchorPane)(scene.lookup(MODEL_COTNAINER_ID));
		this.timeSlider = (Slider)(scene.lookup(SLIDER_ID));
		this.backwardButton = (Button)(scene.lookup(BACKWARD_BUTTON_ID));
		this.forwardButton = (Button)(scene.lookup(FORWARD_BUTTON_ID));
		this.playButton = (Button)(scene.lookup(PLAY_BUTTON_ID));
		this.timeLabel = (Label)(scene.lookup(TIME_LABEL_ID));
		this.totalNucleiLabel = (Label)(scene.lookup(TOTAL_NUCLEI_LABEL_ID));
		
		createSearchField();
		setIcons();
	}
	
	private void createSearchField() {
		try {
			this.searchTextField = new TextField();
			AnchorPane.setTopAnchor(searchTextField,  10.0);
			AnchorPane.setLeftAnchor(searchTextField,  10.0);
			AnchorPane.setRightAnchor(searchTextField,  10.0);
			searchTextField.setPrefHeight(TextField.USE_COMPUTED_SIZE);
			searchTextField.setPrefWidth(TextField.USE_COMPUTED_SIZE);
			
			AnchorPane parent = (AnchorPane)(scene.lookup(SEARCH_TAB_PANE_ID));
			parent.getChildren().add(searchTextField);
		} catch (NullPointerException npe) {
			System.out.println("could not instantiate search text field");
		}
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
	}
	
	public void init3DWindow() {
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		try {
			Double width = modelContainer.prefWidth(-1);
			Double height = modelContainer.prefHeight(-1);
			
			this.window3D = new Window3DSubScene(width, height, data);
			this.subscene = window3D.getSubScene();
			modelContainer.getChildren().add(subscene);
			
			window3D.setSlider(timeSlider);
			//window3D.setTimeLabel(timeLabel);
			//window3D.setTotalNucleiLabel(totalNucleiLabel);
			
			sizeSubsceneRelativeToParent();
			
		} catch (NullPointerException npe) {
			System.out.println("Cannot insatntiate 3D view.");
			npe.printStackTrace();
		}
	}
	
	private void addListenersFrom3DWindow() {
		try {
			timeSlider.valueProperty().addListener(window3D.getSliderListener());
			backwardButton.setOnAction(window3D.getBackwardButtonListener());
			forwardButton.setOnAction(window3D.getForwardButtonListener());
			searchTextField.textProperty().addListener(window3D.getSearchFieldListener());
		} catch (NullPointerException npe) {
			System.out.println("cannot add listener for oen or more UI components");
		}
	}
	
	private void setLabels() {
		try {
			time.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
				public void changed(ObservableValue<? extends Number> observable,Number oldValue, Number newValue) {
					totalNucleiLabel.setText(newValue.intValue()+" Nuclei");
				}
			});
			totalNucleiLabel.setText(totalNuclei.get()+" Nuclei");
			totalNucleiLabel.toFront();
		} catch (NullPointerException npe) {
			System.out.println("Cannot set total nuclei label");
		}
	}
	
	/*
	private void addPlayingPropertyFrom3DWindow() {
		this.playingMovie = window3D.getPlayingMovieProperty();
		boolean playing = playingMovie.get();
		if (playing)
			playButton.setGraphic(playIcon);
		else
			playButton.setGraphic(pauseIcon);
		playingMovie.set(!playingMovie.get());
	}
	*/
	
	private String makePaddedTime(int time) {
		if (time < 10)
			return "00"+time;
		else if (time < 100)
			return "0"+time;
		else
			return ""+time;
	}
	
	private void sizeSubsceneRelativeToParent() {
		this.subsceneWidth = new SimpleDoubleProperty();
		subsceneWidth.bind(modelContainer.widthProperty());
		this.subsceneHeight = new SimpleDoubleProperty();
		subsceneHeight.bind(modelContainer.heightProperty().subtract(33));
		
		AnchorPane.setTopAnchor(subscene,  0.0);
		AnchorPane.setLeftAnchor(subscene,  0.0);
		AnchorPane.setRightAnchor(subscene,  0.0);
		AnchorPane.setBottomAnchor(subscene,  33.0);
		
		subscene.widthProperty().bind(subsceneWidth);
		subscene.heightProperty().bind(subsceneHeight);
		subscene.setManaged(false);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	
	// FXML id's
	private static final String MODEL_COTNAINER_ID = "#modelAnchorPane",
			SLIDER_ID = "#timeSlider",
			BACKWARD_BUTTON_ID = "#backwardButton",
			FORWARD_BUTTON_ID = "#forwardButton",
			PLAY_BUTTON_ID = "#playButton",
			//SEARCH_TEXTFIELD_ID = "#searchTextField",
			TIME_LABEL_ID = "#timeLabel",
			TOTAL_NUCLEI_LABEL_ID = "#totalNucleiLabel",
			SEARCH_TAB_PANE_ID = "#searchTabAnchorPane";
	
	private static final String FXML_ENTRY_NAME = "wormguides/view/RootLayout.fxml";
	
	//private static final String CS = ", ";
}
