package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import wormguides.AnatomyTerm;
import wormguides.Search;

/*
 * Callback class for our HTML page for a terminal cell case to open links in default browser
 * 
 */
public class InfoWindowLinkController {

	private Stage parentStage; // update scenes on links
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
	
	/**
	 * Callback controller for wiring partner. When a wiring partner is clicked, 
	 * it is shown in 3D and an info window page is generated for the cell
	 * 
	 * @param cellName the name of the clicked wiring partner
	 */
	public void handleWiringPartnerClick(String cellName) {
		//view in 3D
		viewInCellTheater(cellName);

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
	
	/**
	 * Call back controller for keyword "amphid" click
	 * Generates the "Amphid Sensilla" default info window page for now
	 */
	public void handleAmphidClick() {
		Search.addToInfoWindow(AnatomyTerm.AMPHID_SENSILLA);
	}

	public void viewInCellTheater(String cellName) {
		resetLabeledCellProperty(cellName);

		parentStage.requestFocus();
	}

	private void resetLabeledCellProperty(String cellName) {
		labeledCellProperty.set("");
		labeledCellProperty.set(cellName);
	}
	
	private final static String amphidSensilla = "amphid sensilla";
}