package wormguides.model;

/**
 * Defines 4 categories of synapses
 * 
 * @author bradenkatzman
 *
 */
public enum SynapseType {
	S_PRESYNAPTIC("S presynaptic"), R_POSTSYNAPTIC("R postsynaptic"), EJ_ELECTRICAL("EJ electrical"), NMJ_NEUROMUSCULAR(
			"Nmj neuromuscular");

	private String description;
	private boolean poyadic;
	private boolean monadic;

	SynapseType() {
		this("");
		this.poyadic = false;
		this.monadic = false;
	}

	SynapseType(String description) {
		this.description = description;
		this.poyadic = false;
		this.monadic = false;
	}

	public void setPoyadic() {
		this.poyadic = true;
		this.monadic = false;
	}

	public void setMonadic() {
		this.poyadic = false;
		this.monadic = true;
	}

	public boolean isMonadic() {
		return this.monadic;
	}

	public boolean isPoyadic() {
		return this.poyadic;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return getDescription();
	}
}
