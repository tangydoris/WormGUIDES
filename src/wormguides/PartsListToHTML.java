package wormguides;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PartsListToHTML extends HTMLGenerator {
	
	
	public PartsListToHTML() {
		super();
		
	}
	
	public String buildPartsListAsHTML() {
		String html = openTableTagHTML;
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
						html += (openTableRowHTML + openTableDataHTML + line + closeTableDataHTML + closeTableRowHTML);
					}
					break;
				}
			}
			jarFile.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		html += (closeTableRowHTML + closeTableTagHTML);
		
		return generateCompleteHTML(html);
	}
	
	private final static String JAR_NAME = "WormGUIDES.jar";
	private final static String PARTSLIST_NAME = "wormguides/model/partslist.txt";
}