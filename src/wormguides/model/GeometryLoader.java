package wormguides.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
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

	public GeometryLoader() {
		coords = new ArrayList<double[]>();
		faces = new ArrayList<int[]>();
		mesh = new TriangleMesh();
	}

	public MeshView loadOBJ(String fileName) {
		File obj = new File(fileName);
		try {
			Scanner scanner = new Scanner(obj);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();

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
			scanner.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("The file " + fileName + " wasn't found on the system.");
			//e.printStackTrace(); 
			return null;
		}
		return new MeshView(mesh);
	}

	public void createMesh() {
		int counter = 0;

		float[] coordinates = new float[(coords.size() * 3)];
		for (int i = 0; i < coords.size(); i++) {
			for (int j = 0; j < 3; j++) {
				coordinates[counter++] = (float) coords.get(i)[j];
			}
		}

		mesh.getPoints().addAll(coordinates);

		counter = 0;

		int[] faceCoords = new int[(faces.size() * 3)*2];
		for (int i = 0; i < faces.size(); i++) {
			for (int j = 0; j < 3; j++) {
				faceCoords[counter++] = faces.get(i)[j] - 1;
				faceCoords[counter++] = 0; //for our texture coordinate - face syntax: p0, t0, p1, t1, p2, t2
			}
		}

		mesh.getFaces().addAll(faceCoords);

		//null tex coords
		mesh.getTexCoords().addAll(0,0);
	}

	/*--------------------DEBUGGING------------------------*/
	public void printCoords() {
		System.out.println("-----------VERTICES------------- " + coords.size());
		for (int i = 0; i < coords.size(); i++) {
			System.out.print("v ");
			for (int j = 0; j < 3; j++) {
				System.out.print(coords.get(i)[j] + ", ");
			}
			System.out.println("");
		}
	}

	public void printFaces() {
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