package wormguides;

public interface LineageData {
	String[] getNames(int time);
	int[][] getPositions(int time);
	int[] getSizes(int time);
}
