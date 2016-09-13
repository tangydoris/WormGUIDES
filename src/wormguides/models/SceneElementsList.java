package wormguides.models;
/*
 * Reference class for Scene Elements over life of embryo
 * Data structure which contains SceneElements
 *
 * Created: 0ct. 30, 2015
 * Author: Braden Katzman
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import wormguides.view.HTMLNode;
import wormguides.view.InfoWindowDOM;

public class SceneElementsList {

    private final static String CELL_CONFIG_FILE_NAME = "CellShapesConfig.csv";
    private final static String asterisk = "*";
    private ArrayList<SceneElement> elementsList;
    private HashMap<String, ArrayList<String>> nameCellsMap;
    private HashMap<String, String> nameCommentsMap;

    // this will eventually be constructed using a .txt file that contains the
    // Scene Element information for the embryo
    public SceneElementsList() {
        elementsList = new ArrayList<>();
        nameCellsMap = new HashMap<>();
        nameCommentsMap = new HashMap<>();

        buildListFromConfig();
    }

    public boolean isSceneElementName(String name) {
        for (SceneElement se : elementsList) {
            if (se.getSceneName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void buildListFromConfig() {

        URL url = SceneElementsList.class.getResource("/wormguides/models/shapes_file/" + CELL_CONFIG_FILE_NAME);

        try {
            if (url != null) {
                InputStream stream = url.openStream();
                processStreamString(stream);
                processCells();
            }

        } catch (IOException e) {
            System.out.println("The config file '" + CELL_CONFIG_FILE_NAME + "' wasn't found on the system.");
        }
    }

    private void processStreamString(InputStream stream) {
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);

        try {
            reader.readLine();

            String line;
            // process each line
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(",", 8);

                // BUIILD SCENE ELEMENT
                // vector of cell names
                ArrayList<String> cellNames = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(splits[1]);
                while (st.hasMoreTokens()) {
                    cellNames.add(st.nextToken());
                }

                try {
                    SceneElement se = new SceneElement(// objEntries,
                            splits[0], cellNames, splits[2], splits[3], splits[4], Integer.parseInt(splits[5]),
                            Integer.parseInt(splits[6]), splits[7]);

                    // add scene element to list
                    elementsList.add(se);
                    addComments(se);

                } catch (NumberFormatException e) {
                    System.out.println("error in reading scene element time for line " + line);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Invalid file: '" + CELL_CONFIG_FILE_NAME);
        }
    }

    /*
     * the start of our recursive algorithm to unpack entries in cell names
     * which reference other scene elements' cell list
     */
    private void processCells() {
        if (elementsList == null) {
            return;
        }

        for (SceneElement se : elementsList) {
            ArrayList<String> cells = se.getAllCellNames();
            for (int i = 0; i < cells.size(); i++) {
                if (cells.get(i).startsWith(asterisk)) {
                    se.setNewCellNames(unpackCells(cells));
                }
            }
        }
    }

    private ArrayList<String> unpackCells(ArrayList<String> cells) {
        ArrayList<String> unpackedCells = new ArrayList<>();

        for (String cell : cells) {
            // if cell starts with asterisk, recurse. else, add cel
            if (cell.startsWith(asterisk)) {
                // find the matching resource location
// recursively unpack matching location's cell list
                elementsList.stream()
                        .filter(se -> se.getResourceLocation().endsWith(cell.substring(1)))
                        .forEachOrdered(se -> {
                            // recursively unpack matching location's cell list
                            unpackedCells.addAll(unpackCells(se.getAllCellNames()));
                        });
            } else {
                // only add cell name entry if not already added
                if (!unpackedCells.contains(cell)) {
                    unpackedCells.add(cell);
                }
            }
        }

        return unpackedCells;
    }

    /*
     * Returns the biological time (without frame offset) of the first
     * occurrence of element with scene name, name
     */
    public int getFirstOccurrenceOf(String name) {
        int time = Integer.MIN_VALUE;

        for (SceneElement element : elementsList) {
            if (element.getSceneName().equalsIgnoreCase(name)) {
                time = element.getStartTime();
            }
        }

        return time + 1;
    }

    /*
     * Returns the biological time (without frame offset) of the last occurrence
     * of element with scene name, name
     */
    public int getLastOccurrenceOf(String name) {
        int time = Integer.MIN_VALUE;

        for (SceneElement element : elementsList) {
            if (element.getSceneName().equalsIgnoreCase(name)) {
                time = element.getEndTime();
            }
        }

        return time + 1;
    }

    private void addComments(SceneElement element) {
        if (element != null && element.isMulticellular()) {
            nameCommentsMap.put(element.getSceneName().toLowerCase(), element.getComments());
        }
    }

    public void addSceneElement(SceneElement element) {
        if (element != null) {
            elementsList.add(element);
        }

        addComments(element);
    }

    public String[] getSceneElementNamesAtTime(int time) {
        // Add lineage names of all structures at time
        ArrayList<String> list = new ArrayList<>();
        elementsList.stream().filter(se -> se.existsAtTime(time)).forEachOrdered(se -> {
            if (se.isMulticellular()) {
                list.add(se.getSceneName());
            } else {
                list.add(se.getAllCellNames().get(0));
            }
        });

        return list.toArray(new String[list.size()]);
    }

    public ArrayList<SceneElement> getSceneElementsAtTime(int time) {
        ArrayList<SceneElement> sceneElements = elementsList.stream()
                .filter(se -> se.existsAtTime(time))
                .collect(Collectors.toCollection(ArrayList::new));
        return sceneElements;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("scene elements list:\n");
        for (SceneElement se : elementsList) {
            sb.append(se.getSceneName()).append("\n");
        }
        return sb.toString();
    }

    public ArrayList<String> getAllMulticellSceneNames() {
        ArrayList<String> names = new ArrayList<>();
        elementsList.stream()
                .filter(se -> se.isMulticellular() && !names.contains(se))
                .forEachOrdered(se -> names.add(se.getSceneName()));
        return names;
    }

    public ArrayList<SceneElement> getMulticellSceneElements() {
        ArrayList<SceneElement> elements = new ArrayList<>();
        elementsList.stream()
                .filter(se -> se.isMulticellular() && !elements.contains(se))
                .forEachOrdered(elements::add);
        return elements;
    }

    public boolean isMulticellStructureName(String name) {
        for (String cellName : getAllMulticellSceneNames()) {
            if (cellName.equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

    public String getCommentByName(String name) {
        String comment = nameCommentsMap.get(name.trim().toLowerCase());
        if (comment == null) {
            return "";
        }
        return comment;
    }

    public HashMap<String, String> getNameToCommentsMap() {
        return nameCommentsMap;
    }

    public HashMap<String, ArrayList<String>> getNameToCellsMap() {
        return this.nameCellsMap;
    }

    public InfoWindowDOM sceneElementsListDOM() {
        HTMLNode html = new HTMLNode("html");
        HTMLNode head = new HTMLNode("head");
        HTMLNode body = new HTMLNode("body");

        // add nodes to html
        html.addChild(head);
        html.addChild(body);

        HTMLNode sceneElementsListDiv = new HTMLNode("div");
        HTMLNode sceneElementsListTable = new HTMLNode("table");

        // title row
        HTMLNode trH = new HTMLNode("tr");
        HTMLNode th1 = new HTMLNode("th", "", "", "Scene Name");
        HTMLNode th2 = new HTMLNode("th", "", "", "Cell Names");
        HTMLNode th3 = new HTMLNode("th", "", "", "Marker");
        HTMLNode th4 = new HTMLNode("th", "", "", "Start Time");
        HTMLNode th5 = new HTMLNode("th", "", "", "End Time");
        HTMLNode th6 = new HTMLNode("th", "", "", "Comments");

        trH.addChild(th1);
        trH.addChild(th2);
        trH.addChild(th3);
        trH.addChild(th4);
        trH.addChild(th5);
        trH.addChild(th6);

        sceneElementsListTable.addChild(trH);

        for (SceneElement se : elementsList) {
            HTMLNode tr = new HTMLNode("tr");
            tr.addChild(new HTMLNode("td", "", "", se.getSceneName()));
            tr.addChild(new HTMLNode("td", "", "", se.getAllCellNames().toString()));
            tr.addChild(new HTMLNode("td", "", "", se.getMarkerName()));
            tr.addChild(new HTMLNode("td", "", "", Integer.toString(se.getStartTime())));
            tr.addChild(new HTMLNode("td", "", "", Integer.toString(se.getEndTime())));
            tr.addChild(new HTMLNode("td", "", "", se.getComments()));
            sceneElementsListTable.addChild(tr);
        }

        sceneElementsListDiv.addChild(sceneElementsListTable);

        body.addChild(sceneElementsListDiv);

        InfoWindowDOM dom = new InfoWindowDOM(html);
        dom.buildStyleNode();

        return dom;
    }

    public ArrayList<SceneElement> getElementsList() {
        return elementsList;
    }
}