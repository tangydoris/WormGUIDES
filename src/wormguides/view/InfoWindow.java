package wormguides.view;


import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
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

public class InfoWindow {
	
	private Stage infoWindowStage;
	private TabPane tabPane;
	Scene scene;
	Stage window3DStage; //update scenes on links
	IntegerProperty time;
	InfoWindowLinkController linkController;
	
	
	
	/*
	 * TODO
	 * if tab is closed --> remove case from cell cases i.e. internal memory
	 */
	
	public InfoWindow(Stage stage, IntegerProperty timeProperty, 
			StringProperty cellNameProperty) {
		infoWindowStage = new Stage();
		infoWindowStage.setTitle("Info Window");
		
		tabPane = new TabPane();
		
		scene = new Scene(new Group());
		scene.setRoot(tabPane);
		
		infoWindowStage.setScene(scene);
		
		infoWindowStage.setMinHeight(400);
		infoWindowStage.setMinWidth(500);
		infoWindowStage.setHeight(600);
		infoWindowStage.setWidth(700);
		
		infoWindowStage.setResizable(true);
		
		window3DStage = stage;
		time = timeProperty;
		linkController = new InfoWindowLinkController(window3DStage, time, cellNameProperty);
		
	}
	
	public void showWindow() {
		if (infoWindowStage != null) {
			infoWindowStage.show();
			infoWindowStage.toFront();
		}
	}

	public void addTab(InfoWindowDOM dom, ArrayList<String> links) {
		WebView webview = new WebView();
		webview.getEngine().loadContent(dom.DOMtoString());
		webview.setContextMenuEnabled(false);
		
		//link controller
		JSObject window = (JSObject) webview.getEngine().executeScript("window");
		window.setMember("app", linkController);
		
		//link handler
		
		Tab tab = new Tab(dom.getName(), webview);
		tabPane.getTabs().add(0, tab); //prepend the tab
		tabPane.getSelectionModel().select(tab); //show the new tab
		
		//close tab event handler
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		tab.setOnClosed(new EventHandler<javafx.event.Event>() {
			public void handle(javafx.event.Event e) {
				Tab t = (Tab) e.getSource();
				String cellName = t.getText();
				Search.removeCellCase(cellName);
			}
		});
		
		tabPane.setFocusTraversable(true);
	}
	
	public Stage getStage() {
		return this.infoWindowStage;
	}
	
	public static final String EVENT_TYPE_CLICK = "click";

}