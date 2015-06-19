package wormguides;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private BorderPane displayPanel;

	public MainApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WormGUIDES");
		
		initRootLayout();
		init3DWindow();
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
            displayPanel = (BorderPane) scene.lookup("#displayPanel");
            
            primaryStage.show();
            
        } catch (IOException e) {
        	System.out.println("Could not initialize root layout.");
            e.printStackTrace();
        }
	}
	
	public void init3DWindow() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Window3DContainer.fxml"));
			AnchorPane container3D = (AnchorPane) loader.load();
			displayPanel.setCenter(container3D);
			
		} catch (IOException e) {
			System.out.println("Could not initialize 3D Window.");
            e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
		
		AceTreeLoader.loadNucFiles(JAR_NAME);
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	private static final double ASPECT_RATIO = (3/4);
}
