package wormguides.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ConnectomeLoader {
	private String fileName;
	
	public ConnectomeLoader(String fileName) {
		this.fileName = fileName;
	}
	
	public ArrayList<NeuronalSynapse> loadConnectome() {
		try {
			JarFile jarFile = new JarFile(new File("WormGUIDES.jar"));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();

				if (entry.getName().equals(fileName)) {
					System.out.println("Found: " + fileName);
				}
				
			}
			
			jarFile.close();
			return null;
			
		} catch (IOException e) {
			System.out.println("The connectome file " + fileName + " wasn't found on the system.");
		}
		
		return null;
	}
	
	
	
	private static final String JARname = "WormGUIDES.jar";
}
