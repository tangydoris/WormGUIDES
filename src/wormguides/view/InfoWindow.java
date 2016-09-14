package wormguides.view;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import wormguides.Search;
import wormguides.controllers.InfoWindowLinkController;
import wormguides.model.CasesLists;
import wormguides.model.Connectome;
import wormguides.model.LineageData;
import wormguides.model.PartsList;
import wormguides.model.ProductionInfo;

/**
 * The top level container for the list of Info Window cell cases pages. This holds the tabpane of cases
 * 
 * @author katzmanb
 *
 */

public class InfoWindow {

	private Stage infoWindowStage;
	private TabPane tabPane;
	private Scene scene;
	private Stage parentStage; // update scenes on links
//	private IntegerProperty time;
	private InfoWindowLinkController linkController;

	private ProductionInfo productionInfo;
	private String nameToQuery;
	private Service<Void> addNameService;
	private Service<Void> showLoadingService;

	private int count; //to show loading in progress
	
	private boolean defaultEmbryoFlag;
	private LineageData lineageData;

	public InfoWindow(Stage stage, StringProperty cellNameProperty, CasesLists cases, ProductionInfo info,
			Connectome connectome, boolean defaultEmbryoFlag, LineageData lineageData) {
		this.defaultEmbryoFlag = defaultEmbryoFlag;
		this.lineageData = lineageData;
		
		infoWindowStage = new Stage();
		infoWindowStage.setTitle("Cell Info Window");

		productionInfo = info;
		tabPane = new TabPane();

		scene = new Scene(new Group());
		scene.setRoot(tabPane);

		infoWindowStage.setScene(scene);

		infoWindowStage.setMinHeight(300);
		infoWindowStage.setMinWidth(600);
		infoWindowStage.setHeight(620);
		infoWindowStage.setWidth(950);

		infoWindowStage.setResizable(true);

		parentStage = stage;
		linkController = new InfoWindowLinkController(parentStage, cellNameProperty);

		count = 0;
		showLoadingService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						final int modulus = 5;
						while (true) {
							if (isCancelled()) {
								infoWindowStage.setTitle("Cell Info Window");
								break;
							}
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									String loading = "Loading";
									int num = count % modulus;
									switch (num) {
									case 1:
										loading += ".";
										break;
									case 2:
										loading += "..";
										break;
									case 3:
										loading += "...";
										break;
									case 4:
										loading += "....";
										break;
									default:
										//loading = "Cell Info Window";
										break;
									}
									infoWindowStage.setTitle(loading);
								}
							});
							try {
								Thread.sleep(WAIT_TIME_MILLI);
								count++;
								if (count < 0)
									count = 0;
							} catch (InterruptedException ie) {
								break;
							}
						}
						return null;
					}
				};
			}
		};

		addNameService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				final Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						// GENERATE CELL TAB ON CLICK
						final String lineageName = nameToQuery;
						
						if (!defaultEmbryoFlag && !lineageData.isSulstonMode()) {
							System.out.println("first one");
							return null;
						}
						else if(!defaultEmbryoFlag && lineageData.isSulstonMode() && nameToQuery.startsWith(Nuc)) {
							System.out.println("second one");
							return null;
						}

						if (lineageName != null && !lineageName.isEmpty()) {
							if (cases == null) {
								System.out.println("null cell cases");
								return null; // error check
							}

							if (PartsList.containsLineageName(lineageName)) {
								if (cases.containsCellCase(lineageName)) {

									// show the tab
								} else {
									// translate the name if necessary
									// String tabTitle =
									// connectome.checkQueryCell(queryName).toUpperCase();
									// String tabTitle = queryName;
									// add a terminal case --> pass the wiring
									// partners
									
									// check default flag for image series info validation
									if (defaultEmbryoFlag) {
										String funcName = connectome.checkQueryCell(lineageName).toUpperCase();
										cases.makeTerminalCase(lineageName, funcName,
												connectome.queryConnectivity(funcName, true, false, false, false, false),
												connectome.queryConnectivity(funcName, false, true, false, false, false),
												connectome.queryConnectivity(funcName, false, false, true, false, false),
												connectome.queryConnectivity(funcName, false, false, false, true, false),
												productionInfo.getNuclearInfo(), productionInfo.getCellShapeData(funcName));
									} else {
										System.out.println("here");
										String funcName = connectome.checkQueryCell(lineageName).toUpperCase();
										cases.makeTerminalCase(lineageName, funcName,
												connectome.queryConnectivity(funcName, true, false, false, false, false),
												connectome.queryConnectivity(funcName, false, true, false, false, false),
												connectome.queryConnectivity(funcName, false, false, true, false, false),
												connectome.queryConnectivity(funcName, false, false, false, true, false),
												new ArrayList<String>(), 
												new ArrayList<String>());
									}
									
								}
							} else { // not in connectome --> non terminal case
								if (cases.containsCellCase(lineageName)) {

									// show tab
								} else {
									// add a non terminal case
									if (defaultEmbryoFlag) {
										cases.makeNonTerminalCase(lineageName, productionInfo.getNuclearInfo(),
												productionInfo.getCellShapeData(lineageName));
									} else {
										System.out.println("third one");
										cases.makeNonTerminalCase(lineageName, 
												new ArrayList<String>(),
												new ArrayList<String>());
									}
									
								}
							}
						}
						return null;
					}

				};
				return task;
			}
		};

		addNameService.setOnScheduled(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				showLoadingService.restart();
			}
		});
		addNameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				showLoadingService.cancel();
				setInfoWindowTitle();
			}
		});
		addNameService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				showLoadingService.cancel();
				setInfoWindowTitle();
			}
		});
		showLoadingService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				showWindow();
				setInfoWindowTitle();
			}
		});
	}
	
	private void setInfoWindowTitle() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				infoWindowStage.setTitle("Cell Info Window");
			}
		});
	}

	public void addName(String name) {
		nameToQuery = name;
		addNameService.restart();
	}

	public void showWindow() {
		if (infoWindowStage != null) {
			infoWindowStage.show();
			infoWindowStage.toFront();
		}
	}

//	private void goToTabWithName(String name) {
//		if (containsTab(name)) {
//			for (Tab tab : tabPane.getTabs()) {
//				if (tab.getText().equalsIgnoreCase(name)) {
//					tabPane.getSelectionModel().select(tab);
//					break;
//				}
//			}
//		}
//	}
//
//	private boolean containsTab(String name) {
//		for (Tab tab : tabPane.getTabs()) {
//			if (tab.getText().equalsIgnoreCase(name))
//				return true;
//		}
//		return false;
//	}

	/**
	 * Adds a tab to the window in a separate thread
	 * 
	 * @param dom the dom to be added as a tab
	 */
	public void addTab(InfoWindowDOM dom) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebView webview = new WebView();
				webview.getEngine().loadContent(dom.DOMtoString());
				webview.setContextMenuEnabled(false);

				// link controller
				JSObject window = (JSObject) webview.getEngine().executeScript("window");
				window.setMember("app", linkController);

				// link handler
				DraggableTab tab = new DraggableTab(dom.getName());
				tab.setContent(webview);
				tab.setId(dom.getName());
				// Tab tab = new Tab(dom.getName(), webview);
				tabPane.getTabs().add(0, tab); // prepend the tab
				tabPane.getSelectionModel().select(tab); // show the new tab

				// close tab event handler
				tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
				tab.setOnClosed(new EventHandler<Event>() {
					@Override
					public void handle(Event e) {
						Tab t = (Tab) e.getSource();
						String cellName = t.getId();
						Search.removeCellCase(cellName);
					}
				});

				tabPane.setFocusTraversable(true);
			}
		});
	}

	public Stage getStage() {
		return infoWindowStage;
	}

	public final String EVENT_TYPE_CLICK = "click";
	private final long WAIT_TIME_MILLI = 750;
	private final static String Nuc = "Nuc";
}