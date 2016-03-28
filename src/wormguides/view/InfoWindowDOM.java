package wormguides.view;

import java.util.ArrayList;
import java.util.Collections;

import wormguides.AnatomyTerm;
import wormguides.model.AnatomyTermCase;
import wormguides.model.CellCase;
import wormguides.model.NonTerminalCellCase;
import wormguides.model.PartsList;
import wormguides.model.TerminalCellCase;
import wormguides.model.TerminalDescendant;

/**
 * The document object model for WormGUIDES external information windows
 * 
 * @author katzmanb
 *
 */
public class InfoWindowDOM {
	private HTMLNode html;
	private String name;

	// other future dom uses: connectome, parts list, cell shapes index

	/*
	 * TODO - when necessary getNode(String ID) removeNode(String ID)
	 * addChildToNode(String parentID, HTMLNode child) -- need this? add title
	 * tag to head
	 * 
	 */
	public InfoWindowDOM() {
		this.html = new HTMLNode("html");
	}

	public InfoWindowDOM(HTMLNode html) {
		if (!html.getTag().equals("html")) {
			this.html = new HTMLNode("html");
		} else {
			this.html = html;
		}
	}

	/**
	 * A DOM for a terminal cell case
	 * 
	 * @param terminalCase
	 */
	public InfoWindowDOM(TerminalCellCase terminalCase) {
		this.html = new HTMLNode("html");
		this.name = terminalCase.getCellName();

		HTMLNode head = new HTMLNode("head");

		HTMLNode body = new HTMLNode("body");

		// external info
		HTMLNode cellNameDiv = new HTMLNode("div", "cellName", "");
		String cellName = "<strong>" + terminalCase.getExternalInfo() + "</strong>";
		String viewInCellTheaterLink = "<a href=\"#\" name=\"" + terminalCase.getLineageName()
		+ "\" onclick=\"viewInCellTheater(this)\"> View in 3D</a>";
		HTMLNode cellNameP = new HTMLNode("p", "", "", cellName + "<br>" + viewInCellTheaterLink);
		cellNameDiv.addChild(cellNameP);

		// parts list descriptions
		HTMLNode partsListDescrDiv = new HTMLNode("div", "partsListDescr", "");
		String partsListDescription = terminalCase.getPartsListDescription();
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		partsListDescrDiv.addChild(partsListDescrP);

		// image
		HTMLNode imgDiv = new HTMLNode("div", "imgDiv", "width: 50%; height: 10%; float: left;");
		terminalCase.getImageURL();
		HTMLNode img = new HTMLNode(terminalCase.getImageURL(), true);
		imgDiv.addChild(img);

		// wormatlas function
		HTMLNode functionWORMATLASTopContainerDiv = new HTMLNode("div", "functionTopContainer", "");
		HTMLNode collapseFunctionButton = new HTMLNode("button", "functionWORMATLASCollapse", "functionCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode functionWORMATLASTitle = new HTMLNode("p", "functionWORMATLASTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Wormatlas Function: </strong>");
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

		// wiring
		HTMLNode wiringPartnersTopContainerDiv = new HTMLNode("div", "wiringPartnersTopContainer", "");
		HTMLNode collapseWiringPartnersButton = new HTMLNode("button", "wiringPartnersCollapse",
				"wiringPartnersCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode wiringPartnersTitle = new HTMLNode("p", "wiringPartnersTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Wiring Partners: </strong>");
		wiringPartnersTopContainerDiv.addChild(collapseWiringPartnersButton);
		wiringPartnersTopContainerDiv.addChild(wiringPartnersTitle);
		HTMLNode wiringPartnersDiv = new HTMLNode("div", "wiringPartners", "height: 0px; visibility: hidden;");
		// view in wiring diagram
		HTMLNode viewWDDiv = new HTMLNode("div", "viewWD", "");
		HTMLNode viewWDP = new HTMLNode("p", "viewWDTitle", "",
				"<em> View in Wiring Diagram Network:  (image is placeholder for interactive wiring diagram rendering)</em>");
		HTMLNode viewWDImg = new HTMLNode("http://www.wormatlas.org/images/connectome.jpg", true);
		viewWDDiv.addChild(viewWDP);
		viewWDDiv.addChild(viewWDImg);
		// wiring partners UL
		HTMLNode wiringPartnersUL = new HTMLNode("ul");
		ArrayList<String> presynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> postsynapticPartners = terminalCase.getPresynapticPartners();
		ArrayList<String> electricalPartners = terminalCase.getElectricalPartners();
		ArrayList<String> neuromuscularPartners = terminalCase.getNeuromuscularPartners();
		if (presynapticPartners.size() > 0) {
			Collections.sort(presynapticPartners);

			ArrayList<String> presynapticPartnerAnchors = new ArrayList<String>();
			for (String presynapticPartner : presynapticPartners) {
				String anchor = "<a href=\"#\" onclick=\"handleWiringPartnerClick(this);\">" + presynapticPartner
						+ "</a>";
				presynapticPartnerAnchors.add(anchor);
			}
			String prePartners = presynapticPartnerAnchors.toString().substring(1,
					presynapticPartnerAnchors.toString().length() - 2);

			HTMLNode li = new HTMLNode("li", "", "", "<em>Presynaptic to: </em><br>" + prePartners);
			wiringPartnersUL.addChild(li);
		}
		if (postsynapticPartners.size() > 0) {
			Collections.sort(postsynapticPartners);

			ArrayList<String> postsynapticPartnersAnchors = new ArrayList<String>();
			for (String postsynapticPartner : postsynapticPartners) {
				String anchor = "<a href=\"#\" onclick=\"handleWiringPartnerClick(this);\">" + postsynapticPartner
						+ "</a>";
				postsynapticPartnersAnchors.add(anchor);
			}

			String postPartners = postsynapticPartnersAnchors.toString().substring(1,
					postsynapticPartnersAnchors.toString().length() - 2);
			HTMLNode li = new HTMLNode("li", "", "", "<em>Postsynaptic to: </em><br>" + postPartners);
			wiringPartnersUL.addChild(li);
		}
		if (electricalPartners.size() > 0) {
			Collections.sort(electricalPartners);

			ArrayList<String> electricalPartnersAnchors = new ArrayList<String>();
			for (String electricalPartner : electricalPartners) {
				String anchor = "<a href=\"#\" onclick=\"handleWiringPartnerClick(this);\">" + electricalPartner
						+ "</a>";
				electricalPartnersAnchors.add(anchor);
			}

			String electPartners = electricalPartnersAnchors.toString().substring(1,
					electricalPartnersAnchors.toString().length() - 2);
			HTMLNode li = new HTMLNode("li", "", "", "<em>Electrical to: </em><br>" + electPartners);
			wiringPartnersUL.addChild(li);
		}
		if (neuromuscularPartners.size() > 0) {
			Collections.sort(neuromuscularPartners);

			ArrayList<String> neuromuscularPartnersAnchors = new ArrayList<String>();
			for (String neuromuscularPartner : neuromuscularPartners) {
				String anchor = "<a href=\"#\" onclick=\"handleWiringPartnerClick(this);\">" + neuromuscularPartner
						+ "</a>";
				neuromuscularPartnersAnchors.add(anchor);
			}

			String neuroPartners = neuromuscularPartnersAnchors.toString().substring(1,
					neuromuscularPartnersAnchors.toString().length() - 2);
			HTMLNode li = new HTMLNode("li", "", "", "<em>Neuromusclar to: </em><br>" + neuroPartners);
			wiringPartnersUL.addChild(li);
		}

		boolean isneuronpage = (presynapticPartners.size() > 0 || electricalPartners.size() > 0
				|| neuromuscularPartners.size() > 0 || postsynapticPartners.size() > 0);
		// only add this section if it's a neuron (i.e. it appears in wiring diagram) -AS
		if (isneuronpage) {
			wiringPartnersDiv.addChild(wiringPartnersUL);
			wiringPartnersDiv.addChild(viewWDDiv); // reversed order of these
			// elements -AS
		}

		// expresses
		HTMLNode geneExpressionTopContainerDiv = new HTMLNode("div", "expressesTopContainer", "");
		HTMLNode collapseGeneExpressionButton = new HTMLNode("button", "geneExpressionCollapse",
				"geneExpressionCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode geneExpressionTitle = new HTMLNode("p", "geneExpressionTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Gene Expression: </strong>");
		geneExpressionTopContainerDiv.addChild(collapseGeneExpressionButton);
		geneExpressionTopContainerDiv.addChild(geneExpressionTitle);
		HTMLNode geneExpressionDiv = new HTMLNode("div", "geneExpression", "height: 0px; visibility: hidden;");
		ArrayList<String> geneExpressions = terminalCase.getExpressesWORMBASE();
		Collections.sort(geneExpressions);
		String geneExpressionStr = geneExpressions.toString();
		geneExpressionStr = geneExpressionStr.substring(1, geneExpressionStr.length() - 1); // remove surrounding brackets
		HTMLNode geneExpression = new HTMLNode("p", "", "", geneExpressionStr);
		geneExpressionDiv.addChild(geneExpression);
		boolean expresses = false;
		if (geneExpressions.size() > 0) {
			expresses = true;
		}


		// homologues
		boolean hasHomologues = false;
		ArrayList<ArrayList<String>> terminalHomologues = terminalCase.getHomologues();
		HTMLNode homologuesTopContainerDiv = new HTMLNode("div", "homologuesTopContainer", "");
		HTMLNode collapseHomologuesButton = new HTMLNode("button", "homologuesCollapse", "homologuesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode homologuesTitle = new HTMLNode("p", "homologuesTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> Homologues: </strong>");
		homologuesTopContainerDiv.addChild(collapseHomologuesButton);
		homologuesTopContainerDiv.addChild(homologuesTitle);
		HTMLNode homologuesDiv = new HTMLNode("div", "homologues", "height: 20%; height: 0px; visibility: hidden;");
		HTMLNode homologuesLeftRightListDiv = new HTMLNode("div", "homologuesLR", "width: 50%; float: left;");
		HTMLNode lrUL = new HTMLNode("ul");
		HTMLNode lrLIHeaeder = new HTMLNode("li", "", "", "<strong>L/R</strong>");
		lrUL.addChild(lrLIHeaeder); // header
		if (terminalHomologues.size() > 0) {
			hasHomologues = true;
			for (String leftRightHomologue : terminalHomologues.get(0)) {
				HTMLNode lrLI = new HTMLNode("li", "", "", leftRightHomologue);
				lrUL.addChild(lrLI);
			}
		} else {
			hasHomologues = false;
		}
		homologuesLeftRightListDiv.addChild(lrUL);

		HTMLNode homologuesAdditionalSymmDiv = new HTMLNode("div", "homologuesOther", "width: 50%; float: left;");
		HTMLNode additionalSymmUL = new HTMLNode("ul");
		HTMLNode additionaSymmLIHeader = new HTMLNode("li", "", "", "<strong>Additional Symmetries</strong>");
		additionalSymmUL.addChild(additionaSymmLIHeader);
		if (terminalHomologues.size() > 1) {
			hasHomologues = true;
			for (String additionalSymmetry : terminalHomologues.get(1)) {
				HTMLNode additionalSymmLI = new HTMLNode("li", "", "", additionalSymmetry);
				additionalSymmUL.addChild(additionalSymmLI);
			}
		}
		homologuesAdditionalSymmDiv.addChild(additionalSymmUL);

		homologuesDiv.addChild(homologuesLeftRightListDiv);
		homologuesDiv.addChild(homologuesAdditionalSymmDiv);

		// links
		HTMLNode linksTopContainerDiv = new HTMLNode("div", "linksTopContainer", "width: 100%;");
		HTMLNode collapseLinksButton = new HTMLNode("button", "linksCollapse", "linksCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode linksTitle = new HTMLNode("p", "linksTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong>External Links: </strong>");
		linksTopContainerDiv.addChild(collapseLinksButton);
		linksTopContainerDiv.addChild(linksTitle);
		HTMLNode linksDiv = new HTMLNode("div", "links", "height: 0px; visibility: hidden;");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : terminalCase.getLinks()) {
			String anchor = link; // replaced with anchor if valid link

			// begin after www.
			int startIDX = link.indexOf("www.");
			if (startIDX > 0) {
				// check if textpresso link i.e. '-' before www
				if (link.charAt(startIDX - 1) == '-') {
					anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
							+ terminalCase.getCellName() + " on Textpresso</a>";
				} else {

					// move past www.
					startIDX += 4;

					String placeholder = link.substring(startIDX);

					// find end of site name using '.'
					int dotIDX = placeholder.indexOf(".");
					if (dotIDX > 0) {
						placeholder = placeholder.substring(0, dotIDX);

						// capitalize first letter of placeholder
						placeholder = Character.toUpperCase(placeholder.charAt(0)) + placeholder.substring(1);

						// check for google links
						if (placeholder.equals("Google")) {
							// check if wormatlas specific search
							if (link.contains("site:wormatlas.org")) {
								anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
										+ terminalCase.getCellName() + " on Google (searching Wormatlas)" + "</a>";
							} else {
								anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
										+ terminalCase.getCellName() + " on Google</a>";
							}
						} else {
							// make anchor tag
							anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
									+ terminalCase.getCellName() + " on " + placeholder + "</a>";

							// add wormbase link to end of gene expression section
							if (placeholder.equals("Wormbase")) {
								String wormbaseSource = "<em>Source:</em> " + anchor;
								if (expresses) {
									geneExpressionDiv.addChild(new HTMLNode("p", "", "", wormbaseSource));
								}
							}
						}
					}
				}
			} else if (link.startsWith("http://wormwiring.hpc.einstein.yu.edu/data/neuronData.php?name=")) {
				anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
						+ terminalCase.getCellName() + " on Wormwiring</a>";
			}

			// make sure anchor has been built
			if (!anchor.equals(link)) {
				HTMLNode li = new HTMLNode("li", "", "", anchor);
				linksUL.addChild(li);
			}
		}
		/*
		 * TODO cytoshow stub
		 */
		// HTMLNode liSTUB = new HTMLNode("li", "", "", "Cytoshow: [cytoshow
		// link to this cell in EM data]");
		// linksUL.addChild(liSTUB);

		linksDiv.addChild(linksUL);

		// references
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

		// production info
		HTMLNode productionInfoTopContainerDiv = new HTMLNode("div", "productionInfoTopContainer", "");
		HTMLNode collapseProductionInfoButton = new HTMLNode("button", "productionInfoCollapse",
				"productionInfoCollapseButton", "width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode productionInfoTitle = new HTMLNode("p", "productionInfoTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Primary Data: </strong>");
		productionInfoTopContainerDiv.addChild(collapseProductionInfoButton);
		productionInfoTopContainerDiv.addChild(productionInfoTitle);
		HTMLNode productionInfoDiv = new HTMLNode("div", "productionInfo", "height: 0px; visibility: hidden;");
		HTMLNode productionInfoUL = new HTMLNode("ul");

		ArrayList<String> nuclearInfo = terminalCase.getNuclearProductionInfo();
		String markerAndStrainNuc = "<em>Nuclear: </em><br>Strain and Marker name: ";
		if (nuclearInfo.size() == 2) {
			markerAndStrainNuc += nuclearInfo.get(0) + "<br>" + "Image Series: " + nuclearInfo.get(1);
		}
		HTMLNode nuclearLI = new HTMLNode("li", "", "", markerAndStrainNuc);

		boolean hasCellShapeData;
		ArrayList<String> cellShapeInfo = terminalCase.getCellShapeProductionInfo();
		String markerAndStrainCellShape = "<em>Cell Shape: </em><br>Strain and Marker name: ";
		if (cellShapeInfo.size() == 2) {
			hasCellShapeData = true;
			markerAndStrainCellShape += cellShapeInfo.get(0) + "<br>" + "Image Series: " + cellShapeInfo.get(1);
		} else {
			hasCellShapeData = false;
		}
		HTMLNode cellShapeLI = new HTMLNode("li", "", "", markerAndStrainCellShape);

		HTMLNode additionalEmbryosLI = new HTMLNode("li", "", "",
				"<em>Additional Embryos: </em><br>[other equivalent data sets info]");
		productionInfoUL.addChild(nuclearLI);
		if (hasCellShapeData) {
			productionInfoUL.addChild(cellShapeLI);
		}
		productionInfoUL.addChild(additionalEmbryosLI);
		productionInfoDiv.addChild(productionInfoUL);

		if (isneuronpage) {
			HTMLNode topContainerDiv = new HTMLNode("div", "topContainer", "width: 50%; height: 10%; float: left;");
			// will contain external info and parts list description.
			// float left for img on right

			topContainerDiv.addChild(cellNameDiv);
			topContainerDiv.addChild(partsListDescrDiv);
			// add divs to body
			body.addChild(topContainerDiv);
			// System.out.println("image text not null - " + imagetext);
			body.addChild(imgDiv);
			if (functionFound) {
				body.addChild(functionWORMATLASTopContainerDiv);
				body.addChild(functionWORMATLASDiv);
			}
		} else {
			body.addChild(cellNameDiv);
			body.addChild(partsListDescrDiv);
		}

		// anatomy --> only build if anatomy info present
		if (terminalCase.getHasAnatomyFlag()) {
			HTMLNode anatomyTopContainerDiv = new HTMLNode("div", "anatomyTopContainer", "");
			HTMLNode collapseAnatomyButton = new HTMLNode("button", "anatomyCollapse", "anatomyCollapseButton",
					"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
			HTMLNode anatomyTitle = new HTMLNode("p", "anatomyTitle", "width: 95%; margin-top: 2%; float: left;",
					"<strong> Anatomy: </strong>");
			anatomyTopContainerDiv.addChild(collapseAnatomyButton);
			anatomyTopContainerDiv.addChild(anatomyTitle);
			HTMLNode anatomyDiv = new HTMLNode("div", "anatomy", "height: 0px; visibility: hidden;");
			HTMLNode anatomyUL = new HTMLNode("ul");
			ArrayList<String> anatomy = terminalCase.getAnatomy();
			/*
			 * there are 5 fields: name, type, location, function,
			 * neurotransmitter we'll add labels to the data based on
			 * corresponding indices i.e. 0-4 if a data value is '*' it
			 * indicates that the field is N/A and we'll skip those cases
			 */
			if (anatomy.size() == 7) { // we won't add name
				String type = anatomy.get(1);
				String somaLocation = anatomy.get(2);
				String neuriteLocation = anatomy.get(3);
				String morphologicalFeature = anatomy.get(4);
				String function = anatomy.get(5);
				String neurotransmitter = anatomy.get(6);

				boolean hasAmphidLink = false;

				if (!type.equals("*")) {
					// check for keyword: 'amphid'
					if (type.toLowerCase().contains(AMPHID)) {
						hasAmphidLink = true;

						// update occurences of 'amphid' with link
						type = findAndUpdateOccurencesOfAmphid(type);
					}
					anatomyUL.addChild(new HTMLNode("li", "", "", "<em>Type: </em>" + type));
				}

				if (!somaLocation.equals("*")) {
					if (somaLocation.toLowerCase().contains(AMPHID)) {
						hasAmphidLink = true;

						somaLocation = findAndUpdateOccurencesOfAmphid(somaLocation);

					}
					anatomyUL.addChild(new HTMLNode("li", "", "", "<em>Soma Location: </em>" + somaLocation));
				}

				if (!neuriteLocation.equals("*")) {
					// check for keyword: 'amphid'
					if (neuriteLocation.toLowerCase().contains(AMPHID)) {
						hasAmphidLink = true;

						neuriteLocation = findAndUpdateOccurencesOfAmphid(neuriteLocation);
					}
					anatomyUL.addChild(new HTMLNode("li", "", "", "<em>Neurite Location: </em>" + neuriteLocation));
				}

				if (!morphologicalFeature.equals("*")) {
					if (morphologicalFeature.toLowerCase().contains(AMPHID)) {
						hasAmphidLink = true;

						morphologicalFeature = findAndUpdateOccurencesOfAmphid(morphologicalFeature);

					}
					anatomyUL.addChild(
							new HTMLNode("li", "", "", "<em>Morphological Feature: </em>" + morphologicalFeature));
				}

				if (!function.equals("*")) {
					if (function.toLowerCase().contains(AMPHID)) {
						hasAmphidLink = true;

						function = findAndUpdateOccurencesOfAmphid(function);

					}
					anatomyUL.addChild(new HTMLNode("li", "", "", "<em>Function: </em>" + function));
				}

				if (!neurotransmitter.equals("*")) {
					if (neurotransmitter.toLowerCase().contains(AMPHID)) {
						hasAmphidLink = true;

						neurotransmitter = findAndUpdateOccurencesOfAmphid(neurotransmitter);
					}
					anatomyUL.addChild(new HTMLNode("li", "", "", "<em>Neurotransmitter: </em>" + neurotransmitter));
				}

				anatomyDiv.addChild(anatomyUL);
				body.addChild(anatomyTopContainerDiv);
				body.addChild(anatomyDiv);
				body.addChild(collapseAnatomyButton.makeCollapseButtonScript()); // add script here for scoping purposes

				// check if link handler for amphid is needed
				if (hasAmphidLink) {
					// add amphid link controller script
					body.addChild(anatomyDiv.handleAmphidClickScript());
				}
			}
		}

		// only add this section if its contents exist
		if (isneuronpage) {
			body.addChild(wiringPartnersTopContainerDiv);
			body.addChild(wiringPartnersDiv);
		}

		if (expresses) {
			body.addChild(geneExpressionTopContainerDiv);
			body.addChild(geneExpressionDiv);
		}
		
		if (hasHomologues) {
			body.addChild(homologuesTopContainerDiv);
			body.addChild(homologuesDiv);
		}
		
		body.addChild(linksTopContainerDiv);
		body.addChild(linksDiv);
		body.addChild(referencesTopContainerDiv);
		body.addChild(referencesTEXTPRESSODiv);
		body.addChild(productionInfoTopContainerDiv);
		body.addChild(productionInfoDiv);

		// add collapse scripts to body
		body.addChild(collapseFunctionButton.makeCollapseButtonScript());
		body.addChild(collapseWiringPartnersButton.makeCollapseButtonScript());
		if (expresses) {
			body.addChild(collapseGeneExpressionButton.makeCollapseButtonScript());
		}
		
		if (hasHomologues) {
			body.addChild(collapseHomologuesButton.makeHomologuesCollapseButtonScript());
		}
		
		body.addChild(collapseLinksButton.makeCollapseButtonScript());
		body.addChild(collapseReferencesButton.makeCollapseButtonScript());
		body.addChild(collapseProductionInfoButton.makeCollapseButtonScript());

		// link controller scripts
		body.addChild(body.addLinkHandlerScript());
		body.addChild(body.handleWiringPartnerClickScript());
		body.addChild(body.viewInCellTheaterScript());

		// add head and body to html
		html.addChild(head);
		html.addChild(body);

		// add style node
		buildStyleNode();
	}

	/**
	 * A DOM for a non terminal cell case
	 * 
	 * @param nonTerminalCase
	 */
	public InfoWindowDOM(NonTerminalCellCase nonTerminalCase) {
		this.html = new HTMLNode("html");
		this.name = nonTerminalCase.getLineageName();

		HTMLNode head = new HTMLNode("head");

		HTMLNode body = new HTMLNode("body");

		// cell name
		HTMLNode cellNameDiv = new HTMLNode("div", "externalInfo", "");
		String externalInfo = "<strong>" + nonTerminalCase.getLineageName() + "</strong>";
		String viewInCellTheaterLink = "<a href=\"#\" name=\"" + nonTerminalCase.getLineageName()
		+ "\" onclick=\"viewInCellTheater(this)\"> View in 3D</a>";
		HTMLNode cellNameP = new HTMLNode("p", "", "", externalInfo + "<br>" + viewInCellTheaterLink);
		cellNameDiv.addChild(cellNameP);

		// homologues
		boolean hasHomologues = false;
		HTMLNode homologuesTopContainerDiv = new HTMLNode("div", "homologuesTopContainer", "TEST TEST TEST");
		HTMLNode collapseHomologuesButton = new HTMLNode("button", "homologuesCollapse", "homologuesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode homologuesTitle = new HTMLNode("p", "homologuesTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> Homologues: </strong>");
		homologuesTopContainerDiv.addChild(collapseHomologuesButton);
		homologuesTopContainerDiv.addChild(homologuesTitle);
		HTMLNode homologuesDiv = new HTMLNode("div", "homologues", "height: 0px; visibility: hidden;");
		HTMLNode homologuesLeftRightListDiv = new HTMLNode("div", "homologuesLR", "width: 50%; float: left");
		HTMLNode lrUL = new HTMLNode("ul");
		HTMLNode lrLI = new HTMLNode("li", "", "", "<strong>L/R</strong>");
		HTMLNode lrLI2 = new HTMLNode("li", "", "", nonTerminalCase.getEmbryonicHomology());
		lrUL.addChild(lrLI);
		lrUL.addChild(lrLI2);
		homologuesLeftRightListDiv.addChild(lrUL);
		if (!nonTerminalCase.getEmbryonicHomology().equals("N/A")) {
			hasHomologues = true;
		}
		
		HTMLNode homologuesAdditionalSymmDiv = new HTMLNode("div", "homologuesOther", "width: 50%; float: right;");
		HTMLNode additionalSymmUL = new HTMLNode("ul");
		HTMLNode additionaSymmLI = new HTMLNode("li", "", "", "<strong>N/A</strong>");
		additionalSymmUL.addChild(additionaSymmLI);
		homologuesAdditionalSymmDiv.addChild(additionalSymmUL);
		homologuesDiv.addChild(homologuesLeftRightListDiv);
		homologuesDiv.addChild(homologuesAdditionalSymmDiv);

		// terminal descendants
		HTMLNode terminalDescendantsTopContainerDiv = new HTMLNode("div", "terminalDescendantsTopContainer", "");
		HTMLNode collapseTerminalDescendantsButton = new HTMLNode("button", "terminalDescendantsCollapse",
				"terminalDescendantsCollapseButton", "width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+",
				true);
		HTMLNode terminalDescendantsTitle = new HTMLNode("p", "terminalDescendantsTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Terminal Descendants: </strong>");
		terminalDescendantsTopContainerDiv.addChild(collapseTerminalDescendantsButton);
		terminalDescendantsTopContainerDiv.addChild(terminalDescendantsTitle);
		HTMLNode terminalDescendantsDiv = new HTMLNode("div", "terminalDescendants",
				"height: 0px; visibility: hidden;");
		HTMLNode terminalDescendantsUL = new HTMLNode("ul");
		for (TerminalDescendant terminalDescendant : nonTerminalCase.getTerminalDescendants()) {
			String descendant = "";
			String functionalName = PartsList.getFunctionalNameByLineageName(terminalDescendant.getCellName());

			if (functionalName != null) {
				descendant += "<strong>" + functionalName.toUpperCase() + " (" + terminalDescendant.getCellName()
				+ ")</strong>";
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
		int terminalnum = nonTerminalCase.getTerminalDescendants().size();// #
		// of
		// terminals
		String partsListDescription = "Embryonic progenitor cell that generates " + terminalnum + " cells at hatching.";
		HTMLNode partsListDescrP = new HTMLNode("p", "", "", partsListDescription);
		partsListDescrDiv.addChild(partsListDescrP);

		// expresses
		HTMLNode geneExpressionTopContainerDiv = new HTMLNode("div", "expressesTopContainer", "");
		HTMLNode collapseGeneExpressionButton = new HTMLNode("button", "geneExpressionCollapse",
				"geneExpressionCollapseButton", "width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode geneExpressionTitle = new HTMLNode("p", "geneExpressionTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Gene Expression: </strong>");
		geneExpressionTopContainerDiv.addChild(collapseGeneExpressionButton);
		geneExpressionTopContainerDiv.addChild(geneExpressionTitle);
		HTMLNode geneExpressionDiv = new HTMLNode("div", "geneExpression", "height: 0px; visibility: hidden;");
		ArrayList<String> geneExpressions = nonTerminalCase.getExpressesWORMBASE();
		Collections.sort(geneExpressions);
		String geneExpressionStr = geneExpressions.toString();
		geneExpressionStr = geneExpressionStr.substring(1, geneExpressionStr.length() - 1); // remove
		// surrounding brackets
		HTMLNode geneExpression = new HTMLNode("p", "", "", geneExpressionStr);
		geneExpressionDiv.addChild(geneExpression);
		boolean expresses = false;
		if (geneExpressions.size() > 0) {
			expresses = true;
		}

		// links
		HTMLNode linksTopContainerDiv = new HTMLNode("div", "linksTopContainer", "width: 100%;");
		HTMLNode collapseLinksButton = new HTMLNode("button", "linksCollapse", "linksCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
		HTMLNode linksTitle = new HTMLNode("p", "linksTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong>External Links: </strong>");
		linksTopContainerDiv.addChild(collapseLinksButton);
		linksTopContainerDiv.addChild(linksTitle);
		HTMLNode linksDiv = new HTMLNode("div", "links", "height: 0px; visibility: hidden;");
		HTMLNode linksUL = new HTMLNode("ul");
		for (String link : nonTerminalCase.getLinks()) {
			if (!link.equals("")) {
				String anchor = link; // replaced with anchor if valid link

				// begin after www.
				int startIDX = link.indexOf("www.") + 4;
				if (startIDX > 0) {
					String placeholder = link.substring(startIDX);

					// find end of site name using '.'
					int dotIDX = placeholder.indexOf(".");
					if (dotIDX > 0) {
						placeholder = placeholder.substring(0, dotIDX);

						// check for google links
						if (placeholder.equals("google")) {
							// check if wormatlas specific search
							if (link.contains("site:wormatlas.org")) {
								anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
										+ nonTerminalCase.getLineageName() + " on Google (searching Wormatlas)" + "</a>";
							} else {
								anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
										+ nonTerminalCase.getLineageName() + " on Google</a>";
							}
						} else {
							if (placeholder.toLowerCase().equals("cacr")) {
								placeholder = "Textpresso";
							}
							anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
									+ nonTerminalCase.getLineageName() + " on " + placeholder + "</a>";
							
							// add wormbase link to end of gene expression section
							if (placeholder.equals("wormbase")) {
								String wormbaseSource = "<em>Source:</em> " + anchor;
								if (expresses) {
									geneExpressionDiv.addChild(new HTMLNode("p", "", "", wormbaseSource));
								}
							}
						}
					}
				}
				HTMLNode li = new HTMLNode("li", "", "", anchor);
				linksUL.addChild(li);
			}
		}
		linksDiv.addChild(linksUL);

		// references
		HTMLNode referencesTopContainerDiv = new HTMLNode("div", "referencesTopContainer", "");
		HTMLNode collapseReferencesButton = new HTMLNode("button", "referencesCollapse", "referencesCollapseButton",
				"width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode referencesTitle = new HTMLNode("p", "referencesTitle", "width: 95%; margin-top: 2%; float: left;",
				"<strong> References: </strong>");
		referencesTopContainerDiv.addChild(collapseReferencesButton);
		referencesTopContainerDiv.addChild(referencesTitle);
		HTMLNode referencesTEXTPRESSODiv = new HTMLNode("div", "references", "height: 0px; visibility: hidden;");
		HTMLNode referencesUL = new HTMLNode("ul");
		for (String reference : nonTerminalCase.getReferences()) {
			HTMLNode li = new HTMLNode("li", "", "", reference);
			referencesUL.addChild(li);
		}
		referencesTEXTPRESSODiv.addChild(referencesUL);

		// production info
		HTMLNode productionInfoTopContainerDiv = new HTMLNode("div", "productionInfoTopContainer", "");
		HTMLNode collapseProductionInfoButton = new HTMLNode("button", "productionInfoCollapse",
				"productionInfoCollapseButton", "width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+", true);
		HTMLNode productionInfoTitle = new HTMLNode("p", "productionInfoTitle",
				"width: 95%; margin-top: 2%; float: left;", "<strong> Primary Data: </strong>");
		productionInfoTopContainerDiv.addChild(collapseProductionInfoButton);
		productionInfoTopContainerDiv.addChild(productionInfoTitle);
		HTMLNode productionInfoDiv = new HTMLNode("div", "productionInfo", "height: 0px; visibility: hidden;");
		HTMLNode productionInfoUL = new HTMLNode("ul");

		ArrayList<String> nuclearInfo = nonTerminalCase.getNuclearProductionInfo();
		String markerAndStrainNuc = "<em>Nuclear: </em><br>Marker and Strain name: ";
		if (nuclearInfo.size() == 2) {
			markerAndStrainNuc += nuclearInfo.get(0) + "<br>" + "Image Series: " + nuclearInfo.get(1);
		}
		HTMLNode nuclearLI = new HTMLNode("li", "", "", markerAndStrainNuc);

		boolean hasCellShapeData;
		ArrayList<String> cellShapeInfo = nonTerminalCase.getCellShapeProductionInfo();
		String markerAndStrainCellShape = "<em>Cell Shape: </em><br>Marker and Strain name: ";
		if (cellShapeInfo.size() == 2) {
			hasCellShapeData = true;
			markerAndStrainCellShape += cellShapeInfo.get(0) + "<br>" + "Image Series: " + cellShapeInfo.get(1);
		} else {
			hasCellShapeData = false;
		}
		HTMLNode cellShapeLI = new HTMLNode("li", "", "", markerAndStrainCellShape);

		HTMLNode additionalEmbryosLI = new HTMLNode("li", "", "",
				"<em>Additional Embryos: </em><br>[other equivalent data sets info]");
		productionInfoUL.addChild(nuclearLI);
		if (hasCellShapeData) {
			productionInfoUL.addChild(cellShapeLI);
		}
		productionInfoUL.addChild(additionalEmbryosLI);
		productionInfoDiv.addChild(productionInfoUL);

		// add divs to body
		body.addChild(cellNameDiv);
		body.addChild(partsListDescrDiv); // added non terminal description -AS
		
		if (hasHomologues) {
			body.addChild(homologuesTopContainerDiv);
			body.addChild(homologuesDiv);
		}
		
		if (expresses) {
			body.addChild(geneExpressionTopContainerDiv);
			body.addChild(geneExpressionDiv);
		}
		
		// body.addChild(embryonicHomologyDiv);
		body.addChild(terminalDescendantsTopContainerDiv);
		body.addChild(terminalDescendantsDiv);
		body.addChild(linksTopContainerDiv);
		body.addChild(linksDiv);
		body.addChild(referencesTopContainerDiv);
		body.addChild(referencesTEXTPRESSODiv);
		body.addChild(productionInfoTopContainerDiv);
		body.addChild(productionInfoDiv);

		// add collapse scripts to body
		if (hasHomologues) {
			body.addChild(collapseHomologuesButton.makeHomologuesCollapseButtonScript());
		}
		
		if (expresses) {
			body.addChild(collapseGeneExpressionButton.makeCollapseButtonScript());
		}
		
		body.addChild(collapseTerminalDescendantsButton.makeCollapseButtonScript());
		body.addChild(collapseLinksButton.makeCollapseButtonScript());
		body.addChild(collapseReferencesButton.makeCollapseButtonScript());
		body.addChild(collapseProductionInfoButton.makeCollapseButtonScript());

		// add link controller
		body.addChild(body.addLinkHandlerScript());
		body.addChild(body.viewInCellTheaterScript());

		// add head and body to html
		html.addChild(head);
		html.addChild(body);

		// add style node
		buildStyleNode();
	}

	public InfoWindowDOM(AnatomyTermCase termCase) {
		// build the amphid sensilla dom
		if (termCase.getName().equals(AnatomyTerm.AMPHID_SENSILLA.getTerm())) {

			this.html = new HTMLNode("html");
			this.name = termCase.getName();

			HTMLNode head = new HTMLNode("head");
			HTMLNode body = new HTMLNode("body");

			// term
			HTMLNode termDiv = new HTMLNode("div", "externalInfo", "");
			String term = "<strong>" + termCase.getName() + "</strong>";
			String description = termCase.getDescription();
			HTMLNode termP = new HTMLNode("p", "", "", term + "<br>" + description);
			termDiv.addChild(termP);

			// wormatlas anatomy
			HTMLNode wormatlasAnatomyTopContainerDiv = new HTMLNode("div", "wormatlasAnatomyTopContainer", "");
			HTMLNode collapseWormatlasAnatomyButton = new HTMLNode("button", "wormatlasAnatomyCollapse",
					"wormatlasAnatomyCollapseButton", "width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+",
					true);
			HTMLNode wormatlasAnatomyTitle = new HTMLNode("p", "wormatlasAnatomyTitle",
					"width: 95%; margin-top: 2%; float: left;", "<strong> Wormatlas Anatomy: </strong>");
			wormatlasAnatomyTopContainerDiv.addChild(collapseWormatlasAnatomyButton);
			wormatlasAnatomyTopContainerDiv.addChild(wormatlasAnatomyTitle);
			HTMLNode wormatlasAnatomyDiv = new HTMLNode("div", "wormatlasAnatomy", "height: 0px; visibility: hidden;");
			HTMLNode wormatlasAnatomyUL = new HTMLNode("ul");

			String wormatlastAnchor1 = "<a href=\"#\" name=\"" + termCase.getWormatlasLink1()
			+ "\" onclick=\"handleLink(this)\">" + termCase.getWormatlasLink1() + "</a>";

			String wormatlastAnchor2 = "<a href=\"#\" name=\"" + termCase.getWormatlasLink2()
			+ "\" onclick=\"handleLink(this)\">" + termCase.getWormatlasLink2() + "</a>";

			HTMLNode li1 = new HTMLNode("li", "", "", wormatlastAnchor1);
			HTMLNode li2 = new HTMLNode("li", "", "", wormatlastAnchor2);
			wormatlasAnatomyUL.addChild(li1);
			wormatlasAnatomyUL.addChild(li2);
			wormatlasAnatomyDiv.addChild(wormatlasAnatomyUL);

			// amphid cells
			HTMLNode amphidCellsTopContainerDiv = new HTMLNode("div", "amphidCellsTopContainer", "");
			HTMLNode collapseAmphidCellsButton = new HTMLNode("button", "amphidCellsCollapse",
					"amphidCellsCollapseButton", "width: 3%; margin-top: 2%; margin-right: 1%; float: left;", "+",
					true);
			HTMLNode amphidCellsTitle = new HTMLNode("p", "amphidCellsTitle",
					"width: 95%; margin-top: 2%; float: left;", "<strong> Amphid Cells: </strong>");
			amphidCellsTopContainerDiv.addChild(collapseAmphidCellsButton);
			amphidCellsTopContainerDiv.addChild(amphidCellsTitle);
			HTMLNode amphidCellsDiv = new HTMLNode("div", "amphidCells", "height: 0px; visibility: hidden;");
			HTMLNode amphidCellsTable = new HTMLNode("table");
			HTMLNode amphidCellsTHR = new HTMLNode("tr");
			HTMLNode amphidCellsTHFuncName = new HTMLNode("th", "", "", "Functional Name");
			HTMLNode amphidCellsTHLineageName = new HTMLNode("th", "", "", "Lineage Name");
			HTMLNode amphidCellsTHDescription = new HTMLNode("th", "", "", "Description");
			amphidCellsTHR.addChild(amphidCellsTHFuncName);
			amphidCellsTHR.addChild(amphidCellsTHLineageName);
			amphidCellsTHR.addChild(amphidCellsTHDescription);
			amphidCellsTable.addChild(amphidCellsTHR);
			for (String amphidCell : termCase.getAmphidCells()) {
				HTMLNode tr = new HTMLNode("tr");

				int funcNameIdx = amphidCell.indexOf("*");
				String funcName = amphidCell.substring(0, amphidCell.indexOf("*", 0));

				int lineageNameIdx = amphidCell.indexOf("*", funcNameIdx + 1);
				String lineageName = amphidCell.substring(funcNameIdx + 1, lineageNameIdx);

				String acDescription = amphidCell.substring(lineageNameIdx + 1);

				tr.addChild(new HTMLNode("td", "", "", funcName));
				tr.addChild(new HTMLNode("td", "", "", lineageName));
				tr.addChild(new HTMLNode("td", "", "", acDescription));

				amphidCellsTable.addChild(tr);
			}
			amphidCellsDiv.addChild(amphidCellsTable);

			// links
			HTMLNode linksTopContainerDiv = new HTMLNode("div", "linksTopContainer", "width: 100%;");
			HTMLNode collapseLinksButton = new HTMLNode("button", "linksCollapse", "linksCollapseButton",
					"width: 3%; margin-top: 2%; margin-right: 2%; float: left;", "+", true);
			HTMLNode linksTitle = new HTMLNode("p", "linksTitle", "width: 95%; margin-top: 2%; float: left;",
					"<strong>External Links: </strong>");
			linksTopContainerDiv.addChild(collapseLinksButton);
			linksTopContainerDiv.addChild(linksTitle);
			HTMLNode linksDiv = new HTMLNode("div", "links", "height: 0px; visibility: hidden;");
			HTMLNode linksUL = new HTMLNode("ul");
			for (String link : termCase.getLinks()) {
				if (!link.equals("")) {
					String anchor = link; // replaced with anchor if valid link

					// begin after www.
					int startIDX = link.indexOf("www.") + 4;
					if (startIDX > 0) {
						String placeholder = link.substring(startIDX);

						// find end of site name using '.'
						int dotIDX = placeholder.indexOf(".");
						if (dotIDX > 0) {
							placeholder = placeholder.substring(0, dotIDX);

							// check for google links
							if (placeholder.equals("google")) {
								// check if wormatlas specific search
								if (link.contains("site:wormatlas.org")) {
									anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
											+ termCase.getName() + " on Google (searching Wormatlas)" + "</a>";
								} else {
									anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
											+ termCase.getName() + " on Google</a>";
								}
							} else {
								if (placeholder.toLowerCase().equals("cacr")) {
									placeholder = "Textpresso";
								}
								anchor = "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
										+ termCase.getName() + " on " + placeholder + "</a>";
							}
						}
					}
					HTMLNode li = new HTMLNode("li", "", "", anchor);
					linksUL.addChild(li);
				}
			}
			linksDiv.addChild(linksUL);

			// add divs to body
			body.addChild(termDiv);
			body.addChild(wormatlasAnatomyTopContainerDiv);
			body.addChild(wormatlasAnatomyDiv);
			body.addChild(amphidCellsTopContainerDiv);
			body.addChild(amphidCellsDiv);
			body.addChild(linksTopContainerDiv);
			body.addChild(linksDiv);

			// add collapse button scripts
			body.addChild(collapseWormatlasAnatomyButton.makeCollapseButtonScript());
			body.addChild(collapseAmphidCellsButton.makeCollapseButtonScript());
			body.addChild(collapseLinksButton.makeCollapseButtonScript());

			// add link controller
			body.addChild(body.addLinkHandlerScript());

			// add head and body to html
			html.addChild(head);
			html.addChild(body);

			// add style node
			buildStyleNode();
		}
	}

	/**
	 * 
	 * @return the dom as a string to be set to the content of a WebView
	 */
	public String DOMtoString() {
		String domAsString = doctypeTag;

		// String str = domAsString += html.formatNode();
		// System.out.println(str);
		// return str;
		return domAsString += html.formatNode();

	}

	/**
	 * Iterates through the DOM and builds the style tag add to the head node
	 * 
	 * Nodes with style are classified by their having an ID or not
	 */
	public void buildStyleNode() {
		if (html == null)
			return;

		// default style rules
		String style = newLine + "ul {" + newLine + "list-style-type: none;" + newLine + "display: block;" + newLine
				+ "width: 100%;" + newLine + "}" + newLine + newLine + "li {" + newLine + "margin-bottom: 2%;" + newLine
				+ "}" + newLine + newLine + "div {" + newLine + "width: 100%;" + newLine + "overflow: hidden;" + newLine
				+ "}" + newLine + "table, th, td {" + newLine + "border: 1px solid black;" + newLine
				+ "border-collapse: collapse;" + newLine + "}" + newLine;
		HTMLNode head = null; // saved to add style node as child of head
		if (html.hasChildren()) {
			for (HTMLNode node : html.getChildren()) {
				if (node.getTag().equals("head")) { // save head
					head = node;
				} else if (node.getTag().equals("body")) { // get style
					style += findStyleInSubTree(node);
				}

			}
		}
		addStyleNodeToHead(head, style);
	}

	/**
	 * Adds the compiled style string to a style node in the head node
	 * 
	 * @param head
	 * @param style
	 */
	private void addStyleNodeToHead(HTMLNode head, String style) {
		if (head != null) {
			head.addChild(new HTMLNode(style, "text/css"));
		}
	}

	/**
	 * called by buildStyleNode to scan the body node and extract style
	 * attributes from children - only called if node is body tag and if body
	 * has children
	 * 
	 * @param node
	 *            the body node in which to search for nodes with style
	 * @return all of the styles formatted as a string to go into a node in the
	 *         head
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

	/**
	 * Formats a style attribute for a specific node
	 * 
	 * @param node
	 *            the node to build a style attribute for
	 * @return a string representation of the style for the style node
	 */
	private String styleAsStr(HTMLNode node) {
		return (newLine + "#" + node.getID() + " {" + newLine + node.getStyle() + newLine + "}");
	}

	/**
	 * Finds all occurences of 'amphid' and replaces with a link using
	 * addAmphidLink()
	 * 
	 * @param str
	 *            the string to be searched
	 * @return the updated string with links
	 */
	private String findAndUpdateOccurencesOfAmphid(String str) {
		// find all occurences of "Amphid" and add anchor at each point
		int idx = str.toLowerCase().indexOf(AMPHID);
		while (idx != -1) {
			str = addAmphidLink(str, idx);
			idx += amphidAnchor.length();
			idx = str.toLowerCase().indexOf(AMPHID, idx);
		}

		return str;
	}

	/**
	 * Adds an <a> to an anatomy section which contains the keyword "Amphid"
	 * These links will generate a new info window page of the Amphid type
	 * 
	 * @param anatomyInfo
	 *            the section which the link will be added to
	 * @return updated anatomyInfo with link
	 */
	private String addAmphidLink(String anatomyInfo, int idx) {
		if (idx == -1)
			return anatomyInfo;

		String updated = anatomyInfo.substring(0, idx) + amphidAnchor + anatomyInfo.substring(idx + AMPHID.length());

		return updated;
	}

	public HTMLNode getHTML() {
		return this.html;
	}

	public String getName() {
		return this.name;
	}
	
	private final static String terminalCellClassName = "wormguides.model.TerminalCellCase";
	private final static String nonTerminalCellClassName = "wormguides.model.NonTerminalCellCase";
	private final static String AMPHID = "amphid";
	private final static String amphidAnchor = "<a href=\"#\" onclick=\"handleAmphidClick()\">Amphid</a>";
	private final static String doctypeTag = "<!DOCTYPE html>";
	private final static String newLine = "\n";
	// private final static String meta_charset = "<meta charset=\"utf-8\">";
	// private final static String meta_httpequiv_content = "<meta
	// http-equiv=\"X-UA-Compatible\" content=\"WormGUIDES, MSKCC, Zhirong
	// Bao\">";
}
