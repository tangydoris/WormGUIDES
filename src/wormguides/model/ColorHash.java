package wormguides.model;

import java.util.Hashtable;
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
		
		//System.out.println("constructor done");
	}
	
	public void addRule(String cellName, Color color) {
		cellName = cellName.toLowerCase();
		System.out.println("adding rule for "+cellName);
		
		// Iterate through hash to see if there is already a rule
		// for cell name
		boolean found = false;
		for (ColorRule rule : keySet()) {
			// this MAY cause some issues later on with string matching
			// just the prefixes...we will see
			if (!cellName.isEmpty() && rule.getName().startsWith(cellName)) {
				found = true;
				//remove(rule);
				rule.addColor(color);
				System.out.println("make material for "+cellName);
				Material material = makeMaterial(rule.getColors());
				put(rule, material);
			}
		}
		
		if (!found) {
			ColorRule rule = new ColorRule(cellName, color);
			System.out.println("make material for "+cellName);
			Material material = makeMaterial(rule.getColors());
			put(rule, material);
		}
	}
	
	private Material makeMaterial(Color[] colors) {
		WritableImage wImage = new WritableImage(100, 100);
		PixelWriter writer = wImage.getPixelWriter();
		
		int segmentLength = (int) wImage.getHeight()/colors.length;
		Color color = Color.BLACK;
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
		//System.out.println("getting material for "+cellName);
		cellName = cellName.toLowerCase();
		for (ColorRule rule : keySet()) {
			// may have to change this matching later
			if (cellName.startsWith(rule.getName())) {
				return get(rule);
			}
		}
		
		Color[] white = {Color.WHITE};
		return makeMaterial(white);
	}
}
