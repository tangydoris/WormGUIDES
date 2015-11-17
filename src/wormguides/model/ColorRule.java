package wormguides.model;

import java.util.ArrayList;
import java.util.Arrays;

import wormguides.SearchOption;
import wormguides.SearchType;
import javafx.scene.paint.Color;

public class ColorRule extends Rule{
	
	
	private SearchType type;
	
	
	public ColorRule(String searched, Color color) {
		this(searched, color, new SearchOption[] {SearchOption.CELL, SearchOption.DESCENDANT});
	}
	
	
	public ColorRule(String searched, Color color, SearchOption...options) {
		this(searched, color, new ArrayList<SearchOption>(Arrays.asList(options)), SearchType.SYSTEMATIC);
	}
	
	
	public ColorRule(String searched, Color color, ArrayList<SearchOption> options, SearchType type) {
		super (searched, color, options);
		this.type = type;
	}

	public SearchType getSearchType() {
		return type;
	}
	
}
