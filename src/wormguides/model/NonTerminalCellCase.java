package wormguides.model;

import java.util.ArrayList;
import wormguides.Search;

public class NonTerminalCellCase {
	private String cellName;
	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;
	
	public NonTerminalCellCase(String cellName) {
		this.cellName = cellName; //use this for identifier and external information
		
		//reference embryonic analogues cells db for homology
		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(this.cellName);
		
		buildTerminalDescendants();
	}
	
	private void buildTerminalDescendants() {
		if (terminalDescendants == null) {
			terminalDescendants = new ArrayList<TerminalDescendant>();
		}

		ArrayList<String> descendantsList = Search.getDescendantsList(this.cellName);
		
		//add each descendant as terminal descendant object
		for (String descendant : descendantsList) {
			terminalDescendants.add
						(new TerminalDescendant(descendant, PartsList.getDescriptionByLineageName(descendant)));
		}
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
}
