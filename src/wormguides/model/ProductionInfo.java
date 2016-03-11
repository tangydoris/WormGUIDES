package wormguides.model;

import java.util.ArrayList;
import wormguides.loaders.ProductionInfoLoader;
import wormguides.view.HTMLNode;
import wormguides.view.InfoWindowDOM;

/**
 * Class which holds the database of production info defined in
 * model/production_info_file/
 * 
 * @author bradenkatzman
 *
 */
public class ProductionInfo {

	private ArrayList<ArrayList<String>> productionInfoData;

	public ProductionInfo() {
		productionInfoData = ProductionInfoLoader.buildProductionInfo();
	}

	public ArrayList<String> getNuclearInfo() {
		ArrayList<String> nuclearInfo = new ArrayList<String>();

		if (productionInfoData.get(0).get(0).equals("all-nuclear positions")) {
			nuclearInfo.add(productionInfoData.get(3).get(0) + ", " + productionInfoData.get(2).get(0)); // store, strain, marker, data
			nuclearInfo.add(productionInfoData.get(1).get(0)); // store image, series data
		}

		return nuclearInfo;
	}

	public int getDefaultStartTime() {
		return DEFAULT_START_TIME;
	}

	public int getMovieTimeOffset() {
		String input = productionInfoData.get(8).get(0);

		try {
			int startTime = Integer.parseInt(input);
			return startTime - DEFAULT_START_TIME;
		} catch (NumberFormatException e) {
			System.out.println("Input: '" + input + "'");
			System.out.println("Invalid input for movie start time. Using default start time of " + DEFAULT_START_TIME);
		}

		return 0;
	}

	public ArrayList<String> getCellShapeData(String queryCell) {
		ArrayList<String> cellShapeData = new ArrayList<String>();

		queryCell = PartsList.getFunctionalNameByLineageName(queryCell);

		if (queryCell == null) {
			return cellShapeData;
		}

		for (int i = 0; i < productionInfoData.get(0).size(); i++) {
			String cells = productionInfoData.get(0).get(i);
			if (cells.contains(queryCell)) {
				cellShapeData.add(productionInfoData.get(3).get(i) + ", " + productionInfoData.get(2).get(i)); // store
																												// strain,
																												// marker
																												// data
				cellShapeData.add(productionInfoData.get(1).get(i)); // store
																		// image
																		// series
																		// data
			}
		}

		return cellShapeData;
	}

	public ArrayList<ArrayList<String>> getProductionInfoData() {
		return productionInfoData;
	}

	/**
	 * Builds the production info as an HTML page with DOM paradigm
	 * 
	 * @return the dom for the info window
	 */
	public InfoWindowDOM getProductionInfoDOM() {
		HTMLNode html = new HTMLNode("html");
		HTMLNode head = new HTMLNode("head");
		HTMLNode body = new HTMLNode("body");

		HTMLNode productionInfoDiv = new HTMLNode("div");
		HTMLNode productionInfoTable = new HTMLNode("table");

		// title row
		HTMLNode trH = new HTMLNode("tr");
		HTMLNode th1 = new HTMLNode("th", "", "", "Cells");
		HTMLNode th2 = new HTMLNode("th", "", "", "Image Series");
		HTMLNode th3 = new HTMLNode("th", "", "", "Marker");
		HTMLNode th4 = new HTMLNode("th", "", "", "Strain");
		HTMLNode th5 = new HTMLNode("th", "", "", "Compressed Embryo?");
		HTMLNode th6 = new HTMLNode("th", "", "", "Temporal Resolution");
		HTMLNode th7 = new HTMLNode("th", "", "", "Segmentation");
		HTMLNode th8 = new HTMLNode("th", "", "", "Cytoshow Link");
		HTMLNode th9 = new HTMLNode("th", "", "", "Movie Start Time (min)");
		trH.addChild(th1);
		trH.addChild(th2);
		trH.addChild(th3);
		trH.addChild(th4);
		trH.addChild(th5);
		trH.addChild(th6);
		trH.addChild(th7);
		trH.addChild(th8);
		trH.addChild(th9);

		productionInfoTable.addChild(trH);

		int rows = productionInfoData.get(0).size();
		for (int i = 0; i < rows; i++) {
			HTMLNode tr = new HTMLNode("tr");
			for (int k = 0; k < productionInfoData.size(); k++) {
				String data = productionInfoData.get(k).get(i);
				HTMLNode td = new HTMLNode("td", "", "", data);
				tr.addChild(td);
			}
			productionInfoTable.addChild(tr);
		}

		productionInfoDiv.addChild(productionInfoTable);

		body.addChild(productionInfoDiv);

		html.addChild(head);
		html.addChild(body);

		InfoWindowDOM productionInfoDOM = new InfoWindowDOM(html);
		productionInfoDOM.buildStyleNode();

		return productionInfoDOM;
	}

	private final int DEFAULT_START_TIME = 1;
}
