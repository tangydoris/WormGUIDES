package wormguides.view;

public class InfoWindowDOM {
	private HTMLNode html;
	
	/*
	 * TODO
	 * FINISH STYLE SCAN THROUGH BODY
	 * getNode(String ID)
	 * removeNode(String ID)
	 * addChildToNode(String parentID, HTMLNode child) -- need this?
	 * toString() @override --> formatted for webview.loadContent()...
	 * add title tag to head
	 * make final static vars for meta tags
	 * 
	 * method that includes the <!DOCTYPE html> tag before loadContent on the webview
	 * build style tag for inside head tag before loadContent
	 */
	public InfoWindowDOM() {
		this.html = new HTMLNode("html");
	}
	
	public InfoWindowDOM(HTMLNode html) {
		if (!html.getTag().equals("html")) {
			this.html = new HTMLNode("html");
		} else {
			this.html = html;
		}
	}
	
	/*
	 * iterates through the DOM and builds the style tag add to the head node
	 */
	public void buildStyleNode() {
		if (html == null) return;
		
		String style = "";
		HTMLNode head = null;
		if (html.hasChildren()) {
			for (HTMLNode node : html.getChildren()) {
				if (node.getTag().equals("head")) { //save head
					head = node;
				} else if (node.getTag().equals("body")) { //get style
					if (node.hasChildren()) {
						style = findStyleInBody(node);
					}
				}
				
				if (node.hasID()) {
					style += (newLine + node.getID() + " {"
							+ newLine + node.getStyle()
							+ newLine + "}");
				}
			}
		}
		
		addStyleNodeToHead(head, style);
	}
	
	/*
	 * called by buildStyleNode to scan the body node and extract style attributes from children
	 * - only called if node is body tag and if body has children
	 */
	private String findStyleInBody(HTMLNode body) {
		for (HTMLNode node : body.getChildren()) {
			
		}
		
		return "";
	}
	
	private void addStyleNodeToHead(HTMLNode head, String style) {
		head.addChild(new HTMLNode(style, "text/css"));
	}
	
	private final static String doctypeTag = "<!DOCTYPE html>";
	private final static String newLine = "\n";
}
