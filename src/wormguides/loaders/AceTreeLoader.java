package wormguides.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wormguides.model.TableLineageData;

// Loader class to read nuclei files
public class AceTreeLoader {
	
	private static ArrayList<String> allCellNames = new ArrayList<String>();
	
	public static TableLineageData loadNucFiles() {
		
//		//find the JAR name
//		String JarName = "";
//		try {
//			String pathToJar = ProductionInfoLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
//			
//			//remove bin from path
//			pathToJar = pathToJar.substring(0, pathToJar.indexOf("/bin/"));
//			
//			//take JAR name and add extension
//			JarName = pathToJar.substring(pathToJar.lastIndexOf('/')+1) + ".jar";
//		} catch (URISyntaxException e1) {
//			// TODO Auto-generated catch block
//			//e1.printStackTrace();
//		}		
		
		TableLineageData tld = new TableLineageData(allCellNames);
		try {
			JarFile jarFile = new JarFile(new File("WormGUIDES.jar"));

			Enumeration<JarEntry> entries = jarFile.entries();
			int time = 0;
			
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
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
			String[] tokens = new String[TOKEN_ARRAY_SIZE];
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
	        int k = 0;
	        while (tokenizer.hasMoreTokens())
	            tokens[k++] = tokenizer.nextToken().trim();
	        
	        int valid = Integer.parseInt(tokens[VALID]);
	        if (valid == 1) {
	        	makeNucleus(tld, time, tokens);
	        }
		}
		
		reader.close();
	}
	
	private static void makeNucleus(TableLineageData tld, int time, String[] tokens) {
		try {
			String name = tokens[IDENTITY];
	        int x = Integer.parseInt(tokens[XCOR]);
	        int y = Integer.parseInt(tokens[YCOR]);
	        int z = (int) Math.round(Double.parseDouble(tokens[ZCOR]));
	        int diameter = Integer.parseInt(tokens[DIAMETER]);
	
	        tld.addNucleus(time, name, x, y, z, diameter);
		}
		catch (NumberFormatException nfe) {
			System.out.println("Incorrect format in nucleus file for time " + time + ".");
		}
	}
	
	public static boolean isLineageName(String name) {
		return allCellNames.contains(name);
	}
	
	
	private static final String ENTRY_PREFIX = "wormguides/model/nuclei_files/";
	private static final int TOKEN_ARRAY_SIZE = 21;
	private static final int VALID = 1,
							XCOR = 5,
							YCOR = 6,
							ZCOR = 7,
							DIAMETER = 8,
							IDENTITY = 9;

}
