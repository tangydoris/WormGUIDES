package wormguides.view;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

public class TreePane extends AnchorPane {

	public TreePane() {
        this(new TreeItem<>("null root"));
    }

	public TreePane(TreeItem<String> root) {
		super();

		setPrefHeight(500.0);
		setPrefWidth(250.0);

        TreeView<String> view = new TreeView<>(root);
        AnchorPane.setTopAnchor(view, 0d);
        AnchorPane.setLeftAnchor(view, 0d);
		AnchorPane.setRightAnchor(view, 0d);
		AnchorPane.setBottomAnchor(view, 0d);
		view.setFocusTraversable(false);
		view.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");

		getChildren().add(view);
		setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
	}

}