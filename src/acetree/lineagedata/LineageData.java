/*
 * Bao Lab 2016
 */

package acetree.lineagedata;

import java.util.ArrayList;

/**
 * Data structure interface with methods to query the underlying cell lineage data.
 */
public interface LineageData {

    /**
     * @return all cell names in the lineage, case-sensitive
     */
    ArrayList<String> getAllCellNames();

    /**
     * @param time
     *         time to check
     *
     * @return names of cells that exist at that time. The i-th element of the name, positions and diameter arrays
     * are information on the i-th cell at one timepoint.
     */
    String[] getNames(final int time);

    /**
     * @param time
     *         time to check
     *
     * @return size-3 integer arrays that specify the x-, y-, z-coordinates of the cell positions in 3D space. The i-th
     * element of the name, positions and diameter arrays are information on the i-th cell at one timepoint.
     */
    Integer[][] getPositions(final int time);

    /**
     * @param time
     *         time to check
     *
     * @return diameters at that time.  The i-th element of the name, positions and diameter arrays are information
     * on the i-th cell at one timepoint.
     */
    Integer[] getDiameters(final int time);

    /**
     * @return number of total timepoints in the lineage
     */
    int getNumberOfTimePoints();

    /**
     * Retrieves the first occurence of a cell with a specified name
     *
     * @param name
     *         the name of the cell
     *
     * @return first point in time for which the cell exists
     */
    int getFirstOccurrenceOf(final String name);

    /**
     * Retrieves the last occurence of a cell with a specified name
     *
     * @param name
     *         the name of the cell
     *
     * @return final point in time for which the cell exists
     */
    int getLastOccurrenceOf(final String name);

    /**
     * Adds a time frame to the lineage data. A time frame is be represented by whatever data structure an
     * implementing class chooses to use, and should contain information on all the cells, their positions and their
     * diameters at one point in time.
     */
    void addTimeFrame();

    /**
     * Adds nucleus data for a cell for the specified case-sensitive name
     *
     * @param time
     *         time at which this cell exists, starting from 1
     * @param name
     *         name of the cell
     * @param x
     *         x-coordinate of the cell in 3D space
     * @param y
     *         y-coordinate of the cell in 3D space
     * @param z
     *         z-coordinate of the cell in 3D space
     * @param diameter
     *         diameter of the cell
     */
    void addNucleus(final int time, final String name, final int x, final int y, final int z, final int diameter);

    /**
     * @param name
     *         name to check
     *
     * @return true if the name is a case-insensitive cell name in the lineage data, false otherwise
     */
    boolean isCellName(final String name);

    /**
     * Shifts all the positions in all time frames by a specified x-, y- and z-offset.
     *
     * @param x
     *         Amount of offset the x-coordinates by
     * @param y
     *         Amount of offset the y-coordinates by
     * @param z
     *         Amount of offset the z-coordinates by
     */
    void shiftAllPositions(final int x, final int y, final int z);

    /**
     * @return true if the lineage is in Sulston mode, false otherwise
     */
    boolean isSulstonMode();

    /**
     * Sets the flag that speficies whether the lineage is in Sulston mode
     *
     * @param isSulston
     *         false if the lineage is in Sulston mode, false otherwise
     */
    void setIsSulstonModeFlag(final boolean isSulston);
}