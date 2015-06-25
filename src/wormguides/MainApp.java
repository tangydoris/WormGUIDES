package wormguides;

import java.io.IOException;

import wormguides.model.TableLineageData;
import wormguides.view.Window3DSubScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private AnchorPane subsceneContainer;
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
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            this.subsceneContainer = (AnchorPane)(scene.lookup(MODEL_COTNAINER_ID));
            if (subsceneContainer == null) {
            	System.out.println("Cannot get 3D model container");
            }
            else {
            	init3DWindow();
            }

        } catch (IOException e) {
        	System.out.println("Could not initialize root layout.");
            e.printStackTrace();
        }
	}
	
	public void init3DWindow() {
		TableLineageData data = AceTreeLoader.loadNucFiles(JAR_NAME);
		
		Double width = subsceneContainer.prefWidth(-1);
		Double height = subsceneContainer.prefHeight(-1);
		
		Window3DSubScene window3D = new Window3DSubScene(width, height, data);
		SubScene subscene = window3D.getSubScene();
		subsceneContainer.getChildren().add(subscene);
		System.out.println("subScene"+CS+subscene.getHeight()+CS+subscene.getWidth());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static final String JAR_NAME = "WormGUIDES.jar";
	private static final String MODEL_COTNAINER_ID = "#modelAnchorPane";
	private static final String CS = ", ";
}
