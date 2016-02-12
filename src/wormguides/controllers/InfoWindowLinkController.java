package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
	Window3DController window3DController;
	
	public InfoWindowLinkController(Window3DController window3DController) {
		this.window3DController = window3DController;
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
		
		if(window3DController.getTime() >= startTime && window3DController.getTime() <= endTime) {
			//highlight the clicked cell
			System.out.println(startTime + " - " + endTime);
			window3DController.insertLabelFor(cellName);
		} else {
			System.out.println(startTime + " - " + endTime);
			window3DController.setTime(startTime);
			window3DController.insertLabelFor(cellName);
			//highlight clicked cell
		}
		
		window3DController.getStage().requestFocus();
	}
}