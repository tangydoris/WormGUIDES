package wormguides;

public enum SearchOption {
	
	CELL("cell nucleus"),
	CELLBODY("cell body"),
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
