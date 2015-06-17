package wormguides;

public class TableLineageData implements LineageData{
	
	private Frame[] timeFrames;
	private int	totalFrames;
	
	public TableLineageData() {
		
	}

	@Override
	public String[] getNames(int time) {
		if (time >= totalFrames || time < 0)
			return null;
		else
			return timeFrames[time].getNames();
	}

	@Override
	public int[][] getPositions(int time) {
		if (time >= totalFrames || time < 0)
			return null;
		else
			return timeFrames[time].getPositions();
	}

	@Override
	public int[] getSizes(int time) {
		if (time >= totalFrames || time < 0)
			return null;
		else
			return timeFrames[time].getSizes();
	}
	
	private class Frame {
		private String[] names;
		private int[][] positions;
		private int[] sizes;
		
		private void setNames(String[] names) {
			this.names = names;
		}
		
		private void setPositions(int[][] positions) {
			this.positions = positions;
		}
		
		private void setSizes(int[] sizes) {
			this.sizes = sizes;
		}
		
		private String[] getNames() {
			return names;
		}
		
		private int[][] getPositions() {
			return positions;
		}
		
		private int[] getSizes() {
			return sizes;
		}
	}

}
