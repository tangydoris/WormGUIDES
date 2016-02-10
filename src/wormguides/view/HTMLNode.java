package wormguides.view;

import java.util.ArrayList;

public class HTMLNode {
	private String tag;
	private String ID;
	private String style;
	private String innerHTML;

	//for type checking
	private boolean isContainer;
	private boolean hasID;
	private boolean isImage;
	private boolean isStyle;
	private boolean isButton;
	private boolean isScript;

	//button vars
	private String onclick;

	//script vars
	private String script;

	//image vars
	private String imgSrc;

	private ArrayList<HTMLNode> children;

	//container node with no ID - e.g. head, body, ul
	public HTMLNode(String tag) {
		this.tag = tag;

		this.isContainer = true;
		this.hasID = false;
		this.isImage = false;
		this.isStyle = false;
		this.isButton = false;
		this.isScript = false;

		this.ID = null;
		this.style = null;
		this.innerHTML = null;
		this.imgSrc = null;
		this.onclick = null;
		this.script = null;

		this.children = new ArrayList<HTMLNode>();
	}

	//container node with ID - e.g. <div>
	public HTMLNode(String tag, String ID, String style) {
		this.tag = tag;
		this.ID = ID;
		this.style = style;

		this.isContainer = true;
		this.hasID = true;
		this.isImage = false;
		this.isStyle = false;
		this.isButton = false;
		this.isScript = false;

		this.innerHTML = null;
		this.imgSrc = null;
		this.onclick = null;
		this.script = null;

		this.children = new ArrayList<HTMLNode>();
	}

	//inner node - e.g. <p>
	public HTMLNode(String tag, String ID, String style, String innerHTML) {
		this.tag = tag;
		this.ID = ID;
		this.style = style;
		this.innerHTML = innerHTML;

		this.hasID = true;
		this.isContainer = false;
		this.isImage = false;
		this.isStyle = false;
		this.isButton = false;
		this.isScript = false;

		this.imgSrc = null;
		this.onclick = null;
		this.script = null;
		children = null;
	}

	//img node
	public HTMLNode(String imgSrc, boolean isImage) {
		this.tag = "img";
		this.imgSrc = imgSrc;

		this.isImage = isImage;
		this.isContainer = false;
		this.hasID = false;
		this.isStyle = false;
		this.isButton = false;
		this.isScript = false;

		this.onclick = null;
		this.script = null;
		this.innerHTML = null;
		this.children = null;
	}

	//style node
	public HTMLNode(String style, String type) {
		this.tag = "style";
		this.innerHTML = style;

		if (type.equals("text/css")) {
			this.ID = type; //we'll use the ID var for type="text/css"
		}

		this.isStyle = true;
		this.isContainer = false;
		this.hasID = false;
		this.isImage = false;
		this.isButton = false;
		this.isScript = false;

		this.imgSrc = null;
		this.style = null;
		this.onclick = null;
		this.script = null;
		this.children = null;
	}

	//button node
	public HTMLNode(String tag, String onclick, String ID, String style, String buttonText, boolean button) {
		this.tag = tag;
		this.onclick = onclick;
		this.ID = ID;  
		this.style = style;
		this.innerHTML = buttonText;

		this.isButton = button;
		this.isContainer = false;
		this.hasID = true;
		this.isImage = false;
		this.isStyle = false;
		this.isScript = false;

		this.imgSrc = null;
		this.script = null;
		this.children = null;
	}

	//script
	public HTMLNode(String tag, String script, boolean isScript) {
		this.tag = tag;
		this.script = script;

		this.isScript = isScript;
		this.isContainer = false;
		this.hasID = false;
		this.isImage = false;
		this.isStyle = false;

		this.imgSrc = null;
		this.innerHTML = null;
		this.children = null;

	}

	public void addChild(HTMLNode child) {
		if (children == null) return;

		if (child != null) {
			this.children.add(child);
		}
	}

	public String formatNode() {
		return formatNode(this);
	}

	/*
	 * TODO
	 * - add remove child method
	 */

	private String formatNode(HTMLNode node) {
		/*
		 * TODO
		 * - container without ID --> <head>
		 * - container with ID --> div
		 * - format with children
		 */

		if (node == null) return null;

		String nodeStr = "";
		if (node.isContainer()) { //e.g. <head>, <div>

			if (!node.hasID()) { //e.g. <head>
				nodeStr = newLine + "<" + node.getTag() + ">";
			} else { //e.g. <div>
				nodeStr = newLine + "<" + node.getTag() + " id=\"" + node.getID() + "\">";
			}

			//add children to node
			if (node.hasChildren()) {
				for (HTMLNode n : node.getChildren()) {
					nodeStr += formatNode(n);
				}
			}
			
			if (!node.getTag().equals("br")) {
				nodeStr += (newLine + "</" + node.getTag() + ">");
			}
			
		} else if (!node.isContainer() && !node.isImage() && !node.isButton() && !node.isScript()) { //e.g. <p id...
			if (!node.getID().equals("")) {
				nodeStr = newLine + "<" + node.getTag() + " id=\"" + node.getID() + "\">" 
						+ newLine + node.getInnerHTML() 
						+ newLine +  "</" + node.getTag() + ">"; 
			} else {
				nodeStr = newLine + "<" + node.getTag() + ">"
				+ newLine + node.getInnerHTML() 
				+ newLine +  "</" + node.getTag() + ">";
			}
					 
		} else if (node.isImage()) { //e.g. <img id...
			nodeStr = newLine + "<" + node.getTag() + " src=\"" + node.getImgSrc() + "\" alt=\"" + node.getImgSrc() + "\">"; 
		} else if (node.isStyle()) {
			nodeStr = newLine + "<" + node.getTag() + " type=\"" + node.getID() + "\">"
					+ newLine + newLine + node.getStyle() + newLine + "</" + node.getTag() + ">";
		} else if (node.isButton()) { //using imgSrc for onlick and innerHTML for button text
			nodeStr = newLine + "<" + node.getTag() + " onclick=\"" + node.getOnclick() + "()\"" + 
					" id=\"" + node.getID() + "\">" +
					node.innerHTML + "</" + node.getTag() + ">";
		} else if (node.isScript()) {
			nodeStr = newLine + "<" + node.getTag() + ">"
					+ newLine + node.getScript()
					+ newLine + "</" + node.getTag() + ">";
		}

		return nodeStr;
	}

	public HTMLNode makeCollapseButtonScript() {
		if (!this.isButton()) return null;

		String functionName = "function " + this.getOnclick() + "() {";

		String divToCollapseID = this.getOnclick().substring(0, this.getOnclick().indexOf("Collapse"));
		String script = functionName 
				+ newLine + "    if (document.getElementById('" + this.getID() + "').innerHTML == \"+\") {"
				+ newLine + "        document.getElementById('" + this.getID() + "').innerHTML = \"-\";"
				+ newLine + "        document.getElementById('" + divToCollapseID + "').style.height = '20%'; "
				+ newLine + "        document.getElementById('" + divToCollapseID + "').style.visibility = 'visible'; "
				+ newLine + "    } else {"
				+ newLine + "        document.getElementById('" + this.getID() + "').innerHTML = \"+\";"
				+ newLine + "        document.getElementById(\"" + divToCollapseID + "\").style.height = '0px'; "
				+ newLine + "        document.getElementById('" + divToCollapseID + "').style.visibility = 'hidden'; "
				+ newLine + "    }"
				+ newLine + "}";

		/*
		 * 
		 * function function________Collapse() { 
    		 if (document.getElementById('functionCollapseButton').innerHTML == "+") {
        	 	document.getElementById('functionCollapseButton').innerHTML = "-";
        	 	document.getElementById("functionWORMATLAS").style.height = '20%'; 
    		    document.getElementById('functionWORMATLAS').style.visibility = 'visible';
    		 } else {
        	 	document.getElementById('functionCollapseButton').innerHTML = "+";
        	 	document.getElementById("functionWORMATLAS").style.height = '0px'; 
    		    document.getElementById('functionWORMATLAS').style.visibility = 'hidden';
    		 }
		   }
		 */
		return new HTMLNode("script", script, true);
	}
	
	public HTMLNode makeHomologuesCollapseButtonScript() {
		if (!this.isButton()) return null;
		
		String script = "function homologuesCollapse() {"
				+ newLine + "if (document.getElementById('homologuesCollapseButton').innerHTML == \"+\") {"
				+ newLine + "document.getElementById('homologuesCollapseButton').innerHTML = \"-\";"
				+ newLine + "document.getElementById('homologues').style.height = '20%';"
				+ newLine + "document.getElementById('homologues').style.visibility = 'visible'; "
				+ newLine + "document.getElementById('homologuesLR').style.height = '20%';"
				+ newLine + "document.getElementById('homologuesLR').style.visibility = 'visible';"
				+ newLine + "document.getElementById('homologuesOther').style.height = '20%';"
				+ newLine + "document.getElementById('homologuesOther').style.visibility = 'visible';"
				+ newLine + "} else {"
				+ newLine + "document.getElementById('homologuesCollapseButton').innerHTML = \"+\";"
				+ newLine + "document.getElementById('homologues').style.height = '0px'; "
				+ newLine + "document.getElementById('homologues').style.visibility = 'hidden';"
				+ newLine + "document.getElementById('homologuesLR').style.height = '0px';"
				+ newLine + "document.getElementById('homologuesLR').style.visibility = 'hidden';"
				+ newLine + "document.getElementById('homologuesOther').style.height = '0px';"
				+ newLine + "document.getElementById('homologuesOther').style.visibility = 'hidden';"
				+ newLine + "}"
				+ newLine + "}";

		return new HTMLNode("script", script, true);
	}
	

	
	public HTMLNode addLinkHandlerScript() {
		String script = "function handleLink(element) {"
				+ newLine + "app.handleLink(element.name);"
				+ newLine + "}";	
		return new HTMLNode("script", script, true);
		
	}
	
	public HTMLNode handleWiringPartnerClickScript() {
		String script = "function handleWiringPartnerClick(element) {"
				+ newLine + "app.handleWiringPartnerClick(element.innerHTML);"
				+ newLine + "}";
		
		
		return new HTMLNode("script", script, true);
	}
	
	public HTMLNode viewInCellTheaterScript() {
		String script = "function viewInCellTheater(element) {"
				+ newLine + "app.viewInCellTheater(element.name);"
				+ newLine + "}";
		
		return new HTMLNode("script", script, true);
	}

	public String getTag() {
		if (this.tag != null) {
			return this.tag;
		}
		return "";
	}

	public String getID() {
		if (this.ID != null) {
			return this.ID;
		}

		return "";
	}

	public String getStyle() {
		if (this.style != null) {
			return this.style;
		}

		if (this.isButton()) {
			System.out.println("BUTTON STYLE RETURNED NOTHING");
		}
		return "";
	}

	public String getInnerHTML() {
		if (this.innerHTML != null) {
			return this.innerHTML;
		}
		return "";
	}

	public String getOnclick() {
		if (this.onclick != null) {
			return this.onclick;
		}

		return "";
	}

	public String getImgSrc() {
		if (this.imgSrc != null) {
			return this.imgSrc;
		}

		return "";
	}

	public String getScript() {
		if (this.script != null) {
			return this.script;
		}

		return "";
	}

	public ArrayList<HTMLNode> getChildren() {
		if (this.hasChildren()) {
			return this.children;
		}
		return null;
	}

	public boolean hasChildren() {
		if (children != null) {
			return this.children.size() > 0;
		}
		return false;
	}

	public boolean isContainer() {
		return this.isContainer;
	}

	public boolean hasID() {
		return this.hasID;
	}

	public boolean isImage() {
		return this.isImage;
	}

	public boolean isStyle() {
		return this.isStyle;
	}

	public boolean isButton() {
		return this.isButton;
	}

	public boolean isScript() {
		return this.isScript;
	}

	private final static String newLine = "\n";
}
