/*
 * Bao Lab 2016
 */

package wormguides.layers;

public enum SearchType {

    /**
     * The LINEAGE enum tells the {@link wormguides.Search} class to execute a systematic
     * search for the cells with the lineage name specified in the search field.
     */
    LINEAGE("Lineage Name"),
    /**
     * The FUNCTIONAL enum tells the {@link wormguides.Search} class to execute a search
     * for cells with the functional name specified in the search field.
     */
    FUNCTIONAL("Functional Name"),
    /**
     * The DESCRIPTION enum tells the {@link wormguides.Search} class to execute a searh
     * for cells with the PartsList description specified in the search field.
     */
    DESCRIPTION("\"PartsList\" Description"),
    /**
     * The GENE enum tells the {@link wormguides.Search} class to execute a search for
     * cells with the gene expression expression specified in the search field.
     */
    GENE("Gene"),
    /**
     * The CONNECTOME enum tells the {@link wormguides.Search} class to execute a search
     * for cells with the ticked wiring(s) (pre-synaptic, post-synaptic,
     * electrical, or neuromuscular) to the cell whose lineage name is specified
     * in the search field.
     */
    CONNECTOME("Connectome"),
    /**
     * The MULTICELLULAR_CELL_BASED enum is different from the
     * SearchOption.MULTICELLULAR_NAME_BASED enum.<br>
     * <br>
     * MULTICELLULAR_CELL_BASED tells the {@link wormguides.Search} class to look for cells
     * specified by the searched multicellular structure(s).<br>
     * <br>
     * SearchOption.MULTICELLULAR_NAME_BASED is used to distinguish
     * multicellular structures from cell bodies when scene element meshes query
     * the rules.
     */
    MULTICELLULAR_CELL_BASED("Multicellular Structure"),
    /**
     * The NEIGHBOR enum tells the {@link wormguides.Search} class to execute a search for
     * the neighboring cells with the cell whose lineage name is specified in
     * the search field.
     */
    NEIGHBOR("Neighbor");

    private String description;

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