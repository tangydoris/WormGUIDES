package wormguides;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wormguides.model.Rule;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * This class is the controller for the 'Display' tab where the list of rules
 * are shown. It contains an {@link ArrayList} of color rules that are internal
 * to the application as well as an {@link ObservableList} of color rules that
 * display the rules used at that time in the 3d subscene.<br>
 * <br>
 * The current list of rules changes on story change. If no story is active,
 * then the internal rules are copied to the current list and used. All changes
 * made to the rules displayed in the tab are reflected in the current rules
 * list. On context change (making a story active/inactive), the rules in the
 * current list are stored back into the item that no longer has context
 * (whether it is the internal rules or the story's rules).<br>
 * <br>
 * 
 * 
 * @see Rule
 */

public class DisplayLayer {

	private ArrayList<Rule> internalRulesList;
	private ObservableList<Rule> currentRulesList;
	private HashMap<Rule, Button> buttonMap;

	public DisplayLayer(BooleanProperty useInternalRules) {
		internalRulesList = new ArrayList<Rule>();
		buttonMap = new HashMap<Rule, Button>();

		currentRulesList = FXCollections.observableArrayList();
		currentRulesList.addListener(new ListChangeListener<Rule>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Rule> change) {
				while (change.next()) {
					if (!change.wasUpdated()) {
						// added to current list
						for (Rule rule : change.getAddedSubList()) {
							buttonMap.put(rule, rule.getDeleteButton());

							rule.getDeleteButton().setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									currentRulesList.remove(rule);

									Rule temp;
									if (useInternalRules.get()) {
										Iterator<Rule> iter = internalRulesList.iterator();
										while (iter.hasNext()) {
											temp = iter.next();
											if (temp == rule)
												iter.remove();
										}
									}
									buttonMap.remove(rule);
								}
							});

							// if using default rules, copy changes to internal
							// rules list
							if (useInternalRules.get())
								internalRulesList.add(rule);
						}
					}
				}
			}
		});

		useInternalRules.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO
				// using internal rules now
				// copy all internal rules to current list
				if (newValue) {
					currentRulesList.clear();
					currentRulesList.addAll(internalRulesList);
				}
				// not using internal rules anymore
				// copy all current rule changes back to internal list
				else {

				}
			}
		});
	}

	public ObservableList<Rule> getRulesList() {
		return currentRulesList;
	}

	/*
	 * Renderer for rules in ListView's in Layers tab
	 */
	public Callback<ListView<Rule>, ListCell<Rule>> getRuleCellFactory() {
		return new Callback<ListView<Rule>, ListCell<Rule>>() {
			@Override
			public ListCell<Rule> call(ListView<Rule> param) {
				ListCell<Rule> cell = new ListCell<Rule>() {
					@Override
					protected void updateItem(Rule item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null)
							setGraphic(item.getGraphic());
						else
							setGraphic(null);
						setPickOnBounds(false);
					}
				};
				return cell;
			}
		};
	}

}
