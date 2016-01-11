package wormguides.model;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class TerminalCellCase {
	private String cellName;
	private String externalInfo;
	private String partsListDescription;
	private String imageURL;
	private String functionWORMATLAS;
	
	private ArrayList<String> presynapticPartners;
	private ArrayList<String> postsynapticPartners;
	private ArrayList<String> electricalPartners;
	private ArrayList<String> neuromuscularPartners;
	
	private ArrayList<String> anatomy;
	private ArrayList<String> expressesWORMBASE;
	private ArrayList<String> homologues;
	private ArrayList<String> referencesTEXTPRESSO;
	private ArrayList<String> links;
	
	public TerminalCellCase(String cellName, ArrayList<String> presynapticPartners, ArrayList<String> postsynapticPartners,
			ArrayList<String> electricalPartners, ArrayList<String> neuromuscularPartners) {
		this.cellName = cellName;
		this.externalInfo = this.cellName + " (" + PartsList.getLineageNameByFunctionalName(cellName) + ")";
		this.partsListDescription = PartsList.getDescriptionByFunctionalName(cellName);
		this.imageURL = graphicURL + cellName.toUpperCase() + jpgEXT; 
		setFunctionFromWORMATLAS();
		
		this.presynapticPartners = presynapticPartners;
		this.postsynapticPartners = postsynapticPartners;
		this.electricalPartners = electricalPartners;
		this.neuromuscularPartners = neuromuscularPartners;
		
		//FIGURE OUT HOW TO GENERATE THESE
		this.anatomy = new ArrayList<String>();
		
		setExpressions();
		
		this.expressesWORMBASE = new ArrayList<String>();
		this.homologues = new ArrayList<String>();
		this.referencesTEXTPRESSO = new ArrayList<String>();
		this.links = new ArrayList<String>();
		
		/*
		 * testing purposes
		 */
		anatomy.add("anatomy entry");
		anatomy.add("another anatomy entry");
		expressesWORMBASE.add("expresses entry");
		expressesWORMBASE.add("woah look! another Expresses entry");
		homologues.add("homologues entry");
		homologues.add("second homologue");
		referencesTEXTPRESSO.add("references entry");
		referencesTEXTPRESSO.add("second reference");
		links.add("link entry");
		links.add("LINK");
	}
	
	private void setFunctionFromWORMATLAS() {
		if (this.cellName == null) return;
		
		String content = "";
		URLConnection connection = null;
		
		//extract root of cell name e.g. ribr --> RIB
		String URL = wormatlasURL + 
				this.cellName.substring(0, this.cellName.length()-1).toUpperCase() + 
				wormatlasURLEXT;
		
		try {
			connection = new URL(URL).openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
			//e.printStackTrace();
			//a page wasn't found on wormatlas
			this.functionWORMATLAS = this.cellName + " page not found on Wormatlas";
			return;
		}

		//parse the html for "Function"
		content = content.substring(content.indexOf("Function"));
		content = content.substring(content.indexOf(":")+1, content.indexOf("</td>")); //skip the "Function:" text
		this.functionWORMATLAS = content;
	}
	
	private void setExpressions() {
		if (this.cellName == null) return;
		
		String URL = "http://www.wormbase.org/db/get?name=" + 
		this.cellName + ";class=Anatomy_term\");";
		
		System.out.println(URL);
				
//				File.openUrlAsString(\"http://www.wormbase.org/db/get?name="
//						+ cellName
//						+ ";class=Anatomy_term\");"
//						+ "print(string);");
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
	
	public ArrayList<String> getAnatomy() {
		return this.anatomy;
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
	
	private final static String graphicURL = "http://wormwiring.hpc.einstein.yu.edu/data/ccimages/";
	private final static String jpgEXT = ".jpg";
	
	private final static String wormatlasURL = "http://www.wormatlas.org/neurons/Individual%20Neurons/";
	private final static String wormatlasURLEXT = "mainframe.htm";
}