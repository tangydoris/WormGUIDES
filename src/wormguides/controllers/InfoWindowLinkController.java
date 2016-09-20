/*
 * Bao Lab 2016
 */

package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import wormguides.AnatomyTerm;
import wormguides.layers.SearchLayer;

import partslist.PartsList;

/**
 * Callback class for HTML pages HTML pages generated for Info Window contain
 * links which when clicked fire a JS function that allows us to call back to
 * our java code
 * <p>
 * This class implements functionality for targeting the user's default broswer
 * for linked websites, handles the clicking of a wiring partner to both
 * generate a new cell case page and view the partner in 3D, and controls the
 * generation of AnatomyTerm pages in the info window
 *
 * @author bradenkatzman
 */
public class InfoWindowLinkController {

    private Stage parentStage; // update scenes on links
    private StringProperty labeledCellProperty;

    public InfoWindowLinkController(Stage stage, StringProperty cellNameProperty) {
        parentStage = stage;
        labeledCellProperty = cellNameProperty;
    }

    /**
     * Targets the default browser and opens the supplied link.
     *
     * @param url
     *         the url of the page to be opened
     *
     * @throws IOException
     *         when the desktop cannot be referenced
     * @throws URISyntaxException
     *         when a URI cannot be created from the input url
     */
    public void handleLink(String url) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }

    /**
     * Callback controller for wiring partner. When a wiring partner is clicked,
     * it is shown in 3D and an info window page is generated for the cell
     *
     * @param cellName
     *         the name of the clicked wiring partner
     */
    public void handleWiringPartnerClick(String cellName) {
        // translate to lineage name
        String cell = cellName;

        String lineageName = PartsList.getLineageNameByFunctionalName(cellName);
        if (lineageName != null) {
            cell = lineageName;
        }

        // handle the case of " " to "_" discrepancy --> change all spaces to
        // underscore
        if (cell.contains(" ")) {
            for (int i = 0; i < cell.length(); i++) {
                if (cell.charAt(i) == ' ') {
                    cell = cell.substring(0, i) + "_" + cell.substring(i + 1);
                }
            }
        }

        // view in 3D
        viewInCellTheater(cell);

        if (!SearchLayer.hasCellCase(cell)) {
            // generate a new cell case
            SearchLayer.addToInfoWindow(cell);
        } else {
            /*
             * TODO focus the tab if it already exists
			 */
        }
    }

    /**
     * Call back controller for keyword "amphid" click Generates the
     * "Amphid Sensilla" default info window page for now
     */
    public void handleAmphidClick() {
        SearchLayer.addToInfoWindow(AnatomyTerm.AMPHID_SENSILLA);
    }

    /**
     * This method shows a clicked cell in the scene graph by taking advantage of the string property
     * labeledCellProperty from Window3D. When this property is changed (i.e. a new cell name is set), a listener
     * fires which navigates to the birth of the cell in the embryo
     *
     * @param cellName
     *         cell to view in threather.
     */
    public void viewInCellTheater(String cellName) {
        resetLabeledCellProperty(cellName);
        parentStage.requestFocus();
    }

    /**
     * Changes the StringProperty labeledCellProperty to navigate to the cell in
     * 3D
     *
     * @param cellName
     *         the cell to navigate to
     */
    private void resetLabeledCellProperty(String cellName) {
        labeledCellProperty.set("");
        labeledCellProperty.set(cellName);
    }
}