package wormguides;

public enum SearchType {
	
	SYSTEMATIC ("lineage name"), 
	FUNCTIONAL ("functional name"), 
	DESCRIPTION ("'PartsList' description"),
	GENE ("gene");
	
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
