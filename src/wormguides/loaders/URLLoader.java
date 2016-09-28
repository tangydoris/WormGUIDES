/*
 * Bao Lab 2016
 */

package wormguides.loaders;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

import search.SearchType;
import search.SearchUtil;
import wormguides.controllers.Window3DController;
import wormguides.layers.SearchLayer;
import wormguides.models.Rule;
import wormguides.models.SearchOption;

import static javafx.scene.paint.Color.web;

import static search.SearchType.CONNECTOME;
import static search.SearchType.DESCRIPTION;
import static search.SearchType.FUNCTIONAL;
import static search.SearchType.GENE;
import static search.SearchType.LINEAGE;
import static search.SearchType.MULTICELLULAR_CELL_BASED;
import static search.SearchType.NEIGHBOR;
import static wormguides.models.SearchOption.ANCESTOR;
import static wormguides.models.SearchOption.CELL_BODY;
import static wormguides.models.SearchOption.CELL_NUCLEUS;
import static wormguides.models.SearchOption.DESCENDANT;
import static wormguides.models.SearchOption.MULTICELLULAR_NAME_BASED;

public class URLLoader {

    /**
     * Processes a url string and sets the correct view parameters in the 3D subscene. Called by
     * {@link wormguides.layers.StoriesLayer} and {@link wormguides.controllers.RootLayoutController} for scene
     * sharing/loading and when changing active/inactive wormguides.stories. Documentation for URL (old and new APIs)
     * formatting and syntax can be found in URLDocumentation.txt inside the package wormguides.model.
     *
     * @param url
     *         subscene parameters and rules URL consisting of a prefix url, rules to be parsed, and view arguments
     * @param window3DController
     *         the controller for the 3D subscene
     * @param useInternalScaleFactor
     *         true when called by the {@link wormguides.layers.StoriesLayer} telling the 3D subscene to use the
     *         internal scale factor, false otherwise
     */
    public static void process(
            final String url,
            final Window3DController window3DController,
            final boolean useInternalScaleFactor,
            final SearchLayer searchLayer) {

        if (window3DController == null) {
            return;
        }

        if (!url.contains("testurlscript?/")) {
            return;
        }

        // if no URL is given, revert to internal color rules
        if (url.isEmpty()) {
            return;
        }

        ObservableList<Rule> rulesList = window3DController.getObservableColorRulesList();

        String[] args = url.split("/");
        List<String> ruleArgs = new ArrayList<>();
        List<String> viewArgs = new ArrayList<>();

        // add rules and view parameters to their ArrayList's
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("set")) {
                // do not need the 'set' String
                i++;
                // iterate through set parameters until we hit the view parameters
                while (!args[i].equals("view")) {
                    ruleArgs.add(args[i].trim());
                    i++;
                }

                // iterate through view parameters do not need the 'view' String
                i++;
                while (!args[i].equals("iOS") && !args[i].equals("Android") && !args[i].equals("browser")) {
                    viewArgs.add(args[i]);
                    i++;
                }
            } else {
                i++;
            }
        }

        // process rules, add to current rules list
        parseRules(ruleArgs, rulesList, searchLayer);
        // process view arguments
        parseViewArgs(viewArgs, window3DController, useInternalScaleFactor);
    }

    private static void parseRules(
            final List<String> rules,
            final ObservableList<Rule> rulesList,
            final SearchLayer searchLayer) {

        rulesList.clear();
        for (String rule : rules) {
            final List<String> types = new ArrayList<>();
            final StringBuilder sb = new StringBuilder(rule);
            boolean noTypeSpecified = true;
            boolean isMulticellStructureRule = false;

            // determine if rule is a cell/cellbody rule, or a multicelllar structure rule
            try {
                // multicellular structure rules have a null SearchType parse SearchType args
                // systematic/functional
                if (sb.indexOf("-s") > -1) {
                    types.add("-s");
                }
                // lineage
                if (sb.indexOf("-n") > -1) {
                    types.add("-n");
                }
                // description
                if (sb.indexOf("-d") > -1) {
                    types.add("-d");
                }
                // gene
                if (sb.indexOf("-g") > -1) {
                    types.add("-g");
                }
                // multicell
                if (sb.indexOf("-m") > -1) {
                    types.add("-m");
                }
                // connectome
                if (sb.indexOf("-c") > -1) {
                    types.add("-c");
                }
                // neighbor
                if (sb.indexOf("-b") > -1) {
                    types.add("-b");
                }

                if (!types.isEmpty()) {
                    noTypeSpecified = false;
                    for (String arg : types) {
                        int i = sb.indexOf(arg);
                        sb.replace(i, i + 2, "");
                    }
                }

                String colorString = "";
                if (sb.indexOf("+#ff") > -1) {
                    colorString = sb.substring(sb.indexOf("+#ff") + 4);
                } else if (sb.indexOf("+%23ff") > -1) {
                    colorString = sb.substring(sb.indexOf("+%23ff") + 6);
                }

                final List<SearchOption> options = new ArrayList<>();
                if (noTypeSpecified && sb.indexOf("-M") > -1) {
                    options.add(MULTICELLULAR_NAME_BASED);
                    int i = sb.indexOf("-M");
                    sb.replace(i, i + 2, "");
                } else {
                    if (sb.indexOf("%3C") > -1) {
                        options.add(ANCESTOR);
                        int i = sb.indexOf("%3C");
                        sb.replace(i, i + 3, "");
                    }
                    if (sb.indexOf(">") > -1) {
                        options.add(ANCESTOR);
                        int i = sb.indexOf(">");
                        sb.replace(i, i + 1, "");
                    }
                    if (sb.indexOf("$") > -1) {
                        options.add(CELL_NUCLEUS);
                        int i = sb.indexOf("$");
                        sb.replace(i, i + 1, "");
                    }
                    if (rule.contains("%3E")) {
                        options.add(DESCENDANT);
                        int i = sb.indexOf("%3E");
                        sb.replace(i, i + 3, "");
                    }
                    if (sb.indexOf("<") > -1) {
                        options.add(DESCENDANT);
                        int i = sb.indexOf("<");
                        sb.replace(i, i + 1, "");
                    }
                    if (sb.indexOf("@") > -1) {
                        options.add(CELL_BODY);
                        int i = sb.indexOf("@");
                        sb.replace(i, i + 1, "");
                    }
                }

                // extract name from what's left of rule
                final String name = sb.substring(0, sb.indexOf("+"));
                // add regular ColorRule
                if (!isMulticellStructureRule) {
                    if (types.contains("-s")) {
                        searchLayer.addColorRule(LINEAGE, name, web(colorString), options);
                    }
                    if (types.contains("-n")) {
                        searchLayer.addColorRule(FUNCTIONAL, name, web(colorString), options);
                    }
                    if (types.contains("-d")) {
                        searchLayer.addColorRule(DESCRIPTION, name, web(colorString), options);
                    }
                    if (types.contains("-g")) {
                        searchLayer.addColorRule(GENE, name, web(colorString), options);
                    }
                    if (types.contains("-m")) {
                        searchLayer.addColorRule(
                                MULTICELLULAR_CELL_BASED,
                                name,
                                web(colorString),
                                options);
                    }
                    if (types.contains("-c")) {
                        searchLayer.addColorRule(CONNECTOME, name, web(colorString), options);
                    }
                    if (types.contains("-b")) {
                        searchLayer.addColorRule(NEIGHBOR, name, web(colorString), options);
                    }

                    // if no type present, default is systematic
                    if (noTypeSpecified) {
                        SearchType type = LINEAGE;
                        if (SearchUtil.isGeneFormat(name)) {
                            type = GENE;
                        }
                        searchLayer.addColorRule(type, name, web(colorString), options);
                    }

                } else { // add multicellular structure rule
                    searchLayer.addMulticellularStructureRule(name, web(colorString));
                }

            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("Invalid color rule format");
                e.printStackTrace();
            }
        }
    }

    private static void parseViewArgs(
            final List<String> viewArgs,
            final Window3DController window3DController,
            final boolean useInternalScaleFactor) {

        // manipulate viewArgs arraylist so that rx ry and rz are grouped together to facilitate loading rotations in
        // x and y
        for (int i = 0; i < viewArgs.size(); i++) {
            if (viewArgs.get(i).startsWith("rX")) {
                viewArgs.set(i, viewArgs.get(i) + "," + viewArgs.get(i + 1) + "," + viewArgs.get(i + 2));
                break;
            }
        }

        for (String arg : viewArgs) {
            if (arg.startsWith("rX")) {
                final String[] tokens = arg.split(",");

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

            final String[] tokens = arg.split("=");
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
                            if (useInternalScaleFactor) {
                                window3DController.setScaleInternal(Double.parseDouble(tokens[1]));
                            } else {
                                window3DController.setScale(Double.parseDouble(tokens[1]));
                            }
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