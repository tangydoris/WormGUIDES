package wormguides.model;

import java.util.ArrayList;

import wormguides.ImageLoader;
import wormguides.Search;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

// Every cell has a color rule consisting of its name and the color(s)
// its cell should be
public class ColorRule {
	
	private String cellName;
	private String cellNameLowerCase;
	private ArrayList<Search.Option> options;
	private Color color;
	
	private HBox hbox = new HBox();
	private Label label = new Label();
	private Region region = new Region();
	private Button colorBtn = new Button();
	private Button editBtn = new Button();
	private Button visibleBtn = new Button();
	private Button deleteBtn = new Button();
	
	public ColorRule() {
		this("", Color.WHITE, Search.Option.CELL);
	}
	
	public ColorRule(String cellName, Color color, ArrayList<Search.Option> options) {
		this(cellName, color, options.toArray(new Search.Option[options.size()]));
	}
	
	public ColorRule(String cellName, Color color, Search.Option...options) {
		setCellName(cellName);
		setColor(color);
		setOptions(options);
		
		// format UI elements
		DoubleProperty sideLength = new SimpleDoubleProperty(UI_SIDE_LENGTH);
		
		hbox.setSpacing(2);	
		label.setFont(new Font(14));
		label.setText(toStringFull());
		label.prefHeightProperty().bind(sideLength);
		label.setMaxWidth(180);
		label.textOverrunProperty().set(OverrunStyle.ELLIPSIS);
		
		colorBtn.prefHeightProperty().bind(sideLength);
		colorBtn.prefWidthProperty().bind(sideLength);
		colorBtn.maxHeightProperty().bind(sideLength);
		colorBtn.maxWidthProperty().bind(sideLength);
		colorBtn.minHeightProperty().bind(sideLength);
		colorBtn.minWidthProperty().bind(sideLength);
		colorBtn.setGraphicTextGap(0);
		colorBtn.setGraphic(new Rectangle(UI_SIDE_LENGTH, UI_SIDE_LENGTH, color));
		
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
				System.out.println("edit button pressed");
			}
		});
		
		visibleBtn.prefHeightProperty().bind(sideLength);
		visibleBtn.prefWidthProperty().bind(sideLength);
		visibleBtn.maxHeightProperty().bind(sideLength);
		visibleBtn.maxWidthProperty().bind(sideLength);
		visibleBtn.minHeightProperty().bind(sideLength);
		visibleBtn.minWidthProperty().bind(sideLength);
		visibleBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		visibleBtn.setGraphic(ImageLoader.getEyeIcon());
		visibleBtn.setGraphicTextGap(0);
		visibleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("visible button pressed");
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
		
		HBox.setHgrow(region, Priority.ALWAYS);
		hbox.getChildren().addAll(label, region, colorBtn, editBtn, 
									visibleBtn, deleteBtn);
		
		//System.out.println("made colorrule for "+toStringFull());
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
		this.cellNameLowerCase = cellName.toLowerCase();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setOptions(Search.Option...options){
		this.options = new ArrayList<Search.Option>();
		for (Search.Option option : options)
			if (!this.options.contains(option))
				this.options.add(option);
	}
	
	public String getName() {
		return cellName;
	}
	
	public String getNameLowerCase() {
		return cellNameLowerCase;
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
	
	public Search.Option[] getOptions() {
		return options.toArray(new Search.Option[options.size()]);
	}
	
	public String toString() {
		return cellName;
	}
	
	public boolean equals(ColorRule other) {
		return cellName.equals(other.getName());
	}
	
	// this tostring takes up too much horizontal space
	public String toStringFull() {
		String out = cellName+" ";
		for (int i=0; i<options.size(); i++) {
			out += options.get(i).getDescription();
			if (i != options.size()-1)
				out += ", ";
		}
		return out;
	}

	// length and width of color rule ui buttons
	private static final int UI_SIDE_LENGTH = 22;
}
