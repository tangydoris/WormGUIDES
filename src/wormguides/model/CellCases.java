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
	
	public void makeTerminalCase(String cellName, ArrayList<String> presynapticPartners, ArrayList<String> postsynapticPartners,
			ArrayList<String> electricalPartners, ArrayList<String> neuromuscularPartners) {
		
		TerminalCellCase tCase = new TerminalCellCase(cellName, presynapticPartners, postsynapticPartners,
				electricalPartners, neuromuscularPartners);
		
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
	
	public void makeNonTerminalCase(String cellName) {
		NonTerminalCellCase ntCase = new NonTerminalCellCase(cellName);
		
		addNonTerminalCase(ntCase);
	}
	
	private void addNonTerminalCase(NonTerminalCellCase nonTerminalCase) {
		if (nonTerminalCases != null) {
			nonTerminalCases.add(nonTerminalCase);
			
			//create dom(tab)
			InfoWindowDOM ntcDOM = new InfoWindowDOM(nonTerminalCase);
			
			//add dom(tab) to InfoWindow
			infoWindow.addTab(ntcDOM);
		}
	}
	
	public boolean containsTerminalCase(String cellName) {
		if (terminalCases != null) {
			for (TerminalCellCase tCase : terminalCases) {
				if (tCase.getCellName().equals(cellName)) {
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
				if (ntCase.getCellName().equals(cellName)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
}
