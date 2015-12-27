package wormguides.model;

import java.util.ArrayList;

import javafx.geometry.Point3D;

public class Note {

	private ArrayList<SceneElement> elements;
	private String tagName;
	private String tagContents;
	private Type attachmentType;
	private Display tagDisplay;
	private Point3D location;
	private ArrayList<String> cells;
	private boolean marker;
	private String imgSource;
	private String resourceLocation;
	private int startTime, endTime;
	private String comments;
	
	
	public Note() {
		elements = null;
		tagName = "";
		tagContents = "";
		location = new Point3D(0, 0, 0);
		cells = new ArrayList<String>();
		imgSource = "";
		resourceLocation = "";
		startTime = endTime = 0;
		comments = "";
	}
	
	
	public Note(String tagName, String tagContents) {
		this();
		this.tagName = tagName;
		this.tagContents = tagContents;
	}
	
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	
	public void setTagContents(String tagContents) {
		this.tagContents = tagContents;
	}
	
	
	public void setTagDisplay(String display) throws TagDisplayEnumException {
		for (Display d : Display.values()) {
			if (d.equalsTo(display.trim().toLowerCase())) {
				setTagDisplay(d);
				return;
			}
		}
		throw new TagDisplayEnumException();
	}
	
	
	private void setTagDisplay(Display display) {
		tagDisplay = display;
	}
	
	
	public void setAttachmentType(String type) throws AttachmentTypeEnumException {
		for (Type t : Type.values()) {
			if (t.equalsTo(type)) {
				setAttachmentType(t);
				return;
			}
		}
		throw new AttachmentTypeEnumException();
	}
	
	
	private void setAttachmentType(Type type) {
		attachmentType = type;
	}
	
	
	public void setLocation(String location) throws LocationStringFormatException{
		if (location!=null && !location.isEmpty()) {
			String[] coords = location.trim().split(" ");
			
			if (coords.length!=3)
				throw new LocationStringFormatException();
			
			try {
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				int z = Integer.parseInt(coords[2]);
				setLocation(x, y, z);
			} catch (NumberFormatException e) {
				throw new LocationStringFormatException();
			}
		}
	}
	
	
	public void setLocation(int x, int y, int z) {
		location = new Point3D(x, y, z);
	}
	
	
	public void setCells(String inputString) {
		String[] names = inputString.trim().split(" ");
		for (String name : names)
			cells.add(name);
	}
	
	
	public void setMarker(boolean marker) {
		this.marker = marker;
	}
	
	
	public SceneElement setImgSource(String imgSource) {
		this.imgSource = imgSource;
		if (!imgSource.isEmpty() && imgSource.trim().toLowerCase().endsWith(OBJ_EXT)) {
			elements = new ArrayList<SceneElement>();
			boolean billboardFlag = isDisplayBillboard(tagDisplay.toString());
			SceneElement element = new SceneElement(tagName, imgSource, resourceLocation, startTime,
													endTime, comments, billboardFlag);
			elements.add(element);
			return element;
		}
		return null;
	}
	
	
	public boolean hasSceneElements() {
		return elements!=null && !elements.isEmpty();
	}
	
	
	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}
	
	
	public void setStartTime(String time) throws TimeStringFormatException {
		if (time!=null && !time.isEmpty()) {
			try {
				int t = Integer.parseInt(time.trim());
				setStartTime(t);
			} catch (NumberFormatException e) {
				throw new TimeStringFormatException();
			}
		}
	}
	
	
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	
	
	public void setEndTime(String time) throws TimeStringFormatException {
		if (time!=null && !time.isEmpty()) {
			try {
				int t = Integer.parseInt(time.trim());
				setEndTime(t);
			} catch (NumberFormatException e) {
				throw new TimeStringFormatException();
			}
		}
	}
	
	
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
	
	public void setStartAndEndTimes(int startTime, int endTime) {
		if (startTime<=endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
	}
	
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	public void addSceneElement(SceneElement se) {
		if (se != null)
			elements.add(se);
	}
	
	
	public ArrayList<SceneElement> getSceneElements() {
		return elements;
	}
	
	
	public String getTagName() {
		return tagName;
	}
	
	
	public String getTagContents() {
		return tagContents;
	}
	
	
	public Display getTagDisplay() {
		return tagDisplay;
	}
	
	
	public Type getAttachmentType() {
		return attachmentType;
	}
	
	
	public Point3D getLocation() {
		return location;
	}
	
	
	public String[] getCells() {
		return cells.toArray(new String[cells.size()]);
	}
	
	
	public boolean getMarker() {
		return marker;
	}
	
	
	public String getImgSource() {
		return imgSource;
	}
	
	
	public String getResourceLocation() {
		return resourceLocation;
	}
	
	
	public int getStartTime() {
		return startTime;
	}
	
	
	public int getEndTime() {
		return endTime;
	}
	
	
	public String getComments() {
		return comments;
	}
	
	
	public String toString() {
		return tagName+": "+tagContents;
	}
	
	
	public boolean isTagDisplay(String display) {
		for (Display d : Display.values()) {
			if (d.equalsTo(display.trim().toLowerCase()))
				return true;
		}
		return false;
	}
	
	
	public boolean isDisplayBillboard(String display) {
		return Display.BILLBOARD.toString().equals(display.toLowerCase().trim());
	}
	
	
	public boolean isAttachmentType(String type) {
		for (Type t : Type.values()) {
			if (t.equalsTo(type.trim().toLowerCase()))
				return true;
		}
		return false;
	}
	
	
	public enum Type {
		LOCATION("location"),
		CELL("cell"),
		CELLTIME("cell time"), 
		TIME("time");
		
		private String type;
		
		Type(String type) {
			this.type = type;
		}
		
		String getType() {
			return type;
		}
		
		boolean equalsTo(String type) {
			return this.type.equals(type.toLowerCase());
		}
	}
	
	
	public enum Display {
		OVERLAY("overlay"),
		BILLBOARD("billboard"),
		SPRITE("sprite");
		
		private String display;
		
		Display(String display) {
			this.display = display;
		}
		
		String getDisplay() {
			return display;
		}
		
		boolean equalsTo(String display) {
			return this.display.equals(display.toLowerCase());
		}
		
	}
	
	
	public class TagDisplayEnumException extends Exception {
		public TagDisplayEnumException() {
			super("Invalid note tag display enum, must be one of the following: "
					+ "OVERLAY, BILLBOARD, SPRITE.");
		}
	}
	
	
	public class AttachmentTypeEnumException extends Exception {
		public AttachmentTypeEnumException() {
			super("Invalid note attachment type enum, must be one of the following: "
					+ "LOCATION, CELL, CELLTIME, TIME.");
		}
	}
	
	
	public class LocationStringFormatException extends Exception {
		public LocationStringFormatException() {
			super("Invalid note location string format, must be 3 integers separated by spaces.");
		}
	}
	
	
	public class TimeStringFormatException extends Exception {
		public TimeStringFormatException() {
			super("Invalid note time string format, must be integer.");
		}
	}
	
	
	private final String OBJ_EXT = ".obj";
}
