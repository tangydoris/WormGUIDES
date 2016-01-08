package wormguides.view;


import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class InfoWindow {
	private Stage infoWindowStage;
	private TabPane tabPane;
	Scene scene;
	
	/*
	 * TODO
	 * if tab is closed --> remove case from cell cases i.e. internal memory
	 * //resizable vars and setters
	 * SET SIZE OF INFO WINDOW
	 */
	
	public InfoWindow() {
		infoWindowStage = new Stage();
		infoWindowStage.setTitle("Info Window");
		
		tabPane = new TabPane();
		
		scene = new Scene(new Group());
		scene.setRoot(tabPane);
		
		infoWindowStage.setScene(scene);
	}
	
	public void showWindow() {
		if (infoWindowStage != null) {
			infoWindowStage.show();
		}
	}
	
	public void addTab(InfoWindowDOM dom) {
		WebView webview = new WebView();
		System.out.println(dom.DOMtoString());
		webview.getEngine().loadContent(dom.DOMtoString());
		Tab tab = new Tab(dom.getName(), webview);
		tabPane.getTabs().add(tab);
	}
}