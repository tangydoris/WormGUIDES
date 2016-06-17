package wormguides.model;

import java.util.ArrayList;

public interface LineageData {

	String[] getNames(int time);

	Integer[][] getPositions(int time);

	Integer[] getDiameters(int time);

	ArrayList<String> getAllCellNames();

	int getTotalTimePoints();

	int getFirstOccurrenceOf(String name);

	int getLastOccurrenceOf(String name);

	boolean isCellName(String name);
	
	public void shiftAllPositions(int x, int y, int z);

}
