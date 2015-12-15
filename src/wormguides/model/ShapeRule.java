package wormguides.model;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import wormguides.SearchOption;

public class ShapeRule extends Rule {
	
	public ShapeRule(String searched, Color color, ArrayList<SearchOption> options) {
		super(searched, color, options, true);
		disableDescendantOption();
	}
	
//	public void setCells(String name) {
//		getCells().add(name);		
//	}
		
	// @param name : lineageName of cell body
	public boolean appliesTo(String name) {
		if (isVisible()) {
			return this.getSearchedText().equals(name);
		}
		return false;
	}
}
