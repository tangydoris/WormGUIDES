package wormguides;

import wormguides.model.ColorRule;
import wormguides.model.ColorRuleCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class Layers {
	
	private ObservableList<ColorRule> rulesList;
	private ListView<ColorRule> rulesListView;
	
	public Layers() {
		this(new ListView<ColorRule>());
	}

	public Layers(ListView<ColorRule> listView) {
		rulesList = FXCollections.observableArrayList();
		addDefaultRules();
		
		if (listView==null)
			listView = new ListView<ColorRule>();

		rulesListView = listView;
		rulesListView.setItems(rulesList);
		
		makeCellFactory();
	}
	
	private void addDefaultRules() {
		rulesList.addAll(
				new ColorRule("ABa", Color.RED, Search.Option.CELL, Search.Option.DESCENDANT),
				new ColorRule("ABp", Color.BLUE, Search.Option.CELL, Search.Option.DESCENDANT),
				new ColorRule("P", Color.GREEN, Search.Option.CELL, Search.Option.DESCENDANT)
		);
	}
	
	private void makeCellFactory() {
		rulesListView.setCellFactory(new Callback<ListView<ColorRule>, ListCell<ColorRule>>() {
			@Override
			public ListCell<ColorRule> call(ListView<ColorRule> listView) {
				return new ColorRuleCell();
			}
		});
	}
	
	public AddSearchListener getAddSearchListener() {
		return new AddSearchListener();
	}
	
	private class AddSearchListener implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("adding search...");
		}
	}

}
