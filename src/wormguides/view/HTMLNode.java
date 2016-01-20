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
		
		this.ID = null;
		this.style = null;
		this.innerHTML = null;
		this.imgSrc = null;
		
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
		
		this.innerHTML = null;
		this.imgSrc = null;
		
		this.children = new ArrayList<HTMLNode>();
	}
	
	//inner node - e.g. <p>
	public HTMLNode(String tag, String ID, String style, String innerHTML) {
		this.tag = tag;
		this.ID = ID;
		this.style = style;
		this.innerHTML = innerHTML;
		
		this.isContainer = false;
		this.hasID = true;
		this.isImage = false;
		this.isStyle = false;
		this.isButton = false;
		
		this.imgSrc = null;
		children = null;
	}
	
	//img node
	public HTMLNode(String imgSrc, boolean isImage) {
		this.tag = "img";
		this.imgSrc = imgSrc;
		
		this.isContainer = false;
		this.hasID = false;
		this.isImage = true;
		this.isStyle = false;
		this.isButton = false;
		
		this.children = null;
		this.innerHTML = null;
	}
	
	//style node
	public HTMLNode(String style, String type) {
		this.tag = "style";
		this.innerHTML = style;
		
		if (type.equals("text/css")) {
			this.ID = type; //we'll use the ID var for type="text/css"
		}
		
		this.isContainer = false;
		this.hasID = false;
		this.isImage = false;
		this.isStyle = true;
		this.isButton = false;
		
		this.imgSrc = null;
		this.style = null;
		this.children = null;
	}
	
	//button node
	public HTMLNode(String tag, String onclick, String ID, String style, String buttonText, boolean button) {
		this.tag = tag;
		this.imgSrc = onclick; //will use this for the onlick="function()"
		this.ID = ID;  
		this.style = style;
		this.innerHTML = buttonText;
		
		this.isContainer = false;
		this.hasID = true;
		this.isImage = false;
		this.isStyle = false;
		this.isButton = true;
		
		this.imgSrc = null;
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
			
			nodeStr += (newLine + "</" + node.tag + ">");
		} else if (!node.isContainer() && !node.isImage() && !node.isButton()) { //e.g. <p id...
			nodeStr = newLine + "<" + node.getTag() + " id=\"" + node.getID() + "\">" 
						+ newLine + node.getInnerHTML() 
						+ newLine +  "</" + node.getTag() + ">"; 
		} else if (node.isImage()) { //e.g. <img id...
			nodeStr = newLine + "<" + node.getTag() + " src=\"" + node.getImgSrc() + "\" alt=\"" + node.getImgSrc() + "\">"; 
		} else if (node.isStyle()) {
			nodeStr = newLine + "<" + node.getTag() + " type=\"" + node.getID() + "\">"
					+ newLine + newLine + node.getStyle() + newLine + "</" + node.getTag() + ">";
		} else if (node.isButton()) { //using imgSrc for onlick and innerHTML for button text
			nodeStr = newLine + "<" + node.getTag() + " onlick=\"" + node.getImgSrc() + "\"" + 
						" id=\"" + node.getID() + "\">" +
					node.innerHTML + "</" + node.getTag() + ">";
		}
		
		return nodeStr;
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
		return "";
	}
	
	public String getInnerHTML() {
		if (this.innerHTML != null) {
			return this.innerHTML;
		}
		return "";
	}
	
	public String getImgSrc() {
		if (this.imgSrc != null) {
			return this.imgSrc;
		}
		return "";
	}
	
	public boolean hasChildren() {
		if (children != null) {
			return this.children.size() > 0;
		}
		return false;
	}
	
	public ArrayList<HTMLNode> getChildren() {
		if (this.hasChildren()) {
			return this.children;
		}
		return null;
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
	
	private final static String newLine = "\n";
}
