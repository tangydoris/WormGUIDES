/*
 * Bao Lab 2016
 */

package wormguides.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

import static java.util.Collections.sort;

/**
 * ColorHash is a number of combinations of Colors mapped to a {@link Material}. {@link
 * wormguides.controllers.Window3DController} and {@link wormguides.view.SulstonTreePane} query this class to
 * find the appropriate color striping to apply to a cell/its lineage. This class also contains a map of the material
 * to the opacity (0.0->1.0) of the least opaque color in a Material. This is used so that the "most opaque"
 * materials can be rendered first, followed by sheerer ones.
 */

public class ColorHash {

    private HashMap<List<Color>, Material> materialHash;
    private HashMap<Material, Double> opacityHash;
    private Material highlightMaterial;
    private Material translucentMaterial;
    private Material noteMaterial;

    // Used for 'others' opacity
    private HashMap<Double, Material> opacityMaterialHash;

    public ColorHash() {
        materialHash = new HashMap<>();
        opacityHash = new HashMap<>();

        opacityMaterialHash = new HashMap<>();
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

        if (sb.length() < 2) {
            sb.insert(0, "0");
        }

        for (int i = 0; i < 3; i++) {
            colorString += sb.toString();
        }

        Material material = new PhongMaterial(Color.web(colorString, opacity));

        return material;
    }

    public Material makeMaterial(Color color) {
        List<Color> colors = new ArrayList<>();
        colors.add(color);
        return makeMaterial(colors);
    }

    public Material makeMaterial(List<Color> colors) {
        sort(colors, new ColorComparator());

        // TODO exception here?
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
            System.arraycopy(temp, 0, copy, 0, colors.size());
            copy[colors.size()] = temp[0];
        }

        // Set opacity to alpha value of least opaque color
        for (Color color : copy) {
            if (color.getOpacity() < opacity) {
                opacity = color.getOpacity();
            }
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
        if (material != null) {
            return opacityHash.get(material);
        }

        return 0;
    }

    public Material getHighlightMaterial() {
        return highlightMaterial;
    }

    public Material getTranslucentMaterial() {
        return translucentMaterial;
    }

    public Material getMaterial(List<Color> colors) {
        if (colors == null) {
            colors = new ArrayList<>();
        }

        materialHash.putIfAbsent(colors, makeMaterial(colors));

        return materialHash.get(colors);
    }

}