package wormguides;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wormguides.model.TableLineageData;

// Loader class to read nuclei files
public class AceTreeLoader {
	
	public static TableLineageData loadNucFiles(String filePath) {
		TableLineageData tld = new TableLineageData();
		
		try {
			JarFile jarFile = new JarFile(new File(filePath));

			Enumeration<JarEntry> entries = jarFile.entries();
			for (JarEntry entry = entries.nextElement(); entries.hasMoreElements(); entry = entries.nextElement()) {
				String name = entry.getName();
				
				int time = 1;
				if (entry.getName().startsWith(ENTRY_PREFIX)) {
					InputStream input = jarFile.getInputStream(entry);
					process(tld, time++, input);
				}
			}

			jarFile.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return tld;
	}
	
	private static void process(TableLineageData tld, int time, InputStream input) throws IOException {
		tld.addFrame();
		
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = new String[20];
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
	        int k = 0;
	        while (tokenizer.hasMoreTokens()) {
	            tokens[k++] = tokenizer.nextToken().trim();
	        }
	        
		}
		
		reader.close();
	}
	
	public static void main (String[] args) {
		loadNucFiles(JAR_NAME);
	}
	
	final private static String ENTRY_PREFIX = "wormguides/nuclei_files/";
	final private static String JAR_NAME = "WormGUIDES.jar";
	final private static int
		VALID = 1,
		XCOR = 5,
		YCOR = 6,
		ZCOR = 7,
		DIAMETER = 8,
		IDENTITY = 9;

}
