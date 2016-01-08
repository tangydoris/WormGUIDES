package wormguides.view;

import javafx.scene.web.WebView;

public class InfoWindowDOM {
	private HTMLNode html;
	private WebView webView;
	private String cellName;
	
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
		
//		WebView cellShapesIndexWebView = new WebView();
//		cellShapesIndexWebView.getEngine().loadContent(cellShapesToHTML.buildCellShapesIndexAsHTML());
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
	}
	

	
	/*
	 * TODO
	 */
	@Override
	public String toString() {
		//add doctype tag to top -- <!DOCTYPE html> tag before loadContent on the webview
		return "the first tab";
	}
	
	public void loadContent(String domAsString) {
		webView.getEngine().loadContent(domAsString);
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
