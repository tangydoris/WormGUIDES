package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.property.IntegerProperty;
import javafx.stage.Stage;
import wormguides.Search;
import wormguides.model.PartsList;

/*
 * Callback class for our HTML page for a terminal cell case to open link in default browser
 * 
 * Because our anchors <a> cannot pass vars, we'll keep the links in
 * memory and have the anchor call the appropriate callback method
 */


/* TODO
 * remove links --> pass link as string to openLink
 */
public class InfoWindowLinkController {
	Stage window3DStage; //update scenes on links
	IntegerProperty time;
	
	public InfoWindowLinkController(Stage window3DStage, IntegerProperty time) {
		this.window3DStage = window3DStage;
		this.time = time;
	}
	
	public void handleLink(String url) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(new URI(url));
		}
	}
	
	public void handleWiringPartnerClick(String cellName) {
		if (!Search.hasCellCase(cellName)) {
			//generate a new cell case
			Search.addToInfoWindow(cellName);
		} else {
			/*
			 * TODO
			 * focus the tab if it already exists
			 */
		}
	}
	
	public void viewInCellTheater(String cellName) {
		int startTime;
		int endTime;
		
		
		if (PartsList.getLineageNameByFunctionalName(cellName) != null) {
			startTime = Search.getFirstOccurenceOf(PartsList.getLineageNameByFunctionalName(cellName));
			endTime = Search.getLastOccurenceOf(PartsList.getLineageNameByFunctionalName(cellName));
		} else {
			startTime = Search.getFirstOccurenceOf(cellName);
			endTime = Search.getLastOccurenceOf(cellName);
		}
		
		if(time.get() >= startTime && time.get() <= endTime) {
			//highlight the clicked cell
			//window3DController.insertLabelFor(cellName);
		} else {
			time.set(startTime);
			//window3DController.insertLabelFor(cellName);
			//highlight clicked cell
		}
		
		window3DStage.requestFocus();
	}
}