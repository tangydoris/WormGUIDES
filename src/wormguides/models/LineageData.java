package wormguides.models;

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

    void shiftAllPositions(int x, int y, int z);

    boolean isSulstonMode();

    void setIsSulstonModeFlag(boolean isSulston);
}