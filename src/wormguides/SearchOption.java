package wormguides;

public enum SearchOption {

	CELL("Cell Nucleus"), 
	CELLBODY("Cell Body"),
	/**
	 * The SearchOption.MULTICELLULAR enum is different from
	 * SearchTyle.MULTICELL. SearchTyle.MULTICELL tells the Search class to only
	 * look for cells specified by the multicellular structures.
	 * SearchOption.MULTICELLULAR is used to distinguish multicell structures
	 * from cell bodies when meshes in the 3D window query the rules
	 */
	MULTICELLULAR_STRUCTURE_BASED("Multicellular Structure"), 
	ANCESTOR("Its Ancestors"), 
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
