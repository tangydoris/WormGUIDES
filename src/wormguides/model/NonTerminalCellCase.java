package wormguides.model;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import wormguides.Search;

public class NonTerminalCellCase {
	private String cellName;
	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;
	private ArrayList<String> links;
	
	public NonTerminalCellCase(String cellName) {
		this.cellName = cellName; //use this for identifier and external information
		
		//reference embryonic analogues cells db for homology
		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(this.cellName);
		
		this.terminalDescendants = buildTerminalDescendants();
		
		this.links = buildLinks();
	}
	
	private ArrayList<TerminalDescendant> buildTerminalDescendants() {
		ArrayList<TerminalDescendant> terminalDescendants = new ArrayList<TerminalDescendant>();

		ArrayList<String> descendantsList = Search.getDescendantsList(this.cellName);
		
		//add each descendant as terminal descendant object
		for (String descendant : descendantsList) {
			terminalDescendants.add
						(new TerminalDescendant(descendant, PartsList.getDescriptionByLineageName(descendant)));
		}
		
		return terminalDescendants;
	}
	
	private ArrayList<String> buildLinks() {
		ArrayList<String> links = new ArrayList<String>();
		
		links.add(buildWORMBASELink());
		
		return links;
	}
	
	private String buildWORMBASELink() {
		if (this.cellName == null) return "";
		
		String URL = "http://www.wormbase.org/db/get?name=" + 
		this.cellName + ";class=Anatomy_term";
		
		try {
			URLConnection connection = new URL(URL).openConnection();			
		} catch (Exception e) {
			//e.printStackTrace();
			//a page wasn't found on wormatlas
			System.out.println(this.cellName + " page not found on Wormbase");
		}
		
		return URL;
	}
	
	public String getCellName() {
		return this.cellName;
	}
	
	public String getEmbryonicHomology() {
		return this.embryonicHomology;
	}
	
	public ArrayList<TerminalDescendant> getTerminalDescendants() {
		return this.terminalDescendants;
	}
	
	public ArrayList<String> getLinks() {
		return this.links;
	}
}
