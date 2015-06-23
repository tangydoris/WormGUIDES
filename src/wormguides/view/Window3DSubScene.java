package wormguides.view;

import wormguides.model.TableLineageData;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

public class Window3DSubScene {
	
	private TableLineageData data;
	private SubScene subscene;
	private Group root;
	
	public Window3DSubScene(TableLineageData data) {
		this.data = data;
		this.root = new Group();
		this.subscene = createSubScene();
	}
	
	private SubScene createSubScene() {
		Sphere sphere = new Sphere(1);
		PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        material.setSpecularColor(Color.WHITE);
        sphere.setMaterial(material);
        root.getChildren().add(sphere);
        
        PointLight light = new PointLight(Color.WHITE);
        // JavaFX axis: left-top-near is minus, right-bottom-far is plus
        light.getTransforms().addAll(new Translate(-100, -100, -100));
        root.getChildren().add(light);
        
        boolean fixEyeAtCameraZero = true;
        PerspectiveCamera camera = new PerspectiveCamera(fixEyeAtCameraZero);
        camera.setFieldOfView(30);
        camera.getTransforms().addAll(new Translate(0, 0, -100));
		
		SubScene subscene = new SubScene(root, 554, 516, true, SceneAntialiasing.DISABLED);
		subscene.setFill(Color.WHITE);
		subscene.setCamera(camera);
		
		return subscene;
	}
	
	public SubScene getSubScene() {
		return subscene;
	}
	
	public Group getRoot() {
		return root;
	}
	
	private static final String CS = ", ";
}
