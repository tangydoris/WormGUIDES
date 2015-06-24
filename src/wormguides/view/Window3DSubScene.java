package wormguides.view;

import wormguides.model.TableLineageData;
import javafx.scene.Camera;
import javafx.scene.Cursor;
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
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

public class Window3DSubScene {
	
	private TableLineageData data;
	private SubScene subscene;
	private Group root;
	private PerspectiveCamera camera;
	
	private Xform world;
	
	private Xform cameraXform = new Xform();
    private Xform cameraXform2 = new Xform();
    private Xform cameraXform3 = new Xform();
    
    private Xform axisGroup = new Xform();
	
	public Window3DSubScene(Double width, Double height, TableLineageData data) {
		this.data = data;
		this.root = new Group();
		this.world = new Xform();
		this.subscene = createSubScene(width, height);
	}
	
	private SubScene createSubScene(Double width, Double height) {
		this.subscene = new SubScene(root, width, height, true, SceneAntialiasing.DISABLED);
		AnchorPane.setTopAnchor(subscene,  0.0);
		AnchorPane.setLeftAnchor(subscene,  0.0);
		AnchorPane.setRightAnchor(subscene,  0.0);
		AnchorPane.setBottomAnchor(subscene,  0.0);
		subscene.setFill(Color.GREY);
		subscene.setCursor(Cursor.OPEN_HAND);
		
		buildCamera();
		
		//buildAxes();
		
		//addCylinder();
		addSphere();

		System.out.println("subScene"+CS+subscene.getHeight()+CS+subscene.getWidth());
		
		return subscene;
	}
	
	private void buildAxes() {
		PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
 
        PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
 
        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
 
        Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        Box yAxis = new Box(1, AXIS_LENGTH, 1);
        Box zAxis = new Box(1, 1, AXIS_LENGTH);
        
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
 
        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
	}
	
	private void buildCamera() {
		this.camera = new PerspectiveCamera(true);
		
		root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(0.0);
        
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        
        subscene.setCamera(camera);
	}
	
	private void addLight() {
		PointLight light = new PointLight(Color.WHITE);
        // JavaFX axis: left-top-near is minus, right-bottom-far is plus
        light.getTransforms().addAll(new Translate(-100, -100, -100));
        root.getChildren().add(light);
	}
	
	private void addCylinder() {
		PhongMaterial pinkMaterial = new PhongMaterial();
		pinkMaterial.setDiffuseColor(Color.DARKORCHID);
		pinkMaterial.setSpecularColor(Color.ORCHID);
		Cylinder cylinder = new Cylinder(1, 20);
		cylinder.setMaterial(pinkMaterial);
		root.getChildren().add(cylinder);
	}
	
	private void addSphere() {
		Sphere sphere = new Sphere(5);
		PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        material.setSpecularColor(Color.WHITE);
        sphere.setMaterial(material);
        sphere.setTranslateX(10);
        //sphere.setTranslateY(30);
        root.getChildren().add(sphere);
	}
	
	public SubScene getSubScene() {
		return subscene;
	}
	
	public Group getRoot() {
		return root;
	}
	
	private static final String CS = ", ";
	
	private static final double CAMERA_INITIAL_DISTANCE = -400;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    
    private static final double AXIS_LENGTH = 250.0;
}
