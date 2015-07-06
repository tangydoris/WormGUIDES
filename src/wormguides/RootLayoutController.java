package wormguides;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController implements Initializable{
	
	private Stage aboutStage;
	private Parent aboutRoot;
	
	@FXML
	public void menuCloseAction() {
		System.out.println("exiting...");
		System.exit(0);
	}
	
	@FXML
	public void menuAboutAction() {
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
