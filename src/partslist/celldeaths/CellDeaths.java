/*
 * Bao Lab 2016
 */

package partslist.celldeaths;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import partslist.PartsList;
import wormguides.view.infowindow.HTMLNode;
import wormguides.view.infowindow.InfoWindowDOM;

/**
 * The list of cell deaths represented in internal memory and a DOM for external window viewing
 */
public class CellDeaths {

    private static final String RESOURCE = "/partslist/celldeaths/CellDeaths.csv";

    private static final List<String> cellDeaths = new ArrayList<>();

    private static final InfoWindowDOM dom = new InfoWindowDOM();

    public static void init() {
        final HTMLNode head = new HTMLNode("head");
        final HTMLNode body = new HTMLNode("body");

        final HTMLNode deathsDiv = new HTMLNode("div");
        final HTMLNode deathsTable = new HTMLNode("table");

        final URL url = PartsList.class.getResource(RESOURCE);
        try (final InputStreamReader isr = new InputStreamReader(url.openStream());
             final BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                // add death to table
                final HTMLNode tr = new HTMLNode("tr");
                tr.addChild(new HTMLNode("td", "", "", line));
                deathsTable.addChild(tr);

                // add to internal memory
                cellDeaths.add(line.toLowerCase());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        deathsDiv.addChild(deathsTable);
        body.addChild(deathsDiv);

        dom.getHTML().addChild(head);
        dom.getHTML().addChild(body);
        dom.buildStyleNode();
    }

    public static boolean isInCellDeaths(final String cell) {
        return cellDeaths != null
                && cellDeaths.contains(cell.toLowerCase());
    }

    public static String getCellDeathsDOMAsString() {
        if (dom != null) {
            return dom.DOMtoString();
        }
        return "";
    }
}