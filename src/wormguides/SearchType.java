package wormguides;

public enum SearchType {
	
	SYSTEMATIC ("systematic"), 
	FUNCTIONAL ("functional"), 
	DESCRIPTION ("description"), 
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
