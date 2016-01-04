package wormguides;

import wormguides.model.SceneElement;
import wormguides.model.SceneElementsList;

public class CellShapesIndexToHTML extends HTMLGenerator {
	
	SceneElementsList elementsList;
	
	public CellShapesIndexToHTML(SceneElementsList elementsList) {
		super();
		this.elementsList = elementsList;
	}
	
	public String buildCellShapesIndexAsHTML() {
		String html = openTableTagHTML + openTableRowHTML + 
				openTableHeaderHTML + "Scene Name" + closeTableHeaderHTML +
				openTableHeaderHTML + "Cell Names" + closeTableHeaderHTML +
				openTableHeaderHTML + "Marker" + closeTableHeaderHTML +
				openTableHeaderHTML + "Start Time" + closeTableHeaderHTML +
				openTableHeaderHTML + "End Time" + closeTableHeaderHTML +
				openTableHeaderHTML + "Comments" + closeTableHeaderHTML +
				closeTableRowHTML;

		for (SceneElement se : elementsList.elementsList) {
			String sceneElementAsString = openTableRowHTML +
					openTableDataHTML + se.getSceneName() + closeTableDataHTML +
					openTableDataHTML + se.getAllCellNames().toString() + closeTableDataHTML +
					openTableDataHTML + se.getMarkerName() + closeTableDataHTML +
					openTableDataHTML + se.getStartTime() + closeTableDataHTML +
					openTableDataHTML + se.getEndTime() + closeTableDataHTML +
					openTableDataHTML + se.getComments() + closeTableDataHTML +
					closeTableRowHTML;
			html += sceneElementAsString;
		}
		
		html += closeTableTagHTML;
		return generateCompleteHTML(html);
	}
}
