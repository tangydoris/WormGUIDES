package wormguides.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import wormguides.ColorComparator;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

public class ColorHash extends HashMap<TreeSet<Color>, Material> {
	
	private ObservableList<ColorRule> rulesList;
	private TreeSet<Color> allColors;
	
	private final Material defaultMaterial = makeMaterial(Color.WHITE);
	
	public ColorHash(ObservableList<ColorRule> rulesList) {
		super();
		
		allColors = new TreeSet<Color>(new ColorComparator());
		allColors.add(Color.WHITE);
		
		this.rulesList = rulesList;
		this.rulesList.addListener(new ListChangeListener<ColorRule>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends ColorRule> change) {
				while (change.next()) {
					for (ColorRule rule : change.getAddedSubList()) {
						// add color to list if not in list already
						if (!allColors.contains(rule.getColor())) {
							allColors.add(rule.getColor());
							
							// add new sets of colors that are the original
							// sets with the new color appended
							ArrayList<TreeSet<Color>> newSets = new ArrayList<TreeSet<Color>>();
							for (TreeSet<Color> set : keySet()) {
								TreeSet<Color> copy = copy(set);
								copy.add(rule.getColor());
								newSets.add(copy);
							}
							
							for (TreeSet<Color> set : newSets)
								put(set, makeMaterial(set.toArray(new Color[set.size()])));
							
							TreeSet<Color> soloColorSet = new TreeSet<Color>(new ColorComparator());
							soloColorSet.add(rule.getColor());
							put(soloColorSet, makeMaterial(rule.getColor()));
							
							// for debugging
							for (TreeSet<Color> set : keySet()) {
								System.out.println("color set "+set.first().toString());
							}
							System.out.println("");
						}
					}
				}
			}
		});
	}
	
	private Material makeMaterial(Color...colors) {
		WritableImage wImage = new WritableImage(240, 240);
		PixelWriter writer = wImage.getPixelWriter();
		
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
	
	public Material getDefaultMaterial() {
		return defaultMaterial;
	}
	
	public TreeSet<Color> copy(TreeSet<Color> orig) {
		TreeSet<Color> copy = new TreeSet<Color>(new ColorComparator());
		Iterator<Color> iterator = orig.iterator();
		while (iterator.hasNext())
			copy.add(iterator.next());
		return copy;
    }
}
