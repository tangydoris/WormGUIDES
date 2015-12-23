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
	
	public void debug() {
		System.out.println("Connectome size: " + connectome.size());
		
		ArrayList<String> allConnectomeCellNames = getAllConnectomeCellNames();
		System.out.println("All connectome cells size: " + allConnectomeCellNames.size());
		
		String centralCell = "ADAL";
		ArrayList<String> connectedCells = getConnectedCells(centralCell);
		System.out.println("Connected cells to '" + centralCell + "' size: " + connectedCells.size());
	}
	
	private final static String connectomeFilePath = "src/wormguides/model/connectome_file/NeuronConnect.csv";
}
