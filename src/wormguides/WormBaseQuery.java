package wormguides;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wormguides.model.PartsList;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class WormBaseQuery{
	
	private static ArrayList<String> results;
	private static Service<Void> searchService;
	private static String searched;
	
	static {
		searched = "";
		results = new ArrayList<String>();
		searchService = new Service<Void>() {
			@Override
			protected final Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						results.clear();
						String[] tokens = searched.trim().split(" ");
						if (tokens.length!=0)
							searched = tokens[0];
						final String searchText = searched.trim();
						
						BufferedReader pageStream = openUrl("http://www.wormbase.org/db/get?name="+searchText+";class=gene");
						
						if (pageStream!= null) {
							String firstQueryLine = "";
							String restString = "";
							try {
								while ((firstQueryLine = pageStream.readLine()) != null && restString == "") {
									if (firstQueryLine.contains("wname=\"expression\"")){
										String [] restChunks = pageStream.readLine().split("\"");
										restString= restChunks[1];
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
										results.add(m.group(1));
									}

								}
								pageStream.close();
								restPageStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						for (String name : results) {
							System.out.println(name);
						}
						return null;
					}
				};
				task.setOnScheduled(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("search results refresh service scheduled");
					}
				});
				task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("search results refresh service cancelled");
					}
				});
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("search results refresh service succeeded");
					}
				});
				return task;
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
	
	private static ArrayList<String> doSearch(String text) {
		searched = text;
		searchService.restart();
		return results;
	}
	
	public static ArrayList<String> getResultsList(String text) {
		return doSearch(text);
	}
	
	public static Service<Void> getSearchService() {
		return searchService;
	}
	
	public static void main(String[] args) {
		doSearch("pha-4");
	}
}
