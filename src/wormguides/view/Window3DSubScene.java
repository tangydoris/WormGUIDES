package wormguides.view;

import wormguides.Xform;
import wormguides.model.LineageTree;
import wormguides.model.TableLineageData;
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
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
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
	
	private BooleanProperty playingMovie;
	private PlayService playService;
	private Runnable renderRunnable;
	
	private Sphere[] cells;
	private String[] names;
	private String[] namesLowerCase;
	private boolean[] searched;
	private Integer[][] positions;
	private Integer[] diameters;
	
	private IntegerProperty selectedIndex;
	private StringProperty selectedName;
	
	private StringProperty searchedPrefix;
	private ObservableList<String> subSceneSearchResults;
	
	//private ColorHash colorHash;
	
	public Window3DSubScene(double width, double height, TableLineageData data, LineageTree tree) {
		root = new Group();
		this.data = data;
		time = new SimpleIntegerProperty();
		time.set(START_TIME);
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				buildScene(time.get());
			}
		});
		
		cells = new Sphere[1];
		names = new String[1];
		positions = new Integer[1][3];
		diameters = new Integer[1];
		searched = new boolean[1];
		
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
		
		playService = new PlayService();
		playingMovie = new SimpleBooleanProperty();
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
		
		renderRunnable = new Runnable() {
			@Override
			public void run() {
				refreshScene();
				addCellsToScene();
				
				// render opaque spheres first
				for (int i = 0; i < cells.length; i++) {
					if (searched[i])
						root.getChildren().add(cells[i]);
				}
				// then render opaque spheres
				for (int i = 0; i < cells.length; i++) {
					if (!searched[i])
						root.getChildren().add(cells[i]);
				}
			}
		};
		
		//colorHash = new ColorHash();
		buildScene(time.get());
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
		
		names = data.getNames(time);
		namesLowerCase = toLowerCaseAll(names);
		positions = data.getPositions(time);
		diameters = data.getDiameters(time);
		totalNuclei.set(names.length);
		cells = new Sphere[names.length];
		searched = new boolean[names.length];
		
		// render spheres
		Platform.runLater(renderRunnable);
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
			double radius = SIZE_SCALE*diameters[i]/2;
			Sphere sphere = new Sphere(radius);
			sphere.setDrawMode(DrawMode.FILL);
			
			//sphere.setMaterial(getMaterial(namesLowerCase[i]));
			//Material material = colorHash.getMaterial(namesLowerCase[i]);
			sphere.setMaterial(new PhongMaterial());
	        
	        double x = positions[i][X_COR_INDEX];
	        double y = positions[i][Y_COR_INDEX];
	        double z = positions[i][Z_COR_INDEX]*Z_SCALE;
	        translate(sphere, x, y, z);
	        
	        cells[i] = sphere;
	        if (isSearched(namesLowerCase[i]))
	        	searched[i] = true;
	        else
	        	searched[i] = false;
		}
	}
	
	private boolean isSearched(String name) {
		if (name.startsWith(searchedPrefix.get().toLowerCase()))
			return true;
		else
			return false;
	}
	
	private void translate(Node sphere, double x, double y, double z) {
		sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        sphere.setTranslateZ(z);
	}
	
	/*
	private Material getMaterial(String name) {
		name = name.toLowerCase();
		String prefix = searchedPrefix.get();
		
		WritableImage wImage = new WritableImage(80, 80);
		PixelWriter writer = wImage.getPixelWriter();
		Color color = getColorRule(name);
		// test one stripe3
		for (int j = 0; j < wImage.getHeight(); j++) {
			for (int k = 0; k < wImage.getWidth(); k++) {
				 writer.setColor(k, j, color);
			}
		}
		Material material = new PhongMaterial();
		((PhongMaterial) material).setDiffuseMap(wImage);
		return material;
	}
	*/
	
	/*
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
				return Color.web(UNSELECTED_COLOR_HEX, 0.45d);
			}
		}
	}
	*/
	
	
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
			sumX += positions[i][X_COR_INDEX];
			sumY += positions[i][Y_COR_INDEX];
			sumZ += positions[i][Z_COR_INDEX];
		}
		this.newOriginX = Math.round(sumX/numCells);
		this.newOriginY = Math.round(sumY/numCells);
		this.newOriginZ = (int) Math.round(Z_SCALE*sumZ/numCells);
		
		// Set new origin to average X Y positions
		cameraXform.setPivot(newOriginX, newOriginY, newOriginZ);
		cameraXform.setTranslate(newOriginX, newOriginY, newOriginZ);
	}
	
	public void printCellNames() {
		for (int i = 0; i < names.length; i++)
			System.out.println(names[i]+CS+cells[i]);
	}
	
	public SubScene getSubScene() {
		return subscene;
	}
	
	public Group getRoot() {
		return root;
	}
	
	public ChangeListener<String> getSearchFieldListener() {
		return new SearchFieldListener();
	}
	
	public int getEndTime() {
		return endTime;
	}
	
	public int getStartTime() {
		return START_TIME;
	}
	
	public class SearchFieldListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldValue, String newValue) {
			searchedPrefix.set(newValue.toLowerCase());
			 
			subSceneSearchResults.clear();
			if (!searchedPrefix.get().isEmpty()) {
				for (int i = 0; i < names.length; i++) {
					if (namesLowerCase[i].startsWith(searchedPrefix.get())) {
						//System.out.println(names[i]);
						subSceneSearchResults.add(names[i]);
					}
				}
			}
			
			buildScene(time.get());
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
	
	private static final String FILL_COLOR_HEX = "#272727";
	//private static final String	UNSELECTED_COLOR_HEX = "#333333";
	
	private static final long WAIT_TIME_MILLI = 400;
	
	private static final double CAMERA_INITIAL_DISTANCE = -800,
								CAMERA_INITIAL_X_ANGLE = 0.0,
								CAMERA_INITIAL_Y_ANGLE = 0.0;
    
    private static final double CAMERA_NEAR_CLIP = 0.01,
    							CAMERA_FAR_CLIP = 50000;
    
    private static final int START_TIME = 1;
    
    private static final int X_COR_INDEX = 0,
    						Y_COR_INDEX = 1,
    						Z_COR_INDEX = 2;
    
    private static final double Z_SCALE = 5,
					    		X_SCALE = 1,
					    		Y_SCALE = 1;
    
    private static final double SIZE_SCALE = .9;

}
