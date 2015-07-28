package wormguides.model;

import java.util.ArrayList;

import wormguides.Search;
import javafx.scene.paint.Color;

// Every cell has a color rule consisting of its name and the color(s)
// its cell should be
public class ColorRule {
	
	private String cellName;
	private String cellNameLowerCase;
	private ArrayList<Search.Option> options;
	private Color color;
	
	public ColorRule() {
		this("", Color.WHITE, Search.Option.CELL);
	}
	
	public ColorRule(String cellName, Color color, Search.Option...options) {
		System.out.println("making colorrule for "+cellName);
		setCellName(cellName);
		setColor(color);
		setOptions(options);
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
	
	public Search.Option[] getOptions() {
		return options.toArray(new Search.Option[options.size()]);
	}
	
	public String toString() {
		String out = cellName+", ";
		for (int i=0; i<options.size(); i++) {
			out += options.get(i).getDescription();
			if (i != options.size()-1)
				out += ", ";
		}
		return out;
	}

}
