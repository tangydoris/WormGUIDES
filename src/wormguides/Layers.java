package wormguides;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;

public class Layers {
	
	private ListView<String> colorRulesList;
	
	public Layers() {
		this(new ListView<String>());
	}

	public Layers(ListView<String> colorRulesList) {
		if (colorRulesList==null)
			colorRulesList = new ListView<String>();

		this.colorRulesList = colorRulesList;
	}
	
	public AddSearchListener getAddSearchListener() {
		return new AddSearchListener();
	}
	
	private class AddSearchListener implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("adding search...");
		}
	}

}
