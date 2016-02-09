package wormguides.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProductionInfoLoader {

	public static ArrayList<ArrayList<String>> buildProductionInfo() {
		
		ArrayList<ArrayList<String>> productionInfo = new ArrayList<ArrayList<String>>();
		ArrayList<String> cells = new ArrayList<String>();
		ArrayList<String> imageSeries = new ArrayList<String>();
		ArrayList<String> markers = new ArrayList<String>();
		ArrayList<String> strains = new ArrayList<String>();
		ArrayList<String> compressedEmbryo = new ArrayList<String>();
		ArrayList<String> temporalResolutions = new ArrayList<String>();
		ArrayList<String> segmentations = new ArrayList<String>();
		ArrayList<String> cytoshowLinks = new ArrayList<String>();
		ArrayList<String> movieStartTime = new ArrayList<String>();
		
		try {
			JarFile jarFile = new JarFile(new File(JARname));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				
				if (entry.getName().equalsIgnoreCase(productionInfoFilePath)) {
					
					InputStream stream = jarFile.getInputStream(entry);
					InputStreamReader streamReader = new InputStreamReader(stream);
					BufferedReader reader = new BufferedReader(streamReader);
					
					String line;
					
					while((line = reader.readLine()) != null) {
						//skip product info line and header line
						if (line.equals(productInfoLine)) {
							line = reader.readLine();
							
							if (line.equals(headerLine)) {
								line = reader.readLine();
							}
							
							if (line == null) break;
						}
						
						//make sure valid line
						if (line.length() <= 1) break;
						
						StringTokenizer tokenizer = new StringTokenizer(line, ",");
						//check if valid line i.e. 4 tokens
						if (tokenizer.countTokens() == NUMBER_OF_FIELDS) {
							cells.add(tokenizer.nextToken());
							imageSeries.add(tokenizer.nextToken());
							markers.add(tokenizer.nextToken());
							strains.add(tokenizer.nextToken());
							compressedEmbryo.add(tokenizer.nextToken());
							temporalResolutions.add(tokenizer.nextToken());
							segmentations.add(tokenizer.nextToken());
							cytoshowLinks.add(tokenizer.nextToken());
							movieStartTime.add(tokenizer.nextToken());
						}
					}
				}
			}
			
			//add array lists
			productionInfo.add(cells);
			productionInfo.add(imageSeries);
			productionInfo.add(markers);
			productionInfo.add(strains);
			productionInfo.add(compressedEmbryo);
			productionInfo.add(temporalResolutions);
			productionInfo.add(segmentations);
			productionInfo.add(cytoshowLinks);
			productionInfo.add(movieStartTime);
			
			jarFile.close();
			return productionInfo;
			
		} catch (IOException e) {
			System.out.println("The production info file " + productionInfoFilePath + " wasn't found on the system.");
		}
		
		return productionInfo;
	}
	
	//production info file location
	private final static int NUMBER_OF_FIELDS = 9;
	private final static String productionInfoFilePath = "wormguides/model/production_info_file/Production_Info.csv";
	private final static String JARname = "WormGUIDES.jar";
	private final static String productInfoLine = "Production Information,,,,,,,";
	private final static String headerLine = "Cells,Image Series,Marker,Strain,Compressed Embryo?,Temporal Resolution,Segmentation,cytoshow link,Movie start time (min)";
	
}
