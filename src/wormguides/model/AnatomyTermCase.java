package wormguides.model;

import java.util.ArrayList;

import wormguides.AnatomyTerm;

public final class AnatomyTermCase {
	
	private String name;
	private String description;
	private ArrayList<String> links;
	
	//for amphid sensilla
	private ArrayList<String> amphidCells;
	private String wormatlasLink1;
	private String wormatlasLink2;
	
	public AnatomyTermCase(AnatomyTerm term) {
		if (term.equals(AnatomyTerm.AMPHID_SENSILLA)) {
			initializeAmphidSensillaCase();
		}
	}
	
	private void initializeAmphidSensillaCase() {
		this.name = AnatomyTerm.AMPHID_SENSILLA.getTerm();
		this.description = AnatomyTerm.AMPHID_SENSILLA.getDescription();
		amphidCells = findAmphidCells();
		this.wormatlasLink1 = amphidWormatlasLink1;
		this.wormatlasLink2 = amphidWormatlasLink2;
		this.links = buildSearchBasedLinks();
	}
	
	public ArrayList<String> findAmphidCells() {
		ArrayList<String> cells = new ArrayList<String>();
		
		for (String name : PartsList.getFunctionalNames()) {
			System.out.println(name);
			if (name.toLowerCase().contains(AMPHID)) {
				System.out.println("GOT ONE: " + name);
				cells.add(name);
			}
		}
		
		return cells;
	}
	
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

	private final static String AMPHID = "amphid";
	private final static String amphidWormatlasLink1 = "http://www.wormatlas.org/ver1/handbook/hypodermis/Amphidimagegallery.htm";
	private final static String amphidWormatlasLink2 = "http://wormatlas.org/hermaphrodite/neuronalsupport/jump.html?newLink=mainframe.htm&newAnchor=Amphidsensilla31";
}
