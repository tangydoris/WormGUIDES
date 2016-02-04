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
	private Story parent;
	
	// True when any field value changes, false otherwise
	private BooleanProperty changedProperty;
	// True when graphic in stories list view is expanded, false otherwise
	private BooleanProperty listExpandedProperty;
	// True when graphic in 3d subscene is expanded, false otherwise
	private BooleanProperty sceneExpandedProperty;
	// True when graphical representation is selected, false otherwise
	private BooleanProperty activeProperty;
	
	
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
		changedProperty = new SimpleBooleanProperty(false);
		changedProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				if (newValue)
					setChanged(false);
			}
		});
		
		listExpandedProperty = new SimpleBooleanProperty(false);
		sceneExpandedProperty = new SimpleBooleanProperty(false);
		activeProperty = new SimpleBooleanProperty(false);
		
		setAttachmentType(Type.BLANK);
		setTagDisplay(Display.BLANK);
	}
	
	public Note(Story parent, String tagName, String tagContents) {
		this(parent);
		this.tagName = tagName;
		this.tagContents = tagContents;
	}
	
	
	public BooleanProperty getActiveProperty() {
		return activeProperty;
	}
	
	
	public boolean isActive() {
		return activeProperty.get();
	}
	
	
	public void setActive(boolean active) {
		activeProperty.set(active);
	}
	
	
	public BooleanProperty getSceneExpandedProperty() {
		return sceneExpandedProperty;
	}
	
	
	public boolean isSceneExpanded() {
		return sceneExpandedProperty.get();
	}
	
	
	public void setSceneExpanded(boolean expanded) {
		sceneExpandedProperty.set(expanded);
	}
	
	
	public BooleanProperty getListExpandedProperty() {
		return listExpandedProperty;
	}
	
	
	public boolean isListExpanded() {
		return listExpandedProperty.get();
	}
	
	
	public void setListExpanded(boolean expanded) {
		listExpandedProperty.set(expanded);
	}
	
	
	public BooleanProperty getChangedProperty() {
		return changedProperty;
	}
	
	
	public void setChanged(boolean changed) {
		changedProperty.set(changed);
	}
	
	
	public boolean changed() {
		return changedProperty.get();
	}
	
	
	public Story getParent() {
		return parent;
	}
	
	
	public void setUrl(String url) {
		if (url!=null) {
		}
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

	
	public void setStartTime(int time) {
		if (time>=1) {
			startTime = time;
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setStartTime(startTime);
			}
		}
	}
	
	
	public void setEndTime(int time) {
		if (time>=1) {
			endTime = time;
			
			if (elements!=null) {
				for (SceneElement se : elements)
					se.setEndTime(endTime);
			}
		}
	}
	
	
	public void setStartAndEndTimes(int start, int end) {
		startTime = start;
		endTime = end;
		
		if (elements!=null) {
			for (SceneElement se : elements) {
				se.setStartTime(startTime);
				se.setEndTime(endTime);
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
		StringBuilder sb = new StringBuilder();
		sb.append("Note[").append("@Name='").append(tagName).append("' ");
		sb.append("@Type=").append(attachmentType).append(" ");
		sb.append("@Display=").append(tagDisplay).append(" ");
		sb.append("@Time=").append(startTime).append(", ").append(endTime).append(" ");
		sb.append("@Location=").append(x).append(", ").append(y).append(", ").append(z).append(" ");
		sb.append("@Cell='").append(cellName).append("' ");
		sb.append("@Resource='").append(resourceLocation).append("']");
		
		return sb.toString();
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
		if (tagDisplay.equals(Display.BLANK))
			return true;
		
		if (!tagDisplay.equals(Display.OVERLAY) && attachmentType.equals(Type.CELL) 
				&& !isEntitySpecified())
			return true;
		
		return false;
	}
	
	
	public boolean hasLocationError() {
		if (attachmentType.equals(Type.LOCATION) && !isLoctionSpecified())
				return true;
		
		return false;
	}
	
	
	public boolean hasEntityNameError() {
		if (!tagDisplay.equals(Display.OVERLAY) && (attachedToCell() 
				|| attachedToStructure())  && cellName.isEmpty())
			return true;
		
		return false;
	}
	
	
	public boolean attachedToStructure() {
		return attachmentType.equals(Type.STRUCTURE);
	}
	
	
	public boolean attachedToCell() {
		return attachmentType.equals(Type.CELL);
	}
	
	
	public boolean attachedToLocation() {
		return attachmentType.equals(Type.LOCATION);
	} 
	
	
	public boolean attachedToGlobalEvent() {
		return attachmentType.equals(Type.BLANK);
	}
	
	
	public boolean isEntitySpecified() {
		return !cellName.isEmpty();
	}
	
	
	public boolean isTimeSpecified() {
		return startTime>=0 && endTime>=0;
	}
	
	
	// Returns true if note is visible at input time, or in sprite cell/celltime mode
	// false otherwise
	public boolean mayExistAtTime(int time) {
		if (!isWithoutScope()) {
			// If start and end times are not set
			// then note exists at all times
			if (!isTimeSpecified())
				return true;
			
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
	
	
	public boolean isBillboardFront() {
		return tagDisplay==Display.BILLBOARD_FRONT;
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
		STRUCTURE("structure"),
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
		
		static String valuesToString() {
			StringBuilder sb = new StringBuilder();
			int len = values().length;
			Type[] values = values();
			for (int i=0; i<len; i++) {
				if (i<len-1)
					sb.append(values[i]).append(", ");
				else
					sb.append(values[i]);
			}
			return sb.toString();
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
		
		static String valuesToString() {
			StringBuilder sb = new StringBuilder();
			int len = values().length;
			Display[] values = values();
			for (int i=0; i<len; i++) {
				if (i<len-1)
					sb.append(values[i]).append(", ");
				else
					sb.append(values[i]);
			}
			return sb.toString();
		}
	}
	
	
	public class TagDisplayEnumException extends Exception {
		public TagDisplayEnumException() {
			super("Invalid note tag display enum, must be one of the "
					+ "following: " + Display.valuesToString());
		}
	}
	
	
	public class AttachmentTypeEnumException extends Exception {
		public AttachmentTypeEnumException() {
			super("Invalid note attachment type enum, must be one of the "
					+ "following: " + Type.valuesToString());
		}
	}
	
	
	public class LocationStringFormatException extends Exception {
		public LocationStringFormatException() {
			super("Invalid note location string format, must be 3 "
					+ "integers separated by spaces.");
		}
	}
	
	
	private final String OBJ_EXT = ".obj";
	private final int FRAME_OFFSET = 19;
}
