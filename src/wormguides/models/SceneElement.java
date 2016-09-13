package wormguides.models;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.shape.MeshView;

import wormguides.loaders.GeometryLoader;

/*
 * A SceneElement represents a cell body structure (uni or multicellular)
 */

public class SceneElement {

    private final String OBJ_EXT = ".obj";
    private String sceneName; // descriptor or display of object
    private ArrayList<String> cellNames; // cell names at time point i.e. cells
    // involved in this scene
    private String markerName; // used when neuron is separated from marker
    private String embryoName; // used when based on specific embryo
    private String imagingSource; // meta data
    private String resourceLocation; // directory that contains the .obj files
    // for this scene element. URL, JAR,
    // path, web server
    private int startTime;
    private int endTime;
    private String comments;
    private boolean completeResourceFlag;
    private int x, y, z; // coordinates used when element belongs to a note

    public SceneElement(
            String sceneName, ArrayList<String> cellNames, String markerName, String imagingSource,
            String resourceLocation, int startTime, int endTime, String comments) {

        this.sceneName = sceneName;
        this.cellNames = cellNames;
        this.markerName = markerName;
        this.embryoName = ""; // will fill this field in later?
        this.imagingSource = imagingSource;
        this.resourceLocation = resourceLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = comments;
        completeResourceFlag = isResourceComplete();

        // make sure there is proper capitalization in cell names
        // specificially "Ab" instead of "AB"
        ArrayList<String> editedNames = new ArrayList<>();
        Iterator<String> iter = cellNames.iterator();
        String name;
        while (iter.hasNext()) {
            name = iter.next();
            if (name.startsWith("Ab")) {
                iter.remove();
                editedNames.add("AB" + name.substring(2));
            }
        }
        cellNames.addAll(editedNames);
    }

    // Geometry used for notes in stories
    public SceneElement(
            String sceneName, String cellName, String markerName, String imagingSource,
            String resourceLocation, int startTime, int endTime, String comments) {
        this.sceneName = sceneName;
        this.cellNames = new ArrayList<>();
        cellNames.add(cellName);
        this.markerName = markerName;
        this.embryoName = ""; // will fill this field in later?
        this.imagingSource = imagingSource;
        this.resourceLocation = resourceLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = comments;

        completeResourceFlag = isResourceComplete();
    }

    private boolean isResourceComplete() {
        boolean complete = true;
        if (resourceLocation != null) {
            int idx = resourceLocation.lastIndexOf(".");
            if (idx != -1) {
                String extCheck = resourceLocation.substring(++idx); // substring
                // after
                // "."
                for (int i = 0; i < extCheck.length(); i++) {
                    if (!Character.isLetter(extCheck.charAt(i))) {
                        complete = false;
                    }
                }
            } else {
                complete = false;
            }
        }
        return complete;
    }

    public MeshView buildGeometry(int time) {
        // time++;
        // TODO OPTIMIZE THIS LATER
        // GeometryLoader loader = new GeometryLoader();

        // check if complete resource
        if (completeResourceFlag) {
            return GeometryLoader.loadOBJ(resourceLocation);
        }

        // append time and ext to resource location
        String objFile = resourceLocation + "_t" + time + OBJ_EXT;
        return GeometryLoader.loadOBJ(objFile);
    }

    public void setNewCellNames(ArrayList<String> cells) {
        this.cellNames.clear();
        this.cellNames = cells;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setMarker(String marker) {
        if (marker != null) {
            markerName = marker;
        }
    }

    public void addCellName(String name) {
        if (name != null) {
            cellNames.add(name);
        }
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String name) {
        if (name != null) {
            sceneName = name;
        }
    }

    public ArrayList<String> getAllCellNames() {
        return cellNames;
    }

    public boolean isMulticellular() {
        return cellNames.size() > 1;
    }

    public boolean existsAtTime(int time) {
        // time++;
        return startTime <= time && time <= endTime;
    }

    public String getMarkerName() {
        return markerName;
    }

    public String getEmbryoName() {
        return embryoName;
    }

    public void setEmbryoName(String name) {
        if (name != null) {
            embryoName = name;
        }
    }

    public String getImagingSource() {
        return imagingSource;
    }

    public void setImagingSource(String source) {
        if (source != null) {
            this.imagingSource = source;
        }
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String location) {
        if (location != null) {
            resourceLocation = location;
        }
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int time) {
        if (-1 < time) {
            startTime = time;
        }
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int time) {
        if (-1 < time) {
            endTime = time;
        }
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        if (comments != null) {
            this.comments = comments;
        }
    }

    public boolean getCompleteResourseFlag() {
        return completeResourceFlag;
    }

    public String toString() {
        String sb = "SceneElement[" + "@scenename=" + sceneName +
                "; @startTime=" + startTime +
                "; @endTime=" + endTime +
                "; @cells=" + cellNames.toString() +
                "; @resourceLocation=" + resourceLocation +
                "; @comments=" + comments +
                "]";
        return sb;
    }
}