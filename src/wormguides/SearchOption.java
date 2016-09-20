/*
 * Bao Lab 2016
 */

package wormguides;

/**
 * Options used to elaborate on a search being made. Every search returns returns a list of cells, and these options
 * extend the list to include/disclude other cells/entities that fit the search option.
 */
public enum SearchOption {

    /**
     * SearchLayer for cells associated with the searched name. The {@link SearchOption#ANCESTOR} and {@link
     * SearchOption#DESCENDANT} searches are based off the list returned when this search is made.
     */
    CELL_NUCLEUS("Cell Nucleus"),

    /** SearchLayer for cell bodies that contain the cells in the search results list */
    CELL_BODY("Cell Body"),

    /**
     * Used to distinguish multicellular structures from cell bodies when scene element meshes query the rules to see
     * if the rule applies to it.
     */
    MULTICELLULAR_NAME_BASED("Multicellular Structure"),

    /** SearchLayer for ancestors of cells in the search results list */
    ANCESTOR("Its Ancestors"),

    /** SearchLayer for descendants of cells in the search results list */
    DESCENDANT("Its Descendants");

    private final String description;

    SearchOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
