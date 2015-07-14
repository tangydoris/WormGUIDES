package wormguides.view;

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class About extends AnchorPane {
		
		public AnchorPane load() {
			AnchorPane root = new AnchorPane();
			root.setPrefHeight(400.0);
			root.setPrefWidth(300.0);
			{
				TextArea e_1 = new TextArea();
				e_1.setId("aboutText");
				e_1.setEditable(false);
				e_1.setLayoutX(100.0);
				e_1.setLayoutY(33.0);
				e_1.setStyle("-fx-border-radius: 0; -fx-background-radius: 0;");
				e_1.setText("WormGUIDES, Bao Lab");
				root.getChildren().add(e_1);
				AnchorPane.setBottomAnchor(e_1,0.0);
				AnchorPane.setLeftAnchor(e_1,0.0);
				AnchorPane.setRightAnchor(e_1,0.0);
				AnchorPane.setTopAnchor(e_1,0.0);
			}
			return root;
		}
	}