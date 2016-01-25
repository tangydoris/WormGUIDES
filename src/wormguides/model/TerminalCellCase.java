package wormguides.model;

import java.io.InputStream;
import java.io.InputStreamReader;
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
	private ArrayList<String> geneExpression;
	private ArrayList<String> homologues;
	private ArrayList<String> references;
	private ArrayList<String> links;
	
	public TerminalCellCase(String cellName, ArrayList<String> presynapticPartners, ArrayList<String> postsynapticPartners,
			ArrayList<String> electricalPartners, ArrayList<String> neuromuscularPartners) {
		this.links = new ArrayList<String>();
		this.cellName = cellName;
		this.externalInfo = this.cellName + " (" + PartsList.getLineageNameByFunctionalName(cellName) + ")";
		this.partsListDescription = PartsList.getDescriptionByFunctionalName(cellName);
		this.imageURL = graphicURL + cellName.toUpperCase() + jpgEXT; 
		
		//parse wormatlas for the "Function" field
		setFunctionFromWORMATLAS();
		
		this.presynapticPartners = presynapticPartners;
		this.postsynapticPartners = postsynapticPartners;
		this.electricalPartners = electricalPartners;
		this.neuromuscularPartners = neuromuscularPartners;
		
		//FIGURE OUT HOW TO GENERATE THESE
		this.anatomy = new ArrayList<String>();
		
		//set expressions
		setExpressionsFromWORMBASE();
		
		this.geneExpression = new ArrayList<String>();
		this.homologues = new ArrayList<String>();
		this.references = new ArrayList<String>();

		
		/*
		 * testing purposes
		 */
		anatomy.add("anatomy entry");
		anatomy.add("another anatomy entry");
		geneExpression.add("WORMBASE"); //how do we want to use Wormbase here???
		geneExpression.add("expresses entry 2");
		homologues.add("homologues entry");
		homologues.add("second homologue entry");
		references.add("TEXTPRESSO"); //how do we want to use TEXTPRESSO here???
		references.add("second reference");
		links.add("Cytoshow: [cytoshow link to this cell in EM data]");

	}
	
	private void setFunctionFromWORMATLAS() {
		if (this.cellName == null) return;
		
		String content = "";
		URLConnection connection = null;
		
		/* 
		 * USING mainframe.htm EXT
		 * Leaving code for frameset.htm check
		 */
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
			//try second extension
//			URL = wormatlasURL + 
//					this.cellName.substring(0, this.cellName.length()-1).toUpperCase() + 
//					wormatlasURLEXT2;
//			try {
//				connection = new URL(URL).openConnection();
//				Scanner scanner = new Scanner(connection.getInputStream());
//				scanner.useDelimiter("\\Z");
//				content = scanner.next();
//				scanner.close();
//			} catch (Exception e1) {
//				//e1.printStackTrace();
//				//a page wasn't found on wormatlas
//				this.functionWORMATLAS = this.cellName + " page not found on Wormatlas";
//				return;
//			}
			
			this.functionWORMATLAS = this.cellName + " page not found on Wormatlas";
			return;
		}

		//parse the html for "Function"
		content = content.substring(content.indexOf("Function"));
		content = content.substring(content.indexOf(":")+1, content.indexOf("</td>")); //skip the "Function:" text
		this.functionWORMATLAS = "<a href=\"" + URL + "\">" + URL + "</a><br><br>" + content;
		
		//add the link to the list
		links.add(URL);
	}
	
	private void setExpressionsFromWORMBASE() {
		if (this.cellName == null) return;
		
		String URL = "http://www.wormbase.org/db/get?name=" + 
		this.cellName + ";class=Anatomy_term";
		
		String content = "";
		URLConnection connection = null;
		
		try {
			connection = new URL(URL).openConnection();			
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
			//e.printStackTrace();
			//a page wasn't found on wormatlas
			this.functionWORMATLAS = this.cellName + " page not found on Wormbase";
			return;
		}
		
		//parse the html
		
		//add the link to the list
		links.add(URL);
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
		return this.geneExpression;
	}
	
	public ArrayList<String> getHomologues() {
		return this.homologues;
	}
	
	public ArrayList<String> getReferences() {
		return this.references;
	}
	
	public ArrayList<String> getLinks() {
		return this.links;
	}
	
	private final static String graphicURL = "http://wormwiring.hpc.einstein.yu.edu/data/ccimages/";
	private final static String jpgEXT = ".jpg";
	
	private final static String wormatlasURL = "http://www.wormatlas.org/neurons/Individual%20Neurons/";
	private final static String wormatlasURLEXT = "mainframe.htm";
	private final static String wormatlasURLEXT2 = "frameset.html";
}