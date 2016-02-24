package wormguides.model;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import wormguides.SearchOption;

/*
 * Rule used for multicellular structures
 */
public class MulticellularStructureRule extends Rule {

	public MulticellularStructureRule(String searched, Color color, ArrayList<SearchOption> options) {
		super(searched, color, options, true);
	}

	// @param name : lineageName of multicellular structure
	public boolean appliesTo(String name) {
		if (isVisible()) {
			return this.getSearchedText().equals(name);
		}
		return false;
	}
}
