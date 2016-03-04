package wormguides;

public enum SearchType {
	
	LINEAGE ("Lineage Name"), 
	FUNCTIONAL ("Functional Name"), 
	DESCRIPTION ("\"PartsList\" Description"),
	GENE ("Gene"),
	CONNECTOME ("Connectome"),
	/**
	 * This SearchType.MULTICELL enum is different from SearchOption.MULTICELLULAR.
	 * SearchTyle.MULTICELL tells the Search class to only look for cells specified by multicellular structures.
	 * SearchOption.MULTICELLULAR is used to distinguish multicell structures from cell bodies
	 * when meshes in the 3D window query the rules
	 */
	MULTICELLULAR_CELL_BASED ("Multicellular Structure"),
	NEIGHBOR ("Neighbor");
	
	
	private String description;
	
	
	SearchType(String description) {
		this.description = description;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public String toString() {
		return getDescription();
	}
	
}
