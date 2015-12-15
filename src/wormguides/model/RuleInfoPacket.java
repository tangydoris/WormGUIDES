package wormguides.model;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import wormguides.SearchOption;

public class RuleInfoPacket {
	private final String name;
	private Color color;
	private ArrayList<SearchOption> options;
	private boolean disableDescendantOption;
	private boolean shapeRule;
	
	
	public RuleInfoPacket() {
		this("", Color.WHITE, null, false);
	}
	
	
	public RuleInfoPacket(String name, Color color, ArrayList<SearchOption> options, boolean shapeRule) {
		this.name = name;
		this.color = color;
		this.options = options;
		this.disableDescendantOption = false;
		this.shapeRule = shapeRule;
	}
	
	
	public void disableDescendantOption() {
		disableDescendantOption = true;
	}
	
	
	public boolean isDescendantDisabled() {
		return disableDescendantOption;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public Color getColor() {
		return color;
	}
	
	
	public ArrayList<SearchOption> getOptions() {
		return options;
	}
	
	
	public void setColor(Color color) {
		if (color != null)
			this.color = color;
	}
	
	
	public void setOptions(ArrayList<SearchOption> options) {
		if (options != null)
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
	
	
	public void setCellBodySelected(boolean selected) {
		if (selected) {
			if (!options.contains(SearchOption.CELLBODY))
				options.add(SearchOption.CELLBODY);
		}
		else
			options.remove(SearchOption.CELLBODY);
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
	
	
	public boolean isCellBodySelected() {
		return options.contains(SearchOption.CELLBODY);
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
	
	public boolean isShapeRule() {
		return this.shapeRule;
	}
	
}

