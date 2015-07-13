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
	
	private ArrayList<String> properNames;
	private ArrayList<String> sulstonNames;
	private ArrayList<String> descriptions;
	
	public PartsList() {
		properNames = new ArrayList<String>();
		sulstonNames = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		
		try {
			JarFile jarFile = new JarFile(new File(JAR_NAME));
	
			Enumeration<JarEntry> entries = jarFile.entries();
			//int time = 0;
			
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
				//System.out.println(entry.getName());
				if (entry.getName().equals(PARTSLIST_NAME)) {
					InputStream input = jarFile.getInputStream(entry);
					InputStreamReader isr = new InputStreamReader(input);
					BufferedReader br = new BufferedReader(isr);
					
					String line;
					while ((line = br.readLine()) != null) {
						String[] lineArray = line.split("\t");
						properNames.add(lineArray[0]);
						sulstonNames.add(lineArray[1]);
						descriptions.add(lineArray[2]);
					}
					//System.out.println("partslist size "+properNames.size());
					break;
				}
			}
			jarFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public boolean contains(String name) {
		return sulstonNames.contains(name);
	}
	
	public String getProperName(int i) {
		return properNames.get(i);
	}
	
	public String getSulstonName(int i) {
		return sulstonNames.get(i);
	}
	
	public String getProperName(String sulstonName) {
		try {
			return properNames.get(sulstonNames.indexOf(sulstonName));
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public String getDescription(int i) {
		return descriptions.get(i);
	}
	
	public String getDescription(String sulstonName) {
		try {
			return descriptions.get(sulstonNames.indexOf(sulstonName));
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private final static String JAR_NAME = "WormGUIDES.jar",
		PARTSLIST_NAME = "wormguides/model/partslist.txt";
}
