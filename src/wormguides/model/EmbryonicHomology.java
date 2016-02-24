package wormguides.model;

public class EmbryonicHomology {
	private String cell_1;
	private String cell_2;

	public EmbryonicHomology(String cell_1, String cell_2) {
		this.cell_1 = cell_1;
		this.cell_2 = cell_2;
	}

	public String getCell1() {
		if (this.cell_1 != null) {
			return this.cell_1;
		}
		return "";
	}

	public String getCell2() {
		if (this.cell_2 != null) {
			return this.cell_2;
		}
		return "";
	}

	public String getHomology() {
		return this.getCell1() + ":" + this.getCell2();
	}
}
