package wormguides.model;

import java.util.ArrayList;
import java.util.Collections;
import wormguides.loaders.ConnectomeLoader;
import wormguides.view.HTMLNode;
import wormguides.view.InfoWindowDOM;

/**
 * The underlying model of the all neuronal connections
 * Holds a list of NeuronalSynapses which define two terminal cells which are wired
 * 
 * @author katzmanb
 *
 */
public class Connectome {

	private ArrayList<NeuronalSynapse> connectome;
	private ConnectomeLoader connectomeLoader;

	public Connectome() {
		connectome = new ArrayList<NeuronalSynapse>();
		connectomeLoader = new ConnectomeLoader();

		buildConnectome();
	}

	private void buildConnectome() {
		connectome = connectomeLoader.loadConnectome();
	}

	public ArrayList<NeuronalSynapse> getConnectomeList() {
		return this.connectome;
	}

	public ArrayList<String> getAllConnectomeCellNames() {
		// iterate through connectome arraylist and add all cell names
		ArrayList<String> allConnectomeCellNames = new ArrayList<String>();
		for (NeuronalSynapse ns : connectome) {
			allConnectomeCellNames.add(PartsList.getLineageNameByFunctionalName(ns.getCell1()));
			allConnectomeCellNames.add(PartsList.getLineageNameByFunctionalName(ns.getCell2()));
		}

		return allConnectomeCellNames;
	}

	public ArrayList<String> getConnectedCells(String centralCell) {
		// find all cells that are connected to the central cell
		ArrayList<String> connectedCells = new ArrayList<String>();
		for (NeuronalSynapse ns : connectome) {
			if (ns.getCell1().equals(centralCell)) {
				connectedCells.add(ns.getCell2());
			} else if (ns.getCell2().equals(centralCell)) {
				connectedCells.add(ns.getCell1());
			}
		}
		return connectedCells;
	}

	/**
	 * Provides name translation from systematic to functional
	 * 
	 * @param queryCell the cell to be checked
	 * @return the resultant translated or untranslated cell name
	 */
	public String checkQueryCell(String queryCell) {
		if (PartsList.containsLineageName(queryCell)) {
			queryCell = PartsList.getFunctionalNameByLineageName(queryCell).toLowerCase();
		}

		return queryCell;
	}

	/**
	 * 
	 * @param queryCell the cell to query in the connectome
	 * @return boolean corresponding to whether the query is in the connectome or not
	 */
	public boolean containsCell(String queryCell) {
		queryCell = checkQueryCell(queryCell);

		for (NeuronalSynapse ns : connectome) {
			if (ns.getCell1().toLowerCase().equals(queryCell) || ns.getCell2().toLowerCase().equals(queryCell)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Search function which takes cell and filters results based on filter
	 * toggles filter toggles = 4 Synapse Types
	 * 
	 * @param queryCell the cell to be searched for
	 * @param presynapticTicked
	 * @param postsynapticTicked
	 * @param electricalTicked
	 * @param neuromuscularTicked
	 * @param getLineage denotes whether to return the partners are lineage names or functional names
	 * @return the list of connections to the query cell
	 */
	public ArrayList<String> queryConnectivity(String queryCell, boolean presynapticTicked, boolean postsynapticTicked,
			boolean electricalTicked, boolean neuromuscularTicked, boolean getLineage) {

		// query only works for lineage names
		if (PartsList.containsFunctionalName(queryCell))
			queryCell = PartsList.getLineageNameByFunctionalName(queryCell);

		queryCell = checkQueryCell(queryCell);

		ArrayList<String> searchResults = new ArrayList<String>();

		// error check
		if (queryCell == null) {
			return searchResults;
		}

		// //iterate over connectome
		for (NeuronalSynapse ns : connectome) {
			// check if synapse contains query cell
			if (ns.getCell1().toLowerCase().contains(queryCell) || ns.getCell2().toLowerCase().contains(queryCell)) {
				String cell_1 = ns.getCell1();
				String cell_2 = ns.getCell2();

				// process type code
				String synapseTypeDescription = ns.getSynapseType().getDescription();

				// find synapse type code for connection, compare to toggle
				// ticks
				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					if (presynapticTicked) {
						// don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}

						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					if (postsynapticTicked) {
						// don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}

						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					if (electricalTicked) {
						// don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}

						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					if (neuromuscularTicked) {
						// don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}

						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				}
			}
		}

		// Return lineage names instead of functional names if flag is true
		if (getLineage) {
			ArrayList<String> lineageNameResults = new ArrayList<String>();
			for (String result : searchResults) {
				String lineageName = PartsList.getLineageNameByFunctionalName(result);

				if (lineageName != null)
					lineageNameResults.add(lineageName);
			}
			return lineageNameResults;
		}

		// check if queryCell in results, remove
		if (searchResults.contains(queryCell.toUpperCase())) {
			searchResults.remove(queryCell.toUpperCase());
		}

		return searchResults;
	}

	/**
	 * Builds the connectome as a DOM to be displayed in an external popup window
	 * 
	 * @return the DOM of the connectome
	 */
	public InfoWindowDOM connectomeDOM() {
		HTMLNode html = new HTMLNode("html");
		HTMLNode head = new HTMLNode("head");
		HTMLNode body = new HTMLNode("body");

		HTMLNode connectomeTablesDiv = new HTMLNode("div");

		// add formatted wiring partners for each cell in connectome

		// collect all unique cells
		ArrayList<String> cells = new ArrayList<String>();
		for (NeuronalSynapse ns : connectome) {
			String cell_1 = ns.getCell1();
			String cell_2 = ns.getCell2();

			// add unique entries to list
			if (!cells.contains(cell_1)) {
				cells.add(cell_1);
			}

			if (!cells.contains(cell_2)) {
				cells.add(cell_2);
			}
		}

		// alphabetize the connectome cells
		Collections.sort(cells);

		// add tables of wiring partners for each unique entry
		for (String cell : cells) {
			connectomeTablesDiv.addChild(queryWiringPartnersAsHTMLTable(cell));
			connectomeTablesDiv.addChild(new HTMLNode("br"));
			connectomeTablesDiv.addChild(new HTMLNode("br"));
		}
		
		body.addChild(connectomeTablesDiv);
		html.addChild(head);
		html.addChild(body);
		
		InfoWindowDOM dom = new InfoWindowDOM(html);
		dom.buildStyleNode();
		
		return dom;
	}

	/**
	 * Generates a table of synaptic partners for a given cell
	 * 
	 * @param queryCell the cell for which the table is generated
	 * @return the table HTML node for the DOM
	 */
	public HTMLNode queryWiringPartnersAsHTMLTable(String queryCell) {
		/*
		 * FORMAT Cell Name presynaptic: cellname (numconnections), cellname
		 * (numconnections) postsynaptic: ...
		 */

		ArrayList<String> presynapticPartners = new ArrayList<String>();
		ArrayList<String> postsynapticPartners = new ArrayList<String>();
		ArrayList<String> electricalPartners = new ArrayList<String>();
		ArrayList<String> neuromuscularPartners = new ArrayList<String>();

		// get wiring partners
		for (NeuronalSynapse ns : connectome) {
			String cell_1 = ns.getCell1();
			String cell_2 = ns.getCell2();

			if (queryCell.equals(cell_1)) {
				// add cell_2 as a wiring partner

				// extract number of synapses
				int numberOfSynapses = ns.numberOfSynapses();

				// extract synapse type
				String synapseTypeDescription = ns.getSynapseType().getDescription();

				// format wiring partner with cell_2
				String wiringPartner = cell_2 + formatNumberOfSynapses(Integer.toString(numberOfSynapses));

				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					presynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					postsynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					electricalPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					neuromuscularPartners.add(wiringPartner);
				}

			} else if (queryCell.equals(cell_2)) {
				// add cell_1 as a wiring partner

				// extract number of synapses
				int numberOfSynapses = ns.numberOfSynapses();

				// extract synapse type
				String synapseTypeDescription = ns.getSynapseType().getDescription();

				// format wiring partner with cell_1
				String wiringPartner = cell_1 + formatNumberOfSynapses(Integer.toString(numberOfSynapses));

				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					presynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					postsynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					electricalPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					neuromuscularPartners.add(wiringPartner);
				}
			}
		}

		HTMLNode table = new HTMLNode("table");
		HTMLNode trH = new HTMLNode("th");
		HTMLNode th = new HTMLNode("th", "", "", "Cell: " + queryCell.toUpperCase());

		trH.addChild(th);
		table.addChild(trH);

		HTMLNode trPre;
		HTMLNode trPost;
		HTMLNode trNeuro;
		HTMLNode trElec;

		Collections.sort(presynapticPartners); // alphabetize
		if (presynapticPartners.size() > 0) {
			trPre = new HTMLNode("tr");

			HTMLNode tdPreTitle = new HTMLNode("td", "", "", presynapticPartnersTitle);
			HTMLNode tdPre = new HTMLNode("td", "td", "td",
					presynapticPartners.toString().substring(1, presynapticPartners.toString().length() - 1));

			trPre.addChild(tdPreTitle);
			trPre.addChild(tdPre);

			table.addChild(trPre);
		}

		Collections.sort(postsynapticPartners); // alphabetize
		if (postsynapticPartners.size() > 0) {
			trPost = new HTMLNode("tr");

			HTMLNode tdPostTitle = new HTMLNode("td", "", "", postsynapticPartnersTitle);
			HTMLNode tdPost = new HTMLNode("td", "td", "td",
					postsynapticPartners.toString().substring(1, postsynapticPartners.toString().length() - 1));

			trPost.addChild(tdPostTitle);
			trPost.addChild(tdPost);

			table.addChild(trPost);
		}

		Collections.sort(electricalPartners); // alphabetize
		if (electricalPartners.size() > 0) {
			trElec = new HTMLNode("tr");

			HTMLNode tdElecTitle = new HTMLNode("td", "", "", electricalPartnersTitle);
			HTMLNode tdElec = new HTMLNode("td", "td", "td",
					electricalPartners.toString().substring(1, electricalPartners.toString().length() - 1));

			trElec.addChild(tdElecTitle);
			trElec.addChild(tdElec);

			table.addChild(trElec);
		}

		Collections.sort(neuromuscularPartners); // alphabetize
		if (neuromuscularPartners.size() > 0) {
			trNeuro = new HTMLNode("tr");

			HTMLNode tdNeuroTitle = new HTMLNode("td", "", "", neuromusclarPartnersTitle);
			HTMLNode tdNeuro = new HTMLNode("td", "td", "td",
					neuromuscularPartners.toString().substring(1, neuromuscularPartners.toString().length() - 1));

			trNeuro.addChild(tdNeuroTitle);
			trNeuro.addChild(tdNeuro);

			table.addChild(trNeuro);
		}

		return table;

	}

	private String formatNumberOfSynapses(String numberOfSynapses) {
		return "(" + numberOfSynapses + ")";
	}

//	public void debug() {
//		System.out.println("Connectome size: " + connectome.size());
//
//		ArrayList<String> allConnectomeCellNames = getAllConnectomeCellNames();
//		System.out.println("All connectome cells size: " + allConnectomeCellNames.size());
//
//		String centralCell = "ADAL";
//		ArrayList<String> connectedCells = getConnectedCells(centralCell);
//		System.out.println("Connected cells to '" + centralCell + "' size: " + connectedCells.size());
//
//		String queryCell = "AIZL";
//		queryConnectivity(queryCell, true, true, true, true, true);
//	}

	// static vars

	// connectome config file location
	//private final static String connectomeFilePath = "src/wormguides/model/connectome_file/NeuronConnect.csv";

	// synapse types as strings for search logic
	private final static String s_presynapticDescription = "S presynaptic";
	private final static String r_postsynapticDescription = "R postsynaptic";
	private final static String ej_electricalDescription = "EJ electrical";
	private final static String nmj_neuromuscularDescrpition = "Nmj neuromuscular";

	private final String presynapticPartnersTitle = "Presynaptic: ";
	private final String postsynapticPartnersTitle = "Postsynaptic: ";
	private final String electricalPartnersTitle = "Electrical: ";
	private final String neuromusclarPartnersTitle = "Neuromusclar: ";
}
