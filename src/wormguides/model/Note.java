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
	private String cellName;
	private String marker;
	private String imagingSource;
	private String resourceLocation;
	private int startTime, endTime;
	private String comments;
	private String url;
	
	
	public Note() {
		elements = new ArrayList<SceneElement>();
		tagName = "";
		tagContents = "";
		location = new Point3D(0, 0, 0);
		cellName = "";
		imagingSource = "";
		resourceLocation = "";
		startTime = endTime = -1;
		comments = "";
		url = "";
	}
	
	
	public Note(String tagName, String tagContents) {
		this();
		this.tagName = tagName;
		this.tagContents = tagContents;
	}
	
	
	public void setUrl(String url) {
		if (url!=null)
			this.url = url;
	}
	
	
	public void setTagName(String tagName) {
		if (tagName!=null)
			this.tagName = tagName;
	}
	
	
	public void setTagContents(String tagContents) {
		if (tagContents!=null)
			this.tagContents = tagContents;
	}
	
	
	public SceneElement setTagDisplay(String display) throws TagDisplayEnumException {
		if (display!=null) {
			for (Display d : Display.values()) {
				if (d.equalsTo(display.trim().toLowerCase())) {
					setTagDisplay(d);
					if (d.equalsTo("billboard")) {
						SceneElement element = new SceneElement(tagName, cellName, marker, imagingSource, resourceLocation, 
																startTime, endTime, comments, true);
						elements.add(element);
						return element;
					}
					return null;
				}
			}
			throw new TagDisplayEnumException();
		}
		return null;
	}
	
	
	private void setTagDisplay(Display display) {
		if (display!=null)
			tagDisplay = display;
	}
	
	
	public void setAttachmentType(String type) throws AttachmentTypeEnumException {
		if (type!=null) {
			for (Type t : Type.values()) {
				if (t.equalsTo(type)) {
					setAttachmentType(t);
					return;
				}
			}
			throw new AttachmentTypeEnumException();
		}
	}
	
	
	private void setAttachmentType(Type type) {
		if (type!=null)
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
		for (SceneElement se : elements)
			se.setBillboardLocation(x, y, z);
	}
	
	
	public void setCellName(String name) {
		if (name!=null) {
			cellName = name;
			for (SceneElement se : elements)
				se.addCellName(name);
		}
	}
	
	
	public void setMarker(String marker) {
		if (marker!=null) {
			this.marker = marker;
			for (SceneElement se : elements)
				se.setMarker(marker);
		}
	}
	
	
	public void setImagingSource(String source) {
		if (source!=null && !source.isEmpty() && source.trim().toLowerCase().endsWith(OBJ_EXT)) {
			imagingSource = source;
			
			for (SceneElement se : elements)
				se.setImagingSource(imagingSource);
		}
	}
	
	
	public boolean hasSceneElements() {
		return !elements.isEmpty();
	}
	
	
	public void setResourceLocation(String location) {
		if (location!=null) {
			resourceLocation = location;
			for (SceneElement se : elements)
				se.setResourceLocation(resourceLocation);
		}
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
	
	
	public void setStartTime(int time) {
		this.startTime = time;
		for (SceneElement se : elements)
			se.setStartTime(time);
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
	
	
	public void setEndTime(int time) {
		this.endTime = time;
		for (SceneElement se : elements)
			se.setEndTime(time);
	}
	
	
	public void setStartAndEndTimes(int startTime, int endTime) {
		if (startTime<=endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			for (SceneElement se : elements) {
				se.setStartTime(startTime);
				se.setEndTime(endTime);
			}
		}
	}
	
	
	public void setComments(String comments) {
		if (comments!=null) {
			this.comments = comments;
			for (SceneElement se : elements)
				se.setComments(comments);
		}
	}
	
	
	public void addSceneElement(SceneElement element) {
		if (element!=null)
			elements.add(element);
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
	
	
	public String getCellName() {
		return cellName;
	}
	
	
	public String getMarker() {
		return marker;
	}
	
	
	public String getImgSource() {
		return imagingSource;
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
	
	
	// Returns true if display string correspongs to the Display.BILLBOARD enum
	// false otherwise
	public boolean isDisplayBillboardEnum(String display) {
		return Display.BILLBOARD.toString().equals(display.toLowerCase().trim());
	}
	
	
	// Returns true if the note tag display is Display.BILLBOARD
	// false otherwise
	public boolean isBillboard() {
		return tagDisplay==Display.BILLBOARD;
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
