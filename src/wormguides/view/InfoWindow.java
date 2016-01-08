package wormguides.view;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class InfoWindow {
	private Stage infoWindowStage;
	private TabPane tabPane;
	private ArrayList<InfoWindowDOM> cellTabs;
	
	//resizable vars and setters
	
	//listeners on InfoWindowDOM objects
	
	//add webview to tabpane --> tabpane is list of tabs --> each tab gets a webview
	public InfoWindow() {
		infoWindowStage = new Stage();
		infoWindowStage.setTitle("Info Window");
		
		tabPane = new TabPane();
		cellTabs = new ArrayList<InfoWindowDOM>();
	}
	

	public void domToTab(String cellName) {
		//iterate through the DOMs and find the correct cell
		for (InfoWindowDOM dom : cellTabs) {
			if (dom.getCellName().equals(cellName)) {
				WebView newWebView = new WebView();
				newWebView.getEngine().loadContent(dom.toString());
				Tab tab2 = new Tab(dom.getCellName(), newWebView);
				tabPane.getTabs().add(tab2);
				
				Scene scene = new Scene(new Group());
				scene.setRoot(tabPane);
				
				infoWindowStage.setScene(scene);
				infoWindowStage.show();
			}
		}
		
		
	}
	
	public void showWindow() {
		if (infoWindowStage != null) {
			infoWindowStage.show();
		}
	}
	
	/*
	 * debug
	 */
	public void addDOM() {
		HTMLNode html = new HTMLNode("html");
		HTMLNode head = new HTMLNode("head");
		HTMLNode body = new HTMLNode("body");
		HTMLNode div = new HTMLNode("div", "firstDiv", "text-align: center;");
		HTMLNode p = new HTMLNode("p", "firstP", "font-size: 13pt;", "hello!");
		HTMLNode p2 = new HTMLNode("p", "secondP", "font-size: 15pt;", "oh heyyy");
		HTMLNode img = new HTMLNode("firstImg", "imgSrc", "altText", "float: left;", 35, 42);
		div.addChild(img);
		body.addChild(p);
		body.addChild(div);
		body.addChild(p2);
		html.addChild(head);
		html.addChild(body);
		InfoWindowDOM dom = new InfoWindowDOM(html);
		dom.buildStyleNode();
		
		cellTabs.add(dom);
	}
	
}

//@FXML
//public void viewCellShapesIndex() {
//	if (elementsList == null) return;
//	
//	if (cellShapesIndexStage == null) {
//		cellShapesIndexStage = new Stage();
//		cellShapesIndexStage.setTitle("Cell Shapes Index");
//		
//		CellShapesIndexToHTML cellShapesToHTML = new CellShapesIndexToHTML(elementsList);
//		
//		//webview to render cell shapes list i.e. elementsList
//		WebView cellShapesIndexWebView = new WebView();
//		cellShapesIndexWebView.getEngine().loadContent(cellShapesToHTML.buildCellShapesIndexAsHTML());
//		
//		VBox root = new VBox();
//		root.getChildren().addAll(cellShapesIndexWebView);
//		Scene scene = new Scene(new Group());
//		scene.setRoot(root);
//		
//		cellShapesIndexStage.setScene(scene);
//		cellShapesIndexStage.setResizable(false);
//	}
//	cellShapesIndexStage.show();
//}
