package wormguides.model;

import java.util.ArrayList;
//import java.util.Collections;

import wormguides.ConnectomeToHTML;
import wormguides.loaders.ConnectomeLoader;

public class Connectome {
	
	private ArrayList<NeuronalSynapse> connectome;
	private ConnectomeLoader connectomeLoader;
	
	//terminal cell case
	
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
	 * provides name translation from systematic to functional
	 */
	public String checkQueryCell(String queryCell) {
		if (PartsList.containsLineageName(queryCell)) {
			queryCell = PartsList.getFunctionalNameByLineageName(queryCell).toLowerCase();
		}
		
		return queryCell;
	}
	
	public boolean containsCell(String queryCell) {
		queryCell = checkQueryCell(queryCell);
		
		for (NeuronalSynapse ns : connectome) {
			if (ns.getCell1().toLowerCase().equals(queryCell) || ns.getCell2().toLowerCase().equals(queryCell)) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * Search function which takes cell and filters results based on filter toggles
	 * filter toggles = 4 Synapse Types
	 */
	public ArrayList<String> queryConnectivity(String queryCell,
			boolean presynapticTicked, boolean postsynapticTicked,
			boolean electricalTicked, boolean neuromuscularTicked, 
			boolean getLineage) {
		
		// query only works for lineage names
		if (PartsList.containsFunctionalName(queryCell))
			queryCell = PartsList.getLineageNameByFunctionalName(queryCell);
		
		queryCell = checkQueryCell(queryCell);
		
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
		
		// Return lineage names instead of functional names if flag is true
		if (getLineage) {
			ArrayList<String> lineageNameResults = new ArrayList<String>();
			for (String result : searchResults) {
				String lineageName = PartsList.getLineageNameByFunctionalName(result);
				
				if (lineageName!=null)
					lineageNameResults.add(lineageName);
			}
			return lineageNameResults;
		}
		
		//check if queryCell in results, remove
		if (searchResults.contains(queryCell.toUpperCase())) {
			searchResults.remove(queryCell.toUpperCase());
		}
		
		return searchResults;
	}
	
	public String connectomeAsHTML() {
		if (connectome != null) {
			ConnectomeToHTML connectomeToHTML = new ConnectomeToHTML(this);
			return connectomeToHTML.buildConnectomeAsHTML();
		}
			return "";
	}
	
	public void debug() {
		System.out.println("Connectome size: " + connectome.size());
		
		ArrayList<String> allConnectomeCellNames = getAllConnectomeCellNames();
		System.out.println("All connectome cells size: " + allConnectomeCellNames.size());
		
		String centralCell = "ADAL";
		ArrayList<String> connectedCells = getConnectedCells(centralCell);
		System.out.println("Connected cells to '" + centralCell + "' size: " + connectedCells.size());
		
		String queryCell = "AIZL";
		queryConnectivity(queryCell, true, true, true, true, true);
	}
	
	//static vars

	//connectome config file location
	private final static String connectomeFilePath = "src/wormguides/model/connectome_file/NeuronConnect.csv";
	
	//synapse types as strings for search logic
	private final static String s_presynapticDescription = "S presynaptic";
	private final static String r_postsynapticDescription = "R postsynaptic";
	private final static String ej_electricalDescription = "EJ electrical";
	private final static String nmj_neuromuscularDescrpition = "Nmj neuromuscular";
}
