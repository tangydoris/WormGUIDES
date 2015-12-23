package wormguides.model;

import java.util.ArrayList;

public class Connectome {
	private ArrayList<NeuronalSynapse> connectome;
	private ConnectomeLoader connectomeLoader;
	
	public Connectome() {
		connectome = new ArrayList<NeuronalSynapse>();
		connectomeLoader = new ConnectomeLoader(connectomeFileName);
	}
	
	public void buildConnectome() {
		connectome = connectomeLoader.loadConnectome();
	}
	
	public ArrayList<String> getAllConnectomeCellNames() {
		//iterate through connectome arraylist and add all cell names
		ArrayList<String> allConnectomeCellNames = new ArrayList<String>();
		
		return allConnectomeCellNames;
	}
	
	public ArrayList<String> getConnectedCells(String centralCell) {
		//find all cells that are connected to the central cell
		ArrayList<String> connectedCells = new ArrayList<String>();
		
		return connectedCells;
	}
	
	private final static String connectomeFileName = "src/wormguides/model/connectome_file/NeuronConnect.csv";
}
