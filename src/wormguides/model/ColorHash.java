package wormguides.model;

import java.util.HashMap;
import java.util.TreeSet;

import wormguides.ColorComparator;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/*
 * Hash of a combination of Colors mapped to a Material
 */

@SuppressWarnings("serial")
public class ColorHash extends HashMap<TreeSet<Color>, Material> {
	
	private Material highlightMaterial = makeMaterial(Color.GOLD);
	private Material translucentMaterial = makeMaterial(Color.web("#555555", 0.40));
	private Material defaultMaterial = makeMaterial(Color.WHITE);
	
	public ColorHash() {
		super();
	}
	
	public static Material makeMaterial(Color...colors) {
		TreeSet<Color> colorSet = new TreeSet<Color>(new ColorComparator());
		for (Color color : colors)
			colorSet.add(color);
		return makeMaterial(colorSet);
	}
	
	public static Material makeMaterial(TreeSet<Color> colors) {
		WritableImage wImage = new WritableImage(200, 200);
		PixelWriter writer = wImage.getPixelWriter();
		Color[] temp = colors.toArray(new Color[colors.size()]);
		
		Color[] copy;
		if (colors.isEmpty()) {
			copy = new Color[1];
			copy[0] = Color.WHITE;
		}
		else if (colors.size()==1) {
			copy = new Color[1];
			copy[0] = colors.first();
		}
		else {
			// we want first and last color to be the same because of JavaFX material wrapping bug
			copy = new Color[colors.size()+1];
			for (int i=0; i<colors.size(); i++)
				copy[i]=temp[i];
			copy[colors.size()]=temp[0];
		}
		
		// for more than two colors, we want segments
		int segmentLength = (int) wImage.getHeight()/copy.length;
		Color color = Color.BLACK;
		
		for (int i = 0; i < copy.length; i++) {
			for (int j = i*segmentLength; j < (i+1)*segmentLength; j++) {
				for (int k = 0; k < wImage.getWidth(); k++) {
					 if (j < (i+1)*segmentLength)
						 color = copy[i];
					 
					 writer.setColor(k, j, color);
				}
			}
		}
		
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(wImage);
		
		return material;
	}
	
	public Material getMaterial(TreeSet<Color> colorSet) {
		if (colorSet==null)
			colorSet = new TreeSet<Color>();
		
		if (get(colorSet)==null)
			put(colorSet, makeMaterial(colorSet));
		
		return get(colorSet);
	}
	
	public Material getHighlightMaterial() {
		return highlightMaterial;
	}
	
	public Material getTranslucentMaterial() {
		return translucentMaterial;
	}

}
