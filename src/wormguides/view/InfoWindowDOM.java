package wormguides.view;

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
		HTMLNode topContainerDiv = new HTMLNode("div", "topContainer", "width: 50%; float: left; background-color: green;"); //will contain external info and parts list description. float left for img on right
		HTMLNode externalInfoDiv = new HTMLNode("div", "externalInfo", "background-color: red;"); 
		HTMLNode partsListDescrDiv = new HTMLNode("div", "partsListDescr", "background-color: green;");
		topContainerDiv.addChild(externalInfoDiv);
		topContainerDiv.addChild(partsListDescrDiv);
		
		HTMLNode imgDiv = new HTMLNode("div", "imgDiv", "width: 50%; float: left; background-color: red;");
		
		HTMLNode functionWORMATLASDiv = new HTMLNode("div", "functionWORMATLAS", "background-color: green;");
		HTMLNode anatomyDiv = new HTMLNode("div", "anatomy", "background-color: red;");
		HTMLNode wiringPartnersDiv = new HTMLNode("div", "wiringPartners", "background-color: green;");
		HTMLNode expressesWORMBASEDiv = new HTMLNode("div", "expressesWORMBASE", "background-color: red;");
		HTMLNode referencesTEXTPRESSODiv = new HTMLNode("div", "referencesTEXTPRESS", "background-color: green;");
		HTMLNode linksDiv = new HTMLNode("div", "links", "background-color: red;");
		
		//build data tags from terminal case
		HTMLNode externalInfoP = new HTMLNode("p", "", "", terminalCase.getExternalInfo());
		
		//add data tags to divs
		externalInfoDiv.addChild(externalInfoP);
		
		//add divs to body
		body.addChild(topContainerDiv);
		body.addChild(imgDiv);
		body.addChild(functionWORMATLASDiv);
		body.addChild(anatomyDiv);
		body.addChild(wiringPartnersDiv);
		body.addChild(expressesWORMBASEDiv);
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
