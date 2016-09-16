package wormguides.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import wormguides.view.HTMLNode;
import wormguides.view.InfoWindowDOM;

import partslist.PartsList;

/**
 * The list of cell deaths represented in internal memory and a DOM for external window viewing
 *
 * @author katzmanb
 */
public class CellDeaths {
    private final static String CellDeathsFile = "/wormguides/models/cell_deaths/CellDeaths.csv";
    private static ArrayList<String> cellDeaths;
    private static InfoWindowDOM dom;

    static {
        cellDeaths = new ArrayList<>();

        // build the dom
        dom = new InfoWindowDOM();
        HTMLNode head = new HTMLNode("head");
        HTMLNode body = new HTMLNode("body");

        HTMLNode deathsDiv = new HTMLNode("div");
        HTMLNode deathsTable = new HTMLNode("table");

        try {
            URL url = PartsList.class.getResource(CellDeathsFile);

            InputStream input = url.openStream();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                // add death to table
                HTMLNode tr = new HTMLNode("tr");
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

    public static boolean containsCell(String cell) {
        return cellDeaths != null && cellDeaths.contains(cell.toLowerCase());
    }

    public static String getCellDeathsDOMAsString() {
        if (dom != null) {
            return dom.DOMtoString();
        }
        return "";
    }
}