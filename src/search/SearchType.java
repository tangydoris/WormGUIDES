/*
 * Bao Lab 2016
 */

package search;

/**
 * Types of search being made. The type defines the database that the application queries, whether it is the lineage
 * data, parts list, connectome, list of scene elements, or list of cell cases.
 */
public enum SearchType {

    /** Systematic search for the cells with the lineage name specified in the search field */
    LINEAGE("Lineage"),

    /** SearchLayer for cells with the functional name specified in the search field */
    FUNCTIONAL("Functional"),

    /** SearchLayer for cells with the PartsList description specified in the search field */
    DESCRIPTION("\"PartsList\" Description"),

    /** SearchLayer for cells with the gene expression expression specified in the search field */
    GENE("Gene"),

    /**
     * SearchLayer for cells with the ticked wiring(s) (pre-synaptic, post-synaptic, electrical, or neuromuscular) to the
     * cell whose lineage name is specified in the search field.
     */
    CONNECTOME("Connectome"),

    /** SearchLayer for cells specified by the searched multicellular structure(s) */
    MULTICELLULAR_CELL_BASED("Multicellular Structure"),

    /** SearchLayer for the neighboring cells with the cell whose lineage name is specified in the search field */
    NEIGHBOR("Neighbor");

    private final String description;

    SearchType(String description) {
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