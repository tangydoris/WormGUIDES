package wormguides.model;

import java.util.ArrayList;
import wormguides.Search;

public class NonTerminalCellCase {

	private String cellName;
	private String embryonicHomology;
	private ArrayList<TerminalDescendant> terminalDescendants;
	private ArrayList<String> links;
	private ArrayList<String> geneExpression;
	ArrayList<String> nuclearProductionInfo;
	ArrayList<String> cellShapeProductionInfo;

	public NonTerminalCellCase(String cellName, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {
		this.cellName = cellName; // use this for identifier and external
									// information

		// reference embryonic analogues cells db for homology
		this.embryonicHomology = EmbryonicAnalogousCells.findEmbryonicHomology(this.cellName);

		this.terminalDescendants = buildTerminalDescendants();

		this.links = buildLinks();

		this.nuclearProductionInfo = nuclearProductionInfo;
		this.cellShapeProductionInfo = cellShapeProductionInfo;
	}

	public String getLineageName() {
		return cellName;
	}

	private ArrayList<TerminalDescendant> buildTerminalDescendants() {
		ArrayList<TerminalDescendant> terminalDescendants = new ArrayList<TerminalDescendant>();

		ArrayList<String> descendantsList = Search.getDescendantsList(this.cellName);

		// add each descendant as terminal descendant object
		for (String descendant : descendantsList) {
			String partsListDescription = PartsList.getDescriptionByLineageName(descendant);
			if (partsListDescription == null) {
				if (CellDeaths.containsCell(descendant)) {
					partsListDescription = "Cell Death";
				} else {
					partsListDescription = "";
				}
			}
			terminalDescendants.add(new TerminalDescendant(descendant, partsListDescription));
		}

		return terminalDescendants;
	}

	private ArrayList<String> buildLinks() {
		ArrayList<String> links = new ArrayList<String>();

		links.add(buildWORMBASELink());
		links.add(addGoogleLink());
		links.add(addGoogleWormatlasLink());

		return links;
	}

	private String buildWORMBASELink() {
		if (this.cellName == null)
			return "";

		String URL = wormbaseURL + this.cellName + wormbaseURLEXT;

		try {
			// URLConnection connection = new URL(URL).openConnection();
		} catch (Exception e) {
			// e.printStackTrace();
			// a page wasn't found on wormatlas
			System.out.println(this.cellName + " page not found on Wormbase");
		}

		return URL;
	}

	private String addGoogleLink() {
		if (this.cellName != null) {
			return googleURL + this.cellName + "+c.+elegans";
		}

		return "";
	}

	private String addGoogleWormatlasLink() {
		if (this.cellName != null) {
			return googleWormatlasURL + this.cellName;
		}

		return "";
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

	public ArrayList<String> getNuclearProductionInfo() {
		return this.nuclearProductionInfo;
	}

	public ArrayList<String> getCellShapeProductionInfo() {
		return this.cellShapeProductionInfo;
	}

	private final static String wormbaseURL = "http://www.wormbase.org/db/get?name=";
	private final static String wormbaseURLEXT = ";class=Anatomy_term";
	private final static String googleURL = "https://www.google.com/#q=";
	private final static String googleWormatlasURL = "https://www.google.com/#q=site:wormatlas.org+";
}
