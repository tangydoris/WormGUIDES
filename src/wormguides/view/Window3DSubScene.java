package wormguides.view;

import wormguides.Xform;
import wormguides.model.TableLineageData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

public class Window3DSubScene implements Runnable{
	
	private TableLineageData data;
	private SubScene subscene;
	private Group root;
	private PerspectiveCamera camera;
	
	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	private double mouseDeltaX, mouseDeltaY;
	
	private int newOriginX, newOriginY, newOriginZ;
	
	private int time;
	
	private Xform cameraXform;
    
	private Slider timeSlider;
	private Button forwardButton, backwardButton, playButton;
	
	private boolean playingMovie;
	private Image playIcon, pauseIcon;
	
	private Thread thread;
	private final Object waitLock = new Object();
	
	public Window3DSubScene(double width, double height, TableLineageData data) {
		this.thread = new Thread(this);
		thread.start();
		
		this.root = new Group();
		this.data = data;
		this.time = START_TIME;
		this.subscene = createSubScene(width, height);
		
		this.mousePosX = 0;
		this.mousePosY = 0;
		this.mouseOldX = 0;
		this.mouseOldY = 0;
		this.mouseDeltaX = 0;
		this.mouseDeltaY = 0;
		
		this.playingMovie = false;
		loadPlayPauseIcons();
	}
	
	private void loadPlayPauseIcons() {
		try {
			this.playIcon = new Image(getClass().getResourceAsStream("./icons/play.png"));
			this.pauseIcon = new Image(getClass().getResourceAsStream("./icons/pause.png"));
		} catch (NullPointerException npe) {
			System.out.println("cannot load icons");
		}
	}
	
	public void setUIComponents(Slider timeSlider, Button backwardButton, Button forwardButton, Button playButton) {
		this.timeSlider = timeSlider;
		this.backwardButton = backwardButton;
		this.forwardButton = forwardButton;
		this.playButton = playButton;
		
		setSliderProperties();
		addListeners();
	}
	
	private void setSliderProperties() {
		try {
			timeSlider.setMin(1);
			timeSlider.setMax(data.getTotalTimePoints()-1);
			timeSlider.setValue(1);
		} catch (NullPointerException npe) {
			System.out.println("null time slider");
		}
	}
	
	private SubScene createSubScene(Double width, Double height) {
		this.subscene = new SubScene(root, width, height-40, true, SceneAntialiasing.DISABLED);
		AnchorPane.setTopAnchor(subscene,  0.0);
		AnchorPane.setLeftAnchor(subscene,  0.0);
		AnchorPane.setRightAnchor(subscene,  0.0);
		subscene.setFill(Color.GREY);
		subscene.setCursor(Cursor.HAND);
		
		subscene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				subscene.setCursor(Cursor.CLOSED_HAND);
				
				mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getX();
                mousePosY = me.getY();
                mouseDeltaX = (mousePosX - mouseOldX)/2;
                mouseDeltaY = (mousePosY - mouseOldY)/2;
                
                double ryAngle = cameraXform.getRotateY();
                cameraXform.setRotateY(ryAngle - mouseDeltaX);
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
				System.out.println("clicked "+me.getX()+CS+me.getY());
			}
		});
		subscene.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mousePosX = me.getX();
				mousePosY = me.getY();
			}
		});
		
		buildCamera();
		buildScene(time);
		return subscene;
	}
	
	// Builds subscene for a given timepoint
	private void buildScene(int time) {
		// Frame is indexed 1 less than the time requested
		time--;
		refreshScene();
		String[] names = data.getNames(time);
		Integer[][] positions = data.getPositions(time);
		Integer[] diameters = data.getDiameters(time);
		
		for (int i = 0; i < names.length; i++) {
			addCellToScene(names[i], positions[i], diameters[i]);
		}
	}
	
	private void refreshScene() {
		root = new Group();
		root.getChildren().add(cameraXform);
		subscene.setRoot(root);
	}
	
	private void addCellToScene(String name, Integer[] position, Integer diameter) {
		Sphere sphere = new Sphere(diameter/2);
		Color color = getColorRule(name);
		PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        sphere.setMaterial(material);
        sphere.setTranslateX(position[X_COR]);
        sphere.setTranslateY(position[Y_COR]);
        sphere.setTranslateZ(position[Z_COR]*Z_SCALE);
        
        root.getChildren().add(sphere);
        //System.out.println(name+CS+position[X_COR]+CS+position[Y_COR]+CS+position[Z_COR]);
	}
	
	private Color getColorRule(String name) {
		name = name.toLowerCase();
		if (name.startsWith("aba"))
			return Color.RED;
		else if (name.startsWith("abp"))
			return Color.BLUE;
		else if (name.startsWith("p"))
			return Color.YELLOW;
		
		return Color.WHITE;
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
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);     
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
	
	private void addLight() {
		PointLight light = new PointLight(Color.WHITE);
        // JavaFX axis: left-top-near is minus, right-bottom-far is plus
        light.getTransforms().addAll(new Translate(-100, -100, -100));
        root.getChildren().add(light);
	}
	
	// Add listeners to UI components
	private void addListeners() {
		if (timeSlider != null) {
			timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
					time = newValue.intValue();
					buildScene(time);
					//System.out.println(newValue.intValue());
				}
			});
		}
		
		if (backwardButton != null) {
			backwardButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (!playingMovie)
						timeSlider.setValue(--time);
				}
			});
		}
		
		if (forwardButton != null) {
			forwardButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (!playingMovie)
						timeSlider.setValue(++time);
				}
			});
		}
		
		if (playButton != null) {
			playButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public synchronized void handle(ActionEvent event) {
					playingMovie = !playingMovie;
					if (playingMovie) {
						playButton.setGraphic(new ImageView(pauseIcon));
						start();
					}
					else {
						playButton.setGraphic(new ImageView(playIcon));
						pause();
					}
				}
			});
		}
	}
	
    public void start() {
    	System.out.println("start thread");
        synchronized (waitLock) {
            playingMovie = true;
            waitLock.notify();
        }
    }
    
    public void pause() {
    	System.out.println("pause thread");
        synchronized (waitLock) {
            playingMovie = false;
            waitLock.notify();
        }
    }
    
	@Override
	public void run() {
		while (true) {
            while (playingMovie) {
                try {
                	Thread.sleep(500);
                	timeSlider.setValue(++time);
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
            try {
                synchronized (waitLock) {
                    waitLock.wait();
                }
            } catch (Exception e) {
            }
        }
	}
	
	// Accessor methods
	public SubScene getSubScene() {
		return subscene;
	}
	
	public Group getRoot() {
		return root;
	}
	
	
	private static final String CS = ", ";
	
	private static final double CAMERA_INITIAL_DISTANCE = -900;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000;
    
    private static final int START_TIME = 1;
    private static final int X_COR = 0;
    private static final int Y_COR = 1;
    private static final int Z_COR = 2;
    
    private static final double Z_SCALE = 5;
}
