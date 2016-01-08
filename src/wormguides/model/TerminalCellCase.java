package wormguides.model;

import java.util.ArrayList;

public class TerminalCellCase {
	private String cellName;
	private String externalInfo;
	private String partsListDescription;
	private String imageURL;
	private String functionWORMATLAS;
	private String anatomy;
	
	private ArrayList<String> presynapticPartners;
	private ArrayList<String> postsynapticPartners;
	private ArrayList<String> electricalPartners;
	private ArrayList<String> neuromuscularPartners;
	
	private ArrayList<String> expressesWORMBASE;
	private ArrayList<String> homologues;
	private ArrayList<String> referencesTEXTPRESSO;
	private ArrayList<String> links;
	
	public TerminalCellCase(String cellName, ArrayList<String> presynapticPartners, ArrayList<String> postsynapticPartners,
			ArrayList<String> electricalPartners, ArrayList<String> neuromuscularPartners) {
		this.cellName = cellName;
		this.externalInfo = this.cellName + " (" + PartsList.getLineageNameByFunctionalName(cellName) + ")";
		this.partsListDescription = PartsList.getDescriptionByFunctionalName(cellName);
		this.imageURL = "WILL BE THE IMAGE URL";
		
		this.presynapticPartners = presynapticPartners;
		this.postsynapticPartners = postsynapticPartners;
		this.electricalPartners = electricalPartners;
		this.neuromuscularPartners = neuromuscularPartners;
		
		//FIGURE OUT HOW TO GENERATE THESE
		this.expressesWORMBASE = new ArrayList<String>();
		this.homologues = new ArrayList<String>();
		this.referencesTEXTPRESSO = new ArrayList<String>();
		this.links = new ArrayList<String>();
	}
	
	public String getCellName() {
		if (this.cellName != null) {
			return this.cellName;
		}
		return "";
	}
	
	public String getExternalInfo() {
		if (this.externalInfo != null) {
			return this.externalInfo;
		}
		return "";
	}
	
	public String getPartsListDescription() {
		if (this.partsListDescription != null) {
			return this.partsListDescription;
		}
		return "";
	}
	
	public String getImageURL() {
		if (this.imageURL != null) {
			return this.imageURL;
		}
		return "";
	}
	
	public String getFunctionWORMATLAS() {
		if (this.functionWORMATLAS != null) {
			return this.functionWORMATLAS;
		}
		return "";
	}
	
	public String getAnatomy() {
		if (this.anatomy != null) {
			return this.anatomy;
		}
		return "";
	}
	
	public ArrayList<String> getPresynapticPartners() {
		return this.presynapticPartners;
	}
	
	public ArrayList<String> getPostsynapticPartners() {
		return this.postsynapticPartners;
	}
	
	public ArrayList<String> getElectricalPartners() {
		return this.electricalPartners;
	}
	
	public ArrayList<String> getNeuromuscularPartners() {
		return this.neuromuscularPartners;
	}
	
	public ArrayList<String> getExpressesWORMBASE() {
		return this.expressesWORMBASE;
	}
	
	public ArrayList<String> getHomologues() {
		return this.homologues;
	}
	
	public ArrayList<String> getReferencesTEXTPRESSO() {
		return this.referencesTEXTPRESSO;
	}
	
	public ArrayList<String> getLinks() {
		return this.links;
	}
}