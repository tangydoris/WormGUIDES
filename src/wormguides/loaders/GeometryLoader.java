package wormguides.loaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
/*
 * Utility Class
 * Builds 3D Geometry for Scene Elements to be placed in 3D scene
 *
 * Created: Nov. 2nd, 2015
 */


public class GeometryLoader {
	
	private static final String vertexLine = "v ";
	private static final String faceLine = "f ";
	private static ArrayList<double[]> coords;
	private static ArrayList<int[]> faces;
	private static TriangleMesh mesh;
 
	public static MeshView loadOBJ(String fileName) {
		/*
		 * can't use '..' in getResouce --> instead use complete relative path from root directory. has to start with '/'
		 */
		//take path up until model
//		fileName = fileName.substring(fileName.indexOf("/model"));
//		fileName = ".." + fileName;
		
		//append /
		URL url = ProductionInfoLoader.class.getResource("/" + fileName);
		
		coords = new ArrayList<double[]>();
		faces = new ArrayList<int[]>();
		mesh = new TriangleMesh();
		
		try {
			if (url != null) {
				InputStream stream = url.openStream();
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);
				
				String line;
				
				while((line = reader.readLine()) != null) {
					//make sure valid line
					if (line.length() <= 1) break;

					//process each line in the obj file
					String lineType = line.substring(0, 2);
					if (lineType.equals(vertexLine)) {
						//process vertex lines
						String v = line.substring(2);
						double[] vertices = new double[3];
						int counter = 0;

						StringTokenizer tokenizer = new StringTokenizer(v);
						while(tokenizer.hasMoreTokens()) {
							vertices[counter++] = Double.parseDouble(tokenizer.nextToken());
						}
						//make sure good line
						if (counter == 3) {
							coords.add(vertices);
						}
					} else if (lineType.equals(faceLine)) {
						//process face lines
						String f = line.substring(2);
						int[] faceCoords = new int[3];
						int counter = 0;

						StringTokenizer tokenizer = new StringTokenizer(f);
						while(tokenizer.hasMoreTokens()) {
							faceCoords[counter++] = Integer.parseInt(tokenizer.nextToken());
						}

						if (counter == 3) {
							faces.add(faceCoords);
						}
					} else {} //ignore other cases
				}
				createMesh();
				reader.close();
			}
		} catch (IOException e1) {
			System.out.println("The file " + fileName + " wasn't found on the system.");
			return null;
		}

		return new MeshView(mesh);
	}

	
	private static void createMesh() {
		int counter = 0;
		int texCounter = 0;
		float stripeSeparation = 1500;
		
		float[] coordinates = new float[(coords.size() * 3)];
		float[] texCoords = new float[(coords.size() * 2)];
		for (int i = 0; i < coords.size(); i++) {
			for (int j = 0; j < 3; j++) {
				coordinates[counter++] = (float) coords.get(i)[j];
			}
			texCoords[texCounter++] = 0;
			texCoords[texCounter++] = ((float)coords.get(i)[0]/stripeSeparation) * 200;
		}

		mesh.getPoints().addAll(coordinates);
		mesh.getTexCoords().addAll(texCoords);		
		
		counter = 0;

		int[] faceCoords = new int[(faces.size() * 3)*2];
		for (int i = 0; i < faces.size(); i++) {
			for (int j = 0; j < 3; j++) {
				faceCoords[counter++] = faces.get(i)[j] - 1;
				faceCoords[counter++] = faces.get(i)[j] - 1; //for our texture coordinate - face syntax: p0, t0, p1, t1, p2, t2
			}
		}

		mesh.getFaces().addAll(faceCoords);
	}

	/*--------------------DEBUGGING------------------------*/
	public static void printCoords() {
		System.out.println("-----------VERTICES------------- " + coords.size());
		for (int i = 0; i < coords.size(); i++) {
			System.out.print("v ");
			for (int j = 0; j < 3; j++) {
				System.out.print(coords.get(i)[j] + ", ");
			}
			System.out.println("");
		}
	}

	public static void printFaces() {
		System.out.println("-----------FACES-------------");
		for (int i = 0; i < faces.size(); i++) {
			System.out.print("f ");
			for (int j = 0; j < 3; j++) {
				System.out.print(faces.get(i)[j] + ", ");
			}
			System.out.println("");
		}
	}
}