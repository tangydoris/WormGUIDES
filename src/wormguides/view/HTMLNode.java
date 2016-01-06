package wormguides.view;

public class HTMLNode {
	private String tag;
	private String ID;
	private String style;
	private String innerHTML;
	
	private boolean isImage;
	
	//image vars
	private String imgSrc;
	private String alt;
	private int height;
	private int width;
	
	public HTMLNode(String tag, String ID, String style, String innerHTML) {
		this.tag = tag;
		this.ID = ID;
		this.style = style;
		this.innerHTML = innerHTML;
		
		this.isImage = false;
	}
	
	public HTMLNode(String ID, String imgSrc, String alt, int height, int width) {
		this.tag = "img";
		this.ID = ID;
		this.style = null;
		this.innerHTML = null;
		
		this.isImage = true;
		
		this.imgSrc = imgSrc;
		this.alt = alt;
		this.height = height;
		this.width = width;
	}
	
	public String formatNode() {
		String node = null;
		if (!isImage) {
			node = "<" + tag + " id=\"" + this.ID + "\">" 
						+ this.innerHTML + "</" + this.tag + ">"; 
		} else {
			node = "<" + this.tag + " id=\"" + this.ID + 
				" alt=\"" + this.alt + "\" height=\"" + this.height + "\" width=\"" + this.width + "\">"; 
		}
		
		return node;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public String getStyle() {
		return this.style;
	}
	
	public String getInnerHTML() {
		return this.innerHTML;
	}
	
	public String getImgSrc() {
		return this.imgSrc;
	}
	
	public String getAlt() {
		return this.alt;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
}
