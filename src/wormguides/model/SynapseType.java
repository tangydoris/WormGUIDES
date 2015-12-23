package wormguides.model;

public enum SynapseType {
	S_PRESYNAPTIC("S presynaptic"),
	R_POSTSYNAPTIC("R postsynaptic"),
	EJ_ELECTRICAL("EJ electrical"),
	NMJ_NEUROMUSCULAR("Nmj neuromuscular");
	
	private String description;
	
	SynapseType() {
		this("");
	}
	
	SynapseType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String toString() {
		return getDescription();
	}
}
