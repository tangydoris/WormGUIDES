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
	
	//image vars
	private String imgSrc;
	private String alt;
	private int height;
	private int width;

	private ArrayList<HTMLNode> children;
	
	//container node with no ID - e.g. head, body
	public HTMLNode(String tag) {
		this.tag = tag;
	
		this.isContainer = true;
		this.hasID = false;
		this.isImage = false;
		this.isStyle = false;
		
		this.ID = null;
		this.style = null;
		this.innerHTML = null;
		this.imgSrc = null;
		this.alt = null;
		
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
		
		
		this.innerHTML = null;
		this.imgSrc = null;
		this.alt = null;
		
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
		
		this.imgSrc = null;
		this.alt = null;
		
		children = null;
	}
	
	//img node
	public HTMLNode(String ID, String imgSrc, String alt, String style, int height, int width) {
		this.tag = "img";
		this.ID = ID;
		this.imgSrc = imgSrc;
		this.alt = alt;
		this.style = style;
		this.height = height;
		this.width = width;
		
		this.isContainer = false;
		this.hasID = true;
		this.isImage = true;
		this.isStyle = false;
		
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
		
		this.imgSrc = null;
		this.alt = null;
		this.style = null;
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
		} else if (!node.isContainer() && !node.isImage()) { //e.g. <p id...
			nodeStr = newLine + "<" + node.getTag() + " id=\"" + node.getID() + "\">" 
						+ newLine + node.getInnerHTML() 
						+ newLine +  "</" + node.getTag() + ">"; 
		} else if (node.isImage() && node.hasID()) { //e.g. <img id...
			nodeStr = newLine + "<" + node.getTag() + " id=\"" + node.getID() + "\" src=\"" + node.getImgSrc() +
				"\" alt=\"" + node.getAlt() + "\" height=\"" + node.getHeight() + "\" width=\"" + node.getWidth() + "\">"; 
		} else if (node.isStyle()) {
			nodeStr = newLine + "<" + node.getTag() + " type=\"" + node.getID() + "\">"
					+ newLine + newLine + node.getStyle() + newLine + "</" + node.getTag() + ">";
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
		if (isImage) {
			return this.imgSrc;
		}
		return "";
	}
	
	public String getAlt() {
		if (isImage) {
			return this.alt;
		}
		return "";
	}
	
	public int getHeight() {
		if (isImage) {
			return this.height;
		}
		return 0;
	}
	
	public int getWidth() {
		if (isImage) {
			return this.width;
		}
		return 0;
	}
	
	private boolean hasChildren() {
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
	
	private final static String newLine = "\n";
}
