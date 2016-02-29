package wormguides.model;

/**
 * A class which defines a synapse between two cells
 * 
 * @author katzmanb
 *
 */
public class NeuronalSynapse {

	// cell_1 and _2 are functional names
	private String cell_1;
	private String cell_2;

	private SynapseType synapseType;
	private int numberOfSynapses;

	public NeuronalSynapse(String cell_1, String cell_2, SynapseType synapseType, int numberOfSynapses) {
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