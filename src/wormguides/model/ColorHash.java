package wormguides.model;

import java.util.HashMap;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Material;

public class ColorHash extends HashMap<ColorRule, Material> {
	
	// Prefix that was searched
	private String prefix;
	
	// Ticked boxes used to modify search
	private String[] modifiers;
		
	// Customized image for the sphere material
	private WritableImage image;
	
	public ColorHash() {
		super();
	}
	
	public void addRule(String cellName, Color color) {
		
	}
}
