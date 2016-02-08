package wormguides.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import wormguides.PartsListToHTML;

public class PartsList {
	
	private static ArrayList<String> functionalNames;
	private static ArrayList<String> lineageNames;
	private static ArrayList<String> descriptions;
	
	
	static {
		functionalNames = new ArrayList<String>();
		lineageNames = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		
		try {
			
			URL url = PartsList.class.getResource("partslist.txt");
			InputStream input = url.openStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
					
			String line;
			while ((line = br.readLine()) != null) {
						
				String[] lineArray = line.split("\t");
				functionalNames.add(lineArray[0]);
				lineageNames.add(lineArray[1]);
				descriptions.add(lineArray[2]);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	public static boolean containsLineageName(String name) {
		//case insensitive search
		for (String lineageName : lineageNames) {
			if (lineageName.toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	
	public static boolean containsFunctionalName(String name) {
		return functionalNames.contains(name);
	}
	
	
	public static String getFunctionalNameByIndex(int i) {
		try {
			return functionalNames.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	
	public static String getFunctionalNameByLineageName(String name) {
		//account for case insensitivity and translate lineage name
		for (String lineageName : lineageNames) {
			if (lineageName.toLowerCase().equals(name.toLowerCase())) {
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
	
	
	public static String getLineageNameByFunctionalName(String functionalName) {
		return getLineageNameByIndex(functionalNames.indexOf(functionalName));
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
	
	public static String getPartsListAsHTMLTable() {
		PartsListToHTML plToHTML = new PartsListToHTML();
		return plToHTML.buildPartsListAsHTML();
	}

}
