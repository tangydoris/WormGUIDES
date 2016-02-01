package wormguides.view;

import java.util.ArrayList;
import java.util.Collections;

import wormguides.model.NonTerminalCellCase;
import wormguides.model.PartsList;
import wormguides.model.TerminalCellCase;
import wormguides.model.TerminalDescendant;

public class InfoWindowDOM {
	private HTMLNode html;
	private String name;

	//other dom uses: connectome, parts list, cell shapes index
	
	/*
	 * TODO
	 * getNode(String ID)
	 * removeNode(String ID)
	 * addChildToNode(String parentID, HTMLNode child) -- need this?
	 * add title tag to head
	 * 
	 */
	public InfoWindowDOM() {
		this.html = new HTMLNode("html");
		this.name = "CELL TITLE";
	}
	
	//pass the cell name as a string which will be the name at the top of the tab
	public InfoWindowDOM(HTMLNode html) {
		if (!html.getTag().equals("html")) {
			this.html = new HTMLNode("html");
		} else {
			this.html = html;
		}

		this.name = "CELL TITLE";
	}
	
	/*
	 * TERMINAL CELL CASE
	 */
	public InfoWindowDOM(TerminalCellCase terminalCase) {
		this.html = new HTMLNode("html");
		this.name = terminalCase.getCellName();
	
		HTMLNode head = new HTMLNode("head");
		
		HTMLNode body = new HTMLNode("body");
		
		
		
		
		//external info
		HTMLNode cellNameDiv = new HTMLNode("div", "cellName", "");
		String cellName = "<strong>" + terminalCase.getExternalInfo() + "</strong>";
		HTMLNode cellNameP = new HTMLNode("p", "", "", cellName);
		cellNameDiv.addChild(cellNameP);
		
		//parts list descriptions
		HTMLNode partsListDescrDiv = new HTMLNode("div", "partsListDescr", "");
		String partsListDescription = terminalCase.getPartsListDescription();
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		partsListDescrDiv.addChild(partsListDescrP);
		
		
		//image
		HTMLNode imgDiv = new HTMLNode("div", "imgDiv", "width: 50%; height: 10%; float: left;");
		String imagetext=terminalCase.getImageURL();
		HTMLNode img = new HTMLNode(terminalCase.getImageURL(), true);
		imgDiv.addChild(img);
	
		
		//wormatlas function
		HTMLNode functionWORMATLASTopContainerDiv = new HTMLNode("div", "functionTopContainer", "");
		HTMLNode collapseFunctionButton = new HTMLNode("button", "functionWORMATLASCollapse", "functionCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode functionWORMATLASTitle = new HTMLNode("p", "functionWORMATLASTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> Wormatlas Function: </strong>");
		functionWORMATLASTopContainerDiv.addChild(collapseFunctionButton);
		functionWORMATLASTopContainerDiv.addChild(functionWORMATLASTitle);
		HTMLNode functionWORMATLASDiv = new HTMLNode("div", "functionWORMATLAS", "height: 0px; visibility: hidden;");
		boolean functionFound = false;
		String functionWORMATLAS = terminalCase.getFunctionWORMATLAS();
		if (!functionWORMATLAS.equals("")) {
			functionFound = true;
			HTMLNode functionWORMATLASP = new HTMLNode("p", "", "", terminalCase.getFunctionWORMATLAS());
			functionWORMATLASDiv.addChild(functionWORMATLASP);
		}
		
		//anatomy
		HTMLNode anatomyTopContainerDiv = new HTMLNode("div", "anatomyTopContainer", "");
		HTMLNode collapseAnatomyButton = new HTMLNode("button", "anatomyCollapse", "anatomyCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode anatomyTitle = new HTMLNode("p", "anatomyTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> Anatomy: </strong>");
		anatomyTopContainerDiv.addChild(collapseAnatomyButton);
		anatomyTopContainerDiv.addChild(anatomyTitle);
		HTMLNode anatomyDiv = new HTMLNode("div", "anatomy", "height: 0px; visibility: hidden;");
		HTMLNode anatomyUL = new HTMLNode("ul");
		for (String anatomyEntry : terminalCase.getAnatomy()) {
			HTMLNode li = new HTMLNode("li", "", "", anatomyEntry);
			anatomyUL.addChild(li);
		}
		anatomyDiv.addChild(anatomyUL);
		
		//wiring
		HTMLNode wiringPartnersTopContainerDiv = new HTMLNode("div", "wiringPartnersTopContainer", "");
		HTMLNode collapseWiringPartnersButton = new HTMLNode("button", "wiringPartnersCollapse", "wiringPartnersCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode wiringPartnersTitle = new HTMLNode("p", "wiringPartnersTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> Wiring Partners: </strong>");
		wiringPartnersTopContainerDiv.addChild(collapseWiringPartnersButton);
		wiringPartnersTopContainerDiv.addChild(wiringPartnersTitle);
		HTMLNode wiringPartnersDiv = new HTMLNode("div", "wiringPartners", "height: 0px; visibility: hidden;");
		//view in wiring diagram
//		HTMLNode viewWDTopContainerDiv = new HTMLNode("div", "viewWDTopContainer", "");
//		HTMLNode collapseViewWDButton = new HTMLNode("button", "viewWDCollapse", "viewWDCollapseButton",  "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "-", true);
//		HTMLNode viewWDTitle = new HTMLNode("p", "viewWDTitle", "width: 95%; float: left;",
//				"<strong> View in Wiring Diagram Network: </strong>");
//		viewWDTopContainerDiv.addChild(collapseViewWDButton);
//		viewWDTopContainerDiv.addChild(viewWDTitle);
		HTMLNode viewWDDiv = new HTMLNode("div", "viewWD", "");
		HTMLNode viewWDP = new HTMLNode("p", "viewWDTitle", "", "<em> View in Wiring Diagram Network: </em>");
		HTMLNode viewWDImg = new HTMLNode("http://www.wormatlas.org/images/connectome.jpg", true);
		viewWDDiv.addChild(viewWDP);
		viewWDDiv.addChild(viewWDImg);
		//wiring partners UL
		HTMLNode wiringPartnersUL = new HTMLNode("ul");
		ArrayList<String> presynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> postsynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> electricalPartners = terminalCase.getElectricalPartners();
		ArrayList<String> neuromuscularPartners = terminalCase.getNeuromuscularPartners();
		if (presynapticPartners.size() > 0) {
			Collections.sort(presynapticPartners);
			
			//remove brackets
			String prePartners = presynapticPartners.toString();
			prePartners = prePartners.substring(1, prePartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Presynaptic to: </em><br>" + prePartners);
			wiringPartnersUL.addChild(li);
		}
		if (postsynapticPartners.size() > 0) {
			Collections.sort(postsynapticPartners);
			String postPartners = postsynapticPartners.toString();
			
			postPartners = postPartners.substring(1, postPartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Postsynaptic to: </em><br>" + postPartners);
			wiringPartnersUL.addChild(li);
		}
		if (electricalPartners.size() > 0) {
			Collections.sort(electricalPartners);
			
			String electPartners = electricalPartners.toString();
			electPartners = electPartners.substring(1, electPartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Electrical to: </em><br>" + electPartners);
			wiringPartnersUL.addChild(li);
		}
		if (neuromuscularPartners.size() > 0) {
			Collections.sort(neuromuscularPartners);
			
			String neuroPartners = neuromuscularPartners.toString();
			neuroPartners = neuroPartners.substring(1, neuroPartners.length()-2);
			
			HTMLNode li = new HTMLNode("li", "", "", "<em>Neuromusclar to: </em><br>" + neuroPartners);
			wiringPartnersUL.addChild(li);
		}
		
		boolean isneuronpage=(presynapticPartners.size() > 0||electricalPartners.size()>0||neuromuscularPartners.size() > 0||postsynapticPartners.size() > 0);
		// only add this section if it's a neuron (i.e. it appears in wiring diagram) -AS
		if(isneuronpage){
			wiringPartnersDiv.addChild(wiringPartnersUL);
			wiringPartnersDiv.addChild(viewWDDiv); //reversed order of these elements -AS
		}
		
		//expresses
		HTMLNode geneExpressionTopContainerDiv = new HTMLNode("div", "expressesTopContainer", "");
		HTMLNode collapseGeneExpressionButton = new HTMLNode("button", "geneExpressionCollapse", "geneExpressionCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode geneExpressionTitle = new HTMLNode("p", "geneExpressionTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> Gene Expression: </strong>");
		geneExpressionTopContainerDiv.addChild(collapseGeneExpressionButton);
		geneExpressionTopContainerDiv.addChild(geneExpressionTitle);
		HTMLNode geneExpressionDiv = new HTMLNode("div", "geneExpression", "height: 0px; visibility: hidden;");
		ArrayList<String> expresses = terminalCase.getExpressesWORMBASE();
		Collections.sort(expresses);
		String geneExpressionStr = expresses.toString();
		geneExpressionStr = geneExpressionStr.substring(1, geneExpressionStr.length()-1); //remove surrounding brackets
		HTMLNode geneExpression = new HTMLNode("p", "", "", geneExpressionStr);
		geneExpressionDiv.addChild(geneExpression);
		
		//homologues
		ArrayList<ArrayList<String>> terminalHomologues = terminalCase.getHomologues();
		HTMLNode homologuesTopContainerDiv = new HTMLNode("div", "homologuesTopContainer", "");
		HTMLNode collapseHomologuesButton = new HTMLNode("button", "homologuesCollapse", "homologuesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode homologuesTitle = new HTMLNode("p", "homologuesTitle",  "width: 95%; margin-top: 2%; float: left;",
				"<strong> Homologues: </strong>");
		homologuesTopContainerDiv.addChild(collapseHomologuesButton);
		homologuesTopContainerDiv.addChild(homologuesTitle);
		HTMLNode homologuesDiv = new HTMLNode("div", "homologues", "height: 20%; height: 0px; visibility: hidden;");
		HTMLNode homologuesLeftRightListDiv = new HTMLNode("div", "homologuesLR", "width: 50%; float: left;");
		HTMLNode lrUL = new HTMLNode("ul");
		HTMLNode lrLIHeaeder = new HTMLNode("li", "", "", "<strong>L/R</strong>");
		lrUL.addChild(lrLIHeaeder); //header
		if (terminalHomologues.size() > 0) {
			for (String leftRightHomologue : terminalHomologues.get(0)) {
				HTMLNode lrLI = new HTMLNode("li", "", "", leftRightHomologue);
				lrUL.addChild(lrLI);
			}
		}
		homologuesLeftRightListDiv.addChild(lrUL);
		
		
		HTMLNode homologuesAdditionalSymmDiv = new HTMLNode("div", "homologuesOther", "width: 50%; float: left;");
		HTMLNode additionalSymmUL = new HTMLNode("ul");
		HTMLNode additionaSymmLIHeader = new HTMLNode("li", "", "", "<strong>Additional Symmetries</strong>");
		additionalSymmUL.addChild(additionaSymmLIHeader);
		if (terminalHomologues.size() > 1) {
			for (String additionalSymmetry : terminalHomologues.get(1)) {
				HTMLNode additionalSymmLI = new HTMLNode("li", "", "", additionalSymmetry);
				additionalSymmUL.addChild(additionalSymmLI);
			}
		}
		homologuesAdditionalSymmDiv.addChild(additionalSymmUL);
		
		homologuesDiv.addChild(homologuesLeftRightListDiv);
		homologuesDiv.addChild(homologuesAdditionalSymmDiv);
		
		//links
		HTMLNode linksTopContainerDiv = new HTMLNode("div", "linksTopContainer", "width: 100%;");
		HTMLNode collapseLinksButton = new HTMLNode("button", "linksCollapse", "linksCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode linksTitle = new HTMLNode("p", "linksTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> External Links: </strong>");
		linksTopContainerDiv.addChild(collapseLinksButton);
		linksTopContainerDiv.addChild(linksTitle);
		HTMLNode linksDiv = new HTMLNode("div", "links", "height: 0px; visibility: hidden;");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : terminalCase.getLinks()) {
			String anchor = link; //replaced with anchor if valid link
			
			//begin after www.
			int startIDX = link.indexOf("www.")+4;
			if (startIDX > 0) {
				String placeholder = link.substring(startIDX);
				
				//find end of site name using '.'
				int dotIDX = placeholder.indexOf(".");
				if (dotIDX > 0) {
					placeholder = placeholder.substring(0, dotIDX);
					
					//make anchor tag
					String callbackMethod = "app." + placeholder + "()";
					anchor = "<a href=\"#\" onclick=\"" + callbackMethod + "\">" +
							terminalCase.getCellName() + " on " + placeholder +
							"</a>";
				}
			}
			HTMLNode li = new HTMLNode("li", "", "", anchor);
			linksUL.addChild(li);
		}
		linksDiv.addChild(linksUL);
		
		//references
		HTMLNode referencesTopContainerDiv = new HTMLNode("div", "referencesTopContainer", "");
		HTMLNode collapseReferencesButton = new HTMLNode("button", "referencesCollapse", "referencesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode referencesTitle = new HTMLNode("p", "referencesTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> References: </strong>");
		referencesTopContainerDiv.addChild(collapseReferencesButton);
		referencesTopContainerDiv.addChild(referencesTitle);
		HTMLNode referencesTEXTPRESSODiv = new HTMLNode("div", "references", "height: 0px; visibility: hidden;");
		HTMLNode referencesUL = new HTMLNode("ul");
		for (String reference : terminalCase.getReferences()) {
			HTMLNode li = new HTMLNode("li", "", "", reference);
			referencesUL.addChild(li);
		}
		referencesTEXTPRESSODiv.addChild(referencesUL);
		
		//production info
		HTMLNode productionInfoTopContainerDiv = new HTMLNode("div", "productionInfoTopContainer", "");
		HTMLNode collapseProductionInfoButton = new HTMLNode("button", "productionInfoCollapse", "productionInfoCollapseButton", 
				"width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode productionInfoTitle = new HTMLNode("p", "productionInfoTitle",  "width: 95%; margin-top: 2%; float: left;",
				"<strong> Production Information: </strong>");
		productionInfoTopContainerDiv.addChild(collapseProductionInfoButton);
		productionInfoTopContainerDiv.addChild(productionInfoTitle);
		HTMLNode productionInfoDiv = new HTMLNode("div", "productionInfo", "height: 0px; visibility: hidden;");
		HTMLNode productionInfoUL = new HTMLNode("ul");
		
		ArrayList<String> nuclearInfo = terminalCase.getNuclearProductionInfo();
		String markerAndStrainNuc = "<em>Nuclear: </em><br>Marker and Strain name: ";
		if (nuclearInfo.size() == 2) {
			markerAndStrainNuc += nuclearInfo.get(0) + "<br>"
					+ "Image Series: " + nuclearInfo.get(1);
		}
		HTMLNode nuclearLI = new HTMLNode("li", "", "", markerAndStrainNuc);
		
		boolean hasCellShapeData;
		ArrayList<String> cellShapeInfo = terminalCase.getCellShapeProductionInfo();
		String markerAndStrainCellShape = "<em>Cell Shape: </em><br>Marker and Strain name: ";
		if (cellShapeInfo.size() == 2) {
			hasCellShapeData = true;
			markerAndStrainCellShape += cellShapeInfo.get(0) + "<br>"
					+ "Image Series: " + cellShapeInfo.get(1);
		} else {
			hasCellShapeData = false;
		}
		HTMLNode cellShapeLI = new HTMLNode("li", "", "", markerAndStrainCellShape);
		
		HTMLNode additionalEmbryosLI = new HTMLNode("li", "", "", "<em>Additional Embryos: </em><br>[other equivalent data sets info]");
		productionInfoUL.addChild(nuclearLI);
		if (hasCellShapeData) {
			productionInfoUL.addChild(cellShapeLI);
		}
		productionInfoUL.addChild(additionalEmbryosLI);
		productionInfoDiv.addChild(productionInfoUL);

		
		if(isneuronpage){
			HTMLNode topContainerDiv = new HTMLNode("div", "topContainer", "width: 50%; height: 10%; float: left;"); //will contain external info and parts list description. float left for img on right
		
			topContainerDiv.addChild(cellNameDiv);
			topContainerDiv.addChild(partsListDescrDiv);
			//add divs to body
			body.addChild(topContainerDiv);
			System.out.println("image text not null - "+imagetext);
			body.addChild(imgDiv);
			if (functionFound) {
				body.addChild(functionWORMATLASTopContainerDiv);
				body.addChild(functionWORMATLASDiv);
			}
		} else {
			body.addChild(cellNameDiv);
			body.addChild(partsListDescrDiv);
		}
			

		body.addChild(anatomyTopContainerDiv);
		body.addChild(anatomyDiv);
		
		//only add this section if its contents exist
		if(isneuronpage){			
			body.addChild(wiringPartnersTopContainerDiv);
			body.addChild(wiringPartnersDiv);
		}
//		body.addChild(viewWDTopContainerDiv);
//		body.addChild(viewWDDiv);
		body.addChild(geneExpressionTopContainerDiv);
		body.addChild(geneExpressionDiv);
		body.addChild(homologuesTopContainerDiv);
		body.addChild(homologuesDiv);
		body.addChild(linksTopContainerDiv);
		body.addChild(linksDiv);
		body.addChild(referencesTopContainerDiv);
		body.addChild(referencesTEXTPRESSODiv);
		body.addChild(productionInfoTopContainerDiv);
		body.addChild(productionInfoDiv);
		
		//add collapse scripts to body
		body.addChild(collapseFunctionButton.makeCollapseButtonScript());
		body.addChild(collapseAnatomyButton.makeCollapseButtonScript());
		body.addChild(collapseWiringPartnersButton.makeCollapseButtonScript());
//		body.addChild(collapseViewWDButton.makeCollapseButtonScript());
		body.addChild(collapseGeneExpressionButton.makeCollapseButtonScript());
		body.addChild(collapseHomologuesButton.makeHomologuesCollapseButtonScript());
		body.addChild(collapseLinksButton.makeCollapseButtonScript());
		body.addChild(collapseReferencesButton.makeCollapseButtonScript());
		body.addChild(collapseProductionInfoButton.makeCollapseButtonScript());
		
		//add head and body to html
		html.addChild(head);
		html.addChild(body);
		
		//add style node
		buildStyleNode();	
	}
	
	/*
	 * NON TERMINAL CELL CASE
	 */
	public InfoWindowDOM(NonTerminalCellCase nonTerminalCase) {
		this.html = new HTMLNode("html");
		this.name = nonTerminalCase.getCellName();
	
		HTMLNode head = new HTMLNode("head");

		HTMLNode body = new HTMLNode("body");
		
		//cell name
		HTMLNode cellNameDiv = new HTMLNode("div", "externalInfo", "");
		String externalInfo = "<strong>" + nonTerminalCase.getCellName() + "</strong>";
		HTMLNode cellNameP = new HTMLNode("p", "", "", externalInfo);
		cellNameDiv.addChild(cellNameP);
		
		//homologues
		HTMLNode homologuesTopContainerDiv = new HTMLNode("div", "homologuesTopContainer", "TEST TEST TEST");
		HTMLNode collapseHomologuesButton = new HTMLNode("button", "homologuesCollapse", "homologuesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode homologuesTitle = new HTMLNode("p", "homologuesTitle",  "width: 95%; margin-top: 2%; float: left;",
				"<strong> Homologues: </strong>");
		homologuesTopContainerDiv.addChild(collapseHomologuesButton);
		homologuesTopContainerDiv.addChild(homologuesTitle);
		HTMLNode homologuesDiv = new HTMLNode("div", "homologues", "height: 0px; visibility: hidden;");
		HTMLNode homologuesLeftRightListDiv = new HTMLNode("div", "homologuesLR", "width: 50%; float: left");
		HTMLNode lrUL = new HTMLNode("ul");
		HTMLNode lrLI = new HTMLNode("li", "", "", "<strong>L/R</strong>");
		HTMLNode lrLI2 = new HTMLNode("li", "", "", nonTerminalCase.getEmbryonicHomology()); //is this the left/right option?
		lrUL.addChild(lrLI);
		lrUL.addChild(lrLI2);
		homologuesLeftRightListDiv.addChild(lrUL);
		HTMLNode homologuesAdditionalSymmDiv = new HTMLNode("div", "homologuesOther", "width: 50%; float: right;");
		HTMLNode additionalSymmUL = new HTMLNode("ul");
		HTMLNode additionaSymmLI = new HTMLNode("li", "", "", "<strong>N/A</strong>");
		additionalSymmUL.addChild(additionaSymmLI);
		homologuesAdditionalSymmDiv.addChild(additionalSymmUL);
		homologuesDiv.addChild(homologuesLeftRightListDiv);
		homologuesDiv.addChild(homologuesAdditionalSymmDiv);
		
		//terminal descendants
		HTMLNode terminalDescendantsTopContainerDiv = new HTMLNode("div", "terminalDescendantsTopContainer", "");
		HTMLNode collapseTerminalDescendantsButton = new HTMLNode("button", "terminalDescendantsCollapse", "terminalDescendantsCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode terminalDescendantsTitle = new HTMLNode("p", "terminalDescendantsTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> TerminalDescendants: </strong>");
		terminalDescendantsTopContainerDiv.addChild(collapseTerminalDescendantsButton);
		terminalDescendantsTopContainerDiv.addChild(terminalDescendantsTitle);
		HTMLNode terminalDescendantsDiv = new HTMLNode("div", "terminalDescendants", "height: 0px; visibility: hidden;");
		HTMLNode terminalDescendantsUL = new HTMLNode("ul");
		for (TerminalDescendant terminalDescendant : nonTerminalCase.getTerminalDescendants()) {
			String descendant = "";
			String functionalName = PartsList.getFunctionalNameByLineageName(terminalDescendant.getCellName());
			
			if (functionalName != null) {
				descendant += "<strong>" + functionalName.toUpperCase() + " (" + terminalDescendant.getCellName() + ")</strong>";
			} else {
				descendant = "<strong>" + terminalDescendant.getCellName() + "</strong>";
			}
			
			String partsListEntry = terminalDescendant.getPartsListEntry();
			if (!partsListEntry.equals("N/A")) {
				descendant += (", " + partsListEntry);
			}
			
			HTMLNode li = new HTMLNode("li", "", "", descendant);
			terminalDescendantsUL.addChild(li);
		}
		terminalDescendantsDiv.addChild(terminalDescendantsUL);
		
		// description for non terminal cell
		HTMLNode partsListDescrDiv = new HTMLNode("div", "partsListDescr", "");
		int terminalnum = nonTerminalCase.getTerminalDescendants().size();//# of terminals
		String partsListDescription = "Embryonic progenitor cell that generates " + terminalnum + " cells at hatching.";
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		partsListDescrDiv.addChild(partsListDescrP);
		
		//links
		HTMLNode linksTopContainerDiv = new HTMLNode("div", "linksTopContainer", "width: 100%;");
		HTMLNode collapseLinksButton = new HTMLNode("button", "linksCollapse", "linksCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode linksTitle = new HTMLNode("p", "linksTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> External Links: </strong>");
		linksTopContainerDiv.addChild(collapseLinksButton);
		linksTopContainerDiv.addChild(linksTitle);
		HTMLNode linksDiv = new HTMLNode("div", "links", "height: 0px; visibility: hidden;");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : nonTerminalCase.getLinks()) {
			String anchor = link; //replaced with anchor if valid link
			
			//begin after www.
			int startIDX = link.indexOf("www.")+4;
			if (startIDX > 0) {
				String placeholder = link.substring(startIDX);
				
				//find end of site name using '.'
				int dotIDX = placeholder.indexOf(".");
				if (dotIDX > 0) {
					placeholder = placeholder.substring(0, dotIDX);
					
					//make anchor tag
					String callbackMethod = "app." + placeholder + "()";
					anchor = "<a href=\"#\" onclick=\"" + callbackMethod + "\">" +
							nonTerminalCase.getCellName() + " on " + placeholder +
							"</a>";
				}
			}
			HTMLNode li = new HTMLNode("li", "", "", anchor);
			linksUL.addChild(li);
		}
		linksDiv.addChild(linksUL);

		
		//production info
		HTMLNode productionInfoTopContainerDiv = new HTMLNode("div", "productionInfoTopContainer", "");
		HTMLNode collapseProductionInfoButton = new HTMLNode("button", "productionInfoCollapse", "productionInfoCollapseButton", 
				"width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode productionInfoTitle = new HTMLNode("p", "productionInfoTitle",  "width: 95%; margin-top: 2%; float: left;",
				"<strong> Production Information: </strong>");
		productionInfoTopContainerDiv.addChild(collapseProductionInfoButton);
		productionInfoTopContainerDiv.addChild(productionInfoTitle);
		HTMLNode productionInfoDiv = new HTMLNode("div", "productionInfo", "height: 0px; visibility: hidden;");
		HTMLNode productionInfoUL = new HTMLNode("ul");
		
		ArrayList<String> nuclearInfo = nonTerminalCase.getNuclearProductionInfo();
		String markerAndStrainNuc = "<em>Nuclear: </em><br>Marker and Strain name: ";
		if (nuclearInfo.size() == 2) {
			markerAndStrainNuc += nuclearInfo.get(0) + "<br>"
					+ "Image Series: " + nuclearInfo.get(1);
		}
		HTMLNode nuclearLI = new HTMLNode("li", "", "", markerAndStrainNuc);
				
		boolean hasCellShapeData;
		ArrayList<String> cellShapeInfo = nonTerminalCase.getCellShapeProductionInfo();
		String markerAndStrainCellShape = "<em>Cell Shape: </em><br>Marker and Strain name: ";
		if (cellShapeInfo.size() == 2) {
			hasCellShapeData = true;
			markerAndStrainCellShape += cellShapeInfo.get(0) + "<br>"
					+ "Image Series: " + cellShapeInfo.get(1);
		} else {
			hasCellShapeData = false;
		}
		HTMLNode cellShapeLI = new HTMLNode("li", "", "", markerAndStrainCellShape);
				
		HTMLNode additionalEmbryosLI = new HTMLNode("li", "", "", "<em>Additional Embryos: </em><br>[other equivalent data sets info]");
		productionInfoUL.addChild(nuclearLI);
		if (hasCellShapeData) {
			productionInfoUL.addChild(cellShapeLI);
		}
		productionInfoUL.addChild(additionalEmbryosLI);
		productionInfoDiv.addChild(productionInfoUL);
		
		//add divs to body
		body.addChild(cellNameDiv);
		body.addChild( partsListDescrDiv); //added non terminal description -AS
		body.addChild(homologuesTopContainerDiv);
		body.addChild(homologuesDiv);
		//body.addChild(embryonicHomologyDiv);
		body.addChild(terminalDescendantsTopContainerDiv);
		body.addChild(terminalDescendantsDiv);
		body.addChild(linksTopContainerDiv);
		body.addChild(linksDiv);
		body.addChild(productionInfoTopContainerDiv);
		body.addChild(productionInfoDiv);
		
		//add collapse scripts to body
		body.addChild(collapseHomologuesButton.makeHomologuesCollapseButtonScript());
		body.addChild(collapseTerminalDescendantsButton.makeCollapseButtonScript());
		body.addChild(collapseLinksButton.makeCollapseButtonScript());
		body.addChild(collapseProductionInfoButton.makeCollapseButtonScript());
		
		//add head and body to html
		html.addChild(head);
		html.addChild(body);
				
		//add style node
		buildStyleNode();
	}
	
	public String DOMtoString() {
		String domAsString = doctypeTag;
		
//		String str = domAsString += html.formatNode();
//		System.out.println(str);
//		return str;
		return domAsString += html.formatNode();
		
	}
	
	/*
	 * iterates through the DOM and builds the style tag add to the head node
	 */
	public void buildStyleNode() {
		if (html == null) return;
		
		//start with rule for unorder list --> no bullets
		String style = newLine + "ul {"
				+ newLine + "list-style-type: none;"
				+ newLine + "display: block;"
				+ newLine + "width: 100%;"
				+ newLine + "}"
				+ newLine + newLine + "li {"
				+ newLine + "margin-bottom: 2%;"
				+ newLine + "}"
				+ newLine + newLine + "div {"
				+ newLine + "width: 100%;"
				+ newLine + "overflow: hidden;"
				+ newLine + "}" + newLine;
		HTMLNode head = null; //saved to add style node as child of head
		if (html.hasChildren()) {
			for (HTMLNode node : html.getChildren()) {
				if (node.getTag().equals("head")) { //save head
					head = node;
				} else if (node.getTag().equals("body")) { //get style
						style += findStyleInSubTree(node);
				}
					
			}
		}
		addStyleNodeToHead(head, style);
	}
	
	private void addStyleNodeToHead(HTMLNode head, String style) {
		if (head != null) {
			head.addChild(new HTMLNode(style, "text/css"));
		}
	}
	
	/*
	 * called by buildStyleNode to scan the body node and extract style attributes from children
	 * - only called if node is body tag and if body has children
	 */
	private String findStyleInSubTree(HTMLNode node) {
		String style = "";
		if (node.hasChildren()) {
			for (HTMLNode n : node.getChildren()) {
				if (n.hasID() && !n.getStyle().equals("")) {
					style += styleAsStr(n);
				}
					
				if (n.hasChildren()) {
					for (HTMLNode n1 : n.getChildren()) {
						style += findStyleInSubTree(n1);
					}
				}
			}
			
			if (node.hasID() && !node.getStyle().equals("")) {
				style += styleAsStr(node);
			}
		} else {
			if (node.hasID() && !node.getStyle().equals("")) {
				style += styleAsStr(node);
			}
		}
		return style;
	}
	
	private String styleAsStr(HTMLNode node) {
	 return (newLine + "#" + node.getID() + " {"
				+ newLine + node.getStyle()
				+ newLine + "}");
	}
	
	public String getName() {
		return this.name;
	}

	private final static String doctypeTag = "<!DOCTYPE html>";
	private final static String newLine = "\n";
//	private final static String meta_charset = "<meta charset=\"utf-8\">";
//	private final static String meta_httpequiv_content = "<meta http-equiv=\"X-UA-Compatible\" content=\"WormGUIDES, MSKCC, Zhirong Bao\">";
}
