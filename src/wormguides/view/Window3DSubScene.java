package wormguides.view;

import java.util.function.Function;

import wormguides.model.fxyz.shapes.SphereSegment;

import wormguides.Xform;
import wormguides.model.TableLineageData;
import wormguides.model.fxyz.geometry.Point3D;
import wormguides.model.fxyz.shapes.primitives.SegmentedSphereMesh;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
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
	
	private IntegerProperty selectedIndex;
	private StringProperty selectedName;
	
	private StringProperty searchedPrefix;
	private ObservableList<String> subSceneSearchResults;
	
	public Window3DSubScene(double width, double height, TableLineageData data) {
		root = new Group();
		this.data = data;
		time = new SimpleIntegerProperty();
		time.set(START_TIME);
		
		cells = new Sphere[1];
		names = new String[1];
		positions = new Integer[1][3];
		diameters = new Integer[1];
		
		selectedIndex = new SimpleIntegerProperty();
		selectedIndex.set(-1);
		
		selectedName = new SimpleStringProperty();
		selectedName.set("");
		selectedName.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				int selected = getIndexByName(newValue);
				if (selected != -1)
					selectedIndex.set(selected);
			}
		});
		
		searchedPrefix = new SimpleStringProperty();
		searchedPrefix.set("");
		subSceneSearchResults = FXCollections.observableArrayList();
		
		totalNuclei = new SimpleIntegerProperty();
		totalNuclei.set(0);
		
		endTime = data.getTotalTimePoints();
		subscene = createSubScene(width, height);
		
		mousePosX = 0;
		mousePosY = 0;
		mouseOldX = 0;
		mouseOldY = 0;
		mouseDeltaX = 0;
		mouseDeltaY = 0;
		
		this.playService = new PlayService();
		this.playingMovie = new SimpleBooleanProperty();
		playingMovie.set(false);
		playingMovie.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					playService.restart();
				}
				else {
					playService.cancel();
				}
			}
		});
	}
	
	public ObservableList<String> getSearchResults() {
		return subSceneSearchResults;
	}
	
	public IntegerProperty getTimeProperty() {
		return time;
	}
	
	public IntegerProperty getSelectedIndex() {
		return selectedIndex;
	}
	
	public StringProperty getSelectedName() {
		return selectedName;
	}
	
	public StringProperty getSearchedPrefix() {
		return searchedPrefix;
	}
	
	public IntegerProperty getTotalNucleiProperty() {
		return totalNuclei;
	}
	
	public BooleanProperty getPlayingMovieProperty() {
		return playingMovie;
	}
	
	public void setSlider(Slider timeSlider) {
		this.timeSlider = timeSlider;
		setSliderProperties();
	}
	
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
		subscene = new SubScene(root, width, height, true, SceneAntialiasing.DISABLED);

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
                double rxAngle = cameraXform.getRotateX();
                cameraXform.setRotateX(rxAngle + mouseDeltaY);
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
				Node node = result.getIntersectedNode();
				if (node instanceof Sphere) {
					selectedIndex.set(getPickedSphereIndex((Sphere)node));
					selectedName.set(names[selectedIndex.get()]);
				}
				else
					selectedIndex.set(-1);
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
	
	private int getIndexByName(String name) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name))
				return i;
		}
		return -1;
	}
	
	private int getPickedSphereIndex(Sphere picked) {
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
		names = data.getNames(time);
		namesLowerCase = toLowerCaseAll(names);
		totalNuclei.set(names.length);
		positions = data.getPositions(time);
		diameters = data.getDiameters(time);
		cells = new Sphere[names.length];
		
		addCellsToScene();
		//addStripedCellsToScene();
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
	
	// for testing purposes
	/*
	private void addStripedCellsToScene() {
		for (int i = 0; i < names.length; i ++) {
			SegmentedSphereMesh sphere = new SegmentedSphereMesh(200,00,00,SIZE_SCALE*diameters[i]/2);
			Function<Point3D, Number> dens = p->p.y>0?1:0;
			sphere.setTextureModeVertices3D(3,dens);
			
	        sphere.setTranslateX(positions[i][X_COR]);
	        sphere.setTranslateY(positions[i][Y_COR]);
	        sphere.setTranslateZ(positions[i][Z_COR]*Z_SCALE);
	        
	        //cells[i] = sphere;
	        root.getChildren().add(sphere);
	        //System.out.println(name+CS+position[X_COR]+CS+position[Y_COR]+CS+position[Z_COR]);
		}
	}
	*/
	
	private void addCellsToScene() {
		for (int i = 0; i < names.length; i ++) {
			double radius = SIZE_SCALE*diameters[i]/2;
			Sphere sphere = new Sphere(radius);
			
			SphereSegment sphereSegment = new SphereSegment(radius+1, Color.PURPLE,
	                Math.toRadians(0), Math.toRadians(360),
	                Math.toRadians(-30), Math.toRadians(30),
	                50, false, true);
			
			Color color = getColorRule(namesLowerCase[i]);
			PhongMaterial material = new PhongMaterial();
	        material.setDiffuseColor(color);
	        sphere.setMaterial(material);
	        /*
	        if (!namesLowerCase[i].startsWith(searchedPrefix.get())) {
	        	sphere.setOpacity(0.05);
	        }
	        */
	        
	        /*
	        AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().addAll(sphere, sphereSegment);
	        */
	        
	        double x = positions[i][X_COR];
	        double y = positions[i][Y_COR];
	        double z = positions[i][Z_COR]*Z_SCALE;
	        translate(sphere, x, y, z);
	        translate(sphereSegment, x, y, z);
	        
	        cells[i] = sphere;
	        root.getChildren().addAll(sphereSegment, sphere);//, light);
	        //System.out.println(name+CS+position[X_COR]+CS+position[Y_COR]+CS+position[Z_COR]);
		}
	}
	
	private void translate(Node sphere, double x, double y, double z) {
		sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        sphere.setTranslateZ(z);
	}
	
	private Color getColorRule(String name) {
		name = name.toLowerCase();
		String prefix = searchedPrefix.get();
		if (prefix.isEmpty()) {
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
			if (name.startsWith(prefix))
				return Color.GOLD.brighter().brighter();
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
		//System.out.println("origin "+newOriginX+CS+newOriginY+CS+newOriginZ);
	}
	
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
		public void changed(ObservableValue<? extends String> observable,
				String oldValue, String newValue) {
			searchedPrefix.set(newValue.toLowerCase());
			 
			subSceneSearchResults.clear();
			if (!searchedPrefix.get().isEmpty()) {
				for (int i = 0; i < names.length; i++) {
					//System.out.println(names[i]);
					if (namesLowerCase[i].startsWith(searchedPrefix.get())) {
						//System.out.println(names[i]);
						subSceneSearchResults.add(names[i]);
					}
				}
			}
			
			buildScene(time.get());
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
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			time.set(newValue.intValue());
			int t = time.get();
			if (t > 0 & t <= endTime)
				buildScene(t);
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
    private static final double SIZE_SCALE = .9;

}
