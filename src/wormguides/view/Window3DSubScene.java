package wormguides.view;

import wormguides.Xform;
import wormguides.model.TableLineageData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class Window3DSubScene{
	
	private TableLineageData data;
	private SubScene subscene;
	private Group root;
	private PerspectiveCamera camera;
	private Xform cameraXform;
	
	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	private double mouseDeltaX, mouseDeltaY;
	
	private int newOriginX, newOriginY, newOriginZ;
	
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	
	private int endTime;
	
	private Slider timeSlider;
	
	private BooleanProperty playingMovie;
	private PlayService playService;
	
	private Sphere[] cells;
	private String[] names;
	private String[] namesLowerCase;
	private Integer[][] positions;
	private Integer[] diameters;
	
	private int selectedIndex;
	
	private String selectedPrefix;
	
	private SphereDragHandler sphereDragHandler;
	
	public Window3DSubScene(double width, double height, TableLineageData data) {
		this.root = new Group();
		this.data = data;
		this.time = new SimpleIntegerProperty();
		time.set(START_TIME);
		
		this.cells = new Sphere[1];
		this.names = new String[1];
		this.positions = new Integer[1][3];
		this.diameters = new Integer[1];
		
		this.selectedIndex = -1;
		this.selectedPrefix = "";
		
		this.sphereDragHandler = new SphereDragHandler();
		
		this.totalNuclei = new SimpleIntegerProperty();
		totalNuclei.set(0);
		
		this.endTime = data.getTotalTimePoints();
		this.subscene = createSubScene(width, height);
		
		this.mousePosX = 0;
		this.mousePosY = 0;
		this.mouseOldX = 0;
		this.mouseOldY = 0;
		this.mouseDeltaX = 0;
		this.mouseDeltaY = 0;
		
		//loadPlayPauseIcons();
		
		this.playService = new PlayService();
		this.playingMovie = new SimpleBooleanProperty();
		playingMovie.set(false);
		playingMovie.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					playService.restart();
				}
				else {
					playService.cancel();
				}
			}
		});
	}
	
	public IntegerProperty getTimeProperty() {
		return this.time;
	}
	
	public IntegerProperty getTotalNucleiProperty() {
		return this.totalNuclei;
	}
	
	public BooleanProperty getPlayingMovieProperty() {
		return this.playingMovie;
	}
	
	public void setSlider(Slider timeSlider) {
		this.timeSlider = timeSlider;
		setSliderProperties();
	}
	
	/*
	private void loadPlayPauseIcons() {
		try {
			this.playIcon = new ImageView(new Image(getClass().getResourceAsStream("./icons/play.png")));
			this.pauseIcon = new ImageView(new Image(getClass().getResourceAsStream("./icons/pause.png")));
		} catch (NullPointerException npe) {
			System.out.println("cannot load play/pause icons");
		}
	}
	*/
	
	/*
	public void setUIComponents(Slider timeSlider, Button backwardButton, Button forwardButton, Button playButton,
			TextField searchTextField) {
		this.timeSlider = timeSlider;
		this.backwardButton = backwardButton;
		this.forwardButton = forwardButton;
		this.playButton = playButton;
		this.searchTextField = searchTextField;
		
		setSliderProperties();
	}
	*/

	private void setSliderProperties() {
		try {
			timeSlider.setMin(1);
			timeSlider.setMax(data.getTotalTimePoints()-1);
			timeSlider.setValue(START_TIME);
		} catch (NullPointerException npe) {
			System.out.println("null time slider");
		}
	}
	
	private SubScene createSubScene(Double width, Double height) {
		this.subscene = new SubScene(root, width, height, true, SceneAntialiasing.DISABLED);

		subscene.setFill(Color.web(FILL_COLOR_HEX, 1.0));
		subscene.setCursor(Cursor.HAND);
		
		subscene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				subscene.setCursor(Cursor.CLOSED_HAND);
				
				mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX)/3;
                mouseDeltaY = (mousePosY - mouseOldY)/3;
                
                double ryAngle = cameraXform.getRotateY();
                cameraXform.setRotateY(ryAngle + mouseDeltaX);
                //System.out.println("Y: "+(ryAngle + mouseDeltaX));
                double rxAngle = cameraXform.getRotateX();
                cameraXform.setRotateX(rxAngle + mouseDeltaY);
                //System.out.println("X: "+(rxAngle + mouseDeltaY));
			}
		});
		
		subscene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				subscene.setCursor(Cursor.HAND);
			}
		});
		
		subscene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				PickResult result = me.getPickResult();
				//System.out.println(result.toString());
				Node node = result.getIntersectedNode();
				//System.out.println(node.toString());
				if (node instanceof Sphere) {
					int index = fetchPickedSphereIndex((Sphere)node);
					selectedIndex = index;
					
					System.out.println(names[selectedIndex]);
					
					/*
					if (index != -1) {
						if (selectedIndex != -1) {
							PhongMaterial material = (PhongMaterial)(cells[selectedIndex].getMaterial());
							Color color = material.getDiffuseColor();
							Color darkerC`olor = color.darker();
							material.setDiffuseColor(darkerColor);
							material.setSpecularColor(darkerColor);
							cells[selectedIndex].setMaterial(material);
						}
						
						selectedIndex = index;
						PhongMaterial material = (PhongMaterial)(cells[selectedIndex].getMaterial());
						Color color = material.getDiffuseColor();
						Color brighterColor = color.brighter();
						material.setDiffuseColor(brighterColor);
						material.setSpecularColor(brighterColor);
						cells[selectedIndex].setMaterial(material);
					}
					*/
				}
				else
					selectedIndex = -1;
			}
		});
		subscene.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
			}
		});
		
		buildCamera();
		buildScene(time.get());
		
		return subscene;
	}
	
	private int fetchPickedSphereIndex(Sphere picked) {
		for (int i = 0; i < names.length; i++) {
			if (cells[i].equals(picked)) {
				
				return i;
			}
		}
		return -1;
	}
	
	// Builds subscene for a given timepoint
	private void buildScene(int time) {
		// Frame is indexed 1 less than the time requested
		time--;
		refreshScene();
		this.names = data.getNames(time);
		this.namesLowerCase = toLowerCaseAll(names);
		this.totalNuclei.set(names.length);
		this.positions = data.getPositions(time);
		this.diameters = data.getDiameters(time);
		this.cells = new Sphere[names.length];
		
		addCellsToScene();
	}
	
	private String[] toLowerCaseAll(String[] in) {
		int length = in.length;
		String[] out = new String[length];
		for (int i = 0; i < length; i++)
			out[i] = in[i].toLowerCase();
		return out;
	}
	
	private void refreshScene() {
		root = new Group();
		root.getChildren().add(cameraXform);
		subscene.setRoot(root);
	}
	
	private void addCellsToScene() {
		for (int i = 0; i < names.length; i ++) {
			Sphere sphere = new Sphere(SIZE_SCALE*diameters[i]/2);
			
			Color color = getColorRule(namesLowerCase[i]);
			PhongMaterial material = new PhongMaterial();
	        material.setDiffuseColor(color);
	        sphere.setMaterial(material);
	        if (!namesLowerCase[i].startsWith(selectedPrefix)) {
	        	sphere.setOpacity(0.05);
	        }
	        
	        sphere.setTranslateX(positions[i][X_COR]);
	        sphere.setTranslateY(positions[i][Y_COR]);
	        sphere.setTranslateZ(positions[i][Z_COR]*Z_SCALE);
	        
	        sphere.setOnMouseDragOver(sphereDragHandler);
	        sphere.setOnMouseDragEntered(sphereDragHandler);
	        sphere.setOnMouseDragExited(sphereDragHandler);
	        //sphere.setOnMouseDragged(sphereDragHandler);
	        
	        cells[i] = sphere;
	        root.getChildren().add(sphere);
	        //System.out.println(name+CS+position[X_COR]+CS+position[Y_COR]+CS+position[Z_COR]);
		}
		
	}
	
	private Color getColorRule(String name) {
		name = name.toLowerCase();
		if (selectedPrefix.isEmpty()) {
			if (name.startsWith("aba"))
				return Color.RED.brighter();
			else if (name.startsWith("abp"))
				return Color.BLUE.brighter();
			else if (name.startsWith("p"))
				return Color.YELLOW.brighter();
			else if (name.startsWith("ems"))
				return Color.GREEN.brighter();
			
			return Color.WHITE;
		}
		else {
			if (name.startsWith(selectedPrefix))
				return Color.GOLD.brighter();
			else {
				//return Color.TRANSPARENT;
				return Color.web(UNSELECTED_COLOR_HEX);
			}
		}
	}
	
	private void buildCamera() {
		this.camera = new PerspectiveCamera(true);
		this.cameraXform = new Xform();
		cameraXform.reset();
		
		root.getChildren().add(cameraXform);
		cameraXform.getChildren().add(camera);
        
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.setRotateX(CAMERA_INITIAL_X_ANGLE); 
        cameraXform.setRotateY(CAMERA_INITIAL_Y_ANGLE);
        
        cameraXform.setScaleX(X_SCALE);
        cameraXform.setScaleY(Y_SCALE);
            
        setNewOrigin();
        
        subscene.setCamera(camera);
	}
	
	private void setNewOrigin() {
		// Find average X Y positions of initial timepoint
		Integer[][] positions = data.getPositions(START_TIME);
		int numCells = positions.length;
		int sumX = 0;
		int sumY = 0;
		int sumZ = 0;
		for (int i = 0; i < numCells; i++) {
			sumX += positions[i][X_COR];
			sumY += positions[i][Y_COR];
			sumZ += positions[i][Z_COR];
		}
		this.newOriginX = Math.round(sumX/numCells);
		this.newOriginY = Math.round(sumY/numCells);
		this.newOriginZ = (int) Math.round(Z_SCALE*sumZ/numCells);
		
		// Set new origin to average X Y positions
		cameraXform.setPivot(newOriginX, newOriginY, newOriginZ);
		cameraXform.setTranslate(newOriginX, newOriginY, newOriginZ);
		
		System.out.println("origin "+newOriginX+CS+newOriginY+CS+newOriginZ);
	}
	
	/*
	private void addLight() {
		PointLight light = new PointLight(Color.WHITE);
        // JavaFX axis: left-top-near is minus, right-bottom-far is plus
        light.getTransforms().addAll(new Translate(-100, -100, -100));
        root.getChildren().add(light);
	}
	*/
	
	public void printCellNames() {
		for (int i = 0; i < names.length; i++)
			System.out.println(names[i]+CS+cells[i]);
	}
	
	// Accessor methods
	public SubScene getSubScene() {
		return subscene;
	}
	
	public Group getRoot() {
		return root;
	}
	
	public ChangeListener<String> getSearchFieldListener() {
		return new SearchFieldListener();
	}
	
	public EventHandler<ActionEvent> getBackwardButtonListener() {
		return new BackwardButtonListener();
	}
	
	public EventHandler<ActionEvent> getForwardButtonListener() {
		return new ForwardButtonListener();
	}
	
	public ChangeListener<Number> getSliderListener() {
		return new SliderListener();
	}
	
	public int getEndTime() {
		return this.endTime;
	}
	
	// Listener classes
	public class SearchFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			selectedPrefix = newValue.toLowerCase();
			buildScene(time.get());
			/*
			if (newValue.isEmpty()) {
				buildScene(time.get());	
			}
			else {
				for (int i = 0; i < names.length; i++) {
					if (namesLowerCase[i].startsWith(selectedPrefix)) {
						System.out.println(names[i]);
						
					}
				}
			}
			*/
		}
	}
	
	public class BackwardButtonListener implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (!playingMovie.get())
				time.set(time.get()-1);
		}
	}
	
	public class ForwardButtonListener implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (!playingMovie.get())
				time.set(time.get()+1);
		}
	}
	
	public class SliderListener implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			time.set(newValue.intValue());
			int t = time.get();
			if (t > 0 & t <= endTime)
				buildScene(t);
		}
	}
	
	private class SphereDragHandler implements EventHandler<MouseDragEvent> {
		@Override
		public void handle(MouseDragEvent event) {
			System.out.println("dragged over sphere");
			return;
		}
	}
	
	private class PlayService extends Service<Void>{
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					while (true) {
						if (isCancelled()) {
							break;
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								//timeSlider.setValue(++time);
								time.set(time.get()+1);
							}
						});
						try {
							Thread.sleep(WAIT_TIME_MILLI);
						} catch (InterruptedException ie) {
							break;
						}
					}
					return null;
				}
			};
		}
		
	}
	
	private static final String CS = ", ";
	
	private static final String FILL_COLOR_HEX = "#272727",
			UNSELECTED_COLOR_HEX = "#333333";
	
	private static final long WAIT_TIME_MILLI = 400;
	
	private static final double CAMERA_INITIAL_DISTANCE = -900;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    
    private static final double CAMERA_NEAR_CLIP = 0.01;
    private static final double CAMERA_FAR_CLIP = 50000;
    
    private static final int START_TIME = 1;
    private static final int X_COR = 0;
    private static final int Y_COR = 1;
    private static final int Z_COR = 2;
    
    private static final double Z_SCALE = 5,
    		X_SCALE = 1,
    		Y_SCALE = 1;
    private static final double SIZE_SCALE = .8;

}
