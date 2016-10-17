/*
 * Bao Lab 2016
 */

package wormguides.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Syntax rules for config file:
 * <p>
 * Replace ',' with ';' to support StringTokenizer with ',' delimeter (.csv file)
 */
public class ProductionInfoLoader {

    private static final int NUMBER_OF_FIELDS = 14;

    private static final String PRODUCTION_INFO_FILE_PATH = "/wormguides/model/production_info_file/"
            + "Production_Info.csv";

    private static final String PRODUCT_INFO_LINE = "Production Information,,,,,,,,,,,,,";

    private static final String HEADER_LINE = "Cells,Image Series,Marker,Strain,Compressed Embryo?,Temporal "
            + "Resolution,Segmentation,cytoshow link,Movie start time (min),isSulstonMode?,Total Time Points,X_SCALE,"
            + "Y_SCALE,Z_SCALE";

    /**
     * Tokenizes each line in the config file and creates a 2D array of the file
     *
     * @return the 2D array
     */
    public static List<List<String>> buildProductionInfo() {
        final URL url = ProductionInfoLoader.class
                .getResource("/wormguides/models/production_info_file/Production_Info.csv");

        List<List<String>> productionInfo = new ArrayList<>();
        List<String> cells = new ArrayList<>();
        List<String> imageSeries = new ArrayList<>();
        List<String> markers = new ArrayList<>();
        List<String> strains = new ArrayList<>();
        List<String> compressedEmbryo = new ArrayList<>();
        List<String> temporalResolutions = new ArrayList<>();
        List<String> segmentations = new ArrayList<>();
        List<String> cytoshowLinks = new ArrayList<>();
        List<String> movieStartTime = new ArrayList<>();
        List<String> isSulston = new ArrayList<>();
        List<String> totalTimePoints = new ArrayList<>();
        List<String> xScale = new ArrayList<>();
        List<String> yScale = new ArrayList<>();
        List<String> zScale = new ArrayList<>();

        try (InputStream stream = url.openStream();
             InputStreamReader streamReader = new InputStreamReader(stream);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;

            while ((line = reader.readLine()) != null) {
                // skip product info line and header line
                if (line.equals(PRODUCT_INFO_LINE)) {
                    line = reader.readLine();

                    if (line.equals(HEADER_LINE)) {
                        line = reader.readLine();
                    }

                    if (line == null) {
                        break;
                    }
                }

                // make sure valid line
                if (line.length() <= 1) {
                    break;
                }

                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                // check if valid line
                if (tokenizer.countTokens() == NUMBER_OF_FIELDS) {
                    cells.add(tokenizer.nextToken());
                    imageSeries.add(tokenizer.nextToken());
                    markers.add(tokenizer.nextToken());
                    strains.add(tokenizer.nextToken());
                    compressedEmbryo.add(tokenizer.nextToken());
                    temporalResolutions.add(tokenizer.nextToken());
                    segmentations.add(tokenizer.nextToken());
                    cytoshowLinks.add(tokenizer.nextToken());
                    movieStartTime.add(tokenizer.nextToken());
                    isSulston.add(tokenizer.nextToken());
                    totalTimePoints.add(tokenizer.nextToken());
                    xScale.add(tokenizer.nextToken());
                    yScale.add(tokenizer.nextToken());
                    zScale.add(tokenizer.nextToken());
                }
            }

            // add array lists
            productionInfo.add(cells);
            productionInfo.add(imageSeries);
            productionInfo.add(markers);
            productionInfo.add(strains);
            productionInfo.add(compressedEmbryo);
            productionInfo.add(temporalResolutions);
            productionInfo.add(segmentations);
            productionInfo.add(cytoshowLinks);
            productionInfo.add(movieStartTime);
            productionInfo.add(isSulston);
            productionInfo.add(totalTimePoints);
            productionInfo.add(xScale);
            productionInfo.add(yScale);
            productionInfo.add(zScale);

            return productionInfo;

        } catch (IOException e) {
            System.out.println("The production info file "
                    + PRODUCTION_INFO_FILE_PATH
                    + " wasn't found on the system.");
        }
        return productionInfo;
    }

}