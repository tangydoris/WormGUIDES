package wormguides;

import java.io.IOException;

import wormguides.model.TableLineageData;
import wormguides.view.Window3DSubScene;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
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
	
	private SubScene subscene;
	private ObservableValue<Number> subsceneWidth;
	private ObservableValue<Number> subsceneHeight;

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
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            this.scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            primaryStage.minWidthProperty().bind(scene.heightProperty());
            primaryStage.minHeightProperty().bind(scene.widthProperty());
            
            fetchUIComponents();
            init3DWindow();

        } catch (IOException e) {
        	System.out.println("Could not initialize root layout.");
            e.printStackTrace();
        }
	}
	
	private void fetchUIComponents() {
		this.modelContainer = (AnchorPane)(scene.lookup(MODEL_COTNAINER_ID));
		this.timeSlider = (Slider)(scene.lookup(SLIDER_ID));
		this.backwardButton = (Button)(scene.lookup(BACKWARD_BUTTON_ID));
		this.forwardButton = (Button)(scene.lookup(FORWARD_BUTTON_ID));
		this.playButton = (Button)(scene.lookup(PLAY_BUTTON_ID));
	}
	
	public void init3DWindow() {
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		
		try {
			Double width = modelContainer.prefWidth(-1);
			Double height = modelContainer.prefHeight(-1);
			
			Window3DSubScene window3D = new Window3DSubScene(width, height, data);
			this.subscene = window3D.getSubScene();
			modelContainer.getChildren().add(subscene);
			sizeSubsceneRelativeToParent();
			window3D.setUIComponents(timeSlider, backwardButton, forwardButton, playButton);
			
		} catch (NullPointerException npe) {
			System.out.println("Cannot display 3D model view - could not fetch view container.");
			npe.printStackTrace();
		}
	}
	
	private void sizeSubsceneRelativeToParent() {
		this.subsceneWidth = (ObservableValue<Number>)(modelContainer.widthProperty());
		this.subsceneHeight = (ObservableValue<Number>)(modelContainer.heightProperty().subtract(33));
		
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
			PLAY_BUTTON_ID = "#playButton";
	
	private static final String CS = ", ";
}
