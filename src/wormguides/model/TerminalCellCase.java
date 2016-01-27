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
	private ArrayList<String> geneExpression;
	private ArrayList<ArrayList<String>> homologues; //homologues[0] will contain L/R homologues, homologues[1] will contain additional symmetries
	private ArrayList<String> references;
	private ArrayList<String> links;
	
	public TerminalCellCase(String cellName, ArrayList<String> presynapticPartners, 
			ArrayList<String> postsynapticPartners,ArrayList<String> electricalPartners, 
			ArrayList<String> neuromuscularPartners) {
		
		this.links = new ArrayList<String>();
		
		this.cellName = cellName;
		this.externalInfo = this.cellName + " (" + PartsList.getLineageNameByFunctionalName(cellName) + ")";
		this.partsListDescription = PartsList.getDescriptionByFunctionalName(cellName);
		this.imageURL = graphicURL + cellName.toUpperCase() + jpgEXT; 
		
		//parse wormatlas for the "Function" field
		this.functionWORMATLAS = setFunctionFromWORMATLAS();
		
		//set the wiring partners from connectome
		this.presynapticPartners = presynapticPartners;
		this.postsynapticPartners = postsynapticPartners;
		this.electricalPartners = electricalPartners;
		this.neuromuscularPartners = neuromuscularPartners;
		
		/*
		 * TODO
		 */
		this.anatomy = setAnatomy();
		this.geneExpression = setExpressionsFromWORMBASE();
		this.homologues = setHomologues();
		this.references = setReferences();

		
		/*
		 * testing purposes
		 */
		links.add("Cytoshow: [cytoshow link to this cell in EM data]");

	}
	
	private String setFunctionFromWORMATLAS() {
		if (this.cellName == null) return "";
		
		String content = "";
		URLConnection connection = null;
		
		/* 
		 * USING mainframe.htm EXT
		 * Leaving code for frameset.htm check
		 */
		
		/*
		 * if R/L cell, find base name for URL
		 * e.g. ribr --> RIB
		 * 
		 * if no R/L, leave as is
		 * e.g. AVG
		 */
		String cell = this.cellName;
		Character lastChar = cell.charAt(cell.length()-1);
		lastChar = Character.toLowerCase(lastChar);
		if (lastChar == 'r' || lastChar == 'l') {
			cell = cell.substring(0, cell.length()-1);
		}
		String URL = wormatlasURL + cell + wormatlasURLEXT;
		
		try {
			connection = new URL(URL).openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
			//try second extension
			URL = wormatlasURL + cell.substring(0, cell.length()-1).toUpperCase() + "N" + wormatlasURLEXT;
			//System.out.println("TRYING SECOND URL: " + URL);

			try {
				connection = new URL(URL).openConnection();
				Scanner scanner = new Scanner(connection.getInputStream());
				scanner.useDelimiter("\\Z");
				content = scanner.next();
				scanner.close();
			} catch (Exception e1) {
				//e1.printStackTrace();
				//a page wasn't found on wormatlas
				return this.cellName + " page not found on Wormatlas";
			}
		}
		return findFunctionInHTML(content, URL);
	}
	
	private String findFunctionInHTML(String content, String URL) {
		//parse the html for "Function"
		content = content.substring(content.indexOf("Function"));
		content = content.substring(content.indexOf(":")+1, content.indexOf("</td>")); //skip the "Function:" text

		//add the link to the list
		links.add(URL);
		
		return "<a href=\"" + URL + "\">" + URL + "</a><br><br>" + content;
	}
	
	private ArrayList<String> setAnatomy() {
		ArrayList<String> anatomy = new ArrayList<String>();
		
		if (this.cellName == null) return anatomy;
		
		
		/*
		 * TESTING PURPOSES
		 */
		anatomy.add("anatomy entry");
		anatomy.add("another anatomy entry");
		
		return anatomy;
	}
	
	private ArrayList<String> setExpressionsFromWORMBASE() {
		ArrayList<String> geneExpression = new ArrayList<String>();
		
		if (this.cellName == null) return geneExpression;
		
		/*
		 * FOR TESTING PURPOSES
		 */
		geneExpression.add("WORMBASE"); //how do we want to use Wormbase here???
		geneExpression.add("expresses entry 2");
		
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
			System.out.println(this.cellName + " page not found on Wormbase");
			return geneExpression;
		}
		
		/*
		 * TODO
		 * add expressions 
		 * - generate list from cytoshow?
		 */
		
		//add the link to the list
		links.add(URL);
		
		return geneExpression;
	}
	
	private ArrayList<ArrayList<String>> setHomologues() {
		ArrayList<ArrayList<String>> homologues = new ArrayList<ArrayList<String>>();
		ArrayList<String> leftRightHomologues = new ArrayList<String>();
		ArrayList<String> additionalSymmetries = new ArrayList<String>();
		
		if (this.cellName == null) return homologues;
		
		char lastChar = cellName.charAt(cellName.length()-1);
		lastChar = Character.toLowerCase(lastChar);
		
		String cell = this.cellName;
		//check for left, right, dorsal, or ventral suffix --> update cell
		if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v') {
			cell = cell.substring(0, cell.length()-1);
		} else if (Character.isDigit(lastChar)) { //check for # e.g. DD1 --> update cell
			cell = cell.substring(0, cell.length()-1);
		}
		
		cell = cell.toLowerCase();
		
		//search parts list for matching prefix terms
		ArrayList<String> partsListHits = new ArrayList<String>();
		for (String lineageName : PartsList.getLineageNames()) {
			lineageName = PartsList.getFunctionalNameByLineageName(lineageName);
			if (lineageName.toLowerCase().startsWith(cell)) {
				partsListHits.add(lineageName);
			}
		}
		
		/*
		 * Add hits to categories:
		 * L/R: ends with l/r
		 * AdditionalSymm: ends with d/v
		 */
		for (String lineageName : partsListHits) {
			lastChar = lineageName.charAt(lineageName.length()-1);
			lastChar = Character.toLowerCase(lastChar);
			System.out.println(lastChar);
			if (lastChar == 'l' || lastChar == 'r') {
				leftRightHomologues.add(lineageName);
			} else if (lastChar == 'd' || lastChar == 'v' || Character.isDigit(lastChar)) {
				additionalSymmetries.add(lineageName);
			}
		}
		
		homologues.add(leftRightHomologues);
		homologues.add(additionalSymmetries);
		
		return homologues;
	}
	
	private ArrayList<String> setReferences() {
		ArrayList<String> references = new ArrayList<String>();
		
		references.add("TEXTPRESSO"); //how do we want to use TEXTPRESSO here???
		references.add("second reference");
		
		return references;
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
	
	public ArrayList<ArrayList<String>> getHomologues() {
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