/*
 * Bao Lab 2017
 */

package wormguides.models.subscenegeometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.shape.MeshView;

import static java.util.Objects.requireNonNull;

import static wormguides.loaders.GeometryLoader.loadOBJ;

/**
 * A structure in the scene, whether it is uni or multicellular. This can also be an entity with a mesh rendering (that
 * contains no cells) attached to a {@link wormguides.stories.Note}.
 */
public class SceneElement {

    /** Descriptor or display of object */
    private String sceneName;
    /** Cells contained by this structure */
    private List<String> cellNames;
    /** Used when neuron is separated from marker */
    private String markerName;
    /** Used when based on specific embryo */
    private String embryoName;
    /** Metadata */
    private String imagingSource;
    /** Path to the .obj file for this structure */
    private String resourceLocation;
    private int startTime;
    private int endTime;
    private String comments;
    private boolean completeResourceFlag;

    /** Coordinates used when element belongs to a note */
    private int x, y, z;

    /**
     * Constructor
     *
     * @param sceneName
     *         the scene name
     * @param cellNames
     *         the cells contained in the structure
     * @param markerName
     *         the marker name
     * @param imagingSource
     *         the imagine source
     * @param resourceLocation
     *         resource specifying the .obj file location
     * @param startTime
     *         the first time point in which this structure appears
     * @param endTime
     *         the last time point in which this structure appears
     * @param comments
     *         the structure comments
     */
    public SceneElement(
            final String sceneName,
            final List<String> cellNames,
            final String markerName,
            final String imagingSource,
            final String resourceLocation,
            final int startTime,
            final int endTime,
            final String comments) {

        this.sceneName = requireNonNull(sceneName);
        this.cellNames = requireNonNull(cellNames);
        this.markerName = requireNonNull(markerName);
        this.embryoName = ""; // will fill this field in later?
        this.imagingSource = requireNonNull(imagingSource);
        this.resourceLocation = requireNonNull(resourceLocation);
        this.completeResourceFlag = isResourceComplete();

        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = requireNonNull(comments);

        // make sure that lineage names that start with "AB" has the proper casing
        final List<String> editedNames = new ArrayList<>();
        final Iterator<String> iter = cellNames.iterator();
        final String lineagePrefix = "ab";
        String name;
        String namePrefix;
        while (iter.hasNext()) {
            name = iter.next();
            if (name.length() > 2) {
                namePrefix = name.substring(0, 2);
                if (namePrefix.startsWith(lineagePrefix)) {
                    iter.remove();
                    editedNames.add("AB" + name.substring(2));
                }
            }
        }
        cellNames.addAll(editedNames);
    }

    // Geometry used for notes in wormguides.stories
    public SceneElement(
            final String sceneName,
            final String cellName,
            final String markerName,
            final String imagingSource,
            final String resourceLocation,
            final int startTime,
            final int endTime,
            final String comments) {

        this.sceneName = sceneName;
        this.cellNames = new ArrayList<>();
        this.cellNames.add(cellName);
        this.markerName = markerName;
        this.embryoName = ""; // will fill this field in later?
        this.imagingSource = imagingSource;
        this.resourceLocation = resourceLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = comments;
        this.completeResourceFlag = isResourceComplete();
    }

    /**
     * @return true if the resource location path is complete with the .obj extension, false otherwise
     */
    private boolean isResourceComplete() {
        return resourceLocation.endsWith(".obj");
    }

    public MeshView buildGeometry(int time) {
        // check if complete resource
        if (completeResourceFlag) {
            return loadOBJ(resourceLocation);
        }
        // append time and ext to resource location
        return loadOBJ(resourceLocation + "_t" + time);
    }

    public void setNewCellNames(List<String> cells) {
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

    public List<String> getAllCells() {
        return cellNames;
    }

    public boolean isMulticellular() {
        return cellNames.size() > 1 || cellNames.get(0).toLowerCase().equals(MCS.toLowerCase());
    }
    
    public boolean isNoCellStructure() {
    	return cellNames.size() == 0;
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

    @Override
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
    
    private final static String MCS = "MCS";
}