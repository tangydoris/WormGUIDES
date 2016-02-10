package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import wormguides.Search;

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
}