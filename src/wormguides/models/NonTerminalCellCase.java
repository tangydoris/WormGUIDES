/*
 * Bao Lab 2016
 */

package wormguides.models;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import partslist.PartsList;
import search.SearchUtil;

/**
 * A non-terminal cell object which contains the information for the info window
 */
public class NonTerminalCellCase extends CellCase {

    private final static String WORMATLAS_URL_EXT = "mainframe.htm";

    private String embryonicHomology;

    private List<TerminalDescendant> terminalDescendants;

    /**
     * Class constructor
     *
     * @param lineageName
     *         name of the non-terminal cell case
     * @param nuclearProductionInfo
     *         the production information under Nuclear
     * @param cellShapeProductionInfo
     *         the production information under Cell Shape
     */
    public NonTerminalCellCase(
            final String lineageName,
            final List<String> nuclearProductionInfo,
            final List<String> cellShapeProductionInfo) {

        super(lineageName, nuclearProductionInfo, cellShapeProductionInfo);

        // reference embryonic analogues cells db for homology
        this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(getLineageName());

        this.terminalDescendants = buildTerminalDescendants();

        addLink(buildWormatlasLink());
    }

    /**
     * Finds the terminal descendants of the cell using the parts list
     *
     * @return the list of terminal descendants
     */
    private List<TerminalDescendant> buildTerminalDescendants() {
        List<TerminalDescendant> terminalDescendants = new ArrayList<>();

        List<String> descendantsList = SearchUtil.getDescendantsList(getLineageName());

        // add each descendant as terminal descendant object
        for (String descendant : descendantsList) {
            String partsListDescription = PartsList.getDescriptionByLineageName(descendant);
            if (partsListDescription == null) {
                if (CellDeaths.containsCell(descendant)) {
                    partsListDescription = "Cell Death";
                } else {
                    partsListDescription = "";
                }
            }
            terminalDescendants.add(new TerminalDescendant(descendant, partsListDescription));
        }

        return terminalDescendants;
    }

    private String buildWormatlasLink() {
        if (getLineageName() == null) {
            return "";
        }

        final String urlString = WORMATLAS_URL
                + getLineageName().toUpperCase()
                + WORMATLAS_URL_EXT;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 404) {
                return "";
            } else if (connection.getResponseCode() == 200) {
                return urlString;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(getLineageName() + " page not found on Wormatlas");
        }

        return "";
    }

    public String getEmbryonicHomology() {
        return embryonicHomology;
    }

    public List<TerminalDescendant> getTerminalDescendants() {
        return terminalDescendants;
    }
}