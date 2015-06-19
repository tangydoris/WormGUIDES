package wormguides.view;

import wormguides.model.LineageData;
import wormguides.model.TableLineageData;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Window3DSubSceneController {
	
	/*
	@FXML
	private Canvas canvas3D;

	@FXML
	private Slider timeSlider;
	*/

	private AnchorPane window3DContainer;
	
	@FXML
	private SubScene root;
	
	private TableLineageData table;
	
	public Window3DSubSceneController () {
	}
	
	@FXML
	private void initialize() {
		setUpSubScene();
		renderSphere();
	}
	
	public void setParentContainer(AnchorPane parent) {
		window3DContainer = parent;
	}
	
	public void setLineageData(LineageData data) {
		table = (TableLineageData) data;
		// Data loads correctly
	}
	
	// Sets up the subscene with a camera perspective
	public void setUpSubScene() {
		// Create and position camera
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll (
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS),
                new Translate(0, 0, -15)
        );
		
		//root = new SubScene(window3DContainer, window3DContainer.getWidth(), window3DContainer.getHeight());
		root.setFill(Color.RED);
		root.setCamera(camera); 
		
		Group cameraGroup = new Group();
		cameraGroup.getChildren().add(camera);
	}
	
	// Draws sphere in the middle of the canvas
	public void renderSphere() {
		// Create sphere
		PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setSpecularColor(Color.ORANGE);
		redMaterial.setDiffuseColor(Color.RED);
		Sphere sphere = new Sphere(100);
		sphere.setMaterial(redMaterial);
	}
}
