package wormguides.model;

import java.util.ListResourceBundle;
import wormguides.model.LineageData;

public class AceTreeResource extends ListResourceBundle {
	private LineageData lineageData;
	
	public AceTreeResource(LineageData data) {
		this.lineageData = data;
	}
	
	protected Object[][] getContents() {
		return new Object[][] {
			{"lineageData", lineageData}
		};
	}
}