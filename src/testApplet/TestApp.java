package testApplet;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TestApp extends Application {
	
	private Scene scene;
	
	private Stage primaryStage;
	
	private BorderPane rootLayout;

	public TestApp() {
	}
	
	@Override
	public void start(Stage primaryStage) {
		System.out.println("start");
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("- Test App -");

		initRootLayout();

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
        rootLayout = new BorderPane();
        rootLayout.setPrefSize(400, 400);
        rootLayout.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY )));
        this.scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
