package wormguides.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import wormguides.ColorComparator;
import javafx.collections.ObservableList;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

public class ColorHash extends HashMap<TreeSet<Color>, Material> {
	
	private TreeSet<Color> allColors;
	
	private Material highlightMaterial = makeMaterial(Color.GOLD);
	private Material translucentMaterial = makeMaterial(Color.web("#555555", 0.40));
	
	public ColorHash(ObservableList<ColorRule> rulesList) {
		super();
		allColors = new TreeSet<Color>(new ColorComparator());
		allColors.add(Color.WHITE);
	}
	
	public void addColorToHash(Color color) {
		// add color to list if not in list already
		if (!allColors.contains(color)) {
			allColors.add(color);
			
			// add new sets of colors that are the original
			// sets with the new color appended
			ArrayList<TreeSet<Color>> newSets = new ArrayList<TreeSet<Color>>();
			for (TreeSet<Color> set : keySet()) {
				TreeSet<Color> copy = copy(set);
				copy.add(color);
				newSets.add(copy);
			}
			
			for (TreeSet<Color> set : newSets)
				put(set, makeMaterial(set.toArray(new Color[set.size()])));
			
			TreeSet<Color> soloColorSet = new TreeSet<Color>(new ColorComparator());
			soloColorSet.add(color);
			put(soloColorSet, makeMaterial(color));
		}
	}
	
	private Material makeMaterial(Color...colors) {
		WritableImage wImage = new WritableImage(240, 240);
		PixelWriter writer = wImage.getPixelWriter();
		
		
		// we want first and last color to be the same because of JavaFX material wrapping bug
		Color[] copy = new Color[colors.length+1];
		for (int i=0; i<colors.length; i++)
			copy[i]=colors[i];
		copy[colors.length]=colors[0];
		colors=copy;
		
		// for more than two colors, we want segments
		int segmentLength = (int) wImage.getHeight()/colors.length;
		Color color = Color.BLACK;
		
		for (int i = 0; i < colors.length; i++) {
			for (int j = i*segmentLength; j < (i+1)*segmentLength; j++) {
				for (int k = 0; k < wImage.getWidth(); k++) {
					 if (j < (i+1)*segmentLength)
						 color = colors[i];
					 writer.setColor(k, j, color);
				}
			}
		}
		
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(wImage);
		return material;
	}
	
	public Material getMaterial(TreeSet<Color> set) {
		return get(set);
	}
	
	public Material getHighlightMaterial() {
		return highlightMaterial;
	}
	
	public Material getTranslucentMaterial() {
		return translucentMaterial;
	}
	
	public TreeSet<Color> copy(TreeSet<Color> orig) {
		TreeSet<Color> copy = new TreeSet<Color>(new ColorComparator());
		Iterator<Color> iterator = orig.iterator();
		while (iterator.hasNext())
			copy.add(iterator.next());
		return copy;
    }
}
