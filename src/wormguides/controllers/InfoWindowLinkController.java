package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

/*
 * Callback class for our HTML page for a terminal cell case to open links in default browser
 * 
 */
public class InfoWindowLinkController {
	
	private Stage parentStage; //update scenes on links
	private StringProperty labeledCellProperty;
	
	public InfoWindowLinkController(Stage stage, StringProperty cellNameProperty) {
		parentStage = stage;
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
		resetLabeledCellProperty(cellName);
		
		parentStage.requestFocus();
	}
	
	private void resetLabeledCellProperty(String cellName) {
		labeledCellProperty.set("");
		labeledCellProperty.set(cellName);
	}
}