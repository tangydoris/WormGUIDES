package wormguides.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import wormguides.model.LineageData;
import wormguides.model.TableLineageData;

/**
 * This loader class reads the nuclei files located in
 * wormguides.model.nuclei_files and creates a {@link TableLineageData} from the
 * data.
 * 
 * @author Doris Tang
 */
public class AceTreeLoader {

	private static int avgX, avgY, avgZ;
	private static ArrayList<String> allCellNames = new ArrayList<String>();

	public static TableLineageData loadNucFiles(int totalTimePoints) {
		TableLineageData tld = new TableLineageData(allCellNames);

		try {
			tld.addFrame(); // accounts for first tld.addFrame() added when
							// reading from JAR --> from dir name first entry
							// match
			URL url;
			for (int i = 1; i <= totalTimePoints; i++) {
				if (i < 10) {
					url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + twoZeroPad + i + ENTRY_EXT);
					if (url != null) {
						process(tld, i, url.openStream());
					} else {
						System.out.println("Could not process file: " + ENTRY_PREFIX + t + twoZeroPad + i + ENTRY_EXT);
					}
				} else if (i >= 10 && i < 100) {
					url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + oneZeroPad + i + ENTRY_EXT);
					if (url != null) {
						process(tld, i, url.openStream());
					} else {
						System.out.println("Could not process file: " + ENTRY_PREFIX + t + oneZeroPad + i + ENTRY_EXT);
					}
				} else if (i >= 100) {
					url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + i + ENTRY_EXT);
					if (url != null) {
						process(tld, i, url.openStream());
					} else {
						System.out.println("Could not process file: " + ENTRY_PREFIX + t + i + ENTRY_EXT);
					}
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// translate all cells to center around (0,0,0)
		setOriginToZero(tld, true);

		return tld;
	}

	public static int getAvgXOffsetFromZero() {
		return avgX;
	}

	public static int getAvgYOffsetFromZero() {
		return avgY;
	}

	public static int getAvgZOffsetFromZero() {
		return avgZ;
	}

	public static void setOriginToZero(LineageData lineageData, boolean defaultEmbryoFlag) {
		int totalPositions = 0;
		double sumX, sumY, sumZ;
		sumX = 0d;
		sumY = 0d;
		sumZ = 0d;

		// sum up all x-, y- and z-coordinates of nuclei
		for (int i = 0; i < lineageData.getTotalTimePoints(); i++) {
			Integer[][] positionsArray = lineageData.getPositions(i);
			for (int j = 1; j < positionsArray.length; j++) {
				sumX += positionsArray[j][X_POS_IND];
				sumY += positionsArray[j][Y_POS_IND];
				sumZ += positionsArray[j][Z_POS_IND];
				totalPositions++;
			}
		}

		// find average of x-, y- and z-coordinates
		avgX = (int) sumX / totalPositions;
		avgY = (int) sumY / totalPositions;
		avgZ = (int) sumZ / totalPositions;

		System.out.println("average nuclei position offsets from zero: " + avgX + ", " + avgY + ", " + avgZ);
		
		
		// offset all nuclei x-, y- and z- positions by x, y and z averages
		lineageData.shiftAllPositions(avgX, avgY, avgZ);
	}

	private static void process(TableLineageData tld, int time, InputStream input) throws IOException {
		tld.addFrame();

		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = new String[TOKEN_ARRAY_SIZE];
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			int k = 0;
			while (tokenizer.hasMoreTokens())
				tokens[k++] = tokenizer.nextToken().trim();

			int valid = Integer.parseInt(tokens[VALID]);
			if (valid == 1) {
				makeNucleus(tld, time, tokens);
			}
		}

		reader.close();
	}

	private static void makeNucleus(TableLineageData tld, int time, String[] tokens) {
		try {
			String name = tokens[IDENTITY];
			int x = Integer.parseInt(tokens[XCOR]);
			int y = Integer.parseInt(tokens[YCOR]);
			int z = (int) Math.round(Double.parseDouble(tokens[ZCOR]));
			int diameter = Integer.parseInt(tokens[DIAMETER]);

			tld.addNucleus(time, name, x, y, z, diameter);

		} catch (NumberFormatException nfe) {
			System.out.println("Incorrect format in nucleus file for time " + time + ".");
		}
	}

	private static final String ENTRY_PREFIX = "/wormguides/model/nuclei_files/";
	private static final String t = "t";
	private static final String ENTRY_EXT = "-nuclei";
	private static final int TOKEN_ARRAY_SIZE = 21;
	private static final int VALID = 1, XCOR = 5, YCOR = 6, ZCOR = 7, DIAMETER = 8, IDENTITY = 9;
	private final static String oneZeroPad = "0";
	private final static String twoZeroPad = "00";

	/**
	 * Indicies of the x-, y- and z-coordinates in the position Integer array
	 * for a nucleus in a time frame.
	 */
	private static final int X_POS_IND = 0, Y_POS_IND = 1, Z_POS_IND = 2;
}
