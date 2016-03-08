package wormguides;

public enum SearchOption {

	/**
	 * The CELL enum tells the {@link Search} class to look for cells associated
	 * with the searched name. This is the base list from which ANCESTOR and
	 * DESCENDANT cells are queried.
	 */
	CELLNUCLEUS("Cell Nucleus"),
	/**
	 * The CELLBODY enum tells the {@link Search} class to look for cell bodies
	 * that contain the cells in the search results list.
	 */
	CELLBODY("Cell Body"),
	/**
	 * The MULTICELLULAR_NAME_BASED enum is different from the
	 * SearchTyle.MULTICELLULAR_CELL_BASED enum.<br>
	 * <br>
	 * SearchTyle.MULTICELLULAR_CELL_BASED tells the {@link Search} class to
	 * look for cells contained in the searched multicellular structure(s).<br>
	 * <br>
	 * This MULTICELLULAR_NAME_BASED enum is used to distinguish multicellular
	 * structures from cell bodies when scene element meshes in query the rules.
	 */
	MULTICELLULAR_NAME_BASED("Multicellular Structure"),
	/**
	 * The ANCESTOR enum tells the {@link Search} class to look for ancestors of
	 * cells in the search results list.
	 */
	ANCESTOR("Its Ancestors"),
	/**
	 * The DESCENDANT enum tells the {@link Search} class to look for
	 * descendants of cells in the search results list.
	 */
	DESCENDANT("Its Descendants");

	private String description;

	SearchOption(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return getDescription();
	}
}
