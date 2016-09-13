package wormguides;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import wormguides.controllers.RootLayoutController;
import wormguides.loaders.ImageLoader;
import wormguides.models.NucleiMgrAdapterResource;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static NucleiMgrAdapterResource nucleiMgrAdapterResource;
    private Scene scene;
    private BorderPane rootLayout;
    private RootLayoutController controller;

    public static void startProgramatically(String[] args, NucleiMgrAdapterResource nmar) {
        nucleiMgrAdapterResource = nmar;
        launch(args);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("start");

        ImageLoader.loadImages();

        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle("WormGUIDES");

        long start_time = System.nanoTime();
        initRootLayout();
        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        System.out.println("root layout init " + difference + "ms");

        primaryStage.setResizable(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();

                if (controller != null) {
                    controller.initCloseApplication();
                }
            }
        });
    }

    public void initRootLayout() {
        // Load root layout from FXML file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/layouts/RootLayout.fxml"));

        if (nucleiMgrAdapterResource != null) {
            loader.setResources(nucleiMgrAdapterResource);
            Platform.setImplicitExit(false);
        }

        controller = new RootLayoutController();
        controller.setStage(primaryStage);
        loader.setController(controller);
        loader.setRoot(controller);

        try {
            rootLayout = loader.load();

            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();

            Parent root = scene.getRoot();
            for (Node node : root.getChildrenUnmodifiable()) {
                node.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
            }

        } catch (IOException e) {
            System.out.println("could not initialize root layout.");
            e.printStackTrace();
        }
    }
}