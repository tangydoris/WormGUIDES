package wormguides.view;

import java.util.ArrayList;
import java.util.Collections;

import wormguides.model.NonTerminalCellCase;
import wormguides.model.PartsList;
import wormguides.model.TerminalCellCase;
import wormguides.model.TerminalDescendant;

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
		this.name = terminalCase.getCellName();
	
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
		
		//function (wormatlas)
		HTMLNode functionWORMATLASTopContainerDiv = new HTMLNode("div", "functionTopContainer", "width: 100%;");
		HTMLNode collapseFunctionButton = new HTMLNode("button", "functionWORMATLASCollapse", "functionCollapseButton", "width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "-", true);
		HTMLNode functionWORMATLASTitle = new HTMLNode("p", "functionWORMATLASTitle", "width: 95%; float: left;",
				"<strong> Function (Wormatlas): </strong>");
		functionWORMATLASTopContainerDiv.addChild(collapseFunctionButton);
		functionWORMATLASTopContainerDiv.addChild(functionWORMATLASTitle);
		HTMLNode functionWORMATLASDiv = new HTMLNode("div", "functionWORMATLAS", "");
		
		//anatomy
		HTMLNode anatomyDiv = new HTMLNode("div", "anatomy", "");
		
		//wiring
		HTMLNode wiringPartnersDiv = new HTMLNode("div", "wiringPartners", "");
		
		//expresses
		HTMLNode expressesWORMBASEDiv = new HTMLNode("div", "expressesWORMBASE", "");
		HTMLNode homologuesDiv = new HTMLNode("div", "homologuesDiv", "");
		HTMLNode referencesTEXTPRESSODiv = new HTMLNode("div", "referencesTEXTPRESS", "");
		HTMLNode linksDiv = new HTMLNode("div", "links", "");
		
		//build data tags from terminal case
		String externalInfo = "<strong>External Information: </strong>" + terminalCase.getExternalInfo();
		HTMLNode externalInfoP = new HTMLNode("p", "", "", externalInfo);
		
		String partsListDescription = "<strong>Parts List Description: </strong>" + terminalCase.getPartsListDescription();
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		
		HTMLNode img = new HTMLNode(terminalCase.getImageURL(), true);
		
		HTMLNode functionWORMATLASP = new HTMLNode("p", "", "", terminalCase.getFunctionWORMATLAS());
		
		HTMLNode anatomyP = new HTMLNode("p", "", "", "<strong>- Anatomy: </strong>");
		HTMLNode anatomyUL = new HTMLNode("ul");
		for (String anatomyEntry : terminalCase.getAnatomy()) {
			HTMLNode li = new HTMLNode("li", "", "", anatomyEntry);
			anatomyUL.addChild(li);
		}
		
		HTMLNode wiringPartnersP = new HTMLNode("p", "", "", "<strong>- Wiring: </strong>");
		HTMLNode wiringPartnersUL = new HTMLNode("ul");
		ArrayList<String> presynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> postsynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> electricalPartners = terminalCase.getElectricalPartners();
		ArrayList<String> neuromuscularPartners = terminalCase.getNeuromuscularPartners();
		if (presynapticPartners.size() > 0) {
			Collections.sort(presynapticPartners);
			
			//remove brackets
			String prePartners = presynapticPartners.toString();
			prePartners = prePartners.substring(1, prePartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Presynaptic to: </em>" + prePartners);
			wiringPartnersUL.addChild(li);
		}
		if (postsynapticPartners.size() > 0) {
			Collections.sort(postsynapticPartners);
			String postPartners = postsynapticPartners.toString();
			
			postPartners = postPartners.substring(1, postPartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Postsynaptic to: </em>" + postPartners);
			wiringPartnersUL.addChild(li);
		}
		if (electricalPartners.size() > 0) {
			Collections.sort(electricalPartners);
			
			String electPartners = electricalPartners.toString();
			electPartners = electPartners.substring(1, electPartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Electrical to: </em>" + electPartners);
			wiringPartnersUL.addChild(li);
		}
		if (neuromuscularPartners.size() > 0) {
			Collections.sort(neuromuscularPartners);
			
			String neuroPartners = neuromuscularPartners.toString();
			neuroPartners = neuroPartners.substring(1, neuroPartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Neuromusclar to: </em>" + neuroPartners);
			wiringPartnersUL.addChild(li);
		}
		
		String expressesWORMBASE = "<strong>- Expresses (Wormbase): </strong>" + terminalCase.getExpressesWORMBASE().toString();
		HTMLNode expressesWORMBASEP = new HTMLNode("p", "", "", expressesWORMBASE);
		
		ArrayList<String> homologuesList = terminalCase.getHomologues();
		Collections.sort(homologuesList);
		String homologues = "<strong>- Homologues: </strong>" + homologuesList.toString();
		HTMLNode homologuesP = new HTMLNode("p", "", "", homologues);
		
		HTMLNode linksP = new HTMLNode("p", "", "", "<strong>- Links</strong>");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : terminalCase.getLinks()) {
			HTMLNode li = new HTMLNode("li", "", "", link);
			linksUL.addChild(li);
		}
		
		HTMLNode referencesP = new HTMLNode("p", "", "", "<strong>- References (Textpresso)</strong>");
		HTMLNode referencesUL = new HTMLNode("ul");
		for (String reference : terminalCase.getReferencesTEXTPRESSO()) {
			HTMLNode li = new HTMLNode("li", "", "", reference);
			referencesUL.addChild(li);
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
		linksDiv.addChild(linksP);
		linksDiv.addChild(linksUL);
		referencesTEXTPRESSODiv.addChild(referencesP);
		referencesTEXTPRESSODiv.addChild(referencesUL);

		//add divs to body
		body.addChild(topContainerDiv);
		body.addChild(imgDiv);
		body.addChild(functionWORMATLASTopContainerDiv);
		body.addChild(functionWORMATLASDiv);
		body.addChild(anatomyDiv);
		body.addChild(wiringPartnersDiv);
		body.addChild(expressesWORMBASEDiv);
		body.addChild(homologuesDiv);
		body.addChild(linksDiv);
		body.addChild(referencesTEXTPRESSODiv);
		
		//add collapse scripts to body
		body.addChild(collapseFunctionButton.makeCollapseButtonScript());
		
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
	
		HTMLNode head = new HTMLNode("head");
		
		/*
		 * TODO
		 * meta tags
		 * title
		 */
		
		HTMLNode body = new HTMLNode("body");
		
		//divs
		HTMLNode externalInfoDiv = new HTMLNode("div", "externalInfo", ""); 
		HTMLNode embryonicHomologyDiv = new HTMLNode("div", "partsListDescr", "");
		HTMLNode terminalDescendantsDiv = new HTMLNode("div", "terminalDescendants", "");
		
		//build data tags from terminal case
		String externalInfo = "<strong>External Information: </strong>" + nonTerminalCase.getCellName();
		HTMLNode externalInfoP = new HTMLNode("p", "", "", externalInfo);
		
		String embryonicHomology = "<strong>Embryonic Homology to: " + nonTerminalCase.getEmbryonicHomology();
		HTMLNode embryonicHomologyP = new HTMLNode("p", "", "", embryonicHomology);
		
		HTMLNode terminalDescendantsP = new HTMLNode("p", "", "", "<strong>- Terminal Descendants: </strong>");
		HTMLNode terminalDescendantsUL = new HTMLNode("ul");
		for (TerminalDescendant terminalDescendant : nonTerminalCase.getTerminalDescendants()) {
			String descendant = "";
			String functionalName = PartsList.getFunctionalNameByLineageName(terminalDescendant.getCellName());
			
			if (functionalName != null) {
				descendant += "<strong>" + functionalName.toUpperCase() + " (" + terminalDescendant.getCellName() + ")</strong>";
			} else {
				descendant = "<strong>" + terminalDescendant.getCellName() + "</strong>";
			}
			
			String partsListEntry = terminalDescendant.getPartsListEntry();
			if (!partsListEntry.equals("N/A")) {
				descendant += (", " + partsListEntry);
			}
			
			HTMLNode li = new HTMLNode("li", "", "", descendant);
			terminalDescendantsUL.addChild(li);
		}
		
		//add data tags to div
		externalInfoDiv.addChild(externalInfoP);
		embryonicHomologyDiv.addChild(embryonicHomologyP);
		terminalDescendantsDiv.addChild(terminalDescendantsP);
		terminalDescendantsDiv.addChild(terminalDescendantsUL);
		
		//add divs to body
		body.addChild(externalInfoDiv);
		body.addChild(embryonicHomologyDiv);
		body.addChild(terminalDescendantsDiv);
		
		//add head and body to html
		html.addChild(head);
		html.addChild(body);
				
		//add style node
		buildStyleNode();
	}
	
	public String DOMtoString() {
		String domAsString = doctypeTag;
		//System.out.println(domAsString += html.formatNode());
		return domAsString += html.formatNode();
		
	}
	
	/*
	 * iterates through the DOM and builds the style tag add to the head node
	 */
	public void buildStyleNode() {
		if (html == null) return;
		
		String style = "";
		HTMLNode head = null; //saved to add style node as child of head
		if (html.hasChildren()) {
			for (HTMLNode node : html.getChildren()) {
				if (node.getTag().equals("head")) { //save head
					head = node;
				} else if (node.getTag().equals("body")) { //get style
						style += findStyleInSubTree(node);
				}
					
			}
		}
		addStyleNodeToHead(head, style);
	}
	
	private void addStyleNodeToHead(HTMLNode head, String style) {
		if (head != null) {
			head.addChild(new HTMLNode(style, "text/css"));
		}
	}
	
	/*
	 * called by buildStyleNode to scan the body node and extract style attributes from children
	 * - only called if node is body tag and if body has children
	 */
	private String findStyleInSubTree(HTMLNode node) {
		String style = "";
		if (node.hasChildren()) {
			for (HTMLNode n : node.getChildren()) {
				if (n.hasID() && !n.getStyle().equals("")) {
					style += styleAsStr(n);
				}
					
				if (n.hasChildren()) {
					for (HTMLNode n1 : n.getChildren()) {
						style += findStyleInSubTree(n1);
					}
				}
			}
		} else {
			if (node.hasID() && !node.getStyle().equals("")) {
				style += styleAsStr(node);
			}
		}
		return style;
	}
	
	private String styleAsStr(HTMLNode node) {
	 return (newLine + "#" + node.getID() + " {"
				+ newLine + node.getStyle()
				+ newLine + "}");
	}
	
	public String getName() {
		return this.name;
	}
	
	private final static String doctypeTag = "<!DOCTYPE html>";
	private final static String newLine = "\n";
	private final static String meta_charset = "<meta charset=\"utf-8\">";
	private final static String meta_httpequiv_content = "<meta http-equiv=\"X-UA-Compatible\" content=\"WormGUIDES, MSKCC, Zhirong Bao\">";
}
