package wormguides;

import java.io.IOException;

import wormguides.model.TableLineageData;
import wormguides.view.Window3DSubScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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

	public MainApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		System.out.println("start");
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WormGUIDES");

		initRootLayout();
		
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public void initRootLayout() {
		try {
            // Load root layout from FXML file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            this.scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
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
			
			RootLayoutController controller = new RootLayoutController();
			window3D.setUIComponents(timeSlider, backwardButton, forwardButton, playButton);
			
		} catch (NullPointerException npe) {
			System.out.println("Cannot display 3D model view - could not fetch view container.");
		}
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
