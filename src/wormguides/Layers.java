package wormguides;

import java.util.HashMap;

import wormguides.model.ColorRule;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class Layers {
	
	private ObservableList<ColorRule> rulesList;
	private HashMap<ColorRule, Button> buttonMap;
	private ListView<ColorRule> rulesListView;
	
	//private BooleanProperty updated;
	
	public Layers() {
		this(new ListView<ColorRule>());
	}

	public Layers(ListView<ColorRule> listView) {
		if (listView==null)
			listView = new ListView<ColorRule>();
		rulesListView = listView;
		
		buttonMap = new HashMap<ColorRule, Button>();
		
		rulesList = FXCollections.observableArrayList();
		rulesList.addListener(new ListChangeListener<ColorRule>() {
			@Override
			public void onChanged(ListChangeListener
									.Change<? extends ColorRule> change) {
				while (change.next()) {
					if (!change.wasUpdated()) {
						// added to list
						for (ColorRule rule : change.getAddedSubList()) {
							//System.out.println("added rule "+rule.toStringFull());
							ColorRule ruleToRemove = rule;
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
			
		rulesListView.setItems(rulesList);
		makeCellFactory();
	}
	
	public ObservableList<ColorRule> getRulesList() {
		return rulesList;
	}
	
	public void addDefaultRules() {
		rulesList.add(new ColorRule("ABa", Color.RED));
		rulesList.add(new ColorRule("ABp", Color.BLUE));
		rulesList.add(new ColorRule("EMS", Color.GREEN));
		rulesList.add(new ColorRule("P1", Color.YELLOW, SearchOption.CELL));
		rulesList.add(new ColorRule("P2", Color.YELLOW));
	}
	
	private void makeCellFactory() {
		rulesListView.setCellFactory(new Callback<ListView<ColorRule>, 
										ListCell<ColorRule>>() {
			@Override
			public ListCell<ColorRule> call(ListView<ColorRule> listView) {
				ListCell<ColorRule> cell = new ListCell<ColorRule>(){
                    @Override
                    protected void updateItem(ColorRule item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) 
                            setGraphic(item.getHBox());
                        else
                        	setGraphic(null);
                    }
                };
                return cell;
			}
		});
	}
	
}
