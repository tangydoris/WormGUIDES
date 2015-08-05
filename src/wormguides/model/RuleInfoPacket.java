package wormguides.model;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import wormguides.SearchOption;

public class RuleInfoPacket {
	private final String name;
	private Color color;
	private ArrayList<SearchOption> optionsList;
	//private ArrayList<String> searchResultsList;
	public RuleInfoPacket(String name, Color color, 
							//ArrayList<String> searchResultsList,
							SearchOption[] options) {
		this.name = name;
		this.color = color;
		//this.searchResultsList = searchResultsList;
		optionsList = new ArrayList<SearchOption>(Arrays.asList(options));
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public ArrayList<SearchOption> getOptions() {
		return new ArrayList<SearchOption>(Arrays.asList(
				optionsList.toArray(new SearchOption[optionsList.size()])));
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setOptions(ArrayList<SearchOption> options) {
		optionsList.clear();
		optionsList.addAll(options);
	}
	
	public void setCellSelected(boolean selected) {
		if (selected) {
			if (!optionsList.contains(SearchOption.CELL))
				optionsList.add(SearchOption.CELL);
		}
		else
			optionsList.remove(SearchOption.CELL);
	}
	
	public void setDescendantSelected(boolean selected) {
		if (selected) {
			if (!optionsList.contains(SearchOption.DESCENDANT))
				optionsList.add(SearchOption.DESCENDANT);
		}
		else
			optionsList.remove(SearchOption.DESCENDANT);
	}
	
	public void setAncestorSelected(boolean selected) {
		if (selected) {
			if (!optionsList.contains(SearchOption.ANCESTOR))
				optionsList.add(SearchOption.ANCESTOR);
		}
		else
			optionsList.remove(SearchOption.ANCESTOR);
	}
	
	public boolean isCellSelected() {
		return optionsList.contains(SearchOption.CELL);
	}
	
	public boolean isDescendantSelected() {
		return optionsList.contains(SearchOption.DESCENDANT);
	}
	
	public boolean isAncestorSelected() {
		return optionsList.contains(SearchOption.ANCESTOR);
	}
	
	public String toString() {
		String out = "packet info: "
					+ getName()+" "
					+ getColor()+" ";
		for (SearchOption option : getOptions())
			out += option+" ";
		return out;
	}
}
