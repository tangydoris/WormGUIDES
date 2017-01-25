/*
 * Bao Lab 2017
 */

package wormguides.stories;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import wormguides.models.subscenegeometry.SceneElement;
import wormguides.models.subscenegeometry.SceneElementsList;

/**
 * This class represents a note that belongs to a story (its parent). A note contains a tag name, tag contents, an
 * attachment type, and a tag display. It may contain a location to which it belongs in the subscene, a cell to which
 * it is attached to, a marker name, an imaging attached to, a start/end time, and comments.
 * <p>
 * Notes can appears as sprites, 3D billboards, front-facing 3D billboards, or as text in the info pane. This is
 * dictated by {@link Note.Display}. If blank, the note is without scope and does not appear in the subscene.
 * <p>
 * Notes can be attached to entities such as cells, multicellular structures, or a location in the 3D subscene. This
 * is dictated by {@link Note.Type}. If a note is attached to a cell, structure, or location, but the cell,
 * structure, or location is not specified, the note is without scope and does not appear in the subscene.
 */
public class Note {

    private static final String OBJ_EXT = ".obj";

    // it is possible for a note to have multiple scene elements just by setting its resource location
    private List<SceneElement> elements;
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
    // True when graphic in wormguides.stories list view is expanded, false otherwise
    private BooleanProperty listExpandedProperty;
    // True when graphic in 3d subscene is expanded, false otherwise
    private BooleanProperty sceneExpandedProperty;
    // True when graphical representation is selected, false otherwise
    private BooleanProperty activeProperty;

    public Note(final Story parent, final String tagName, final String tagContents) {
        this(parent);
        this.tagName = tagName;
        this.tagContents = tagContents;
    }

    public Note(final Story parent) {
        this.parent = parent;
        this.elements = null;
        this.tagName = "";
        this.tagContents = "";
        this.x = this.y = this.z = Integer.MIN_VALUE;
        this.cellName = "";
        this.imagingSource = "";
        this.resourceLocation = "";
        this.startTime = endTime = Integer.MIN_VALUE;
        this.comments = "";
        this.changedProperty = new SimpleBooleanProperty(false);
        this.changedProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setChanged(false);
            }
        });

        this.listExpandedProperty = new SimpleBooleanProperty(false);
        this.sceneExpandedProperty = new SimpleBooleanProperty(false);
        this.activeProperty = new SimpleBooleanProperty(false);

        setTagDisplay(Display.OVERLAY);
        setAttachmentType(Type.BLANK);
    }

    public String getLocationString() {
        if (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE || z == Integer.MIN_VALUE) {
            return "";
        }

        return x + " " + y + " " + z;
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

    public boolean isExpandedInScene() {
        return sceneExpandedProperty.get();
    }

    public void setExpandedInScene(boolean expanded) {
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
        if (url != null) {
        }
    }

    public void setTagDisplay(final String display) throws TagDisplayEnumException {
        if (display != null) {
            for (Display d : Display.values()) {
                if (d.equals(display.trim())) {
                    setTagDisplay(d);
                    return;
                }
            }
            throw new TagDisplayEnumException();
        }
    }

    public void setAttachmentType(final String type) throws AttachmentTypeEnumException {
        if (type != null) {
            for (Type t : Type.values()) {
                if (t.equals(type.trim())) {
                    setAttachmentType(t);
                    return;
                }
            }

            throw new AttachmentTypeEnumException();
        }
    }

    public void setLocation(final String location) throws LocationStringFormatException {
        if (location != null && !location.isEmpty()) {
            final String[] coords = location.trim().split(" ");

            if (coords.length != 3) {
                throw new LocationStringFormatException();
            }

            try {
                setLocation(
                        Integer.parseInt(coords[0]),
                        Integer.parseInt(coords[1]),
                        Integer.parseInt(coords[2]));

            } catch (NumberFormatException e) {
                throw new LocationStringFormatException();
            }
        }
    }

    public void setLocation(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;

        if (elements != null) {
            for (SceneElement se : elements) {
                se.setLocation(x, y, z);
            }
        }
    }

    public void setImagingSource(final String source) {
        if (source != null
                && !source.isEmpty()
                && source.trim().toLowerCase().endsWith(OBJ_EXT)) {

            imagingSource = source.trim();

            if (elements != null) {
                for (SceneElement se : elements) {
                    se.setImagingSource(imagingSource);
                }
            }
        }
    }

    public boolean hasSceneElements() {
        return elements != null && !elements.isEmpty();
    }

    public void setStartAndEndTimes(int start, int end) {
        startTime = start;
        endTime = end;

        if (elements != null) {
            for (SceneElement se : elements) {
                se.setStartTime(startTime);
                se.setEndTime(endTime);
            }
        }
    }

    public void addSceneElementsToList(final SceneElementsList list) {
        if (list != null && elements != null) {
            elements.forEach(list::addSceneElement);
        }
    }

    public List<SceneElement> getSceneElements() {
        return elements;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        if (tagName != null) {
            this.tagName = tagName;
        }

        if (elements != null) {
            for (SceneElement se : elements) {
                se.setSceneName(tagName);
            }
        }
    }

    public String getTagContents() {
        return tagContents;
    }

    public void setTagContents(String tagContents) {
        if (tagContents != null) {
            this.tagContents = tagContents;
        }
    }

    public Display getTagDisplay() {
        return tagDisplay;
    }

    public void setTagDisplay(Display display) {
        if (display != null) {
            tagDisplay = display;
        }
    }

    public Type getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(Type type) {
        if (type != null) {
            attachmentType = type;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String name) {
        if (name != null) {
            cellName = name.trim();

            if (elements != null) {
                for (SceneElement se : elements) {
                    se.addCellName(cellName);
                }
            }
        }
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        if (marker != null) {
            this.marker = marker.trim();

            if (elements != null) {
                for (SceneElement se : elements) {
                    se.setMarker(this.marker);
                }
            }
        }
    }

    public String getImgSource() {
        return imagingSource;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(final String location) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        if (location != null && !location.isEmpty()) {
            resourceLocation = location.trim();
            String sceneName = resourceLocation;
            if (resourceLocation.lastIndexOf("/") != -1) {
                sceneName = resourceLocation.substring(resourceLocation.lastIndexOf("/") + 1);
            }
            final SceneElement se = new SceneElement(
                    sceneName,
                    cellName,
                    marker,
                    imagingSource,
                    resourceLocation,
                    startTime,
                    endTime + 1,
                    comments);
            se.setLocation(x, y, z);
            elements.add(se);
        }
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int time) {
        if (time >= 1) {
            startTime = time;

            if (elements != null) {
                for (SceneElement se : elements) {
                    se.setStartTime(startTime);
                }
            }
        }
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int time) {
        if (time >= 1) {
            endTime = time;

            if (elements != null) {
                for (SceneElement se : elements) {
                    se.setEndTime(endTime);
                }
            }
        }
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        if (comments != null) {
            this.comments = comments.trim();

            if (elements != null) {
                for (SceneElement se : elements) {
                    se.setComments(this.comments);
                }
            }
        }
    }

    public boolean isTagDisplayEnum(String display) {
        for (Display d : Display.values()) {
            if (d.equals(display)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tells whether a note is without scope. Such a note is not displayed. Not having scope is defined by any of the
     * following combinations:
     * 1. time is not specified for the CELLTIME or TIME attachment types
     * 2. cell name is not specified for the CELLTIME or CELL attachment types
     * 3. no tag display methods is specified
     *
     * @return true if the note is without scope, false otherwise
     */
    public boolean isWithoutScope() {
        return tagDisplay.equals(Display.BLANK)
                || !tagDisplay.equals(Display.OVERLAY)
                && attachmentType.equals(Type.CELL)
                && !isEntitySpecified();

    }

    public boolean hasLocationError() {
        return attachmentType.equals(Type.LOCATION)
                && !isLoctionSpecified();

    }

    public boolean hasEntityNameError() {
        return !tagDisplay.equals(Display.OVERLAY)
                && (attachedToCell() || attachedToStructure())
                && cellName.isEmpty();

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
        return startTime >= 0 && endTime >= 0;
    }

    /**
     * @param time
     *         time to check
     *
     * @return true if note is visible at input time, or in sprite cell/celltime mode, false otherwise
     */
    public boolean mayExistAtTime(int time) {
        if (!isWithoutScope()) {
            // If start and end times are not set
            // then note exists at all times
            if (!isTimeSpecified()) {
                return true;
            }
            if (startTime <= time && time <= endTime) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if location was specified in the CSV file, false otherwise
     */
    public boolean isLoctionSpecified() {
        return (x != Integer.MIN_VALUE
                && y != Integer.MIN_VALUE
                && z != Integer.MIN_VALUE);
    }

    public boolean isOverlay() {
        return tagDisplay == Display.OVERLAY;
    }

    public boolean isSprite() {
        return tagDisplay == Display.SPRITE;
    }

    public boolean isBillboard() {
        return tagDisplay == Display.BILLBOARD;
    }

    public boolean isBillboardFront() {
        return tagDisplay == Display.BILLBOARD_FRONT;
    }

    public boolean isAttachmentTypeEnum(String type) {
        for (Type t : Type.values()) {
            if (t.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "Note[" + "@Name='" + tagName + "' " +
                "@Type=" + attachmentType + " " +
                "@Display=" + tagDisplay + " " +
                "@Time=" + startTime + ", " + endTime + " " +
                "@Location=" + x + ", " + y + ", " + z + " " +
                "@Cell='" + cellName + "' " +
                "@Resource='" + resourceLocation + "']";
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

        public static String valuesToString() {
            final ArrayList<String> values = new ArrayList<>();
            for (Type type : values()) {
                values.add(type.toString());
            }
            return String.join(",", values);
        }

        @Override
		public String toString() {
            return type;
        }

        public boolean equals(String type) {
            return this.type.equalsIgnoreCase(type.trim());
        }

        public boolean equals(Type type) {
            return this == type;
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

        public static String valuesToString() {
            final ArrayList<String> values = new ArrayList<>();
            for (Display display : values()) {
                values.add(display.toString());
            }
            return String.join(",", values);
        }

        @Override
		public String toString() {
            return display;
        }

        public boolean equals(final String display) {
            return this.display.equalsIgnoreCase(display.trim());
        }

        public boolean equals(final Display display) {
            return this == display;
        }
    }

    public class TagDisplayEnumException extends Exception {
    	// default variable needed for some reason
		private static final long serialVersionUID = 1L;

		public TagDisplayEnumException() {
            super("Invalid note tag display enum, must be one of the " + "following: " + Display.valuesToString());
        }
    }

    public class AttachmentTypeEnumException extends Exception {
    	// default variable needed for some reason
		private static final long serialVersionUID = 1L;

		public AttachmentTypeEnumException() {
            super("Invalid note attachment type enum, must be one of the " + "following: " + Type.valuesToString());
        }
    }

    public class LocationStringFormatException extends Exception {
    	// default variable needed for some reason
		private static final long serialVersionUID = 1L;

		public LocationStringFormatException() {
            super("Invalid note location string format, must be 3 " + "integers separated by spaces.");
        }
    }
}
