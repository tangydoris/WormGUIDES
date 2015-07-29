package wormguides;

import java.util.ArrayList;

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
	private ArrayList<Button> closeButtonsList;
	private ListView<ColorRule> rulesListView;
	
	public Layers() {
		this(new ListView<ColorRule>());
	}

	public Layers(ListView<ColorRule> listView) {
		if (listView==null)
			listView = new ListView<ColorRule>();
		rulesListView = listView;
		
		closeButtonsList = new ArrayList<Button>();
		
		rulesList = FXCollections.observableArrayList();
		rulesList.addListener(new ListChangeListener<ColorRule>() {
			@Override
			public void onChanged(ListChangeListener
									.Change<? extends ColorRule> change) {
				while (change.next()) {
					if (!change.wasUpdated()) {
						// added to list
						for (ColorRule rule : change.getAddedSubList()) {
							System.out.println("added rule "+rule.toStringFull());
							rule.getDeleteButton().setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									int index = -1;
									for (Button button : closeButtonsList) {
										if (button==(Button)event.getSource()) {
											index = closeButtonsList.indexOf(button);
											break;
										}
									}
									if (index != -1) {
										closeButtonsList.remove(index);
										rulesList.remove(index);
									}
								}
							});
							closeButtonsList.add(rule.getDeleteButton());
						}
						// removed from list (need to implement?)
						for (ColorRule rule : change.getRemoved()) {
							rulesList.remove(rule);
						}
					}
				}
			}
		});
		addDefaultRules();	
		
		rulesListView.setStyle("-fx-background-insets: 1 ;");
		rulesListView.setItems(rulesList);
		
		makeCellFactory();
	}
	
	public ObservableList<ColorRule> getRulesList() {
		return rulesList;
	}
	
	private void addDefaultRules() {
		rulesList.add(new ColorRule("ABa", Color.RED, Search.Option.CELL, 
						Search.Option.DESCENDANT, Search.Option.ANCESTOR));
		rulesList.add(new ColorRule("ABp", Color.BLUE, Search.Option.CELL, 
						Search.Option.DESCENDANT));
		rulesList.add(new ColorRule("P", Color.GREEN, Search.Option.CELL, 
						Search.Option.DESCENDANT));
		rulesList.add(new ColorRule("EMS", Color.YELLOW, Search.Option.CELL, 
						Search.Option.DESCENDANT));
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
                        if (item != null) {
                            setGraphic(item.getHBox());
                        }
                    }
                };
                return cell;
			}
		});
	}
	
	public EventHandler<ActionEvent> getDeleteButtonListener() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		};
	}
	
}
