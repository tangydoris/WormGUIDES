package wormguides.controllers;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.sun.javafx.scene.CameraHelper;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.TransformChangedEvent;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import wormguides.ColorComparator;
import wormguides.JavaPicture;
import wormguides.JpegImagesToMovie;
import wormguides.MainApp;
import wormguides.Search;
import wormguides.SearchOption;
import wormguides.Xform;
import wormguides.layers.SearchType;
import wormguides.layers.StoriesLayer;
import wormguides.model.CasesLists;
import wormguides.model.ColorHash;
import wormguides.model.Connectome;
import wormguides.model.LineageData;
import wormguides.model.Note;
import wormguides.model.Note.Display;
import wormguides.model.PartsList;
import wormguides.model.ProductionInfo;
import wormguides.view.AppFont;
import wormguides.model.Rule;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;

/**
 * The controller for the 3D subscene inside the root layout. This class
 * contains the subscene itself, and places it into the AnchorPane called
 * modelAnchorPane inside the root layout. It is also responsible for refreshing
 * the scene on time, search, stories, notes, and rules change. This class
 * contains observable properties that are passed to other classes so that a
 * subscene refresh can be trigger from that other class. <br>
 * <br>
 * 
 * An "entity" in the subscene is either a cell, cell body, or multicellular
 * structure. These are graphically represented by the Shape3Ds Sphere and
 * MeshView available in JavaFX. Sphere's represent cells and MeshView's
 * represent cell bodies and multicellular structures (see {@link Sphere} and
 * {@link MeshView}). Notes and labels are rendered as Texts (see {@link Text}).
 * This class queries the lineage data ({@link LineageData}) and list of scene
 * elements ({@link SceneElementsList}) for a certain time and renders the
 * entities, notes, story, and labels present in that time frame. <br>
 * <br>
 * 
 * For coloring of entities, this class queries an observable list of rules (
 * {@link Rule}) to see which ones apply to a particular entity, then queries
 * the color hash ({@link ColorHash}) for the material ({@link PhongMaterial})
 * to use for the entity.
 * 
 * @author Doris Tang
 * 
 */

public class Window3DController {

	private Stage parentStage;

	private LineageData cellData;

	private SubScene subscene;

	private TextField searchField;

	// transformation stuff
	private Group root;
	private PerspectiveCamera camera;
	private Xform xform;
	private double mousePosX, mousePosY, mousePosZ;
	private double mouseOldX, mouseOldY, mouseOldZ;
	private double mouseDeltaX, mouseDeltaY;
	private int newOriginX, newOriginY, newOriginZ;
	private double angleOfRotation;

	// housekeeping stuff
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private int endTime;
	private int startTime;
	private static Sphere[] spheres;
	private static MeshView[] meshes;
	private static String[] cellNames;
	private static String[] meshNames;
	private boolean[] searchedCells;
	private boolean[] searchedMeshes;
	private Integer[][] positions;
	private Integer[] diameters;
	private DoubleProperty zoom;

	// switching timepoints stuff
	private BooleanProperty playingMovie;
	private PlayService playService;
	private RenderService renderService;

	// subscene click cell selection stuff
	private IntegerProperty selectedIndex;
	private StringProperty selectedName;
	private StringProperty selectedNameLabeled;
	private Stage contextMenuStage;
	private ContextMenuController contextMenuController;
	private CasesLists cases;
	private BooleanProperty cellClicked;

	// searched highlighting stuff
	private boolean inSearch;
	private ObservableList<String> searchResultsList;
	private ArrayList<String> localSearchResults;

	// color rules stuff
	private ColorHash colorHash;
	private ObservableList<Rule> currentRulesList;
	private Comparator<Color> colorComparator;
	private Comparator<Shape3D> opacityComparator;

	// specific boolean listener for gene search results
	private BooleanProperty geneResultsUpdated;

	// opacity value for "other" cells (with no rule attached)
	private DoubleProperty othersOpacity;
	private ArrayList<String> otherCells;

	// rotation stuff
	private final Rotate rotateX;
	private final Rotate rotateY;
	private final Rotate rotateZ;

	// Scene Elements stuff
	private SceneElementsList sceneElementsList;
	private ArrayList<SceneElement> sceneElementsAtTime;
	private ArrayList<MeshView> currentSceneElementMeshes;
	private ArrayList<SceneElement> currentSceneElements;

	// Uniform nuclei sizef
	private boolean uniformSize;

	// Cell body and cell nucleus highlighting in search mode
	private boolean cellNucleusTicked;
	private boolean cellBodyTicked;
	// Story elements stuff
	private StoriesLayer storiesLayer;
	// currentNotes contains all notes that are 'active' within a scene
	// (any note that should be visible in a given frame)
	private ArrayList<Note> currentNotes;
	// Map of current note graphics to their note objects
	private HashMap<Node, Note> currentGraphicNoteMap;
	// Map of current notes to their scene elements
	private HashMap<Note, MeshView> currentNoteMeshMap;

	private VBox overlayVBox;
	private Pane spritesPane;

	// maps of sprite/billboard front notes attached to cell, or cell and time
	private HashMap<Node, VBox> entitySpriteMap;
	private HashMap<Node, Node> billboardFrontEntityMap;

	// Label stuff
	private ArrayList<String> allLabels;
	private ArrayList<String> currentLabels;
	private HashMap<Node, Text> entityLabelMap;
	private Text transientLabelText; // shows up on hover

	// orientation indicator
	private Cylinder orientationIndicator;
	private Rotate indicatorRotation;// this is the time varying component of
										// rotation
	// private Group orientationIndicator;//this isn't needed globally really
	private double[] keyValuesRotate = { 0, 45, 100, 100, 145 };
	private double[] keyFramesRotate = { 1, 20, 320, 340, 400 }; // start

	private EventHandler<MouseEvent> clickableMouseEnteredHandler;
	private EventHandler<MouseEvent> clickableMouseExitedHandler;

	private ProductionInfo productionInfo;
	private Connectome connectome;

	private BooleanProperty bringUpInfoProperty;

	private SubsceneSizeListener subsceneSizeListener;

	private BooleanProperty captureVideo;
	private Vector<File> movieFiles;
	Vector<JavaPicture> javaPictures;
	private int count;
	private String movieName;
	private String moviePath;
	private File frameDir;

	private BooleanProperty update3D;

	private DoubleProperty rotateXAngle;
	private DoubleProperty rotateYAngle;
	private DoubleProperty rotateZAngle;

	private Quaternion quaternion;

	/**
	 * Window3DController class constructor called by
	 * {@link RootLayoutController} upon initialization.
	 * 
	 * @param parent
	 *            {@link Stage} to which the main application belongs to.
	 *            Reference used for context menu (whether it should appear in
	 *            the sulston tree or the 3D subscene.
	 * @param parentPane
	 *            {@link AnchorPane} to which sprites, labels, and the notes
	 *            info panel are added
	 * @param data
	 *            {@link LineageData} to contains cell information loaded from
	 *            the nuclear files
	 * @param cases
	 *            {@link casesList} that contains information about
	 *            terminal/non-terminal cells/anatomy terms
	 * @param info
	 *            {@link ProductionInfo} that contains information about
	 *            segmentation and the movie time offset
	 * @param connectome
	 *            {@link Connectome} that contains information about the
	 *            embryo's connectome
	 * @param bringUpInfoProperty
	 *            {@link BooleanProperty} that should be set to TRUE when the
	 *            info window should be brought up, FALSE otherwise
	 */
	public Window3DController(Stage parent, AnchorPane parentPane, LineageData data, CasesLists cases,
			ProductionInfo info, Connectome connectome, BooleanProperty bringUpInfoProperty) {
		parentStage = parent;

		root = new Group();
		cellData = data;
		productionInfo = info;
		this.connectome = connectome;

		startTime = productionInfo.getDefaultStartTime();

		time = new SimpleIntegerProperty(startTime);
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int t = newValue.intValue();
				if (t < startTime)
					t = startTime;
				if (startTime <= t && t <= endTime)
					buildScene();
			}
		});

		spheres = new Sphere[1];
		meshes = new MeshView[1];
		cellNames = new String[1];
		meshNames = new String[1];
		positions = new Integer[1][3];
		diameters = new Integer[1];
		searchedCells = new boolean[1];
		searchedMeshes = new boolean[1];

		selectedIndex = new SimpleIntegerProperty(-1);

		selectedName = new SimpleStringProperty("");
		selectedName.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int selected = getIndexByCellName(newValue);
				if (selected != -1)
					selectedIndex.set(selected);
			}
		});
		selectedNameLabeled = new SimpleStringProperty("");
		selectedNameLabeled.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty()) {
					// value passed may be a functional name
					// use lineage names instead
					// String lineageName =
					// PartsList.getLineageNameByFunctionalName(newValue);
					// if (lineageName == null)
					// lineageName = newValue;
					/*
					 * TODO Removed above check (caused bugs with P4 clicked in
					 * lineage window) check if this causes problems elsewhere
					 * 
					 * ***** If it does, we should do name translation before
					 * changing name *****
					 */
					String lineageName = newValue;

					selectedName.set(lineageName);

					if (!allLabels.contains(lineageName))
						allLabels.add(lineageName);

					Shape3D entity = getEntityWithName(lineageName);

					// go to labeled name
					int startTime;
					int endTime;

					startTime = Search.getFirstOccurenceOf(lineageName);
					endTime = Search.getLastOccurenceOf(lineageName);

					if (startTime <= 0 || endTime <= 0)
						return; // if the entity doesn't appear in the lifetime
								// of the embryo, don't change the scene

					// if (startTime <= 0) {
					// startTime = 1;
					// }
					//
					// if (endTime <= 0) {
					// endTime = 1;
					// }

					if (time.get() < startTime || time.get() > endTime) {
						time.set(startTime);
					} else {
						insertLabelFor(lineageName, entity);
					}

					highlightActiveCellLabel(entity);
				}
			}
		});

		cellClicked = new SimpleBooleanProperty(false);

		inSearch = false;

		totalNuclei = new SimpleIntegerProperty();
		totalNuclei.set(0);

		endTime = data.getTotalTimePoints() - 1;

		createSubScene(parentPane.widthProperty().get(), parentPane.heightProperty().get());
		parentPane.getChildren().add(subscene);

		subsceneSizeListener = new SubsceneSizeListener();
		parentPane.widthProperty().addListener(subsceneSizeListener);
		parentPane.heightProperty().addListener(subsceneSizeListener);

		mousePosX = 0;
		mousePosY = 0;
		mousePosZ = 0;
		mouseOldX = 0;
		mouseOldY = 0;
		mouseOldZ = 0.;
		mouseDeltaX = 0;
		mouseDeltaY = 0;
		angleOfRotation = 0.;

		playService = new PlayService();
		playingMovie = new SimpleBooleanProperty();
		playingMovie.set(false);
		playingMovie.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				hideContextPopups();
				if (newValue)
					playService.restart();
				else
					playService.cancel();
			}
		});

		renderService = new RenderService();

		zoom = new SimpleDoubleProperty(INITIAL_ZOOM);
		zoom.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				xform.setScale(zoom.get());
				repositionSprites();
				repositionNoteBillboardFronts();
			}
		});
		xform.setScale(zoom.get());

		localSearchResults = new ArrayList<String>();

		geneResultsUpdated = new SimpleBooleanProperty();

		othersOpacity = new SimpleDoubleProperty(1);
		otherCells = new ArrayList<String>();

		rotateX = new Rotate(0, 0, newOriginY, newOriginZ, Rotate.X_AXIS);
		rotateY = new Rotate(0, newOriginX, 0, newOriginZ, Rotate.Y_AXIS);
		rotateZ = new Rotate(0, newOriginX, newOriginY, 0, Rotate.Z_AXIS);

		// initialize
		rotateXAngle = new SimpleDoubleProperty(1);
		rotateYAngle = new SimpleDoubleProperty(1);
		rotateZAngle = new SimpleDoubleProperty(1);

		// set intial values
		rotateXAngle.set(rotateX.getAngle());
		rotateYAngle.set(rotateY.getAngle());
		rotateZAngle.set(rotateZ.getAngle());

		// add listener for control from rotationcontroller
		rotateXAngle.addListener(getRotateXAngleListener());
		rotateYAngle.addListener(getRotateYAngleListener());
		rotateZAngle.addListener(getRotateZAngleListener());

		// set listeners to update doubleproperties ^^
		rotateX.setOnTransformChanged(getRotateXChangeHandler());
		rotateY.setOnTransformChanged(getRotateYChangeHandler());
		rotateZ.setOnTransformChanged(getRotateZChangeHandler());

		quaternion = new Quaternion();

		uniformSize = false;

		currentRulesList = FXCollections.observableArrayList();

		colorHash = new ColorHash();
		colorComparator = new ColorComparator();
		opacityComparator = new OpacityComparator();

		currentSceneElementMeshes = new ArrayList<MeshView>();
		currentSceneElements = new ArrayList<SceneElement>();

		currentNotes = new ArrayList<Note>();
		currentGraphicNoteMap = new HashMap<Node, Note>();
		currentNoteMeshMap = new HashMap<Note, MeshView>();
		entitySpriteMap = new HashMap<Node, VBox>();
		billboardFrontEntityMap = new HashMap<Node, Node>();

		allLabels = new ArrayList<String>();
		currentLabels = new ArrayList<String>();
		entityLabelMap = new HashMap<Node, Text>();

		EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseEvent(event);
			}
		};
		subscene.setOnMouseClicked(handler);
		subscene.setOnMouseDragged(handler);
		subscene.setOnMouseEntered(handler);
		subscene.setOnMousePressed(handler);
		subscene.setOnMouseReleased(handler);

		setNotesPane(parentPane);

		clickableMouseEnteredHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				spritesPane.setCursor(Cursor.HAND);
			}
		};
		clickableMouseExitedHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				spritesPane.setCursor(Cursor.DEFAULT);
			}
		};

		this.cases = cases;

		movieFiles = new Vector<File>();
		javaPictures = new Vector<JavaPicture>();
		count = -1;

		// set up the orientation indicator in bottom right corner
		double radius = 5.0;
		double height = 15.0;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.RED);
		orientationIndicator = new Cylinder(radius, height);
		orientationIndicator.getTransforms().addAll(rotateX, rotateY, rotateZ);
		orientationIndicator.setMaterial(material);

		xform.getChildren().add(createOrientationIndicator());

		this.bringUpInfoProperty = bringUpInfoProperty;

		initializeUpdate3D();
	}

	public void initializeWithCannonicalOrientation() {
		// set default cannonical orientations

		rotateX.setAngle(cannonicalOrientationX);
		rotateY.setAngle(cannonicalOrientationY);
		rotateZ.setAngle(cannonicalOrientationZ);

		repositionSprites();
		repositionNoteBillboardFronts();
	}

	private Group createOrientationIndicator() {
		indicatorRotation = new Rotate();
		// top level group
		// had rotation to make it match main rotation
		Group orientationIndicator = new Group();
		// has rotation to make it match biological orientation
		Group middleTransformGroup = new Group();

		// set up the orientation indicator in bottom right corner
		Text t = makeNoteBillboardText("P     A");
		t.setTranslateX(-10);
		middleTransformGroup.getChildren().add(t);

		t = makeNoteBillboardText("D     V");
		t.setTranslateX(-42);
		t.setTranslateY(32);
		t.setRotate(90);
		middleTransformGroup.getChildren().add(t);

		t = makeNoteBillboardText("L    R");
		t.setTranslateX(5);
		t.setTranslateZ(10);
		t.getTransforms().add(new Rotate(90, new Point3D(0, 1, 0)));
		middleTransformGroup.getChildren().add(t);

		middleTransformGroup.getTransforms().add(new Rotate(-30, 0, 0));// rotation
																		// to
																		// match
																		// lateral
																		// orientation
																		// in
																		// image
		middleTransformGroup.getTransforms().add(new Scale(3, 3, 3));
		// xy relocates z shrinks apparent by moving away from camera? improves
		// resolution?
		orientationIndicator.getTransforms().add(new Translate(270, 200, 800));
		orientationIndicator.getTransforms().add(new Translate(-newOriginX, -newOriginY, -newOriginZ));
		orientationIndicator.getTransforms().addAll(rotateZ, rotateY, rotateX);
		orientationIndicator.getTransforms().add(new Translate(newOriginX, newOriginY, newOriginZ));
		orientationIndicator.getChildren().add(middleTransformGroup);
		middleTransformGroup.getTransforms().add(indicatorRotation);
		return orientationIndicator;
	}

	private double computeInterpolatedValue(int timevalue, double[] keyFrames, double[] keyValues) {
		if (timevalue <= keyFrames[0])
			return keyValues[0];

		if (timevalue >= keyFrames[keyFrames.length - 1])
			return keyValues[keyValues.length - 1];

		int i;
		for (i = 0; i < keyFrames.length; i++) {
			if (keyFrames[i] == timevalue)
				return (keyValues[i]);

			if (keyFrames[i] > timevalue)
				break;
		}

		// interpolate btw values at i and i-1
		double alpha = ((timevalue - keyFrames[i - 1]) / (keyFrames[i] - keyFrames[i - 1]));
		double value = keyValues[i] * alpha + keyValues[i - 1] * (1 - alpha);
		// System.out.println("alpha "+alpha+keyValues[i-1]+" "+keyValues[i]+"
		// value "+value);
		return value;
	}

	private void initializeUpdate3D() {
		update3D = new SimpleBooleanProperty(false);

		update3D.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) { // i.e. out of focus, now refresh the
										// scene
					buildScene();
					update3D.set(false);
				}
			}
		});
	}

	public void addListenerToRebuildSceneFlag(BooleanProperty flag) {
		flag.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue)
					buildScene();
			}
		});
	}

	public ColorHash getColorHash() {
		return colorHash;
	}

	/**
	 * Inserts a transient label into the sprites pane for the specified entity
	 * if the entity is an 'other' entity that is less than 10% opaque.
	 * 
	 * @param name
	 *            String containing the name that appears on the transient label
	 * @param entity
	 *            The entity {@link Node} that the label should appear on
	 */
	private void transientLabel(String name, Node entity) {
		/*
		if (othersOpacity.get() > .1 || (othersOpacity.get() <= .1 && currentRulesApplyTo(name))) {
			showTransientLabel(name, entity);
		}
		*/
		if (currentRulesApplyTo(name))
			showTransientLabel(name, entity);
	}

	private void showTransientLabel(String name, Node entity) {
		boolean labelDrawn = false;

		if (currentLabels.contains(name))
			labelDrawn = true;

		if (!labelDrawn && entity != null) {
			Bounds b = entity.getBoundsInParent();

			if (b != null) {
				String funcName = PartsList.getFunctionalNameByLineageName(name);
				if (funcName != null)
					name = funcName;

				transientLabelText = makeNoteSpriteText(name);

				transientLabelText.setWrappingWidth(-1);
				transientLabelText.setFill(Color.web(TRANSIENT_LABEL_COLOR_HEX));
				transientLabelText.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						event.consume();
					}
				});
				transientLabelText.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						event.consume();
					}
				});

				Point2D p = CameraHelper.project(camera, new Point3D((b.getMinX() + b.getMaxX()) / 2,
						(b.getMinY() + b.getMaxY()) / 2, (b.getMaxZ() + b.getMinZ()) / 2));
				double x = p.getX();
				double y = p.getY();

				double vOffset = b.getHeight() / 2;
				double hOffset = b.getWidth() / 2;

				x += hOffset;
				y -= vOffset + 5;

				transientLabelText.getTransforms().add(new Translate(x, y));

				spritesPane.getChildren().add(transientLabelText);
			}
		}
	}

	/**
	 * Removes transient label from sprites pane.
	 */
	private void removeTransientLabel() {
		spritesPane.getChildren().remove(transientLabelText);
	}

	// Called by RootLayoutController to set the loaded SceneElementsList
	public void setSceneElementsList(SceneElementsList list) {
		if (list != null)
			sceneElementsList = list;
	}

	public void setStoriesLayer(StoriesLayer layer) {
		if (layer != null) {
			storiesLayer = layer;
			if (update3D != null) {
				initializeUpdate3D();
			}
			storiesLayer.setUpdate3DProperty(update3D);

			buildScene();
		}
	}

	public BooleanProperty getUpdate3DProperty() {
		return this.update3D;
	}

	public IntegerProperty getTimeProperty() {
		return time;
	}

	public DoubleProperty getZoomProperty() {
		return zoom;
	}

	public IntegerProperty getSelectedIndex() {
		return selectedIndex;
	}

	public StringProperty getSelectedNameLabeled() {
		return selectedNameLabeled;
	}

	public StringProperty getSelectedName() {
		return selectedName;
	}

	public BooleanProperty getCellClicked() {
		return cellClicked;
	}

	public IntegerProperty getTotalNucleiProperty() {
		return totalNuclei;
	}

	public BooleanProperty getPlayingMovieProperty() {
		return playingMovie;
	}

	@SuppressWarnings("unchecked")
	public void handleMouseEvent(MouseEvent me) {
		EventType<MouseEvent> type = (EventType<MouseEvent>) me.getEventType();

		if (type == MouseEvent.MOUSE_ENTERED_TARGET || type == MouseEvent.MOUSE_ENTERED
				|| type == MouseEvent.MOUSE_RELEASED || type == MouseEvent.MOUSE_MOVED)
			handleMouseReleasedOrEntered();

		else if (type == MouseEvent.MOUSE_CLICKED && me.isStillSincePress())
			handleMouseClicked(me);

		else if (type == MouseEvent.MOUSE_DRAGGED)
			handleMouseDragged(me);

		else if (type == MouseEvent.MOUSE_PRESSED)
			handleMousePressed(me);
	}

	private void handleMouseDragged(MouseEvent event) {
		hideContextPopups();

		spritesPane.setCursor(Cursor.CLOSED_HAND);

		mouseOldX = mousePosX;
		mouseOldY = mousePosY;
		mouseOldZ = mousePosZ;
		mousePosX = event.getSceneX();
		mousePosY = event.getSceneY();
		mouseDeltaX = (mousePosX - mouseOldX);
		mouseDeltaY = (mousePosY - mouseOldY);
		mouseDeltaX /= 4;
		mouseDeltaY /= 4;

		angleOfRotation = rotationAngleFromMouseMovement();
		mousePosZ = computeZCoord(mousePosX, mousePosY, angleOfRotation);
		// mousePosZ = 0;

		if (event.isSecondaryButtonDown() || event.isMetaDown() || event.isControlDown()) {
			double tx = xform.t.getTx() - mouseDeltaX;
			double ty = xform.t.getTy() - mouseDeltaY;

			if (tx > 0 && tx < 450)
				xform.t.setX(tx);
			if (ty > 0 && ty < 450)
				xform.t.setY(ty);

			repositionSprites();
			repositionNoteBillboardFronts();
		}

		else if (event.isPrimaryButtonDown()) {
			mouseDeltaX /= 2;
			mouseDeltaY /= 2;

			/*
			 * TODO how to get Z COORDINATE?
			 */
			if (quaternion != null) {
				// double[] vectorToOldMousePos = vectorBWPoints(newOriginX,
				// newOriginY, newOriginZ, mouseOldX, mouseOldY, mouseOldZ);
				// double[] vectorToNewMousePos = vectorBWPoints(newOriginX,
				// newOriginY, newOriginZ, mousePosX, mousePosY, mousePosZ);

				double[] vectorToOldMousePos = vectorBWPoints(mouseOldX, mouseOldY, mouseOldZ, newOriginX, newOriginY,
						newOriginZ);
				double[] vectorToNewMousePos = vectorBWPoints(mousePosX, mousePosY, mousePosZ, newOriginX, newOriginY,
						newOriginZ);

				if (vectorToOldMousePos.length == 3 && vectorToNewMousePos.length == 3) {
					// System.out.println("from origin to old mouse pos: <" +
					// vectorToOldMousePos[0] + ", " + vectorToOldMousePos[1] +
					// ", " + vectorToOldMousePos[2] + ">");
					// System.out.println("from origin to old mouse pos: <" +
					// vectorToNewMousePos[0] + ", " + vectorToNewMousePos[1] +
					// ", " + vectorToNewMousePos[2] + ">");
					// System.out.println(" ");

					// compute cross product
					double[] cross = crossProduct(vectorToNewMousePos, vectorToOldMousePos);
					if (cross.length == 3) {
						// System.out.println("cross product: <" + cross[0] + ",
						// " + cross[1] + ", " + cross[2] + ">");
						quaternion.updateOnRotate(angleOfRotation, cross[0], cross[1], cross[2]);

						ArrayList<Double> eulerAngles = quaternion.toEulerRotation();

						if (eulerAngles.size() == 3) {
							// rotateX.setAngle(eulerAngles.get(2));
							// rotateY.setAngle(eulerAngles.get(0));
						}
					}
				}
			}

			rotateX.setAngle((rotateX.getAngle() + mouseDeltaY) % 360);
			rotateY.setAngle((rotateY.getAngle() - mouseDeltaX) % 360);

			repositionSprites();
			repositionNoteBillboardFronts();
		}
	}

	private void handleMouseReleasedOrEntered() {
		spritesPane.setCursor(Cursor.DEFAULT);
	}

	private void handleMouseClicked(MouseEvent event) {
		spritesPane.setCursor(Cursor.HAND);

		hideContextPopups();

		Node node = event.getPickResult().getIntersectedNode();

		// Nucleus
		if (node instanceof Sphere) {
			Sphere picked = (Sphere) node;
			selectedIndex.set(getPickedSphereIndex(picked));
			String name = normalizeName(cellNames[selectedIndex.get()]);
			selectedName.set(name);
			cellClicked.set(true);

			if (event.getButton() == MouseButton.SECONDARY
					|| (event.getButton() == MouseButton.PRIMARY && (event.isMetaDown() || event.isControlDown()))) {
				showContextMenu(name, event.getScreenX(), event.getScreenY(), SearchOption.CELLNUCLEUS);
			} else if (event.getButton() == MouseButton.PRIMARY) {
				if (allLabels.contains(name))
					removeLabelFor(name);

				else {
					if (!allLabels.contains(name)) {
						allLabels.add(name);
						currentLabels.add(name);

						Shape3D entity = getEntityWithName(name);
						insertLabelFor(name, entity);
						highlightActiveCellLabel(entity);
					}
				}
			}

		}

		// Cell body/structure
		else if (node instanceof MeshView) {
			boolean found = false;
			for (int i = 0; i < currentSceneElementMeshes.size(); i++) {
				MeshView curr = currentSceneElementMeshes.get(i);
				if (curr.equals(node)) {
					SceneElement clickedSceneElement = currentSceneElements.get(i);
					String name = normalizeName(clickedSceneElement.getSceneName());
					selectedName.set(name);
					found = true;

					if (event.getButton() == MouseButton.SECONDARY || (event.getButton() == MouseButton.PRIMARY
							&& (event.isMetaDown() || event.isControlDown()))) {
						if (sceneElementsList.isMulticellStructureName(name))
							showContextMenu(name, event.getScreenX(), event.getScreenY(),
									SearchOption.MULTICELLULAR_NAME_BASED);
						else
							showContextMenu(name, event.getScreenX(), event.getScreenY(), SearchOption.CELLBODY);
					}

					else if (event.getButton() == MouseButton.PRIMARY) {
						if (allLabels.contains(name))
							removeLabelFor(name);

						else {
							allLabels.add(name);
							currentLabels.add(name);
							insertLabelFor(name, node);

							highlightActiveCellLabel((Shape3D) node);
						}
					}

					break;
				}
			}

			// Note structure
			if (!found) {
				for (Note note : currentNoteMeshMap.keySet()) {
					if (currentNoteMeshMap.get(note).equals(node))
						selectedName.set(note.getTagName());
				}
			}
		} else {
			selectedIndex.set(-1);
			selectedName.set("");
		}
	}

	private double[] vectorBWPoints(double px, double py, double pz, double qx, double qy, double qz) {
		double[] vector = new double[3];

		double vx, vy, vz;

		vx = qx - px;
		vy = qy - py;
		vz = qz - pz;

		vector[0] = vx;
		vector[1] = vy;
		vector[2] = vz;

		return vector;
	}

	/*
	 * TODO fix this
	 * 
	 */
	// http://stackoverflow.com/questions/14954317/know-coordinate-of-z-from-xy-value-and-angle
	// --> law of cosines: https://en.wikipedia.org/wiki/Law_of_cosines
	// http://answers.ros.org/question/42803/convert-coordinates-2d-to-3d-point-theoretical-question/
	private double computeZCoord(double xCoord, double yCoord, double angleOfRotation) {
		return Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2) - (2 * xCoord * yCoord * Math.cos(angleOfRotation)));
	}

	// http://math.stackexchange.com/questions/59/calculating-an-angle-from-2-points-in-space
	private double rotationAngleFromMouseMovement() {
		double rotationAngleRadians = Math
				.acos(((mouseOldX * mousePosX) + (mouseOldY * mousePosY) + (mouseOldZ * mousePosZ))
						/ Math.sqrt((Math.pow(mouseOldX, 2) + Math.pow(mouseOldY, 2) + Math.pow(mouseOldZ, 2))
								* (Math.pow(mousePosX, 2) + Math.pow(mousePosY, 2) + Math.pow(mousePosZ, 2))));

		return rotationAngleRadians;
	}

	// http://mathworld.wolfram.com/CrossProduct.html
	private double[] crossProduct(double[] u, double[] v) {
		if (u.length != 3 || v.length != 3)
			return null;

		double[] cross = new double[3];

		double cx, cy, cz;
		cx = (u[1] * v[2]) - (u[2] * v[1]);
		cy = (u[2] * v[0]) - (u[0] * v[2]);
		cz = (u[0] * v[1]) - (u[1] * v[0]);

		cross[0] = cx;
		cross[1] = cy;
		cross[2] = cz;

		return cross;
	}

	private String normalizeName(String name) {
		if (name.indexOf("(") > -1)
			name = name.substring(0, name.indexOf("("));
		name = name.trim();
		return name;
	}

	private void handleMousePressed(MouseEvent event) {
		mousePosX = event.getSceneX();
		mousePosY = event.getSceneY();
	}

	private void showContextMenu(String name, double sceneX, double sceneY, SearchOption option) {
		if (contextMenuStage == null)
			initContextMenuStage();

		contextMenuController.setName(name);

		String funcName = PartsList.getFunctionalNameByLineageName(name);
		if (funcName == null)
			contextMenuController.disableTerminalCaseFunctions(true);
		else
			contextMenuController.disableTerminalCaseFunctions(false);

		contextMenuController.setColorButtonListener(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Rule rule = Search.addColorRule(SearchType.LINEAGE, name, Color.WHITE, option);
				rule.showEditStage(parentStage);
				contextMenuStage.hide();
			}
		});

		contextMenuController.setColorNeighborsButtonListener(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// color neighboring cell bodies, multicellular structures, as
				// well as nuclei
				Rule rule = Search.addColorRule(SearchType.NEIGHBOR, name, Color.WHITE, SearchOption.CELLNUCLEUS,
						SearchOption.CELLBODY);
				rule.showEditStage(parentStage);
				contextMenuStage.hide();
			}
		});

		contextMenuStage.setX(sceneX);
		contextMenuStage.setY(sceneY);

		contextMenuStage.show();
		((Stage) contextMenuStage.getScene().getWindow()).toFront();
	}

	private void initContextMenuStage() {
		if (contextMenuStage == null) {
			contextMenuController = new ContextMenuController(parentStage, bringUpInfoProperty, cases, productionInfo,
					connectome);

			contextMenuStage = new Stage();
			contextMenuStage.initStyle(StageStyle.UNDECORATED);

			contextMenuController.setOwnStage(contextMenuStage);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/layouts/ContextMenuLayout.fxml"));

			loader.setController(contextMenuController);
			loader.setRoot(contextMenuController);

			try {
				contextMenuStage.setScene(new Scene((AnchorPane) loader.load()));
				contextMenuStage.initModality(Modality.NONE);
				contextMenuStage.setResizable(false);
				contextMenuStage.setTitle("Menu");

				for (Node node : contextMenuStage.getScene().getRoot().getChildrenUnmodifiable()) {
					node.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
				}

				contextMenuController.setInfoButtonListener(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						contextMenuStage.hide();
					}
				});

			} catch (IOException e) {
				System.out.println("error in initializing context menu.");
				e.printStackTrace();
			}
		}
	}

	private void createSubScene(Double width, Double height) {
		subscene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
		subscene.setFill(Color.web(FILL_COLOR_HEX));

		buildCamera();
	}

	public ContextMenuController getContextMenuController() {
		if (contextMenuStage == null)
			initContextMenuStage();

		return contextMenuController;
	}

	private void repositionNoteBillboardFronts() {
		for (Node billboard : billboardFrontEntityMap.keySet()) {
			Node entity = billboardFrontEntityMap.get(billboard);
			if (entity != null) {
				Bounds b = entity.getBoundsInParent();

				if (b != null) {
					billboard.getTransforms().clear();
					double x = b.getMaxX();
					double y = b.getMaxY() + b.getHeight() / 2;
					double z = b.getMaxZ();
					billboard.getTransforms().addAll(new Translate(x, y, z),
							new Scale(BILLBOARD_SCALE, BILLBOARD_SCALE));
				}
			}
		}
	}

	/**
	 * Repositions sprites (labels and note sprites) by projecting the sphere's
	 * 3d coordinate onto the front of the subscene
	 */
	private void repositionSprites() {
		for (Node entity : entitySpriteMap.keySet())
			alignTextWithEntity(entitySpriteMap.get(entity), entity, false);

		for (Node entity : entityLabelMap.keySet())
			alignTextWithEntity(entityLabelMap.get(entity), entity, true);
	}

	// Input text is the note/label geometry
	// Input entity is the cell/body/mesh that the text should align with
	private void alignTextWithEntity(Node noteGraphic, Node node, boolean isLabel) {
		if (node != null) {
			Bounds b = node.getBoundsInParent();
			// System.out.println("static - "+b.toString());

			if (b != null) {
				noteGraphic.getTransforms().clear();
				Point2D p = CameraHelper.project(camera, new Point3D((b.getMinX() + b.getMaxX()) / 2,
						(b.getMinY() + b.getMaxY()) / 2, (b.getMaxZ() + b.getMinZ()) / 2));
				double x = p.getX();
				double y = p.getY();

				double vOffset = b.getHeight() / 2;
				double hOffset = b.getWidth() / 2;

				if (isLabel) {
					x += hOffset;
					y -= (vOffset + 5);
				} else {
					x += hOffset;
					y += vOffset + 5;
				}

				noteGraphic.getTransforms().add(new Translate(x, y));
			}
		}
	}

	private int getIndexByCellName(String name) {
		for (int i = 0; i < cellNames.length; i++) {
			if (cellNames[i].equals(name))
				return i;
		}
		return -1;
	}

	private int getPickedSphereIndex(Sphere picked) {
		for (int i = 0; i < cellNames.length; i++) {
			if (spheres[i].equals(picked)) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * Calls service to retrieve subscene data at current time point then render
	 * entities, notes, and labels
	 */
	private void buildScene() {
		// Spool thread for actual rendering to subscene
		renderService.restart();
	}

	private void getSceneData() {
		final int requestedTime = time.get();
		cellNames = cellData.getNames(requestedTime);
		positions = cellData.getPositions(requestedTime);
		diameters = cellData.getDiameters(requestedTime);
		otherCells.clear();

		totalNuclei.set(cellNames.length);

		spheres = new Sphere[cellNames.length];
		meshes = new MeshView[meshNames.length];

		// Start scene element list, find scene elements present at time, build
		// and meshes
		// empty meshes and scene element references from last rendering. Same
		// for story elements
		if (sceneElementsList != null)
			meshNames = sceneElementsList.getSceneElementNamesAtTime(requestedTime);

		if (!currentSceneElementMeshes.isEmpty()) {
			currentSceneElementMeshes.clear();
			currentSceneElements.clear();
		}

		if (sceneElementsList != null) {
			// System.out.println("scene elements at time "+requestedTime);
			sceneElementsAtTime = sceneElementsList.getSceneElementsAtTime(requestedTime);
			for (int i = 0; i < sceneElementsAtTime.size(); i++) {
				// add meshes from each scene element
				SceneElement se = sceneElementsAtTime.get(i);
				MeshView mesh = se.buildGeometry(requestedTime - 1);

				if (mesh != null) {
					// null mesh when file not found thrown
					mesh.getTransforms().addAll(rotateZ, rotateY, rotateX);

					// add rendered mesh to meshes list
					currentSceneElementMeshes.add(mesh);

					// add scene element to rendered scene element reference for
					// on click responsiveness
					currentSceneElements.add(se);
					// System.out.println(se.toString());
				}
			}
		}
		// End scene element mesh loading/building

		// Label stuff
		entityLabelMap.clear();
		currentLabels.clear();

		for (String label : allLabels) {
			for (String cell : cellNames) {
				if (!currentLabels.contains(label) && cell.equalsIgnoreCase(label)) {
					currentLabels.add(label);
					break;
				}
			}

			for (int i = 0; i < currentSceneElements.size(); i++) {
				if (!currentLabels.contains(label)
						&& label.equalsIgnoreCase(normalizeName(currentSceneElements.get(i).getSceneName()))) {
					currentLabels.add(label);
					break;
				}
			}
		}
		// End label stuff

		// Story stuff
		// Notes are indexed starting from 1 (or 1+offset shown to user)
		if (storiesLayer != null) {
			currentNotes.clear();

			currentNoteMeshMap.clear();
			currentGraphicNoteMap.clear();

			entitySpriteMap.clear();
			billboardFrontEntityMap.clear();

			currentNotes = storiesLayer.getNotesAtTime(requestedTime);

			for (Note note : currentNotes) {
				// Revert to overlay display if we have invalid
				// display/attachment
				// type combination
				if (note.hasLocationError() || note.hasEntityNameError())
					note.setTagDisplay(Display.OVERLAY);

				// make mesh views for scene elements from note resources
				if (note.hasSceneElements()) {
					for (SceneElement se : note.getSceneElements()) {
						MeshView mesh = se.buildGeometry(requestedTime);

						if (mesh != null) {
							mesh.setMaterial(colorHash.getNoteSceneElementMaterial());
							mesh.getTransforms().addAll(rotateZ, rotateY, rotateX);
							currentNoteMeshMap.put(note, mesh);
						}
					}
				}
			}
		}
		// End story stuff

		// Search stuff
		if (localSearchResults.isEmpty()) {
			searchedCells = new boolean[cellNames.length];
			searchedMeshes = new boolean[meshNames.length];
		} else
			consultSearchResultsList();
		// End search stuff
	}

	private void updateLocalSearchResults() {
		if (searchResultsList == null)
			return;

		localSearchResults.clear();

		for (String name : searchResultsList) {
			if (name.indexOf("(") != -1)
				localSearchResults.add(name.substring(0, name.indexOf("(")).trim());
			else
				localSearchResults.add(name);
		}

		buildScene();
	}

	private void refreshScene() {
		// clear note billboards, cell spheres and meshes
		root.getChildren().clear();
		root.getChildren().add(xform);

		// clear note sprites and overlays
		overlayVBox.getChildren().clear();

		Iterator<Node> iter = spritesPane.getChildren().iterator();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node instanceof Text)
				iter.remove();
			else if (node instanceof VBox && node != overlayVBox)
				iter.remove();
		}

		// root.getChildren().add(orientationIndicator);
		double newrotate = computeInterpolatedValue(time.get(), keyFramesRotate, keyValuesRotate);
		indicatorRotation.setAngle(-newrotate);
		// System.out.println(time.get()+" rotation is "+newrotate);
		indicatorRotation.setAxis(new Point3D(1, 0, 0));
	}

	private void addEntitiesToScene() {
		ArrayList<Shape3D> entities = new ArrayList<Shape3D>();
		ArrayList<Node> notes = new ArrayList<Node>();

		// add spheres
		addCellGeometries(entities);

		// add scene element meshes (from notes and from scene elements list)
		addSceneElementGeometries(entities);

		Collections.sort(entities, opacityComparator);
		root.getChildren().addAll(entities);

		// add notes
		insertOverlayTitles();

		if (!currentNotes.isEmpty())
			addNoteGeometries(notes);

		// add labels
		Shape3D activeEntity = null;
		for (String name : currentLabels) {
			insertLabelFor(name, getEntityWithName(name));

			if (name.equalsIgnoreCase(selectedName.get()))
				activeEntity = getEntityWithName(name);
		}
		if (activeEntity != null)
			highlightActiveCellLabel(activeEntity);

		if (!notes.isEmpty())
			root.getChildren().addAll(notes);

		repositionSprites();
		repositionNoteBillboardFronts();
	}

	private void addSceneElementGeometries(ArrayList<Shape3D> list) {
		// add scene elements from note resources
		for (Note note : currentNoteMeshMap.keySet()) {
			list.add(currentNoteMeshMap.get(note));
		}

		// Consult rules
		if (!currentSceneElements.isEmpty()) {
			for (int i = 0; i < currentSceneElements.size(); i++) {
				SceneElement se = currentSceneElements.get(i);
				MeshView mesh = currentSceneElementMeshes.get(i);

				// in search mode
				if (inSearch) {
					if (cellBodyTicked && searchedMeshes[i])
						mesh.setMaterial(colorHash.getHighlightMaterial());
					else
						mesh.setMaterial(colorHash.getTranslucentMaterial());
				}

				else {
					// in regular view mode
					ArrayList<String> allNames = se.getAllCellNames();
					String sceneName = se.getSceneName();

					// default white meshes
					if (allNames.isEmpty()) {
						mesh.setMaterial(new PhongMaterial(Color.WHITE));
						mesh.setCullFace(CullFace.NONE);
					}

					// If mesh has with name(s), then process rules (cell or
					// shape) that apply to it
					else {
						ArrayList<Color> colors = new ArrayList<Color>();
						for (Rule rule : currentRulesList) {
							if (rule.isMulticellularStructureRule()
									&& rule.appliesToMulticellularStructure(sceneName)) {
								colors.add(rule.getColor());
							}

							else {
								for (String name : allNames) {
									if (rule.appliesToCellBody(name)) {
										colors.add(rule.getColor());
									}
								}
							}
						}
						Collections.sort(colors, colorComparator);

						// if any rules applied
						if (!colors.isEmpty()) {
							mesh.setMaterial(colorHash.getMaterial(colors));
						} else {
							mesh.setMaterial(colorHash.getOthersMaterial(othersOpacity.get()));
						}
					}
				}

				mesh.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						spritesPane.setCursor(Cursor.HAND);

						// make label appear
						String name = normalizeName(se.getSceneName());

						if (!currentLabels.contains(name.toLowerCase()))
							transientLabel(name, getEntityWithName(name));
					}
				});
				mesh.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						spritesPane.setCursor(Cursor.DEFAULT);

						// make label disappear
						removeTransientLabel();
					}
				});

				list.add(mesh);
			}
		}
	}

	private void addCellGeometries(ArrayList<Shape3D> list) {
		// Sphere stuff
		for (int i = 0; i < cellNames.length; i++) {
			double radius;
			if (!uniformSize)
				radius = SIZE_SCALE * diameters[i] / 2;
			else
				radius = SIZE_SCALE * UNIFORM_RADIUS;
			Sphere sphere = new Sphere(radius);

			Material material = new PhongMaterial();
			// if in search, do highlighting
			if (inSearch) {
				if (cellNucleusTicked && searchedCells[i])
					material = colorHash.getHighlightMaterial();
				else
					material = colorHash.getTranslucentMaterial();
			}
			// not in search mode
			else {
				ArrayList<Color> colors = new ArrayList<Color>();
				for (Rule rule : currentRulesList) {
					// just need to consult rule's active list
					if (rule.appliesToCellNucleus(cellNames[i]))
						colors.add(Color.web(rule.getColor().toString()));
				}
				Collections.sort(colors, colorComparator);
				material = colorHash.getMaterial(colors);

				if (colors.isEmpty())
					material = colorHash.getOthersMaterial(othersOpacity.get());
			}

			sphere.setMaterial(material);

			sphere.getTransforms().addAll(rotateZ, rotateY, rotateX, new Translate(positions[i][X_COR_INDEX] * X_SCALE,
					positions[i][Y_COR_INDEX] * Y_SCALE, positions[i][Z_COR_INDEX] * Z_SCALE));

			spheres[i] = sphere;

			final int index = i;
			sphere.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					spritesPane.setCursor(Cursor.HAND);

					// make label appear
					String name = cellNames[index];

					if (!currentLabels.contains(name.toLowerCase())) {
						// get cell body version of sphere, if there is one
						transientLabel(name, getEntityWithName(name));
					}
				}
			});
			sphere.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					spritesPane.setCursor(Cursor.DEFAULT);

					// make label disappear
					removeTransientLabel();
				}
			});

			list.add(sphere);
		}
		// End sphere stuff
	}

	private void removeLabelFor(String name) {
		allLabels.remove(name);
		currentLabels.remove(name);

		Node entity = getEntityWithName(name);

		if (entity != null)
			removeLabelFrom(entity);
	}

	private void removeLabelFrom(Node entity) {
		if (entity != null) {
			spritesPane.getChildren().remove(entityLabelMap.get(entity));
			entityLabelMap.remove(entity);
		}
	}

	private void insertLabelFor(String name, Node entity) {
		// if label is already in scene, make all labels white
		// and highlight that one
		Text label = entityLabelMap.get(entity);
		if (label != null) {
			for (Node shape : entityLabelMap.keySet())
				entityLabelMap.get(shape).setFill(Color.web(SPRITE_COLOR_HEX));

			label.setFill(Color.web(ACTIVE_LABEL_COLOR_HEX));
			return;
		}

		String funcName = PartsList.getFunctionalNameByLineageName(name);
		Text text;
		if (funcName != null)
			text = makeNoteSpriteText(funcName);
		else
			text = makeNoteSpriteText(name);

		final String tempName = name;
		text.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				removeLabelFor(tempName);
			}
		});

		text.setWrappingWidth(-1);

		entityLabelMap.put(entity, text);

		spritesPane.getChildren().add(text);
		alignTextWithEntity(text, entity, true);
	}

	private void highlightActiveCellLabel(Shape3D entity) {
		for (Node shape3D : entityLabelMap.keySet())
			entityLabelMap.get(shape3D).setFill(Color.web(SPRITE_COLOR_HEX));

		if (entity != null && entityLabelMap.get(entity) != null)
			entityLabelMap.get(entity).setFill(Color.web(ACTIVE_LABEL_COLOR_HEX));
	}

	/**
	 * @return The {@link Shape3D} entity with input name. Priority is given to
	 *         meshes (if a mesh and a cell have the same name, the mesh is
	 *         returned)
	 */
	private Shape3D getEntityWithName(String name) {
		// mesh view label
		for (int i = 0; i < currentSceneElements.size(); i++) {
			if (normalizeName(currentSceneElements.get(i).getSceneName()).equalsIgnoreCase(name)
					&& currentSceneElementMeshes.get(i) != null
					&& currentSceneElementMeshes.get(i).getBoundsInParent().getMinZ() > 0)
				return currentSceneElementMeshes.get(i);
		}

		// sphere label
		for (int i = 0; i < cellNames.length; i++) {
			if (spheres[i] != null && cellNames[i].equalsIgnoreCase(name)) {
				return spheres[i];
			}
		}

		return null;
	}

	// Inserts note geometries to scene
	// Input list is the list that billboards are added to which are added to
	// the subscene
	// Note overlays and sprites are added to the pane that contains the
	// subscene
	private void addNoteGeometries(ArrayList<Node> list) {
		for (Note note : currentNotes) {
			// map notes to their sphere/mesh view
			Node text = makeNoteGraphic(note);
			currentGraphicNoteMap.put(text, note);

			text.setOnMouseEntered(clickableMouseEnteredHandler);
			text.setOnMouseExited(clickableMouseExitedHandler);

			// SPRITE
			if (note.isSprite()) {
				// location attachment
				if (note.attachedToLocation()) {
					VBox box = new VBox(3);
					box.getChildren().add(text);
					// add inivisible location marker to scene at location
					// specified by note
					Sphere marker = createLocationMarker(note.getX(), note.getY(), note.getZ());
					root.getChildren().add(marker);
					entitySpriteMap.put(marker, box);
					// add vbox to sprites pane
					spritesPane.getChildren().add(box);
				}

				// cell attachment
				else if (note.attachedToCell()) {
					for (int i = 0; i < cellNames.length; i++) {
						if (cellNames[i].equalsIgnoreCase(note.getCellName()) && spheres[i] != null) {
							// if another note is already attached to the same
							// sphere,
							// create a vbox for note positioning
							if (!entitySpriteMap.containsKey(spheres[i])) {
								VBox box = new VBox(3);
								box.getChildren().add(text);
								entitySpriteMap.put(spheres[i], box);
								spritesPane.getChildren().add(box);
							} else
								entitySpriteMap.get(spheres[i]).getChildren().add(text);

							break;
						}
					}
				}

				// structure attachment
				else if (note.attachedToStructure()) {
					for (int i = 0; i < currentSceneElements.size(); i++) {
						if (currentSceneElements.get(i).getSceneName().equalsIgnoreCase(note.getCellName())) {
							MeshView mesh = currentSceneElementMeshes.get(i);
							if (!entitySpriteMap.containsKey(mesh)) {
								VBox box = new VBox(3);
								box.getChildren().add(text);
								entitySpriteMap.put(mesh, box);
								spritesPane.getChildren().add(box);
							} else
								entitySpriteMap.get(mesh).getChildren().add(text);
						}
					}
				}
			}

			// BILLBOARD_FRONT
			else if (note.isBillboardFront()) {
				// location attachment
				if (note.attachedToLocation()) {
					Sphere marker = createLocationMarker(note.getX(), note.getY(), note.getZ());
					root.getChildren().add(marker);
					billboardFrontEntityMap.put(text, marker);
				}
				// cell attachment
				else if (note.attachedToCell()) {
					for (int i = 0; i < cellNames.length; i++) {
						if (cellNames[i].equalsIgnoreCase(note.getCellName()) && spheres[i] != null) {
							billboardFrontEntityMap.put(text, spheres[i]);
						}
					}
				}
				// structure attachment
				else if (note.attachedToStructure()) {
					for (int i = 0; i < currentSceneElements.size(); i++) {
						if (currentSceneElements.get(i).getSceneName().equalsIgnoreCase(note.getCellName())) {
							billboardFrontEntityMap.put(text, currentSceneElementMeshes.get(i));
						}
					}
				}
			}

			// BILLBOARD
			else if (note.isBillboard()) {
				// location attachment
				if (note.attachedToLocation()) {
					text.getTransforms().addAll(rotateZ, rotateY, rotateX);
					text.getTransforms().addAll(new Translate(note.getX(), note.getY(), note.getZ()),
							new Scale(BILLBOARD_SCALE, BILLBOARD_SCALE));
				}
				// cell attachment
				else if (note.attachedToCell()) {
					for (int i = 0; i < cellNames.length; i++) {
						if (cellNames[i].equalsIgnoreCase(note.getCellName()) && spheres[i] != null) {
							double offset = 5;
							if (!uniformSize)
								offset = spheres[i].getRadius() + 2;

							text.getTransforms().addAll(spheres[i].getTransforms());
							text.getTransforms().addAll(new Translate(offset, offset),
									new Scale(BILLBOARD_SCALE, BILLBOARD_SCALE));
						}
					}
				}
				// structure attachment
				else if (note.attachedToStructure()) {
					for (int i = 0; i < currentSceneElements.size(); i++) {
						if (currentSceneElements.get(i).getSceneName().equalsIgnoreCase(note.getCellName())) {
							text.getTransforms().addAll(currentSceneElementMeshes.get(i).getTransforms());
							double offset = 5;
							text.getTransforms().addAll(new Translate(offset, offset),
									new Scale(BILLBOARD_SCALE, BILLBOARD_SCALE));
						}
					}
				}
			}

			// add graphic to appropriate place (scene, overlay box, or on top
			// of scene)
			Display display = note.getTagDisplay();
			if (display != null) {
				switch (display) {
				case SPRITE:
					break;

				case BILLBOARD_FRONT: // fall to billboard case

				case BILLBOARD:
					list.add(text);
					break;

				case OVERLAY: // fall to default case

				case BLANK: // fall to default case

				default:
					overlayVBox.getChildren().add(text);
					break;
				}
			}
		}
	}

	private void insertOverlayTitles() {
		if (storiesLayer != null) {
			Text infoPaneTitle = makeNoteOverlayText("Story Title:");

			if (storiesLayer.getActiveStory() != null) {
				Text storyTitle = makeNoteOverlayText(storiesLayer.getActiveStory().getName());
				overlayVBox.getChildren().addAll(infoPaneTitle, storyTitle);
			} else {
				Text noStoryTitle = makeNoteOverlayText("none");
				overlayVBox.getChildren().addAll(infoPaneTitle, noStoryTitle);
			}
		}
	}

	private Text makeNoteOverlayText(String title) {
		Text text = new Text(title);
		text.setFill(Color.web(SPRITE_COLOR_HEX));
		text.setFontSmoothingType(FontSmoothingType.LCD);
		text.setWrappingWidth(overlayVBox.getWidth());
		text.setFont(AppFont.getSpriteAndOverlayFont());
		return text;
	}

	private Text makeNoteSpriteText(String title) {
		Text text = makeNoteOverlayText(title);
		text.setWrappingWidth(160);
		return text;
	}

	private Text makeNoteBillboardText(String title) {
		Text text = new Text(title);
		text.setWrappingWidth(90);
		text.setFont(AppFont.getBillboardFont());
		text.setSmooth(false);
		text.setStrokeWidth(2);
		text.setFontSmoothingType(FontSmoothingType.LCD);
		text.setCacheHint(CacheHint.QUALITY);
		text.setFill(Color.web(SPRITE_COLOR_HEX));
		return text;
	}

	private Sphere createLocationMarker(double x, double y, double z) {
		Sphere sphere = new Sphere(1);
		sphere.getTransforms().addAll(rotateZ, rotateY, rotateX, new Translate(x * X_SCALE, y * Y_SCALE, z * Z_SCALE));
		// make marker transparent
		sphere.setMaterial(colorHash.getOthersMaterial(0));
		return sphere;
	}

	// Makes an anchor pane that contains the text to be shown
	// if isOverlay is true, then the text is larger
	private Text makeNoteGraphic(Note note) {
		String title = note.getTagName();
		if (note.isExpandedInScene())
			title += ": " + note.getTagContents();
		else
			title += "\n[more...]";

		Text node = null;
		if (note.getTagDisplay() != null) {
			switch (note.getTagDisplay()) {
			case SPRITE:
				node = makeNoteSpriteText(title);
				break;

			case BILLBOARD:
				node = makeNoteBillboardText(title);
				break;

			case BILLBOARD_FRONT:
				node = makeNoteBillboardText(title);
				break;

			case OVERLAY: // fall to default case

			case BLANK: // fall to default case

			default:
				node = makeNoteOverlayText(title);
				break;

			}
		}
		return node;
	}

	private void buildCamera() {
		this.camera = new PerspectiveCamera(true);
		this.xform = new Xform();
		xform.reset();

		root.getChildren().add(xform);
		xform.getChildren().add(camera);

		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);

		xform.setScaleX(X_SCALE);
		xform.setScaleY(Y_SCALE);

		setNewOrigin();

		subscene.setCamera(camera);
	}

	private void setNewOrigin() {
		// Find average X Y positions of initial timepoint
		Integer[][] positions = cellData.getPositions(startTime);
		int numCells = positions.length;
		int sumX = 0;
		int sumY = 0;
		int sumZ = 0;
		for (int i = 0; i < numCells; i++) {
			sumX += positions[i][X_COR_INDEX];
			sumY += positions[i][Y_COR_INDEX];
			sumZ += positions[i][Z_COR_INDEX];
		}
		newOriginX = (int) Math.round(X_SCALE * sumX / numCells);
		newOriginY = (int) Math.round(Y_SCALE * sumY / numCells);
		newOriginZ = (int) Math.round(Z_SCALE * sumZ / numCells);

		// Set new origin to average X Y positions
		xform.setTranslate(newOriginX, newOriginY, newOriginZ);
		System.out.println("origin xyz: " + newOriginX + " " + newOriginY + " " + newOriginZ);
	}

	public void setSearchResultsList(ObservableList<String> list) {
		searchResultsList = list;
	}

	public void setSearchResultsUpdateService(Service<Void> service) {
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				updateLocalSearchResults();
			}
		});
	}

	public void setGeneResultsUpdated(BooleanProperty updated) {
		geneResultsUpdated = updated;
		geneResultsUpdated.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				updateLocalSearchResults();
			}
		});
	}

	public void consultSearchResultsList() {
		searchedCells = new boolean[cellNames.length];
		searchedMeshes = new boolean[meshNames.length];

		// look for searched cells
		for (int i = 0; i < cellNames.length; i++) {
			if (localSearchResults.contains(cellNames[i]))
				searchedCells[i] = true;
			else
				searchedCells[i] = false;
		}

		// look for single celled meshes
		for (int i = 0; i < meshNames.length; i++) {
			if (sceneElementsAtTime.get(i).isMulticellular()) {
				searchedMeshes[i] = true;
				for (String name : sceneElementsAtTime.get(i).getAllCellNames()) {
					if (localSearchResults.contains(name))
						searchedMeshes[i] &= true;
					else
						searchedMeshes[i] &= false;
				}
			} else {
				if (localSearchResults.contains(meshNames[i]))
					searchedMeshes[i] = true;
				else
					searchedMeshes[i] = false;
			}
		}
	}

	public boolean currentRulesApplyTo(String name) {
		// get the scene name associated with the cell
		String sceneName = "";
		ArrayList<String> cells = new ArrayList<String>();
		for (int i = 0; i < sceneElementsList.elementsList.size(); i++) {
			SceneElement currSE = sceneElementsList.elementsList.get(i);

			// check if multicellular structure --> find match with name in
			// cells
			if (currSE.isMulticellular()) {
				if (currSE.getSceneName().toLowerCase().equals(name.toLowerCase())) {
					sceneName = name;
					cells = currSE.getAllCellNames(); // save the cells in case
														// there isn't an
														// explicit structure
														// rule but the
														// structure is still
														// colored
				}
			} else {
				String sn = sceneElementsList.elementsList.get(i).getSceneName();

				StringTokenizer st = new StringTokenizer(sn);
				if (st.countTokens() == 2) {
					String sceneNameLineage = st.nextToken();
					if (sceneNameLineage.toLowerCase().equals(name.toLowerCase())) {
						sceneName = sn;
						break;
					}
				}
			}
		}

		if (sceneName.equals(""))
			sceneName = name;

		for (Rule rule : currentRulesList) {
			if (rule.isMulticellularStructureRule() && rule.appliesToMulticellularStructure(sceneName))
				return true;
			else if (rule.appliesToCellBody(name))
				return true;
			else if (rule.appliesToCellNucleus(name))
				return true;
			else { // check if cells corresponding to multicellular structure
						// have rule - in the case of a non explicit
						// multicellular rule but a structure that's colored
				if (cells.size() > 0) {
					for (String cell : cells) {
						if (rule.appliesToCellBody(cell)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean captureImagesForMovie() {

		movieFiles.clear();
		count = -1;

		Stage fileChooserStage = new Stage();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Save Location");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("MOV File", "*.mov"));

		File fakeFile = fileChooser.showSaveDialog(fileChooserStage);

		if (fakeFile == null) {
			System.out.println("null file");
			return false;
		}

		// save the name from the file chooser for later MOV file
		movieName = fakeFile.getName();
		moviePath = fakeFile.getAbsolutePath();

		// make a temp directory for the frames at the given save location
		String path = fakeFile.getAbsolutePath();
		if (path.lastIndexOf("/") < 0) {
			path = path.substring(0, path.lastIndexOf("\\") + 1) + "tempFrameDir";
		} else {
			path = path.substring(0, path.lastIndexOf("/") + 1) + "tempFrameDir";
		}

		frameDir = new File(path);

		try {
			frameDir.mkdir();
		} catch (SecurityException se) {
			return false;
		}

		String frameDirPath = frameDir.getAbsolutePath() + "/";

		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (captureVideo != null) {
					if (captureVideo.get()) {

						WritableImage screenCapture = subscene.snapshot(new SnapshotParameters(), null);

						try {
							File file = new File(frameDirPath + "movieFrame" + count++ + ".JPEG");

							if (file != null) {
								RenderedImage renderedImage = SwingFXUtils.fromFXImage(screenCapture, null);
								ImageIO.write(renderedImage, "JPEG", file);
								movieFiles.addElement(file);
							}
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				}
			}
		});

		return true;
	}

	public void convertImagesToMovie() {

		// make our files into JavaPicture
		javaPictures.clear();

		for (File movieFile : movieFiles) {
			JavaPicture jp = new JavaPicture();

			jp.loadImage(movieFile);

			javaPictures.addElement(jp);
		}

		if (javaPictures.size() > 0) {
			new JpegImagesToMovie((int) subscene.getWidth(), (int) subscene.getHeight(), 3, movieName, javaPictures);

			// move the movie to the originally specified location
			File movJustMade = new File(movieName);
			movJustMade.renameTo(new File(moviePath));

			// remove the .movtemp.jpg file
			File movtempjpg = new File(".movtemp.jpg");
			if (movtempjpg != null) {
				movtempjpg.delete();
			}
		}

		// remove all of the images in the frame directory
		if (frameDir != null && frameDir.isDirectory()) {
			File[] frames = frameDir.listFiles();
			for (File frame : frames) {
				frame.delete();
			}

			frameDir.delete();
		}

	}

	/*
	 * When called, a snapshot of the screen is saved
	 */
	public void stillscreenCapture() {
		Stage fileChooserStage = new Stage();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Save Location");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG File", "*.png"));

		WritableImage screenCapture = subscene.snapshot(new SnapshotParameters(), null);

		/*
		 * write the image to a file
		 */
		try {
			File file = fileChooser.showSaveDialog(fileChooserStage);

			if (file != null) {
				RenderedImage renderedImage = SwingFXUtils.fromFXImage(screenCapture, null);
				ImageIO.write(renderedImage, "png", file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printCellNames() {
		for (int i = 0; i < cellNames.length; i++)
			System.out.println(cellNames[i] + CS + spheres[i]);
	}

	public void printMeshNames() {
		for (int i = 0; i < meshNames.length; i++)
			System.out.println(meshNames[i] + CS + meshes[i]);
	}

	// sets everything associated with color rules
	public void setRulesList(ObservableList<Rule> list) {
		if (list == null)
			return;

		currentRulesList = list;
		currentRulesList.addListener(new ListChangeListener<Rule>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Rule> change) {
				while (change.next()) {
					if (change.getAddedSize() > 0) {
						buildScene();

						for (Rule rule : change.getAddedSubList()) {
							rule.getRuleChangedProperty().addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
										Boolean newValue) {
									if (newValue)
										buildScene();
								}
							});
						}
					}

					if (!change.getRemoved().isEmpty()) {
						buildScene();
					}
				}
			}
		});
	}

	/**
	 * Sets transparent anchor pane overlay for sprite notes display
	 * 
	 * @param parentPane
	 *            The {@link AnchorPane} in which labels and sprites reside
	 */
	public void setNotesPane(AnchorPane parentPane) {
		if (parentPane != null) {
			spritesPane = parentPane;

			overlayVBox = new VBox(5);
			overlayVBox.setPrefWidth(170);
			overlayVBox.setMaxWidth(overlayVBox.getPrefWidth());
			overlayVBox.setMinWidth(overlayVBox.getPrefWidth());

			AnchorPane.setTopAnchor(overlayVBox, 5.0);
			AnchorPane.setRightAnchor(overlayVBox, 5.0);

			spritesPane.getChildren().add(overlayVBox);
		}
	}

	// Hides cell name label/context menu
	private void hideContextPopups() {
		if (contextMenuStage != null)
			contextMenuStage.hide();
	}

	public ArrayList<Rule> getColorRulesList() {
		ArrayList<Rule> list = new ArrayList<Rule>();
		for (Rule rule : currentRulesList)
			list.add(rule);

		return list;
	}

	public ObservableList<Rule> getObservableColorRulesList() {
		return currentRulesList;
	}

	public void setColorRulesList(ArrayList<Rule> list) {
		currentRulesList.clear();
		currentRulesList.setAll(list);
	}

	public int getTime() {
		return time.get();
	}

	public void setTime(int t) {
		if (startTime <= t && t <= endTime) {
			hideContextPopups();
			time.set(t);
		}

		else if (t < startTime)
			time.set(startTime);

		else if (t > endTime)
			time.set(endTime);
	}

	public void setRotations(double rx, double ry, double rz) {
		/*
		 * rx = Math.toDegrees(rx); ry = Math.toDegrees(ry); rx =
		 * Math.toDegrees(rz);
		 */

		// rotateX.setAngle(rx+180);
		rotateX.setAngle(rx);
		rotateY.setAngle(ry);
		rotateZ.setAngle(rz);
	}

	public void setCaptureVideo(BooleanProperty captureVideo) {
		this.captureVideo = captureVideo;
	}

	public double getRotationX() {
		return rotateX.getAngle();
	}

	public double getRotationY() {
		return rotateY.getAngle();
	}

	public double getRotationZ() {
		return rotateZ.getAngle();
	}

	public double getTranslationX() {
		return xform.t.getTx() - newOriginX;
	}

	public void setTranslationX(double tx) {
		double newTx = tx + newOriginX;
		if (newTx > 0 && newTx < 450)
			xform.t.setX(newTx);
	}

	public double getTranslationY() {
		return xform.t.getTy() - newOriginY;
	}

	public void setTranslationY(double ty) {
		double newTy = ty + newOriginY;
		if (newTy > 0 && newTy < 450)
			xform.t.setY(newTy);
	}

	/**
	 * Used for internal URL generation of rules associated with stories
	 * 
	 * @return The value of the zoom {@link DoubleProperty}
	 */
	public double getScaleInternal() {
		return zoom.get();
	}

	/**
	 * Used for internal URL generation of rules associated with stories. Sets
	 * the value of the zoom {@link DoubleProperty}
	 */
	public void setScaleInternal(double scale) {
		zoom.set(scale);
	}

	public double getScale() {
		double scale = zoom.get() - 0.5;
		scale = 1 - (scale / 6.5);
		return scale;
	}

	public void setScale(double scale) {
		if (scale > 1)
			scale = 1;
		scale = 6.5 * (1 - scale);
		// smaller zoom value means larger picture
		zoom.set((scale + 0.5));
	}

	public double getOthersVisibility() {
		return othersOpacity.get();
	}

	public void setOthersVisibility(double dim) {
		othersOpacity.set(dim);
	}

	private EventHandler<TransformChangedEvent> getRotateXChangeHandler() {
		return new EventHandler<TransformChangedEvent>() {
			@Override
			public void handle(TransformChangedEvent arg0) {
				rotateXAngle.set(rotateX.getAngle());
				repositionSprites();
				repositionNoteBillboardFronts();
			}
		};
	}

	private EventHandler<TransformChangedEvent> getRotateYChangeHandler() {
		return new EventHandler<TransformChangedEvent>() {
			@Override
			public void handle(TransformChangedEvent arg0) {
				rotateYAngle.set(rotateY.getAngle());
				repositionSprites();
				repositionNoteBillboardFronts();
			}
		};
	}

	private EventHandler<TransformChangedEvent> getRotateZChangeHandler() {
		return new EventHandler<TransformChangedEvent>() {
			@Override
			public void handle(TransformChangedEvent arg0) {
				rotateZAngle.set(rotateZ.getAngle());
				repositionSprites();
				repositionNoteBillboardFronts();
			}
		};
	}

	private ChangeListener<Number> getRotateXAngleListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				rotateX.setAngle(rotateXAngle.get());
			}
		};
	}

	private ChangeListener<Number> getRotateYAngleListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				rotateY.setAngle(rotateYAngle.get());
			}
		};
	}

	private ChangeListener<Number> getRotateZAngleListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				rotateZ.setAngle(rotateZAngle.get());
			}
		};
	}

	public DoubleProperty getRotateXAngleProperty() {
		return this.rotateXAngle;
	}

	public DoubleProperty getRotateYAngleProperty() {
		return this.rotateYAngle;
	}

	public DoubleProperty getRotateZAngleProperty() {
		return this.rotateZAngle;
	}

	public SubScene getSubScene() {
		return subscene;
	}

	public Group getRoot() {
		return root;
	}

	public ChangeListener<Number> getOthersOpacityListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				othersOpacity.set(Math.round(newValue.doubleValue()) / 100d);

				buildScene();
			}
		};
	}

	public void addListenerToOpacitySlider(Slider slider) {
		othersOpacity.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				Double arg = arg0.getValue().doubleValue();
				if (arg >= 0 && arg <= 1.0) {
					slider.setValue(arg * 100.0);
				}
			}
		});
	}

	public ChangeListener<String> getSearchFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.isEmpty()) {
					inSearch = false;
					buildScene();
				} else
					inSearch = true;
			}
		};
	}

	public void setSearchField(TextField field) {
		searchField = field;
		searchField.textProperty().addListener(getSearchFieldListener());
	}

	public int getEndTime() {
		return endTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public EventHandler<ActionEvent> getZoomInButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hideContextPopups();

				double z = zoom.get();
				z -= 0.25;
				if (z < 0.25)
					z = 0.25;
				else if (z > 5)
					z = 5;

				zoom.set(z);
			}
		};
	}

	public EventHandler<ActionEvent> getZoomOutButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hideContextPopups();

				double z = zoom.get();
				z += 0.25;
				if (z < 0.25)
					z = 0.25;
				else if (z > 5)
					z = 5;

				zoom.set(z);
			}
		};
	}

	public EventHandler<ActionEvent> getBackwardButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hideContextPopups();
				if (!playingMovie.get())
					setTime(time.get() - 1);
			}
		};
	}

	public EventHandler<ActionEvent> getForwardButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hideContextPopups();
				if (!playingMovie.get())
					setTime(time.get() + 1);
			}
		};
	}

	public EventHandler<ActionEvent> getUpdate3DListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

			}
		};
	}

	/**
	 * This method returns the {@link ChangeListener} that listens for the
	 * {@link BooleanProperty} that changes when 'uniform nucleus' is
	 * ticked/unticked in the display tab. On change, the scene refreshes and
	 * cell bodies are highlighted/unhighlighted accordingly.
	 * 
	 * @return The listener.
	 */
	public ChangeListener<Boolean> getUniformSizeCheckBoxListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				uniformSize = newValue.booleanValue();
				buildScene();
			}
		};
	}

	/**
	 * This JavaFX {@link Service} of type Void spools a thread that<br>
	 * 1) retrieves the data for cells, cell bodies, and multicellular
	 * structures for the current time<br>
	 * 2) clears the notes, labels, and entities in the subscene<br>
	 * 3) adds the current notes, labels, and entities to the subscene
	 */
	private final class RenderService extends Service<Void> {
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							getSceneData();
							refreshScene();
							addEntitiesToScene();
						}
					});
					return null;
				}
			};
		}
	}

	/**
	 * This JavaFX {@link Service} of type Void spools a thread to play the
	 * subscene movie. It waits the time in milliseconds defined in the variable
	 * WAIT_TIME_MILLI (defined in the parent class) before rendering the next
	 * time frame.
	 */
	private final class PlayService extends Service<Void> {
		@Override
		protected final Task<Void> createTask() {
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
								setTime(time.get() + 1);
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

	/**
	 * This class is the {@link ChangeListener} that listens changes in the
	 * height or width of the modelAnchorPane in which the subscene lives. When
	 * the size changes, front-facing billboards and sprites (notes and labels)
	 * are repositioned to align with their appropriate positions (whether it is
	 * a location to an entity).
	 */
	private final class SubsceneSizeListener implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			repositionSprites();
			repositionNoteBillboardFronts();
		}
	}

	/**
	 * This method returns the {@link ChangeListener} that listens for the
	 * {@link BooleanProperty} that changes when 'cell nucleus' is
	 * ticked/unticked in the search tab. On change, the scene refreshes and
	 * cell bodies are highlighted/unhighlighted accordingly.
	 * 
	 * @return The listener.
	 */
	public ChangeListener<Boolean> getCellNucleusTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				cellNucleusTicked = newValue;
				buildScene();
			}
		};
	}

	/**
	 * This method returns the {@link ChangeListener} that listens for the
	 * {@link BooleanProperty} that changes when 'cell body' is ticked/unticked
	 * in the search tab. On change, the scene refreshes and cell bodies are
	 * highlighted/unhighlighted accordingly.
	 * 
	 * @return The listener.
	 */
	public ChangeListener<Boolean> getCellBodyTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				cellBodyTicked = newValue;
				buildScene();
			}
		};
	}

	/**
	 * This class is the Comparator for Shape3Ds that compares based on opacity.
	 * This is used for z-buffering for semi-opaque materials. Entities with
	 * opaque materials should be rendered last (added first to the root
	 * {@link Group}.
	 * 
	 * @return The Shape3D comparator.
	 */
	private class OpacityComparator implements Comparator<Shape3D> {
		@Override
		public int compare(Shape3D o1, Shape3D o2) {
			double op1 = colorHash.getMaterialOpacity(o1.getMaterial());
			double op2 = colorHash.getMaterialOpacity(o2.getMaterial());
			if (op1 < op2)
				return 1;
			else if (op1 > op2)
				return -1;
			else
				return 0;
		}
	}

	public ChangeListener<Boolean> getMulticellModeListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			}
		};
	}

	/**
	 * The getter for the {@link EventHandler} for the {@link MouseEvent} that
	 * is fired upon clicking on a note. The handler expands the note on click.
	 * 
	 * @return The event handler.
	 */
	public EventHandler<MouseEvent> getNoteClickHandler() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isStillSincePress()) {
					Node result = event.getPickResult().getIntersectedNode();
					if (result instanceof Text) {
						Text picked = (Text) result;
						Note note = currentGraphicNoteMap.get(picked);
						if (note != null) {
							note.setExpandedInScene(!note.isExpandedInScene());
							if (note.isExpandedInScene())
								picked.setText(note.getTagName() + ": " + note.getTagContents());
							else
								picked.setText(note.getTagName() + "\n[more...]");
						}
					}
				}
			}
		};
	}

	public Stage getStage() {
		return this.parentStage;
	}

	private final static double cannonicalOrientationX = 145.;
	private final static double cannonicalOrientationY = -170.;
	private final static double cannonicalOrientationZ = 25.;

	private final String CS = ", ";

	private final String FILL_COLOR_HEX = "#272727";

	private final String ACTIVE_LABEL_COLOR_HEX = "#ffff66", SPRITE_COLOR_HEX = "#ffffff",
			TRANSIENT_LABEL_COLOR_HEX = "#f0f0f0";

	/**
	 * The wait time (in milliseconds) between consecutive time frames while a
	 * movie is playing.
	 */
	private final long WAIT_TIME_MILLI = 200;

	private final double CAMERA_INITIAL_DISTANCE = -220;

	private final double CAMERA_NEAR_CLIP = 1, CAMERA_FAR_CLIP = 2000;

	private final int X_COR_INDEX = 0, Y_COR_INDEX = 1, Z_COR_INDEX = 2;

	/**
	 * The scale of the subscene z-coordinate axis so that the embryo does not
	 * appear flat and squished.
	 */
	private final double Z_SCALE = 5;
	/** The scale of the subscene x-coordinate axis. */
	private final double X_SCALE = 1;
	/** The scale of the subscene y-coordinate axis. */
	private final double Y_SCALE = 1;
	/** Text size scale used for the rendering of billboard notes. */
	private final double BILLBOARD_SCALE = 0.9;

	/**
	 * Scale used for the radii of spheres that represent cells, multiplied with
	 * the cell's radius loaded from the nuc files.
	 */
	private final double SIZE_SCALE = 1;
	/** The radius of all spheres when 'uniform size' is ticked. */
	private final double UNIFORM_RADIUS = 4;
	/**
	 * The default camera zoom of the embryo. On program startup, the embryo is
	 * zoomed in so that the entire embryo is not visible.
	 */
	private final double INITIAL_ZOOM = 1.5;

}