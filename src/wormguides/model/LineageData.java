package wormguides.model;

public interface LineageData {
	String[] getNames(int time);
	Integer[][] getPositions(int time);
	Integer[] getDiameters(int time);
}
