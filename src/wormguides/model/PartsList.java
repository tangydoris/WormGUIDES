package wormguides.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PartsList {
	
	private static ArrayList<String> functionalNames;
	private static ArrayList<String> lineageNames;
	private static ArrayList<String> descriptions;
	
	public PartsList() {
		functionalNames = new ArrayList<String>();
		lineageNames = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		
		try {
			JarFile jarFile = new JarFile(new File(JAR_NAME));
	
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
				if (entry.getName().equals(PARTSLIST_NAME)) {
					InputStream input = jarFile.getInputStream(entry);
					InputStreamReader isr = new InputStreamReader(input);
					BufferedReader br = new BufferedReader(isr);
					
					String line;
					while ((line = br.readLine()) != null) {
						String[] lineArray = line.split("\t");
						functionalNames.add(lineArray[0]);
						lineageNames.add(lineArray[1]);
						descriptions.add(lineArray[2]);
					}

					break;
				}
			}
			jarFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static boolean contains(String name) {
		return lineageNames.contains(name);
	}
	
	public static String getFunctionalName(int i) {
		try {
			return functionalNames.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public static String getLineageName(int i) {
		try {
			return lineageNames.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public static String getFunctionalName(String lineageName) {
		try {
			return functionalNames.get(lineageNames.indexOf(lineageName));
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public static String getDescription(int i) {
		try {
			return descriptions.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public static String getDescription(String sulstonName) {
		try {
			return descriptions.get(lineageNames.indexOf(sulstonName));
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private final static String JAR_NAME = "WormGUIDES.jar",
		PARTSLIST_NAME = "wormguides/model/partslist.txt";
}
