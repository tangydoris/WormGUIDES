package wormguides.model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import wormguides.Search;

/**
 * A non terminal cell object which contains the information for the Information Window feature
 * 
 * @author katzmanb
 *
 */
public class NonTerminalCellCase extends CellCase {

	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;

	/**
	 * 
	 * @param cellName
	 * @param nuclearProductionInfo the production information under Nuclear
	 * @param cellShapeProductionInfo the production information under Cell Shape
	 */
	public NonTerminalCellCase(String lineageName, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {
		super(lineageName, nuclearProductionInfo, cellShapeProductionInfo);

		// reference embryonic analogues cells db for homology
		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(getLineageName());

		this.terminalDescendants = buildTerminalDescendants();
		
		addLink(buildWormatlasLink());
	}

	/**
	 * Finds the terminal descendants of the cell using the parts list
	 * 
	 * @return the list of terminal descendants
	 */
	private ArrayList<TerminalDescendant> buildTerminalDescendants() {
		ArrayList<TerminalDescendant> terminalDescendants = new ArrayList<TerminalDescendant>();

		ArrayList<String> descendantsList = Search.getDescendantsList(getLineageName());

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

	private String buildWormatlasLink() {
		if (getLineageName() == null)
			return "";	
		
		String URL = wormatlasURL + getLineageName().toUpperCase() + wormatlasURLEXT;
		
		try {
			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			if (connection.getResponseCode() == 404) {
				return "";
			} else if (connection.getResponseCode() == 200) {
				return URL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(getLineageName() + " page not found on Wormatlas");
			return "";
		}

		return "";
	}


	public String getEmbryonicHomology() {
		return this.embryonicHomology;
	}

	public ArrayList<TerminalDescendant> getTerminalDescendants() {
		return this.terminalDescendants;
	}

	private final static String wormatlasURLEXT = "mainframe.htm";

}

//package wormguides.model;
//
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//import wormguides.Search;
//
///**
// * A non terminal cell object which contains the information for the Information Window feature
// * 
// * @author katzmanb
// *
// */
//public class NonTerminalCellCase {
//
//	private String lineageName; //shared
//	private String embryonicHomology;
//	private ArrayList<TerminalDescendant> terminalDescendants;
//	private ArrayList<String> links; //shared
//	private ArrayList<String> geneExpression; //shared
//	private ArrayList<String> references; //shared
//	private ArrayList<String> nuclearProductionInfo; //shared
//	private ArrayList<String> cellShapeProductionInfo; //shared
//
//	/**
//	 * 
//	 * @param cellName
//	 * @param nuclearProductionInfo the production information under Nuclear
//	 * @param cellShapeProductionInfo the production information under Cell Shape
//	 */
//	public NonTerminalCellCase(String lineageName, ArrayList<String> nuclearProductionInfo,
//			ArrayList<String> cellShapeProductionInfo) {
//		this.lineageName = lineageName; // use this for identifier and external information
//
//		// reference embryonic analogues cells db for homology
//		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(this.lineageName);
//
//		this.terminalDescendants = buildTerminalDescendants();
//
//		this.links = buildLinks();
//
//		this.geneExpression = setExpressionsFromWORMBASE();
//		this.references = setReferences();
//		this.nuclearProductionInfo = nuclearProductionInfo;
//		this.cellShapeProductionInfo = cellShapeProductionInfo;
//	}
//
//	/**
//	 * Finds the terminal descendants of the cell using the parts list
//	 * 
//	 * @return the list of terminal descendants
//	 */
//	private ArrayList<TerminalDescendant> buildTerminalDescendants() {
//		ArrayList<TerminalDescendant> terminalDescendants = new ArrayList<TerminalDescendant>();
//
//		ArrayList<String> descendantsList = Search.getDescendantsList(this.lineageName);
//
//		// add each descendant as terminal descendant object
//		for (String descendant : descendantsList) {
//			String partsListDescription = PartsList.getDescriptionByLineageName(descendant);
//			if (partsListDescription == null) {
//				if (CellDeaths.containsCell(descendant)) {
//					partsListDescription = "Cell Death";
//				} else {
//					partsListDescription = "";
//				}
//			}
//			terminalDescendants.add(new TerminalDescendant(descendant, partsListDescription));
//		}
//
//		return terminalDescendants;
//	}
//	
//	/**
//	 * 
//	 * @return the list of gene expressions from the wormbase page corresponding to this cell
//	 */
//	private ArrayList<String> setExpressionsFromWORMBASE() {
//		ArrayList<String> geneExpression = new ArrayList<String>();
//		
//		if (lineageName == null)
//			return geneExpression;
//
//		String URL = wormbaseURL + lineageName + wormbaseEXT;
//		System.out.println(URL);
//		
//		String content = "";
//		URLConnection connection = null;
//		
//		try {
//			connection = new URL(URL).openConnection();			
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//			
//		} catch (Exception e) {
//			//e.printStackTrace();
//			//a page wasn't found on wormatlas
//			System.out.println(lineageName + " page not found on Wormbase");
//			return geneExpression;
//		}
//		
//		//add the link to the list before parsing with cytoshow snippet (first link is more human readable)
//		links.add(URL);
//		
//		/*
//		 * Snippet adapted from cytoshow
//		 */
//		String[] logLines = content.split("wname=\"associations\"");
//		String restString = "";
//		if (logLines != null && logLines.length > 1 && logLines[1].split("\"").length > 1) {
//			restString = logLines[1].split("\"")[1];
//		}
//		
//		URL = "http://www.wormbase.org" + restString;
//		
//		try {
//			connection = new URL(URL).openConnection();			
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//		} catch (Exception e) {
//			//e.printStackTrace();
//			//a page wasn't found on wormatlas
//			System.out.println(this.lineageName + " page not found on Wormbase (second URL)");
//			
//			//remove the link
//			for (int i = 0; i < links.size(); i++) {
//				if (links.get(i).startsWith(wormbaseURL)) {
//					links.remove(i);
//				}
//					
//			}
//			
//			return geneExpression;
//		}
//		
//		//extract expressions
//		String[] genes = content.split("><");
//		for (String gene : genes) {
//			if (gene.startsWith("span class=\"locus\"")) {
//				gene = gene.substring(gene.indexOf(">")+1, gene.indexOf("<"));
//				geneExpression.add(gene);
//			}
//		}
//		
//		return geneExpression;
//	}
//	
//	/**
//	 * Finds the number of matches and documents for this cell on texpresso, and the first page of results
//	 *  
//	 * @return the number of matches, documents, and first page of results
//	 */
//	private ArrayList<String> setReferences() {
//		ArrayList<String> references = new ArrayList<String>();
//		
//		//open connection with the textpresso page
//		String URL = textpressoURL + this.lineageName + textpressoURLEXT;
//				
//		String content = "";
//		URLConnection connection = null;
//		
//		try {
//			connection = new URL(URL).openConnection();			
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//		} catch (Exception e) {
//			//e.printStackTrace();
//			//a page wasn't found on wormatlas
//			System.out.println(this.lineageName + " page not found on Textpresso");
//			return geneExpression;
//		}
//		
//		int matchesIDX = content.indexOf(" matches found in </span><span style=\"font-weight:bold;\">");
//		
//		if (matchesIDX > 0) {
//			matchesIDX--; //move back to the first digit
//			//find the start of the number of matches
//			String matchesStr = "";
//			for (;; matchesIDX--) {
//				char curr = content.charAt(matchesIDX);
//				if (Character.isDigit(curr)) {
//					matchesStr += curr;
//				} else {
//					break;
//				}
//			}
//			//reverse the string
//			matchesStr = new StringBuffer(matchesStr).reverse().toString();
//			
//			//find the number of documents
//			int documentsIDX = content.indexOf(" matches found in </span><span style=\"font-weight:bold;\">")+57;
//			
//			String documentsStr = "";
//			for (;; documentsIDX++) {
//				char curr = content.charAt(documentsIDX);
//				if (Character.isDigit(curr)) {
//					documentsStr += curr;
//				} else {
//					break;
//				}
//			}
//			
//			//add matches and documents to top of references list
//			references.add("<em>Textpresso</em>: " + matchesStr + " matches found in " + documentsStr + " documents");
//			/*
//			 * TODO
//			 * add textpresso url to page with open in browser
//			 */
//			
//			//parse the document for "Title: "
//			int lastIDX = 0;
//			while (lastIDX != -1) {
//				lastIDX = content.indexOf(textpressoTitleStr, lastIDX);
//				
//				if (lastIDX != -1) {
//					lastIDX += textpressoTitleStr.length(); //skip the title just seen
//					
//					//extract the title
//					String title = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
//					
//					//move the index past the authors section
//					while (!content.substring(lastIDX).startsWith(textpressoAuthorsStr)) lastIDX++;
//					
//					lastIDX += textpressoAuthorsStr.length();
//					
//					//extract the authors
//					String authors = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
//					
//					
//					//move the index past the year section
//					while (!content.substring(lastIDX).startsWith(textpressoYearStr)) lastIDX++;
//					
//					lastIDX += textpressoYearStr.length();
//					
//					//extract the year
//					String year = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
//					
//					String reference = title + authors + ", " + year;
//					
//					//update anchors
//					reference = updateAnchors(reference);
//					
//					references.add(reference);
//				}
//			}
//		}
//		
//		//"<em>Source: </em><a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a>
//		
//		//add the source
//		String source = "<em>Source:</em> <a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a>";
//		references.add(source);
//		
//		links.add(URL);
//		return references;
//	}
//
//	private ArrayList<String> buildLinks() {
//		ArrayList<String> links = new ArrayList<String>();
//
//		links.add(buildWormatlasLink());
//		links.add(addGoogleLink());
//		links.add(addGoogleWormatlasLink());
//
//		return links;
//	}
//
//	private String buildWormatlasLink() {
//		if (this.lineageName == null)
//			return "";	
//		String URL = wormatlasURL + lineageName.toUpperCase() + wormatlasURLEXT;
//		
//		try {
//			URL url = new URL(URL);
//			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//			connection.setRequestMethod("GET");
//			connection.connect();
//			
//			if (connection.getResponseCode() == 404) {
//				return "";
//			} else if (connection.getResponseCode() == 200) {
//				return URL;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(this.lineageName + " page not found on Wormatlas");
//			return "";
//		}
//
//		return "";
//	}
//
//	private String addGoogleLink() {
//		if (this.lineageName != null) {
//			return googleURL + this.lineageName + "+c.+elegans";
//		}
//
//		return "";
//	}
//
//	private String addGoogleWormatlasLink() {
//		if (this.lineageName != null) {
//			return googleWormatlasURL + this.lineageName;
//		}
//
//		return "";
//	}
//	
//	private String updateAnchors(String content) {
//		/*
//		 * find the anchor tags and change to:
//		 *  "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
//		 *  paradigm
//		 */
//		String findStr = "<a ";
//		int lastIdx = 0;
//		
//		while (lastIdx != -1) {
//			lastIdx = content.indexOf(findStr, lastIdx);
//
//			//check if another anchor found
//			if (lastIdx != -1) {
//				//save the string preceding the anchor
//				String precedingStr = content.substring(0, lastIdx);
//				
//				
//				//find the end of the anchor and extract the anchor
//				int anchorEndIdx = content.indexOf(anchorClose, lastIdx);
//				String anchor = content.substring(lastIdx, anchorEndIdx + anchorClose.length());
//				
//				//extract the source href --> "href=\""
//				boolean isLink = true;
//				int startSrcIdx = anchor.indexOf(href) + href.length();
//				
//				String src = "";
//				//make sure not a citation i.e. first character is '#'
//				if (anchor.charAt(startSrcIdx) == '#') {
//					isLink = false;
//				} else {
//					src = anchor.substring(startSrcIdx, anchor.indexOf("\"", startSrcIdx));
//				}
//				
//				if (isLink) {
//					//check if relative src
//					if (!src.contains("www.") && !src.contains("http")) {
//						//remove path
//						if (src.contains("..")) {
//							src = src.substring(src.lastIndexOf("/") + 1);
//						}
//						src = wormatlasURL + src;
//					}
//					
//					//extract the anchor text --> skip over the first <
//					String text = anchor.substring(anchor.indexOf(">") + 1, anchor.substring(1).indexOf("<") + 1);
//					
//					// build new anchor
//					String newAnchor = "<a href=\"#\" name=\"" + src + "\" onclick=\"handleLink(this)\">" + text + "</a>";
//
//					
//					//replace previous anchor
//					content = precedingStr + newAnchor + content.substring(anchorEndIdx + anchorClose.length());
//				} else {
//					//remove anchor
//					String txt = anchor.substring(anchor.indexOf(">") + 1, anchor.substring(1).indexOf("<") + 1);
//					
//					content = precedingStr + txt + content.substring(anchorEndIdx + anchorClose.length());
//				}
//				
//				
//				//move lastIdx past just processed anchor
//				lastIdx += findStr.length();
//			}
//		}
//		
//		return content;
//	}
//
//
//	public String getLineageName() {
//		return this.lineageName;
//	}
//
//	public String getEmbryonicHomology() {
//		return this.embryonicHomology;
//	}
//
//	public ArrayList<TerminalDescendant> getTerminalDescendants() {
//		return this.terminalDescendants;
//	}
//	
//	public ArrayList<String> getExpressesWORMBASE() {
//		return geneExpression;
//	}
//	
//	public ArrayList<String> getReferences() {
//		return references;
//	}
//
//	public ArrayList<String> getLinks() {
//		return this.links;
//	}
//
//	public ArrayList<String> getNuclearProductionInfo() {
//		return this.nuclearProductionInfo;
//	}
//
//	public ArrayList<String> getCellShapeProductionInfo() {
//		return this.cellShapeProductionInfo;
//	}
//
//	private final static String wormbaseURL = "http://www.wormbase.org/db/get?name=";
//	private final static String wormbaseEXT = ";class=Anatomy_term";
//	private final static String wormatlasURL = "http://www.wormatlas.org/neurons/Individual%20Neurons/";
//	private final static String wormatlasURLEXT = "mainframe.htm";
//	private final static String textpressoURL = "http://textpresso-www.cacr.caltech.edu/cgi-bin/celegans/search?searchstring=";
//	private final static String textpressoURLEXT = ";cat1=Select%20category%201%20from%20list%20above;cat2=Select%20category%202%20from%20list%20above;cat3=Select%20category%203%20from%20list%20above;cat4=Select%20category%204%20from%20list%20above;cat5=Select%20category%205%20from%20list%20above;search=Search!;exactmatch=on;searchsynonyms=on;literature=C.%20elegans;target=abstract;target=body;target=title;target=introduction;target=materials;target=results;target=discussion;target=conclusion;target=acknowledgments;target=references;sentencerange=sentence;sort=score%20(hits);mode=boolean;authorfilter=;journalfilter=;yearfilter=;docidfilter=;";
//	private final static String textpressoTitleStr = "Title: </span>";
//	private final static String textpressoAuthorsStr = "Authors: </span>";
//	private final static String textpressoYearStr = "Year: </span>";
//	private final static String googleURL = "https://www.google.com/#q=";
//	private final static String googleWormatlasURL = "https://www.google.com/#q=site:wormatlas.org+";
//	private final static String anchorClose = "</a>";
//	private final static String href = "href=\"";
//}
