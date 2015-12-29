package wormguides.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import wormguides.model.StoriesList;
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
import javafx.geometry.Bounds;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import wormguides.ColorComparator;
import wormguides.Xform;
import wormguides.model.ColorHash;
import wormguides.model.ColorRule;
import wormguides.model.LineageData;
import wormguides.model.Note;
import wormguides.model.Note.Display;
import wormguides.model.Note.Type;
import wormguides.model.Rule;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;
import wormguides.model.ShapeRule;

public class Window3DSubScene{

	private LineageData data;

	private SubScene subscene;
	private VBox overlayVBox;
	
	// for note sprites/billboards attached to cell/celltime
	private HashMap<Node, Sphere> spriteSphereMap;
	private HashMap<Node, Sphere> billboardSphereMap;

	// transformation stuff
	private Group root;
	private PerspectiveCamera camera;
	private Xform cameraXform;
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

	// searched highlighting stuff
	private boolean inSearch;
	private ObservableList<String> searchResultsList;
	private ArrayList<String> localSearchResults;

	// color rules stuff
	private ColorHash colorHash;
	private ObservableList<Rule> rulesList;
	private ObservableList<ShapeRule> shapeRulesList;
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
	//reference of successfully rendered scene elements for click responsiveness
	private ArrayList<SceneElement> currentSceneElements;
	
	// Uniform nuclei size
	private boolean uniformSize;
	
	// Cell body and cell nucleus highlighting in search mode
	private boolean cellNucleusTicked;
	private boolean cellBodyTicked;
	private boolean multicellMode;

	//Story elements stuff
	private StoriesList storiesList;
	// currentNotes contains all notes that are 'active' within a scene
	// (any note that should be visible in a given frame)
	private ArrayList<Note> currentNotes;

	
	public Window3DSubScene(double width, double height, LineageData data) {
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

		selectedIndex = new SimpleIntegerProperty();
		selectedIndex.set(-1);

		selectedName = new SimpleStringProperty();
		selectedName.set("");
		selectedName.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				int selected = getIndexByCellName(newValue);
				if (selected != -1)
					selectedIndex.set(selected);
			}
		});

		inSearch = false;

		totalNuclei = new SimpleIntegerProperty();
		totalNuclei.set(0);

		endTime = data.getTotalTimePoints();

		createSubScene(width, height);

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

		renderService = new RenderService();

		zoom.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				cameraXform.setScale(zoom.get());
			}
		});

		localSearchResults = new ArrayList<String>();

		geneResultsUpdated = new SimpleBooleanProperty();

		// get completely opaque 'other' material first
		othersOpacity = new SimpleDoubleProperty(1);
		
		otherCells = new ArrayList<String>();

		rotateX = new Rotate(0, 0, newOriginY, newOriginZ, Rotate.X_AXIS);
		rotateY = new Rotate(0, newOriginX, 0, newOriginZ, Rotate.Y_AXIS);
		rotateZ = new Rotate(0, newOriginX, newOriginY, 0, Rotate.Z_AXIS);
		
		uniformSize = false;
		
		rulesList = FXCollections.observableArrayList();
		shapeRulesList = FXCollections.observableArrayList();
		
		colorHash = new ColorHash();
		colorComparator = new ColorComparator();
		opacityComparator = new OpacityComparator();
		
		currentSceneElementMeshes = new ArrayList<MeshView>();
		currentSceneElements = new ArrayList<SceneElement>();
		
		currentNotes = new ArrayList<Note>();
		spriteSphereMap = new HashMap<Node, Sphere>();
		billboardSphereMap = new HashMap<Node, Sphere>();
	}
	
	
	/*
	 * Called by RootLayoutController to set the loaded SceneElementsList
	 * after the list is set, the SceneElements are loaded
	 */
	public void setSceneElementsList(SceneElementsList list) {
		if (list!=null) {
			sceneElementsList = list;
			sceneElementsList.toString();
		}
	}
	
	
	public void setStoriesList(StoriesList list) {
		if (list!=null) {
			storiesList = list;
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

	
	public IntegerProperty getTotalNucleiProperty() {
		return totalNuclei;
	}

	
	public BooleanProperty getPlayingMovieProperty() {
		return playingMovie;
	}

	
	private SubScene createSubScene(Double width, Double height) {
		subscene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);

		subscene.setFill(Color.web(FILL_COLOR_HEX));
		subscene.setCursor(Cursor.HAND);

		subscene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				subscene.setCursor(Cursor.CLOSED_HAND);

				mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);
                mouseDeltaX /= 4;
                mouseDeltaY /= 4;

				if (me.isPrimaryButtonDown()) {
					mouseDeltaX /= 2;
	                mouseDeltaY /= 2;
	                rotateX.setAngle((rotateX.getAngle()+mouseDeltaY)%360);
	        		rotateY.setAngle((rotateY.getAngle()-mouseDeltaX)%360);
	        		repositionNotes();
	        		//System.out.println("x: "+rotateX.getAngle()+" y: "+rotateY.getAngle());
				}

				else if (me.isSecondaryButtonDown()) {
					double tx = cameraXform.t.getTx()-mouseDeltaX;
					double ty = cameraXform.t.getTy()-mouseDeltaY;

					if (tx>0 && tx<450)
						cameraXform.t.setX(tx);
					if (ty>0 && ty<450)
						cameraXform.t.setY(ty);
					
					repositionNotes();
				}
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
					selectedName.set(cellNames[selectedIndex.get()]);
				}
				else if (node instanceof MeshView) {
					for (int i = 0; i < currentSceneElementMeshes.size(); i++) {
						MeshView curr = currentSceneElementMeshes.get(i);
						if (curr.equals(node)) {
							SceneElement clickedSceneElement = currentSceneElements.get(i);
							selectedName.set(clickedSceneElement.getSceneName());
						}
					}
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
	
	
	private void repositionNotes() {
		// Reposition sprites
		for (Node node : spriteSphereMap.keySet()) {
			Sphere s = spriteSphereMap.get(node);
			if (s!=null) {
				Bounds bound = s.localToParent(s.getBoundsInLocal());	
				node.getTransforms().clear();
				node.getTransforms().add(new Translate(
										(bound.getMinX()+bound.getMaxX())/2, 
										(bound.getMinY()+bound.getMaxY())/2,
										(bound.getMinZ()+bound.getMaxZ())/2));
			}
		}
		
		for (Node node : billboardSphereMap.keySet()) {
			Sphere s = billboardSphereMap.get(node);
			node.getTransforms().clear();
			node.getTransforms().addAll(s.getTransforms());
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
		
		
//-------------------------STORY ELEMENTS---------------------
		if (storiesList!=null) {
			currentNotes = storiesList.getNotesAtTime(time);
			for (Note note : storiesList.getNotesWithCell()) {
				for (String name : cellNames) {
					if (name.equalsIgnoreCase(note.getCellName())) {
						//System.out.println("got a cell note");
						currentNotes.add(note);
						break;
					}
				}
			}
			
			spriteSphereMap.clear();
			billboardSphereMap.clear();
		}
//---------------------------------------------------------------
		

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

	
	private void refreshScene() {
		// clear note billboards, cell spheres and meshes
		root.getChildren().clear();
		root.getChildren().add(cameraXform);
		
		// clear note sprites and overlays
		if (overlayVBox!=null)
			overlayVBox.getChildren().clear();
	}

	
	private void addEntitiesToScene() {
		ArrayList<Shape3D> entities = new ArrayList<Shape3D>();

		refreshScene();
		
		// Must render everything that belongs to scene first before
		// making the notes
		addSpheresToList(entities);
 		addMeshesToList(entities);
 		
 		// render opaque entities first
		addNotesGeometryToList(root.getChildren());
		repositionNotes();
 			
		Collections.sort(entities, opacityComparator);
		root.getChildren().addAll(entities);
		
		// TODO Remove this later
		// only for testing purposes (make Stanford bunny appear)
		for (int i=0; i<currentSceneElements.size(); i++) {
			SceneElement se = currentSceneElements.get(i);
			if (se.getSceneName().contains("first scene")) {
				MeshView mesh = currentSceneElementMeshes.get(i);
				mesh.getTransforms().addAll(new Translate(newOriginX, newOriginY, newOriginZ), new Scale(180, -180, -180));
				//System.out.println(mesh.getBoundsInParent().toString());
			}
		}
	}
	
	
	private void addNotesGeometryToList(ObservableList<Node> list) {
		for (Note note : currentNotes) {
			
			// Revert to overlay display if we have invalid display/attachment 
			// type combination
			if (note.hasLocationError() || note.hasCellNameError() 
										|| note.hasTimeError())
				note.setTagDisplay(Display.OVERLAY);
			
			Node node = makeShowTextNode(note.getTagName(), note.isOverlay());
			
			Type type = note.getAttachmentType();
			Display display = note.getTagDisplay();
			
			switch (display) {
			
				// Overlay: text is always facing the user
				case OVERLAY:
						// set overlay position relative to parent anchor pane
						if (overlayVBox!=null) {
							overlayVBox.getChildren().add(node);
							//System.out.println("overlay: "+note.toString());
						}
						break;
				
				// Billboard: text transforms with the scene meshes/spheres
				case BILLBOARD:
						if (positionSpriteOrBillboard(note, node))
							list.add(node);
						//System.out.println("billboard: "+note.toString());
						break;
				
				// Sprite: text moves with whatever it is attached to and
				// always faces user
				case SPRITE:
						if (positionSpriteOrBillboard(note, node))
							list.add(node);
						break;
						
				default:
						break;
			}
		}
	}
	
	
	// Returns true if sprite or billboard is successfully positioned
	// (whether it is to location or cell)
	// false otherwise
	private boolean positionSpriteOrBillboard(Note note, Node node) {
		if (note.getAttachmentType()==Type.LOCATION) {
			if (note.isBillboard())
				node.getTransforms().addAll(rotateX, rotateY, rotateZ);
			node.getTransforms().add(new Translate(
									newOriginX+note.getX(),
									newOriginY+note.getY(),
									newOriginZ+note.getZ()));
			return true;
		}
		else if (note.isAttachedToCell() 
				|| (note.isAttachedToCellTime() && note.existsAtTime(time.get()-1))) {
			for (int i=0; i<cellNames.length; i++) {
				if (cellNames[i].equalsIgnoreCase(note.getCellName())) {
					if (spheres[i]!=null) {
						if (note.isBillboard()) {
							node.getTransforms().addAll(rotateX, rotateY, rotateZ);
							billboardSphereMap.put(node, spheres[i]);
							node.getTransforms().addAll(spheres[i].getTransforms());
						}
						else if (note.isSprite())
							spriteSphereMap.put(node, spheres[i]);
						
						return true;
					}
					break;
				}
			}
		}
		return false;
	}
	
	
	// Makes an anchor pane that contains the text to be shown
	// if isOverlay is true, then the text is larger
	private Node makeShowTextNode(String text, boolean isOverlay) {
		AnchorPane pane = new AnchorPane();
		Text t = new Text(text);
		AnchorPane.setTopAnchor(t, 0.0);
		AnchorPane.setLeftAnchor(t, 0.0);
		AnchorPane.setRightAnchor(t, 0.0);
		AnchorPane.setBottomAnchor(t, 0.0);
		
		if (isOverlay) {
			pane.setPrefWidth(120);
			t.setFont(new Font(18));
		}
		else {
			pane.setPrefWidth(60);
			t.setFont(new Font(12));
		}
		
		t.setWrappingWidth(t.prefWidth(-1));
		t.setFill(Color.WHITE);
		pane.getChildren().add(t);
		
		return pane;
	}
	
	
	private void addMeshesToList(ArrayList<Shape3D> list) {
		// consult ShapeRule(s)
		// process only if meshes at this time point
		if (!currentSceneElements.isEmpty()) {	
			for (int i=0; i<currentSceneElements.size(); i++) {
				// in search mode
				if (inSearch) {
	 				if (cellBodyTicked && searchedMeshes[i])
	 					currentSceneElementMeshes.get(i).setMaterial(colorHash.getHighlightMaterial());
	 				else 
	 					currentSceneElementMeshes.get(i).setMaterial(colorHash.getTranslucentMaterial());
	 			}
				
				else {
					// in regular view mode
					ArrayList<String> allNames = currentSceneElements.get(i).getAllCellNames();
					String sceneName = currentSceneElements.get(i).getSceneName();
					
					// default white meshes
					if (allNames.isEmpty()) {
						currentSceneElementMeshes.get(i).setMaterial(new PhongMaterial(Color.WHITE));
						currentSceneElementMeshes.get(i).setCullFace(CullFace.NONE);
					}
					
					// If mesh has with name(s), then process rules (cell or shape)
					// that apply to it
					else {
						TreeSet<Color> colors = new TreeSet<Color>(colorComparator);
						
						//iterate over rulesList
						for (Rule rule: rulesList) {
							if (rule instanceof ShapeRule) {
								//check equivalence of shape rule to scene name
								if (((ShapeRule)rule).appliesTo(sceneName)) {
									colors.add(Color.web(((ShapeRule)rule).getColor().toString()));
								}
								
							}
							else if(rule instanceof ColorRule) {
								//iterate over cells and check if cells apply
								for (String name: allNames) {
									if(((ColorRule)rule).appliesToBody(name)) {
										colors.add(((ColorRule)rule).getColor());
									}
								}	
							}
						}
						
						// if ShapeRule(s) applied
						if (!colors.isEmpty())
							currentSceneElementMeshes.get(i).setMaterial(colorHash.getMaterial(colors));
						else
							currentSceneElementMeshes.get(i).setMaterial(colorHash.getOthersMaterial(othersOpacity.get()));
					}
				}
				
				list.add(currentSceneElementMeshes.get(i));
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
		this.cameraXform = new Xform();
		cameraXform.reset();

		root.getChildren().add(cameraXform);
		cameraXform.getChildren().add(camera);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);

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
		cameraXform.setTranslate(newOriginX, newOriginY, newOriginZ);
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
	public void setColorRulesList(ObservableList<Rule> list) {
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
							public void changed(
									ObservableValue<? extends Boolean> observable,
									Boolean oldValue, Boolean newValue) {
								if (newValue)
									buildScene(time.get());
							}
						});
					}
					buildScene(time.get());
				}
			}
		});
	}
	
	
	// Sets parent anchor pane in order to
	// display text from notes
	public void setOverlayVBox(VBox box) {
		if (box!=null) {
			overlayVBox = box;
			overlayVBox.toFront();
		}
	}
	
	
	// Sets anything associated with shape rules
	public void setShapeRulesList(ObservableList<ShapeRule> list) {
		if (list == null)
			return;
		
		shapeRulesList = list;
		shapeRulesList.addListener(new ListChangeListener<ShapeRule>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends ShapeRule> change) {
				while (change.next()) {
					for (ShapeRule rule : change.getAddedSubList()) {
						rule.getRuleChangedProperty().addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(
									ObservableValue<? extends Boolean> observable,
									Boolean oldValue, Boolean newValue) {
								if (newValue)
									buildScene(time.get());
							}
						});
					}
					buildScene(time.get());
				}
			}
		});
	}

	
	public ArrayList<ColorRule> getColorRulesList() {
		ArrayList<ColorRule> list = new ArrayList<ColorRule>();
		for (Rule rule : rulesList) {
			if (rule instanceof ColorRule) {
				list.add((ColorRule)rule);
			}
		}
		return list;
	}
	
	
	public ArrayList<ShapeRule> getShapeRulesList() {
		ArrayList<ShapeRule> list = new ArrayList<ShapeRule>();
		for (ShapeRule rule : shapeRulesList)
			list.add(rule);
		return list;
	}

	
	public ObservableList<Rule> getObservableColorRulesList() {
		return rulesList;
	}
	
	
	public ObservableList<ShapeRule> getObservableShapeRulesList() {
		return shapeRulesList;
	}

	
	public void setColorRulesList(ArrayList<ColorRule> list) {
		rulesList.clear();
		rulesList.setAll(list);
	}
	
	
	public void setShapeRulesList(ArrayList<ShapeRule> list) {
		shapeRulesList.clear();
		shapeRulesList.setAll(list);
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
		return cameraXform.t.getTx()-newOriginX;
	}
	
	
	public void setTranslationX(double tx) {
		double newTx = tx+newOriginX;
		if (newTx>0 && newTx<450)
			cameraXform.t.setX(newTx);
	}
	
	
	public double getTranslationY() {
		return cameraXform.t.getTy()-newOriginY;
	}
	
	
	public void setTranslationY(double ty) {
		double newTy = ty+newOriginY;
		if (newTy>0 && newTy<450)
			cameraXform.t.setY(newTy);
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
	

	private static final String CS = ", ";

	private static final String FILL_COLOR_HEX = "#272727";

	private static final long WAIT_TIME_MILLI = 200;

	private static final double CAMERA_INITIAL_DISTANCE = -800;

    private static final double CAMERA_NEAR_CLIP = 1,
    							CAMERA_FAR_CLIP = 2000;

    private static final int START_TIME = 1;

    private static final int X_COR_INDEX = 0,
    						Y_COR_INDEX = 1,
    						Z_COR_INDEX = 2;

    private static final double Z_SCALE = 5,
					    		X_SCALE = 1,
					    		Y_SCALE = 1;

    private static final double SIZE_SCALE = .9;
    private static final double UNIFORM_RADIUS = 4;

}
