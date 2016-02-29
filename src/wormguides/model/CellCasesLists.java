package wormguides.model;

import java.util.ArrayList;

import wormguides.view.InfoWindow;
import wormguides.view.InfoWindowDOM;

/**
 * Class which contains the internal structure for the InfoWindow TabPane
 * 
 * Cells are either terminal cases (neurons) or non-terminal cases
 * --> as these cases are generated for view in InfoWindow, they are stored here
 * 
 * MVC: 
 * 		- Model representation of InfoWindow TabPane
 * 
 * 
 * @author katzmanb
 *
 */
public class CellCasesLists {

	private ArrayList<TerminalCellCase> terminalCases;
	private ArrayList<NonTerminalCellCase> nonTerminalCases;

	private InfoWindow infoWindow;

	public CellCasesLists(InfoWindow window) {
		terminalCases = new ArrayList<TerminalCellCase>();
		nonTerminalCases = new ArrayList<NonTerminalCellCase>();
		infoWindow = window;
	}

	public void setInfoWindow(InfoWindow window) {
		infoWindow = window;
	}

	/**
	 * 
	 * @param lineageName
	 * @param cellName functional name
	 * @param presynapticPartners
	 * @param postsynapticPartners
	 * @param electricalPartners
	 * @param neuromuscularPartners
	 * @param nuclearProductionInfo production information under Nuclear
	 * @param cellShapeProductionInfo production information under Cell Shape
	 */
	public void makeTerminalCase(String lineageName, String cellName, ArrayList<String> presynapticPartners,
			ArrayList<String> postsynapticPartners, ArrayList<String> electricalPartners,
			ArrayList<String> neuromuscularPartners, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {

		TerminalCellCase tCase = new TerminalCellCase(lineageName, cellName, presynapticPartners, postsynapticPartners,
				electricalPartners, neuromuscularPartners, nuclearProductionInfo, cellShapeProductionInfo);

		addTerminalCase(tCase);
	}

	/**
	 * Adds a terminal case to the list of terminal cases, updates the view with the new case
	 * 
	 * @param terminalCase the case to be added
	 */
	private void addTerminalCase(TerminalCellCase terminalCase) {
		if (terminalCases != null) {
			terminalCases.add(terminalCase);

			if (infoWindow != null) {
				// create dom(tab)
				InfoWindowDOM tcDOM = new InfoWindowDOM(terminalCase);
				// add dom(tab) to InfoWindow
				infoWindow.addTab(tcDOM);
			}
		}
	}

	/**
	 * Finds the case corresponding to the given cell
	 * 
	 * @param cellName the cell to search form
	 * @return the case corresponding to the given cell
	 */
	public TerminalCellCase getTerminalCellCase(String cellName) {
		if (!containsTerminalCase(cellName))
			return null;

		for (TerminalCellCase cellCase : terminalCases) {
			if (cellCase.getCellName().equalsIgnoreCase(cellName))
				return cellCase;
		}

		return null;
	}

	/**
	 * Creates a non terminal case for a cell and adds the case to the list
	 * 
	 * @param cellName
	 * @param nuclearProductionInfo
	 * @param cellShapeProductionInfo
	 */
	public void makeNonTerminalCase(String cellName, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {
		NonTerminalCellCase ntCase = new NonTerminalCellCase(cellName, nuclearProductionInfo, cellShapeProductionInfo);

		addNonTerminalCase(ntCase);
	}

	/**
	 * Adds the given non terminal case to the list
	 * 
	 * @param nonTerminalCase the case to be added
	 */
	private void addNonTerminalCase(NonTerminalCellCase nonTerminalCase) {
		if (nonTerminalCases != null) {
			nonTerminalCases.add(nonTerminalCase);

			// create dom(tab)
			InfoWindowDOM ntcDOM = new InfoWindowDOM(nonTerminalCase);

			// add dom(tab) to InfoWindow
			if (infoWindow != null)
				infoWindow.addTab(ntcDOM);
		}
	}

	/**
	 * Searches the list of terminal cases for a case corresponding to the given cell
	 * 
	 * 
	 * @param cellName the cell to be searched for
	 * @return boolean corresponding to if the cell was found
	 */
	public boolean containsTerminalCase(String cellName) {
		if (terminalCases != null) {
			for (TerminalCellCase tCase : terminalCases) {
				if (tCase.getCellName().equals(cellName)
						|| tCase.getCellName().equals(PartsList.getFunctionalNameByLineageName(cellName))) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Searches the list of non terminal cases for a case corresponding to the given cell
	 * 
	 * 
	 * @param cellName the cell to be searched for
	 * @return boolean corresponding to if the cell was found
	 */
	public boolean containsNonTerminalCase(String cellName) {
		if (nonTerminalCases != null) {
			for (NonTerminalCellCase ntCase : nonTerminalCases) {
				if (ntCase.getCellName().equals(cellName)
						|| ntCase.getCellName().equals(PartsList.getLineageNameByFunctionalName(cellName))) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Checks if a cell name has a corresponding case in the terminal cases OR non terminal cases
	 * 
	 * @param cellName
	 * @return boolean corresponding to if the cell case is found
	 */
	public boolean hasCellCase(String cellName) {
		return containsTerminalCase(cellName) || containsNonTerminalCase(cellName);
	}

	/**
	 * Removes the cell case from the internal lists (when the tab is closed)
	 * 
	 * @param cellName the cell to remove
	 */
	public void removeCellCase(String cellName) {
		if (containsTerminalCase(cellName)) {
			for (int i = 0; i < terminalCases.size(); i++) {
				if (terminalCases.get(i).getCellName().toLowerCase().equals(cellName.toLowerCase())) {
					terminalCases.remove(i);
					return;
				}
			}
		}

		if (containsNonTerminalCase(cellName)) {
			for (int i = 0; i < nonTerminalCases.size(); i++) {
				if (nonTerminalCases.get(i).getCellName().toLowerCase().equals(cellName.toLowerCase())) {
					nonTerminalCases.remove(i);
					return;
				}
			}
		}
	}

}
