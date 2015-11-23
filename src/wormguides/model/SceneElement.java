package wormguides.model;
import java.util.StringTokenizer;
import java.util.Vector;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;

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
	private final boolean CompleteResourceFlag;
	private final boolean BillboardFlag;
	private static final String OBJEXT = ".obj";
	
	//private ArrayList<JarEntry> objEntries;

	public SceneElement(String sceneName, Vector<String> cellNames,
			String markerName, String imagingSource, String resourceLocation, 
			int startTime, int endTime, String comments, boolean completeResourceFlag, boolean billboardFlag) { //add EmbryoName
		//this.objEntries = objEntries;
		
		this.SceneName = sceneName;
		this.cellNames = cellNames;
		this.MarkerName = markerName;
		this.EmbryoName = ""; //will fill this field in later?
		this.ImagingSource = imagingSource;
		this.ResourceLocation = resourceLocation;
		this.StartTime = startTime;
		this.EndTime = endTime;
		this.Comments = comments;
		this.CompleteResourceFlag = completeResourceFlag;
		this.BillboardFlag = billboardFlag;
	}
	
	public MeshView buildGeometry(int time) {
		// TODO OPTIMIZE THIS LATER
		GeometryLoader loader = new GeometryLoader();
		
		//check if complete resource
		if (CompleteResourceFlag) {
			return loader.loadOBJ(this.ResourceLocation);
		} else {
			//append time and ext to resource location
			String objFile = this.ResourceLocation + "_t" + time + OBJEXT;
			return loader.loadOBJ(objFile);
		}
		
	}
	
	public Text buildBillboard() {
		if (BillboardFlag) {	
			//extract positions from resource location
			double x, y, z;
			StringTokenizer st = new StringTokenizer(ResourceLocation);
			if (st.countTokens() == 3) {
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				z = Double.parseDouble(st.nextToken());
				Text t = new Text(20, 50, SceneName);
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
	
	public boolean getCompleteResourseFlag() {
		return this.CompleteResourceFlag;
	}
	
	public boolean getBillboardFlag() {
		return this.BillboardFlag;
	}
}