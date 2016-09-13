package wormguides.models;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import partslist.PartsList;

/**
 * A terminal cell object which contains the information for the Information Window feature
 * 
 * @author katzmanb
 *
 */
public class TerminalCellCase extends CellCase {

    private final static String graphicURL = "http://www.wormatlas.org/neurons/Images/";
    private final static String graphicURLRange = "http://www.wormatlas.org/neurons/Images/";
    private final static String jpgEXT = ".jpg";
    private final static String wormatlasURL = "http://www.wormatlas.org/neurons/Individual%20Neurons/";
    private final static String wormatlasURLEXT = "mainframe.htm";
    //private final static String wormatlasURLEXT2 = "frameset.html";
    private final static String wormwiringBaseURL = "http://wormwiring.hpc.einstein.yu.edu/data/neuronData.php?name=";
    private final static String wormwiringN2UEXT = "&db=N2U";
    private String funcName;
	private String externalInfo;
	private String partsListDescription;
	private String imageURL;
	private String functionWORMATLAS;
	private ArrayList<String> presynapticPartners;
	private ArrayList<String> postsynapticPartners;
	private ArrayList<String> electricalPartners;
	private ArrayList<String> neuromuscularPartners;
	private boolean hasAnatomy;
	private ArrayList<String> anatomy;
	private ArrayList<ArrayList<String>> homologues; //homologues[0] will contain L/R homologues, homologues[1] will contain additional symmetries

	/**
     *
     * @param lineageName
     * @param cellName functional name
	 * @param presynapticPartners
	 * @param postsynapticPartners
	 * @param electricalPartners
	 * @param neuromuscularPartners
	 * @param nuclearProductionInfo information from the production file under Nuclear
	 * @param cellShapeProductionInfo information from the production file under Cell Shape
	 */
    public TerminalCellCase(
            String lineageName, String cellName, ArrayList<String> presynapticPartners,
            ArrayList<String> postsynapticPartners, ArrayList<String> electricalPartners,
            ArrayList<String> neuromuscularPartners, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {

        super(lineageName, nuclearProductionInfo, cellShapeProductionInfo);

        this.funcName = cellName;
		this.externalInfo = this.funcName + " (" + lineageName + ")";

		this.partsListDescription = PartsList.getDescriptionByLineageName(lineageName);

        if (Character.isDigit(cellName.charAt(cellName.length() - 1))){
			this.imageURL = graphicURL + cellName.toUpperCase() + jpgEXT;
		} else {
            this.imageURL = graphicURL + cellName.toLowerCase() + jpgEXT;
        }

		//parse wormatlas for the "Function" field, also set image field
		this.functionWORMATLAS = setFunctionFromWORMATLAS();

		//set the wiring partners from connectome
		this.presynapticPartners = presynapticPartners;
		this.postsynapticPartners = postsynapticPartners;
		this.electricalPartners = electricalPartners;
		this.neuromuscularPartners = neuromuscularPartners;

		this.hasAnatomy = Anatomy.hasAnatomy(this.funcName);
		if (hasAnatomy) {
			setAnatomy();
		}

		this.homologues = setHomologues();

		//generate and add the wormwiring link
		addLink(addWormWiringLink());


		/*
		 * TODO
		 * cytoshow stub
		 */
		//links.add("Cytoshow: [cytoshow link to this cell in EM data]");
	}
	
	/**
	 * Finds the wormatlas page corresponding to this cell and parses its html for the 'Function' section, which it then pulls
     *
     * @return the "Function" section of html from wormatlas.org
	 */
	private String setFunctionFromWORMATLAS() {
		if (this.funcName == null) return "";

		String content = "";
		URLConnection connection = null;

		/*
         * USING mainframe.htm EXT
		 * Leaving code for frameset.htm check
		 */

		/*
         * if R/L cell, find base name for URL
		 * e.g. ribr --> RIB
		 *
		 * if no R/L, leave as is
		 * e.g. AVG
		 */
		String cell = this.funcName;
		Character lastChar = cell.charAt(cell.length()-1);
		lastChar = Character.toLowerCase(lastChar);
		if (lastChar == 'r' || lastChar == 'l') {
			cell = cell.substring(0, cell.length()-1);

			//check if preceding d/v
			lastChar = cell.charAt(cell.length()-1);
			lastChar = Character.toLowerCase(lastChar);
			if (lastChar == 'd' || lastChar == 'v') {
				cell = cell.substring(0, cell.length()-1);
			}
		} else if (lastChar == 'd' || lastChar == 'v') { //will l/r ever come before d/v
			cell = cell.substring(0, cell.length()-1);

            //check if preceding l/r
			lastChar = cell.charAt(cell.length()-1);
			lastChar = Character.toLowerCase(lastChar);
			if (lastChar == 'l' || lastChar == 'r') {
				cell = cell.substring(0, cell.length()-1);
			}
		} else if (Character.isDigit(lastChar)) {
			cell = cell.substring(0, cell.length()-1).toUpperCase() + "N";
		}

        String URL = wormatlasURL + cell.toUpperCase() + wormatlasURLEXT;
		try {
			connection = new URL(URL).openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
//			//try second extension
//			URL = wormatlasURL + cell + wormatlasURLEXT;
//			System.out.println("TRYING SECOND URL: " + URL);
//
//			try {
//				connection = new URL(URL).openConnection();
//				Scanner scanner = new Scanner(connection.getInputStream());
//				scanner.useDelimiter("\\Z");
//				content = scanner.next();
//				scanner.close();
//			} catch (Exception e1) {
//				//e1.printStackTrace();
//				//a page wasn't found on wormatlas
//				return this.cellName + " page not found on Wormatlas";
//			}
			//e1.printStackTrace();
			//a page wasn't found on wormatlas
			return null;
		}

        //find the image src in the html and set the imageURL
		findImageURLInHTML(content);

        return findFunctionInHTML(content, URL);
	}
	
	private void findImageURLInHTML(String content) {
		//find all instances of '/Images/
		ArrayList<String> images = new ArrayList<String>();
		String quotes = "\"";
		String findStr = "/Images/";
		int lastIdx = 0;
		int closeQuoteIdx = 0;

        while (lastIdx != -1) {
			lastIdx = content.indexOf(findStr, lastIdx);

            if (lastIdx != -1) {
				//find the index of the closing quotes for the image url
                closeQuoteIdx = content.indexOf(quotes, lastIdx);

				//add the url to the list
				images.add(content.substring(lastIdx, closeQuoteIdx));

                //move lastIdx past just processed image url
				lastIdx += findStr.length();
			}
		}

        //look for matches, first check if funcName ends with number
		boolean cellWithNum = Character.isDigit(funcName.charAt(funcName.length()-1));
		for (String url : images) {
			String imageName = url.substring(findStr.length(), url.indexOf("."));

            if (imageName.toLowerCase().equals(funcName.toLowerCase())) {
				if (cellWithNum) {
					this.imageURL = graphicURL + funcName.toUpperCase() + jpgEXT;
					return;
				} else {
					this.imageURL = graphicURL + funcName.toLowerCase() + jpgEXT;
					return;
				}

            }

            //if funcName ends with number, check if the image has a range of numbers e.g. DA3-7, or if two consecutive images form a range
			if (cellWithNum) {
				//find the base name of the cell
				String baseName = "";
				for (int i = 0; i < funcName.length(); i++) {
					if (!Character.isDigit(funcName.charAt(i))) {
						baseName += funcName.charAt(i);
					}
				}

                //extract the number for this cell
				int num = Character.getNumericValue(funcName.charAt(funcName.length()-1));

                //check if the image has a range
				if (imageName.contains("-")) {
					int upperBound = Integer.parseInt(imageName.substring(imageName.indexOf("-")+1));
					int lowerBound = Character.getNumericValue(imageName.charAt(imageName.indexOf("-")-1));

                    //check if the base name matches the image url and falls within the range
					if (imageName.toLowerCase().startsWith(baseName.toLowerCase())) {

                        if (num >= lowerBound && num <= upperBound) {
							this.imageURL = graphicURLRange + imageName + jpgEXT;
							return;
						}
					}
				}

                //check if the two images form a range which this cell falls in i.e. wormatlas will use da3.jpg to represent da3-7 --> check for da3 and da8
				else if (images.indexOf(url) != images.size()-1) { //make sure there is another entry in the list
					String url2 = images.get(images.indexOf(url)+1);
					String imageName2 = url2.substring(findStr.length(), url2.indexOf("."));

                    //see if both images are for the same cell
					if (imageName.toLowerCase().startsWith(baseName.toLowerCase()) && imageName2.toLowerCase().startsWith(baseName.toLowerCase())) {
						//find the range formed by the two images
						int lowerBound = Character.getNumericValue(imageName.charAt(imageName.length()-1));
						int upperBound = Character.getNumericValue(imageName2.charAt(imageName2.length()-1));

                        //check if num is between the range
						if (num > lowerBound && num < upperBound) {
							this.imageURL = graphicURLRange + imageName + jpgEXT;
							return;
                        }
                    }
				}
			}
		}
	}
	
	/**
     *
     * @param content the full html page from wormatlas
	 * @param URL the full url for the page to add to to this cell case's external link
	 * @return the 'Function' section from the wormatlas page
	 */
	private String findFunctionInHTML(String content, String URL) {
		//parse the html for "Function"
		content = content.substring(content.indexOf("Function"));
		content = content.substring(content.indexOf(":")+1, content.indexOf("</td>")); //skip the "Function:" text

		content = updateAnchors(content);

		//add the link to the list
		/* links.add(URL); */

        return content + "<br><em>Source: </em><a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a>" ;
	}
	
	/**
	 * Sets the anatomy information is applicable to this cell
	 */
	private void setAnatomy() {
		if (this.funcName == null) return;

        if (Anatomy.hasAnatomy(this.funcName)) {
            this.anatomy = Anatomy.getAnatomy(this.funcName);
        }
	}
	
	/**
	 * Searches the parts list lineage names and finds matching prefixes to the query cell
     *
     * @return the list of homologues for this cell
	 */
	private ArrayList<ArrayList<String>> setHomologues() {
		ArrayList<ArrayList<String>> homologues = new ArrayList<ArrayList<String>>();
		ArrayList<String> leftRightHomologues = new ArrayList<String>();
		ArrayList<String> additionalSymmetries = new ArrayList<String>();

        if (this.funcName == null) return homologues;

        char lastChar = funcName.charAt(funcName.length()-1);
		lastChar = Character.toLowerCase(lastChar);

        String cell = this.funcName;
		//check for left, right, dorsal, or ventral suffix --> update cell
		if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
			//check if multiple suffixes
			lastChar = funcName.charAt(funcName.length()-2);
			lastChar = Character.toLowerCase(lastChar);
			if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
				cell = cell.substring(0, cell.length()-2);
			} else {
				cell = cell.substring(0, cell.length()-1);
			}
		} else if (Character.isDigit(lastChar)) { //check for # e.g. DD1 --> update cell
			//check if double digit
			if (Character.isDigit(funcName.length()-2)) {
				cell = cell.substring(0, cell.length()-2);
			} else {
				cell = cell.substring(0, cell.length()-1);
			}

        } else { //if no suffix, no homologues e.g. AVG, M
			return homologues;
		}

        cell = cell.toLowerCase();

        //search parts list for matching prefix terms
		ArrayList<String> partsListHits = new ArrayList<String>();
		for (String lineageName : PartsList.getLineageNames()) {
			//GET BASE NAME FOR LINEAGE NAME AS DONE ABOVE WITH CELL NAME
			lineageName = PartsList.getFunctionalNameByLineageName(lineageName);

			if (lineageName.toLowerCase().startsWith(cell)) {
				partsListHits.add(lineageName);
			}
		}

		/*
         * Add hits to categories:
		 * L/R: ends with l/r
		 * AdditionalSymm: ends with d/v
		 *
		 * NOTE:
		 * RIAL will show as L/R homologue to RIVL because there is not currently
		 * logic that remembers what was peeled off the original base name
		 */
		for (String lineageName : partsListHits) {

            //the base name of the cell
			String base = lineageName;

            lastChar = base.charAt(base.length()-1);
			lastChar = Character.toLowerCase(lastChar);
			//check for left, right, dorsal, or ventral suffix --> update cell
			if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
				//check if multiple suffixes
				lastChar = base.charAt(base.length()-2);
				lastChar = Character.toLowerCase(lastChar);
				if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
					base = base.substring(0, base.length()-2);
				} else {
					base = base.substring(0, base.length()-1);
				}
			} else if (Character.isDigit(lastChar)) { //check for # e.g. DD1 --> update cell
				//check if double digit
				if (Character.isDigit(base.length()-2)) {
					base = base.substring(0, base.length()-2);
				} else {
					base = base.substring(0, base.length()-1);
				}

            } else {}

            lastChar = lineageName.charAt(lineageName.length()-1);
			lastChar = Character.toLowerCase(lastChar);
			if (lastChar == 'l' || lastChar == 'r') {
				if (base.toLowerCase().equals(cell)) {
					leftRightHomologues.add(lineageName);
				}
			} else if (lastChar == 'd' || lastChar == 'v' || Character.isDigit(lastChar)) {
				if (base.toLowerCase().equals(cell)) {
					additionalSymmetries.add(lineageName);
				}
			}
		}

        //remove self from lists
		if (leftRightHomologues.contains(this.funcName)) {
			leftRightHomologues.remove(this.funcName);
		}

        if (additionalSymmetries.contains(this.funcName)) {
			additionalSymmetries.remove(this.funcName);
		}

        homologues.add(leftRightHomologues);
		homologues.add(additionalSymmetries);

        return homologues;
	}
	
	/* TODO */
	private String addWormWiringLink() {
		if (this.funcName != null) {
			String cell = this.funcName;
            //check if N2U, n2y or n930 image series
//			boolean N2U = true;
//			boolean N2Y = false;
//			boolean N930 = false;

            //need to zero pad in link generation
			char lastChar = funcName.charAt(funcName.length()-1);
			if (Character.isDigit(lastChar)) {
				for (int i = 0; i < funcName.length(); i++) {
					if (Character.isDigit(funcName.charAt(i))) {
						if (i != 0) { //error check
							cell = funcName.substring(0, i) + "0" + funcName.substring(i);
						}
					}
				}
			}

            return wormwiringBaseURL + cell.toUpperCase() + wormwiringN2UEXT;

//			if (N2U) {
//				return wormwiringBaseURL + cell.toUpperCase() + wormwiringN2UEXT;
//			} else if (N2Y) {
//				return wormwiringBaseURL + cell.toUpperCase() + wormwiringN2YEXT;
//			} else if (N930) {
//				return wormwiringBaseURL + cell.toUpperCase() + wormwiringN930EXT;
//			}
		}

        return "";
	}

    public String getCellName() {
		if (this.funcName != null) {
			return this.funcName;
		}
		return "";
	}
	
	public String getExternalInfo() {
		if (this.externalInfo != null) {
			return this.externalInfo;
		}
		return "";
	}
	
	public String getPartsListDescription() {
		if (this.partsListDescription != null) {
			return this.partsListDescription;
		}
		return "";
	}
	
	public String getImageURL() {
		if (this.imageURL != null) {
			return this.imageURL;
		}
		return "";
	}
	
	public String getFunctionWORMATLAS() {
		if (this.functionWORMATLAS != null) {
			return this.functionWORMATLAS;
		}
		return "";
	}
	
	public boolean getHasAnatomyFlag() {
		return this.hasAnatomy;
	}

    public ArrayList<String> getAnatomy() {
		if (this.hasAnatomy) {
			return this.anatomy;
		}
		return null;
	}

    public ArrayList<String> getPresynapticPartners() {
		return this.presynapticPartners;
	}

    public ArrayList<String> getPostsynapticPartners() {
		return this.postsynapticPartners;
	}

    public ArrayList<String> getElectricalPartners() {
		return this.electricalPartners;
	}

    public ArrayList<String> getNeuromuscularPartners() {
		return this.neuromuscularPartners;
	}

    public ArrayList<ArrayList<String>> getHomologues() {
		return homologues;
	}
}


//package wormguides.model;
//
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.Scanner;
//
///**
// * A terminal cell object which contains the information for the Information Window feature
// * 
// * @author katzmanb
// *
// */
//public class TerminalCellCase {
//	
//	private String funcName;
//	private String lineageName; //shared
//	private String externalInfo;
//	private String partsListDescription;
//	private String imageURL;
//	private String functionWORMATLAS;
//	
//	private ArrayList<String> presynapticPartners;
//	private ArrayList<String> postsynapticPartners;
//	private ArrayList<String> electricalPartners;
//	private ArrayList<String> neuromuscularPartners;
//	
//	private boolean hasAnatomy;
//	private ArrayList<String> anatomy;
//	private ArrayList<String> geneExpression; //shared
//	private ArrayList<ArrayList<String>> homologues; //homologues[0] will contain L/R homologues, homologues[1] will contain additional symmetries
//	private ArrayList<String> references; //shared
//	private ArrayList<String> links; //shared
//	private ArrayList<String> nuclearProductionInfo; //shared
//	private ArrayList<String> cellShapeProductionInfo; //shared
//	
//	/**
//	 * 
//	 * @param lineageName 
//	 * @param cellName functional name
//	 * @param presynapticPartners
//	 * @param postsynapticPartners
//	 * @param electricalPartners
//	 * @param neuromuscularPartners
//	 * @param nuclearProductionInfo information from the production file under Nuclear
//	 * @param cellShapeProductionInfo information from the production file under Cell Shape
//	 */
//	public TerminalCellCase(String lineageName, String cellName, ArrayList<String> presynapticPartners, 
//			ArrayList<String> postsynapticPartners,ArrayList<String> electricalPartners, 
//			ArrayList<String> neuromuscularPartners, ArrayList<String> nuclearProductionInfo,
//			ArrayList<String> cellShapeProductionInfo) {
//		
//		// TODO buildlinks method
//		this.links = new ArrayList<String>();
//		
//		this.lineageName = lineageName;
//		
//		this.funcName = cellName;
//		this.externalInfo = this.funcName + " (" + lineageName + ")";
//		
//		this.partsListDescription = PartsList.getDescriptionByLineageName(lineageName);
//		
//		if (Character.isDigit(cellName.charAt(cellName.length() - 1))){
//			this.imageURL = graphicURL + cellName.toUpperCase() + jpgEXT;
//		} else {
//			this.imageURL = graphicURL + cellName.toLowerCase() + jpgEXT; 
//		}
//		
//		//parse wormatlas for the "Function" field
//		this.functionWORMATLAS = setFunctionFromWORMATLAS();
//		
//		//set the wiring partners from connectome
//		this.presynapticPartners = presynapticPartners;
//		this.postsynapticPartners = postsynapticPartners;
//		this.electricalPartners = electricalPartners;
//		this.neuromuscularPartners = neuromuscularPartners;
//		
//		this.hasAnatomy = Anatomy.hasAnatomy(this.funcName);
//		if (hasAnatomy) {
//			setAnatomy();
//		}
//		this.geneExpression = setExpressionsFromWORMBASE();
//		this.homologues = setHomologues();
//		this.references = setReferences();
//		this.nuclearProductionInfo = nuclearProductionInfo;
//		this.cellShapeProductionInfo = cellShapeProductionInfo;
//
//		links.add(addWormWiringLink());
//		links.add(addGoogleLink());
//		links.add(addGoogleWormatlasLink());
//		
//		
//		/*
//		 * TODO
//		 * cytoshow stub
//		 */
//		links.add("Cytoshow: [cytoshow link to this cell in EM data]");
//	}
//	
//	public String getLineageName() {
//		return lineageName;
//	}
//	
//	/**
//	 * Finds the wormatlas page corresponding to this cell and parses its html for the 'Function' section, which it then pulls
//	 * 
//	 * @return the "Function" section of html from wormatlas.org
//	 */
//	private String setFunctionFromWORMATLAS() {
//		if (this.funcName == null) return "";
//		
//		String content = "";
//		URLConnection connection = null;
//		
//		/* 
//		 * USING mainframe.htm EXT
//		 * Leaving code for frameset.htm check
//		 */
//		
//		/*
//		 * if R/L cell, find base name for URL
//		 * e.g. ribr --> RIB
//		 * 
//		 * if no R/L, leave as is
//		 * e.g. AVG
//		 */
//		String cell = this.funcName;
//		Character lastChar = cell.charAt(cell.length()-1);
//		lastChar = Character.toLowerCase(lastChar);
//		if (lastChar == 'r' || lastChar == 'l') {
//			cell = cell.substring(0, cell.length()-1);
//			
//			//check if preceding d/v
//			lastChar = cell.charAt(cell.length()-1);
//			lastChar = Character.toLowerCase(lastChar);
//			if (lastChar == 'd' || lastChar == 'v') {
//				cell = cell.substring(0, cell.length()-1);
//			}
//		} else if (lastChar == 'd' || lastChar == 'v') { //will l/r ever come before d/v
//			cell = cell.substring(0, cell.length()-1);
//			
//			//check if preceding l/r
//			lastChar = cell.charAt(cell.length()-1);
//			lastChar = Character.toLowerCase(lastChar);
//			if (lastChar == 'l' || lastChar == 'r') {
//				cell = cell.substring(0, cell.length()-1);
//			}
//		} else if (Character.isDigit(lastChar)) {
//			cell = cell.substring(0, cell.length()-1).toUpperCase() + "N";
//		}
//		
//		String URL = wormatlasURL + cell.toUpperCase() + wormatlasURLEXT;
//		try {
//			connection = new URL(URL).openConnection();
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//		} catch (Exception e) {
////			//try second extension
////			URL = wormatlasURL + cell + wormatlasURLEXT;
////			System.out.println("TRYING SECOND URL: " + URL);
////
////			try {
////				connection = new URL(URL).openConnection();
////				Scanner scanner = new Scanner(connection.getInputStream());
////				scanner.useDelimiter("\\Z");
////				content = scanner.next();
////				scanner.close();
////			} catch (Exception e1) {
////				//e1.printStackTrace();
////				//a page wasn't found on wormatlas
////				return this.cellName + " page not found on Wormatlas";
////			}
//			//e1.printStackTrace();
//			//a page wasn't found on wormatlas
//			return null;
//		}
//		return findFunctionInHTML(content, URL);
//	}
//	
//	/**
//	 * 
//	 * @param content the full html page from wormatlas
//	 * @param URL the full url for the page to add to to this cell case's external link
//	 * @return the 'Function' section from the wormatlas page
//	 */
//	private String findFunctionInHTML(String content, String URL) {
//		//parse the html for "Function"
//		content = content.substring(content.indexOf("Function"));
//		content = content.substring(content.indexOf(":")+1, content.indexOf("</td>")); //skip the "Function:" text
//
//		content = updateAnchors(content);
//
//		//add the link to the list
//		links.add(URL);
//		
//		return "<em>Source: </em><a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a><br><br>" + content;
//	}
//	
//	/**
//	 * Sets the anatomy information is applicable to this cell
//	 */
//	private void setAnatomy() {
//		if (this.funcName == null) return;
//		
//		if (Anatomy.hasAnatomy(this.funcName)) {
//			this.anatomy = Anatomy.getAnatomy(this.funcName); 
//		}
//	}
//	
//	/**
//	 * 
//	 * @return the list of gene expressions from the wormbase page corresponding to this cell
//	 */
//	private ArrayList<String> setExpressionsFromWORMBASE() {
//		ArrayList<String> geneExpression = new ArrayList<String>();
//		
//		if (funcName == null)
//			return geneExpression;
//
//		String URL = wormbaseURL + funcName + wormbaseEXT;
//		
//		String content = "";
//		URLConnection connection = null;
//		
//		try {
//			connection = new URL(URL).openConnection();			
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//			
//		} catch (Exception e) {
//			//e.printStackTrace();
//			//a page wasn't found on wormatlas
//			System.out.println(funcName + " page not found on Wormbase");
//			return geneExpression;
//		}
//		
//		//add the link to the list before parsing with cytoshow snippet (first link is more human readable)
//		links.add(URL);
//		
//		/*
//		 * Snippet adapted from cytoshow
//		 */
//		String[] logLines = content.split("wname=\"associations\"");
//		String restString = "";
//		if (logLines != null && logLines.length > 1 && logLines[1].split("\"").length > 1) {
//			restString = logLines[1].split("\"")[1];
//		}
//		
//		URL = "http://www.wormbase.org" + restString;
//		
//		try {
//			connection = new URL(URL).openConnection();			
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//		} catch (Exception e) {
//			//e.printStackTrace();
//			//a page wasn't found on wormatlas
//			System.out.println(this.funcName + " page not found on Wormbase (second URL)");
//			
//			//remove the link
//			for (int i = 0; i < links.size(); i++) {
//				if (links.get(i).startsWith(wormbaseURL)) {
//					links.remove(i);
//				}
//					
//			}
//			
//			return geneExpression;
//		}
//		
//		//extract expressions
//		String[] genes = content.split("><");
//		for (String gene : genes) {
//			if (gene.startsWith("span class=\"locus\"")) {
//				gene = gene.substring(gene.indexOf(">")+1, gene.indexOf("<"));
//				geneExpression.add(gene);
//			}
//		}
//		
//		return geneExpression;
//	}
//	
//	/**
//	 * Searches the parts list lineage names and finds matching prefixes to the query cell
//	 * 
//	 * @return the list of homologues for this cell
//	 */
//	private ArrayList<ArrayList<String>> setHomologues() {
//		ArrayList<ArrayList<String>> homologues = new ArrayList<ArrayList<String>>();
//		ArrayList<String> leftRightHomologues = new ArrayList<String>();
//		ArrayList<String> additionalSymmetries = new ArrayList<String>();
//		
//		if (this.funcName == null) return homologues;
//		
//		char lastChar = funcName.charAt(funcName.length()-1);
//		lastChar = Character.toLowerCase(lastChar);
//		
//		String cell = this.funcName;
//		//check for left, right, dorsal, or ventral suffix --> update cell
//		if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
//			//check if multiple suffixes
//			lastChar = funcName.charAt(funcName.length()-2);
//			lastChar = Character.toLowerCase(lastChar);
//			if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
//				cell = cell.substring(0, cell.length()-2);
//			} else {
//				cell = cell.substring(0, cell.length()-1);
//			}
//		} else if (Character.isDigit(lastChar)) { //check for # e.g. DD1 --> update cell
//			//check if double digit
//			if (Character.isDigit(funcName.length()-2)) {
//				cell = cell.substring(0, cell.length()-2);
//			} else {
//				cell = cell.substring(0, cell.length()-1);
//			}
//			
//		} else { //if no suffix, no homologues e.g. AVG, M
//			return homologues;
//		}
//		
//		cell = cell.toLowerCase();
//		
//		//search parts list for matching prefix terms
//		ArrayList<String> partsListHits = new ArrayList<String>();
//		for (String lineageName : PartsList.getLineageNames()) {
//			//GET BASE NAME FOR LINEAGE NAME AS DONE ABOVE WITH CELL NAME
//			lineageName = PartsList.getFunctionalNameByLineageName(lineageName);
//
//			if (lineageName.toLowerCase().startsWith(cell)) {
//				partsListHits.add(lineageName);
//			}
//		}
//		
//		/*
//		 * Add hits to categories:
//		 * L/R: ends with l/r
//		 * AdditionalSymm: ends with d/v
//		 * 
//		 * NOTE:
//		 * RIAL will show as L/R homologue to RIVL because there is not currently 
//		 * logic that remembers what was peeled off the original base name
//		 */
//		for (String lineageName : partsListHits) {
//			
//			//the base name of the cell
//			String base = lineageName;
//			
//			lastChar = base.charAt(base.length()-1);
//			lastChar = Character.toLowerCase(lastChar);
//			//check for left, right, dorsal, or ventral suffix --> update cell
//			if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
//				//check if multiple suffixes
//				lastChar = base.charAt(base.length()-2);
//				lastChar = Character.toLowerCase(lastChar);
//				if (lastChar == 'l' || lastChar == 'r' || lastChar == 'd' || lastChar == 'v' || lastChar == 'a' || lastChar == 'p') {
//					base = base.substring(0, base.length()-2);
//				} else {
//					base = base.substring(0, base.length()-1);
//				}
//			} else if (Character.isDigit(lastChar)) { //check for # e.g. DD1 --> update cell
//				//check if double digit
//				if (Character.isDigit(base.length()-2)) {
//					base = base.substring(0, base.length()-2);
//				} else {
//					base = base.substring(0, base.length()-1);
//				}
//				
//			} else {}
//			
//			lastChar = lineageName.charAt(lineageName.length()-1);
//			lastChar = Character.toLowerCase(lastChar);
//			if (lastChar == 'l' || lastChar == 'r') {
//				if (base.toLowerCase().equals(cell)) {
//					leftRightHomologues.add(lineageName);
//				}
//			} else if (lastChar == 'd' || lastChar == 'v' || Character.isDigit(lastChar)) {
//				if (base.toLowerCase().equals(cell)) {
//					additionalSymmetries.add(lineageName);
//				}
//			}
//		}
//		
//		homologues.add(leftRightHomologues);
//		homologues.add(additionalSymmetries);
//		
//		return homologues;
//	}
//	
//	/**
//	 * Finds the number of matches and documents for this cell on texpresso, and the first page of results
//	 *  
//	 * @return the number of matches, documents, and first page of results
//	 */
//	private ArrayList<String> setReferences() {
//		ArrayList<String> references = new ArrayList<String>();
//		
//		//open connection with the textpresso page
//		String URL = textpressoURL + this.lineageName + textpressoURLEXT;
//				
//		String content = "";
//		URLConnection connection = null;
//		
//		try {
//			connection = new URL(URL).openConnection();			
//			Scanner scanner = new Scanner(connection.getInputStream());
//			scanner.useDelimiter("\\Z");
//			content = scanner.next();
//			scanner.close();
//		} catch (Exception e) {
//			//e.printStackTrace();
//			//a page wasn't found on wormatlas
//			System.out.println(this.funcName + " page not found on Textpresso");
//			return geneExpression;
//		}
//		
//		int matchesIDX = content.indexOf(" matches found in </span><span style=\"font-weight:bold;\">");
//		
//		if (matchesIDX > 0) {
//			matchesIDX--; //move back to the first digit
//			//find the start of the number of matches
//			String matchesStr = "";
//			for (;; matchesIDX--) {
//				char curr = content.charAt(matchesIDX);
//				if (Character.isDigit(curr)) {
//					matchesStr += curr;
//				} else {
//					break;
//				}
//			}
//			//reverse the string
//			matchesStr = new StringBuffer(matchesStr).reverse().toString();
//			
//			//find the number of documents
//			int documentsIDX = content.indexOf(" matches found in </span><span style=\"font-weight:bold;\">")+57;
//			
//			String documentsStr = "";
//			for (;; documentsIDX++) {
//				char curr = content.charAt(documentsIDX);
//				if (Character.isDigit(curr)) {
//					documentsStr += curr;
//				} else {
//					break;
//				}
//			}
//			
//			//add matches and documents to top of references list
//			references.add("<em>Textpresso</em>: " + matchesStr + " matches found in " + documentsStr + " documents");
//			/*
//			 * TODO
//			 * add textpresso url to page with open in browser
//			 */
//			
//			//parse the document for "Title: "
//			int lastIDX = 0;
//			while (lastIDX != -1) {
//				lastIDX = content.indexOf(textpressoTitleStr, lastIDX);
//				
//				if (lastIDX != -1) {
//					lastIDX += textpressoTitleStr.length(); //skip the title just seen
//					
//					//extract the title
//					String title = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
//					
//					//move the index past the authors section
//					while (!content.substring(lastIDX).startsWith(textpressoAuthorsStr)) lastIDX++;
//					
//					lastIDX += textpressoAuthorsStr.length();
//					
//					//extract the authors
//					String authors = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
//					
//					
//					//move the index past the year section
//					while (!content.substring(lastIDX).startsWith(textpressoYearStr)) lastIDX++;
//					
//					lastIDX += textpressoYearStr.length();
//					
//					//extract the year
//					String year = content.substring(lastIDX, content.indexOf("<br />", lastIDX));
//					
//					String reference = title + authors + ", " + year;
//					
//					//update the anchors
//					reference = updateAnchors(reference);
//					
//					references.add(reference);
//				}
//			}
//		}
//		
//		//add the source
//		String source = "<em>Source:</em> <a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL + "</a>";
//		references.add(source);
//		
//		links.add(URL);
//		return references;
//	}
//	
//	private String addGoogleLink() {
//		if (this.funcName != null) {
//			return googleURL + this.funcName + "+c.+elegans";
//		}
//		
//		return "";
//	}
//	
//	private String addGoogleWormatlasLink() {
//		if (this.funcName != null) {
//			return googleWormatlasURL + this.funcName;
//		}
//		
//		return "";
//	}
//	
//	private String addWormWiringLink() {
//		if (this.funcName != null) {
//			String cell = this.funcName;
//			//check if N2U, n2y or n930 image series 
////			boolean N2U = true;
////			boolean N2Y = false;
////			boolean N930 = false;
//			
//			//need to zero pad in link generation
//			char lastChar = funcName.charAt(funcName.length()-1);
//			if (Character.isDigit(lastChar)) {
//				for (int i = 0; i < funcName.length(); i++) {
//					if (Character.isDigit(funcName.charAt(i))) {
//						if (i != 0) { //error check
//							cell = funcName.substring(0, i) + "0" + funcName.substring(i);
//						}
//					}
//				}
//			}
//			
//			return wormwiringBaseURL + cell.toUpperCase() + wormwiringN2UEXT;
//			
////			if (N2U) {
////				return wormwiringBaseURL + cell.toUpperCase() + wormwiringN2UEXT;
////			} else if (N2Y) {
////				return wormwiringBaseURL + cell.toUpperCase() + wormwiringN2YEXT;
////			} else if (N930) {
////				return wormwiringBaseURL + cell.toUpperCase() + wormwiringN930EXT;
////			}
//		}
//		
//		return "";
//	}
//	
//	private String updateAnchors(String content) {
//		/*
//		 * find the anchor tags and change to:
//		 *  "<a href=\"#\" name=\"" + link + "\" onclick=\"handleLink(this)\">"
//		 *  paradigm
//		 */
//		String findStr = "<a ";
//		int lastIdx = 0;
//		
//		while (lastIdx != -1) {
//			lastIdx = content.indexOf(findStr, lastIdx);
//
//			//check if another anchor found
//			if (lastIdx != -1) {
//				//save the string preceding the anchor
//				String precedingStr = content.substring(0, lastIdx);
//				
//				
//				//find the end of the anchor and extract the anchor
//				int anchorEndIdx = content.indexOf(anchorClose, lastIdx);
//				String anchor = content.substring(lastIdx, anchorEndIdx + anchorClose.length());
//				
//				//extract the source href --> "href=\""
//				boolean isLink = true;
//				int startSrcIdx = anchor.indexOf(href) + href.length();
//				
//				String src = "";
//				//make sure not a citation i.e. first character is '#'
//				if (anchor.charAt(startSrcIdx) == '#') {
//					isLink = false;
//				} else {
//					src = anchor.substring(startSrcIdx, anchor.indexOf("\"", startSrcIdx));
//				}
//				
//				if (isLink) {
//					//check if relative src
//					if (!src.contains("www.") && !src.contains("http")) {
//						//remove path
//						if (src.contains("..")) {
//							src = src.substring(src.lastIndexOf("/") + 1);
//						}
//						src = wormatlasURL + src;
//					}
//					
//					//extract the anchor text --> skip over the first <
//					String text = anchor.substring(anchor.indexOf(">") + 1, anchor.substring(1).indexOf("<") + 1);
//					
//					// build new anchor
//					String newAnchor = "<a href=\"#\" name=\"" + src + "\" onclick=\"handleLink(this)\">" + text + "</a>";
//
//					
//					//replace previous anchor
//					content = precedingStr + newAnchor + content.substring(anchorEndIdx + anchorClose.length());
//				} else {
//					//remove anchor
//					String txt = anchor.substring(anchor.indexOf(">") + 1, anchor.substring(1).indexOf("<") + 1);
//					
//					content = precedingStr + txt + content.substring(anchorEndIdx + anchorClose.length());
//				}
//				
//				
//				//move lastIdx past just processed anchor
//				lastIdx += findStr.length();
//			}
//		}
//		
//		return content;
//	}
//
//	public String getCellName() {
//		if (this.funcName != null) {
//			return this.funcName;
//		}
//		return "";
//	}
//	
//	public String getExternalInfo() {
//		if (this.externalInfo != null) {
//			return this.externalInfo;
//		}
//		return "";
//	}
//	
//	public String getPartsListDescription() {
//		if (this.partsListDescription != null) {
//			return this.partsListDescription;
//		}
//		return "";
//	}
//	
//	public String getImageURL() {
//		if (this.imageURL != null) {
//			return this.imageURL;
//		}
//		return "";
//	}
//	
//	public String getFunctionWORMATLAS() {
//		if (this.functionWORMATLAS != null) {
//			return this.functionWORMATLAS;
//		}
//		return "";
//	}
//	
//	public boolean getHasAnatomyFlag() {
//		return this.hasAnatomy;
//	}
//	
//	public ArrayList<String> getAnatomy() {
//		if (this.hasAnatomy) {
//			return this.anatomy;
//		}
//		return null;
//	}
//	
//	public ArrayList<String> getPresynapticPartners() {
//		return this.presynapticPartners;
//	}
//	
//	public ArrayList<String> getPostsynapticPartners() {
//		return this.postsynapticPartners;
//	}
//	
//	public ArrayList<String> getElectricalPartners() {
//		return this.electricalPartners;
//	}
//	
//	public ArrayList<String> getNeuromuscularPartners() {
//		return this.neuromuscularPartners;
//	}
//	
//	public ArrayList<String> getExpressesWORMBASE() {
//		return geneExpression;
//	}
//	
//	public ArrayList<ArrayList<String>> getHomologues() {
//		return homologues;
//	}
//	
//	public ArrayList<String> getReferences() {
//		return references;
//	}
//	
//	public ArrayList<String> getLinks() {
//		return links;
//	}
//	
//	public ArrayList<String> getNuclearProductionInfo() {
//		return this.nuclearProductionInfo;
//	}
//	
//	public ArrayList<String> getCellShapeProductionInfo() {
//		return this.cellShapeProductionInfo;
//	}
//	
//	private final static String graphicURL = "http://www.wormatlas.org/neurons/Images/";
//	private final static String jpgEXT = ".jpg";
//	private final static String wormatlasURL = "http://www.wormatlas.org/neurons/Individual%20Neurons/";
//	private final static String wormatlasURLEXT = "mainframe.htm";
//	//private final static String wormatlasURLEXT2 = "frameset.html";
//	private final static String wormbaseURL = "http://www.wormbase.org/db/get?name=";
//	private final static String wormbaseEXT = ";class=Anatomy_term";
//	private final static String textpressoURL = "http://textpresso-www.cacr.caltech.edu/cgi-bin/celegans/search?searchstring=";
//	private final static String textpressoURLEXT = ";cat1=Select%20category%201%20from%20list%20above;cat2=Select%20category%202%20from%20list%20above;cat3=Select%20category%203%20from%20list%20above;cat4=Select%20category%204%20from%20list%20above;cat5=Select%20category%205%20from%20list%20above;search=Search!;exactmatch=on;searchsynonyms=on;literature=C.%20elegans;target=abstract;target=body;target=title;target=introduction;target=materials;target=results;target=discussion;target=conclusion;target=acknowledgments;target=references;sentencerange=sentence;sort=score%20(hits);mode=boolean;authorfilter=;journalfilter=;yearfilter=;docidfilter=;";
//	private final static String textpressoTitleStr = "Title: </span>";
//	private final static String textpressoAuthorsStr = "Authors: </span>";
//	private final static String textpressoYearStr = "Year: </span>";
//	private final static String googleURL = "https://www.google.com/#q=";
//	private final static String googleWormatlasURL = "https://www.google.com/#q=site:wormatlas.org+";
//	private final static String wormwiringBaseURL = "http://wormwiring.hpc.einstein.yu.edu/data/neuronData.php?name=";
//	private final static String wormwiringN2UEXT = "&db=N2U";
////	private final static String wormwiringN2YEXT = "&db=n2y";
////	private final static String wormwiringN930EXT = "&db=n930";
//	
//	private final static String anchorClose = "</a>";
//	private final static String href = "href=\"";
//	
//}