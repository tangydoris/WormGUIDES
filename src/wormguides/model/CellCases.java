package wormguides.model;

import java.util.ArrayList;

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
	
	public CellCases() {
		terminalCases = new ArrayList<TerminalCellCase>();
		nonTerminalCases = new ArrayList<NonTerminalCellCase>();
	}
	
	public void makeTerminalCase(String cellName) {
		TerminalCellCase tCase = new TerminalCellCase(cellName);
		
		addTerminalCase(tCase);
	}
	
	private void addTerminalCase(TerminalCellCase terminalCase) {
		if (terminalCases != null) {
			terminalCases.add(terminalCase);
			//update tabpane
		}
	}
	
	public void makeNonTerminalCase(String cellName) {
		NonTerminalCellCase ntCase = new NonTerminalCellCase(cellName);
		
		addNonTerminalCase(ntCase);
	}
	
	private void addNonTerminalCase(NonTerminalCellCase nonTerminalCase) {
		if (nonTerminalCases != null) {
			nonTerminalCases.add(nonTerminalCase);
			
			//update tabpane
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
