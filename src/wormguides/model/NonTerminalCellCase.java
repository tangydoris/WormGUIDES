package wormguides.model;

import java.util.ArrayList;

public class NonTerminalCellCase {
	private String cellName;
	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;
	
	public NonTerminalCellCase(String cellName) {
		this.cellName = cellName; //use this for identifier and external information
		
		//reference embryonic analogues cells db for homology
		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(this.cellName);
		
		terminalDescendants = new ArrayList<TerminalDescendant>();
	}
	
	public String getCellName() {
		return this.cellName;
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
