package wormguides.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class EmbryonicAnalogousCells {

	private static ArrayList<EmbryonicHomology> homologues;

	static {
		homologues = new ArrayList<EmbryonicHomology>();

		URL url = EmbryonicAnalogousCells.class.getResource("analogous_cell_file/EmbryonicAnalogousCells.csv");

		try {
			if (url != null) {
				InputStream input = url.openStream();
				InputStreamReader isr = new InputStreamReader(input);
				BufferedReader br = new BufferedReader(isr);

				String line;
				while ((line = br.readLine()) != null) {
					String[] cells = line.split(",");
					if (cells.length == 2 && cells[0].length() > 0 && cells[1].length() > 0) {
						EmbryonicHomology eh = new EmbryonicHomology(cells[0], cells[1]);
						homologues.add(eh);
					}

				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/*
	 * find a match in the database given a query cell Case 1: matches a
	 * homologous listing Case 2: descendant of a listed homology
	 */
	public static String findEmbryonicHomology(String cell) {
		for (EmbryonicHomology eh : homologues) {
			if (cell.startsWith(eh.getCell1())) {

				// check if case 1 i.e. complete match
				if (cell.equals(eh.getCell1())) {
					return eh.getCell2();
				}

				// otherwise, case 1 i.e. descendant --> add suffix
				String suffix = cell.substring(eh.getCell2().length());
				String descendantHomology = eh.getCell2() + suffix + " (" + eh.getCell1() + ": " + eh.getCell2() + ")"; // list
																														// upstream
																														// parallel
				return descendantHomology;

			}

			if (cell.startsWith(eh.getCell2())) {

				// check if case 1 i.e. complete match
				if (cell.equals(eh.getCell2())) {
					return eh.getCell1();
				}

				// otherwise, case 1 i.e. descendant --> add suffix
				String suffix = cell.substring(eh.getCell1().length());
				String descendantHomology = eh.getCell1() + suffix + " (" + eh.getCell2() + ": " + eh.getCell1() + ")"; // list
																														// upstream
																														// parallel
				return descendantHomology;
			}
		}
		return "N/A";
	}
}
