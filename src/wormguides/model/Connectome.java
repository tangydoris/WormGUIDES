package wormguides.model;

import java.util.ArrayList;

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
	
	public String generateHTML(String queryCell) {
		String HTML = "";
		
		return HTML;
		
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
	
	private final static String connectomeFilePath = "src/wormguides/model/connectome_file/NeuronConnect.csv";
	private final static String s_presynapticDescription = "S presynaptic";
	private final static String r_postsynapticDescription = "R postsynaptic";
	private final static String ej_electricalDescription = "EJ electrical";
	private final static String nmj_neuromuscularDescrpition = "Nmj neuromuscular";
}
