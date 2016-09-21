/*
 * Bao Lab 2016
 */

package search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WormBaseQuery {

    private static Service<List<String>> searchService;
    private static String searched;
    private static HashMap<String, List<String>> resultsHash;

    static {
        searched = "";

        searchService = new Service<List<String>>() {
            @Override
            protected final Task<List<String>> createTask() {
                Task<List<String>> task = new Task<List<String>>() {
                    @Override
                    protected List<String> call() throws Exception {
                        final String[] tokens = searched.trim().split(" ");
                        if (tokens.length != 0) {
                            searched = tokens[0];
                        }
                        final String searchText = searched.trim();
                        searched = searchText;

                        // try to get result if previously searched
                        if (resultsHash.containsKey(searched)) {
                            return resultsHash.get(searched);
                        }

                        // do actual search if result was not cached
                        final List<String> out = new ArrayList<>();

                        final BufferedReader pageStream = openUrl(
                                "http://www.wormbase.org/db/get?name=" + searchText + ";class=gene");
                        if (pageStream != null) {
                            String firstQueryLine = "";
                            String restString = "";
                            try {
                                while ((firstQueryLine = pageStream.readLine()) != null && restString.isEmpty()) {
                                    if (firstQueryLine.contains("wname=\"expression\"")) {
                                        String[] restChunks = pageStream.readLine().split("\"");
                                        restString = restChunks[1];
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            BufferedReader restPageStream = openUrl("http://www.wormbase.org" + restString);
                            String wbGeneLine = "";
                            try {
                                while ((wbGeneLine = restPageStream.readLine()) != null) {
                                    final Pattern p = Pattern
                                            .compile("class=\"anatomy_term-link\"" + " title=\"\">(\\S+)</a>");
                                    final Matcher m = p.matcher(wbGeneLine);
                                    while (m.find()) {
                                        final String name = m.group(1);
                                        if (SearchUtil.isLineageName(name) && !out.contains(name)) {
                                            out.add(name);
                                        }
                                    }
                                }

                                pageStream.close();
                                restPageStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(out);
                        resultsHash.put(searched, out);
                        return out;
                    }
                };
                return task;
            }
        };

        resultsHash = new HashMap<>();
    }

    private static BufferedReader openUrl(final String target) {
        HttpURLConnection connection = null;


        try {
            URL url = new URL(target);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            return rd;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void doSearch(final String text) {
        searched = text;
        searchService.restart();
    }

    public static String getSearchedText() {
        return searched;
    }

    public static Service<List<String>> getSearchService() {
        return searchService;
    }

}