package wormguides.model;

import java.util.ArrayList;
import java.util.Collections;

public class Connectome {
	private ArrayList<NeuronalSynapse> connectome;
	private ConnectomeLoader connectomeLoader;
	
	public Connectome() {
		connectome = new ArrayList<NeuronalSynapse>();
		connectomeLoader = new ConnectomeLoader(connectomeFilePath);
	}
	
	public void buildConnectome() {
		connectome = connectomeLoader.loadConnectome();
	}
	
	public ArrayList<String> getAllConnectomeCellNames() {
		//iterate through connectome arraylist and add all cell names
		ArrayList<String> allConnectomeCellNames = new ArrayList<String>();
		for (NeuronalSynapse ns : connectome) {
			allConnectomeCellNames.add(PartsList.getLineageNameByFunctionalName(ns.getCell1()));
			allConnectomeCellNames.add(PartsList.getLineageNameByFunctionalName(ns.getCell2()));
		}

		return allConnectomeCellNames;
	}
	
	public ArrayList<String> getConnectedCells(String centralCell) {
		//find all cells that are connected to the central cell
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
	
	/*
	 * Search function which takes cell and filters results based on filter toggles
	 * filter toggles = 4 Synapse Types
	 */
	public ArrayList<String> querryConnectivity(String queryCell,
			boolean presynapticTicked, boolean postsynapticTicked,
			boolean electricalTicked, boolean neuromuscularTicked) {
		
		/* TODO
		 * - add synapse types to check (add to search class)
		 * - build color rule
		 * - add search spec from email --> translation in search class
		 */
		
		ArrayList<String> searchResults = new ArrayList<String>();

		//error check
		if (queryCell == null) {
			return searchResults;
		}
		
//		//iterate over connectome
		for (NeuronalSynapse ns : connectome) {
			//check if synapse contains query cell
			if (ns.getCell1().toLowerCase().contains(queryCell) || ns.getCell2().toLowerCase().contains(queryCell)) {
				String cell_1 = ns.getCell1();
				String cell_2 = ns.getCell2();
				
				//process type code
				String synapseTypeDescription = ns.getSynapseType().getDescription();
				
				//find synapse type code for connection, compare to toggle ticks
				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					if (presynapticTicked) {
						//don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}
						
						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					if (postsynapticTicked) {
						//don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}
						
						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					if (electricalTicked) {
						//don't add duplicates
						if (!searchResults.contains(cell_1)) {
							searchResults.add(cell_1);
						}
						
						if (!searchResults.contains(cell_2)) {
							searchResults.add(cell_2);
						}
					}
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					if (neuromuscularTicked) {
						//don't add duplicates
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
		
		// Return lineage names instead of functional names
		ArrayList<String> lineageNameResults = new ArrayList<String>();
		for (String result : searchResults) {
			String lineageName = PartsList.getLineageNameByFunctionalName(result);
			
			if (lineageName!=null)
				lineageNameResults.add(lineageName);
		}
		
		return lineageNameResults;
	}
	
	
	/*
	 * TODO
	 * - MAKE CONNECTOME RULE TIED TO SELECTED CELL AND NOT SEARCH STRING --> send email, this is also happening with other rules
	 * - implemented grayed out view options
	 * - send email about plan for class for external html window for connectome
	 */
	
	/*
	 * Method that generates a legible summary of the wiring partners given a query cell in the connectome
	 * 
	 * Used to generate a snippet of HTML for the global connectome HTML file
	 * OR to generate a complete HTML page for a single search cell
	 * 
	 * If using this method to generate an external info page for a single cell, pass a TRUE bool val to
	 * generate a full HTML page
	 */
	public String queryWiringPartnersAsHTMLTable(String queryCell, boolean generateFullHTML) {
		/*	FORMAT 
		 *   Cell Name
		 *   presynaptic:  cellname (numconnections), cellname (numconnections)
		 *   postsynaptic: ...
		 */
		ArrayList<String> presynapticPartners = new ArrayList<String>();
		ArrayList<String> postsynapticPartners = new ArrayList<String>();
		ArrayList<String> electricalPartners = new ArrayList<String>();
		ArrayList<String> neuromuscularPartners = new ArrayList<String>();
		
		//get wiring partners
		for (NeuronalSynapse ns : connectome) {
			String cell_1 = ns.getCell1();
			String cell_2 = ns.getCell2();
			
			if (queryCell.equals(cell_1)) {
				//add cell_2 as a wiring partner
				
				//extract number of synapses
				int numberOfSynapses = ns.numberOfSynapses();
				
				//extract synapse type
				String synapseTypeDescription = ns.getSynapseType().getDescription();
				
				//format wiring partner with cell_2
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
				//add cell_1 as a wiring partner
				
				//extract number of synapses
				int numberOfSynapses = ns.numberOfSynapses();
				
				//extract synapse type
				String synapseTypeDescription = ns.getSynapseType().getDescription();
				
				//format wiring partner with cell_1
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
		
		//format wiring partners as HTML --> only format if > 0 partners
		String queryCellTableHeaderRow = openTableRowHTML + openTableHeaderHTML + 
				cellTitle + queryCell +
				closeTableHeaderHTML + closeTableRowHTML;
		
		String presynapticPartnersTableRow;
		Collections.sort(presynapticPartners); //alphabetize
		if (presynapticPartners.size() > 0) {
			presynapticPartnersTableRow = openTableRowHTML + openTableDataHTML +
					presynapticPartnersTitle + closeTableDataHTML + openTableDataHTML +
					presynapticPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			presynapticPartnersTableRow = "";
		}
		
		
		String postsynapticPartnersTableRow;
		Collections.sort(postsynapticPartners); //alphabetize
		if (postsynapticPartners.size() > 0) {
			postsynapticPartnersTableRow = openTableRowHTML + openTableDataHTML + 
					postsynapticPartnersTitle + closeTableDataHTML + openTableDataHTML +
					postsynapticPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			postsynapticPartnersTableRow = "";
		}
		
		String electricalPartnersTableRow;
		Collections.sort(electricalPartners); //alphabetize
		if (electricalPartners.size() > 0) {
			electricalPartnersTableRow = openTableRowHTML + openTableDataHTML +
					electricalPartnersTitle + closeTableDataHTML + openTableDataHTML +
					electricalPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			electricalPartnersTableRow = "";
		}
		
		String neuromuscularPartnersTableRow;
		Collections.sort(neuromuscularPartners); //alphabetize
		if (neuromuscularPartners.size() > 0) {
			neuromuscularPartnersTableRow = openTableRowHTML + openTableDataHTML +
					neuromusclarPartnersTitle + closeTableDataHTML + openTableDataHTML +
					neuromuscularPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			neuromuscularPartnersTableRow = "";
		}
 		
		String table = openTableTagHTML + queryCellTableHeaderRow + presynapticPartnersTableRow
				+ postsynapticPartnersTableRow + electricalPartnersTableRow  
				+ neuromuscularPartnersTableRow + closeTableTagHTML;
		
		//check if need to generate a full html page
		if (generateFullHTML) {
			String queryCellResultsAsHTML = htmlStart + table + htmlEnd;
			return queryCellResultsAsHTML;
		} else {
			return table;
		}
	}
	
	public String connectomeAsHTML() {
		if (connectome != null) {
			
		//add formatted wiring partners for each cell in connectome
			
			//collect all unique cells
			ArrayList<String> cells = new ArrayList<String>();
			for (NeuronalSynapse ns: connectome) {
				String cell_1 = ns.getCell1();
				String cell_2 = ns.getCell2();
				
				//add unique entries to list
				if (!cells.contains(cell_1)) {
					cells.add(cell_1);
				}
				
				if (!cells.contains(cell_2)) {
					cells.add(cell_2);
				}
			}
			
			//alphabetize the connectome cells
			Collections.sort(cells);
			
			//add tables of wiring partners for each unique entry
			ArrayList<String> wiringPartnersAsHTMLTables = new ArrayList<String>();
			for (String cell : cells) {
				wiringPartnersAsHTMLTables.add(queryWiringPartnersAsHTMLTable(cell, false));
			}
			
			String htmlTables = "";
			for (String table : wiringPartnersAsHTMLTables) {
				htmlTables += (newLine + table);
			}
			
			String htmlFile = htmlStart + htmlTables + htmlEnd;
			return htmlFile;
		}
		return "";
	}
	
	private String formatNumberOfSynapses(String numberOfSynapses) {
		return "(" + numberOfSynapses + ")";
	}
	
	public void debug() {
		System.out.println("Connectome size: " + connectome.size());
		
		ArrayList<String> allConnectomeCellNames = getAllConnectomeCellNames();
		System.out.println("All connectome cells size: " + allConnectomeCellNames.size());
		
		String centralCell = "ADAL";
		ArrayList<String> connectedCells = getConnectedCells(centralCell);
		System.out.println("Connected cells to '" + centralCell + "' size: " + connectedCells.size());
		
		String queryCell = "AIZL";
		querryConnectivity(queryCell, true, true, true, true);
	}
	
	//static vars
	private final static String newLine = "\n";
	
	//connectome config file location
	private final static String connectomeFilePath = "src/wormguides/model/connectome_file/NeuronConnect.csv";
	
	//synapse types as strings for search logic
	private final static String s_presynapticDescription = "S presynaptic";
	private final static String r_postsynapticDescription = "R postsynaptic";
	private final static String ej_electricalDescription = "EJ electrical";
	private final static String nmj_neuromuscularDescrpition = "Nmj neuromuscular";
	
	//html page structuring
	private final static String htmlStart = "<!DOCTYPE html>" + 
											newLine + "<html>" +
											newLine + "<head>" +
											newLine + "<meta charset=\"utf-8\">" +
											newLine + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
											newLine + "<title>Connectome</title>" +
											newLine + "</head>" +
											newLine + "<body>" + newLine;
	private final static String htmlEnd = newLine + "</body>" +
										  newLine + "</html>";
	
	
	//html table tags
	private final static String openTableTagHTML = newLine + "<table>";
	private final static String closeTableTagHTML = newLine + "</table>";
	private final static String openTableRowHTML = newLine + "<tr>";
	private final static String closeTableRowHTML = newLine + "</tr>";
	private final static String openTableHeaderHTML = newLine + "<th colspan=\"2\">";
	private final static String closeTableHeaderHTML = "</th>";
	private final static String openTableDataHTML = newLine + "<td>";
	private final static String closeTableDataHTML = "</td>";
	
	private final static String cellTitle = "Cell: ";
	
	private final static String presynapticPartnersTitle = "Presynaptic: ";
	private final static String postsynapticPartnersTitle = "Postsynaptic: ";
	private final static String electricalPartnersTitle = "Electrical: ";
	private final static String neuromusclarPartnersTitle = "Neuromusclar: ";
}
