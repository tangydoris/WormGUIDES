package wormguides.model;

import java.util.ArrayList;

public interface LineageData {
	String[] getNames(int time);
	Integer[][] getPositions(int time);
	Integer[] getDiameters(int time);
	ArrayList<String> getAllCellNames();
}
