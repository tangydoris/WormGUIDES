/*
 * Bao Lab 2016
 */

package partslist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wormguides.view.infowindow.HTMLNode;
import wormguides.view.infowindow.InfoWindowDOM;

/**
 * Contains static methods that query the parts list for functional names, lineage names, and descriptions. Content
 * is loaded from partslist.txt.
 */
public class PartsList {

    private static final String RESOURCE = "/partslist/partslist.txt";

    private static final List<String> functionalNames = new ArrayList<>();
    private static final List<String> lineageNames = new ArrayList<>();
    private static final List<String> descriptions = new ArrayList<>();

    /**
     * Initializes the lists of names according to the parts list file
     */
    public static void init() {
        final URL url = PartsList.class.getResource(RESOURCE);

        try (final InputStreamReader isr = new InputStreamReader(url.openStream());
             final BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split("\t");
                functionalNames.add(lineArray[0].trim());
                lineageNames.add(lineArray[1].trim());
                descriptions.add(lineArray[2].trim());
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Checks whether a name is a lineage name (case-insensitive)
     *
     * @param name
     *         name to check
     *
     * @return true if the name if a lineage name (disregarding case), false otherwise
     */
    public static boolean isLineageName(final String name) {
        if (name == null) {
            return false;
        }

        // case insensitive search
        for (String lineageName : lineageNames) {
            if (lineageName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a name is a functional name
     *
     * @param name
     *         name to check
     *
     * @return true is the name is a functional name, false otherwise
     */
    public static boolean isFunctionalName(final String name) {
        final String trimmed = name.trim();
        for (String funcName : functionalNames) {
            if (funcName.equalsIgnoreCase(trimmed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the lineage name with the correct case. If the name is not a lineage name, "ABCDEFG" is returned.
     *
     * @param lineageName
     *         case-insensitive lineage name
     *
     * @return lineage name with the correct case
     */
    public static String getLineageNameCorrectCase(final String lineageName) {
        final String trimmed = lineageName.trim();
        for (String name : lineageNames) {
            if (name.equalsIgnoreCase(trimmed)) {
                return name;
            }
        }
        return "ABCDEFG";
    }

    /**
     * Returns the functional name with the correct case. If the name is not a functional name, "ABCDEFG" is returned.
     *
     * @param functionalName
     *         case-insensitive functional name
     *
     * @return lineage name with the correct case
     */
    public static String getFunctionalNameCorrectCase(final String functionalName) {
        String trimmed = functionalName.trim();
        for (String name : functionalNames) {
            if (name.equalsIgnoreCase(trimmed)) {
                return name;
            }
        }
        return "ABCDEFG";
    }

    /**
     * Returns the lineage name at an index in the list of lineage names
     *
     * @param index
     *         index at which to retrieve the lineage name
     *
     * @return lineage name at the index, null if the index is out of bounds
     */
    public static String getLineageNameByIndex(final int index) {
        if (index >= 0 && index < lineageNames.size()) {
            return lineageNames.get(index);
        }
        return null;
    }

    /**
     * Returns the functional name at an index in the list of functional names
     *
     * @param index
     *         index at which to retrieve the functional name
     *
     * @return functional name at the index, null if the index is out of bounds
     */
    public static String getFunctionalNameByIndex(final int index) {
        if (index >= 0 && index < functionalNames.size()) {
            return functionalNames.get(index);
        }
        return null;
    }

    /**
     * Returns the description at an index in the list of descriptions
     *
     * @param index
     *         index at which to retrieve the description
     *
     * @return description at the index, null if the index is out of bounds
     */
    public static String getDescriptionByIndex(final int index) {
        if (index >= 0 && index < descriptions.size()) {
            return descriptions.get(index);
        }
        return null;
    }

    /**
     * Retrieves the lineage name for the functional name
     *
     * @param functionalName
     *         the lineage name
     *
     * @return lineage name for that functional name
     */
    public static String getLineageNameByFunctionalName(final String functionalName) {
        return getLineageNameByIndex(functionalNames.indexOf(getFunctionalNameCorrectCase(functionalName)));
    }

    /**
     * Retrieves the functional name for the lineage name
     *
     * @param lineageName
     *         the lineage name
     *
     * @return functional name for that lineage name
     */
    public static String getFunctionalNameByLineageName(final String lineageName) {
        return getFunctionalNameByIndex(lineageNames.indexOf(getLineageNameCorrectCase(lineageName)));
    }

    /**
     * Retrieves the description associated with a lineage name
     *
     * @param lineageName
     *         the lineage name
     *
     * @return description for that lineage name
     */
    public static String getDescriptionByLineageName(final String lineageName) {
        return getDescriptionByIndex(lineageNames.indexOf(getLineageNameCorrectCase(lineageName)));
    }

    /**
     * Retrieves the description associated with a functional name
     *
     * @param functionalName
     *         the functional name
     *
     * @return description for that functional name
     */
    public static String getDescriptionByFunctionalName(final String functionalName) {
        return getDescriptionByIndex(functionalNames.indexOf(getFunctionalNameCorrectCase(functionalName)));
    }

    /**
     * @return copy of the list of lineage names
     */
    public static ArrayList<String> getLineageNames() {
        return new ArrayList<>(lineageNames);
    }

    /**
     * @return copy of the list of functional names
     */
    public static ArrayList<String> getFunctionalNames() {
        return new ArrayList<>(functionalNames);
    }

    /**
     * @return copy of the list of descriptions
     */
    public static ArrayList<String> getDescriptions() {
        return new ArrayList<>(descriptions);
    }

    /**
     * @return info window DOM display for the parts list
     */
    public static InfoWindowDOM createPartsListDOM() {
        final HTMLNode html = new HTMLNode("html");
        final HTMLNode head = new HTMLNode("head");
        final HTMLNode body = new HTMLNode("body");

        final HTMLNode partsListTableDiv = new HTMLNode("div");
        final HTMLNode partsListTable = new HTMLNode("table");

        for (int i = 0; i < functionalNames.size(); i++) {
            final HTMLNode tr = new HTMLNode("tr");

            tr.addChild(new HTMLNode("td", "", "", functionalNames.get(i)));

            if (lineageNames.get(i) != null) {
                tr.addChild(new HTMLNode("td", "", "", lineageNames.get(i)));
            }

            if (descriptions.get(i) != null) {
                tr.addChild(new HTMLNode("td", "", "", descriptions.get(i)));
            }

            partsListTable.addChild(tr);
        }

        partsListTableDiv.addChild(partsListTable);
        body.addChild(partsListTableDiv);

        html.addChild(head);
        html.addChild(body);

        final InfoWindowDOM dom = new InfoWindowDOM(html);
        dom.buildStyleNode();

        return dom;
    }

}
