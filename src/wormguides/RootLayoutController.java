package wormguides;

import javafx.fxml.FXML;

public class RootLayoutController {
	
	@FXML
	protected void menuCloseAction() {
		System.out.println("exiting...");
		System.exit(0);
	}
	
	@FXML
	protected void menuAboutAction() {
		
	}
	
	// TODO add time slider controller
	
	// TODO add play, forward, backward button controls
}
