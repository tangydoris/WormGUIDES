package wormguides;

import java.util.ArrayList;

import wormguides.models.Rule;

public class URLGenerator {

	public static String generateIOS(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder("wormguides://wormguides/testurlscript?");
		builder.append(generateParameterString(rules, time, rX, rY, rZ, tX, tY, scale, dim));
		builder.append("/iOS/");
		return builder.toString();
	}

	public static String generateAndroid(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder("http://scene.wormguides.org/wormguides/testurlscript?");
		builder.append(generateParameterString(rules, time, rX, rY, rZ, tX, tY, scale, dim));
		builder.append("/Android/");
		return builder.toString();
	}

	public static String generateWeb(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder("http://scene.wormguides.org/wormguides/testurlscript?");
		builder.append(generateParameterString(rules, time, rX, rY, rZ, tX, tY, scale, dim));
		builder.append("/browser/");
		return builder.toString();
	}

	public static String generateInternal(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder("http://scene.wormguides.org/wormguides/testurlscript?");
		builder.append(generateInternalParameterString(rules, time, rX, rY, rZ, tX, tY, scale, dim));
		builder.append("/browser/");
		return builder.toString();
	}

	private static String generateParameterString(ArrayList<Rule> rules, int time, double rX, double rY, double rZ,
			double tX, double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder();
		builder.append(generateSetParameters(rules));
		builder.append(generateViewParameters(time, rX, rY, rZ, tX, tY, scale, dim));
		return builder.toString();
	}

	private static String generateInternalParameterString(ArrayList<Rule> rules, int time, double rX, double rY,
			double rZ, double tX, double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder();
		builder.append(generateInternalSetParameters(rules));
		builder.append(generateViewParameters(time, rX, rY, rZ, tX, tY, scale, dim));
		return builder.toString();
	}

	private static String generateInternalSetParameters(ArrayList<Rule> rules) {
		StringBuilder builder = new StringBuilder("/set");

		for (Rule rule : rules) {
			String ruleName = rule.getSearchedText();
			if (ruleName.indexOf("'") != -1) {
				ruleName = ruleName.substring(0, ruleName.lastIndexOf("'"));
				ruleName = ruleName.substring(ruleName.indexOf("'") + 1, ruleName.length());
			}
			builder.append("/").append(ruleName);

			// rule from cell search
			// rule from multicellular structure search
			if (rule.isMulticellularStructureRule()) {
				// specify a multicellular structure rule that is not
				// cell-based, but scene name-based
				builder.append("-M");
			} else {
				// search types
				switch (rule.getSearchType()) {
				case LINEAGE:
					builder.append("-s");
					break;
				case DESCRIPTION:
					builder.append("-d");
					break;
				case FUNCTIONAL:
					builder.append("-n");
					break;
				case MULTICELLULAR_CELL_BASED:
					builder.append("-m");
					break;
				case GENE:
					builder.append("-g");
					break;
				case NEIGHBOR:
					builder.append("-b");
					break;
				case CONNECTOME:
					builder.append("-c");
					break;
				default:
					break;
				}

				// ancestry modifiers
				// descendant
				if (rule.isDescendantSelected())
					builder.append("<");
				// cell
				if (rule.isCellSelected())
					builder.append("$");
				// cell body
				if (rule.isCellBodySelected())
					builder.append("@");
				// ancestor
				if (rule.isAncestorSelected())
					builder.append(">");
			}

			// color
			String color = rule.getColor().toString();
			color = color.substring(color.indexOf("x") + 1, color.length() - 2);
			builder.append("+#ff").append(color);
		}
		return builder.toString();
	}

	private static String generateSetParameters(ArrayList<Rule> rules) {
		StringBuilder builder = new StringBuilder("/set");

		for (Rule rule : rules) {
			if (!rule.isMulticellularStructureRule()) {
				String ruleText = rule.getSearchedText();
				if (ruleText.indexOf("'") != -1) {
					ruleText = ruleText.substring(0, ruleText.lastIndexOf("'"));
					ruleText = ruleText.substring(ruleText.indexOf("'") + 1, ruleText.length());
				}
				builder.append("/").append(ruleText);

				// search types
				if (rule.getSearchType() == null)
					System.out.println(rule.toStringFull());
				switch (rule.getSearchType()) {
				case LINEAGE:
					builder.append("-s");
					break;
				case DESCRIPTION:
					builder.append("-d");
					break;
				case FUNCTIONAL:
					builder.append("-n");
					break;
				case GENE:
					builder.append("-g");
					break;
				default:
					break;
				}

				// ancestry modifiers
				// descendant <
				if (rule.isDescendantSelected())
					builder.append("<");
				// cell $
				if (rule.isCellSelected())
					builder.append("$");
				// ancestor >
				if (rule.isAncestorSelected())
					builder.append(">");

				// color
				String color = rule.getColor().toString();
				color = color.substring(color.indexOf("x") + 1, color.length() - 2);
				builder.append("+#ff").append(color);
			}
		}

		return builder.toString();
	}

	private static String generateViewParameters(int time, double rX, double rY, double rZ, double tX, double tY,
			double scale, double dim) {
		StringBuilder builder = new StringBuilder("/view");

		// time
		builder.append("/time=").append(time);

		// rotation
		builder.append("/rX=").append(rX);
		builder.append("/rY=").append(rY);
		builder.append("/rZ=").append(rZ);

		// translation
		builder.append("/tX=").append(tX);
		builder.append("/tY=").append(tY);

		// others
		builder.append("/scale=").append(scale);
		builder.append("/dim=").append(dim);

		return builder.toString();
	}

}
