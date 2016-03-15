package wormguides.view;

import wormguides.model.ColorHash;
import wormguides.model.LineageData;
import wormguides.model.PartsList;
import wormguides.model.Rule;
import wormguides.ColorComparator;
import wormguides.Search;
import wormguides.SearchOption;
import wormguides.SearchType;
import wormguides.controllers.ContextMenuController;
import wormguides.loaders.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.Field;
import javafx.util.Duration;
import java.lang.Math;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SulstonTreePane extends ScrollPane {

	private LineageData data;
	private HashMap<String, Integer> nameXUseMap;
	private HashMap<String, Integer> nameYStartUseMap;
	private ArrayList<String> hiddenNodes;
	private TreeItem<String> lineageTreeRoot;

	private ColorHash colorHash;

	private int maxX = 0; // global to class to keep track of current x layout
							// position
	private ObservableList<Rule> rules;
	private Pane mainPane;
	private Group zoomGroup;

	// Node content;
	private Scale scaleTransform;
	private Line timeIndicatorBar;
	private Text timeIndicator;
	private int ttduration = 0;
	private IntegerProperty time;

	private int xsc = 5;// =XScale minimal spacing between branches, inter
						// branch gap
	// seems to be some multiple of this?

	private int iXmax = 19; // left margin
	private int iYmin = 19;

	private Stage contextMenuStage;
	private ContextMenuController contextMenuController;
	private StringProperty selectedNameLabeled;

	private Stage ownStage;

	private Pane canvas;
	private final static int timeLabelOffsetX = 20;

	private final static int timeOffset = 19;

	EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			String sourceName = ((Node) event.getSource()).getId();

			// right click
			if (event.getButton() == MouseButton.SECONDARY
					|| (event.getButton() == MouseButton.PRIMARY && (event.isControlDown() || event.isMetaDown()))) {
				showContextMenu(sourceName, event.getScreenX(), event.getScreenY());
			}

			// left click
			else if (event.getButton() == MouseButton.PRIMARY) {
				contextMenuStage.hide();

				resetSelectedNameLabeled(sourceName);

				if (hiddenNodes.contains(sourceName))
					hiddenNodes.remove(sourceName);
				else
					hiddenNodes.add(sourceName);
				updateDrawing();
			}
		}

	};

	public SulstonTreePane() {

	}

	public SulstonTreePane(Stage ownStage, LineageData data, TreeItem<String> lineageTreeRoot,
			ObservableList<Rule> rules, ColorHash colorHash, IntegerProperty time, ContextMenuController controller,
			StringProperty selectedNameLabeled) {
		super();

		this.ownStage = ownStage;

		ownStage.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldStageWidth,
					Number newStageWidth) {
				if (canvas != null) {
					canvas.setPrefWidth(newStageWidth.doubleValue());
					;
				}
			}
		});

		ownStage.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldStageHeight,
					Number newStageHeight) {
				if (canvas != null) {
					canvas.setPrefHeight(newStageHeight.doubleValue());
					;
				}
			}
		});

		this.time = time;
		time.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				repositionTimeLine();
			}
		});

		this.hiddenNodes = new ArrayList<String>();
		// start with founders visible
		hiddenNodes.add("ABala");
		hiddenNodes.add("ABalp");
		hiddenNodes.add("ABara");
		hiddenNodes.add("ABarp");
		hiddenNodes.add("ABpla");
		hiddenNodes.add("ABplp");
		hiddenNodes.add("ABpra");
		hiddenNodes.add("ABprp");

		hiddenNodes.add("MSa");
		hiddenNodes.add("MSp");
		hiddenNodes.add("Ea");
		hiddenNodes.add("Ep");
		hiddenNodes.add("Ca");
		hiddenNodes.add("Cp");
		hiddenNodes.add("D");
		hiddenNodes.add("P4");

		this.rules = rules;
		this.colorHash = colorHash;
		this.lineageTreeRoot = lineageTreeRoot;

		rules.addListener(new ListChangeListener<Rule>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Rule> change) {
				while (change.next()) {
					if (change.getAddedSize() > 0) {
						updateColoring();

						for (Rule rule : change.getAddedSubList()) {
							rule.getRuleChangedProperty().addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
										Boolean newValue) {
									if (newValue)
										updateColoring();
								}
							});
						}
					}

					if (change.wasRemoved())
						updateColoring();
				}
			}
		});

		nameXUseMap = new HashMap<String, Integer>();
		nameYStartUseMap = new HashMap<String, Integer>();

		canvas = new Pane();
		canvas.setStyle("-fx-background-color: #e1e1ea;");
		this.data = data;
		mainPane = canvas;

		// zooming
		// TODO
		scaleTransform = new Scale(1.75, 1.75, 0, 0);
		Group contentGroup = new Group();
		zoomGroup = new Group();
		contentGroup.getChildren().add(zoomGroup);
		zoomGroup.getChildren().add(canvas);
		zoomGroup.getTransforms().add(scaleTransform);

		canvas.setVisible(true);

		this.getChildren().add(contentGroup);
		this.setPannable(true);

		addLines(lineageTreeRoot, canvas);
		this.setPrefSize(520, 700);

		// add controls for zoom
		Button plus = new Button();
		plus.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		plus.setGraphic(new ImageView(ImageLoader.getPlusIcon()));
		plus.setStyle("-fx-focus-color: -fx-outer-border; " + "-fx-faint-focus-color: transparent;"
				+ "-fx-background-color: transparent;");
		plus.setPrefSize(30, 30);
		plus.setMaxSize(30, 30);
		plus.setMinSize(30, 30);

		Button minus = new Button();
		minus.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		minus.setGraphic(new ImageView(ImageLoader.getMinusIcon()));
		minus.setStyle("-fx-focus-color: -fx-outer-border; " + "-fx-faint-focus-color: transparent;"
				+ "-fx-background-color: transparent;");
		minus.setPrefSize(30, 30);
		minus.setMaxSize(30, 30);
		minus.setMinSize(30, 30);

		contentGroup.getChildren().add(plus);
		contentGroup.getChildren().add(minus);
		plus.getTransforms().add(new Translate(50, 5));
		minus.getTransforms().add(new Translate(15, 5));

		plus.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				scaleTransform.setX(scaleTransform.getX() * 1.3333);
				scaleTransform.setY(scaleTransform.getY() * 1.3333);
			}
		});

		minus.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				scaleTransform.setX(scaleTransform.getX() * .75);
				scaleTransform.setY(scaleTransform.getY() * .75);
			}
		});

		Pane yetanotherlevel = new Pane();
		yetanotherlevel.setStyle("-fx-background-color: #e1e1ea;");
		yetanotherlevel.getChildren().add(contentGroup);
		this.setContent(yetanotherlevel);

		bindLocation(plus, this, yetanotherlevel);
		bindLocation(minus, this, yetanotherlevel);

		contextMenuController = controller;
		contextMenuStage = contextMenuController.getOwnStage();

		this.selectedNameLabeled = selectedNameLabeled;
	}

	private void resetSelectedNameLabeled(String name) {
		selectedNameLabeled.set("");
		selectedNameLabeled.set(name);
	}

	public void useStartupDefaultView() {
		// expand specific lineages for default view on startup
		// expand these
		String[] cellsToExpand = new String[] { "ABala", "ABalp", "ABara", "ABarp", "ABpla", "ABprp", "ABpra", "MSa",
				"MSp", "Ca", "Cp" };
		for (String cell : cellsToExpand) {
			if (hiddenNodes.contains(cell))
				hiddenNodes.remove(cell);
			else
				hiddenNodes.add(cell);
		}
		// hide these
		String[] cellsToHide = new String[] { "ABalaa", "ABalap", "ABalpa", "ABalpp", "ABaraa", "ABarap", "ABarpa",
				"ABarpp", "ABplpa", "ABplaa", "ABplap", "ABplppaa", "ABplpppp", "ABpraa", "ABprap", "ABprpaaa",
				"ABprpapaa", "ABprppaa", "ABprppp", "MSaa", "MSap", "MSpa", "MSpp", "Caa", "Cap", "Cpa", "Cpp" };
		for (String cell : cellsToHide) {
			if (hiddenNodes.contains(cell))
				hiddenNodes.remove(cell);
			else
				hiddenNodes.add(cell);
		}
		updateDrawing();
	}

	private void showContextMenu(String name, double sceneX, double sceneY) {
		if (contextMenuStage != null) {
			contextMenuController.setName(name);

			String funcName = PartsList.getFunctionalNameByLineageName(name);

			if (funcName == null)

				contextMenuController.disableTerminalCaseFunctions(true);
			else
				contextMenuController.disableTerminalCaseFunctions(false);

			contextMenuController.setColorButtonListener(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Rule rule = Search.addColorRule(SearchType.LINEAGE, name, Color.WHITE, SearchOption.CELLNUCLEUS);
					rule.showEditStage(ownStage);

					contextMenuStage.hide();
				}
			});

			contextMenuController.setColorNeighborsButtonListener(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// call distance Search method
					Rule rule = Search.addColorRule(SearchType.NEIGHBOR, name, Color.WHITE, SearchOption.CELLNUCLEUS);
					rule.showEditStage(ownStage);
					contextMenuStage.hide();
				}
			});

			contextMenuStage.setX(sceneX);
			contextMenuStage.setY(sceneY);
			contextMenuStage.show();

			((Stage) contextMenuStage.getScene().getWindow()).toFront();
		}
	}

	private void repositionTimeLine() {
		timeIndicatorBar.setEndY(iYmin + time.getValue());
		timeIndicatorBar.setStartY(iYmin + time.getValue());
		timeIndicator.setY(iYmin + time.getValue());

		timeIndicator.setText(Integer.toString(time.get() + timeOffset));
	}

	private void bindLocation(Button plus, ScrollPane s, Pane scontent) {
		plus.layoutYProperty().bind(
				// to vertical scroll shift (which ranges from 0 to 1)
				s.vvalueProperty()
						// multiplied by (scrollableAreaHeight -
						// visibleViewportHeight)
						.multiply(scontent.heightProperty().subtract(new ScrollPaneViewPortHeightBinding(s))));

		plus.layoutXProperty().bind(
				// to vertical scroll shift (which ranges from 0 to 1)
				s.hvalueProperty()
						// multiplied by (scrollableAreaHeight -
						// visibleViewportHeight)
						.multiply(scontent.widthProperty().subtract(new ScrollPaneViewPortWidthBinding(s))));

	}

	// lifted code to create control zoom overlays
	// we need this class because Bounds object doesn't support binding
	private static class ScrollPaneViewPortHeightBinding extends DoubleBinding {
		private final ScrollPane root;

		public ScrollPaneViewPortHeightBinding(ScrollPane root) {
			this.root = root;
			super.bind(root.viewportBoundsProperty());
		}

		@Override
		protected double computeValue() {
			return root.getViewportBounds().getHeight();
		}
	}

	private static class ScrollPaneViewPortWidthBinding extends DoubleBinding {

		private final ScrollPane root;

		public ScrollPaneViewPortWidthBinding(ScrollPane root) {
			this.root = root;
			super.bind(root.viewportBoundsProperty());
		}

		@Override
		protected double computeValue() {
			return root.getViewportBounds().getWidth();
		}
	}

	private void updateDrawing() {
		// clear drawing
		// System.out.println("update drawing");
		mainPane.getChildren().clear();
		maxX = 0;
		// update drawing
		addLines(lineageTreeRoot, mainPane);
	}

	public void updateColoring() {
		// iterate over all drawn lines and recompute their color
		ObservableList<Node> contentnodes = mainPane.getChildren();
		for (Node currentnode : contentnodes) {
			if (currentnode instanceof Line) {
				Line currline = (Line) currentnode;
				Paint lnewcolors = paintThatAppliesToCell(currentnode.getId());

				// note this is relying on using last color to set colors for
				// division lines that return null because are tagged with both
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (lnewcolors != null) {
							currline.setStroke(lnewcolors);
						} else {
							if (currline != null && currline.getId() != null) {
								if (!currline.getId().equals("time")) {
									currline.setStroke(Color.BLACK);
								}
							}
						}
					}
				});
			}
		}
	}

	private int addLines(TreeItem<String> lineageTreeRoot, Pane mainPane) {
		int x = recursiveDraw(mainPane, 400, 10, lineageTreeRoot, 10);
		// add time indicator bar
		int timevalue = time.getValue();
		timeIndicatorBar = new Line(0, iYmin + timevalue, maxX + iXmax * 2, iYmin + timevalue);
		timeIndicatorBar.setStroke(new Color(.5, .5, .5, .5));
		timeIndicatorBar.setId("time");

		// add time indicator
		timeIndicator = new Text(timeLabelOffsetX, iYmin + timevalue, Integer.toString(time.get() + timeOffset));
		timeIndicator.setFont(new Font(6));
		timeIndicator.setStroke(new Color(.5, .5, .5, .5));
		timeIndicator.setId("timeValue");
		mainPane.getChildren().add(timeIndicatorBar);
		mainPane.getChildren().add(timeIndicator);
		drawTimeTicks();
		return x;
	}

	// retrieves material for use as texture on lines
	private Paint paintThatAppliesToCell(String cellname) {
		if (cellname != null) {
			ArrayList<Color> colors = new ArrayList<Color>();
			// iterate over rulesList
			for (Rule rule : rules) {
				if (rule.appliesToCellNucleus(cellname) || rule.appliesToCellBody(cellname))
					colors.add(rule.getColor());
			}
			Collections.sort(colors, new ColorComparator());

			// translate color list to material from material cache
			if (!colors.isEmpty()) {
				PhongMaterial m = (PhongMaterial) colorHash.getMaterial(colors);
				Image i = m.getDiffuseMap();

				if (i != null) {
					ImagePattern ip = new ImagePattern(i, 0, 0, 21, 21, false);
					return ip;
				}
			}
		}
		return null;
	}

	private void drawTimeTicks() {
		for (int i = 0; i <= 400; i = i + 100) {
			Line l = new Line(0, i, 5, i);
			Text number = new Text(Integer.toString(i));
			number.setFont(new Font(6));
			number.setX(7);
			number.setY(i);
			mainPane.getChildren().add(number);
			mainPane.getChildren().add(l);
		}

		for (int i = 25; i <= 400; i = i + 25) {
			Line l = new Line(0, i, 3, i);
			mainPane.getChildren().add(l);
		}

	}

	// recursively draws each cell in Tree
	// not sure what rootstart is
	// note returns the midpoint of the sublineage just drawn
	private int recursiveDraw(Pane mainPane, int h, int x, TreeItem<String> cell, int rootStart) {
		boolean done = false;
		String cellName = cell.getValue();

		if (hiddenNodes.contains(cellName))
			done = true;

		int startTime = data.getFirstOccurrenceOf(cellName);
		int lastTime = data.getLastOccurrenceOf(cellName);
		if (startTime < 1)
			startTime = 1;
		int length = ((lastTime - startTime));

		int yStartUse = ((startTime + iYmin));
		nameYStartUseMap.put(cellName, new Integer(yStartUse));

		// compute color
		Paint lcolor = paintThatAppliesToCell(cellName);

		if (cell.isLeaf() || done) {
			if (x < iXmax)
				x = iXmax + xsc;
			// terminal case line drawn
			maxX = Math.max(x, maxX);
			Line lcell = new Line(x, yStartUse, x, yStartUse + length);
			if (lcolor != null)
				lcell.setStroke(lcolor); // first for now
			Tooltip t = new Tooltip(cellName);
			hackTooltipStartTiming(t, ttduration);
			Tooltip.install(lcell, t);
			lcell.setId(cellName);
			lcell.setOnMousePressed(handler);
			if (done) { // this is a collapsed node not a terminal cell
				// System.out.println("done rendering");
				Circle circle = new Circle(2, Color.BLACK);
				circle.relocate(x - 2, yStartUse + length - 2);
				t = new Tooltip("Expand " + cellName);
				hackTooltipStartTiming(t, ttduration);
				Tooltip.install(circle, t);
				circle.setId(cellName);
				mainPane.getChildren().add(circle);
				circle.setOnMousePressed(handler);
			}
			mainPane.getChildren().add(lcell);
			int offsetx = 2;
			int offsety = 3;
			String cellnametextstring = cellName;
			String terminalname = PartsList.getFunctionalNameByLineageName(cellName);
			if (!(terminalname == null))
				cellnametextstring = cellnametextstring + " ; " + terminalname;
			Text cellnametext = new Text(x - offsetx, yStartUse + length + offsety, cellnametextstring);
			cellnametext.getTransforms().add(new Rotate(90, x - offsetx, yStartUse + length + offsety));
			cellnametext.setFont(new Font(5));

			mainPane.getChildren().add(cellnametext);
			nameXUseMap.put(cellName, new Integer(x));
			return x;
		}

		// note left right not working here or relying on presort
		ObservableList<TreeItem<String>> childrenlist = cell.getChildren();

		TreeItem<String> cLeft = childrenlist.get(0);
		TreeItem<String> cRite = childrenlist.get(1);
		int x1 = recursiveDraw(mainPane, h, x, cLeft, rootStart);
		nameXUseMap.put(cLeft.getValue(), new Integer(x1));
		int xx = maxX + xsc;
		int x2 = recursiveDraw(mainPane, h, xx, cRite, rootStart);
		nameXUseMap.put(cRite.getValue(), new Integer(x2));

		Integer leftXUse = nameXUseMap.get(cLeft.getValue());
		Integer rightXUse = nameXUseMap.get(cRite.getValue());
		Integer leftYUse = nameYStartUseMap.get(cLeft.getValue());
		nameYStartUseMap.get(cRite.getValue());
		// division line
		Line lcell = new Line(leftXUse.intValue(), leftYUse.intValue(), rightXUse.intValue(), leftYUse.intValue());
		if (!(lcolor == null))
			lcell.setStroke(lcolor); // first for now
		// lines with child names

		lcell.setId(cellName);// set division line to parent id to aid
								// recoloring
		mainPane.getChildren().add(lcell);
		x = (x1 + x2) / 2;
		length = leftYUse.intValue() - yStartUse;

		// nonerminal case line drawn
		lcell = new Line(x, yStartUse, x, leftYUse.intValue());
		if (!(lcolor == null))
			lcell.setStroke(lcolor); // first for now

		lcell.setOnMousePressed(handler);// handler for collapse
		lcell.setId(cellName);
		Tooltip t = new Tooltip(cellName);
		hackTooltipStartTiming(t, ttduration);
		Tooltip.install(lcell, t);
		mainPane.getChildren().add(lcell);
		return x;
	}

	// stolen from web to hack these tooltips to come up faster
	public static void hackTooltipStartTiming(Tooltip tooltip, int duration) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);

			Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			fieldTimer.setAccessible(true);
			Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

			objTimer.getKeyFrames().clear();
			objTimer.getKeyFrames().add(new KeyFrame(new Duration(duration)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}