package wormguides.model;

import java.util.ArrayList;

public class NonTerminalCellCase {
	private String cellName;
	//private String externalInfo;
	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;
	
	public NonTerminalCellCase(String cellName) {
		this.cellName = cellName;
		
		//look in the csv file 
		
		terminalDescendants = new ArrayList<TerminalDescendant>();
	}
	
	public String getCellName() {
		return this.cellName;
	}
	
	public void loadHomologues() {
		//separate method for open file - load jar entry
		
	}
	
	public String getEmbryonicHomology() {
		return this.embryonicHomology;
	}
	
	public ArrayList<TerminalDescendant> getTerminalDescendants() {
		return this.terminalDescendants;
	}
	
	
	/*
	 * private inner class which holds a cell name and parts list entry for each terminal descendant (neuron)
	 */
	private class TerminalDescendant {
		private String cellName;
		private String partsListEntry;
		
		public TerminalDescendant(String cellName, String partsListEntry) {
			this.cellName = cellName;
			this.partsListEntry = partsListEntry;
		}
		
		public String getCellName() {
			return this.cellName;
		}
		
		public String getPartsListEntry() {
			return this.partsListEntry;
		}
	}
	
}
