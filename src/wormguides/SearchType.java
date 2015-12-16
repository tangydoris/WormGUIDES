package wormguides;

public enum SearchType {
	
	SYSTEMATIC ("lineage name"), 
	FUNCTIONAL ("functional name"), 
	DESCRIPTION ("'PartsList' description"),
	GENE ("gene"),
	CONNECTOME ("connectome"),
	/*
	 * This MULTICELL enum is different from SearchOption.MULTICELLULAR
	 * SearchTyle.MULTICELL tells the Search class to only look for multicellular structures
	 * SearchOption.MULTICELLULAR is used to distinguish multicell structures from cell bodies
	 * when meshes in the 3D window query the rules
	 */
	MULTICELL ("multicellular");
	
	private String description;
	
	SearchType() {
		this("");
	}
	
	SearchType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
