package wormguides.model;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import wormguides.Search;

/**
 * A non terminal cell object which contains the information for the Information Window feature
 * 
 * @author katzmanb
 *
 */
public class NonTerminalCellCase {

	private String cellName;
	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;
	private ArrayList<String> links;
	private ArrayList<String> geneExpression;
	private ArrayList<String> references;
	private ArrayList<String> nuclearProductionInfo;
	private ArrayList<String> cellShapeProductionInfo;

	/**
	 * 
	 * @param cellName
	 * @param nuclearProductionInfo the production information under Nuclear
	 * @param cellShapeProductionInfo the production information under Cell Shape
	 */
	public NonTerminalCellCase(String cellName, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {
		this.cellName = cellName; // use this for identifier and external
									// information

		// reference embryonic analogues cells db for homology
		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(this.cellName);

		this.terminalDescendants = buildTerminalDescendants();

		this.links = buildLinks();

		this.geneExpression = setExpressionsFromWORMBASE();
		this.references = setReferences();
		this.nuclearProductionInfo = nuclearProductionInfo;
		this.cellShapeProductionInfo = cellShapeProductionInfo;
	}

	public String getLineageName() {
		return cellName;
	}

	/**
	 * Finds the terminal descendants of the cell using the parts list
	 * 
	 * @return the list of terminal descendants
	 */
	private ArrayList<TerminalDescendant> buildTerminalDescendants() {
		ArrayList<TerminalDescendant> terminalDescendants = new ArrayList<TerminalDescendant>();

		ArrayList<String> descendantsList = Search.getDescendantsList(this.cellName);

		// add each descendant as terminal descendant object
		for (String descendant : descendantsList) {
			String partsListDescription = PartsList.getDescriptionByLineageName(descendant);
			if (partsListDescription == null) {
				if (CellDeaths.containsCell(descendant)) {
					partsListDescription = "Cell Death";
				} else {
					partsListDescription = "";
				}
			}
			terminalDescendants.add(new TerminalDescendant(descendant, partsListDescription));
		}

		return terminalDescendants;
	}
	
	/**
	 * 
	 * @return the list of gene expressions from the wormbase page corresponding to this cell
	 */
	private ArrayList<String> setExpressionsFromWORMBASE() {
		ArrayList<String> geneExpression = new ArrayList<String>();
		
		if (cellName == null)
			return geneExpression;

		String URL = wormbaseURL + cellName + wormbaseEXT;
		
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
			System.out.println(cellName + " page not found on Wormbase");
			return geneExpression;
		}
		
		//add the link to the list before parsing with cytoshow snippet (first link is more human readable)
		links.add(URL);
		
		/*
		 * Snippet adapted from cytoshow
		 */
		String[] logLines = content.split("wname=\"associations\"");
		String restString = "";
		if (logLines != null && logLines.length > 1 && logLines[1].split("\"").length > 1) {
			restString = logLines[1].split("\"")[1];
		}
		
		URL = "http://www.wormbase.org" + restString;
		
		try {
			connection = new URL(URL).openConnection();			
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
			//e.printStackTrace();
			//a page wasn't found on wormatlas
			System.out.println(this.cellName + " page not found on Wormbase (second URL)");
			
			//remove the link
			for (int i = 0; i < links.size(); i++) {
				if (links.get(i).startsWith(wormbaseURL)) {
					links.remove(i);
				}
					
			}
			
			return geneExpression;
		}
		
		//extract expressions
		String[] genes = content.split("><");
		for (String gene : genes) {
			if (gene.startsWith("span class=\"locus\"")) {
				gene = gene.substring(gene.indexOf(">")+1, gene.indexOf("<"));
				geneExpression.add(gene);
			}
		}
		
		return geneExpression;
	}
	
	/**
	 * Finds the number of matches and documents for this cell on texpresso, and the first page of results
	 *  
	 * @return the number of matches, documents, and first page of results
	 */
	private ArrayList<String> setReferences() {
		ArrayList<String> references = new ArrayList<String>();
		
		//open connection with the textpresso page
		String URL = textpressoURL + this.cellName + textpressoURLEXT;
				
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
			System.out.println(this.cellName + " page not found on Textpresso");
			return geneExpression;
		}
		
		int matchesIDX = content.indexOf(" matches found in </span><span style=\"font-weight:bold;\">");
		
		if (matchesIDX > 0) {
			matchesIDX--; //move back to the first digit
			//find the start of the number of matches
			String matchesStr = "";
			for (;; matchesIDX--) {
				char curr = content.charAt(matchesIDX);
				if (Character.isDigit(curr)) {
					matchesStr += curr;
				} else {
					break;
				}
			}
			//reverse the string
			matchesStr = new StringBuffer(matchesStr).reverse().toString();
			
			//find the number of documents
			int documentsIDX = content.indexOf(" matches found in </span><span style=\"font-weight:bold;\">")+57;
			
			String documentsStr = "";
			for (;; documentsIDX++) {
				char curr = content.charAt(documentsIDX);
				if (Character.isDigit(curr)) {
					documentsStr += curr;
				} else {
					break;
				}
			}
			
			//add matches and documents to top of references list
			references.add("<em>Textpresso</em>: " + matchesStr + " matches found in " + documentsStr + " documents");
			/*
			 * TODO
			 * add textpresso url to page with open in browser
			 */
			
			//parse the document for "Title: "
			int lastIDX = 0;
			while (lastIDX != -1) {
				lastIDX = content.indexOf(textpressoTitleStr, lastIDX);
				
				if (lastIDX != -1) {
					lastIDX += textpressoTitleStr.length(); //skip the title just seen
					
					//extract the title
					String title = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
					//System.out.println(title);
					
					//move the index past the authors section
					while (!content.substring(lastIDX).startsWith(textpressoAuthorsStr)) lastIDX++;
					
					lastIDX += textpressoAuthorsStr.length();
					
					//extract the authors
					String authors = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
					
					
					//move the index past the year section
					while (!content.substring(lastIDX).startsWith(textpressoYearStr)) lastIDX++;
					
					lastIDX += textpressoYearStr.length();
					
					//extract the year
					String year = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
					
					String reference = title + authors + ", " + year;
					references.add(reference);
				}
			}
		}
		
		//"<em>Source: </em><a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a>
		
		//add the source
		String source = "<em>Source:</em> <a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a>";
		references.add(source);
		
		links.add(URL);
		return references;
	}

	private ArrayList<String> buildLinks() {
		ArrayList<String> links = new ArrayList<String>();

		links.add(buildWORMBASELink());
		links.add(addGoogleLink());
		links.add(addGoogleWormatlasLink());

		return links;
	}

	private String buildWORMBASELink() {
		if (this.cellName == null)
			return "";

		String URL = wormbaseURL + this.cellName + wormbaseEXT;

		try {
			// URLConnection connection = new URL(URL).openConnection();
		} catch (Exception e) {
			// e.printStackTrace();
			// a page wasn't found on wormatlas
			System.out.println(this.cellName + " page not found on Wormbase");
		}

		return URL;
	}

	private String addGoogleLink() {
		if (this.cellName != null) {
			return googleURL + this.cellName + "+c.+elegans";
		}

		return "";
	}

	private String addGoogleWormatlasLink() {
		if (this.cellName != null) {
			return googleWormatlasURL + this.cellName;
		}

		return "";
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
	
	public ArrayList<String> getExpressesWORMBASE() {
		return geneExpression;
	}
	
	public ArrayList<String> getReferences() {
		return references;
	}

	public ArrayList<String> getLinks() {
		return this.links;
	}

	public ArrayList<String> getNuclearProductionInfo() {
		return this.nuclearProductionInfo;
	}

	public ArrayList<String> getCellShapeProductionInfo() {
		return this.cellShapeProductionInfo;
	}

	private final static String wormbaseURL = "http://www.wormbase.org/db/get?name=";
	private final static String wormbaseEXT = ";class=Anatomy_term";
	private final static String textpressoURL = "http://textpresso-www.cacr.caltech.edu/cgi-bin/celegans/search?searchstring=";
	private final static String textpressoURLEXT = ";cat1=Select%20category%201%20from%20list%20above;cat2=Select%20category%202%20from%20list%20above;cat3=Select%20category%203%20from%20list%20above;cat4=Select%20category%204%20from%20list%20above;cat5=Select%20category%205%20from%20list%20above;search=Search!;exactmatch=on;searchsynonyms=on;literature=C.%20elegans;target=abstract;target=body;target=title;target=introduction;target=materials;target=results;target=discussion;target=conclusion;target=acknowledgments;target=references;sentencerange=sentence;sort=score%20(hits);mode=boolean;authorfilter=;journalfilter=;yearfilter=;docidfilter=;";
	private final static String textpressoTitleStr = "Title: </span>";
	private final static String textpressoAuthorsStr = "Authors: </span>";
	private final static String textpressoYearStr = "Year: </span>";
	private final static String googleURL = "https://www.google.com/#q=";
	private final static String googleWormatlasURL = "https://www.google.com/#q=site:wormatlas.org+";
}
