package wormguides.model;

import java.util.ListResourceBundle;

public class NucleiMgrAdapterResource extends ListResourceBundle {
	private LineageData lineageData;
	
	public NucleiMgrAdapterResource(LineageData data) {
		this.lineageData = data;
	}
	
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{"lineageData", lineageData}
		};
	}
}