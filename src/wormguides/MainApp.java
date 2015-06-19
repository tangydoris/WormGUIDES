package wormguides;

import java.io.IOException;

import wormguides.model.TableLineageData;
import wormguides.view.Window3DSubScene;
import wormguides.view.Window3DSubSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	//private BorderPane displayPanel;
	
	private AnchorPane window3DContainer;

	public MainApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WormGUIDES");
		
		initRootLayout();
		init3DWindow();
		
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public void initRootLayout() {
		try {
            // Load root layout from FXML file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            // Get reference to right-side panel to add 3D Window later
            //displayPanel = (BorderPane) scene.lookup("#displayPanel");
            
            window3DContainer = (AnchorPane) scene.lookup("window3DContainer");
            
        } catch (IOException e) {
        	System.out.println("Could not initialize root layout.");
            e.printStackTrace();
        }
	}
	
	public void init3DWindow() {
		/*
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Window3DContainer.fxml"));
			AnchorPane container3D = (AnchorPane) loader.load();
			displayPanel.setCenter(container3D);
			
			Window3DSubSceneController win3DController = loader.getController();
			TableLineageData tld = AceTreeLoader.loadNucFiles(JAR_NAME);
			win3DController.setLineageData(tld);
			
		} catch (IOException e) {
			System.out.println("Could not initialize 3D Window.");
            e.printStackTrace();
		}
		*/
		
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		new Window3DSubScene(window3DContainer, data);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	private static final double ASPECT_RATIO = (3/4);
}
