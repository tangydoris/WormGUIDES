package wormguides.model;

import java.util.ArrayList;

public class NonTerminalCellCase {
	private String cellName;
	private String externalInfo;
	private String embyonicHomology;
	private ArrayList<String> terminalDescendants; //just one or multiple?
	private ArrayList<String> descendantsPartsListEntries;
	
	public NonTerminalCellCase(String cellName) {
		this.cellName = cellName;
		terminalDescendants = new ArrayList<String>();
		descendantsPartsListEntries = new ArrayList<String>();
	}
	
	public String getCellName() {
		return this.cellName;
	}
	
	public void loadHomologues() {
		//separate method for open file - load jar entry
		
	}
}
