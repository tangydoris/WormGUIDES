package wormguides.controllers;

import java.util.ArrayList;

import wormguides.loaders.ProductionInfoLoader;

public class ProductionInfo {
	
	private ArrayList<ArrayList<String>> productionInfoData;
	
	public ProductionInfo() {
		ProductionInfoLoader piLoader = new ProductionInfoLoader();
		
		this.productionInfoData = piLoader.buildProductionInfo();
	}
	
	public ArrayList<ArrayList<String>>getProductionInfoData() {
		return this.productionInfoData;
	}
	
}
