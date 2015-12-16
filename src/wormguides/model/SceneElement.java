package wormguides.model;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;


public class SceneElement {
	
	private final String sceneName; //descriptor or display of object
	private ArrayList<String> cellNames; //cell names at time point i.e. cells involved in this scene
	private final String markerName; //used when neuron is separated from marker
	private final String embryoName; //used when based on specific embryo
	private final String imagingSource; //meta data
	private final String resourceLocation; //directory that contains the .obj files for this scene element. URL, JAR, path, web server
	private final int startTime;
	private final int endTime;
	private final String comments;
	private final boolean completeResourceFlag;
	private final boolean billboardFlag;
	private static final String OBJEXT = ".obj";
	

	public SceneElement(String sceneName, ArrayList<String> cellNames,
			String markerName, String imagingSource, String resourceLocation, 
			int startTime, int endTime, String comments, boolean completeResourceFlag, boolean billboardFlag) { //add EmbryoName
		
		this.sceneName = sceneName;
		this.cellNames = cellNames;
		this.markerName = markerName;
		this.embryoName = ""; //will fill this field in later?
		this.imagingSource = imagingSource;
		this.resourceLocation = resourceLocation;
		this.startTime = startTime;
		this.endTime = endTime;
		this.comments = comments;
		this.completeResourceFlag = completeResourceFlag;
		this.billboardFlag = billboardFlag;
		
		// make sure there is proper capitalization in cell names
		// specificially "Ab" instead of "AB"
		ArrayList<String> namesToRemove = new ArrayList<String>();
		for (String name : cellNames) {
			if (name.startsWith("Ab"))
				namesToRemove.add(name);
		}
		for (String name : namesToRemove) {
			cellNames.remove(name);
			name = "AB"+name.substring(2);
			cellNames.add(name);
		}
	}
	
	
	public MeshView buildGeometry(int time) {
		time++;
		// TODO OPTIMIZE THIS LATER
		GeometryLoader loader = new GeometryLoader();
		
		//check if complete resource
		if (completeResourceFlag) {
			return loader.loadOBJ(this.resourceLocation);
		} else {
			//append time and ext to resource location
			String objFile = this.resourceLocation + "_t" + time + OBJEXT;
			return loader.loadOBJ(objFile);
		}
	}
	
	
	public Text buildBillboard() {
		if (billboardFlag) {	
			//extract positions from resource location
			double x, y, z;
			StringTokenizer st = new StringTokenizer(resourceLocation);
			if (st.countTokens() == 3) {
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				z = Double.parseDouble(st.nextToken());
				Text t = new Text(20, 50, sceneName);
				t.setFont(new Font(25));
				
				//add positioning to text
				Translate tr = new Translate(x, y, z);
				t.getTransforms().add(tr);
				return t;
			} else { //incorrect position format
				return null;
			}	
		} else { //billboardflag is false, method was incorrectly called
			return null;
		}
	}
	

	public String getSceneName() {
		return sceneName;
	}
	
	
	public ArrayList<String> getAllCellNames() {		
		return cellNames;
	}
	
	
	public String getMarkerName() {
		return markerName;
	}
	
	
	public String getEmbryoName() {
		return embryoName;
	}
	
	
	public String getImagingSource() {
		return imagingSource;
	}
	
	
	public String getResourceLocation() {
		return resourceLocation;
	}

	
	public int getStartTime() {
		return startTime;
	}

	
	public int getEndTime() {
		return endTime;
	}

	
	public String getComments() {
		return comments;
	}
	
	
	public boolean getCompleteResourseFlag() {
		return completeResourceFlag;
	}
	
	
	public boolean getBillboardFlag() {
		return billboardFlag;
	}
	
}