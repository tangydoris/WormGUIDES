package wormguides.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import wormguides.view.HTMLNode;
import wormguides.view.InfoWindowDOM;

public class PartsList {

	private static ArrayList<String> functionalNames;
	private static ArrayList<String> lineageNames;
	private static ArrayList<String> descriptions;

	static {
		functionalNames = new ArrayList<String>();
		lineageNames = new ArrayList<String>();
		descriptions = new ArrayList<String>();

		try {
			URL url = PartsList.class.getResource("/wormguides/model/partslist.txt");
			InputStream input = url.openStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);

			String line;
			while ((line = br.readLine()) != null) {

				String[] lineArray = line.split("\t");
				functionalNames.add(lineArray[0].trim());
				lineageNames.add(lineArray[1].trim());
				descriptions.add(lineArray[2].trim());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static boolean containsLineageName(String name) {
		if (name == null)
			return false;
		// case insensitive search
		for (String lineageName : lineageNames) {
			if (lineageName.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getLineageNameCorrectCase(String lineageName) {
		lineageName.trim();
		System.out.println("getting correct case for - "+lineageName);
		for (String name : lineageNames) {
			if (name.equalsIgnoreCase(lineageName))
				return name;
		}
		return "ABCDEFG";
	}

	public static boolean containsFunctionalName(String name) {
		for (String funcName : functionalNames) {
			if (funcName.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public static String getFunctionalNameByIndex(int i) {
		try {
			return functionalNames.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String getFunctionalNameByLineageName(String name) {
		// account for case insensitivity and translate lineage name
		for (String lineageName : lineageNames) {
			if (lineageName.equalsIgnoreCase(name)) {
				name = lineageName;
				break;
			}
		}
		return getFunctionalNameByIndex(lineageNames.indexOf(name));
	}

	public static String getLineageNameByIndex(int i) {
		try {
			return lineageNames.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String getLineageNameByFunctionalName(String name) {
		for (String funcName : functionalNames) {
			if (funcName.equalsIgnoreCase(name)) {
				name = funcName;
				break;
			}
		}
		return getLineageNameByIndex(functionalNames.indexOf(name));
	}

	public static String getDescriptionByIndex(int i) {
		try {
			return descriptions.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String getDescriptionByLineageName(String lineageName) {
		return getDescriptionByIndex(lineageNames.indexOf(lineageName));
	}

	public static String getDescriptionByFunctionalName(String functionalName) {
		return getDescriptionByIndex(functionalNames.indexOf(functionalName));
	}

	public static ArrayList<String> getLineageNames() {
		return lineageNames;
	}

	public static ArrayList<String> getFunctionalNames() {
		return functionalNames;
	}

	public static ArrayList<String> getDescriptions() {
		return descriptions;
	}
	
	public static InfoWindowDOM partsListDOM() {
		HTMLNode html = new HTMLNode("html");
		HTMLNode head = new HTMLNode("head");
		HTMLNode body = new HTMLNode("body");
		
		HTMLNode partsListTableDiv = new HTMLNode("div");
		HTMLNode partsListTable = new HTMLNode("table");
		
		for (int i = 0; i < functionalNames.size(); i++) {
			HTMLNode tr = new HTMLNode("tr");
			
			tr.addChild(new HTMLNode("td", "", "", functionalNames.get(i)));
			
			if (lineageNames.get(i) != null) {
				tr.addChild(new HTMLNode("td", "", "", lineageNames.get(i)));
			}
			
			if (descriptions.get(i) != null) {
				tr.addChild(new HTMLNode("td", "", "", descriptions.get(i)));
			}
			
			partsListTable.addChild(tr);
		}
		
		partsListTableDiv.addChild(partsListTable);
		body.addChild(partsListTableDiv);
		
		html.addChild(head);
		html.addChild(body);
		
		InfoWindowDOM dom = new InfoWindowDOM(html);
		dom.buildStyleNode();
		
		return dom;
	}

}
