/*
 * Bao Lab 2016
 */

package connectome;

import java.util.ArrayList;
import java.util.List;

import wormguides.view.infowindow.HTMLNode;
import wormguides.view.infowindow.InfoWindowDOM;

import static connectome.ConnectomeLoader.loadConnectome;
import static java.util.Collections.sort;
import static partslist.PartsList.getFunctionalNameByLineageName;
import static partslist.PartsList.getLineageNameByFunctionalName;
import static partslist.PartsList.isFunctionalName;
import static partslist.PartsList.isLineageName;

/**
 * Underlying model of the all neuronal connections. It holds a list of {@link NeuronalSynapse}s that define the
 * wiring between two terminal cells
 */
public class Connectome {

    // synapse types as strings for search logic
    private final static String s_presynapticDescription = "S presynaptic";
    private final static String r_postsynapticDescription = "R postsynaptic";
    private final static String ej_electricalDescription = "EJ electrical";
    private final static String nmj_neuromuscularDescrpition = "Nmj neuromuscular";

    private final String presynapticPartnersTitle = "Presynaptic: ";
    private final String postsynapticPartnersTitle = "Postsynaptic: ";
    private final String electricalPartnersTitle = "Electrical: ";
    private final String neuromusclarPartnersTitle = "Neuromusclar: ";

    private List<NeuronalSynapse> synapses;

    public Connectome() {
        synapses = loadConnectome();
    }

    public List<NeuronalSynapse> getConnectomeList() {
        return synapses;
    }

    public List<String> getAllConnectomeCellNames() {
        // iterate through synapses arraylist and add all cell names
        List<String> allConnectomeCellNames = new ArrayList<>();
        for (NeuronalSynapse ns : synapses) {
            allConnectomeCellNames.add(getLineageNameByFunctionalName(ns.getCell1()));
            allConnectomeCellNames.add(getLineageNameByFunctionalName(ns.getCell2()));
        }
        return allConnectomeCellNames;
    }

    public List<String> getConnectedCells(String centralCell) {
        // find all cells that are connected to the central cell
        List<String> connectedCells = new ArrayList<>();
        for (NeuronalSynapse ns : synapses) {
            if (ns.getCell1().equals(centralCell)) {
                connectedCells.add(ns.getCell2());
            } else if (ns.getCell2().equals(centralCell)) {
                connectedCells.add(ns.getCell1());
            }
        }
        //make sure self isn't in list
        if (connectedCells.contains(centralCell)) {
            connectedCells.remove(centralCell);
        }
        return connectedCells;
    }

    /**
     * Provides name translation from systematic to functional
     *
     * @param queryCell
     *         the cell to be checked
     *
     * @return the resultant translated or untranslated cell name
     */
    public String checkQueryCell(String queryCell) {
        if (isLineageName(queryCell)) {
            queryCell = getFunctionalNameByLineageName(queryCell).toLowerCase();
        }
        return queryCell;
    }

    /**
     * @param queryCell
     *         the cell to query in the synapses
     *
     * @return boolean corresponding to whether the query is in the synapses
     * or not
     */
    public boolean containsCell(String queryCell) {
        queryCell = checkQueryCell(queryCell);
        for (NeuronalSynapse ns : synapses) {
            if (ns.getCell1().toLowerCase().equals(queryCell)
                    || ns.getCell2().toLowerCase().equals(queryCell)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search function which takes cell and filters results based on filter toggles filter toggles = 4 SynapseTypes
     *
     * @param queryCell
     *         lineage name of the cell to be searched for
     * @param isPresynapticTicked
     *         true if the presynaptic search box is ticked, false otherwise
     * @param isPostsynapticTicked
     *         true if the postsynaptic search box is ticked, false otherwise
     * @param isElectricalTicked
     *         true if the electrical search box is ticked, false otherwise
     * @param isNeuromuscularTicked
     *         true if the neuromuscular search box is ticked, false otherwise
     * @param areLineageNamesReturned
     *         true if lineage names should be returned, false otherwise
     *
     * @return the list of connections to the query cell
     */
    public List<String> queryConnectivity(
            String queryCell,
            boolean isPresynapticTicked,
            boolean isPostsynapticTicked,
            boolean isElectricalTicked,
            boolean isNeuromuscularTicked,
            boolean areLineageNamesReturned) {

        // query only works for lineage names
        if (isFunctionalName(queryCell)) {
            queryCell = getLineageNameByFunctionalName(queryCell);
        }

        queryCell = checkQueryCell(queryCell);

        List<String> searchResults = new ArrayList<>();

        // error check
        if (queryCell == null) {
            return searchResults;
        }

        // //iterate over synapses
        for (NeuronalSynapse ns : synapses) {
            // check if synapse contains query cell
            if (ns.getCell1().toLowerCase().contains(queryCell)
                    || ns.getCell2().toLowerCase().contains(queryCell)) {

                String cell1 = ns.getCell1();
                String cell2 = ns.getCell2();

                // process type code
                String synapseTypeDescription = ns.getSynapseType().getDescription();

                // find synapse type code for connection, compare to toggle
                // ticks
                switch (synapseTypeDescription) {
                    case s_presynapticDescription:
                        if (isPresynapticTicked) {
                            // don't add duplicates
                            if (!searchResults.contains(cell1)) {
                                searchResults.add(cell1);
                            }

                            if (!searchResults.contains(cell2)) {
                                searchResults.add(cell2);
                            }
                        }
                        break;
                    case r_postsynapticDescription:
                        if (isPostsynapticTicked) {
                            // don't add duplicates
                            if (!searchResults.contains(cell1)) {
                                searchResults.add(cell1);
                            }

                            if (!searchResults.contains(cell2)) {
                                searchResults.add(cell2);
                            }
                        }
                        break;
                    case ej_electricalDescription:
                        if (isElectricalTicked) {
                            // don't add duplicates
                            if (!searchResults.contains(cell1)) {
                                searchResults.add(cell1);
                            }

                            if (!searchResults.contains(cell2)) {
                                searchResults.add(cell2);
                            }
                        }
                        break;
                    case nmj_neuromuscularDescrpition:
                        if (isNeuromuscularTicked) {
                            // don't add duplicates
                            if (!searchResults.contains(cell1)) {
                                searchResults.add(cell1);
                            }

                            if (!searchResults.contains(cell2)) {
                                searchResults.add(cell2);
                            }
                        }
                        break;
                }
            }
        }

        // Return lineage names instead of functional names if flag is true
        if (areLineageNamesReturned) {
            List<String> lineageNameResults = new ArrayList<>();
            for (String result : searchResults) {
                String lineageName = getLineageNameByFunctionalName(result);

                if (lineageName != null) {
                    lineageNameResults.add(lineageName);
                }
            }
            return lineageNameResults;
        }

        // check if queryCell in results, remove
        if (searchResults.contains(queryCell.toUpperCase())) {
            searchResults.remove(queryCell.toUpperCase());
        }

        return searchResults;
    }

    /**
     * Builds the synapses as a DOM to be displayed in an external popup window
     *
     * @return the DOM of the synapses
     */
    public InfoWindowDOM connectomeDOM() {
        HTMLNode html = new HTMLNode("html");
        HTMLNode head = new HTMLNode("head");
        HTMLNode body = new HTMLNode("body");

        HTMLNode connectomeTablesDiv = new HTMLNode("div");

        // add formatted wiring partners for each cell in synapses

        // collect all unique cells
        List<String> cells = new ArrayList<>();
        for (NeuronalSynapse ns : synapses) {
            String cell_1 = ns.getCell1();
            String cell_2 = ns.getCell2();

            // add unique entries to list
            if (!cells.contains(cell_1)) {
                cells.add(cell_1);
            }

            if (!cells.contains(cell_2)) {
                cells.add(cell_2);
            }
        }

        // alphabetize the synapses cells
        sort(cells);

        // add tables of wiring partners for each unique entry
        for (String cell : cells) {
            connectomeTablesDiv.addChild(queryWiringPartnersAsHTMLTable(cell));
            connectomeTablesDiv.addChild(new HTMLNode("br"));
            connectomeTablesDiv.addChild(new HTMLNode("br"));
        }

        body.addChild(connectomeTablesDiv);
        html.addChild(head);
        html.addChild(body);

        InfoWindowDOM dom = new InfoWindowDOM(html);
        dom.buildStyleNode();

        return dom;
    }

    /**
     * Generates a table of synaptic partners for a given cell
     *
     * @param queryCell
     *         the cell for which the table is generated
     *
     * @return the table HTML node for the DOM
     */
    public HTMLNode queryWiringPartnersAsHTMLTable(String queryCell) {
        // FORMAT Cell Name presynaptic: cellname (numconnections), cellname (numconnections) postsynaptic: ...
        List<String> presynapticPartners = new ArrayList<>();
        List<String> postsynapticPartners = new ArrayList<>();
        List<String> electricalPartners = new ArrayList<>();
        List<String> neuromuscularPartners = new ArrayList<>();

        // get wiring partners
        for (NeuronalSynapse ns : synapses) {
            String cell_1 = ns.getCell1();
            String cell_2 = ns.getCell2();

            if (queryCell.equals(cell_1)) {
                // add cell_2 as a wiring partner

                // extract number of synapses
                int numberOfSynapses = ns.numberOfSynapses();

                // extract synapse type
                String synapseTypeDescription = ns.getSynapseType().getDescription();

                // format wiring partner with cell_2
                String wiringPartner = cell_2 + formatNumberOfSynapses(Integer.toString(numberOfSynapses));

                switch (synapseTypeDescription) {
                    case s_presynapticDescription:
                        presynapticPartners.add(wiringPartner);
                        break;
                    case r_postsynapticDescription:
                        postsynapticPartners.add(wiringPartner);
                        break;
                    case ej_electricalDescription:
                        electricalPartners.add(wiringPartner);
                        break;
                    case nmj_neuromuscularDescrpition:
                        neuromuscularPartners.add(wiringPartner);
                        break;
                }

            } else if (queryCell.equals(cell_2)) {
                // add cell_1 as a wiring partner

                // extract number of synapses
                int numberOfSynapses = ns.numberOfSynapses();

                // extract synapse type
                String synapseTypeDescription = ns.getSynapseType().getDescription();

                // format wiring partner with cell_1
                String wiringPartner = cell_1 + formatNumberOfSynapses(Integer.toString(numberOfSynapses));

                switch (synapseTypeDescription) {
                    case s_presynapticDescription:
                        presynapticPartners.add(wiringPartner);
                        break;
                    case r_postsynapticDescription:
                        postsynapticPartners.add(wiringPartner);
                        break;
                    case ej_electricalDescription:
                        electricalPartners.add(wiringPartner);
                        break;
                    case nmj_neuromuscularDescrpition:
                        neuromuscularPartners.add(wiringPartner);
                        break;
                }
            }
        }

        HTMLNode table = new HTMLNode("table");
        HTMLNode trH = new HTMLNode("th");
        HTMLNode th = new HTMLNode("th", "", "", "Cell: " + queryCell.toUpperCase());

        trH.addChild(th);
        table.addChild(trH);

        HTMLNode trPre;
        HTMLNode trPost;
        HTMLNode trNeuro;
        HTMLNode trElec;

        sort(presynapticPartners); // alphabetize
        if (presynapticPartners.size() > 0) {
            trPre = new HTMLNode("tr");

            HTMLNode tdPreTitle = new HTMLNode("td", "", "", presynapticPartnersTitle);
            HTMLNode tdPre = new HTMLNode("td", "td", "td",
                    presynapticPartners.toString().substring(1, presynapticPartners.toString().length() - 1));

            trPre.addChild(tdPreTitle);
            trPre.addChild(tdPre);

            table.addChild(trPre);
        }

        sort(postsynapticPartners); // alphabetize
        if (postsynapticPartners.size() > 0) {
            trPost = new HTMLNode("tr");

            HTMLNode tdPostTitle = new HTMLNode("td", "", "", postsynapticPartnersTitle);
            HTMLNode tdPost = new HTMLNode("td", "td", "td",
                    postsynapticPartners.toString().substring(1, postsynapticPartners.toString().length() - 1));

            trPost.addChild(tdPostTitle);
            trPost.addChild(tdPost);

            table.addChild(trPost);
        }

        sort(electricalPartners); // alphabetize
        if (electricalPartners.size() > 0) {
            trElec = new HTMLNode("tr");

            HTMLNode tdElecTitle = new HTMLNode("td", "", "", electricalPartnersTitle);
            HTMLNode tdElec = new HTMLNode("td", "td", "td",
                    electricalPartners.toString().substring(1, electricalPartners.toString().length() - 1));

            trElec.addChild(tdElecTitle);
            trElec.addChild(tdElec);

            table.addChild(trElec);
        }

        sort(neuromuscularPartners); // alphabetize
        if (neuromuscularPartners.size() > 0) {
            trNeuro = new HTMLNode("tr");

            HTMLNode tdNeuroTitle = new HTMLNode("td", "", "", neuromusclarPartnersTitle);
            HTMLNode tdNeuro = new HTMLNode("td", "td", "td",
                    neuromuscularPartners.toString().substring(1, neuromuscularPartners.toString().length() - 1));

            trNeuro.addChild(tdNeuroTitle);
            trNeuro.addChild(tdNeuro);

            table.addChild(trNeuro);
        }

        return table;
    }

    private String formatNumberOfSynapses(String numberOfSynapses) {
        return "(" + numberOfSynapses + ")";
    }
}
