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
		
		//external info
		HTMLNode cellNameDiv = new HTMLNode("div", "cellName", "");
		String cellName = "<strong>" + terminalCase.getExternalInfo() + "</strong>";
		HTMLNode cellNameP = new HTMLNode("p", "", "", cellName);
		cellNameDiv.addChild(cellNameP);
		
		//parts list descriptions
		HTMLNode partsListDescrDiv = new HTMLNode("div", "partsListDescr", "");
		String partsListDescription = terminalCase.getPartsListDescription();
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		partsListDescrDiv.addChild(partsListDescrP);
		
		//container for external info & parts list description
		HTMLNode topContainerDiv = new HTMLNode("div", "topContainer", "width: 50%; height: 10%; float: left;"); //will contain external info and parts list description. float left for img on right
		topContainerDiv.addChild(cellNameDiv);
		topContainerDiv.addChild(partsListDescrDiv);
		
		//image
		HTMLNode imgDiv = new HTMLNode("div", "imgDiv", "width: 50%; height: 10%; float: left;");
		HTMLNode img = new HTMLNode(terminalCase.getImageURL(), true);
		imgDiv.addChild(img);
			
		//function (wormatlas)
		HTMLNode functionWORMATLASTopContainerDiv = new HTMLNode("div", "functionTopContainer", "");
		HTMLNode collapseFunctionButton = new HTMLNode("button", "functionWORMATLASCollapse", "functionCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
		HTMLNode functionWORMATLASTitle = new HTMLNode("p", "functionWORMATLASTitle", "width: 95%; float: left;",
				"<strong> Function (Wormatlas): </strong>");
		functionWORMATLASTopContainerDiv.addChild(collapseFunctionButton);
		functionWORMATLASTopContainerDiv.addChild(functionWORMATLASTitle);
		HTMLNode functionWORMATLASDiv = new HTMLNode("div", "functionWORMATLAS", "");
		HTMLNode functionWORMATLASP = new HTMLNode("p", "", "", terminalCase.getFunctionWORMATLAS());
		functionWORMATLASDiv.addChild(functionWORMATLASP);
		
		//anatomy
		HTMLNode anatomyTopContainerDiv = new HTMLNode("div", "anatomyTopContainer", "");
		HTMLNode collapseAnatomyButton = new HTMLNode("button", "anatomyCollapse", "anatomyCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
		HTMLNode anatomyTitle = new HTMLNode("p", "anatomyTitle", "width: 95%; float: left;",
				"<strong> Anatomy: </strong>");
		anatomyTopContainerDiv.addChild(collapseAnatomyButton);
		anatomyTopContainerDiv.addChild(anatomyTitle);
		HTMLNode anatomyDiv = new HTMLNode("div", "anatomy", "");
		HTMLNode anatomyUL = new HTMLNode("ul");
		for (String anatomyEntry : terminalCase.getAnatomy()) {
			HTMLNode li = new HTMLNode("li", "", "", anatomyEntry);
			anatomyUL.addChild(li);
		}
		anatomyDiv.addChild(anatomyUL);
		
		//wiring
		HTMLNode wiringPartnersTopContainerDiv = new HTMLNode("div", "wiringPartnersTopContainer", "");
		HTMLNode collapseWiringPartnersButton = new HTMLNode("button", "wiringPartnersCollapse", "wiringPartnersCollapseButton","width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
		HTMLNode wiringPartnersTitle = new HTMLNode("p", "anatomyTitle", "width: 95%; float: left;",
				"<strong> Wiring Partners: </strong>");
		wiringPartnersTopContainerDiv.addChild(collapseWiringPartnersButton);
		wiringPartnersTopContainerDiv.addChild(wiringPartnersTitle);
		HTMLNode wiringPartnersDiv = new HTMLNode("div", "wiringPartners", "");
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
		wiringPartnersDiv.addChild(wiringPartnersUL);
		
		//expresses
		HTMLNode geneExpressionTopContainerDiv = new HTMLNode("div", "expressesTopContainer", "");
		HTMLNode collapseGeneExpressionButton = new HTMLNode("button", "geneExpressionCollapse", "geneExpressionCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
		HTMLNode geneExpressionTitle = new HTMLNode("p", "geneExpressionTitle", "width: 95%; float: left;",
				"<strong> Gene Expression: </strong>");
		geneExpressionTopContainerDiv.addChild(collapseGeneExpressionButton);
		geneExpressionTopContainerDiv.addChild(geneExpressionTitle);
		HTMLNode geneExpressionDiv = new HTMLNode("div", "geneExpression", "");
		ArrayList<String> expresses = terminalCase.getExpressesWORMBASE();
		Collections.sort(expresses);
		String geneExpressionStr = expresses.toString();
		HTMLNode geneExpression = new HTMLNode("p", "", "", geneExpressionStr);
		geneExpressionDiv.addChild(geneExpression);
		
		//terminal homologues
		HTMLNode homologuesTopContainerDiv = new HTMLNode("div", "homologuesTopContainer", "");
		HTMLNode collapseHomologuesButton = new HTMLNode("button", "homologuesCollapse", "homologuesCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
		HTMLNode homologuesTitle = new HTMLNode("p", "homologuesTitle",  "width: 95%; float: left;",
				"<strong> Homologues: </strong>");
		homologuesTopContainerDiv.addChild(collapseHomologuesButton);
		homologuesTopContainerDiv.addChild(homologuesTitle);
		HTMLNode homologuesDiv = new HTMLNode("div", "homologues", "");
		ArrayList<String> homologuesList = terminalCase.getHomologues();
		Collections.sort(homologuesList);
		String homologuesStr = homologuesList.toString();
		HTMLNode homologues = new HTMLNode("p", "", "", homologuesStr);
		homologuesDiv.addChild(homologues);
		
		//links
		HTMLNode linksTopContainerDiv = new HTMLNode("div", "linksTopContainer", "");
		HTMLNode collapseLinksButton = new HTMLNode("button", "linksCollapse", "linksCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
		HTMLNode linksTitle = new HTMLNode("p", "linksTitle", "width: 95%; float: left;",
				"<strong> External Links: </strong>");
		linksTopContainerDiv.addChild(collapseLinksButton);
		linksTopContainerDiv.addChild(linksTitle);
		HTMLNode linksDiv = new HTMLNode("div", "links", "");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : terminalCase.getLinks()) {
			HTMLNode li = new HTMLNode("li", "", "", link);
			linksUL.addChild(li);
		}
		linksDiv.addChild(linksUL);
		
		//references
		HTMLNode referencesTopContainerDiv = new HTMLNode("div", "referencesTopContainer", "");
		HTMLNode collapseReferencesButton = new HTMLNode("button", "referencesCollapse", "referencesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "-", true);
		HTMLNode referencesTitle = new HTMLNode("p", "referencesTitle", "width: 95%; float: left;",
				"<strong> References: </strong>");
		referencesTopContainerDiv.addChild(collapseReferencesButton);
		referencesTopContainerDiv.addChild(referencesTitle);
		HTMLNode referencesTEXTPRESSODiv = new HTMLNode("div", "references", "");
		HTMLNode referencesUL = new HTMLNode("ul");
		for (String reference : terminalCase.getReferences()) {
			HTMLNode li = new HTMLNode("li", "", "", reference);
			referencesUL.addChild(li);
		}
		referencesTEXTPRESSODiv.addChild(referencesUL);
		

		//add divs to body
		body.addChild(topContainerDiv);
		body.addChild(imgDiv);
		body.addChild(functionWORMATLASTopContainerDiv);
		body.addChild(functionWORMATLASDiv);
		body.addChild(anatomyTopContainerDiv);
		body.addChild(anatomyDiv);
		body.addChild(wiringPartnersTopContainerDiv);
		body.addChild(wiringPartnersDiv);
		body.addChild(geneExpressionTopContainerDiv);
		body.addChild(geneExpressionDiv);
		body.addChild(homologuesTopContainerDiv);
		body.addChild(homologuesDiv);
		body.addChild(linksTopContainerDiv);
		body.addChild(linksDiv);
		body.addChild(referencesTopContainerDiv);
		body.addChild(referencesTEXTPRESSODiv);
		
		//add collapse scripts to body
		body.addChild(collapseFunctionButton.makeCollapseButtonScript());
		body.addChild(collapseAnatomyButton.makeCollapseButtonScript());
		body.addChild(collapseWiringPartnersButton.makeCollapseButtonScript());
		body.addChild(collapseGeneExpressionButton.makeCollapseButtonScript());
		body.addChild(collapseHomologuesButton.makeCollapseButtonScript());
		body.addChild(collapseLinksButton.makeCollapseButtonScript());
		body.addChild(collapseReferencesButton.makeCollapseButtonScript());
		
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
		
		String embryonicHomology = "<strong>Embryonic Homology to: </strong>" + nonTerminalCase.getEmbryonicHomology();
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
		
//		String str = domAsString += html.formatNode();
//		System.out.println(str);
//		return str;
		return domAsString += html.formatNode();
		
	}
	
	/*
	 * iterates through the DOM and builds the style tag add to the head node
	 */
	public void buildStyleNode() {
		if (html == null) return;
		
		//start with rule for unorder list --> no bullets
		String style = newLine + "ul li {"
				+ newLine + "list-style-type: none;"
				+ newLine + "margin-bottom: 2%;"
				+ newLine + "}";
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
