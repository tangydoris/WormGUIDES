package wormguides;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import wormguides.model.ColorHash;
import wormguides.model.ColorRule;
import wormguides.model.LineageData;
import wormguides.model.MulticellularStructureRule;
import wormguides.model.Note;
import wormguides.model.Note.Display;
import wormguides.model.Note.Type;
import wormguides.model.Rule;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;

public class Window3DController {

	private LineageData data;

	private SubScene subscene;
	private VBox overlayVBox;
	private Pane spritesPane;
	
	// maps of note attached to cell, or cell and time
	private HashMap<Node, Sphere> spriteSphereMap;
	private HashMap<Node, Sphere> billboardFrontSphereMap;
	
	// transformation stuff
	private Group root;
	private PerspectiveCamera camera;
	private Xform xform;
	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	private double mouseDeltaX, mouseDeltaY;
	private int newOriginX, newOriginY, newOriginZ;

	// housekeeping stuff
	private IntegerProperty time;
	private IntegerProperty totalNuclei;
	private int endTime;
	private Sphere[] spheres;
	private MeshView[] meshes;
	private String[] cellNames;
	private String[] meshNames;
	private boolean[] searchedCells;
	private boolean[] searchedMeshes;
	private Integer[][] positions;
	private Integer[] diameters;
	private DoubleProperty zoom;
	private double zScale;

	// switching timepoints stuff
	private BooleanProperty playingMovie;
	private PlayService playService;
	private RenderService renderService;

	// subscene click cell selection stuff
	private IntegerProperty selectedIndex;
	private StringProperty selectedName;
	private BooleanProperty cellClicked;

	// searched highlighting stuff
	private boolean inSearch;
	private ObservableList<String> searchResultsList;
	private ArrayList<String> localSearchResults;

	// color rules stuff
	private ColorHash colorHash;
	private ObservableList<Rule> rulesList;
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
	
	// Uniform nuclei size
	private boolean uniformSize;
	
	// connectome - synapse type checkboxes
	private boolean presynapticTicked;
	private boolean postsynapticTicked;
	private boolean electricalTicked;
	private boolean neuromuscularTicked;
	
	// Cell body and cell nucleus highlighting in search mode
	private boolean cellNucleusTicked;
	private boolean cellBodyTicked;
	private boolean multicellMode;
	
	// Story elements stuff
	private StoriesLayer storiesLayer;
	// currentNotes contains all notes that are 'active' within a scene
	// (any note that should be visible in a given frame)
	private ArrayList<Note> currentNotes;
	//private ArrayList<MeshView> currentNoteMeshes;
	private HashMap<Note, MeshView> currentNoteMeshMap;
	
	private SubsceneSizeListener subsceneSizeListener;

	
	public Window3DController(Pane parentPane, LineageData data) {
		root = new Group();
		this.data = data;

		time = new SimpleIntegerProperty(START_TIME);
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue) {
				buildScene(time.get());
			}
		});

		zoom = new SimpleDoubleProperty(1.0);
		zScale = Z_SCALE;

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
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				int selected = getIndexByCellName(newValue);
				if (selected != -1)
					selectedIndex.set(selected);
			}
		});
		
		cellClicked = new SimpleBooleanProperty(false);

		inSearch = false;

		totalNuclei = new SimpleIntegerProperty();
		totalNuclei.set(0);

		endTime = data.getTotalTimePoints();

		createSubScene(parentPane.widthProperty().get(), parentPane.heightProperty().get());
		parentPane.getChildren().add(subscene);
		
		subsceneSizeListener = new SubsceneSizeListener();
		parentPane.widthProperty().addListener(subsceneSizeListener);
		parentPane.heightProperty().addListener(subsceneSizeListener);

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
				if (newValue)
					playService.restart();
				else
					playService.cancel();
			}
		});

		renderService = new RenderService();

		zoom.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				xform.setScale(zoom.get());
				repositionNoteSprites();
				repositionNoteBillboardFronts();
			}
		});

		localSearchResults = new ArrayList<String>();

		geneResultsUpdated = new SimpleBooleanProperty();

		othersOpacity = new SimpleDoubleProperty(1);
		otherCells = new ArrayList<String>();

		rotateX = new Rotate(0, 0, newOriginY, newOriginZ, Rotate.X_AXIS);
		rotateY = new Rotate(0, newOriginX, 0, newOriginZ, Rotate.Y_AXIS);
		rotateZ = new Rotate(0, newOriginX, newOriginY, 0, Rotate.Z_AXIS);
		
		uniformSize = false;
		
		rulesList = FXCollections.observableArrayList();
		
		colorHash = new ColorHash();
		colorComparator = new ColorComparator();
		opacityComparator = new OpacityComparator();
		
		currentSceneElementMeshes = new ArrayList<MeshView>();
		currentSceneElements = new ArrayList<SceneElement>();
		
		currentNotes = new ArrayList<Note>();
		currentNoteMeshMap = new HashMap<Note, MeshView>();
		spriteSphereMap = new HashMap<Node, Sphere>();
		billboardFrontSphereMap = new HashMap<Node, Sphere>();
		
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
	}
	

	// Called by RootLayoutController to set the loaded SceneElementsList
	// after the list is set, the SceneElements are loaded
	public void setSceneElementsList(SceneElementsList list) {
		if (list!=null) {
			sceneElementsList = list;
			sceneElementsList.toString();
		}
	}
	
	
	public void setStoriesLayer(StoriesLayer layer) {
		if (layer!=null) {
			storiesLayer = layer;
			buildScene(time.get());
		}
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
		
		if (type==MouseEvent.MOUSE_ENTERED_TARGET || type==MouseEvent.MOUSE_ENTERED
				|| type==MouseEvent.MOUSE_RELEASED)
			handleMouseReleasedOrEntered();
		
		else if (type==MouseEvent.MOUSE_MOVED)
			handleMouseReleasedOrEntered();
		
		else if (type==MouseEvent.MOUSE_CLICKED)
			handleMouseClicked(me);
		
		else if (type==MouseEvent.MOUSE_DRAGGED)
			handleMouseDragged(me);
		
		else if (type==MouseEvent.MOUSE_PRESSED)
			handleMousePressed(me);
	}
	
	
	private void handleMouseDragged(MouseEvent event) {
		if (spritesPane!=null)
			spritesPane.setCursor(Cursor.CLOSED_HAND);

		mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);
        mouseDeltaX /= 4;
        mouseDeltaY /= 4;

		if (event.isPrimaryButtonDown()) {
			mouseDeltaX /= 2;
            mouseDeltaY /= 2;
            
            rotateX.setAngle((rotateX.getAngle()+mouseDeltaY)%360);
    		rotateY.setAngle((rotateY.getAngle()-mouseDeltaX)%360);
    		
    		repositionNoteSprites();
    		repositionNoteBillboardFronts();
		}

		else if (event.isSecondaryButtonDown()) {
			double tx = xform.t.getTx()-mouseDeltaX;
			double ty = xform.t.getTy()-mouseDeltaY;

			if (tx>0 && tx<450)
				xform.t.setX(tx);
			if (ty>0 && ty<450)
				xform.t.setY(ty);
			
			repositionNoteSprites();
			repositionNoteBillboardFronts();
		}
	}
	
	
	private void handleMouseReleasedOrEntered() {
		if (spritesPane!=null)
			spritesPane.setCursor(Cursor.HAND);
	}
	
	
	private void handleMouseClicked(MouseEvent event) {
		Node node = event.getPickResult().getIntersectedNode();
		
		// Billboard
		if (node instanceof Text) {
			// TODO
			System.out.println("billboard clicked");
		}
		
		// Nucleus
		if (node instanceof Sphere) {
			selectedIndex.set(getPickedSphereIndex((Sphere)node));
			selectedName.set(cellNames[selectedIndex.get()]);
			cellClicked.set(true);
		}
		else if (node instanceof MeshView) {
			// Cell body/structure
			for (int i = 0; i < currentSceneElementMeshes.size(); i++) {
				MeshView curr = currentSceneElementMeshes.get(i);
				if (curr.equals(node)) {
					SceneElement clickedSceneElement = currentSceneElements.get(i);
					selectedName.set(clickedSceneElement.getSceneName());
				}
			}
			
			// Note structure
			if (selectedName.get().isEmpty()) {
				for (Note note : currentNoteMeshMap.keySet()) {
					if (currentNoteMeshMap.get(note).equals(node))
						selectedName.set(note.getTagName());
				}
			}
		}
		else {
			selectedIndex.set(-1);
			selectedName.set("");
		}
	}
	
	
	private void handleMousePressed(MouseEvent event) {
		mousePosX = event.getSceneX();
		mousePosY = event.getSceneY();
	}

	
	private void createSubScene(Double width, Double height) {
		subscene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
		subscene.setFill(Color.web(FILL_COLOR_HEX));
		buildCamera();
	}
	
	
	private void repositionNoteBillboardFronts() {
		for (Node node : billboardFrontSphereMap.keySet()) {
			Sphere s = billboardFrontSphereMap.get(node);
			if (s!=null) {
				Bounds b = s.getBoundsInParent();
				
				if (b!=null) {
					node.getTransforms().clear();
					double radius = s.getRadius();
					double x = b.getMaxX()-radius;
					double y = b.getMaxY()-radius;
					double z = b.getMaxZ()-radius;
					node.getTransforms().add(new Translate(x, y, z));
				}
			}
		}
	}
	
	
	// Reposition sprites by projecting the sphere's 3d coordinate 
	// onto the front of the subscene
	private void repositionNoteSprites() {
		for (Node node : spriteSphereMap.keySet()) {
			Sphere s = spriteSphereMap.get(node);
			if (s!=null) {
				Bounds b = s.getBoundsInParent();
				
				if (b!=null) {
					node.getTransforms().clear();
					Point2D p = CameraHelper.project(camera, 
							new Point3D(b.getMaxX(), b.getMaxY(), b.getMaxZ()));
					
					double radius = s.getRadius();
					double x = p.getX()-radius;
					double y = p.getY()-radius;
					node.getTransforms().add(new Translate(x, y));
				}
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
	

	// Builds subscene for a given timepoint
	private void buildScene(int time) {
		// Frame is indexed 1 less than the time requested
		time--;

		cellNames = data.getNames(time);
		
		if (sceneElementsList!=null)
			meshNames = sceneElementsList.getSceneElementNamesAtTime(time);
		
		positions = data.getPositions(time);
		diameters = data.getDiameters(time);
		totalNuclei.set(cellNames.length);
		spheres = new Sphere[cellNames.length];
		meshes = new MeshView[meshNames.length];
		
		// Start scene element list, find scene elements present at time, build and meshes
		//empty meshes and scene element references from last rendering. Same for story elements
		if (!currentSceneElementMeshes.isEmpty()) {
			currentSceneElementMeshes.clear();
			currentSceneElements.clear();
		}
		
		if (sceneElementsList != null) {
			sceneElementsAtTime = sceneElementsList.getSceneElementsAtTime(time);
			for (int i = 0; i < sceneElementsAtTime.size(); i++) {
				//add meshes from each scene element
				SceneElement se = sceneElementsAtTime.get(i);
				MeshView mesh = se.buildGeometry(time);
				
				if (mesh != null) {
					//null mesh when file not found thrown
					mesh.getTransforms().addAll(rotateZ, rotateY, rotateX);
					
					//add rendered mesh to meshes list
					currentSceneElementMeshes.add(mesh);
					
					//add scene element to rendered scene element reference for on click responsiveness
					currentSceneElements.add(se);
				}
			}	
		}
		// End scene element mesh loading/building
		
		
		// Begin story stuff
		if (storiesLayer!=null) {
			spriteSphereMap.clear();
			
			currentNotes.clear();
			//currentNoteMeshes.clear();
			currentNoteMeshMap.clear();
			
			currentNotes = storiesLayer.getActiveNotes(time);
			for (Note note : storiesLayer.getNotesWithCell()) {
				for (String name : cellNames) {
					if (!currentNotes.contains(note) 
							&& name.equalsIgnoreCase(note.getCellName())) {
						currentNotes.add(note);
						break;
					}
				}
			}
			
			for (Note note : currentNotes) {
				if (note.hasSceneElements()) {
					for (SceneElement se : note.getSceneElements()) {
						MeshView mesh = se.buildGeometry(time);
						mesh.setMaterial(colorHash.getNoteSceneElementMaterial());
						mesh.getTransforms().addAll(rotateX, rotateY, rotateZ);
						mesh.getTransforms().addAll(new Translate(
							newOriginX+se.getX(),
							newOriginY+se.getY(),
							newOriginZ+se.getZ()),
							new Scale(150, -150, -150));
						//currentNoteMeshes.add(mesh);	
						currentNoteMeshMap.put(note, mesh);
					}
				}
			}
		}
		// End story stuff

		if (localSearchResults.isEmpty()) {
			searchedCells = new boolean[cellNames.length];
			searchedMeshes = new boolean[meshNames.length];
		}
		else
			consultSearchResultsList();

		renderService.restart();
	}

	
	private void updateLocalSearchResults() {
		if (searchResultsList==null)
			return;

		localSearchResults.clear();

		for (String name : searchResultsList) {
			if (name.indexOf("(")!=-1)
				localSearchResults.add(name.substring(0, name.indexOf("(")).trim());
			else
				localSearchResults.add(name);
		}

		buildScene(time.get());
	}
	
	
	public ChangeListener<Boolean> getRebuildFlagListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
								Boolean oldValue, Boolean newValue) {
				if (newValue)
					buildScene(time.get());
			}
		};
	}

	
	private void refreshScene() {
		// clear note billboards, cell spheres and meshes
		root.getChildren().clear();
		root.getChildren().add(xform);
		
		// clear note sprites and overlays
		if (overlayVBox!=null)
			overlayVBox.getChildren().clear();
		
		if (spritesPane!=null) {
			Iterator<Node> iter = spritesPane.getChildren().iterator();
			while (iter.hasNext()) {
				Node node = iter.next();
				if (node instanceof Text)
					iter.remove();
			}
		}
	}

	
	private void addEntitiesToScene() {
		//System.out.println(storiesList.toString());
		ArrayList<Shape3D> entities = new ArrayList<Shape3D>();
		ArrayList<Node> notes = new ArrayList<Node>();

		refreshScene();
		
		// Must render everything that belongs to scene first before
		// making the notes
		addSpheresToList(entities);
 		addMeshesToList(entities);
 		
 		insertOverlayTitles();
 		addNoteGeometries(notes);
 		
 		// render opaque entities first
 		if (!notes.isEmpty())
 			root.getChildren().addAll(notes);
		repositionNoteSprites();
		repositionNoteBillboardFronts();
 			
		Collections.sort(entities, opacityComparator);
		root.getChildren().addAll(entities);
	}
	
	
	// Inserts note geometries to scene
	// Input list is the list that billboards are added to which are added to the subscene
	// Note overlays and sprites are added to the pane that contains the subscene
	private void addNoteGeometries(ArrayList<Node> list) {		
		for (Note note : currentNotes) {
			// Revert to overlay display if we have invalid display/attachment 
			// type combination
			if (note.hasLocationError() || note.hasCellNameError() 
					|| note.hasTimeError())
				note.setTagDisplay(Display.OVERLAY);
			
			Node node = makeNoteGraphic(note);
			
			Type type = note.getAttachmentType();
			Display display = note.getTagDisplay();
			
			if (display!=null) {
				switch (display) {
					// Overlay: text is always facing the user in upper right corner
					case OVERLAY:
								// set overlay position relative to parent anchor pane
								if (overlayVBox!=null)
									overlayVBox.getChildren().add(node);
								break;
					
					// Billboard: text transforms with the scene meshes/spheres
					case BILLBOARD:
								if (positionBillboard(note, node))
									list.add(node);
								break;
					
					// Billboard front: text transforms with scene meshes/spheres
					// but always faces the user
					case BILLBOARD_FRONT:
								if (positionBillboardFront(note, node))
									list.add(node);
								break;
					
					// Sprite: text moves with whatever it is attached to and
					// always faces user
					case SPRITE:
								if (spritesPane!=null) {
									if (positionSprite(note, node))
										spritesPane.getChildren().add(node);
								}
								break;
							
					default:
								break;
				}
			}
		}
	}
	
	
	private void insertOverlayTitles() {
		if (storiesLayer!=null) {
			if (storiesLayer.getActiveStory()!=null && overlayVBox!=null) {
				Text infoPaneTitle = makeNoteOverlayText("Info Pane:");
				
				Text storyTitle = makeNoteOverlayText(storiesLayer.getActiveStory().getName());
				storyTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
				
				overlayVBox.getChildren().addAll(infoPaneTitle, storyTitle);
			}
		}
	}
	
	
	private Text makeNoteOverlayText(String title) {
		Text text = new Text(title);
		text.setFill(Color.WHITE);
		text.setFontSmoothingType(FontSmoothingType.LCD);
		if (overlayVBox!=null)
			text.setWrappingWidth(overlayVBox.getWidth());
		text.setFont(Font.font("System", FontWeight.MEDIUM, 16));
		return text;
	}
	
	
	private Text makeNoteSpriteText(String title) {
		Text text = makeNoteOverlayText(title);
		text.setWrappingWidth(130);
		return text;
	}
	
	
	private Text makeNoteBillboardText(String title) {
		Text text = new Text(title);
		text.setWrappingWidth(110);
		text.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
		text.setSmooth(false);
		text.setCacheHint(CacheHint.QUALITY);
		text.setFill(Color.WHITE);
		return text;
	}
	
	
	// Returns true if front-facing billboard is successfully positioned
	// (whether it is to location or cell)
	// false otherwise
	private boolean positionBillboardFront(Note note, Node node) {
		if (note.getAttachmentType()==Type.LOCATION) {
			if (note.isBillboard()) {
				node.getTransforms().add(new Translate(
										newOriginX+note.getX(),
										newOriginY+note.getY(),
										newOriginZ+note.getZ()));
				return true;
			}
		}
		
		else if (note.isAttachedToCell()
				|| (note.isAttachedToCellTime() && note.existsAtTime(time.get()-1))) {
			for (int i=0; i<cellNames.length; i++) {
				if (cellNames[i].equalsIgnoreCase(note.getCellName()) && spheres[i]!=null) {
					billboardFrontSphereMap.put(node, spheres[i]);
					//node.getTransforms().add(new Translate(5, 5));
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	// Returns true if billboard is successfully positioned
	// (whether it is to location or cell)
	// false otherwise
	private boolean positionBillboard(Note note, Node node) {
		if (note.getAttachmentType()==Type.LOCATION) {
			if (note.isBillboard()) {
				node.getTransforms().addAll(rotateX, rotateY, rotateZ);
				node.getTransforms().add(new Translate(
										newOriginX+note.getX(),
										newOriginY+note.getY(),
										newOriginZ+note.getZ()));
				return true;
			}
		}
		
		else if (note.isAttachedToCell()
				|| (note.isAttachedToCellTime() && note.existsAtTime(time.get()-1))) {
			for (int i=0; i<cellNames.length; i++) {
				if (cellNames[i].equalsIgnoreCase(note.getCellName()) && spheres[i]!=null) {
					double offset = 5;
					if (!uniformSize)
						offset = spheres[i].getRadius()+2;
					node.getTransforms().addAll(spheres[i].getTransforms());
					node.getTransforms().add(new Translate(offset, offset));
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	// Returns true if sprite is successfully positioned
	// (whether it is to location or cell)
	// false otherwise
	private boolean positionSprite(Note note, Node node) {
		if (note.getAttachmentType()==Type.LOCATION) {
				// Z coordinate is ignored for sprites - they reside on top of the subscene
				node.getTransforms().add(new Translate(note.getX(), note.getY()));				
				return true;
		}
		
		else if (note.isAttachedToCell() || (note.isAttachedToCellTime() 
				&& note.existsAtTime(time.get()-1))) {
			for (int i=0; i<cellNames.length; i++) {
				if (cellNames[i].equalsIgnoreCase(note.getCellName()) && spheres[i]!=null) {
					spriteSphereMap.put(node, spheres[i]);
					//node.getTransforms().add(new Translate(5, 5));
					return true;
				}
			}
		}
		return false;
	}
	
	
	// Makes an anchor pane that contains the text to be shown
	// if isOverlay is true, then the text is larger
	private Node makeNoteGraphic(Note note) {
		String title = note.getTagName();
		if (note.getTagDisplay()!=null) {
			switch (note.getTagDisplay()) {
				case OVERLAY:
							return makeNoteOverlayText(title);
				
				case SPRITE:
							return makeNoteSpriteText(title);
							
				case BILLBOARD:
							return makeNoteBillboardText(title);
							
				case BILLBOARD_FRONT:
							return makeNoteBillboardText(title);
				
				default:
							return null;
							
			}
		}
		return null;
	}
	
	
	private void addMeshesToList(ArrayList<Shape3D> list) {
		// Add meshes from active notes
		//list.addAll(currentNoteMeshes);
		for (Note note : currentNoteMeshMap.keySet())
			list.add(currentNoteMeshMap.get(note));
		
		// Consult rules
		if (!currentSceneElements.isEmpty()) {	
			for (int i=0; i<currentSceneElements.size(); i++) {
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
					
					// If mesh has with name(s), then process rules (cell or shape)
					// that apply to it
					else {
						TreeSet<Color> colors = new TreeSet<Color>(colorComparator);
						
						//iterate over rulesList
						for (Rule rule: rulesList) {
							
							if (rule instanceof MulticellularStructureRule) {
								//check equivalence of shape rule to scene name
								if (((MulticellularStructureRule)rule).appliesTo(sceneName)) {
									colors.add(Color.web(rule.getColor().toString()));
								}
							}
							
							else if(rule instanceof ColorRule) {
								//iterate over cells and check if cells apply
								for (String name: allNames) {
									if(((ColorRule)rule).appliesToBody(name)) {
										colors.add(rule.getColor());
									}
								}	
							}
						}
						
						// if any rules applied
						if (!colors.isEmpty())
							mesh.setMaterial(colorHash.getMaterial(colors));
						else
							mesh.setMaterial(colorHash.getOthersMaterial(othersOpacity.get()));
					}
				}
				
				list.add(mesh);
			}
		}
	}
	
	
	private void addSpheresToList(ArrayList<Shape3D> list) {
		// for sphere rendering
		for (int i = 0; i < cellNames.length; i ++) {
			double radius;
			if (!uniformSize)
				radius = SIZE_SCALE*diameters[i]/2;
			else
				radius = SIZE_SCALE*UNIFORM_RADIUS;
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
 				TreeSet<Color> colors = new TreeSet<Color>(colorComparator);
 				for (Rule rule : rulesList) {
 					// just need to consult rule's active list
 					if (rule.appliesToCell(cellNames[i])) {
 						colors.add(Color.web(rule.getColor().toString()));
 					}
 				}
 				material = colorHash.getMaterial(colors);

 				if (colors.isEmpty())
 					material = colorHash.getOthersMaterial(othersOpacity.get());
 			}

 			sphere.setMaterial(material);

 			double x = positions[i][X_COR_INDEX];
	        double y = positions[i][Y_COR_INDEX];
	        double z = positions[i][Z_COR_INDEX]*zScale;
	        sphere.getTransforms().addAll(rotateZ, rotateY, rotateX);
	        translateSphere(sphere, x, y, z);
	        
	        spheres[i] = sphere;
	        
	        list.add(spheres[i]);
		}
	}
	

	private void translateSphere(Node sphere, double x, double y, double z) {
		Translate t = new Translate(x, y, z);
		sphere.getTransforms().add(t);
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
		xform.setTranslate(newOriginX, newOriginY, newOriginZ);
		System.out.println("origin xyz: "+newOriginX+" "+newOriginY+" "+newOriginZ);
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
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				updateLocalSearchResults();
			}
		});
	}

	
	public void consultSearchResultsList() {
		searchedCells = new boolean[cellNames.length];
		searchedMeshes = new boolean[meshNames.length];
		
		// look for searched cells
		for (int i=0; i<cellNames.length; i++) {
			if (localSearchResults.contains(cellNames[i]))
				searchedCells[i] = true;
			else
				searchedCells[i] = false;
		}
		
		// look for single celled meshes
		for (int i=0; i<meshNames.length; i++) {
			if (sceneElementsAtTime.get(i).isMulticellular()) {
				searchedMeshes[i] = true;
				for (String name : sceneElementsAtTime.get(i).getAllCellNames()) {
					if (localSearchResults.contains(name))
						searchedMeshes[i] &= true;
					else
						searchedMeshes[i] &= false;
				}
			}
			else {
				if (localSearchResults.contains(meshNames[i]))
					searchedMeshes[i] = true;
				else
					searchedMeshes[i] = false;
			}
		}
	}

	
	public void printCellNames() {
		for (int i = 0; i < cellNames.length; i++)
			System.out.println(cellNames[i]+CS+spheres[i]);
	}
	
	
	public void printMeshNames() {
		for (int i = 0; i < meshNames.length; i++)
			System.out.println(meshNames[i]+CS+meshes[i]);
	}

	
	// sets everything associated with color rules
	public void setRulesList(ObservableList<Rule> list) {
		if (list == null)
			return;
		
		rulesList = list;
		rulesList.addListener(new ListChangeListener<Rule>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Rule> change) {
				while (change.next()) {
					for (Rule rule : change.getAddedSubList()) {
						rule.getRuleChangedProperty().addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable,
									Boolean oldValue, Boolean newValue) {
								if (newValue)
									buildScene(time.get());
							}
						});
						buildScene(time.get());
					}
					
					for (Rule rule : change.getRemoved()) {
						buildScene(time.get());
					}
				}
			}
		});
	}
	
	
	// Sets transparent anchor pane overlay for sprite notes display
	public void setNotesPane(Pane parentPane) {
		if (parentPane!=null) {
			spritesPane = parentPane;
			
			overlayVBox = new VBox(5);
			overlayVBox.setPrefWidth(130);
			overlayVBox.setMaxWidth(overlayVBox.getPrefWidth());
			AnchorPane.setTopAnchor(overlayVBox, 5.0);
			AnchorPane.setRightAnchor(overlayVBox, 5.0);
			
			spritesPane.getChildren().add(overlayVBox);
		}
	}

	
	public ArrayList<ColorRule> getColorRulesList() {
		ArrayList<ColorRule> list = new ArrayList<ColorRule>();
		for (Rule rule : rulesList) {
			if (rule instanceof ColorRule)
				list.add((ColorRule)rule);
		}
		return list;
	}

	
	public ObservableList<Rule> getObservableColorRulesList() {
		return rulesList;
	}

	
	public void setColorRulesList(ArrayList<ColorRule> list) {
		rulesList.clear();
		rulesList.setAll(list);
	}

	
	public int getTime() {
		return time.get();
	}

	
	public void setTime(int t) {
		if (t > 0 && t < endTime)
			time.set(t);
	}
	
	
	public void setRotations(double rx, double ry, double rz) {
		rx = Math.toDegrees(rx);
		ry = Math.toDegrees(ry);
		rx = Math.toDegrees(rz);
		
		rotateX.setAngle(rx+180);
		rotateY.setAngle(ry);
		rotateZ.setAngle(rz);
	}
	

	public double getRotationX() {
		if (spheres[0]!=null) {
			Transform transform = spheres[0].getLocalToSceneTransform();
			double roll = Math.atan2(-transform.getMyx(), transform.getMxx());
			return roll;  
		}
		else
			return 0;
	}

	
	public double getRotationY() {
		if (spheres[0]!=null) {
			Transform transform = spheres[0].getLocalToSceneTransform();
			double pitch = Math.atan2(-transform.getMzy(), transform.getMzz());
			return pitch;
		}
		else
			return 0;
	}
	
	
	public double getRotationZ() {
		if (spheres[0]!=null) {
			Transform transform = spheres[0].getLocalToSceneTransform();
			double yaw = Math.atan2(transform.getMzx(), Math.sqrt((transform.getMzy()*transform.getMzy()
														+(transform.getMzz()*transform.getMzz()))));
			return yaw;
		}
		else
			return 0;
	}

	
	public double getTranslationX() {
		return xform.t.getTx()-newOriginX;
	}
	
	
	public void setTranslationX(double tx) {
		double newTx = tx+newOriginX;
		if (newTx>0 && newTx<450)
			xform.t.setX(newTx);
	}
	
	
	public double getTranslationY() {
		return xform.t.getTy()-newOriginY;
	}
	
	
	public void setTranslationY(double ty) {
		double newTy = ty+newOriginY;
		if (newTy>0 && newTy<450)
			xform.t.setY(newTy);
	}

	
	public double getScale() {
		double scale = zoom.get()-0.5;
		scale = 1-(scale/6.5);
		return scale;
	}

	
	public void setScale(double scale) {
		if (scale > 1)
			scale = 1;
		scale = 6.5*(1-scale);
		// smaller zoom value means larger picture
		zoom.set((scale+0.5));
	}

	
	public double getOthersVisibility() {
		return othersOpacity.get();
	}
	

	public void setOthersVisibility(double dim) {
		othersOpacity.set(dim);
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
			public void changed(ObservableValue<? extends Number> observable,
											Number oldValue, Number newValue) {
				othersOpacity.set(Math.round(newValue.doubleValue())/100d);

				

				buildScene(time.get());
			}
		};
	}

	
	public void addListenerToOpacitySlider(Slider slider) {
		othersOpacity.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				Double arg = arg0.getValue().doubleValue();
				if (arg>=0 && arg<=1.0) {
					slider.setValue(arg*100.0);
				}
			}
		});
	}

	
	public ChangeListener<String> getSearchFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
										String oldValue, String newValue) {
				if (newValue.isEmpty()) {
					inSearch = false;
					buildScene(time.get());
				}
				else
					inSearch = true;
			}
		};
	}
	

	public int getEndTime() {
		return endTime;
	}

	
	public int getStartTime() {
		return START_TIME;
	}
	

	public EventHandler<ActionEvent> getZoomInButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double z = zoom.get();
				if (z<=5 && z>0.25)
					zoom.set(z-.25);
			}
		};
	}
	

	public EventHandler<ActionEvent> getZoomOutButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double z = zoom.get();
				if (z<5 && z>=0.25)
					zoom.set(z+.25);
			}
		};
	}
	

	public EventHandler<ActionEvent> getBackwardButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!playingMovie.get()) {
					int t = time.get();
					if (t>1 && t<=getEndTime())
						time.set(t-1);
				}
			}
		};
	}
	

	public EventHandler<ActionEvent> getForwardButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!playingMovie.get()) {
					int t = time.get();
					if (t>=1 && t<getEndTime()-1)
						time.set(t+1);
				}
			}
		};
	}
	
	
	public ChangeListener<Boolean> getUniformSizeCheckBoxListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
									Boolean oldValue, Boolean newValue) {
				uniformSize = newValue.booleanValue();
				buildScene(time.get());
			}
		};
	}
	

	private final class RenderService extends Service<Void> {
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							refreshScene();
							otherCells.clear();
							addEntitiesToScene();
						}
					});
					return null;
				}
			};
		}
	}
	

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
								if (time.get()<endTime-1)
									time.set(time.get()+1);
								else
									time.set(endTime-1);
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
	
	
	private final class SubsceneSizeListener implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			repositionNoteSprites();
			repositionNoteBillboardFronts();
		}
	}
	

	public ChangeListener<Boolean> getCellNucleusTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellNucleusTicked = newValue;
				buildScene(time.get());
			}
		};
	}
	
	
	public ChangeListener<Boolean> getCellBodyTickListener() {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				cellBodyTicked = newValue;
				buildScene(time.get());
			}
		};
	}
	
	
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
				multicellMode = newValue;
			}
		};
	}
	
	
	public EventHandler<MouseEvent> getNoteClickHandler() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				PickResult result = event.getPickResult();
				if (result.getIntersectedNode() instanceof Text) {
					// TODO
					System.out.println("note clicked");
				}
			}
		};
	}
	

	private final String CS = ", ";

	private final String FILL_COLOR_HEX = "#272727";

	private final long WAIT_TIME_MILLI = 200;

	private final double CAMERA_INITIAL_DISTANCE = -800;

    private final double CAMERA_NEAR_CLIP = 1,
    							CAMERA_FAR_CLIP = 2000;

    private final int START_TIME = 1;

    private final int X_COR_INDEX = 0,
    						Y_COR_INDEX = 1,
    						Z_COR_INDEX = 2;

    private final double Z_SCALE = 5,
					    		X_SCALE = 1,
					    		Y_SCALE = 1;

    private final double SIZE_SCALE = .9;
    private final double UNIFORM_RADIUS = 4;
}
