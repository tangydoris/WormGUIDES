package wormguides;

import java.util.ArrayList;

import wormguides.model.ColorRule;
import wormguides.model.Rule;
import wormguides.model.MulticellularStructureRule;

public class URLGenerator {

	public static String generateIOS(ArrayList<ColorRule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder("wormguides://wormguides/testurlscript?");
		builder.append(generateParameterString(rules, time, rX, rY, rZ, tX, tY, scale, dim));
		builder.append("/iOS/");
		return builder.toString();
	}

	public static String generateAndroid(ArrayList<ColorRule> rules, int time, double rX, double rY, double rZ,
			double tX, double tY, double scale, double dim) {
		StringBuilder builder = new StringBuilder("http://scene.wormguides.org/wormguides/testurlscript?");
		builder.append(generateParameterString(rules, time, rX, rY, rZ, tX, tY, scale, dim));
		builder.append("/Android/");
		return builder.toString();
	}

	public static String generateWeb(ArrayList<ColorRule> rules, int time, double rX, double rY, double rZ, double tX,
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

	private static String generateParameterString(ArrayList<ColorRule> rules, int time, double rX, double rY, double rZ,
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
			// rule from cell search
			if (rule instanceof ColorRule) {
				ColorRule colorRule = (ColorRule) rule;
				String ruleText = colorRule.getSearchedText();

				if (ruleText.indexOf("'") != -1) {
					ruleText = ruleText.substring(0, ruleText.lastIndexOf("'"));
					ruleText = ruleText.substring(ruleText.indexOf("'") + 1, ruleText.length());
				}
				builder.append("/").append(ruleText);

				// search types
				switch (colorRule.getSearchType()) {
				case LINEAGE:
					builder.append("-s");
					break;
				case DESCRIPTION:
					builder.append("-d");
					break;
				case FUNCTIONAL:
					builder.append("-n");
					break;
				case MULTICELL:
					builder.append("-m");
					break;
				case GENE:
					builder.append("-g");
					break;
				default:
					break;
				}

				// ancestry modifiers
				// descendant <
				if (colorRule.isDescendantSelected())
					builder.append("%3E");
				// cell $
				if (colorRule.isCellSelected())
					builder.append("$");
				// cell body #
				if (colorRule.isCellBodySelected())
					builder.append("#");
				// ancestor >
				if (colorRule.isAncestorSelected())
					builder.append("%3C");

				// color
				String color = colorRule.getColor().toString();
				color = color.substring(color.indexOf("x") + 1, color.length() - 2);
				builder.append("+%23ff").append(color);

			}
			// rule from multicellular structure search
			else if (rule instanceof MulticellularStructureRule) {
				MulticellularStructureRule structureRule = (MulticellularStructureRule) rule;
				String ruleName = structureRule.getSearchedText();
				
				builder.append("/").append(ruleName);
				String color = structureRule.getColor().toString();
				color = color.substring(color.indexOf("x") + 1, color.length() - 2);
				builder.append("+%23ff").append(color);
			}
		}

		return builder.toString();

	}

	private static String generateSetParameters(ArrayList<ColorRule> rules) {
		StringBuilder builder = new StringBuilder("/set");

		for (ColorRule rule : rules) {
			String ruleText = rule.getSearchedText();
			if (ruleText.indexOf("'") != -1) {
				ruleText = ruleText.substring(0, ruleText.lastIndexOf("'"));
				ruleText = ruleText.substring(ruleText.indexOf("'") + 1, ruleText.length());
			}
			builder.append("/").append(ruleText);

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
			case GENE:
				builder.append("-g");
				break;
			default:
				break;
			}

			// ancestry modifiers
			// descendant <
			if (rule.isDescendantSelected())
				builder.append("%3E");
			// cell $
			if (rule.isCellSelected())
				builder.append("$");
			// ancestor >
			if (rule.isAncestorSelected())
				builder.append("%3C");

			// color
			String color = rule.getColor().toString();
			color = color.substring(color.indexOf("x") + 1, color.length() - 2);
			builder.append("+%23ff").append(color);
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
