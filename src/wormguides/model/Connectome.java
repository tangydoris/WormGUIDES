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
			allConnectomeCellNames.add(ns.getCell1());
			allConnectomeCellNames.add(ns.getCell2());
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
	public ArrayList<NeuronalSynapse> filterToggleSearch(String queryCell,
			boolean presynapticTicked, boolean postsynapticTicked,
			boolean electricalTicked, boolean neuromuscularTicked) {
		
		ArrayList<NeuronalSynapse> searchResults = new ArrayList<NeuronalSynapse>();
		
		//iterate over connectome
		for (NeuronalSynapse ns : connectome) {
			//check if synapse contains query cell
			if (ns.getCell1().equals(queryCell) || ns.getCell2().equals(queryCell)) {
				//process type code
				String synapseTypeDescription = ns.getSynapseType().getDescription();
				
				//find synapse type code for connection, compare to toggle ticks
				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					if (presynapticTicked) {
						searchResults.add(ns);
					}
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					if (postsynapticTicked) {
						searchResults.add(ns);
					}
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					if (electricalTicked) {
						searchResults.add(ns);
					}
					
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					if (neuromuscularTicked) {
						searchResults.add(ns);
					}
				}
			}
		}
		
		return searchResults;
	}
	
	public void debug() {
		System.out.println("Connectome size: " + connectome.size());
		
		ArrayList<String> allConnectomeCellNames = getAllConnectomeCellNames();
		System.out.println("All connectome cells size: " + allConnectomeCellNames.size());
		
		String centralCell = "ADAL";
		ArrayList<String> connectedCells = getConnectedCells(centralCell);
		System.out.println("Connected cells to '" + centralCell + "' size: " + connectedCells.size());
		
		String queryCell = "AIZL";
		filterToggleSearch(queryCell, true, true, true, true);
	}
	
	private final static String connectomeFilePath = "src/wormguides/model/connectome_file/NeuronConnect.csv";
	private final static String s_presynapticDescription = "";
	private final static String r_postsynapticDescription = "";
	private final static String ej_electricalDescription = "";
	private final static String nmj_neuromuscularDescrpition = "";
}
