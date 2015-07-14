package wormguides;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wormguides.view.RootLayout;

public class MainApp extends Application {
	
	private Scene scene;
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	public MainApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		System.out.println("start");
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WormGUIDES");
		
		long start_time = System.nanoTime();
		initRootLayout();
		long end_time = System.nanoTime();
		double difference = (end_time - start_time)/1e6;
		System.out.println("root layout init "+difference+"ms");
		
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
            this.rootLayout = (BorderPane) loader.load();
            
            this.scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            
        } catch (IOException e) {
        	System.out.println("could not initialize root layout.");
            e.printStackTrace();
        }
        
		/*
		rootLayout = (new RootLayout()).load();
		
		scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        */
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	//private static final String JAR_NAME = "WormGUIDES.jar";
	
	// FXML id's
	/*
	private static final String MODEL_COTNAINER_ID = "#modelAnchorPane",
			SLIDER_ID = "#timeSlider",
			BACKWARD_BUTTON_ID = "#backwardButton",
			FORWARD_BUTTON_ID = "#forwardButton",
			PLAY_BUTTON_ID = "#playButton",
			
			TIME_LABEL_ID = "#timeLabel",
			TOTAL_NUCLEI_LABEL_ID = "#totalNucleiLabel",
			
			SEARCH_TAB_PANE_ID = "#searchTabAnchorPane",
			DISPLAY_PANEL_ID = "#displayPanel",
			INFORMATION_PANE_ID = "#informationPane",
			
			EDIT_ABA_ICON_ID = "#editAbaIcon",
			EDIT_ABP_ICON_ID = "#editAbpIcon",
			EDIT_EMS_ICON_ID = "#editEmsIcon",
			
			ABA_CLOSE_ICON_ID = "#abaCloseButton",
			ABP_CLOSE_ICON_ID = "#abpCloseButton",
			EMS_CLOSE_ICON_ID = "#emsCloseButton",
			
			ABA_EYE_ICON_ID = "#abaEyeIcon",
			ABP_EYE_ICON_ID = "#abpEyeIcon",
			EMS_EYE_ICON_ID = "#emsEyeIcon",
			VNC_EYE_ICON_ID = "#vncEyeIcon",
			DD1_EYE_ICON_ID = "#dd1EyeIcon",
			NERVE_RING_EYE_ICON_ID = "#nerveRingEyeIcon",
			MUS_EYE_ICON_ID = "#musEyeIcon",
			BOD_EYE_ICON_ID = "#bodEyeIcon",
			PHA_EYE_ICON_ID = "#phaEyeIcon",
			NEU_EYE_ICON_ID = "#neuEyeIcon",
			ALI_EYE_ICON_ID = "#aliEyeIcon",
			TAG_VNC_EYE_ICON_ID = "#tagVncEyeIcon",
			TAG_NER_EYE_ICON_ID = "#tagNerEyeIcon",
			TAG_GAS_EYE_ICON_ID = "#tagGasEyeIcon";
	*/
	
	//private static final String FXML_ENTRY_NAME = "wormguides/view/RootLayout.fxml";
	
	//private static final String CS = ", ";
}
