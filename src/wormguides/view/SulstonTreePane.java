/*
 * Bao Lab 2016
 */

package wormguides.view;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import wormguides.ColorComparator;
import wormguides.ColorHash;
import wormguides.SearchOption;
import wormguides.controllers.ContextMenuController;
import wormguides.layers.SearchLayer;
import wormguides.loaders.ImageLoader;
import wormguides.models.Rule;

import acetree.lineagedata.LineageData;
import partslist.PartsList;
import search.SearchType;

public class SulstonTreePane extends ScrollPane {

    private final static int timeLabelOffsetX = 20;
    private final static int timeOffset = 19;
    private final int ZOOM_BUTTON_SIZE = 30;
    private final double DEFAULT_WINDOW_HEIGHT = 820;
    private final double DEFAULT_WINDOW_WIDTH = 775;
    private final Color ZOOM_BUTTONS_SHADOW_COLOR = Color.web("AAAAAA");
    private LineageData data;
    private HashMap<String, Integer> nameXUseMap;
    private HashMap<String, Integer> nameYStartUseMap;
    private ArrayList<String> hiddenNodes;
    private TreeItem<String> lineageTreeRoot;
    private ColorHash colorHash;
    private int maxX = 0; // global to class to keep track of current x layout
    // position
    private ObservableList<Rule> rules;
    private AnchorPane mainPane;
    private Group zoomGroup;
    // branch gap
    // seems to be some multiple of this?
    // Node content;
    private Scale scaleTransform;
    private Line timeIndicatorBar;
    private Text timeIndicator;
    private int ttduration = 0;
    private IntegerProperty time;
    private int xsc = 5;// =XScale minimal spacing between branches, inter
    private int iXmax = 25; // left margin
    private int iYmin = 19;
    private Stage contextMenuStage;
    private ContextMenuController contextMenuController;
    private StringProperty selectedNameLabeled;
    private Stage ownStage;
    private AnchorPane canvas;
    private EventHandler<MouseEvent> clickHandler;
    private boolean defaultEmbryoFlag;

    public SulstonTreePane(
            Stage ownStage, LineageData data, TreeItem<String> lineageTreeRoot,
            ObservableList<Rule> rules, ColorHash colorHash, IntegerProperty time, ContextMenuController controller,
            StringProperty selectedNameLabeled, boolean defaultEmbryoFlag) {
        super();

        this.defaultEmbryoFlag = defaultEmbryoFlag;

        clickHandler = event -> {
            String sourceName = ((Node) event.getSource()).getId();

            // right click
            if (event.getButton() == MouseButton.SECONDARY || (event.getButton() == MouseButton.PRIMARY
                    && (event.isControlDown() || event.isMetaDown()))) {
                showContextMenu(sourceName, event.getScreenX(), event.getScreenY());
            }

            // left click
            else if (event.getButton() == MouseButton.PRIMARY) {
                contextMenuStage.hide();

                resetSelectedNameLabeled(sourceName);

                if (hiddenNodes.contains(sourceName)) {
                    hiddenNodes.remove(sourceName);
                } else {
                    hiddenNodes.add(sourceName);
                }

                updateDrawing();
            }
        };

        this.ownStage = ownStage;

        canvas = new AnchorPane();
        mainPane = canvas;
        this.data = data;

        this.time = time;
        time.addListener((observable, oldValue, newValue) -> repositionTimeLine());

        hiddenNodes = new ArrayList<>();
        setUpDefaultView();

        this.rules = rules;
        this.colorHash = colorHash;
        this.lineageTreeRoot = lineageTreeRoot;

        setRulesListener();

        nameXUseMap = new HashMap<>();
        nameYStartUseMap = new HashMap<>();

        // zooming
        scaleTransform = new Scale(1.75, 1.75, 0, 0);
        Group contentGroup = new Group();

        zoomGroup = new Group();

        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(canvas);
        zoomGroup.getTransforms().add(scaleTransform);

        canvas.setVisible(true);

        this.getChildren().add(contentGroup);
        this.setPannable(true);
        this.setPrefSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);

        addLines(lineageTreeRoot, canvas);

        // add controls for zoom
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3.5);
        shadow.setOffsetX(4);
        shadow.setOffsetY(3.5);
        shadow.setColor(ZOOM_BUTTONS_SHADOW_COLOR);
        Button plus = new Button();
        plus.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        plus.setGraphic(new ImageView(ImageLoader.getPlusIcon()));
        plus.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;"
                + "-fx-background-color: transparent;");
        plus.setPrefSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        plus.setMaxSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        plus.setMinSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        plus.setEffect(shadow);

        Button minus = new Button();
        minus.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        minus.setGraphic(new ImageView(ImageLoader.getMinusIcon()));
        minus.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;"
                + "-fx-background-color: transparent;");
        minus.setPrefSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        minus.setMaxSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        minus.setMinSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        minus.setEffect(shadow);

        contentGroup.getChildren().add(plus);
        contentGroup.getChildren().add(minus);
        plus.getTransforms().add(new Translate(50, 5));
        minus.getTransforms().add(new Translate(15, 5));

        plus.setOnMousePressed(event -> {
            scaleTransform.setX(scaleTransform.getX() * 1.3333);
            scaleTransform.setY(scaleTransform.getY() * 1.3333);
        });

        minus.setOnMousePressed(event -> {
            scaleTransform.setX(scaleTransform.getX() * .75);
            scaleTransform.setY(scaleTransform.getY() * .75);
        });

        Pane yetanotherlevel = new Pane();
        yetanotherlevel.getChildren().add(contentGroup);
        this.setContent(yetanotherlevel);

        bindLocation(plus, this, yetanotherlevel);
        bindLocation(minus, this, yetanotherlevel);

        contextMenuController = controller;
        contextMenuStage = contextMenuController.getOwnStage();

        this.selectedNameLabeled = selectedNameLabeled;

        // keyboard shortcut for screenshot
        ownStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F5) {
                Stage fileChooserStage = new Stage();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Save Location");
                fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG File", "*.png"));

                WritableImage screenCapture = mainPane.snapshot(new SnapshotParameters(), null);

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
        });

        updateColoring();
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

    /**
     * Called by {@link wormguides.controllers.RootLayoutController} to resizes this scrollpane and
     * canvas to fit the window. Gets rid of extraneous space outside of the
     * tree.
     */
    public void resizeStageContents() {
        ownStage.widthProperty()
                .addListener((observableValue, oldStageWidth, newStageWidth) -> canvas.setPrefWidth(newStageWidth
                        .doubleValue()));
        canvas.setPrefWidth(ownStage.widthProperty().get());

        ownStage.heightProperty()
                .addListener((observableValue, oldStageHeight, newStageHeight) -> canvas.setPrefHeight(newStageHeight
                        .doubleValue()));
        canvas.setPrefHeight(ownStage.heightProperty().get());
    }

    private void resetSelectedNameLabeled(String name) {
        selectedNameLabeled.set("");
        selectedNameLabeled.set(name);
    }

    public void setUpDefaultView() {
        // empty lines indicate a different level of the lineage tree
        hiddenNodes.add("ABalaa");
        hiddenNodes.add("ABalap");
        hiddenNodes.add("ABalpa");
        hiddenNodes.add("ABalpp");
        hiddenNodes.add("ABaraa");
        hiddenNodes.add("ABarap");
        hiddenNodes.add("ABarpa");
        hiddenNodes.add("ABarpp");
        hiddenNodes.add("ABplaa");
        hiddenNodes.add("ABplap");
        hiddenNodes.add("ABplpa");

        hiddenNodes.add("ABplppaa");
        hiddenNodes.add("ABplpppp");

        hiddenNodes.add("ABpraa");
        hiddenNodes.add("ABprap");

        hiddenNodes.add("ABprpaaa");
        hiddenNodes.add("ABprppaa");

        hiddenNodes.add("ABprpapaa");

        hiddenNodes.add("Abprppaa");

        hiddenNodes.add("ABprppp");

        hiddenNodes.add("MSaa");
        hiddenNodes.add("MSap");
        hiddenNodes.add("MSpa");
        hiddenNodes.add("MSpp");

        hiddenNodes.add("Ea");
        hiddenNodes.add("Ep");

        hiddenNodes.add("Caa");
        hiddenNodes.add("Cap");
        hiddenNodes.add("Cpa");
        hiddenNodes.add("Cpp");

        hiddenNodes.add("D");
        hiddenNodes.add("P4");
    }

    private void showContextMenu(String name, double sceneX, double sceneY) {
        if (contextMenuStage != null) {
            contextMenuController.setName(name);

            String funcName = PartsList.getFunctionalNameByLineageName(name);

            if (funcName == null)

            {
                contextMenuController.disableTerminalCaseFunctions(true);
            } else {
                contextMenuController.disableTerminalCaseFunctions(false);
            }

            contextMenuController.setColorButtonListener(event -> {
                Rule rule = SearchLayer.addColorRule(SearchType.LINEAGE, name, Color.WHITE, SearchOption.CELL_NUCLEUS);
                rule.showEditStage(ownStage);

                contextMenuStage.hide();
            });

            contextMenuController.setColorNeighborsButtonListener(event -> {
                // call distance SearchLayer method
                Rule rule = SearchLayer.addColorRule(SearchType.NEIGHBOR, name, Color.WHITE, SearchOption.CELL_NUCLEUS);
                rule.showEditStage(ownStage);
                contextMenuStage.hide();
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
        if (defaultEmbryoFlag) {
            timeIndicator.setText(Integer.toString(time.get() + timeOffset));
        } else {
            timeIndicator.setText(Integer.toString(time.get()));
        }
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

    private void updateDrawing() {
        // clear drawing
        mainPane.getChildren().clear();
        maxX = 0;
        // update drawing
        addLines(lineageTreeRoot, mainPane);
    }

    public void updateColoring() {
        // iterate over all drawn lines and recompute their color
        ObservableList<Node> contentnodes = mainPane.getChildren();
        // note this is relying on using last color to set colors for
// division lines that return null because are tagged with both
        contentnodes.stream().filter(currentnode -> currentnode instanceof Line).forEachOrdered(currentnode -> {
            Line currline = (Line) currentnode;
            Paint lnewcolors = paintThatAppliesToCell(currentnode.getId());

            // note this is relying on using last color to set colors for
            // division lines that return null because are tagged with both
            Platform.runLater(() -> {
                if (lnewcolors != null) {
                    currline.setStroke(lnewcolors);
                } else {
                    if (currline != null && currline.getId() != null) {
                        if (!currline.getId().equals("time")) {
                            currline.setStroke(Color.BLACK);
                        }
                    }
                }
            });
        });
    }

    private void addLines(TreeItem<String> lineageTreeRoot, Pane mainPane) {
        if (lineageTreeRoot != null) {
            recursiveDraw(mainPane, 400, 10, lineageTreeRoot, 10);
        }
        // add time indicator bar
        int timevalue = time.getValue();
        timeIndicatorBar = new Line(0, iYmin + timevalue, maxX + iXmax, iYmin + timevalue);
        timeIndicatorBar.setStroke(new Color(.5, .5, .5, .5));
        timeIndicatorBar.setId("time");

        // add time indicator
        if (defaultEmbryoFlag) {
            timeIndicator = new Text(timeLabelOffsetX, iYmin + timevalue, Integer.toString(time.get() + timeOffset));
        } else {
            timeIndicator = new Text(timeLabelOffsetX, iYmin + timevalue, Integer.toString(time.get()));
        }

        timeIndicator.setFont(new Font(6));
        timeIndicator.setStroke(new Color(.5, .5, .5, .5));
        timeIndicator.setId("timeValue");
        mainPane.getChildren().add(timeIndicatorBar);
        mainPane.getChildren().add(timeIndicator);
        drawTimeTicks();
    }

    // retrieves material for use as texture on lines
    private Paint paintThatAppliesToCell(String cellname) {
        if (cellname != null) {
            ArrayList<Color> colors = new ArrayList<>();
            // iterate over rulesList
            //this occurs because the wormbase search thread hasn't finished yet
//				if (rule.getSearchType().equals(SearchType.GENE) && rule.getCells().isEmpty()) {
//
//				}
            colors.addAll(rules.stream()
                    .filter(rule -> rule.appliesToCellNucleus(cellname) || rule.appliesToCellBody(cellname))
                    .map(Rule::getColor)
                    .collect(Collectors.toList()));
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

	/*
	private void restart() {
		updateDrawing();
		updateColoring();
	}
	*/

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

        if (hiddenNodes.contains(cellName)) {
            done = true;
        }

        int startTime = data.getFirstOccurrenceOf(cellName);
        int lastTime = data.getLastOccurrenceOf(cellName);
        if (startTime < 1 && defaultEmbryoFlag) {
            startTime = 1;
        }
        int length = ((lastTime - startTime));

        int yStartUse = ((startTime + iYmin));
        nameYStartUseMap.put(cellName, yStartUse);

        // compute color
        Paint lcolor = paintThatAppliesToCell(cellName);

        if (cell.isLeaf() || done) {
            if (x < iXmax) {
                x = iXmax + xsc;
            }
            // terminal case line drawn
            maxX = Math.max(x, maxX);
            Line lcell = new Line(x, yStartUse, x, yStartUse + length);
            if (lcolor != null) {
                lcell.setStroke(lcolor); // first for now
            }
            Tooltip t = new Tooltip(cellName);
            hackTooltipStartTiming(t, ttduration);
            Tooltip.install(lcell, t);
            lcell.setId(cellName);
            lcell.setOnMousePressed(clickHandler);
            if (done) { // this is a collapsed node not a terminal cell
                // System.out.println("done rendering");
                Circle circle = new Circle(2, Color.BLACK);
                circle.relocate(x - 2, yStartUse + length - 2);
                t = new Tooltip("Expand " + cellName);
                hackTooltipStartTiming(t, ttduration);
                Tooltip.install(circle, t);
                circle.setId(cellName);
                mainPane.getChildren().add(circle);
                circle.setOnMousePressed(clickHandler);
            }
            mainPane.getChildren().add(lcell);
            int offsetx = 2;
            int offsety = 3;
            String cellnametextstring = cellName;
            String terminalname = PartsList.getFunctionalNameByLineageName(cellName);
            if (!(terminalname == null)) {
                cellnametextstring = cellnametextstring + " ; " + terminalname;
            }
            Text cellnametext = new Text(x - offsetx, yStartUse + length + offsety, cellnametextstring);
            cellnametext.getTransforms().add(new Rotate(90, x - offsetx, yStartUse + length + offsety));
            cellnametext.setFont(new Font(5));

            mainPane.getChildren().add(cellnametext);
            nameXUseMap.put(cellName, x);
            return x;
        }

        // note left right not working here or relying on presort
        ObservableList<TreeItem<String>> childrenlist = cell.getChildren();

        TreeItem<String> cLeft = childrenlist.get(0);
        TreeItem<String> cRite = childrenlist.get(1);
        int x1 = recursiveDraw(mainPane, h, x, cLeft, rootStart);
        nameXUseMap.put(cLeft.getValue(), x1);
        int xx = maxX + xsc;
        int x2 = recursiveDraw(mainPane, h, xx, cRite, rootStart);
        nameXUseMap.put(cRite.getValue(), x2);

        Integer leftXUse = nameXUseMap.get(cLeft.getValue());
        Integer rightXUse = nameXUseMap.get(cRite.getValue());
        Integer leftYUse = nameYStartUseMap.get(cLeft.getValue());
        nameYStartUseMap.get(cRite.getValue());
        // division line
        Line lcell = new Line(leftXUse, leftYUse, rightXUse, leftYUse);
        if (!(lcolor == null)) {
            lcell.setStroke(lcolor); // first for now
        }
        // lines with child names

        lcell.setId(cellName);// set division line to parent id to aid
        // recoloring
        mainPane.getChildren().add(lcell);
        x = (x1 + x2) / 2;
        length = leftYUse - yStartUse;

        // nonerminal case line drawn
        lcell = new Line(x, yStartUse, x, leftYUse);
        if (!(lcolor == null)) {
            lcell.setStroke(lcolor); // first for now
        }

        lcell.setOnMousePressed(clickHandler);// handler for collapse
        lcell.setId(cellName);
        Tooltip t = new Tooltip(cellName);
        hackTooltipStartTiming(t, ttduration);
        Tooltip.install(lcell, t);
        mainPane.getChildren().add(lcell);
        return x;
    }

    private void setRulesListener() {
        if (this.rules != null) {
            this.rules.addListener(new ListChangeListener<Rule>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Rule> change) {
                    while (change.next()) {
                        updateColoring();
                        if (change.getAddedSize() > 0) {
                            for (Rule rule : change.getAddedSubList()) {
                                rule.getRuleChangedProperty().addListener((observable, oldValue, newValue) -> {
                                    if (newValue) {
                                        updateColoring();
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
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
}