package wormguides.model;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class CellCase {

	private String funcName;
	private String lineageName;

	private String functionWORMATLAS;

	private ArrayList<String> geneExpression;
	private ArrayList<ArrayList<String>> homologues;

	private ArrayList<String> links;

	private ArrayList<String> nuclearProductionInfo;
	private ArrayList<String> cellShapeProductionInfo;

	public CellCase(String lineageName, ArrayList<String> nuclearProductionInfo,
			ArrayList<String> cellShapeProductionInfo) {
		this.lineageName = lineageName;
		funcName = PartsList.getFunctionalNameByLineageName(lineageName);
		if (funcName == null)
			funcName = "";

		links = buildLinks();

		// parse wormatlas for the "Function" field
		functionWORMATLAS = setFunctionFromWORMATLAS();
	}

	protected abstract ArrayList<String> buildLinks();

	protected abstract void findHomologues();

	public String getFunctionalName() {
		return funcName;
	}

	public String getLineageName() {
		return lineageName;
	}

	public ArrayList<String> getExpressesWORMBASE() {
		return geneExpression;
	}

	public ArrayList<ArrayList<String>> getHomologues() {
		return homologues;
	}

	public ArrayList<String> getLinks() {
		return links;
	}

	private String setFunctionFromWORMATLAS() {
		if (this.funcName == null)
			return "";

		String content = "";
		URLConnection connection = null;

		/*
		 * USING mainframe.htm EXT Leaving code for frameset.htm check
		 */

		/*
		 * if R/L cell, find base name for URL e.g. ribr --> RIB
		 * 
		 * if no R/L, leave as is e.g. AVG
		 */
		String cell = this.funcName;
		Character lastChar = cell.charAt(cell.length() - 1);
		lastChar = Character.toLowerCase(lastChar);
		if (lastChar == 'r' || lastChar == 'l') {
			cell = cell.substring(0, cell.length() - 1);

			// check if preceding d/v
			lastChar = cell.charAt(cell.length() - 1);
			lastChar = Character.toLowerCase(lastChar);
			if (lastChar == 'd' || lastChar == 'v') {
				cell = cell.substring(0, cell.length() - 1);
			}
		} else if (lastChar == 'd' || lastChar == 'v') { // will l/r ever come
															// before d/v
			cell = cell.substring(0, cell.length() - 1);

			// check if preceding l/r
			lastChar = cell.charAt(cell.length() - 1);
			lastChar = Character.toLowerCase(lastChar);
			if (lastChar == 'l' || lastChar == 'r') {
				cell = cell.substring(0, cell.length() - 1);
			}
		} else if (Character.isDigit(lastChar)) {
			cell = cell.substring(0, cell.length() - 1).toUpperCase() + "N";
		}

		String URL = wormatlasURL + cell.toUpperCase() + wormatlasURLEXT;
		try {
			connection = new URL(URL).openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
			// //try second extension
			// URL = wormatlasURL + cell + wormatlasURLEXT;
			// System.out.println("TRYING SECOND URL: " + URL);
			//
			// try {
			// connection = new URL(URL).openConnection();
			// Scanner scanner = new Scanner(connection.getInputStream());
			// scanner.useDelimiter("\\Z");
			// content = scanner.next();
			// scanner.close();
			// } catch (Exception e1) {
			// //e1.printStackTrace();
			// //a page wasn't found on wormatlas
			// return this.cellName + " page not found on Wormatlas";
			// }
			// e1.printStackTrace();
			// a page wasn't found on wormatlas
			return null;
		}
		return findFunctionInHTML(content, URL);
	}

	private String findFunctionInHTML(String content, String URL) {
		// parse the html for "Function"
		content = content.substring(content.indexOf("Function"));
		content = content.substring(content.indexOf(":") + 1, content.indexOf("</td>")); // skip
																							// the
																							// "Function:"
																							// text

		/*
		 * find the anchor tags and change to: "<a href=\"#\" name=\"" + link +
		 * "\" onclick=\"handleLink(this)\">" paradigm
		 */
		String findStr = "<a ";
		int lastIdx = 0;

		while (lastIdx != -1) {
			lastIdx = content.indexOf(findStr, lastIdx);

			// check if another anchor found
			if (lastIdx != -1) {
				// save the string preceding the anchor
				String precedingStr = content.substring(0, lastIdx);

				// find the end of the anchor and extract the anchor
				int anchorEndIdx = content.indexOf(anchorClose, lastIdx);
				String anchor = content.substring(lastIdx, anchorEndIdx + anchorClose.length());

				// extract the source href --> "href=\""
				boolean isLink = true;
				int startSrcIdx = anchor.indexOf(href) + href.length();

				String src = "";
				// make sure not a citation i.e. first character is '#'
				if (anchor.charAt(startSrcIdx) == '#') {
					isLink = false;
				} else {
					src = anchor.substring(startSrcIdx, anchor.indexOf("\"", startSrcIdx));
				}

				if (isLink) {
					// check if relative src
					if (!src.contains("www.") && !src.contains("http")) {
						// remove path
						if (src.contains("..")) {
							src = src.substring(src.lastIndexOf("/") + 1);
						}
						src = wormatlasURL + src;
					}

					// extract the anchor text --> skip over the first <
					String text = anchor.substring(anchor.indexOf(">") + 1, anchor.substring(1).indexOf("<") + 1);

					// build new anchor
					String newAnchor = "<a href=\"#\" name=\"" + src + "\" onclick=\"handleLink(this)\">" + text
							+ "</a>";

					// replace previous anchor
					content = precedingStr + newAnchor + content.substring(anchorEndIdx + anchorClose.length());
				} else {
					// remove anchor
					String txt = anchor.substring(anchor.indexOf(">") + 1, anchor.substring(1).indexOf("<") + 1);

					content = precedingStr + txt + content.substring(anchorEndIdx + anchorClose.length());
				}

				// move lastIdx past just processed anchor
				lastIdx += findStr.length();
			}

		}

		// add the link to the list
		links.add(URL);

		return "<em>Source: </em><a href=\"#\" name=\"" + URL + "\" onclick=\"handleLink(this)\">" + URL
				+ "</a><br><br>" + content;
	}

	private final static String wormatlasURL = "http://www.wormatlas.org/neurons/Individual%20Neurons/";
	private final static String wormatlasURLEXT = "mainframe.htm";

	private final static String anchorClose = "</a>";
	private final static String href = "href=\"";
}
