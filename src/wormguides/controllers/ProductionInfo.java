package wormguides.controllers;

import java.util.ArrayList;
import wormguides.loaders.ProductionInfoLoader;
import wormguides.model.PartsList;

public class ProductionInfo {
	
	private ArrayList<ArrayList<String>> productionInfoData;
	
	public ProductionInfo() {
		ProductionInfoLoader piLoader = new ProductionInfoLoader();
		
		this.productionInfoData = piLoader.buildProductionInfo();
	}
	
	public ArrayList<String> getNuclearInfo() {
		ArrayList<String> nuclearInfo = new ArrayList<String>();
		
		if (productionInfoData.get(0).get(0).equals("all-nuclear positions")) {
			nuclearInfo.add(productionInfoData.get(3).get(0) + ", " + productionInfoData.get(2).get(0)); //store strain, marker data
			nuclearInfo.add(productionInfoData.get(1).get(0)); //store image series data
		}
		
		return nuclearInfo;
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
		return this.productionInfoData;
	}
	
}
