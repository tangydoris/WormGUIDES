/*
 * Bao Lab 2016
 */

package acetree.lineagedata;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Table that keeps the lineage data in {@link Frame} data structures in memory. All times known to other classes
 * begin at 1, even though the time frames are indexed from 0 in the class.
 */
public class TableLineageData implements LineageData {

    private final List<Frame> timeFrames;
    private final List<String> allCellNames;
    private boolean isSulston;
    private double[] xyzScale;

    public TableLineageData(final List<String> allCellNames, double X_SCALE, double Y_SCALE, double Z_SCALE) {
        this.allCellNames = requireNonNull(allCellNames);
        this.allCellNames.sort(String::compareTo);
        this.timeFrames = new ArrayList<>();

        this.xyzScale = new double[]{X_SCALE, Y_SCALE, Z_SCALE};
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getAllCellNames() {
        return new ArrayList<>(allCellNames);
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNames(final int time) {
        final int internalTimeIndex = time - 1;
        if (internalTimeIndex >= getNumberOfTimePoints() || internalTimeIndex < 0) {
            return new String[1];
        }
        return timeFrames.get(internalTimeIndex).getNames();
    }

    /** {@inheritDoc} */
    @Override
    public void shiftAllPositions(final int x, final int y, final int z) {
        for (Frame timeFrame : timeFrames) {
            timeFrame.shiftPositions(x, y, z);
        }
    }

    /** {@inheritDoc} */
    @Override
    public double[][] getPositions(final int time) {
        final int internalTimeIndex = time - 1;
        if (internalTimeIndex >= getNumberOfTimePoints() || internalTimeIndex < 0) {
            return new double[1][3];
        }
        return timeFrames.get(internalTimeIndex).getPositions();
    }

    /** {@inheritDoc} */
    @Override
    public double[] getDiameters(final int time) {
        final int internalTimeIndex = time - 1;
        if (internalTimeIndex >= getNumberOfTimePoints() || internalTimeIndex < 0) {
            return new double[1];
        }
        return timeFrames.get(internalTimeIndex).getDiameters();
    }

    /** {@inheritDoc} */
    @Override
    public int getNumberOfTimePoints() {
        return timeFrames.size();
    }

    /** {@inheritDoc} */
    @Override
    public void addTimeFrame() {
        final Frame frame = new Frame();
        timeFrames.add(frame);
    }

    /** {@inheritDoc} */
    @Override
    public void addNucleus(
            final int time,
            final String name,
            final double x,
            final double y,
            final double z,
            final double diameter) {

        if (time <= getNumberOfTimePoints()) {
            final int index = time - 1;

            Frame frame = timeFrames.get(index);
            frame.addName(name);
            frame.addPosition(new Double[]{x, y, z});
            frame.addDiameter(diameter);

            if (!allCellNames.contains(name)) {
                allCellNames.add(name);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getFirstOccurrenceOf(final String name) {
        final String trimmed = name.trim();

        for (int i = 0; i < timeFrames.size(); i++) {
            for (String cell : timeFrames.get(i).getNames()) {
                if (cell.equalsIgnoreCase(trimmed)) {
                    return i + 1;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    public int getLastOccurrenceOf(final String name) {
        final String trimmed = name.trim();
        int time = getFirstOccurrenceOf(trimmed);

        if (time >= 1) {
            outer:
            for (int i = time; i < timeFrames.size(); i++) {
                for (String cell : timeFrames.get(i).getNames()) {
                    if (cell.equalsIgnoreCase(trimmed)) {
                        continue outer;
                    }
                }
                time = i - 1;
                break;
            }
        }
        return time + 1;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellName(final String name) {
        final String trimmed = name.trim();
        for (String cell : allCellNames) {
            if (cell.equalsIgnoreCase(trimmed)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSulstonMode() {
        return isSulston; //default embryo
    }

    /** {@inheritDoc} */
    @Override
    public void setIsSulstonModeFlag(boolean isSulston) {
        this.isSulston = isSulston;
    }

    /** {@inheritDoc} */
    @Override
    public double[] getXYZScale() {
        return this.xyzScale;
    }

    @Override
    public String toString() {
        String out = "";
        final int totalFrames = getNumberOfTimePoints();
        for (int i = 0; i < totalFrames; i++) {
            out += (i + 1) + "\n" + timeFrames.get(i).toString() + "\n";
        }
        return out;
    }

    /**
     * One timeframe of nuclei lineage data
     */
    private class Frame {

        private List<String> names;
        private List<Double[]> positions;
        private List<Double> diameters;

        private Frame() {
            names = new ArrayList<>();
            positions = new ArrayList<>();
            diameters = new ArrayList<>();
        }

        private void shiftPositions(final int x, final int y, final int z) {
            for (int i = 0; i < positions.size(); i++) {
                final Double[] pos = positions.get(i);
                positions.set(i, new Double[]{pos[0] - x, pos[1] - y, pos[2] - z});
            }
        }

        private void addName(String name) {
            names.add(name);
        }

        private void addPosition(Double[] position) {
            positions.add(position);
        }

        private void addDiameter(Double diameter) {
            diameters.add(diameter);
        }

        private String[] getNames() {
            return names.toArray(new String[names.size()]);
        }

        private double[][] getPositions() {
            final double[][] copy = new double[positions.size()][3];
            for (int i = 0; i < positions.size(); i++) {
                for (int j = 0; j < 3; j++) {
                    copy[i][j] = positions.get(i)[j];
                }
            }
            return copy;
        }

        private double[] getDiameters() {
            final double[] copy = new double[diameters.size()];
            for (int i = 0; i < diameters.size(); i++) {
                copy[i] = diameters.get(i);
            }
            return copy;
        }

        @Override
        public String toString() {
            String out = "";
            String[] names = getNames();
            for (String name : names) {
                out += name + "\n";
            }
            return out;
        }
    }

}
