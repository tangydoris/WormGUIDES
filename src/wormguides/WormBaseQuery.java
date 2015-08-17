package wormguides;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WormBaseQuery{
	
	private static Service<ArrayList<String>> searchService;
	private static String searched;
	
	static {
		searched = "";
		
		searchService = new Service<ArrayList<String>>() {
			@Override
			protected final Task<ArrayList<String>> createTask() {
				return new Task<ArrayList<String>>() {
					@Override
					protected ArrayList<String> call() throws Exception {
						ArrayList<String> out = new ArrayList<String>();
						
						String[] tokens = searched.trim().split(" ");
						if (tokens.length!=0)
							searched = tokens[0];
						final String searchText = searched.trim();
						
						BufferedReader pageStream = openUrl("http://www.wormbase.org/db/get?name="
																		+searchText+";class=gene");
						
						if (pageStream!= null) {
							String firstQueryLine = "";
							String restString = "";
							try {
								while ((firstQueryLine=pageStream.readLine()) != null && restString.isEmpty()) {
									if (firstQueryLine.contains("wname=\"expression\"")){
										String [] restChunks = pageStream.readLine().split("\"");
										restString = restChunks[1];
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							BufferedReader restPageStream = openUrl("http://www.wormbase.org"+ restString);
							String wbGeneLine = "";
							try {
								while ((wbGeneLine = restPageStream.readLine()) != null) {
									
									Pattern p = Pattern.compile("class=\"anatomy_term-link\" title=\"\">(\\S+)</a>");
									Matcher m = p.matcher(wbGeneLine);
									
									while (m.find()) {
										String name = m.group(1);
										if (AceTreeLoader.isLineageName(name) && !out.contains(name))
											out.add(name);
									}
								}
								pageStream.close();
								restPageStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						out.sort(new Comparator<String>() {
							@Override
							public int compare(String o1, String o2) {
								return o1.compareTo(o2);
							}
						});
						
						return out;
					}
				};
			}
		};
	}
	
	private static BufferedReader openUrl(String target) {
		HttpURLConnection connection = null;
		try{
			URL url = new URL(target);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			//Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    
		    return rd;
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  }
	}
	
	public static void doSearch(String text) {
		searched = text;
		searchService.restart();
	}
	
	public static Service<ArrayList<String>> getSearchService() {
		return searchService;
	}
	
}
