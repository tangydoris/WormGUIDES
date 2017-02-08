package wormguides.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

import wormguides.MainApp;

public class ParametersLoader {
	private static final String PARAMETERS_FILE_PATH = "/wormguides/model/parameters_file/parameters.txt";
	
	public static HashMap<String, String> loadParameters() {
		final URL url = MainApp.class.getResource("/wormguides/model/parameters_file/parameters.txt");
		final HashMap<String, String> param_map = new HashMap<String, String>();
		
		try (final InputStreamReader isr = new InputStreamReader(url.openStream());
	             final BufferedReader br = new BufferedReader(isr)) {
			
			String line;
			while((line = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(line, " ");
				
				if (st.countTokens() == 2) {
					param_map.put(st.nextToken(), st.nextToken());
				}
			}
					
		} catch (IOException e) {
            System.out.println("The parameters file "
                    + PARAMETERS_FILE_PATH
                    + " wasn't found on the system.");
        }
		
		return param_map;
	}
}