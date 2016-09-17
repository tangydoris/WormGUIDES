/*
 * Bao Lab 2016
 */

package wormguides;

/**
 * Enum for anatomy terms which constitute special cases in the info window
 * 
 * Each AnatomyTerm has a term and a description
 * 
 * @author bradenkatzman
 *
 */
public enum AnatomyTerm {

	AMPHID_SENSILLA("Amphid Sensilla", "The amphids are a pair of laterally "
			+ "located sensilla in the head and are the worms primary chemosensory organs.");

	private String term;
	private String description;

	AnatomyTerm(String term, String description) {
		this.term = term;
		this.description = description;
	}

	public String getTerm() {
		return this.term;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return getTerm() + ": " + getDescription();
	}
}
