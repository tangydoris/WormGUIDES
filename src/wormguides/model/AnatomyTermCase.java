package wormguides.model;

import java.util.ArrayList;

import wormguides.AnatomyTerm;

/**
 * Defines a case i.e. an info window page type corresponding to an AnatomyTerm enum
 * 
 * This class holds in the information about the anatomy term to be used to generate
 * an HTML page for the info window
 * 
 * @author bradenkatzman
 *
 */
public final class AnatomyTermCase {
	
	private String name;
	private String description;
	private ArrayList<String> links;
	
	//for amphid sensilla
	private ArrayList<String> amphidCells;
	private String wormatlasLink1;
	private String wormatlasLink2;
	
	/**
	 * Sets the term based on the term in the enum
	 * 
	 * @param term the defining term
	 */
	public AnatomyTermCase(AnatomyTerm term) {
		if (term.equals(AnatomyTerm.AMPHID_SENSILLA)) {
			initializeAmphidSensillaCase();
		}
	}
	
	/**
	 * Initializes 'Amphid Sensilla' specific information
	 * 	- amphid cells
	 *  = 2 wormatlas links
	 */
	private void initializeAmphidSensillaCase() {
		this.name = AnatomyTerm.AMPHID_SENSILLA.getTerm();
		this.description = AnatomyTerm.AMPHID_SENSILLA.getDescription();
		amphidCells = findAmphidCells();
		this.wormatlasLink1 = amphidWormatlasLink1;
		this.wormatlasLink2 = amphidWormatlasLink2;
		this.links = buildSearchBasedLinks();
	}
	
	/**
	 * Finds cells in the parts list whose descriptions contain 'Amphid'
	 * 
	 * @return the cells with 'Amphid' hits
	 */
	public ArrayList<String> findAmphidCells() {
		ArrayList<String> cells = new ArrayList<String>();
		
		ArrayList<String> functionalNames = PartsList.getFunctionalNames();
		ArrayList<String> lineageNames = PartsList.getLineageNames();
		ArrayList<String> descriptions = PartsList.getDescriptions();
		for (int i = 0; i < descriptions.size(); i++)  {
			if (descriptions.get(i).toLowerCase().contains(AMPHID)) {
				String cell = "";
				
				if (functionalNames.get(i) != null) {
					cell += (functionalNames.get(i) + "*");
				}
				
				if (lineageNames.get(i) != null) {
					cell += (lineageNames.get(i) + "*");
				}
				
				cell += descriptions.get(i);
				
				cells.add(cell);
			}
		}
		
		return cells;
	}
	
	/**
	 * Sets up the wormbase, google search and textpresso links
	 * @return
	 */
	public ArrayList<String> buildSearchBasedLinks() {
		ArrayList<String> searchBasedLinks = new ArrayList<String>();
		
		//add wormbase link
		searchBasedLinks.add("http://www.wormbase.org/species/all/anatomy_term/WBbt:0005391#01--10");
		
		//add google links
		searchBasedLinks.add("https://www.google.com/#q=amphid+sensillia");
		searchBasedLinks.add("https://www.google.com/#q=site:wormatlas.org+amphid+sensillia");
		
		//add textpresso link
		searchBasedLinks.add("http://textpresso-www.cacr.caltech.edu/cgi-bin/celegans/search?searchstring=amphid+sensillia;cat1=Select%20category"
				+ "%201%20from%20list%20above;cat2=Select%20category%202%20from%20list%20above;cat3=Select%20category%203%20from%20list%20above;cat4=Select%20category%204%"
				+ "20from%20list%20above;cat5=Select%20category%205%20from%20list%20above;search=Search!;exactmatch=on;searchsynonyms=on;literature=C.%20elegans;target=abstract;"
				+ "target=body;target=title;target=introduction;target=materials;target=results;target=discussion;target=conclusion;"
				+ "target=acknowledgments;target=references;sentencerange=sentence;sort=score%20(hits);mode=boolean;authorfilter=;journalfilter=;yearfilter=;docidfilter=;");
		
		return searchBasedLinks;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public ArrayList<String> getAmphidCells() {
		if (amphidCells != null) {
			return amphidCells;
		}
		
		return null;
	}
	
	public String getWormatlasLink1() {
		if (wormatlasLink1 != null) {
			return this.wormatlasLink1;
		}
		return null;
	}
	
	public String getWormatlasLink2() {
		if (wormatlasLink2 != null) {
			return this.wormatlasLink2;
		}
		return null;
	}
	
	public ArrayList<String> getLinks() {
		if (this.links != null) {
			return this.links;
		}
		
		return null;
	}

	private final static String AMPHID = "amphid";
	private final static String amphidWormatlasLink1 = "http://www.wormatlas.org/ver1/handbook/hypodermis/Amphidimagegallery.htm";
	private final static String amphidWormatlasLink2 = "http://wormatlas.org/hermaphrodite/neuronalsupport/jump.html?newLink=mainframe.htm&newAnchor=Amphidsensilla31";
}
