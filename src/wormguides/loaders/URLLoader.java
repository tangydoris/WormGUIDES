package wormguides.loaders;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import wormguides.Search;
import wormguides.SearchOption;
import wormguides.controllers.Window3DController;
import wormguides.layers.SearchType;
import wormguides.models.Rule;

public class URLLoader {

	/**
	 * Utility method that processes a url string and sets the correct view
     * parameters in the 3d subscene. Called by {@link wormguides.layers.StoriesLayer} and
     * {@link wormguides.controllers.RootLayoutController} for scene sharing/loading and when changing
     * active/inactive stories. Documentation for URL (old and new APIs)
     * formatting and syntax can be found in URLDocumentation.txt inside the
	 * package wormguides.model.
	 * 
	 * @param url
	 *            String to be parsed (consisting of a prefix url, rules to be
	 *            parsed, and view arguments)
	 * @param window3DController
	 *            Reference to the 3d subscene so that view arguments can be set
	 *            accordingly
	 * @param useInternalScaleFactor
	 *            Boolean that tells the 3d subscene to use the internal scale
	 *            factor, without scaling/translating it to match the old API.
     *            TRUE when called by {@link wormguides.layers.StoriesLayer}, FALSE otherwise.
     */
    public static void process(String url, Window3DController window3DController, boolean useInternalScaleFactor) {
		if (window3DController == null)
			return;

		if (!url.contains("testurlscript?/"))
			return;

		// if no URL is given, revert to internal color rules
		if (url.isEmpty()) {
			return;
		}

		ObservableList<Rule> rulesList = window3DController.getObservableColorRulesList();

		String[] args = url.split("/");
        ArrayList<String> ruleArgs = new ArrayList<>();
        ArrayList<String> viewArgs = new ArrayList<>();

		// add rules and view parameters to their ArrayList's
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("set")) {
				// do not need the 'set' String
				i++;
				// iterate through set parameters until we hit the view
				// parameters
				while (!args[i].equals("view")) {
					ruleArgs.add(args[i].trim());
					i++;
				}

				// iterate through view parameters
				// do not need the 'view' String
				i++;
				while (!args[i].equals("iOS") && !args[i].equals("Android") && !args[i].equals("browser")) {
					viewArgs.add(args[i]);
					i++;
				}
			} else
				i++;
		}

		// process rules, add to current rules list
		parseRules(ruleArgs, rulesList);
		// process view arguments
		parseViewArgs(viewArgs, window3DController, useInternalScaleFactor);
	}

	private static void parseRules(ArrayList<String> rules, ObservableList<Rule> rulesList) {
		rulesList.clear();
		for (String rule : rules) {
            ArrayList<String> types = new ArrayList<>();
            StringBuilder sb = new StringBuilder(rule);
            boolean noTypeSpecified = true;
			boolean isMulticellStructureRule = false;

			try {
				// determine if rule is a cell/cellbody rule, or a multicelllar
				// structure rule

				// multicellular structure rules have a null SearchType
				// parse SearchType args
				if (sb.indexOf("-s") > -1) // systematic/functional
					types.add("-s");
				if (sb.indexOf("-n") > -1) // lineage
					types.add("-n");
				if (sb.indexOf("-d") > -1) // description
					types.add("-d");
				if (sb.indexOf("-g") > -1) // gene
					types.add("-g");
				if (sb.indexOf("-m") > -1) // multicell
					types.add("-m");
				if (sb.indexOf("-c") > -1) // connectome
					types.add("-c");
				if (sb.indexOf("-b") > -1) // neighbor
					types.add("-b");
				
				if (!types.isEmpty()) {
					noTypeSpecified = false;
					for (String arg : types) {
						int i = sb.indexOf(arg);
						sb.replace(i, i + 2, "");
					}
				}

				String colorString = "";
				if (sb.indexOf("+#ff") > -1)
					colorString = sb.substring(sb.indexOf("+#ff") + 4);
				else if (sb.indexOf("+%23ff") > -1)
					colorString = sb.substring(sb.indexOf("+%23ff") + 6);

                ArrayList<SearchOption> options = new ArrayList<>();

				if (noTypeSpecified && sb.indexOf("-M") > -1) {
					options.add(SearchOption.MULTICELLULAR_NAME_BASED);
					int i = sb.indexOf("-M");
					sb.replace(i, i + 2, "");

				} else {
					if (sb.indexOf("%3C") > -1) {
						options.add(SearchOption.ANCESTOR);
						int i = sb.indexOf("%3C");
						sb.replace(i, i + 3, "");
					}
					if (sb.indexOf(">") > -1) {
						options.add(SearchOption.ANCESTOR);
						int i = sb.indexOf(">");
						sb.replace(i, i + 1, "");
					}
					if (sb.indexOf("$") > -1) {
						options.add(SearchOption.CELLNUCLEUS);
						int i = sb.indexOf("$");
						sb.replace(i, i + 1, "");
					}
                    if (rule.contains("%3E")) {
                        options.add(SearchOption.DESCENDANT);
                        int i = sb.indexOf("%3E");
						sb.replace(i, i + 3, "");
					}
					if (sb.indexOf("<") > -1) {
						options.add(SearchOption.DESCENDANT);
						int i = sb.indexOf("<");
						sb.replace(i, i + 1, "");
					}
					if (sb.indexOf("@") > -1) {
						options.add(SearchOption.CELLBODY);
						int i = sb.indexOf("@");
						sb.replace(i, i + 1, "");
					}
				}

				// extract name from what's left of rule
				String name = sb.substring(0, sb.indexOf("+"));

				// add regular ColorRule
				if (!isMulticellStructureRule) {
					if (types.contains("-s"))
						Search.addColorRule(SearchType.LINEAGE, name, Color.web(colorString), options);

					if (types.contains("-n"))
						Search.addColorRule(SearchType.FUNCTIONAL, name, Color.web(colorString), options);

					if (types.contains("-d"))
						Search.addColorRule(SearchType.DESCRIPTION, name, Color.web(colorString), options);

					if (types.contains("-g"))
						Search.addColorRule(SearchType.GENE, name, Color.web(colorString), options);

					if (types.contains("-m"))
						Search.addColorRule(SearchType.MULTICELLULAR_CELL_BASED, name, Color.web(colorString), options);
					
					if (types.contains("-c"))
						Search.addColorRule(SearchType.CONNECTOME, name, Color.web(colorString), options);
					
					if (types.contains("-b"))
						Search.addColorRule(SearchType.NEIGHBOR, name, Color.web(colorString), options);

					// if no type present, default is systematic
					if (noTypeSpecified) {
						SearchType type = SearchType.LINEAGE;
						if (isGeneFormat(name))
							type = SearchType.GENE;
						Search.addColorRule(type, name, Color.web(colorString), options);
					}

				} else { // add multicellular structure rule
					Search.addMulticellularStructureRule(name, Color.web(colorString));
				}

			} catch (StringIndexOutOfBoundsException e) {
				System.out.println("invalid color rule format");
				e.printStackTrace();
			}
		}
	}

	private static boolean isGeneFormat(String name) {
        if (!name.contains("-")) {
            return false;
        }

		String[] tokens = name.split("-");
		if (tokens.length != 2)
			return false;
		try {
			Integer.parseInt(tokens[1]);
		} catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	private static void parseViewArgs(ArrayList<String> viewArgs, Window3DController window3DController,
			boolean useInternalScaleFactor) {
		// manipulate viewArgs arraylist so that rx ry and rz are grouped
		// together
		// to facilitate loading rotations in x and y
		for (int i = 0; i < viewArgs.size(); i++) {
			if (viewArgs.get(i).startsWith("rX")) {
				String ry = viewArgs.get(i + 1);
				String rz = viewArgs.get(i + 2);
				viewArgs.set(i, viewArgs.get(i) + "," + ry + "," + rz);
				break;
			}
		}

		for (String arg : viewArgs) {
			if (arg.startsWith("rX")) {
				String[] tokens = arg.split(",");
				try {
					double rx = Double.parseDouble(tokens[0].split("=")[1]);
					double ry = Double.parseDouble(tokens[1].split("=")[1]);
					double rz = Double.parseDouble(tokens[2].split("=")[1]);
					window3DController.setRotations(rx, ry, rz);
				} catch (NumberFormatException nfe) {
					System.out.println("error in parsing time variable");
					nfe.printStackTrace();
				}

				continue;
			}
			String[] tokens = arg.split("=");
			if (tokens.length != 0) {
				switch (tokens[0]) {
				case "time":
					try {
						window3DController.setTime(Integer.parseInt(tokens[1]));
					} catch (NumberFormatException nfe) {
						System.out.println("error in parsing time variable");
						nfe.printStackTrace();
					}
					break;

				case "tX":
					try {
						window3DController.setTranslationX(Double.parseDouble(tokens[1]));
					} catch (NumberFormatException nfe) {
						System.out.println("error in parsing translation variable");
						nfe.printStackTrace();
					}
					break;

				case "tY":
					try {
						window3DController.setTranslationY(Double.parseDouble(tokens[1]));
					} catch (NumberFormatException nfe) {
						System.out.println("error in parsing translation variable");
						nfe.printStackTrace();
					}
					break;

				case "scale":
					try {
						if (useInternalScaleFactor)
							window3DController.setScaleInternal(Double.parseDouble(tokens[1]));
						else
							window3DController.setScale(Double.parseDouble(tokens[1]));
					} catch (NumberFormatException nfe) {
						System.out.println("error in parsing scale variable");
						nfe.printStackTrace();
					}
					break;

				case "dim":
					try {
						window3DController.setOthersVisibility(Double.parseDouble(tokens[1]));
					} catch (NumberFormatException nfe) {
						System.out.println("error in parsing dim variable");
						nfe.printStackTrace();
					}
					break;
				}
			}
		}
	}
}