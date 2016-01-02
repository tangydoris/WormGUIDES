package wormguides;

import java.util.HashMap;
import wormguides.model.Rule;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class DisplayLayer {
	
	private ObservableList<Rule> rulesList;
	private HashMap<Rule, Button> buttonMap;
	
	public DisplayLayer(ListView<Rule> listView) {
		if (listView==null)
			listView = new ListView<Rule>();
		
		buttonMap = new HashMap<Rule, Button>();
		
		rulesList = FXCollections.observableArrayList();
		rulesList.addListener(new ListChangeListener<Rule>() {
			@Override
			public void onChanged(ListChangeListener
									.Change<? extends Rule> change) {
				while (change.next()) {
					if (!change.wasUpdated()) {
						// added to list
						for (Rule rule : change.getAddedSubList()) {
							Rule ruleToRemove = rule;
							buttonMap.put(ruleToRemove, rule.getDeleteButton());
							
							rule.getDeleteButton().setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									rulesList.remove(ruleToRemove);
									buttonMap.remove(ruleToRemove);
								}
							});
						}
					}
				}
			}
		});
			
		listView.setItems(rulesList);
	}
	
	public ObservableList<Rule> getRulesList() {
		return rulesList;
	}
	
	
	/*
	 * Renderer for rules in ListView's in Layers tab
	 */
	public Callback<ListView<Rule>, ListCell<Rule>> getRuleCellFactory() {
		return new Callback<ListView<Rule>, ListCell<Rule>>() {
			@Override
			public ListCell<Rule> call(ListView<Rule> param) {
				ListCell<Rule> cell = new ListCell<Rule>(){
	                @Override
	                protected void updateItem(Rule item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (item != null) 
	                    	setGraphic(item.getGraphic());
	                	else
	                		setGraphic(null);
	            	}
	        	};
	        	return cell;
			}
		};
	}

	
}
