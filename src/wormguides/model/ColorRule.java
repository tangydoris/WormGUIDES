package wormguides.model;

import java.util.ArrayList;
import java.util.Arrays;

import wormguides.ImageLoader;
import wormguides.SearchOption;
import wormguides.SearchType;
import wormguides.view.AppFont;
import wormguides.view.ColorRuleEditPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Every cell has a color rule consisting of its name and the color(s)
// its cell should be
public class ColorRule {
	
	private Stage editStage;
	
	private String searchedText;
	private String searchedTextLowerCase;
	
	private ArrayList<SearchOption> options;
	private SearchType type;
	private BooleanProperty ruleChanged;
	private boolean visible;
	private Color color;
	
	private ImageView eyeIcon;
	private ImageView eyeInvertIcon;
	
	private ArrayList<String> cells;
	private boolean cellsSet;
	
	private HBox hbox = new HBox();
	private Label label = new Label();
	private Region region = new Region();
	private Button colorBtn = new Button();
	private Button editBtn = new Button();
	private Button visibleBtn = new Button();
	private Button deleteBtn = new Button();
	
	private Tooltip toolTip = new Tooltip();
	
	private RuleInfoPacket infoPacket;
	
	public ColorRule(String searched, Color color) {
		this(searched, color, new SearchOption[] {SearchOption.CELL, SearchOption.DESCENDANT});
	}
	
	public ColorRule(String searched, Color color, SearchOption...options) {
		this(searched, color, new ArrayList<SearchOption>(Arrays.asList(options)),
														SearchType.SYSTEMATIC);
	}
	
	public ColorRule(String searched, Color color, ArrayList<SearchOption> options,
																SearchType type) {
		setSearchedText(searched);
		setColor(color);
		
		cells = new ArrayList<String>();
		cellsSet = false;
		
		setOptions(options);
		
		// format UI elements
		DoubleProperty sideLength = new SimpleDoubleProperty(UI_SIDE_LENGTH);
		
		hbox.setSpacing(2);	
		label.setFont(AppFont.getFont());
		label.prefHeightProperty().bind(sideLength);
		label.setMaxWidth(150);
		label.textOverrunProperty().set(OverrunStyle.ELLIPSIS);
		resetLabel();
		
		colorBtn.prefHeightProperty().bind(sideLength);
		colorBtn.prefWidthProperty().bind(sideLength);
		colorBtn.maxHeightProperty().bind(sideLength);
		colorBtn.maxWidthProperty().bind(sideLength);
		colorBtn.minHeightProperty().bind(sideLength);
		colorBtn.minWidthProperty().bind(sideLength);
		colorBtn.setGraphicTextGap(0);
		setColorButton(color);
		
		editBtn.prefHeightProperty().bind(sideLength);
		editBtn.prefWidthProperty().bind(sideLength);
		editBtn.maxHeightProperty().bind(sideLength);
		editBtn.maxWidthProperty().bind(sideLength);
		editBtn.minHeightProperty().bind(sideLength);
		editBtn.minWidthProperty().bind(sideLength);
		editBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		editBtn.setGraphic(ImageLoader.getEditIcon());
		editBtn.setGraphicTextGap(0);
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (editStage==null) {
					editStage = new Stage();
					editStage.setScene(new Scene(
							new ColorRuleEditPane(
									infoPacket, getSubmitHandler())));
					editStage.setTitle("Edit Color Rule");
					editStage.initModality(Modality.NONE);
					editStage.setResizable(false);
				}
				ruleChanged.set(false);
				editStage.show();
			}
		});
		
		eyeIcon = ImageLoader.getEyeIcon();
		eyeInvertIcon = ImageLoader.getEyeInvertIcon();
		
		visibleBtn.prefHeightProperty().bind(sideLength);
		visibleBtn.prefWidthProperty().bind(sideLength);
		visibleBtn.maxHeightProperty().bind(sideLength);
		visibleBtn.maxWidthProperty().bind(sideLength);
		visibleBtn.minHeightProperty().bind(sideLength);
		visibleBtn.minWidthProperty().bind(sideLength);
		visibleBtn.setPadding(Insets.EMPTY);
		visibleBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		visibleBtn.setGraphic(eyeIcon);
		visibleBtn.setGraphicTextGap(0);
		visibleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ruleChanged.set(true);
				if (visible)
					visibleBtn.setGraphic(eyeInvertIcon);
				else
					visibleBtn.setGraphic(eyeIcon);
				visible = !visible;
				ruleChanged.set(false);
			}
		});
		
		deleteBtn.prefHeightProperty().bind(sideLength);
		deleteBtn.prefWidthProperty().bind(sideLength);
		deleteBtn.maxHeightProperty().bind(sideLength);
		deleteBtn.maxWidthProperty().bind(sideLength);
		deleteBtn.minHeightProperty().bind(sideLength);
		deleteBtn.minWidthProperty().bind(sideLength);
		deleteBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		deleteBtn.setGraphic(ImageLoader.getCloseIcon());
		
		toolTip.setText(toStringFull());
		label.setTooltip(toolTip);
		
		HBox.setHgrow(region, Priority.ALWAYS);
		hbox.getChildren().addAll(label, region, colorBtn, editBtn, 
									visibleBtn, deleteBtn);
		
		infoPacket = new RuleInfoPacket(searchedText, this.color, options);
		ruleChanged = new SimpleBooleanProperty(false);
		ruleChanged.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					setColorButton(infoPacket.getColor());
				}
			}
		});
		
		this.type = type;
		visible = true;
	}
	
	public void setCells(ArrayList<String> list) {
		cells = list;
		cellsSet = true;
	}
	
	public boolean areCellsSet() {
		return cellsSet;
	}
	
	public ArrayList<String> getCells() {
		return cells;
	}
	
	private void setColorButton(Color color) {
		Rectangle rect = new Rectangle(UI_SIDE_LENGTH, UI_SIDE_LENGTH, color);
		rect.setStroke(Color.LIGHTGREY);
		colorBtn.setGraphic(rect);
	}
	
	private void resetLabel() {
		label.setText(toStringFull());
	}

	public void setSearchedText(String name) {
		this.searchedText = name;
		this.searchedTextLowerCase = name.toLowerCase();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setOptions(SearchOption...options){
		setOptions(new ArrayList<SearchOption>(Arrays.asList(options)));
	}
	
	public void setOptions(ArrayList<SearchOption> options) {
		this.options = new ArrayList<SearchOption>();
		for (SearchOption option : options)
			if (option != null)
				this.options.add(option);
	}
	
	public String getSearchedText() {
		return searchedText;
	}
	
	public SearchType getSearchType() {
		return type;
	}
	
	public String getSearchedTextLowerCase() {
		return searchedTextLowerCase;
	}
	
	public Color getColor() {
		return color;
	}
	
	public HBox getHBox() {
		return hbox;
	}
	
	public Button getDeleteButton() {
		return deleteBtn;
	}
	
	public boolean isCellSelected() {
		return options.contains(SearchOption.CELL);
	}
	
	public boolean isAncestorSelected() {
		return options.contains(SearchOption.ANCESTOR);
	}
	
	public boolean isDescendantSelected() {
		return options.contains(SearchOption.DESCENDANT);
	}
	
	public SearchOption[] getOptions() {
		return options.toArray(new SearchOption[options.size()]);
	}
	
	public BooleanProperty getRuleChangedProperty() {
		return ruleChanged;
	}
	
	public String toString() {
		return searchedText;
	}
	
	public boolean equals(ColorRule other) {
		return searchedText.equals(other.getSearchedText());
	}
	
	// this tostring takes up too much horizontal space
	public String toStringFull() {
		String out = searchedText+" ";
		for (int i=0; i<options.size(); i++) {
			out += options.get(i).getDescription();
			if (i != options.size()-1)
				out += ", ";
		}
		return out;
	}
	
	public boolean appliesTo(String name) {
		if (!visible)
			return false;
		
		if (options.contains(SearchOption.CELL) && cells.contains(name))
			return true;
		
		for (String cell : cells) {
			if (options.contains(SearchOption.ANCESTOR) && LineageTree.isAncestor(name, cell))
				return true;
			if (options.contains(SearchOption.DESCENDANT) && LineageTree.isDescendant(name, cell))
				return true;
		}
		
		return false;
	}
	
	private EventHandler<ActionEvent> getSubmitHandler() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setColor(infoPacket.getColor());
				setOptions(infoPacket.getOptions().toArray(
						new SearchOption[infoPacket.getOptions().size()]));
				editStage.hide();
				setOptions(infoPacket.getOptions());
				resetLabel();
				toolTip.setText(toStringFull());
				ruleChanged.set(true);
			}
		};
	}

	// length and width of color rule ui buttons
	private static final int UI_SIDE_LENGTH = 22;
}
