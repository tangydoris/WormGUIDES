package wormguides.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class CellDeaths {
	private static ArrayList<String> cellDeaths;
	private final static String CellDeathsFile = "/wormguides/model/cell_deaths/CellDeaths.csv";
	
	static {
		cellDeaths = new ArrayList<String>();
		try {
			URL url = PartsList.class.getResource(CellDeathsFile);
			
			InputStream input = url.openStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
					
			String line;
			while ((line = br.readLine()) != null) {
				cellDeaths.add(line.toLowerCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean containsCell(String cell) {
		return cellDeaths.contains(cell.toLowerCase());
	}
}