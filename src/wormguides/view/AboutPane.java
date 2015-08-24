package wormguides.view;

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class AboutPane extends AnchorPane {
		
		public AboutPane() {
			super();
			setPrefHeight(400.0);
			setPrefWidth(300.0);
			
			TextArea e_1 = new TextArea();	
			AnchorPane.setBottomAnchor(e_1, 0.0);
			AnchorPane.setLeftAnchor(e_1, 0.0);
			AnchorPane.setRightAnchor(e_1, 0.0);
			AnchorPane.setTopAnchor(e_1, 0.0);
			
			e_1.setEditable(false);
			e_1.setStyle("-fx-border-radius: 0; -fx-background-radius: 0;");
			e_1.setText("WormGUIDES is a collaboration led by Drs. Zhirong Bao (MSKCC), "
					+"Daniel Colon-Ramos (Yale), William Mohler (UConn) and Hari Shroff (NIH). "
					+"For more information, visit our website at http://wormguides.org.\n\n"
					+"The WormGUIDES app is developed and maintained by the laboratories of Dr. Zhirong Bao "
					+"and Dr. William Mohler. Major contributors of the desktop app include "
					+"Doris Tang (New York University) and Dr. Anthony Santella of the Bao Laboratory.\n\n"
					+"For questions or comments contact support@wormguides.org.");
			
			e_1.setWrapText(true);
			
			e_1.setStyle("-fx-focus-color: -fx-outer-border; "+
					"-fx-faint-focus-color: transparent;");
			
			getChildren().add(e_1);
		}
		
	}