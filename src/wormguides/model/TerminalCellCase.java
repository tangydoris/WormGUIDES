package wormguides.model;

import java.util.ArrayList;

public class TerminalCellCase {
	private String cellName;
	private String externalInfo;
	private String partsListDescription;
	private String imageURL;
	private String functionWORMATLAS;
	private String anatomy;
	private String wiring;
	private String expressesWORMBASE;
	private ArrayList<String> homologues;
	private ArrayList<String> referencesTEXTPRESSO;
	private ArrayList<String> links;
	
	public TerminalCellCase(String cellName) {
		this.cellName = cellName;
		homologues = new ArrayList<String>();
		referencesTEXTPRESSO = new ArrayList<String>();
		links = new ArrayList<String>();
	}
	
	public String getCellName() {
		return this.cellName;
	}
}