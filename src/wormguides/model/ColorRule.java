package wormguides.model;

import java.util.ArrayList;

import wormguides.ImageLoader;
import wormguides.Layers;
import wormguides.Search;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
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
		DoubleProperty height = new SimpleDoubleProperty(HEIGHT);
		
		hbox.setSpacing(2);	
		label.setFont(new Font(14));
		label.setText(toString());
		
		label.prefHeightProperty().bind(height);
		
		colorBtn.prefHeightProperty().bind(height);
		colorBtn.prefWidthProperty().bind(height);
		colorBtn.maxHeightProperty().bind(height);
		colorBtn.maxWidthProperty().bind(height);
		colorBtn.minHeightProperty().bind(height);
		colorBtn.minWidthProperty().bind(height);
		colorBtn.setGraphicTextGap(0);
		colorBtn.setGraphic(new Rectangle(HEIGHT, HEIGHT, color));
		colorBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("color button pressed");
			}
		});
		
		editBtn.prefHeightProperty().bind(height);
		editBtn.prefWidthProperty().bind(height);
		editBtn.maxHeightProperty().bind(height);
		editBtn.maxWidthProperty().bind(height);
		editBtn.minHeightProperty().bind(height);
		editBtn.minWidthProperty().bind(height);
		editBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		editBtn.setGraphic(ImageLoader.getEditIcon());
		editBtn.setGraphicTextGap(0);
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("edit button pressed");
			}
		});
		
		visibleBtn.prefHeightProperty().bind(height);
		visibleBtn.prefWidthProperty().bind(height);
		visibleBtn.maxHeightProperty().bind(height);
		visibleBtn.maxWidthProperty().bind(height);
		visibleBtn.minHeightProperty().bind(height);
		visibleBtn.minWidthProperty().bind(height);
		visibleBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		visibleBtn.setGraphic(ImageLoader.getEyeIcon());
		visibleBtn.setGraphicTextGap(0);
		visibleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("visible button pressed");
			}
		});
		
		deleteBtn.prefHeightProperty().bind(height);
		deleteBtn.prefWidthProperty().bind(height);
		deleteBtn.maxHeightProperty().bind(height);
		deleteBtn.maxWidthProperty().bind(height);
		deleteBtn.minHeightProperty().bind(height);
		deleteBtn.minWidthProperty().bind(height);
		deleteBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		deleteBtn.setGraphic(ImageLoader.getCloseIcon());
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("delete button pressed");
			}
		});
		
		HBox.setHgrow(region, Priority.ALWAYS);
		hbox.getChildren().addAll(label, region, colorBtn, editBtn, visibleBtn, deleteBtn);
		
		System.out.println("made colorrule for "+toStringFull());
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
	
	public Search.Option[] getOptions() {
		return options.toArray(new Search.Option[options.size()]);
	}
	
	public String toString() {
		return cellName;
	}
	
	// this tostring takes up too much horizontal space
	public String toStringFull() {
		String out = cellName+", ";
		for (int i=0; i<options.size(); i++) {
			out += options.get(i).getDescription();
			if (i != options.size()-1)
				out += ", ";
		}
		return out;
	}

	private static final int HEIGHT = 22;
}
