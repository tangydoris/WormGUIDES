package wormguides.view;


import java.util.ArrayList;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import wormguides.controllers.InfoWindowLinkController;

public class InfoWindow {
	
	private Stage infoWindowStage;
	private TabPane tabPane;
	Scene scene;
	
	/*
	 * TODO
	 * if tab is closed --> remove case from cell cases i.e. internal memory
	 */
	
	public InfoWindow() {
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
		window.setMember("app", new InfoWindowLinkController(links));
		
		Tab tab = new Tab(dom.getName(), webview);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
		tabPane.setFocusTraversable(true);
	}
	
	public Stage getStage() {
		return this.infoWindowStage;
	}

}