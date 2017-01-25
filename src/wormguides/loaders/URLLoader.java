/*
 * Bao Lab 2016
 */

/*
 * Bao Lab 2016
 */

package wormguides.loaders;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;

import wormguides.layers.SearchLayer;
import wormguides.models.colorrule.Rule;
import wormguides.models.colorrule.SearchOption;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import static javafx.scene.paint.Color.web;

import static search.SearchType.CONNECTOME;
import static search.SearchType.DESCRIPTION;
import static search.SearchType.FUNCTIONAL;
import static search.SearchType.LINEAGE;
import static search.SearchType.MULTICELLULAR_STRUCTURE_BY_CELLS;
import static search.SearchType.NEIGHBOR;
import static search.SearchUtil.isGeneFormat;
import static wormguides.models.colorrule.SearchOption.ANCESTOR;
import static wormguides.models.colorrule.SearchOption.CELL_BODY;
import static wormguides.models.colorrule.SearchOption.CELL_NUCLEUS;
import static wormguides.models.colorrule.SearchOption.DESCENDANT;

public class URLLoader {

    /**
     * Processes a url string and sets the correct view parameters in the 3D subscene. Called by
     * {@link wormguides.layers.StoriesLayer} and {@link wormguides.controllers.RootLayoutController} for scene
     * sharing/loading and when changing active/inactive wormguides.stories. Documentation for URL (old and new APIs)
     * formatting and syntax can be found in URLDocumentation.txt inside the package wormguides.model.
     *
     * @param url
     *         subscene parameters and rules URL consisting of a prefix url, rules to be parsed, and view arguments
     */
    public static void process(
            final String url,
            final ObservableList<Rule> rulesList,
            final SearchLayer searchLayer,
            final IntegerProperty timeProperty,
            final DoubleProperty rotateXAngleProperty,
            final DoubleProperty rotateYAngleProperty,
            final DoubleProperty rotateZAngleProperty,
            final DoubleProperty translateXProperty,
            final DoubleProperty translateYProperty,
            final DoubleProperty zoomProperty,
            final DoubleProperty othersOpacityProperty,
            final BooleanProperty rebuildSubsceneFlag) {

        if (!url.contains("testurlscript?/")) {
            return;
        }

        // if no URL is given, revert to internal color rules
        if (url.isEmpty()) {
            return;
        }

        final String[] args = url.split("/");
        final List<String> ruleArgs = new ArrayList<>();
        final List<String> viewArgs = new ArrayList<>();

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

        // process rules
        parseRules(ruleArgs, rulesList, searchLayer);
        // process view arguments
        final int previousTime = timeProperty.get();
        parseViewArgs(
                viewArgs,
                timeProperty,
                rotateXAngleProperty,
                rotateYAngleProperty,
                rotateZAngleProperty,
                translateXProperty,
                translateYProperty,
                zoomProperty,
                othersOpacityProperty);
        // no need to rebuild subscene again if we are not at a different timepoint than before
        // setting the time property triggers a subscene rebuild
        if (timeProperty.get() == previousTime) {
            rebuildSubsceneFlag.set(true);
        }
    }

    /**
     * Parses a list of rules and adds each to the application's active rules list
     *
     * @param ruleStrings
     *         the string representations of rules
     * @param rulesList
     *         the observable rules list
     * @param searchLayer
     *         the search layer to add color rules
     */
    private static void parseRules(
            final List<String> ruleStrings,
            final ObservableList<Rule> rulesList,
            final SearchLayer searchLayer) {
        rulesList.clear();

        final List<String> types = new ArrayList<>();
        final List<SearchOption> options = new ArrayList<>();
        StringBuilder sb;
        boolean noTypeSpecified;
        String colorString;
        String name;
        for (String ruleString : ruleStrings) {
            types.clear();
            sb = new StringBuilder(ruleString);
            noTypeSpecified = false;
            // determine if rule is a cell/cellbody rule, or a multicelllar structure rule
            try {
                // multicellular structure rules have a null SearchType
                // parse SearchType args
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
                // multicellular structure cell-based
                if (sb.indexOf("-m") > -1) {
                    types.add("-m");
                }
                // structure name-based
                if (sb.indexOf("-M") > -1) {
                    types.add("-M");
                }
                // connectome
                if (sb.indexOf("-c") > -1) {
                    types.add("-c");
                }
                // neighbor
                if (sb.indexOf("-b") > -1) {
                    types.add("-b");
                }

                // remove type arguments from url string
                if (!types.isEmpty()) {
                    for (String arg : types) {
                        int i = sb.indexOf(arg);
                        sb.replace(i, i + 2, "");
                    }
                } else {
                    noTypeSpecified = true;
                }

                colorString = "";
                if (sb.indexOf("+#ff") > -1) {
                    colorString = sb.substring(sb.indexOf("+#ff") + 4);
                } else if (sb.indexOf("+%23ff") > -1) {
                    colorString = sb.substring(sb.indexOf("+%23ff") + 6);
                }

                options.clear();
                int i;
                if (sb.indexOf("%3C") > -1) {
                    options.add(ANCESTOR);
                    i = sb.indexOf("%3C");
                    sb.replace(i, i + 3, "");
                } else if (sb.indexOf(">") > -1) {
                    options.add(ANCESTOR);
                    i = sb.indexOf(">");
                    sb.replace(i, i + 1, "");
                }
                if (sb.indexOf("$") > -1) {
                    options.add(CELL_NUCLEUS);
                    i = sb.indexOf("$");
                    sb.replace(i, i + 1, "");
                }
                if (ruleString.contains("%3E")) {
                    options.add(DESCENDANT);
                    i = sb.indexOf("%3E");
                    sb.replace(i, i + 3, "");
                }
                if (sb.indexOf("<") > -1) {
                    options.add(DESCENDANT);
                    i = sb.indexOf("<");
                    sb.replace(i, i + 1, "");
                }
                if (sb.indexOf("@") > -1) {
                    options.add(CELL_BODY);
                    i = sb.indexOf("@");
                    sb.replace(i, i + 1, "");
                }

                // extract name from what's left of rule
                name = sb.substring(0, sb.indexOf("+"));
                // add regular ColorRule
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
                    searchLayer.addGeneColorRuleFromUrl(name, web(colorString), options);
                }
                if (types.contains("-m")) {
                    searchLayer.addColorRule(
                            MULTICELLULAR_STRUCTURE_BY_CELLS,
                            name,
                            web(colorString),
                            options);
                }
                if (types.contains("-M")) {
                    searchLayer.addStructureRuleBySceneName(name.replace("=", " "), web(colorString));
                }
                if (types.contains("-c")) {
                    searchLayer.addColorRule(CONNECTOME, name, web(colorString), options);
                }
                if (types.contains("-b")) {
                    searchLayer.addColorRule(NEIGHBOR, name, web(colorString), options);
                }

                // if no type present, default is systematic or gene
                if (noTypeSpecified) {
                    if (isGeneFormat(name)) {
                        searchLayer.addGeneColorRuleFromUrl(name, web(colorString), options);
                    } else {
                        searchLayer.addColorRule(LINEAGE, name, web(colorString), options);
                    }
                }
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("Invalid color rule format");
                e.printStackTrace();
            }
        }

        // after all rules have been added, see if any gene rules need to have their
    }

    private static void parseViewArgs(
            final List<String> viewArgs,
            final IntegerProperty timeProperty,
            final DoubleProperty rotateXAngleProperty,
            final DoubleProperty rotateYAngleProperty,
            final DoubleProperty rotateZAngleProperty,
            final DoubleProperty translateXProperty,
            final DoubleProperty translateYProperty,
            final DoubleProperty zoomProperty,
            final DoubleProperty othersOpacityProperty) {

        // time component of the view args is parsed into this variable
        // time property updated after all other view args are updated since it triggers a subscene rebuild
        int newTime = timeProperty.get();

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
                    double rx = parseDouble(tokens[0].split("=")[1]);
                    double ry = parseDouble(tokens[1].split("=")[1]);
                    double rz = parseDouble(tokens[2].split("=")[1]);
                    requireNonNull(rotateXAngleProperty).set(rx);
                    requireNonNull(rotateYAngleProperty).set(ry);
                    requireNonNull(rotateZAngleProperty).set(rz);
                } catch (NumberFormatException nfe) {
                    System.out.println("error in parsing rotation variables");
                    nfe.printStackTrace();
                }
                continue;
            }

            final String[] tokens = arg.split("=");
            if (tokens.length != 0) {
                switch (tokens[0]) {
                    case "time":
                        try {
                            newTime = parseInt(tokens[1]);
                        } catch (NumberFormatException nfe) {
                            System.out.println("error in parsing time variable");
                            nfe.printStackTrace();
                        }
                        break;
                    case "tX":
                        try {
                            requireNonNull(translateXProperty).set(parseDouble(tokens[1]));
                        } catch (NumberFormatException nfe) {
                            System.out.println("error in parsing x translation");
                            nfe.printStackTrace();
                        }
                        break;
                    case "tY":
                        try {
                            requireNonNull(translateYProperty).set(parseDouble(tokens[1]));
                        } catch (NumberFormatException nfe) {
                            System.out.println("error in parsing y translation");
                            nfe.printStackTrace();
                        }
                        break;
                    case "scale":
                        try {
                            requireNonNull(zoomProperty).set(parseDouble(tokens[1]));
                        } catch (NumberFormatException nfe) {
                            System.out.println("error in parsing scale variable");
                            nfe.printStackTrace();
                        }
                        break;
                    case "dim":
                        try {
                            requireNonNull(othersOpacityProperty).set(parseDouble(tokens[1]));
                        } catch (NumberFormatException nfe) {
                            System.out.println("error in parsing dim variable");
                            nfe.printStackTrace();
                        }
                        break;
                }
            }
        }
        timeProperty.set(newTime);
    }
}