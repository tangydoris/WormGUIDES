package wormguides;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController {
	
	private Stage aboutStage;
	private Parent aboutRoot;
	
	@FXML
	protected void menuCloseAction() {
		System.out.println("exiting...");
		System.exit(0);
	}
	
	@FXML
	protected void menuAboutAction() {
		if (aboutStage == null) {
			aboutStage = new Stage();
			try {
				aboutRoot = FXMLLoader.load(getClass().getResource("view/About.fxml"));
				aboutStage.setScene(new Scene(aboutRoot));
				aboutStage.setTitle("About WormGUIDES");
				aboutStage.initModality(Modality.APPLICATION_MODAL);
				
				aboutStage.show();
			} catch (IOException e) {
				System.out.println("cannot load about page.");
				e.printStackTrace();
			}
		}
		else
			aboutStage.show();
		
	}
	
}
