/*
 * Bao Lab 2016
 */

package wormguides.models;

import java.util.ArrayList;
import java.util.List;

import wormguides.view.InfoWindow;
import wormguides.view.InfoWindowDOM;

import static partslist.PartsList.getLineageNameByFunctionalName;
import static wormguides.models.AnatomyTerm.AMPHID_SENSILLA;

/**
 * List of terminal cell cases (neurons) or non-terminal cell cases. These cases generated for view in the info
 * window are stored here.
 */
public class CasesLists {

    private List<CellCase> cellCases;
    private List<AnatomyTermCase> anatomyTermCases;

    private InfoWindow infoWindow;

    public CasesLists(InfoWindow infoWindow) {
        this.cellCases = new ArrayList<>();
        this.anatomyTermCases = new ArrayList<>();
        this.infoWindow = infoWindow;
    }

    public void setInfoWindow(InfoWindow window) {
        infoWindow = window;
    }

    /**
     * Creates a terminal case for a cell and adds to this class' list of cases.
     *
     * @param lineageName
     *         lineage name of the cell
     * @param cellName
     *         functional name of the cell
     * @param presynapticPartners
     *         list of presynaptic partners
     * @param postsynapticPartners
     *         list of postsynaptic partners
     * @param electricalPartners
     *         list of electrical partners
     * @param neuromuscularPartners
     *         list of neuromuscular partners
     * @param nuclearProductionInfo
     *         production information under Nuclear
     * @param cellShapeProductionInfo
     *         production information under Cell Shape
     */
    public void makeTerminalCase(
            String lineageName,
            String cellName,
            List<String> presynapticPartners,
            List<String> postsynapticPartners,
            List<String> electricalPartners,
            List<String> neuromuscularPartners,
            List<String> nuclearProductionInfo,
            List<String> cellShapeProductionInfo) {

        addTerminalCase(new TerminalCellCase(
                lineageName,
                cellName,
                presynapticPartners,
                postsynapticPartners,
                electricalPartners,
                neuromuscularPartners,
                nuclearProductionInfo,
                cellShapeProductionInfo));
    }

    /**
     * Adds a terminal case to the list of terminal cases, updates the view with the new case
     *
     * @param terminalCase
     *         the case to be added
     */
    private void addTerminalCase(TerminalCellCase terminalCase) {
        cellCases.add(terminalCase);
        if (infoWindow != null) {
            // add dom(tab) to InfoWindow
            infoWindow.addTab(new InfoWindowDOM(terminalCase));
        }
    }

    /**
     * Creates a non terminal case for a cell and adds the case to the list
     *
     * @param cellName
     *         name of the cell
     * @param nuclearProductionInfo
     *         the production information under Nuclear
     * @param cellShapeProductionInfo
     *         the production information under Cell Shape
     */
    public void makeNonTerminalCase(
            String cellName,
            List<String> nuclearProductionInfo,
            List<String> cellShapeProductionInfo) {
        addNonTerminalCase(new NonTerminalCellCase(cellName, nuclearProductionInfo, cellShapeProductionInfo));
    }

    /**
     * Adds the given non terminal case to the list
     *
     * @param nonTerminalCase
     *         the case to be added
     */
    private void addNonTerminalCase(NonTerminalCellCase nonTerminalCase) {
        cellCases.add(nonTerminalCase);
        // add dom(tab) to InfoWindow
        if (infoWindow != null) {
            infoWindow.addTab(new InfoWindowDOM(nonTerminalCase));
        }
    }

    public void makeAnatomyTermCase(AnatomyTerm term) {
        if (term.equals(AMPHID_SENSILLA)) {
            AmphidSensillaTerm amphidSensillaCase = new AmphidSensillaTerm(term);
            addAmphidSensillaTermCase(amphidSensillaCase);
        }
    }

    private void addAmphidSensillaTermCase(AmphidSensillaTerm amphidSensillaTermCase) {
        if (amphidSensillaTermCase != null) {
            anatomyTermCases.add(amphidSensillaTermCase);

            //create dom
            InfoWindowDOM termCaseDOM = new InfoWindowDOM(amphidSensillaTermCase);

            //add dom to InfoWindow
            if (infoWindow != null) {
                infoWindow.addTab(termCaseDOM);
            }

        }
    }

    public CellCase getCellCase(String cellName) {
        String cell = cellName;

        //translate name to lineage if passed as function
        String lineage = getLineageNameByFunctionalName(cellName);
        if (lineage != null) {
            cell = lineage;
        }

        for (CellCase cellCase : cellCases) {
            if (cellCase.getLineageName().toLowerCase().equals(cell.toLowerCase())) {
                return cellCase;
            }
        }

        return null;
    }

    public boolean containsCellCase(String cellName) {
        String cell = cellName;

        //translate name to lineage if passed as function
        String lineage = getLineageNameByFunctionalName(cellName);
        if (lineage != null) {
            cell = lineage;
        }

        for (CellCase cellCase : cellCases) {
            if (cellCase.getLineageName().toLowerCase().equals(cell.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public boolean containsAnatomyTermCase(String term) {
        if (anatomyTermCases != null) {
            for (AnatomyTermCase atc : anatomyTermCases) {
                if (atc.getName().toLowerCase().equals(term.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a cell name has a corresponding case in the terminal cases OR non terminal cases
     *
     * @param cellName
     *         name of the cell
     *
     * @return true if a cell case was found for the cell, false otherwise
     */
    public boolean hasCellCase(String cellName) { //TODO refactor this to just be name
        return containsCellCase(cellName) || containsAnatomyTermCase(cellName);
    }

    /**
     * Removes the cell case from the internal lists (when the tab is closed)
     *
     * @param cellName
     *         the cell to remove
     */
    public void removeCellCase(String cellName) {
        String cell = cellName;

        //translate name to lineage if passed as function
        String lineage = getLineageNameByFunctionalName(cellName);
        if (lineage != null) {
            cell = lineage;
        }

        if (containsCellCase(cell)) {
            for (CellCase cellCase : cellCases) {
                if (cellCase.getLineageName().toLowerCase().equals(cell.toLowerCase())) {
                    cellCases.remove(cellCase);
                }
            }
        }

        if (containsAnatomyTermCase(cellName)) {
            for (int i = 0; i < anatomyTermCases.size(); i++) {
                if (anatomyTermCases.get(i).getName().toLowerCase().equals(cellName.toLowerCase())) {
                    anatomyTermCases.remove(i);
                    return;
                }
            }
        }
    }
}
