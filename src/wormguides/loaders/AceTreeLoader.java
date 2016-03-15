package wormguides.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import wormguides.model.TableLineageData;

// Loader class to read nuclei files
public class AceTreeLoader {

	private static ArrayList<String> allCellNames = new ArrayList<String>();

	public static TableLineageData loadNucFiles() {

		TableLineageData tld = new TableLineageData(allCellNames);

		try {
			tld.addFrame(); //accounts for first tld.addFrame() added when reading from JAR --> from dir name first entry match
			URL url;
			int i = 1;
			for (; i < 10; i++) {
				url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + twoZeroPad + i + ENTRY_EXT);
				if (url != null) {
					process(tld, i, url.openStream());
				} else {
					System.out.println("Could not process file: " + ENTRY_PREFIX + t + twoZeroPad + i + ENTRY_EXT);
				}
			}
			
			for (; i < 100; i++) {
				url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + oneZeroPad + i + ENTRY_EXT);
				if (url != null) { 
					process(tld, i, url.openStream());
				} else {
					System.out.println("Could not process file: " + ENTRY_PREFIX + t + oneZeroPad + i + ENTRY_EXT);
				}
			}
			
			for (;i < 401; i++) {
				url = AceTreeLoader.class.getResource(ENTRY_PREFIX + t + i + ENTRY_EXT);
				if (url != null) {
					process(tld, i, url.openStream());
				} else {
					System.out.println("Could not process file: " + ENTRY_PREFIX + t + i + ENTRY_EXT);
				}
			}

			
//			JarFile jarFile = new JarFile(new File("WormGUIDES.jar"));
//
//			Enumeration<JarEntry> entries = jarFile.entries();
//			int time = 0;
//
//			JarEntry entry;
//			while (entries.hasMoreElements()) {
//				entry = entries.nextElement();
//				if (entry.getName().startsWith(ENTRY_PREFIX)) {
//					InputStream input = jarFile.getInputStream(entry);
//					process(tld, time++, input);
//				}
//			}
//
//			jarFile.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return tld;
	}

	private static void process(TableLineageData tld, int time, InputStream input) throws IOException {
		tld.addFrame();

		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = new String[TOKEN_ARRAY_SIZE];
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			int k = 0;
			while (tokenizer.hasMoreTokens())
				tokens[k++] = tokenizer.nextToken().trim();

			int valid = Integer.parseInt(tokens[VALID]);
			if (valid == 1) {
				makeNucleus(tld, time, tokens);
			}
		}

		reader.close();
	}

	private static void makeNucleus(TableLineageData tld, int time, String[] tokens) {
		try {
			String name = tokens[IDENTITY];
			int x = Integer.parseInt(tokens[XCOR]);
			int y = Integer.parseInt(tokens[YCOR]);
			int z = (int) Math.round(Double.parseDouble(tokens[ZCOR]));
			int diameter = Integer.parseInt(tokens[DIAMETER]);

			tld.addNucleus(time, name, x, y, z, diameter);
		} catch (NumberFormatException nfe) {
			System.out.println("Incorrect format in nucleus file for time " + time + ".");
		}
	}

	public static boolean isLineageName(String name) {
		return allCellNames.contains(name);
	}

	private static final String ENTRY_PREFIX = "/wormguides/model/nuclei_files/";
	private static final String t = "t";
	private static final String ENTRY_EXT = "-nuclei";
	private static final int TOKEN_ARRAY_SIZE = 21;
	private static final int VALID = 1, XCOR = 5, YCOR = 6, ZCOR = 7, DIAMETER = 8, IDENTITY = 9;
	private final static String oneZeroPad = "0";
	private final static String twoZeroPad = "00";

}
