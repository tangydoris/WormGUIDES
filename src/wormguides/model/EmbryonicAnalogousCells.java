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

public class EmbryonicAnalogousCells {
	private static ArrayList<EmbryonicHomology> homologues;
	
	private final static String JAR_NAME = "WormGUIDES.jar";
	private final static String FILE_NAME = "wormguides/model/analogous_cell_file/EmbryonicAnalogousCells.csv";
	
	static {
		homologues = new ArrayList<EmbryonicHomology>();
		
		try {
			JarFile jarFile = new JarFile(new File(JAR_NAME));
	
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
				if (entry.getName().equals(FILE_NAME)) {
					InputStream input = jarFile.getInputStream(entry);
					InputStreamReader isr = new InputStreamReader(input);
					BufferedReader br = new BufferedReader(isr);
					
					String line;
					while ((line = br.readLine()) != null) {
						String[] cells = line.split(",");
						if (cells.length == 2 &&
								cells[0].length() > 0 && cells[1].length() > 0) {
							EmbryonicHomology eh = new EmbryonicHomology(cells[0], cells[1]);
							homologues.add(eh);
						}
						
					}

					break;
				}
			}
			jarFile.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static String findEmbryonicHomology(String cell) {
		for (EmbryonicHomology eh : homologues) {
			if (eh.getCell1().equals(cell)) {
				return eh.getCell2();
			}
			
			if (eh.getCell2().equals(cell)) {
				return eh.getCell1();
			}
		}
		
		return "N/A";
	}
}
