package wormguides.util;

import static wormguides.loaders.ParametersLoader.loadParameters;

import java.util.HashMap;

public class Parameters {
	
	// values
	public static long WAIT_TIME_MILLI; /* wait time between consecutive frames while movie is playing */
	public static double INITIAL_ZOOM;
	public static double INITIAL_TRANSLATE_X;
	public static double INITIAL_TRANSLATE_Y;
	public static double CAMERA_INITIAL_DISTANCE;
	public static double CAMERA_NEAR_CLIP;
	public static double CAMERA_FAR_CLIP;
	public static double BILLBOARD_SCALE;
	public static double SIZE_SCALE; /* scale used for radii of spheres, multiplied with cell's radius from nuc */
	public static int UNIFORM_RADIUS; /* radius of all spheres when 'uniform size' is ticked */
	public static int LABEL_SPRITE_Y_OFFSET; /* y-offset from sprite to label for one cell entity */
	public static double DEFAULT_OTHERS_OPACITY; /* default transparency of 'other' entities on startup */
	public static double VISIBILITY_CUTOFF; /* visibility under which 'other' entities are not rendered */
	public static double SELECTABILITY_VISIBILITY_CUTOFF; /* visibility 'other' not selectable */
	
	
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
	
	public static void init() {
		HashMap<String, String> param_map = loadParameters();
		
		WAIT_TIME_MILLI = Long.parseLong(param_map.get(WAIT_TIME_MILLI_KEY));
		INITIAL_ZOOM = Double.parseDouble(param_map.get(INITIAL_ZOOM_KEY));
		INITIAL_TRANSLATE_X = Double.parseDouble(param_map.get(INITIAL_TRANSLATE_X_KEY));
		INITIAL_TRANSLATE_Y = Double.parseDouble(param_map.get(INITIAL_TRANSLATE_Y_KEY));
		CAMERA_INITIAL_DISTANCE = Double.parseDouble(param_map.get(CAMERA_INITIAL_DISTANCE_KEY));
		CAMERA_NEAR_CLIP = Double.parseDouble(param_map.get(CAMERA_NEAR_CLIP_KEY));
		CAMERA_FAR_CLIP = Double.parseDouble(param_map.get(CAMERA_FAR_CLIP_KEY));
		BILLBOARD_SCALE = Double.parseDouble(param_map.get(BILLBOARD_SCALE_KEY));
		SIZE_SCALE = Double.parseDouble(param_map.get(SIZE_SCALE_KEY));
		UNIFORM_RADIUS = Integer.parseInt(param_map.get(UNIFORM_RADIUS_KEY));
		LABEL_SPRITE_Y_OFFSET = Integer.parseInt(param_map.get(LABEL_SPRITE_Y_OFFSET_KEY));
		DEFAULT_OTHERS_OPACITY = Double.parseDouble(param_map.get(DEFAULT_OTHERS_OPACITY_KEY));
		VISIBILITY_CUTOFF = Double.parseDouble(param_map.get(VISIBILITY_CUTOFF_KEY));
		SELECTABILITY_VISIBILITY_CUTOFF = Double.parseDouble(param_map.get(SELECTABILITY_VISIBILITY_CUTOFF_KEY));		
	}
	
	public static long getWaitTimeMilli() {
		return WAIT_TIME_MILLI;
	}
	
	public static double getInitialZoom() {
		return INITIAL_ZOOM;
	}
	
	public static double getInitialTranslateX() {
		return INITIAL_TRANSLATE_X;
	}
	
	public static double getInitialTranslateY() {
		return INITIAL_TRANSLATE_Y;
	}
	
	public static double getCameraInitialDistance() {
		return CAMERA_INITIAL_DISTANCE;
	}
	
	public static double getCameraNearClip() {
		return CAMERA_NEAR_CLIP;
	}
	
	public static double getCameraFarClip() {
		return CAMERA_FAR_CLIP;
	}
	
	public static double getBillboardScale() {
		return BILLBOARD_SCALE;
	}
	
	public static double getSizeScale() {
		return SIZE_SCALE;
	}
	
	public static int getUniformRadius() {
		return UNIFORM_RADIUS;
	}
	
	public static int getLabelSpriteYOffset() {
		return LABEL_SPRITE_Y_OFFSET;
	}
	
	public static double getDefaultOthersOpacity() {
		return DEFAULT_OTHERS_OPACITY;
	}
	
	public static double getVisibilityCutoff() {
		return VISIBILITY_CUTOFF;
	}
	
	public static double getSelectabilityVisibilityCutoff() {
		return SELECTABILITY_VISIBILITY_CUTOFF;
	}	
}
