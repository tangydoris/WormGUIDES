package wormguides;

import java.io.IOException;

import wormguides.model.TableLineageData;
import wormguides.model.Xform;
import wormguides.view.Window3DSubScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private AnchorPane subSceneContainer;
	private SubScene subscene;

	public MainApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		System.out.println("start");
		
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
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            this.subSceneContainer = (AnchorPane)(scene.lookup(MODEL_COTNAINER_ID));
            if (subSceneContainer == null) {
            	System.out.println("Cannot get 3D model container");
            }

        } catch (IOException e) {
        	System.out.println("Could not initialize root layout.");
            e.printStackTrace();
        }
	}
	
	public void init3DWindow() {
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		Window3DSubScene window3D = new Window3DSubScene(data);
		SubScene subScene = window3D.getSubScene();
		
		subSceneContainer.getChildren().add(subScene);
		AnchorPane.setTopAnchor(subScene,  5.0);
		AnchorPane.setLeftAnchor(subScene,  5.0);
		AnchorPane.setRightAnchor(subScene,  5.0);
		AnchorPane.setBottomAnchor(subScene,  5.0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	private static final String MODEL_COTNAINER_ID = "#modelAnchorPane";
}
