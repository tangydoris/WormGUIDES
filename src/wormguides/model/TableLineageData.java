package wormguides.model;

import java.util.ArrayList;

public class TableLineageData implements LineageData{
	
	private ArrayList<Frame> timeFrames;
	
	public TableLineageData() {
		timeFrames = new ArrayList<Frame>();
	}

	@Override
	public String[] getNames(int time) {
		if (time >= getSize() || time < 0)
			return null;
		else
			return timeFrames.get(time).getNames();
	}

	@Override
	public Integer[][] getPositions(int time) {
		if (time >= getSize() || time < 0)
			return null;
		else
			return timeFrames.get(time).getPositions();
	}

	@Override
	public Integer[] getDiameters(int time) {
		if (time >= getSize() || time < 0)
			return null;
		else
			return timeFrames.get(time).getDiameters();
	}
	
	public int getSize() {
		return timeFrames.size();
	}
	
	public void addFrame() {
		Frame frame = new Frame();
		timeFrames.add(frame);
	}
	
	public void addNucleus(int time, String name, int x, int y, int z, int diameter) {
		//System.out.println("time"+time+",size:"+getSize());
		if (time <= getSize()) {
			int index = time - 1;
			Frame frame = timeFrames.get(index);
			frame.addName(name);
			Integer[] position = new Integer[]{x, y, z};
			frame.addPosition(position);
			frame.addDiameter(diameter);
		}
	}
	
	public String toString() {
		String out = "";
		int totalFrames = getSize();
		for (int i = 0; i < totalFrames; i++)
			out += i + Frame.NEWLINE + timeFrames.get(i).toString() + Frame.NEWLINE;
		
		return out;
	}

	private class Frame {
		private ArrayList<String> names;
		private ArrayList<Integer[]> positions;
		private ArrayList<Integer> diameters;
		
		private Frame() {
			names = new ArrayList<String>();
			positions = new ArrayList<Integer[]>();
			diameters = new ArrayList<Integer>();
		}
		
		private void addName(String name) {
			names.add(name);
		}
		
		private void addPosition(Integer[] position) {
			positions.add(position);
		}
		
		private void addDiameter(Integer diameter) {
			diameters.add(diameter);
		}
		
		private void setNames(ArrayList<String> names) {
			this.names = names;
		}
		
		private void setPositions(ArrayList<Integer[]> positions) {
			this.positions = positions;
		}
		
		private void setSizes(ArrayList<Integer> sizes) {
			this.diameters = sizes;
		}
		
		private String[] getNames() {
			return names.toArray(new String[names.size()]);
		}
		
		private Integer[][] getPositions() {
			return positions.toArray(new Integer[positions.size()][3]);
		}
		
		private Integer[] getDiameters() {
			return diameters.toArray(new Integer[diameters.size()]);
		}
		
		public String toString() {
			String out = "";
			String[] names = getNames();
			for (int i = 0; i < names.length; i++) {
				out += names[i] + NEWLINE;
			}
			return out;
		}
		
		private final static String NEWLINE = "\n";
	}

}
