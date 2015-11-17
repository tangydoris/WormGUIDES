package wormguides;

import java.util.HashMap;
import wormguides.model.Rule;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class Layers {
	
	private ObservableList<Rule> rulesList;
	private HashMap<Rule, Button> buttonMap;
	
	public Layers(ListView<Rule> listView) {
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
	
}
