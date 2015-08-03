package wormguides.model;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.paint.Color;
import wormguides.SearchOption;

public class RuleInfoPacket {
	private final String name;
	//private ObjectProperty<Color> colorProperty;
	private Color color;
	private ArrayList<SearchOption> options;
	
	public RuleInfoPacket() {
		this("", Color.WHITE, null);
	}
	
	public RuleInfoPacket(String name, Color color, SearchOption[] options) {
		this.name = name;
		this.color = color;
		this.options = new ArrayList<SearchOption>(Arrays.asList(options));
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	/*
	public ObjectProperty<Color> getColorProperty() {
		return colorProperty;
	}
	*/
	
	public ArrayList<SearchOption> getOptions() {
		return options;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setOptions(ArrayList<SearchOption> options) {
		this.options = options;
	}
	
	public void setCellSelected(boolean selected) {
		if (selected) {
			if (!options.contains(SearchOption.CELL))
				options.add(SearchOption.CELL);
		}
		else
			options.remove(SearchOption.CELL);
	}
	
	public void setDescendantSelected(boolean selected) {
		if (selected) {
			if (!options.contains(SearchOption.DESCENDANT))
				options.add(SearchOption.DESCENDANT);
		}
		else
			options.remove(SearchOption.DESCENDANT);
	}
	
	public void setAncestorSelected(boolean selected) {
		if (selected) {
			if (!options.contains(SearchOption.ANCESTOR))
				options.add(SearchOption.ANCESTOR);
		}
		else
			options.remove(SearchOption.ANCESTOR);
	}
	
	public boolean isCellSelected() {
		return options.contains(SearchOption.CELL);
	}
	
	public boolean isDescendantSelected() {
		return options.contains(SearchOption.DESCENDANT);
	}
	
	public boolean isAncestorSelected() {
		return options.contains(SearchOption.ANCESTOR);
	}
	
	public String toString() {
		String out = "packet info: ";
		out += getName()+" "
				+ getColor()+" ";
		for (SearchOption option : getOptions())
			out += option+" ";
		return out;
	}
}
