package wormguides.model;

public class NeuronalSynapse {
	private String cell_1;
	private String cell_2;
	private SynapseType synapseType;
	private int numberOfSynapses;
	
	public NeuronalSynapse(String cell_1, String cell_2,
							SynapseType synapseType, int numberOfSynapses) {
		this.cell_1 = cell_1;
		this.cell_2 = cell_2;
		this.synapseType = synapseType;
		this.numberOfSynapses = numberOfSynapses;
	}
	
	public String getCell1() {
		return this.cell_1;
	}
	
	public String getCell2() {
		return this.cell_2;
	}
	
	public SynapseType getSynapseType() {
		return this.synapseType;
	}
	
	public int numberOfSynapses() {
		return this.numberOfSynapses;
	}
}