package wormguides.model;
import java.util.Vector;

import javafx.scene.shape.MeshView;

public class SceneElement {
	private final String SceneName; //descriptor or display of object
	private Vector<String> cellNames; //cell names at time point i.e. cells involved in this scene
	private final String MarkerName; //used when neuron is separated from marker
	private final String EmbryoName; //used when based on specific embryo
	private final String ImagingSource; //meta data
	private final String ResourceLocation; //directory that contains the .obj files for this scene element. URL, JAR, path, web server
	private final int StartTime;
	private final int EndTime;
	private final String Comments;
	private static final String OBJEXT = ".obj";

	public SceneElement(String sceneName, Vector<String> cellNames,
			String markerName, String imagingSource, String resourceLocation, 
			int startTime, int endTime, String comments) { //add EmbryoName
		this.SceneName = sceneName;
		this.cellNames = cellNames;
		this.MarkerName = markerName;
		this.EmbryoName = ""; //will fill this field in later?
		this.ImagingSource = imagingSource;
		this.ResourceLocation = resourceLocation;
		this.StartTime = startTime;
		this.EndTime = endTime;
		this.Comments = comments;
	}
	
	public MeshView buildGeometry(int time) {
		//append time and ext to resource location
		String objFile = this.ResourceLocation + "_t" + time + OBJEXT;
		GeometryLoader loader = new GeometryLoader();

		return loader.loadOBJ(objFile);
	}

	public String getSceneName() {
		return this.SceneName;
	}
	
	public Vector<String> getAllCellNames() {
		return this.cellNames;
	}
	
	public String getMarkerName() {
		return this.MarkerName;
	}
	
	public String getEmbryoName() {
		return this.EmbryoName;
	}
	
	public String getImagingSource() {
		return this.ImagingSource;
	}
	
	public String getResourceLocation() {
		return this.ResourceLocation;
	}

	public int getStartTime() {
		return this.StartTime;
	}

	public int getEndTime() {
		return this.EndTime;
	}

	public String getComments() {
		return this.Comments;
	}
}