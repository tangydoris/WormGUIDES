package wormguides.models;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * This class represents a note that belongs to a story (its parent). A note
 * contains a tag name, tag contents, an attachment type {@link Type}, a tag
 * display {@link Display}. It may contain a location to which it belongs in the
 * subscene, a cell to which it is attached to, a marker name, an imaging
 * source, a resource location that specifies the {@link SceneElement} it is
 * attached to, a start/end time, and comments.<br>
 * <br>
 * Notes can appears as sprites, 3D billboards, front-facing 3D billboards, or
 * as text in the info pane. Enums representing these displays can be found in
 * the inner class {@link Display}. If blank, the note is without scope and does
 * not appear in the subscene.<br>
 * <br>
 * Notes can be attached to entities such as cells, multicellular structures, or
 * a location in the 3D subscene. Enums representing these attachment types can
 * be found in the inner class {@link Type}. If a note is attached to a cell,
 * structure, or location, but the cell, structure, or location is not
 * specified, the note is without scope and does not appear in the subscene.
 *
 * @author Doris Tang
 */
public class Note {

    private final String OBJ_EXT = ".obj";
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
        changedProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setChanged(false);
            }
        });

        listExpandedProperty = new SimpleBooleanProperty(false);
        sceneExpandedProperty = new SimpleBooleanProperty(false);
        activeProperty = new SimpleBooleanProperty(false);

        setTagDisplay(Display.OVERLAY);
        setAttachmentType(Type.BLANK);
    }

    public Note(Story parent, String tagName, String tagContents) {
        this(parent);
        this.tagName = tagName;
        this.tagContents = tagContents;
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

    public void setTagDisplay(String display) throws TagDisplayEnumException {
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

    public void setAttachmentType(String type) throws AttachmentTypeEnumException {
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

    public void setLocation(String location) throws LocationStringFormatException {
        if (location != null && !location.isEmpty()) {
            String[] coords = location.trim().split(" ");

            if (coords.length != 3) {
                throw new LocationStringFormatException();
            }

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

        if (elements != null) {
            for (SceneElement se : elements) {
                se.setLocation(x, y, z);
            }
        }
    }

    public void setImagingSource(String source) {
        if (source != null && !source.isEmpty() && source.trim().toLowerCase().endsWith(OBJ_EXT)) {
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

    public void addSceneElementsToList(SceneElementsList list) {
        if (list != null && elements != null) {
            elements.forEach(list::addSceneElement);
        }
    }

    public ArrayList<SceneElement> getSceneElements() {
        return elements;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        if (tagName != null) {
            this.tagName = tagName;
            // graphic.setTitle(tagName);
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
            // graphic.setTitle(tagContents);
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

    public void setResourceLocation(String location) {
        if (location != null && location.trim().toLowerCase().endsWith(".obj")) {
            resourceLocation = location.trim();

            elements = new ArrayList<>();
            SceneElement se = new SceneElement(tagName, cellName, marker, imagingSource, resourceLocation, startTime,
                    endTime + 1, comments);
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

    public String toString() {
        String sb = "Note[" + "@Name='" + tagName + "' " +
                "@Type=" + attachmentType + " " +
                "@Display=" + tagDisplay + " " +
                "@Time=" + startTime + ", " + endTime + " " +
                "@Location=" + x + ", " + y + ", " + z + " " +
                "@Cell='" + cellName + "' " +
                "@Resource='" + resourceLocation + "']";

        return sb;
    }

    public boolean isTagDisplayEnum(String display) {
        for (Display d : Display.values()) {
            if (d.equals(display)) {
                return true;
            }
        }
        return false;
    }

    // Notes in error mode should not be displayed
    // Returns true if time is not specified for CELLTIME and TIME attachment
    // types
    // or if cell name is not specified for CELL and CELLTIME attachment types
    // or if there is no tag display method specified
    // false otherwise
    public boolean isWithoutScope() {
        return tagDisplay.equals(Display.BLANK)
                || !tagDisplay.equals(Display.OVERLAY)
                && attachmentType.equals(Type.CELL)
                && !isEntitySpecified();

    }

    public boolean hasLocationError() {
        return attachmentType.equals(Type.LOCATION) && !isLoctionSpecified();

    }

    public boolean hasEntityNameError() {
        return !tagDisplay.equals(Display.OVERLAY) && (attachedToCell() || attachedToStructure()) && cellName.isEmpty();

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

    // Returns true if note is visible at input time, or in sprite cell/celltime
    // mode
    // false otherwise
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

    // Returns true if location was specified in csv file
    // false otherwise
    public boolean isLoctionSpecified() {
        return (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE && z != Integer.MIN_VALUE);
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

    public enum Type {
        LOCATION("location"), CELL("cell"), STRUCTURE("structure"), BLANK("");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public static String valuesToString() {
            StringBuilder sb = new StringBuilder();
            int len = values().length;
            Type[] values = values();
            for (int i = 0; i < len; i++) {
                if (i < len - 1) {
                    sb.append(values[i]).append(", ");
                } else {
                    sb.append(values[i]);
                }
            }
            return sb.toString();
        }

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
        OVERLAY("overlay"), BILLBOARD("billboard"), BILLBOARD_FRONT("billboard front"), SPRITE("sprite"), BLANK("");

        private String display;

        Display(String display) {
            this.display = display;
        }

        public static String valuesToString() {
            StringBuilder sb = new StringBuilder();
            int len = values().length;
            Display[] values = values();
            for (int i = 0; i < len; i++) {
                if (i < len - 1) {
                    sb.append(values[i]).append(", ");
                } else {
                    sb.append(values[i]);
                }
            }
            return sb.toString();
        }

        public String toString() {
            return display;
        }

        public boolean equals(String display) {
            return this.display.equalsIgnoreCase(display.trim());
        }

        public boolean equals(Display display) {
            return this == display;
        }
    }

    @SuppressWarnings("serial")
    public class TagDisplayEnumException extends Exception {
        public TagDisplayEnumException() {
            super("Invalid note tag display enum, must be one of the " + "following: " + Display.valuesToString());
        }
    }

    @SuppressWarnings("serial")
    public class AttachmentTypeEnumException extends Exception {
        public AttachmentTypeEnumException() {
            super("Invalid note attachment type enum, must be one of the " + "following: " + Type.valuesToString());
        }
    }

    @SuppressWarnings("serial")
    public class LocationStringFormatException extends Exception {
        public LocationStringFormatException() {
            super("Invalid note location string format, must be 3 " + "integers separated by spaces.");
        }
    }
}
