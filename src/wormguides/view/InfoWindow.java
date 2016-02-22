package wormguides.view;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
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
import wormguides.model.CellCases;
import wormguides.model.Connectome;
import wormguides.model.PartsList;
import wormguides.model.ProductionInfo;

public class InfoWindow {

	private Stage infoWindowStage;
	private TabPane tabPane;
	private Scene scene;
	private Stage parentStage; // update scenes on links
	private IntegerProperty time;
	private InfoWindowLinkController linkController;

	private CellCases cellCases;
	private ProductionInfo productionInfo;
	private Connectome connectome;

	private String nameToQuery;
	private Service<Void> addNameService;
	private Service<Void> showLoadingService;

	private int count; // to show loading in progress

	/*
	 * TODO if tab is closed --> remove case from cell cases i.e. internal
	 * memory
	 */

	public InfoWindow(Stage stage, StringProperty cellNameProperty, CellCases cases, ProductionInfo info,
			Connectome connectome) {
		infoWindowStage = new Stage();
		infoWindowStage.setTitle("Info Window");

		cellCases = cases;
		productionInfo = info;
		this.connectome = connectome;

		tabPane = new TabPane();

		scene = new Scene(new Group());
		scene.setRoot(tabPane);

		infoWindowStage.setScene(scene);

		infoWindowStage.setMinHeight(400);
		infoWindowStage.setMinWidth(500);
		infoWindowStage.setHeight(600);
		infoWindowStage.setWidth(700);

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
						final String queryName = nameToQuery;

						if (queryName != null && !queryName.isEmpty()) {
							if (cellCases == null) {
								System.out.println("null cell cases");
								return null; // error check
							}

							if (PartsList.containsLineageName(queryName)) {
								if (cellCases.containsTerminalCase(queryName)) {

									// show the tab
								} else {
									// translate the name if necessary
									//String tabTitle = connectome.checkQueryCell(queryName).toUpperCase();
									//String tabTitle = queryName;
									// add a terminal case --> pass the wiring
									// partners
									String searchName = connectome.checkQueryCell(queryName).toUpperCase();
									cellCases.makeTerminalCase(queryName, searchName,
											connectome.queryConnectivity(searchName, true, false, false, false, false),
											connectome.queryConnectivity(searchName, false, true, false, false, false),
											connectome.queryConnectivity(searchName, false, false, true, false, false),
											connectome.queryConnectivity(searchName, false, false, false, true, false),
											productionInfo.getNuclearInfo(),
											productionInfo.getCellShapeData(searchName));
								}
							} else { // not in connectome --> non terminal case
								if (cellCases.containsNonTerminalCase(queryName)) {

									// show tab
								} else {
									// add a non terminal case
									cellCases.makeNonTerminalCase(queryName, productionInfo.getNuclearInfo(),
											productionInfo.getCellShapeData(queryName));
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
				infoWindowStage.setTitle("Info Window");
				showWindow();
			}
		});
		addNameService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				showLoadingService.cancel();
				infoWindowStage.setTitle("Info Window");
				showWindow();
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

	private void goToTabWithName(String name) {
		if (containsTab(name)) {
			for (Tab tab : tabPane.getTabs()) {
				if (tab.getText().equalsIgnoreCase(name)) {
					tabPane.getSelectionModel().select(tab);
					break;
				}
			}
		}
	}

	private boolean containsTab(String name) {
		for (Tab tab : tabPane.getTabs()) {
			if (tab.getText().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public void addTab(InfoWindowDOM dom, ArrayList<String> links) {
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
				// Tab tab = new Tab(dom.getName(), webview);
				tabPane.getTabs().add(0, tab); // prepend the tab
				tabPane.getSelectionModel().select(tab); // show the new tab

				// close tab event handler
				tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
				tab.setOnClosed(new EventHandler<Event>() {
					public void handle(Event e) {
						Tab t = (Tab) e.getSource();
						String cellName = t.getText();
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
}