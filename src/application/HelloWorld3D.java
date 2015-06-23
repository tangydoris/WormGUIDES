package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
 
public class HelloWorld3D extends Application {
    @Override 
    public void start(Stage stage) throws Exception {
        // 3D parts
        Group root = new Group();
 
        Sphere sphere = new Sphere(20);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        material.setSpecularColor(Color.WHITE);
        sphere.setMaterial(material);
        root.getChildren().add(sphere);
        
        PointLight light = new PointLight(Color.WHITE);
        // JavaFX axis: left-top-near is minus, right-bottom-far is plus
        light.getTransforms().addAll(new Translate(-100, -100, -100));
        root.getChildren().add(light);
            
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(30);
        camera.getTransforms().addAll(new Translate(0, 0, -100));
        
        SubScene subscene = new SubScene(root, 800, 600);
        subscene.setCamera(camera);
        
        // 2D controls
        BorderPane pane = new BorderPane();
        Label label = new Label("Hello World!");
        pane.setTop(label);
        pane.setCenter(subscene);
        
        Scene scene = new Scene(pane, 800, 800);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) throws Exception {
        Application.launch(args);
    }
}
