package wormguides.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wormguides.MainApp;
import wormguides.SearchOption;
import wormguides.controllers.RuleEditorController;
import wormguides.loaders.ImageLoader;
import wormguides.view.AppFont;

/*
 * Superclass for ColorRule and MulticellularStructureRule, which have
 * the same layout (label, some space, 4 buttons)
 */

public abstract class Rule {

	private Stage editStage;

	private String text;
	private String textLowerCase;

	private ArrayList<SearchOption> options;
	private BooleanProperty ruleChanged;
	private boolean visible;
	private Color color;

	private ImageView eyeIcon;
	private ImageView eyeInvertIcon;

	private ArrayList<String> cells;
	private boolean cellsSet;

	private boolean isStructureRule;

	private HBox hbox = new HBox();
	private Label label = new Label();
	private Rectangle colorRectangle = new Rectangle(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
	private Button editBtn = new Button();
	private Button visibleBtn = new Button();
	private Button deleteBtn = new Button();

	private Tooltip toolTip = new Tooltip();

	private RuleEditorController editController;
	private SubmitHandler handler;

	public Rule(String searched, Color color, ArrayList<SearchOption> options, boolean structureRule) {
		setSearchedText(searched);
		setColor(color);

		handler = new SubmitHandler();

		isStructureRule = structureRule;

		cells = new ArrayList<String>();
		// if the cells list from Search is set for this rule, cellsSet is true
		// is false before the list is set
		cellsSet = false;

		setOptions(options);

		hbox.setSpacing(3);
		hbox.setPadding(new Insets(3));
		hbox.setPrefWidth(290);
		hbox.setMinWidth(hbox.getPrefWidth());
		hbox.setMaxWidth(hbox.getPrefWidth());

		label.setFont(AppFont.getFont());
		label.setPrefHeight(UI_SIDE_LENGTH);
		label.setMaxHeight(UI_SIDE_LENGTH);
		label.setMinHeight(UI_SIDE_LENGTH);
		label.textOverrunProperty().set(OverrunStyle.ELLIPSIS);
		label.setFont(AppFont.getFont());
		resetLabel();

		Region r = new Region();
		HBox.setHgrow(r, Priority.ALWAYS);

		colorRectangle.setHeight(UI_SIDE_LENGTH);
		colorRectangle.setWidth(UI_SIDE_LENGTH);
		colorRectangle.setStroke(Color.LIGHTGREY);
		setColorButton(color);

		editBtn.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		editBtn.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		editBtn.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		editBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		editBtn.setPadding(Insets.EMPTY);
		editBtn.setGraphic(ImageLoader.getEditIcon());
		editBtn.setGraphicTextGap(0);
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showEditStage(null);
			}
		});

		eyeIcon = ImageLoader.getEyeIcon();
		eyeInvertIcon = ImageLoader.getEyeInvertIcon();

		visibleBtn.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		visibleBtn.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		visibleBtn.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		visibleBtn.setPadding(Insets.EMPTY);
		visibleBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		visibleBtn.setGraphic(eyeIcon);
		visibleBtn.setGraphicTextGap(0);
		visibleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				visible = !visible;
				if (visible)
					visibleBtn.setGraphic(eyeIcon);
				else
					visibleBtn.setGraphic(eyeInvertIcon);
				
				ruleChanged.set(true);
				ruleChanged.set(false);
			}
		});

		deleteBtn.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		deleteBtn.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		deleteBtn.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		deleteBtn.setPadding(Insets.EMPTY);
		deleteBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		deleteBtn.setGraphic(ImageLoader.getCloseIcon());

		toolTip.setText(toStringFull());
		toolTip.setFont(AppFont.getFont());
		label.setTooltip(toolTip);

		hbox.getChildren().addAll(label, r, colorRectangle, editBtn, visibleBtn, deleteBtn);

		ruleChanged = new SimpleBooleanProperty(false);
		ruleChanged.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue && editController != null) {
					setColorButton(editController.getColor());
				}
			}
		});

		visible = true;
	}

	public void showEditStage(Stage stage) {
		if (editStage == null)
			initEditStage(stage);

		editController.setHeading(label.getText());
		editStage.show();

		((Stage) editStage.getScene().getWindow()).toFront();
	}

	private void initEditStage(Stage stage) {
		editController = new RuleEditorController();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/layouts/RuleEditorLayout.fxml"));

		loader.setController(editController);
		loader.setRoot(editController);

		try {
			editStage = new Stage();
			editStage.setScene(new Scene((AnchorPane) loader.load()));

			for (Node node : editStage.getScene().getRoot().getChildrenUnmodifiable()) {
				node.setStyle("-fx-focus-color: -fx-outer-border; " + "-fx-faint-focus-color: transparent;");
			}

			editStage.setTitle("Edit Rule");
			if (stage != null)
				editStage.initOwner(stage);
			editStage.initModality(Modality.NONE);

			editController.setHeading(text);
			editController.setSubmitHandler(handler);
			editController.setColor(color);
			editController.setCellTicked(isCellSelected());
			editController.setCellBodyTicked(isCellBodySelected());
			editController.setAncestorsTicked(isAncestorSelected());
			editController.setDescendantsTicked(isDescendantSelected());

			if (textLowerCase.contains("functional") || textLowerCase.contains("description"))
				editController.disableDescendantOption();

			else if (isStructureRule)
				editController.disableOptionsForStructureRule();

		} catch (IOException e) {
			System.out.println("error in instantiating rule editor - input/output exception");
			e.printStackTrace();
		} catch (NullPointerException npe) {
			System.out.println("error in instantiating rule editor - null pointer exception");
			npe.printStackTrace();
		}
	}

	public void setCells(ArrayList<String> list) {
		cells = list;
		cellsSet = true;
	}

	public boolean areCellsSet() {
		return cellsSet;
	}

	public ArrayList<String> getCells() {
		return cells;
	}

	private void setColorButton(Color color) {
		colorRectangle.setFill(color);
	}

	public void setText(String title) {
		text = title;
		resetLabel();
	}

	private void resetLabel() {
		label.setText(toStringFull());
	}

	public void setSearchedText(String name) {
		text = name;
		textLowerCase = name.toLowerCase();
	}

	public void setColor(Color color) {
		this.color = color;
		setColorButton(color);
	}

	public void setOptions(SearchOption... options) {
		setOptions(new ArrayList<SearchOption>(Arrays.asList(options)));
	}

	public void setOptions(ArrayList<SearchOption> options) {
		this.options = new ArrayList<SearchOption>();
		for (SearchOption option : options)
			if (option != null)
				this.options.add(option);
	}

	public String getSearchedText() {
		return text;
	}

	public String getSearchedTextLowerCase() {
		return textLowerCase;
	}

	public Color getColor() {
		return color;
	}

	public HBox getGraphic() {
		return hbox;
	}

	public Button getDeleteButton() {
		return deleteBtn;
	}

	public boolean isCellSelected() {
		return options.contains(SearchOption.CELL);
	}

	public boolean isCellBodySelected() {
		return options.contains(SearchOption.CELLBODY);
	}

	public boolean isAncestorSelected() {
		return options.contains(SearchOption.ANCESTOR);
	}

	public boolean isDescendantSelected() {
		return options.contains(SearchOption.DESCENDANT);
	}

	public SearchOption[] getOptions() {
		return options.toArray(new SearchOption[options.size()]);
	}

	public BooleanProperty getRuleChangedProperty() {
		return ruleChanged;
	}

	// TODO
	public String toString() {
		return text+" - visible: "+isVisible();
	}

	public boolean equals(ColorRule other) {
		return text.equals(other.getSearchedText());
	}

	// Returns full string description of the Rule
	public String toStringFull() {
		StringBuilder sb = new StringBuilder(text);
		sb.append(" ");

		if (!options.isEmpty()) {
			sb.append("(");
			for (int i = 0; i < options.size(); i++) {
				sb.append(options.get(i).toString());
				if (i < options.size() - 1)
					sb.append(", ");
			}
			sb.append(")");
		}

		return sb.toString();
	}

	// @param name : lineage name of cell body
	public boolean appliesToBody(String name) {
		if (!visible)
			return false;

		if (cells != null) {
			if (options.contains(SearchOption.CELLBODY) && cells.contains(name))
				return true;

			for (String cell : cells) {
				if (options.contains(SearchOption.ANCESTOR) && LineageTree.isAncestor(name, cell))
					return true;

				if (options.contains(SearchOption.DESCENDANT) && LineageTree.isDescendant(name, cell))
					return true;
			}
		}
		return false;
	}

	// @param name : lineage name of cell
	public boolean appliesToCell(String name) {
		if (!visible)
			return false;

		if (cells != null) {
			if (options.contains(SearchOption.CELL) && cells.contains(name))
				return true;

			for (String cell : cells) {
				if (options.contains(SearchOption.ANCESTOR) && LineageTree.isAncestor(name, cell))
					return true;

				if (options.contains(SearchOption.DESCENDANT) && LineageTree.isDescendant(name, cell))
					return true;
			}
		}

		return false;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setChanged(boolean changed) {
		ruleChanged.set(changed);
	}

	private class SubmitHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (editController != null) {
				setColor(editController.getColor());
				editStage.hide();
				setOptions(editController.getOptions());
				resetLabel();
				toolTip.setText(toStringFull());

				ruleChanged.set(true);
				ruleChanged.set(false);
			}
		}
	}

	// length and width of color rule ui buttons
	private static final int UI_SIDE_LENGTH = 22;
}
