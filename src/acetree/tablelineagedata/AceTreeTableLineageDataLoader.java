/*
 * Bao Lab 2016
 */

package acetree.tablelineagedata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import acetree.LineageData;
import wormguides.resources.ProductionInfo;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.round;

/**
 * Loader that reads the nuclei files located in the same package and creates a {@link LineageData} from the data.
 * This class instantiates a {@link TableLineageData} and creates a lineage using Frame objects defined as an private
 * inner class.
 */
public class AceTreeTableLineageDataLoader {

    private static final String ENTRY_PREFIX = "/acetree/nucleifiles/";

    private static final String T = "t";

    private static final String ENTRY_EXT = "-nuclei";

    private static final int TOKEN_ARRAY_SIZE = 21;

    private static final int VALID = 1,
            XCOR = 5,
            YCOR = 6,
            ZCOR = 7,
            DIAMETER = 8,
            ID = 9;

    private static final String ONE_ZERO_PAD = "0";

    private static final String TWO_ZERO_PAD = "00";

    /** Index of the x-coordinate in the position array for a nucleus in a time frame */
    private static final int X_POS_INDEX = 0;

    /** Index of the y-coordinate in the position array for a nucleus in a time frame */
    private static final int Y_POS_INDEX = 1;

    /** Index of the z-coordinate in the position array for a nucleus in a time frame */
    private static final int Z_POS_INDEX = 2;

    private static final List<String> allCellNames = new ArrayList<>();

    private static int avgX;
    private static int avgY;
    private static int avgZ;

    public static LineageData loadNucFiles(final ProductionInfo productionInfo) {
        final TableLineageData tableLineageData = new TableLineageData(
                allCellNames,
                productionInfo.getXScale(),
                productionInfo.getYScale(),
                productionInfo.getZScale());

        try {
            // accounts for first tld.addFrame() added when reading from JAR --> from dir name first entry match
            tableLineageData.addTimeFrame();

            URL url;
            for (int i = 1; i <= productionInfo.getTotalTimePoints(); i++) {
                if (i < 10) {
                    url = AceTreeTableLineageDataLoader.class.getResource(ENTRY_PREFIX
                            + T
                            + TWO_ZERO_PAD
                            + i
                            + ENTRY_EXT);
                    if (url != null) {
                        process(tableLineageData, i, url.openStream());
                    } else {
                        System.out.println("Could find file: "
                                + ENTRY_PREFIX
                                + T
                                + TWO_ZERO_PAD
                                + i
                                + ENTRY_EXT);
                    }
                } else if (i >= 10 && i < 100) {
                    url = AceTreeTableLineageDataLoader.class.getResource(ENTRY_PREFIX
                            + T
                            + ONE_ZERO_PAD
                            + i
                            + ENTRY_EXT);
                    if (url != null) {
                        process(tableLineageData, i, url.openStream());
                    } else {
                        System.out.println("Could not find file: "
                                + ENTRY_PREFIX
                                + T
                                + ONE_ZERO_PAD
                                + i
                                + ENTRY_EXT);
                    }
                } else if (i >= 100) {
                    url = AceTreeTableLineageDataLoader.class.getResource(ENTRY_PREFIX + T + i + ENTRY_EXT);
                    if (url != null) {
                        process(tableLineageData, i, url.openStream());
                    } else {
                        System.out.println("Could not find file: "
                                + ENTRY_PREFIX
                                + T
                                + i
                                + ENTRY_EXT);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // translate all cells to center around (0,0,0)
        setOriginToZero(tableLineageData, true);

        return tableLineageData;
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

    public static void setOriginToZero(final LineageData lineageData, final boolean defaultEmbryoFlag) {
        int totalPositions = 0;
        double sumX = 0d;
        double sumY = 0d;
        double sumZ = 0d;

        // sum up all x-, y- and z-coordinates of nuclei
        for (int i = 0; i < lineageData.getNumberOfTimePoints(); i++) {
            double[][] positionsArray = lineageData.getPositions(i);
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

        System.out.println("Average nuclei position offsets from origin (0, 0, 0): ("
                + avgX
                + ", "
                + avgY
                + ", "
                + avgZ
                + ")");

        // offset all nuclei x-, y- and z- positions by x, y and z averages
        lineageData.shiftAllPositions(avgX, avgY, avgZ);
    }

    private static void process(final TableLineageData tableLineageData, final int time, final InputStream input) {
        tableLineageData.addTimeFrame();

        try {
            final InputStreamReader isr = new InputStreamReader(input);
            final BufferedReader reader = new BufferedReader(isr);

            String line;
            while ((line = reader.readLine()) != null) {
                final String[] tokens = new String[TOKEN_ARRAY_SIZE];
                final StringTokenizer tokenizer = new StringTokenizer(line, ",");
                int k = 0;
                while (tokenizer.hasMoreTokens()) {
                    tokens[k++] = tokenizer.nextToken().trim();
                }

                if (parseInt(tokens[VALID]) == 1) {
                    makeNucleus(tableLineageData, time, tokens);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in processing input stream");
        }
    }

    private static void makeNucleus(final TableLineageData tableLineageData, final int time, final String[] tokens) {
        try {
            tableLineageData.addNucleus(
                    time,
                    tokens[ID],
                    parseInt(tokens[XCOR]),
                    parseInt(tokens[YCOR]),
                    round(parseDouble(tokens[ZCOR])),
                    parseInt(tokens[DIAMETER]));
        } catch (NumberFormatException nfe) {
            System.out.println("Incorrect format in nucleus file for time " + time + ".");
        }
    }
}