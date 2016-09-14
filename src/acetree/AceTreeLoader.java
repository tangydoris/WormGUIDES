/*
 * Bao Lab 2016
 */

package acetree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import acetree.lineagedata.LineageData;
import acetree.lineagedata.TableLineageData;

/**
 * Loader that reads the nuclei files located in the same package and creates a {@link LineageData} from the data.
 */
public class AceTreeLoader {

    private static final String ENTRY_PREFIX = "/acetree/nucleifiles/";
    private static final String t = "t";
    private static final String ENTRY_EXT = "-nuclei";

    private static final int TOKEN_ARRAY_SIZE = 21;
    private static final int VALID = 1, XCOR = 5, YCOR = 6, ZCOR = 7, DIAMETER = 8, IDENTITY = 9;

    private final static String oneZeroPad = "0";
    private final static String twoZeroPad = "00";

    /** Index of the x-coordinate in the position array for a nucleus in a time frame.*/
    private static final int X_POS_INDEX = 0;
    /** Index of the y-coordinate in the position array for a nucleus in a time frame.*/
    private static final int Y_POS_INDEX = 1;
    /** Index of the z-coordinate in the position array for a nucleus in a time frame.*/
    private static final int Z_POS_INDEX = 2;
    private static final ArrayList<String> allCellNames = new ArrayList<>();
    private static int avgX;
    private static int avgY;
    private static int avgZ;

    public static LineageData loadNucFiles(int totalTimePoints) {
        final LineageData lineageData = new TableLineageData(allCellNames);

        try {
            // account for first tld.addFrame() added when reading from JAR --> from dir name first entry match
            lineageData.addTimeFrame();
            URL url;

            for (int i = 1; i <= totalTimePoints; i++) {
                if (i < 10) {
                    url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + twoZeroPad + i + ENTRY_EXT);
                    if (url != null) {
                        process(lineageData, i, url.openStream());
                    } else {
                        System.out.println("Could not process file: " + ENTRY_PREFIX + t + twoZeroPad + i + ENTRY_EXT);
                    }

                } else if (i >= 10 && i < 100) {
                    url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + oneZeroPad + i + ENTRY_EXT);
                    if (url != null) {
                        process(lineageData, i, url.openStream());
                    } else {
                        System.out.println("Could not process file: " + ENTRY_PREFIX + t + oneZeroPad + i + ENTRY_EXT);
                    }

                } else if (i >= 100) {
                    url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + i + ENTRY_EXT);
                    if (url != null) {
                        process(lineageData, i, url.openStream());
                    } else {
                        System.out.println("Could not process file: " + ENTRY_PREFIX + t + i + ENTRY_EXT);
                    }
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        setOriginToZero(lineageData);
        return lineageData;
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

    public static void setOriginToZero(LineageData lineageData) {
        int totalPositions = 0;
        double sumX, sumY, sumZ;
        sumX = 0d;
        sumY = 0d;
        sumZ = 0d;

        // sum up all x-, y- and z-coordinates of nuclei
        for (int i = 0; i < lineageData.getNumberOfTimePoints(); i++) {
            Integer[][] positionsArray = lineageData.getPositions(i);
            for (int j = 1; j < positionsArray.length; j++) {
                sumX += positionsArray[j][X_POS_INDEX];
                sumY += positionsArray[j][Y_POS_INDEX];
                sumZ += positionsArray[j][Z_POS_INDEX];
                totalPositions++;
            }
        }

        // find average of x-, y- and z-coordinates
        avgX = (int) sumX / totalPositions;
        avgY = (int) sumY / totalPositions;
        avgZ = (int) sumZ / totalPositions;

        lineageData.shiftAllPositions(avgX, avgY, avgZ);
        System.out.println("Average nuclei position offsets from zero: " + avgX + ", " + avgY + ", " + avgZ);
    }

    private static void process(LineageData lineageData, final int time, final InputStream input) throws IOException {
        lineageData.addTimeFrame();

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = new String[TOKEN_ARRAY_SIZE];
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            int k = 0;
            while (tokenizer.hasMoreTokens()) {
                tokens[k++] = tokenizer.nextToken().trim();
            }

            int valid = Integer.parseInt(tokens[VALID]);
            if (valid == 1) {
                makeNucleus(lineageData, time, tokens);
            }
        }

        reader.close();
    }

    private static void makeNucleus(LineageData lineageData, final int time, final String[] tokens) {
        try {
            lineageData.addNucleus(
                    time,
                    tokens[IDENTITY],
                    Integer.parseInt(tokens[XCOR]),
                    Integer.parseInt(tokens[YCOR]),
                    (int) Math.round(Double.parseDouble(tokens[ZCOR])),
                    Integer.parseInt(tokens[DIAMETER]));

        } catch (NumberFormatException nfe) {
            System.out.println("Incorrect format in nucleus file for time " + time + ".");
        }
    }
}
