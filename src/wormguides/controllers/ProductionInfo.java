package wormguides.controllers;

import java.util.ArrayList;
import wormguides.loaders.ProductionInfoLoader;
import wormguides.model.PartsList;

public class ProductionInfo {
	
	private ArrayList<ArrayList<String>> productionInfoData;
	
	public ProductionInfo() {
		productionInfoData = ProductionInfoLoader.buildProductionInfo();
	}
	
	public ArrayList<String> getNuclearInfo() {
		ArrayList<String> nuclearInfo = new ArrayList<String>();
		
		if (productionInfoData.get(0).get(0).equals("all-nuclear positions")) {
			nuclearInfo.add(productionInfoData.get(3).get(0) + ", " + productionInfoData.get(2).get(0)); //store strain, marker data
			nuclearInfo.add(productionInfoData.get(1).get(0)); //store image series data
		}
		
		return nuclearInfo;
	}
	
	public int getDefaultStartTime() {
		return DEFAULT_START_TIME;
	}
	
	public int getMovieTimeOffset() {
		String input = productionInfoData.get(8).get(1);
		
		try {
			int startTime = Integer.parseInt(input);
			return startTime-DEFAULT_START_TIME;
		} catch (NumberFormatException e) {
			System.out.println("Input: '"+input+"'");
			System.out.println("Invalid input for movie start time. Using default start time of "
					+DEFAULT_START_TIME);
		}
		
		return 0;
	}
	
	public ArrayList<String> getCellShapeData(String queryCell) {
		ArrayList<String> cellShapeData = new ArrayList<String>();
		
		queryCell = PartsList.getFunctionalNameByLineageName(queryCell);
		
		if (queryCell == null) {
			return cellShapeData;
		}
		
		for (int i = 0; i < productionInfoData.get(0).size(); i++) {
			String cells = productionInfoData.get(0).get(i);
			if (cells.contains(queryCell)) {
				cellShapeData.add(productionInfoData.get(3).get(i) + ", " + productionInfoData.get(2).get(i)); //store strain, marker data
				cellShapeData.add(productionInfoData.get(1).get(i)); //store image series data
			}
		}
		
		return cellShapeData;
	}
	
	public ArrayList<ArrayList<String>> getProductionInfoData() {
		return productionInfoData;
	}
	
	private final int DEFAULT_START_TIME = 1;
}
