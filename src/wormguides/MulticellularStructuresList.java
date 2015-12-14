package wormguides;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import wormguides.model.SceneElementsList;

public class MulticellularStructuresList {
	private SceneElementsList sceneElementsList;
	@FXML private ListView<String> allStructuresListView;
	private ObservableList<String> allStructuresList;
	
	public MulticellularStructuresList(SceneElementsList sceneElementsList, 
			ListView<String> allStructuresListView) {
		this.sceneElementsList = sceneElementsList;
		this.allStructuresListView = allStructuresListView;
		this.allStructuresList = FXCollections.observableArrayList();
	}
	
	public void setStructuresList() {
		
	}
}
