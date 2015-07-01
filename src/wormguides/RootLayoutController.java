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
		System.out.println("about");
	}
	
}
