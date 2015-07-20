package wormguides.model;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

public class ColorHash extends Hashtable<ColorRule, Material> {
	
	public ColorHash() {
		super();
		
		// Defaults upon startup
		addRule("aba", Color.RED.brighter());
		addRule("abp", Color.BLUE.brighter());
		addRule("p", Color.GREEN.brighter());
		addRule("ems", Color.YELLOW.brighter());
		addRule("", Color.WHITE);
	}
	
	public void addRule(String cellName, Color color) {
		cellName = cellName.toLowerCase();
		
		// Iterate through hash to see if there is already a rule
		// for cell name
		for (ColorRule rule : keySet()) {
			// this MAY cause some issues later on with string matching
			// just the prefixes...we will see
			if (rule.getName().startsWith(cellName)) {
				//remove(rule);
				rule.addColor(color);
			}
			else
				rule = new ColorRule(cellName, color);
			
			Material material = makeMaterial(rule.getColors());
			put(rule, material);
		}
	}
	
	private Material makeMaterial(Color[] colors) {
		WritableImage wImage = new WritableImage(100, 100);
		PixelWriter writer = wImage.getPixelWriter();
		
		int segmentLength = (int) wImage.getHeight()/colors.length;
		Color color = Color.WHITE;
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < wImage.getHeight(); j++) {
				for (int k = 0; k < wImage.getWidth(); k++) {
					 if (j < (i+1)*segmentLength)
						 color = colors[i];
					 //color = Color.web(UNSELECTED_COLOR_HEX, 0.5d);
					 writer.setColor(k, j, color);
				}
			}
		}
		
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(wImage);
		return material;
	}
	
	public Material getMaterial(String cellName) {
		cellName = cellName.toLowerCase();
		for (ColorRule rule : keySet()) {
			if (rule.getName().equals(cellName))
				return get(rule);
		}
		
		Color[] white = {Color.WHITE};
		return makeMaterial(white);
	}
}
