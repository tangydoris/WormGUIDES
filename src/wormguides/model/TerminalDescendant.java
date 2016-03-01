package wormguides.model;

/**
 * class which holds a cell name and parts list entry for each terminal descendant (neuron)
 * 
 * @author katzmanb
 *
 */
public class TerminalDescendant {
	private String cellName;
	private String partsListEntry;

	public TerminalDescendant(String cellName, String partsListEntry) {
		this.cellName = cellName;
		this.partsListEntry = partsListEntry;
	}

	public String getCellName() {
		if (this.cellName != null) {
			return this.cellName;
		}
		return "N/A";
	}

	public String getPartsListEntry() {
		if (this.partsListEntry != null) {
			return this.partsListEntry;
		}
		return "N/A";
	}
}