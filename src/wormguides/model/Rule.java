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
import wormguides.layers.SearchType;
import wormguides.loaders.ImageLoader;
import wormguides.view.AppFont;

/**
 * This class is the color rule that determines the coloring/striping of cell,
 * cell bodies, and multicellular structures. It is instantiated by the
 * {@link Search} class and added to an {@link ObservableList} of Rules that are
 * displayed in the 'Display Options' tab. This class also contains the JavaFX
 * nodes that make up its graphical representation, which are used to display
 * the rule in the {@link ListView} in the tab.
 * 
 * @author Doris Tang
 */

public class Rule {

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

	private HBox hbox;
	private Label label;
	private Rectangle colorRectangle;
	private Button editBtn;
	private Button visibleBtn;
	private Button deleteBtn;
	private Tooltip toolTip;

	private RuleEditorController editController;
	private SubmitHandler handler;

	private SearchType searchType;

	/**
	 * Rule class constructor called by the {@link Search} class.
	 * 
	 * @param searched
	 *            String that contains the term used when the user made a search
	 *            to add this rule
	 * @param color
	 *            {@link Color} that the search cell(s), cell body(ies), and/or
	 *            multicellular structure(s) should have in the 3D subscene
	 * @param type
	 *            {@link SearchType} enum that contains the type of search that
	 *            was made
	 * @param options
	 *            The {@link SearchOption}(s) enum(s) that the rule should be
	 *            extended to
	 */
	public Rule(String searched, Color color, SearchType type, SearchOption... options) {
		this(searched, color, type, new ArrayList<SearchOption>(Arrays.asList(options)));
	}

	/**
	 * Rule class constructor called by the {@link Search} class.
	 * 
	 * @param searched
	 *            String that contains the term used when the user made a search
	 *            to add this rule
	 * @param color
	 *            {@link Color} that the search cell(s), cell body(ies), and/or
	 *            multicellular structure(s) should have in the 3D subscene
	 * @param type
	 *            {@link SearchType} enum that contains the type of search that
	 *            was made
	 * @param options
	 *            The ArrayList of {@link SearchOption} enum(s) that the rule
	 *            should be extended to
	 */
	public Rule(String searched, Color color, SearchType type, ArrayList<SearchOption> options) {
		hbox = new HBox();
		label = new Label();
		colorRectangle = new Rectangle(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
		editBtn = new Button();
		visibleBtn = new Button();
		deleteBtn = new Button();
		toolTip = new Tooltip();

		searchType = type;
		setOptions(options);
		setColor(color);
		setSearchedText(searched);

		handler = new SubmitHandler();

		cells = new ArrayList<String>();
		// if the cells list from Search is set for this rule, cellsSet is true
		// is false before the list is set
		cellsSet = false;

		hbox.setSpacing(3);
		hbox.setPadding(new Insets(3));
		hbox.setPrefWidth(275);
		hbox.setMinWidth(275);
		hbox.setMaxWidth(275);
		hbox.setMinWidth(hbox.getPrefWidth());
		hbox.setMaxWidth(hbox.getPrefWidth());

		label.setFont(AppFont.getFont());
		label.setPrefHeight(UI_SIDE_LENGTH);
		label.setMaxHeight(UI_SIDE_LENGTH);
		label.setMinHeight(UI_SIDE_LENGTH);
		label.textOverrunProperty().set(OverrunStyle.ELLIPSIS);
		label.setFont(AppFont.getFont());

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

	/**
	 * Shows the editor for the rule.
	 * 
	 * @param stage
	 *            The {@link Stage} to which the rule editor window belongs to
	 */
	public void showEditStage(Stage stage) {
		if (editStage == null)
			initEditStage(stage);

		editController.setHeading(label.getText());
		editStage.show();

		((Stage) editStage.getScene().getWindow()).toFront();
	}

	/**
	 * Initializes the edit stage by loading the layout RuleEditorLayout.fxml.
	 * 
	 * @param stage
	 *            The {@link Stage} to which the rule editor window belongs to
	 */
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
				node.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
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

			else if (isMulticellularStructureRule())
				editController.disableOptionsForStructureRule();

		} catch (IOException e) {
			System.out.println("error in instantiating rule editor - input/output exception");
			e.printStackTrace();
		} catch (NullPointerException npe) {
			System.out.println("error in instantiating rule editor - null pointer exception");
			npe.printStackTrace();
		}
	}

	/**
	 * @return TRUE if the rule should color a multicellular structure, FALSE
	 *         otherwise.
	 */
	public boolean isMulticellularStructureRule() {
		return options.contains(SearchOption.MULTICELLULAR_NAME_BASED);
	}

	/**
	 * Called by the {@link Search} class to set the baseline list of cells that
	 * the rule affects. Multicellular structure rule cells are never set since
	 * they are queried by name only.
	 * 
	 * @param list
	 *            ArrayList of baseline cell names that should be affected by
	 *            this rule. The list only contains immediate cells, not the
	 *            ancestor or descendant cells.
	 */
	public void setCells(ArrayList<String> list) {
		cells = list;
		cellsSet = true;
	}

	/**
	 * @return TRUE if the list of baseline cells are set by the {@link Search}
	 *         class, FALSE otherwise
	 */
	public boolean areCellsSet() {
		return cellsSet;
	}

	/**
	 * @return the list of baseline cells that this rule affects, not including
	 *         decsendant or ancestor cells.
	 */
	public ArrayList<String> getCells() {
		return cells;
	}

	/**
	 * Changes the color of the rectangle displayed next to the rule name in the
	 * rule's graphical representation.
	 * 
	 * @param color
	 *            The {@link Color} that the rectangle in the graphical
	 *            representation of the rule should be changed to
	 */
	private void setColorButton(Color color) {
		colorRectangle.setFill(color);
	}

	/**
	 * Sets the searched term entered by the user when the rule was added.
	 * 
	 * @param name
	 *            The String containing the search term entered by the user when
	 *            the rule was added
	 */
	public void setSearchedText(String name) {
		text = name;
		textLowerCase = name.toLowerCase();

		label.setText(toStringFull());
	}

	/**
	 * Resets the label in the graphical representation of the rule.
	 * 
	 * @param labelString
	 *            The String that contains the contents for the new labels
	 */
	public void resetLabel(String labelString) {
		label.setText(labelString);
	}

	/**
	 * Sets the color of the rule.
	 * 
	 * @param color
	 *            The {@link Color} that the rule should apply to the cell(s),
	 *            cell body(ies), and/or multicellular structures it affects
	 */
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
		return options.contains(SearchOption.CELLNUCLEUS);
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

	public String toString() {
		return toStringFull();
	}

	/**
	 * @param other
	 *            The other rule to compare this rule to
	 * @return TRUE if the rules contain the same searched text; FALSE otherwise
	 */
	public boolean equals(Rule other) {
		return text.equalsIgnoreCase(other.getSearchedText());
	}

	/**
	 * @return Full string description of the rule used in the tooltip and the
	 *         label in the heading of the rule editor popup. The return string
	 *         contains the rule's name and options.
	 */
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

	/**
	 * @param name
	 *            lineage name of queried cell
	 * @return TRUE if the rule is visible and applies to cell nucleus with
	 *         specified name; FALSE otherwise
	 */
	public boolean appliesToCellNucleus(String name) {
		if (!visible)
			return false;

		if (cells != null) {
			if (options.contains(SearchOption.CELLNUCLEUS) && cells.contains(name))
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

	/**
	 * @param name
	 *            scene name of multicellular structure
	 * @return TRUE if the rule is visible and it applies to multicellcular
	 *         structure with specified name; FALSE otherwise
	 */
	public boolean appliesToMulticellularStructure(String name) {
		if (!visible) {
			return false;
		}
		
		if (options.contains(SearchOption.MULTICELLULAR_NAME_BASED) && text.equalsIgnoreCase(name)) {	
			return true;
		}

		return false;
	}

	/**
	 * @param name
	 *            lineage name of cellbody
	 * @return TRUE if the rule is visible and applies to cell body with
	 *         specified name; FALSE otherwise
	 */
	public boolean appliesToCellBody(String name) {
		if (!visible)
			return false;

		if (cells != null) {
			if (options.contains(SearchOption.CELLBODY) && cells.contains(name)) {
				return true;
			}

			for (String cell : cells) {
				if (options.contains(SearchOption.ANCESTOR) && LineageTree.isAncestor(name, cell)) {
					return true;
				}

				if (options.contains(SearchOption.DESCENDANT) && LineageTree.isDescendant(name, cell)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the {@link SearchType} of the rule. If the rule has the option
	 * SearchOption.MULTICELLULAR, the return value is null and the rule is a
	 * rule specific to multicellular structures (meaning the rule is defined by
	 * its name instead of by its cells).
	 * 
	 * @return The SearchType of the rule
	 */
	public SearchType getSearchType() {
		return searchType;
	}

	/**
	 * @return TRUE if rule is visible; FALSE otherwise
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the ruleChanged {@link BooleanProperty} to the value defined by the
	 * input parameter.
	 * 
	 * @param changed
	 *            boolean stating whether the rule was changed
	 */
	public void setChanged(boolean changed) {
		ruleChanged.set(changed);
	}

	/**
	 * This private class is an {@link EventHandler} of {@link ActionEvent} that
	 * handles a click on the 'Submit' button in the rule editor popup.
	 */
	private class SubmitHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (editController != null) {
				setColor(editController.getColor());
				editStage.hide();
				
				// because the multicellular name based rule is not a check option, we need to override this function to avoid overwriting the multicellular search option
				if (!options.contains(SearchOption.MULTICELLULAR_NAME_BASED)) {
					setOptions(editController.getOptions());
				}

				label.setText(toStringFull());
				toolTip.setText(toStringFull());

				ruleChanged.set(true);
				ruleChanged.set(false);
			}
		}
	}

	// length and width of color rule ui buttons
	private static final int UI_SIDE_LENGTH = 22;

}
