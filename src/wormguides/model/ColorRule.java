package wormguides.model;

import java.util.ArrayList;
import javafx.scene.paint.Color;

// Every cell has a color rule consisting of its name and the color(s)
// its cell should be
public class ColorRule {
	
	private String cellName;
	private ArrayList<Color> colors;
	
	public ColorRule(String cellName, Color color) {
		this.cellName = cellName.toLowerCase();
		colors = new ArrayList<Color>();
		colors.add(color);
	}
	
	public ColorRule(String cellName, Color[] colors) {
		this.cellName = cellName.toLowerCase();
		this.colors = new ArrayList<Color>();
		for (int i = 0; i < colors.length; i ++)
			this.colors.add(colors[i]);
	}
	
	public void addColor(Color color) {
		colors.add(color);
	}
	
	public void addColor(Color[] colors) {
		for (int i = 0; i < colors.length; i++) {
			if (!this.colors.contains(colors[i]))
				this.colors.add(colors[i]);
		}
	}
	
	public String getName() {
		return cellName;
	}
	
	public Color[] getColors() {
		return colors.toArray(new Color[colors.size()]);
	}
	
	public String toString() {
		return cellName+" "+colorsToString();
	}
	
	private String colorsToString() {
		String out = "";
		for (int i = 0; i < colors.size(); i++)
			out += colors.get(i).toString()+" ";
		return out;
	}
}
