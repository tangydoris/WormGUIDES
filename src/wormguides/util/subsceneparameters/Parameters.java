/*
 * Bao Lab 2017
 */

package wormguides.util.subsceneparameters;

import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import static wormguides.loaders.ParametersLoader.PARAMETERS_FILE_PATH;
import static wormguides.loaders.ParametersLoader.loadParameters;

public class Parameters {

    // keys
    private final static String WAIT_TIME_MILLI_KEY = "WAIT_TIME_MILLI";
    private final static String INITIAL_ZOOM_KEY = "INITIAL_ZOOM";
    private final static String INITIAL_TRANSLATE_X_KEY = "INITIAL_TRANSLATE_X";
    private final static String INITIAL_TRANSLATE_Y_KEY = "INITIAL_TRANSLATE_Y";
    private final static String CAMERA_INITIAL_DISTANCE_KEY = "CAMERA_INITIAL_DISTANCE";
    private final static String CAMERA_NEAR_CLIP_KEY = "CAMERA_NEAR_CLIP";
    private final static String CAMERA_FAR_CLIP_KEY = "CAMERA_FAR_CLIP";
    private final static String BILLBOARD_SCALE_KEY = "BILLBOARD_SCALE";
    private final static String SIZE_SCALE_KEY = "SIZE_SCALE";
    private final static String UNIFORM_RADIUS_KEY = "UNIFORM_RADIUS";
    private final static String LABEL_SPRITE_Y_OFFSET_KEY = "LABEL_SPRITE_Y_OFFSET";
    private final static String DEFAULT_OTHERS_OPACITY_KEY = "DEFAULT_OTHERS_OPACITY";
    private final static String VISIBILITY_CUTOFF_KEY = "VISIBILITY_CUTOFF";
    private final static String SELECTABILITY_VISIBILITY_CUTOFF_KEY = "SELECTABILITY_VISIBILITY_CUTOFF";

    // values
    private static long waitTimeMilli; /* wait time between consecutive frames while movie is playing */
    private static double initialZoom;
    private static double initialTranslateX;
    private static double initialTranslateY;
    private static double cameraInitialDistance;
    private static double cameraNearClip;
    private static double cameraFarClip;
    private static double billboardScale;
    private static double sizeScale; /* scale used for radii of spheres, multiplied with cell's radius from nuc */
    private static int uniformRadius; /* radius of all spheres when 'uniform size' is ticked */
    private static int labelSpriteYOffset; /* y-offset from sprite to label for one cell entity */
    private static double defaultOthersOpacity; /* default transparency of 'other' entities on startup */
    private static double visibilityCutoff; /* visibility under which 'other' entities are not rendered */
    private static double selectabilityVisibilityCutoff; /* visibility 'other' not selectable */

    public static void init() {
        final Map<String, String> param_map = loadParameters();

        try {
            waitTimeMilli = parseLong(param_map.get(WAIT_TIME_MILLI_KEY));
            initialZoom = parseDouble(param_map.get(INITIAL_ZOOM_KEY));
            initialTranslateX = parseDouble(param_map.get(INITIAL_TRANSLATE_X_KEY));
            initialTranslateY = parseDouble(param_map.get(INITIAL_TRANSLATE_Y_KEY));
            cameraInitialDistance = parseDouble(param_map.get(CAMERA_INITIAL_DISTANCE_KEY));
            cameraNearClip = parseDouble(param_map.get(CAMERA_NEAR_CLIP_KEY));
            cameraFarClip = parseDouble(param_map.get(CAMERA_FAR_CLIP_KEY));
            billboardScale = parseDouble(param_map.get(BILLBOARD_SCALE_KEY));
            sizeScale = parseDouble(param_map.get(SIZE_SCALE_KEY));
            uniformRadius = parseInt(param_map.get(UNIFORM_RADIUS_KEY));
            labelSpriteYOffset = parseInt(param_map.get(LABEL_SPRITE_Y_OFFSET_KEY));
            defaultOthersOpacity = parseDouble(param_map.get(DEFAULT_OTHERS_OPACITY_KEY));
            visibilityCutoff = parseDouble(param_map.get(VISIBILITY_CUTOFF_KEY));
            selectabilityVisibilityCutoff = parseDouble(param_map.get(SELECTABILITY_VISIBILITY_CUTOFF_KEY));

        } catch (Exception e) {
            System.out.println("Error in parsing parameters file at " + PARAMETERS_FILE_PATH);
            e.printStackTrace();
        }
    }

    public static long getWaitTimeMilli() {
        return waitTimeMilli;
    }

    public static double getInitialZoom() {
        return initialZoom;
    }

    public static double getInitialTranslateX() {
        return initialTranslateX;
    }

    public static double getInitialTranslateY() {
        return initialTranslateY;
    }

    public static double getCameraInitialDistance() {
        return cameraInitialDistance;
    }

    public static double getCameraNearClip() {
        return cameraNearClip;
    }

    public static double getCameraFarClip() {
        return cameraFarClip;
    }

    public static double getBillboardScale() {
        return billboardScale;
    }

    public static double getSizeScale() {
        return sizeScale;
    }

    public static int getUniformRadius() {
        return uniformRadius;
    }

    public static int getLabelSpriteYOffset() {
        return labelSpriteYOffset;
    }

    public static double getDefaultOthersOpacity() {
        return defaultOthersOpacity;
    }

    public static double getVisibilityCutoff() {
        return visibilityCutoff;
    }

    public static double getSelectabilityVisibilityCutoff() {
        return selectabilityVisibilityCutoff;
    }
}
