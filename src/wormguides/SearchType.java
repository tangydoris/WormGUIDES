package wormguides;

public enum SearchType {
	
	SYSTEMATIC ("Lineage Name"), 
	FUNCTIONAL ("Functional Name"), 
	DESCRIPTION ("\"PartsList\" Description"),
	GENE ("Gene"),
	CONNECTOME ("Connectome"),
	/*
	 * This MULTICELL enum is different from SearchOption.MULTICELLULAR
	 * SearchTyle.MULTICELL tells the Search class to only look for multicellular structures
	 * SearchOption.MULTICELLULAR is used to distinguish multicell structures from cell bodies
	 * when meshes in the 3D window query the rules
	 */
	MULTICELL ("Multicellular Structure");
	
	
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
