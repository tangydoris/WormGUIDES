package wormguides;

import wormguides.HTMLGenerator.HTMLTags;
import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;

public class CellShapesIndexToHTML {
	
	SceneElementsList elementsList;
	
	public CellShapesIndexToHTML(SceneElementsList elementsList) {
		super();
		this.elementsList = elementsList;
	}
	
	public String buildCellShapesIndexAsHTML() {
		String html = HTMLTags.openTableTagHTML + HTMLTags.openTableRowHTML + 
				HTMLTags.openTableHeaderHTML + "Scene Name" + HTMLTags.closeTableHeaderHTML +
				HTMLTags.openTableHeaderHTML + "Cell Names" + HTMLTags.closeTableHeaderHTML +
				HTMLTags.openTableHeaderHTML + "Marker" + HTMLTags.closeTableHeaderHTML +
				HTMLTags.openTableHeaderHTML + "Start Time" + HTMLTags.closeTableHeaderHTML +
				HTMLTags.openTableHeaderHTML + "End Time" + HTMLTags.closeTableHeaderHTML +
				HTMLTags.openTableHeaderHTML + "Comments" + HTMLTags.closeTableHeaderHTML +
				HTMLTags.closeTableRowHTML;

		for (SceneElement se : elementsList.elementsList) {
			String sceneElementAsString = HTMLTags.openTableRowHTML +
					HTMLTags.openTableDataHTML + se.getSceneName() + HTMLTags.closeTableDataHTML +
					HTMLTags.openTableDataHTML + se.getAllCellNames().toString() + HTMLTags.closeTableDataHTML +
					HTMLTags.openTableDataHTML + se.getMarkerName() + HTMLTags.closeTableDataHTML +
					HTMLTags.openTableDataHTML + se.getStartTime() + HTMLTags.closeTableDataHTML +
					HTMLTags.openTableDataHTML + se.getEndTime() + HTMLTags.closeTableDataHTML +
					HTMLTags.openTableDataHTML + se.getComments() + HTMLTags.closeTableDataHTML +
					HTMLTags.closeTableRowHTML;
			html += sceneElementAsString;
		}
		
		html += HTMLTags.closeTableTagHTML;
		
		return HTMLGenerator.generateCompleteHTML(html);
	}
}
