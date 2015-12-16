package wormguides;

public enum SearchOption {
	
	CELL("cell nucleus"),
	CELLBODY("cell body"),
	/*
	 * This multicellular enum is different from SearchTyle.MULTICELL
	 * SearchTyle.MULTICELL tells the Search class to only look for multicellular structures
	 * SearchOption.MULTICELLULAR is used to distinguish multicell structures from cell bodies
	 * when meshes in the 3D window query the rules
	 */
	MULTICELLULAR("multicellular"),
	ANCESTOR("ancestor"),
	DESCENDANT("descendant");
	
	private String description;
	
	SearchOption() {
		this("");
	}
	
	SearchOption(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
