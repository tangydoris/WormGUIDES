package wormguides;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/**
 * ColorHash is a number of combinations of Colors mapped to a {@link Material}.
 * {@link Window3DController} and {@link SulstonTreePane} query this class to
 * find the appropriate color striping to apply to a cell/its lineage. This
 * class also contains a map of the material to the opacity (0.0->1.0) of the
 * least opaque color in a Material. This is used so that the "most opaque"
 * materials can be rendered first, followed by sheerer ones.
 */

public class ColorHash {

	private HashMap<ArrayList<Color>, Material> materialHash;
	private HashMap<Material, Double> opacityHash;
	private Material highlightMaterial;
	private Material translucentMaterial;
	private Material noteMaterial;

	// Used for 'others' opacity
	private HashMap<Double, Material> opacityMaterialHash;

	public ColorHash() {
		materialHash = new HashMap<ArrayList<Color>, Material>();
		opacityHash = new HashMap<Material, Double>();

		opacityMaterialHash = new HashMap<Double, Material>();
		makeOthersMaterial(1.0);

		highlightMaterial = makeMaterial(Color.GOLD);
		translucentMaterial = makeMaterial(Color.web("#555555", 0.40));
		makeMaterial(Color.WHITE);
		noteMaterial = makeMaterial(Color.web("#749bc9"));
	}

	public Material getNoteSceneElementMaterial() {
		return noteMaterial;
	}

	public Material getOthersMaterial(double opacity) {
		if (opacityMaterialHash.get(opacity) == null) {
			Material material = makeOthersMaterial(opacity);
			opacityMaterialHash.put(opacity, material);
			opacityHash.put(material, opacity);
		}

		return opacityMaterialHash.get(opacity);
	}

	// Input opacity is between 0 and 1
	public Material makeOthersMaterial(double opacity) {
		int darkness = (int) (Math.round(opacity * 255));
		String colorString = "#";
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(darkness));

		if (sb.length() < 2)
			sb.insert(0, "0");

		for (int i = 0; i < 3; i++)
			colorString += sb.toString();

		Material material = new PhongMaterial(Color.web(colorString, opacity));

		return material;
	}

	public Material makeMaterial(Color color) {
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(color);
		return makeMaterial(colors);
	}

	public Material makeMaterial(ArrayList<Color> colors) {
		Collections.sort(colors, new ColorComparator());

		// TODO
		WritableImage wImage = new WritableImage(90, 90);
		PixelWriter writer = wImage.getPixelWriter();
		Color[] temp = colors.toArray(new Color[colors.size()]);
		double opacity = 1.0;

		Color[] copy;
		if (colors.isEmpty()) {
			copy = new Color[1];
			copy[0] = Color.WHITE;
		} else if (colors.size() == 1) {
			copy = new Color[1];
			copy[0] = colors.get(0);
		} else {
			// we want first and last color to be the same because of JavaFX
			// material wrapping bug
			copy = new Color[colors.size() + 1];
			for (int i = 0; i < colors.size(); i++)
				copy[i] = temp[i];
			copy[colors.size()] = temp[0];
		}

		// Set opacity to alpha value of least opaque color
		for (Color color : copy) {
			if (color.getOpacity() < opacity)
				opacity = color.getOpacity();
		}

		// for more than two colors, we want segments
		int segmentLength = (int) (wImage.getHeight() / copy.length);
		Color color = Color.BLACK;

		for (int i = 0; i < copy.length; i++) {
			color = copy[i];
			for (int j = i * segmentLength; j < (i + 1) * segmentLength; j++) {
				for (int k = 0; k < wImage.getWidth(); k++) {
					writer.setColor(k, j, color);
				}
			}
		}

		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(wImage);
		opacityHash.put(material, opacity);

		return material;
	}

	public double getMaterialOpacity(Material material) {
		if (material != null)
			return opacityHash.get(material);

		return 0;
	}

	public Material getHighlightMaterial() {
		return highlightMaterial;
	}

	public Material getTranslucentMaterial() {
		return translucentMaterial;
	}

	public Material getMaterial(ArrayList<Color> colors) {
		if (colors == null)
			colors = new ArrayList<Color>();

		if (materialHash.get(colors) == null)
			materialHash.put(colors, makeMaterial(colors));

		return materialHash.get(colors);
	}

}
