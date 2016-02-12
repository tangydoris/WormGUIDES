package wormguides.model;

import java.util.ArrayList;

import wormguides.view.InfoWindow;
import wormguides.view.InfoWindowDOM;

/*  
 * Class which contains the internal structure for the InfoWindow TabPane
 * 
 * Cells are either terminal cases (neurons) or non-terminal cases
 * --> as these cases are generated for view in InfoWindow, they are stored here
 * 
 * MVC: 
 * 		- Model representation of InfoWindow TabPane
 * 
 */
public class CellCases {
	private ArrayList<TerminalCellCase> terminalCases;
	private ArrayList<NonTerminalCellCase> nonTerminalCases;
	
	private InfoWindow infoWindow;
	
	public CellCases(InfoWindow infoWindow) {
		terminalCases = new ArrayList<TerminalCellCase>();
		nonTerminalCases = new ArrayList<NonTerminalCellCase>();
		this.infoWindow = infoWindow;
	}
	
	public void makeTerminalCase(String cellName, 
			ArrayList<String> presynapticPartners, ArrayList<String> postsynapticPartners,
			ArrayList<String> electricalPartners, ArrayList<String> neuromuscularPartners, 
			ArrayList<String> nuclearProductionInfo, ArrayList<String> cellShapeProductionInfo) {
		
		TerminalCellCase tCase = new TerminalCellCase(cellName, presynapticPartners, postsynapticPartners,
				electricalPartners, neuromuscularPartners, nuclearProductionInfo, cellShapeProductionInfo);
		
		addTerminalCase(tCase);
	}
	
	private void addTerminalCase(TerminalCellCase terminalCase) {
		if (terminalCases != null) {
			terminalCases.add(terminalCase);
			
			//create dom(tab)
			InfoWindowDOM tcDOM = new InfoWindowDOM(terminalCase);
			
			//add dom(tab) to InfoWindow
			infoWindow.addTab(tcDOM, terminalCase.getLinks());
		}
	}
	
	public void makeNonTerminalCase(String cellName, 
			ArrayList<String> nuclearProductionInfo, ArrayList<String> cellShapeProductionInfo) {
		NonTerminalCellCase ntCase = new NonTerminalCellCase(cellName, nuclearProductionInfo, cellShapeProductionInfo);
		
		addNonTerminalCase(ntCase);
	}
	
	private void addNonTerminalCase(NonTerminalCellCase nonTerminalCase) {
		if (nonTerminalCases != null) {
			nonTerminalCases.add(nonTerminalCase);
			
			//create dom(tab)
			InfoWindowDOM ntcDOM = new InfoWindowDOM(nonTerminalCase);
			
			//add dom(tab) to InfoWindow
			infoWindow.addTab(ntcDOM, nonTerminalCase.getLinks());
		}
	}
	
	public boolean containsTerminalCase(String cellName) {
		if (terminalCases != null) {
			for (TerminalCellCase tCase : terminalCases) {
				if (tCase.getCellName().equals(cellName) || 
						tCase.getCellName().equals(PartsList.getFunctionalNameByLineageName(cellName))) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public boolean containsNonTerminalCase(String cellName) {
		if (nonTerminalCases != null) {
			for (NonTerminalCellCase ntCase : nonTerminalCases) {
				if (ntCase.getCellName().equals(cellName) || 
						ntCase.getCellName().equals(PartsList.getLineageNameByFunctionalName(cellName))) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public boolean hasCellCase(String cellName) {
		return containsTerminalCase(cellName) || containsNonTerminalCase(cellName);
	}
	
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
