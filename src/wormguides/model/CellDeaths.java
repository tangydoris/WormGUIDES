package wormguides.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import wormguides.view.HTMLNode;
import wormguides.view.InfoWindowDOM;

public class CellDeaths {
	private static ArrayList<String> cellDeaths;
	private static InfoWindowDOM dom;
	private final static String CellDeathsFile = "/wormguides/model/cell_deaths/CellDeaths.csv";

	static {
		cellDeaths = new ArrayList<String>();

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
		if (cellDeaths != null) {
			return cellDeaths.contains(cell.toLowerCase());
		}
		return false;
	}

	public static String getCellDeathsDOMAsString() {
		if (dom != null) {
			return dom.DOMtoString();
		}
		return "";
	}
}