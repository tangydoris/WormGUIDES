package wormguides;

import wormguides.model.ColorRule;
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
		if (listView==null)
			listView = new ListView<ColorRule>();
		rulesListView = listView;
		
		rulesList = FXCollections.observableArrayList();
		addDefaultRules();	
		
		rulesListView.setStyle("-fx-background-insets: 0 ;");
		rulesListView.setItems(rulesList);
		
		makeCellFactory();
	}
	
	private void addDefaultRules() {
		rulesList.add(new ColorRule("ABa", Color.RED, Search.Option.CELL, Search.Option.DESCENDANT, Search.Option.ANCESTOR));
		rulesList.add(new ColorRule("ABp", Color.BLUE, Search.Option.CELL, Search.Option.DESCENDANT));
		rulesList.add(new ColorRule("P", Color.GREEN, Search.Option.CELL, Search.Option.DESCENDANT));
	}
	
	private void makeCellFactory() {
		rulesListView.setCellFactory(new Callback<ListView<ColorRule>, ListCell<ColorRule>>() {
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
