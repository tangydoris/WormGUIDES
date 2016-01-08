package wormguides.view;

import javafx.scene.web.WebView;
import wormguides.model.NonTerminalCellCase;
import wormguides.model.TerminalCellCase;

public class InfoWindowDOM {
	private HTMLNode html;
	private WebView webView;
	private String cellName;
	
	//terminal or non terminal case
	private boolean isCellCase;
	private TerminalCellCase terminalCase;
	private NonTerminalCellCase nonTerminalCase;

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
		webView = new WebView();
		cellName = "CELL TITLE";
		
		this.isCellCase = false;
		this.terminalCase = null;
		this.nonTerminalCase = null;
	}
	
	//pass the cell name as a string which will be the name at the top of the tab
	public InfoWindowDOM(HTMLNode html) {
		if (!html.getTag().equals("html")) {
			this.html = new HTMLNode("html");
		} else {
			this.html = html;
		}
		
		webView = new WebView();
		cellName = "CELL TITLE";
		
		this.isCellCase = false;
		this.terminalCase = null;
		this.nonTerminalCase = null;
	}
	
	/*
	 * TERMINAL CELL CASE
	 */
	public InfoWindowDOM(TerminalCellCase terminalCase) {
		this.html = new HTMLNode("html");
		this.webView = new WebView();
		this.cellName = terminalCase.getCellName();
		
		this.isCellCase = true;
		this.terminalCase = terminalCase;
		this.nonTerminalCase = null;
		
		/*
		 * TODO
		 * construct the dom from terminal cell case accessor methods
		 */
	}
	
	/*
	 * NON TERMINAL CELL CASE
	 */
	public InfoWindowDOM(NonTerminalCellCase nonTerminalCase) {
		this.html = new HTMLNode("html");
		this.webView = new WebView();
		this.cellName = nonTerminalCase.getCellName();
		
		this.isCellCase = true;
		this.terminalCase = null;
		this.nonTerminalCase = nonTerminalCase;
	}
	

	
	/*
	 * TODO
	 */
	public String DOMtoString() {
		//add doctype tag to top -- <!DOCTYPE html> tag before loadContent on the webview
		
		
		if (isCellCase) {
			//check if terminal or non terminal
			if (terminalCase != null) {
				return this.cellName;
			} else {
				return this.cellName;
			}
		}
		
		return "not a cell case tab";
	}
	
	public void loadContent() {
		webView.getEngine().loadContent(this.DOMtoString());
	}
	
//	public void loadContent(String domAsString) {
//		webView.getEngine().loadContent(domAsString);
//	}
	
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
		//if ()
		String style = null;
		for (HTMLNode n : node.getChildren()) {
				if (n.hasID()) {
				style += (newLine + n.getID() + " {"
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
	
	public String getCellName() {
		return this.cellName;
	}
	
	private final static String doctypeTag = "<!DOCTYPE html>";
	private final static String newLine = "\n";
	private final static String meta_charset = "<meta charset=\"utf-8\">";
	private final static String meta_httpequiv_content = "<meta http-equiv=\"X-UA-Compatible\" content=\"WormGUIDES, MSKCC, Zhirong Bao\">";
}
