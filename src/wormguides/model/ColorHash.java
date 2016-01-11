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

public class ColorHash {
	
	private HashMap<TreeSet<Color>, Material> materialHash;
	private HashMap<Material, Double> opacityHash;
	private Material highlightMaterial;
	private Material translucentMaterial;
	private Material defaultMaterial;
	private Material noteMaterial;
	
	// Used for 'others' opacity
	private HashMap<Double, Material> opacityMaterialHash;
	
	
	public ColorHash() {
		materialHash = new HashMap<TreeSet<Color>, Material>();
		opacityHash = new HashMap<Material, Double>();
		
		opacityMaterialHash = new HashMap<Double, Material>();
		makeOthersMaterial(1.0);
		
		highlightMaterial = makeMaterial(Color.GOLD);
		translucentMaterial = makeMaterial(Color.web("#555555", 0.40));
		defaultMaterial = makeMaterial(Color.WHITE);
		noteMaterial = makeMaterial(Color.CORNFLOWERBLUE);
	}
	
	
	public Material getNoteSceneElementMaterial() {
		return noteMaterial;
	}
	
	
	public Material getOthersMaterial(double opacity) {
		if (opacityMaterialHash.get(opacity)==null) {
			Material material = makeOthersMaterial(opacity);
			opacityMaterialHash.put(opacity, material);
			opacityHash.put(material, opacity);
		}
			
		return opacityMaterialHash.get(opacity);
	}
	
	
	// Input opacity is between 0 and 1
	public Material makeOthersMaterial(double opacity) {
		int darkness= (int) (Math.round(opacity*255));
		String colorString = "#";
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(darkness));

		if (sb.length()<2)
			sb.insert(0, "0");

		for (int i=0; i<3; i++)
			colorString += sb.toString();
		
		Material material = new PhongMaterial(Color.web(colorString, opacity));
		
		return material;
	}
	
	
	public Material makeMaterial(Color...colors) {
		TreeSet<Color> colorSet = new TreeSet<Color>(new ColorComparator());
		for (Color color : colors)
			colorSet.add(color);
		return makeMaterial(colorSet);
	}
	
	
	public Material makeMaterial(TreeSet<Color> colors) {
		WritableImage wImage = new WritableImage(200, 200);
		PixelWriter writer = wImage.getPixelWriter();
		Color[] temp = colors.toArray(new Color[colors.size()]);
		double opacity = 1.0;
		
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
		
		// Set opacity to alpha value of least opaque color
		for (Color color : copy) {
			if (color.getOpacity()<opacity)
				opacity = color.getOpacity();
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
		opacityHash.put(material, opacity);
		
		return material;
	}
	
	
	public Material getMaterial(TreeSet<Color> colorSet) {
		if (colorSet==null)
			colorSet = new TreeSet<Color>();
		
		if (materialHash.get(colorSet)==null)
			materialHash.put(colorSet, makeMaterial(colorSet));
		
		return materialHash.get(colorSet);
	}
	
	
	public double getMaterialOpacity(Material material) {
		return opacityHash.get(material);
	}
	
	
	public Material getHighlightMaterial() {
		return highlightMaterial;
	}
	
	
	public Material getTranslucentMaterial() {
		return translucentMaterial;
	}

}
