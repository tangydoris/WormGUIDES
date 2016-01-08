package wormguides.view;

import java.util.ArrayList;
import java.util.Collections;

import wormguides.model.NonTerminalCellCase;
import wormguides.model.TerminalCellCase;

public class InfoWindowDOM {
	private HTMLNode html;
	private String name;

	//other dom uses: connectome, parts list, cell shapes index
	
	/*
	 * TODO
	 * getNode(String ID)
	 * removeNode(String ID)
	 * addChildToNode(String parentID, HTMLNode child) -- need this?
	 * add title tag to head
	 * 
	 */
	public InfoWindowDOM() {
		this.html = new HTMLNode("html");
		this.name = "CELL TITLE";
	}
	
	//pass the cell name as a string which will be the name at the top of the tab
	public InfoWindowDOM(HTMLNode html) {
		if (!html.getTag().equals("html")) {
			this.html = new HTMLNode("html");
		} else {
			this.html = html;
		}

		this.name = "CELL TITLE";
	}
	
	/*
	 * TERMINAL CELL CASE
	 */
	public InfoWindowDOM(TerminalCellCase terminalCase) {
		this.html = new HTMLNode("html");
		//this.webView = new WebView();
		this.name = terminalCase.getCellName();
	
		/*
		 * TODO
		 * construct the dom from terminal cell case accessor methods
		 */
		HTMLNode head = new HTMLNode("head");
		
		/*
		 * TODO
		 * meta tags
		 * title
		 */
		
		HTMLNode body = new HTMLNode("body");
		
		//divs
		HTMLNode topContainerDiv = new HTMLNode("div", "topContainer", "width: 50%; height: 10%; float: left;"); //will contain external info and parts list description. float left for img on right
		HTMLNode externalInfoDiv = new HTMLNode("div", "externalInfo", ""); 
		HTMLNode partsListDescrDiv = new HTMLNode("div", "partsListDescr", "");
		topContainerDiv.addChild(externalInfoDiv);
		topContainerDiv.addChild(partsListDescrDiv);
		
		HTMLNode imgDiv = new HTMLNode("div", "imgDiv", "width: 50%; height: 10%; float: left;");
		
		HTMLNode functionWORMATLASDiv = new HTMLNode("div", "functionWORMATLAS", "");
		HTMLNode anatomyDiv = new HTMLNode("div", "anatomy", "");
		HTMLNode wiringPartnersDiv = new HTMLNode("div", "wiringPartners", "");
		HTMLNode expressesWORMBASEDiv = new HTMLNode("div", "expressesWORMBASE", "");
		HTMLNode homologuesDiv = new HTMLNode("div", "homologuesDiv", "");
		HTMLNode referencesTEXTPRESSODiv = new HTMLNode("div", "referencesTEXTPRESS", "");
		HTMLNode linksDiv = new HTMLNode("div", "links", "");
		
		//build data tags from terminal case
		String externalInfo = "External Information: " + terminalCase.getExternalInfo();
		HTMLNode externalInfoP = new HTMLNode("p", "", "", externalInfo);
		
		String partsListDescription = "Parts List Description: " + terminalCase.getPartsListDescription();
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		
		HTMLNode img = new HTMLNode("http://wormwiring.hpc.einstein.yu.edu/data/ccimages/RIBR.jpg", true);
		
		String functionWORMATLAS = "+Function (Wormatlas): " + terminalCase.getFunctionWORMATLAS();
		HTMLNode functionWORMATLASP = new HTMLNode("p", "", "", functionWORMATLAS);
		
		HTMLNode anatomyP = new HTMLNode("p", "", "", "- Anatomy: ");
		HTMLNode anatomyUL = new HTMLNode("ul");
		for (String anatomyEntry : terminalCase.getAnatomy()) {
			HTMLNode li = new HTMLNode("li", "", "", anatomyEntry);
			anatomyUL.addChild(li);
		}
		
		HTMLNode wiringPartnersP = new HTMLNode("p", "", "", "- Wiring: ");
		HTMLNode wiringPartnersUL = new HTMLNode("ul");
		ArrayList<String> presynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> postsynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> electricalPartners = terminalCase.getElectricalPartners();
		ArrayList<String> neuromuscularPartners = terminalCase.getNeuromuscularPartners();
		if (presynapticPartners.size() > 0) {
			Collections.sort(presynapticPartners);
			HTMLNode li = new HTMLNode("li", "", "", "Presynaptic to: " + presynapticPartners.toString());
			wiringPartnersUL.addChild(li);
		}
		if (postsynapticPartners.size() > 0) {
			Collections.sort(postsynapticPartners);
			HTMLNode li = new HTMLNode("li", "", "", "Postsynaptic to: " + postsynapticPartners.toString());
			wiringPartnersUL.addChild(li);
		}
		if (electricalPartners.size() > 0) {
			Collections.sort(electricalPartners);
			HTMLNode li = new HTMLNode("li", "", "", "Electrical to: " + electricalPartners.toString());
			wiringPartnersUL.addChild(li);
		}
		if (neuromuscularPartners.size() > 0) {
			Collections.sort(neuromuscularPartners);
			HTMLNode li = new HTMLNode("li", "", "", "Neuromusclar to: " + neuromuscularPartners.toString());
			wiringPartnersUL.addChild(li);
		}
		
		String expressesWORMBASE = "- Expresses (Wormbase): " + terminalCase.getExpressesWORMBASE().toString();
		HTMLNode expressesWORMBASEP = new HTMLNode("p", "", "", expressesWORMBASE);
		
		ArrayList<String> homologuesList = terminalCase.getHomologues();
		Collections.sort(homologuesList);
		String homologues = "- Homologues: " + homologuesList.toString();
		HTMLNode homologuesP = new HTMLNode("p", "", "", homologues);
		
		HTMLNode referencesP = new HTMLNode("p", "", "", "- References (Textpresso)");
		HTMLNode referencesUL = new HTMLNode("ul");
		for (String reference : terminalCase.getReferencesTEXTPRESSO()) {
			HTMLNode li = new HTMLNode("li", "", "", reference);
			referencesUL.addChild(li);
		}
		
		HTMLNode linksP = new HTMLNode("p", "", "", "- Links");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : terminalCase.getLinks()) {
			HTMLNode li = new HTMLNode("li", "", "", link);
			linksUL.addChild(li);
		}
		
		//add data tags to divs
		externalInfoDiv.addChild(externalInfoP);
		partsListDescrDiv.addChild(partsListDescrP);
		imgDiv.addChild(img);
		functionWORMATLASDiv.addChild(functionWORMATLASP);
		anatomyDiv.addChild(anatomyP);
		anatomyDiv.addChild(anatomyUL);
		wiringPartnersDiv.addChild(wiringPartnersP);
		wiringPartnersDiv.addChild(wiringPartnersUL);
		expressesWORMBASEDiv.addChild(expressesWORMBASEP);
		homologuesDiv.addChild(homologuesP);
		referencesTEXTPRESSODiv.addChild(referencesP);
		referencesTEXTPRESSODiv.addChild(referencesUL);
		linksDiv.addChild(linksP);
		linksDiv.addChild(linksUL);

		//add divs to body
		body.addChild(topContainerDiv);
		body.addChild(imgDiv);
		body.addChild(functionWORMATLASDiv);
		body.addChild(anatomyDiv);
		body.addChild(wiringPartnersDiv);
		body.addChild(expressesWORMBASEDiv);
		body.addChild(homologuesDiv);
		body.addChild(referencesTEXTPRESSODiv);
		body.addChild(linksDiv);
		
		//add head and body to html
		html.addChild(head);
		html.addChild(body);
		
		//add style node
		buildStyleNode();
	}
	
	/*
	 * NON TERMINAL CELL CASE
	 */
	public InfoWindowDOM(NonTerminalCellCase nonTerminalCase) {
		this.html = new HTMLNode("html");
		this.name = nonTerminalCase.getCellName();
	}
	
	public String DOMtoString() {
		String domAsString = doctypeTag;
		return domAsString += html.formatNode();
	}
	
	/*
	 * iterates through the DOM and builds the style tag add to the head node
	 */
	public void buildStyleNode() {
		if (html == null) return;
		
		String style = "";
		HTMLNode head = null; //saved to add style node as child of head
		//if (html.hasChildren()) {
		for (HTMLNode node : html.getChildren()) {
			if (node.getTag().equals("head")) { //save head
				head = node;
			} else if (node.getTag().equals("body")) { //get style
					style += findStyleInSubTree(node);
			}
				
		}

		addStyleNodeToHead(head, style);
	}
	
	/*
	 * called by buildStyleNode to scan the body node and extract style attributes from children
	 * - only called if node is body tag and if body has children
	 */
	private String findStyleInSubTree(HTMLNode node) {
		String style = "";
		for (HTMLNode n : node.getChildren()) {
				if (n.hasID()) {
				style += (newLine + "#" + n.getID() + " {"
						+ newLine + n.getStyle()
						+ newLine + "}");
			}
		}
		return style;
	}
	
	private void addStyleNodeToHead(HTMLNode head, String style) {
		if (head != null) {
			head.addChild(new HTMLNode(style, "text/css"));
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	private final static String doctypeTag = "<!DOCTYPE html>";
	private final static String newLine = "\n";
	private final static String meta_charset = "<meta charset=\"utf-8\">";
	private final static String meta_httpequiv_content = "<meta http-equiv=\"X-UA-Compatible\" content=\"WormGUIDES, MSKCC, Zhirong Bao\">";
}
