package wormguides.view;

import wormguides.model.TableLineageData;
import javafx.scene.layout.AnchorPane;

public class Window3DSubScene {
	
	private AnchorPane parent;
	private TableLineageData data;
	
	public Window3DSubScene(AnchorPane parent, TableLineageData data) {
		this.parent = parent;
		this.data = data;
	}
}
