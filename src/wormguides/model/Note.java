package wormguides.model;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/*
 * This class represents a note that belongs to a story (its parent)
 */
public class Note {

	// TODO Refactor this to be a single scene element?
	// right now it is possible for a note to have multiple scene elements
	// just by setting its resource location
	private ArrayList<SceneElement> elements;
	private String tagName;
	private String tagContents;
	private Type attachmentType;
	private Display tagDisplay;
	private int x, y, z;
	private String cellName;
	private String marker;
	private String imagingSource;
	private String resourceLocation;
	private int startTime, endTime;
	private String comments;
	private String url;
	
	private Story parent;
	
	// True when any field value changes, false otherwise
	private BooleanProperty changedBooleanProperty;
	// True when graphical representation is expanded, false otherwise
	private BooleanProperty expandedBooleanProperty;
	// True when graphical representation is selected, false otherwise
	private BooleanProperty activeBooleanProperty;
	
	
	public Note(Story parent) {
		this.parent = parent;
		
		elements = null;
		tagName = "";
		tagContents = "";
		x = y = z = Integer.MIN_VALUE;
		cellName = "";
		imagingSource = "";
		resourceLocation = "";
		startTime = endTime = Integer.MIN_VALUE;
		comments = "";
		url = "";
		
		changedBooleanProperty = new SimpleBooleanProperty(false);
		changedBooleanProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				if (newValue)
					setChanged(false);
			}
		});
		
		expandedBooleanProperty = new SimpleBooleanProperty(false);
		activeBooleanProperty = new SimpleBooleanProperty(false);
		
		setAttachmentType(Type.BLANK);
		setTagDisplay(Display.BLANK);
	}
	
	public Note(Story parent, String tagName, String tagContents) {
		this(parent);
		this.tagName = tagName;
		this.tagContents = tagContents;
	}
	
	
	public BooleanProperty getActiveBooleanProperty() {
		return activeBooleanProperty;
	}
	
	
	public boolean isActive() {
		return activeBooleanProperty.get();
	}
	
	
	public void setActive(boolean active) {
		activeBooleanProperty.set(active);
	}
	
	
	public BooleanProperty getExpandedBooleanProperty() {
		return expandedBooleanProperty;
	}
	
	
	public boolean isExpanded() {
		return expandedBooleanProperty.get();
	}
	
	
	public void setExpanded(boolean expanded) {
		expandedBooleanProperty.set(expanded);
	}
	
	
	public BooleanProperty getChangedProperty() {
		return changedBooleanProperty;
	}
	
	
	public void setChanged(boolean changed) {
		changedBooleanProperty.set(changed);
	}
	
	
	public boolean changed() {
		return changedBooleanProperty.get();
	}
	
	
	public Story getParent() {
		return parent;
	}
	
	
	public void setUrl(String url) {
		if (url!=null)
			this.url = url;
	}
	
	
	public void setTagName(String tagName) {
		if (tagName!=null) {
			this.tagName = tagName;
			//graphic.setTitle(tagName);
		}
		
		if (elements!=null) {
			for (SceneElement se : elements)
				se.setSceneName(tagName);
		}
	}
	
	
	public void setTagContents(String tagContents) {
		if (tagContents!=null) {
			this.tagContents = tagContents;
			//graphic.setTitle(tagContents);
		}
	}
	
	
	public void setTagDisplay(String display) throws TagDisplayEnumException {
		if (display!=null) {
			for (Display d : Display.values()) {
				if (d.equals(display.trim())) {
					setTagDisplay(d);
					return;
				}
			}
			throw new TagDisplayEnumException();
		}
	}
	
	
	public void setTagDisplay(Display display) {
		if (display!=null)
			tagDisplay = display;
	}
	
	
	public void setAttachmentType(String type) throws AttachmentTypeEnumException {
		if (type!=null) {
			for (Type t : Type.values()) {
				if (t.equals(type.trim())) {
					setAttachmentType(t);
					return;
				}
			}
			
			throw new AttachmentTypeEnumException();
		}
	}
	
	
	public void setAttachmentType(Type type) {
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
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (elements!=null) {
			for (SceneElement se : elements) {
				se.setLocation(x, y, z);
			}
		}
	}
	
	
	public void setCellName(String name) {
		if (name!=null) {
			cellName = name.trim();
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.addCellName(cellName);
			}
		}
	}
	
	
	public void setMarker(String marker) {
		if (marker!=null) {
			this.marker = marker.trim();
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setMarker(this.marker);
			}
		}
	}
	
	
	public void setImagingSource(String source) {
		if (source!=null && !source.isEmpty() && source.trim().toLowerCase().endsWith(OBJ_EXT)) {
			imagingSource = source.trim();
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setImagingSource(imagingSource);
			}
		}
	}
	
	
	public boolean hasSceneElements() {
		return elements!=null && !elements.isEmpty();
	}
	
	
	public void setResourceLocation(String location) {
		if (location!=null && location.trim().toLowerCase().endsWith(".obj")) {
			resourceLocation = location.trim();
			
			elements = new ArrayList<SceneElement>();
			SceneElement se = new SceneElement(tagName, cellName, marker, imagingSource,
											resourceLocation, startTime, endTime+1, comments);
			se.setLocation(x, y, z);
			elements.add(se);
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
		if (time>-1) {
			startTime = time++;
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setStartTime(startTime+1);
			}
		}
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
		if (time>-1) {
			endTime = time++;
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setEndTime(endTime+1);
			}
		}
	}
	
	
	public void setStartAndEndTimes(int startTime, int endTime) {
		if (-1<startTime && -1<endTime && startTime<=endTime) {
			this.startTime = startTime++;
			this.endTime = endTime++;
			
			if (elements!=null) {
				for (SceneElement se : elements) {
					se.setStartTime(this.startTime);
					se.setEndTime(this.endTime+1);
				}
			}
		}
	}
	
	
	public void setComments(String comments) {
		if (comments!=null) {
			this.comments = comments.trim();
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setComments(this.comments);
			}
		}
	}
	
	
	public void addSceneElementsToList(SceneElementsList list) {
		if (list!=null && elements!=null) {
			for (SceneElement se : elements) {
				list.addSceneElement(se);
			}
		}
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
	
	
	public int getX() {
		return x;
	}
	
	
	public int getY() {
		return x;
	}
	
	
	public int getZ() {
		return x;
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
		return tagName+" - '"+tagContents+"' from time "+startTime+" to "+endTime;
	}
	
	
	public boolean isTagDisplayEnum(String display) {
		for (Display d : Display.values()) {
			if (d.equals(display))
				return true;
		}
		return false;
	}
	
	
	// Notes in error mode should not be displayed
	// Returns true if time is not specified for CELLTIME and TIME attachment types
	// or if cell name is not specified for CELL and CELLTIME attachment types
	// or if there is no tag display method specified
	// false otherwise
	public boolean isWithoutScope() {
		if (tagDisplay==Display.BLANK)
			return true;
		
		if (tagDisplay!=Display.OVERLAY) {
			if ((attachmentType==Type.CELLTIME || attachmentType==Type.TIME)
					&& !isTimeSpecified())
				return true;
			
			if ((attachmentType==Type.CELL || attachmentType==Type.CELLTIME)
					&& !isCellSpecified())
				return true;
			
			if (isWithoutTypeScope())
				return true;
		}
		
		return false;
	}
	
	
	public boolean isWithoutTypeScope() {
		return attachmentType==Type.BLANK;
	}
	
	
	public boolean hasLocationError() {
		if (tagDisplay!=Display.OVERLAY) {
			
			if (attachmentType==Type.LOCATION && !isLoctionSpecified())
				return true;
			
			if (isAttachedToTime())
				return true;
		}
		
		return false;
	}
	
	
	public boolean hasCellNameError() {
		if (tagDisplay!=Display.OVERLAY && (attachmentType==Type.CELL 
				|| attachmentType==Type.CELLTIME) && cellName.isEmpty())
			return true;
		return false;
	}
	
	
	public boolean hasTimeError() {
		if (tagDisplay==Display.SPRITE && attachmentType==Type.TIME)
			return true;
		return false;
	}
	
	
	public boolean existsWithCell() {
		return attachmentType==Type.CELL || attachmentType==Type.CELLTIME;
	}
	
	
	public boolean isValidTimeAttachment() {
		return isAttachedToTime() && isTimeSpecified();
	}
	
	
	public boolean isAttachedToTime() {
		return attachmentType==Type.TIME;
	}
	
	
	public boolean isValidCellAttachment() {
		return isAttachedToCell() && isCellSpecified();
	}
	
	
	public boolean isValidCellTimeAttachment() {
		return isAttachedToCellTime() && isCellSpecified() && isTimeSpecified();
	}
	
	
	public boolean isAttachedToCell() {
		return attachmentType==Type.CELL;
	}
	
	
	public boolean isAttachedToCellTime() {
		return attachmentType==Type.CELLTIME;
	}
	
	
	public boolean isCellSpecified() {
		return !cellName.isEmpty();
	}
	
	
	public boolean isTimeSpecified() {
		return startTime>=0 && endTime>=0;
	}
	
	
	// Returns true if note is visible at input time, or in sprite cell/celltime mode
	// false otherwise
	public boolean existsAtTime(int time) {
		if (!isWithoutScope()) {
			// If start and end times are not set
			// then note exists at all times
			if (!isTimeSpecified())
				return true;
			
			time++;
			if (startTime<=time && time<=endTime)
				return true;
		}
		
		return false;
	}
	
	
	// Returns true if location was specified in csv file
	// false otherwise
	public boolean isLoctionSpecified() {
		return (x!=Integer.MIN_VALUE && y!=Integer.MIN_VALUE && z!=Integer.MIN_VALUE);
	}
	
	
	public boolean isOverlay() {
		return tagDisplay==Display.OVERLAY;
	}
	
	
	public boolean isSprite() {
		return tagDisplay==Display.SPRITE;
	}
	
	
	public boolean isBillboard() {
		return tagDisplay==Display.BILLBOARD;
		
	}
	
	
	public boolean isAttachmentTypeEnum(String type) {
		for (Type t : Type.values()) {
			if (t.equals(type))
				return true;
		}
		return false;
	}
	
	
	public enum Type {
		LOCATION("location"),
		CELL("cell"),
		CELLTIME("cell time"), 
		TIME("time"),
		BLANK("");
		
		private String type;
		
		Type(String type) {
			this.type = type;
		}
		
		String getType() {
			return type;
		}
		
		boolean equals(String type) {
			return this.type.equalsIgnoreCase(type.trim());
		}
		
		boolean equals(Type type) {
			return this==type;
		}
	}
	
	
	public enum Display {
		OVERLAY("overlay"),
		BILLBOARD("billboard"),
		BILLBOARD_FRONT("billboard front"),
		SPRITE("sprite"),
		BLANK("");
		
		private String display;
		
		Display(String display) {
			this.display = display;
		}
		
		String getDisplay() {
			return display;
		}
		
		boolean equals(String display) {
			return this.display.equalsIgnoreCase(display.trim());
		}
		
		boolean equals(Display display) {
			return this==display;
		}
	}
	
	
	public class TagDisplayEnumException extends Exception {
		public TagDisplayEnumException() {
			super("Invalid note tag display enum, must be one of the "
					+ "following: " + Display.values());
		}
	}
	
	
	public class AttachmentTypeEnumException extends Exception {
		public AttachmentTypeEnumException() {
			super("Invalid note attachment type enum, must be one of the "
					+ "following: " + Type.values());
		}
	}
	
	
	public class LocationStringFormatException extends Exception {
		public LocationStringFormatException() {
			super("Invalid note location string format, must be 3 "
					+ "integers separated by spaces.");
		}
	}
	
	
	public class TimeStringFormatException extends Exception {
		public TimeStringFormatException() {
			super("Invalid note time string format, must be integer.");
		}
	}
	
	
	private final String OBJ_EXT = ".obj";
}
