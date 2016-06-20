package wormguides.model;

import java.util.ArrayList;
import java.util.Comparator;

/*
 * All times known to data structures begin at 1
 * Frames are indexed from 0
 */

public class TableLineageData implements LineageData {

	private ArrayList<Frame> timeFrames;
	private ArrayList<String> allCellNames;

	public TableLineageData() {
		this(new ArrayList<String>());
	}

	public TableLineageData(ArrayList<String> allCellNames) {
		timeFrames = new ArrayList<Frame>();
		this.allCellNames = allCellNames;
	}

	public void shiftAllPositions(int x, int y, int z) {
		for (int i = 0; i < timeFrames.size(); i++) {
			timeFrames.get(i).shiftPositions(x, y, z);
		}
	}

	@Override
	public ArrayList<String> getAllCellNames() {
		allCellNames.sort(new Comparator<String>() {
			@Override
			public int compare(String s0, String s1) {
				return s0.compareTo(s1);
			}
		});
		return allCellNames;
	}

	@Override
	public String[] getNames(int time) {
		time--;
		if (time >= getTotalTimePoints() || time < 0)
			return new String[1];

		return timeFrames.get(time).getNames();
	}

	@Override
	public Integer[][] getPositions(int time) {
		time--;
		if (time >= getTotalTimePoints() || time < 0)
			return new Integer[1][3];

		return timeFrames.get(time).getPositions();
	}

	@Override
	public Integer[] getDiameters(int time) {
		time--;
		if (time >= getTotalTimePoints() || time < 0)
			return new Integer[1];

		return timeFrames.get(time).getDiameters();
	}

	public int getTotalTimePoints() {
		return timeFrames.size();
	}

	public void addFrame() {
		Frame frame = new Frame();
		timeFrames.add(frame);
	}

	public void addNucleus(int time, String name, int x, int y, int z, int diameter) {
		if (time <= getTotalTimePoints()) {
			int index = time - 1;
			Frame frame = timeFrames.get(index);
			frame.addName(name);
			Integer[] position = new Integer[] { x, y, z };
			frame.addPosition(position);
			frame.addDiameter(diameter);

			if (!allCellNames.contains(name))
				allCellNames.add(name);
		}
	}

	public int getFirstOccurrenceOf(String name) {
		int time = Integer.MIN_VALUE;
		name = name.trim();

		outer: for (int i = 0; i < timeFrames.size(); i++) {
			for (String cell : timeFrames.get(i).getNames()) {
				if (cell.equalsIgnoreCase(name)) {
					time = i + 1;
					break outer;
				}
			}
		}
		return time;
	}

	public int getLastOccurrenceOf(String name) {
		name = name.trim();
		int time = getFirstOccurrenceOf(name);

		if (time >= 1) {
			outer: for (int i = time; i < timeFrames.size(); i++) {
				for (String cell : timeFrames.get(i).getNames()) {
					if (cell.equalsIgnoreCase(name))
						continue outer;
				}

				time = i - 1;
				break;
			}
		}

		return time + 1;
	}

	public boolean isCellName(String name) {
		name = name.trim();
		for (String cell : allCellNames) {
			if (cell.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public String toString() {
		String out = "";
		int totalFrames = getTotalTimePoints();
		for (int i = 0; i < totalFrames; i++)
			out += (i + 1) + Frame.NEWLINE + timeFrames.get(i).toString() + Frame.NEWLINE;

		return out;
	}
	
	public boolean isLineagedEmbryo() {
		return true; //default embryo
	}

	public class Frame {
		private ArrayList<String> names;
		private ArrayList<Integer[]> positions;
		private ArrayList<Integer> diameters;

		private String[] namesArray;
		private Integer[][] positionsArray;
		private Integer[] diametersArray;

		private Frame() {
			names = new ArrayList<String>();
			positions = new ArrayList<Integer[]>();
			diameters = new ArrayList<Integer>();
		}

		/**
		 * Shifts all the positions in this Frame by a specified x-, y- and
		 * z-offset.
		 * 
		 * @param x
		 *            Amount of offset the x-coordinates by
		 * @param y
		 *            Amount of offset the y-coordinates by
		 * @param z
		 *            Amount of offset the z-coordinates by
		 */
		private void shiftPositions(int x, int y, int z) {
			for (int i = 0; i < positions.size(); i++) {
				Integer[] pos = positions.get(i);
				positions.set(i, new Integer[] { pos[0] - x, pos[1] - y, pos[2] - z });
			}
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

		private String[] getNames() {
			if (namesArray == null)
				namesArray = names.toArray(new String[names.size()]);
			return namesArray;
		}

		private Integer[][] getPositions() {
			positionsArray = positions.toArray(new Integer[positions.size()][3]);
			return positionsArray;
		}

		private Integer[] getDiameters() {
			if (diametersArray == null)
				diametersArray = diameters.toArray(new Integer[diameters.size()]);
			return diametersArray;
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
