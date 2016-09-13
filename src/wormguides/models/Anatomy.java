package wormguides.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import partslist.PartsList;

/**
 * Contains anatomy information for a select number of cells
 *
 * @author katzmanb
 */
public class Anatomy {
    private static ArrayList<String> functionalNames;
    private static ArrayList<String> types;
    private static ArrayList<String> somaLocations;
    private static ArrayList<String> neuriteLocations;
    private static ArrayList<String> morphologicalFeatures;
    private static ArrayList<String> functions;
    private static ArrayList<String> neurotransmitters;

    static {
        functionalNames = new ArrayList<String>();
        types = new ArrayList<String>();
        somaLocations = new ArrayList<String>();
        neuriteLocations = new ArrayList<String>();
        morphologicalFeatures = new ArrayList<String>();
        functions = new ArrayList<String>();
        neurotransmitters = new ArrayList<String>();

        try {

            URL url = PartsList.class.getResource("/wormguides/models/anatomy_file/anatomy.csv");
            InputStream input = url.openStream();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                //valid line has 7 entires
                if (tokenizer.countTokens() == 7) {
                    functionalNames.add(tokenizer.nextToken());
                    types.add(tokenizer.nextToken());
                    somaLocations.add(tokenizer.nextToken());
                    neuriteLocations.add(tokenizer.nextToken());
                    morphologicalFeatures.add(tokenizer.nextToken());
                    functions.add(tokenizer.nextToken());
                    neurotransmitters.add(tokenizer.nextToken());
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Checks if the supplied cell has an anatomy description
     * <p>
     * If a lineage name is given, it is translated to a functional name first
     *
     * @param cell
     *
     * @return
     */
    public static boolean hasAnatomy(String cell) {
        cell = checkQueryCell(cell);

        //check for exact match
        for (String funcName : functionalNames) {
            if (funcName.equals(cell)) {
                return true;
            }
        }

        cell = findRootOfCell(cell);

        //check for match with updated cell name
        for (String funcName : functionalNames) {
            if (funcName.equals(cell)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the base name of a cell i.e. the cell without dorsal, ventral,
     * left, right, etc. classifiers
     *
     * @param cell
     *         the cell to find the base of
     *
     * @return the base name of the cell
     */
    private static String findRootOfCell(String cell) {
        //remove number suffixes, l/r, d/v
        Character lastChar = cell.charAt(cell.length() - 1);
        lastChar = Character.toLowerCase(lastChar);
        if (lastChar == 'r' || lastChar == 'l') {
            cell = cell.substring(0, cell.length() - 1);

            // check if preceding d/v
            lastChar = cell.charAt(cell.length() - 1);
            lastChar = Character.toLowerCase(lastChar);
            if (lastChar == 'd' || lastChar == 'v') {
                cell = cell.substring(0, cell.length() - 1);
            }
        } else if (lastChar == 'd' || lastChar == 'v') { // will l/r ever come
            // before d/v
            cell = cell.substring(0, cell.length() - 1);

            // check if preceding l/r
            lastChar = cell.charAt(cell.length() - 1);
            lastChar = Character.toLowerCase(lastChar);
            if (lastChar == 'l' || lastChar == 'r') {
                cell = cell.substring(0, cell.length() - 1);
            }
        } else if (Character.isDigit(lastChar)) {
            cell = cell.substring(0, cell.length() - 1).toUpperCase();
        }

        return cell;
    }

    /**
     * Provides name translation from systematic to functional (originally used in Connectome.java)
     *
     * @param queryCell
     *         the cell to be checked
     *
     * @return the resultant translated or untranslated cell name
     */
    private static String checkQueryCell(String queryCell) {
        if (PartsList.containsLineageName(queryCell)) {
            queryCell = PartsList.getFunctionalNameByLineageName(queryCell).toUpperCase();
        }

        return queryCell;
    }

    /**
     * Provides anatomy info for a given cell
     *
     * @param cell
     *
     * @return the anatomy information for the given cell
     */
    public static ArrayList<String> getAnatomy(String cell) {
        ArrayList<String> anatomy = new ArrayList<String>();

        if (hasAnatomy(cell)) {
            int idx = -1;

            //exact match
            for (int i = 0; i < functionalNames.size(); i++) {
                if (functionalNames.get(i).equals(cell)) {
                    idx = i;
                    break;
                }
            }

            //if no exact match, update cell and search again
            if (idx == -1) {
                cell = findRootOfCell(cell);

                //check for match with updated cell name
                for (int i = 0; i < functionalNames.size(); i++) {
                    if (functionalNames.get(i).equals(cell)) {
                        idx = i;
                    }
                }
            }

            if (idx != -1) {
                //add functional name
                anatomy.add(functionalNames.get(idx));

                //add type
                if (types.get(idx) != null) {
                    anatomy.add(types.get(idx));
                } else {
                    anatomy.add("*");
                }

                //add soma location
                if (somaLocations.get(idx) != null) {
                    anatomy.add(somaLocations.get(idx));
                } else {
                    anatomy.add("*");
                }

                //add neurite location
                if (neuriteLocations.get(idx) != null) {
                    anatomy.add(neuriteLocations.get(idx));
                } else {
                    anatomy.add("*");
                }

                //add morphological features
                if (morphologicalFeatures.get(idx) != null) {
                    anatomy.add(morphologicalFeatures.get(idx));
                } else {
                    anatomy.add("*");
                }

                //add function
                if (functions.get(idx) != null) {
                    anatomy.add(functions.get(idx));
                } else {
                    anatomy.add("*");
                }

                //add neurotransmitter
                if (neurotransmitters.get(idx) != null) {
                    anatomy.add(neurotransmitters.get(idx));
                } else {
                    anatomy.add("*");
                }
            }
        }
        return anatomy;
    }
}
