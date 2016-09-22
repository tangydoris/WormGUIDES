/*
 * Bao Lab 2016
 */

package wormguides.models;

/**
 * Options queried by the subscene entities to see if a color rule applies to it.
 */
public enum SearchOption {

    /**
     * SearchLayer for cells associated with the searched name. The {@link SearchOption#ANCESTOR} and {@link
     * SearchOption#DESCENDANT} searches are based off the list returned when this search is made.
     */
    CELL_NUCLEUS("Cell Nucleus"),

    /** Search for cell bodies that contain the cells in the search results list */
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
