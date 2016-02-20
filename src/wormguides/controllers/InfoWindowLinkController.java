package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import wormguides.Search;
import wormguides.model.PartsList;

/*
 * Callback class for our HTML page for a terminal cell case to open links in default browser
 * 
 */
public class InfoWindowLinkController {
	
	private Stage parentStage; //update scenes on links
	private IntegerProperty time;
	private StringProperty labeledCellProperty;
	
	public InfoWindowLinkController(Stage stage, IntegerProperty timeProperty, StringProperty cellNameProperty) {
		parentStage = stage;
		time = timeProperty;
		labeledCellProperty = cellNameProperty;
	}
	
	public void handleLink(String url) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(new URI(url));
		}
	}
	
	public void handleWiringPartnerClick(String cellName) {
		//until page generation is faster, just view wiring partner in 3D
		viewInCellTheater(cellName);
		
//		if (!Search.hasCellCase(cellName)) {
//			//generate a new cell case
//			Search.addToInfoWindow(cellName);
//		} else {
//			/*
//			 * TODO
//			 * focus the tab if it already exists
//			 */
//		}
	}
	
	public void viewInCellTheater(String cellName) {
		int startTime;
		int endTime;
		
		String linName = PartsList.getLineageNameByFunctionalName(cellName);
		if (linName != null)
			cellName = linName;

		startTime = Search.getFirstOccurenceOf(cellName);
		endTime = Search.getLastOccurenceOf(cellName);
		
		if (startTime<=0)
			startTime = 1;
		if (endTime<=0)
			endTime = 1;
		
		if(time.get()<startTime || time.get()>endTime)
			time.set(startTime);
		
		resetLabeledCellProperty(cellName);
		
		parentStage.requestFocus();
	}
	
	private void resetLabeledCellProperty(String cellName) {
		labeledCellProperty.set("");
		labeledCellProperty.set(cellName);
	}
}