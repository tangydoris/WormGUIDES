package wormguides;

import java.util.ArrayList;

import wormguides.models.Rule;

public class URLGenerator {

	public static String generateIOS(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
        String builder = "wormguides://wormguides/testurlscript?" + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim) +
                "/iOS/";
        return builder;
    }

	public static String generateAndroid(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
        String builder = "http://scene.wormguides.org/wormguides/testurlscript?" + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim) +
                "/Android/";
        return builder;
    }

	public static String generateWeb(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
        String builder = "http://scene.wormguides.org/wormguides/testurlscript?" + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim) +
                "/browser/";
        return builder;
    }

	public static String generateInternal(ArrayList<Rule> rules, int time, double rX, double rY, double rZ, double tX,
			double tY, double scale, double dim) {
        String builder =
                "http://scene.wormguides.org/wormguides/testurlscript?" + generateInternalParameterString(
                        rules,
                        time,
                        rX,
                        rY,
                        rZ,
                        tX,
                        tY,
                        scale,
                        dim) +
                        "/browser/";
        return builder;
    }

	private static String generateParameterString(ArrayList<Rule> rules, int time, double rX, double rY, double rZ,
			double tX, double tY, double scale, double dim) {
        String builder = generateSetParameters(rules) +
                generateViewParameters(time, rX, rY, rZ, tX, tY, scale, dim);
        return builder;
    }

	private static String generateInternalParameterString(ArrayList<Rule> rules, int time, double rX, double rY,
			double rZ, double tX, double tY, double scale, double dim) {
        String builder = generateInternalSetParameters(rules) +
                generateViewParameters(time, rX, rY, rZ, tX, tY, scale, dim);
        return builder;
    }

	private static String generateInternalSetParameters(ArrayList<Rule> rules) {
		StringBuilder builder = new StringBuilder("/set");

		for (Rule rule : rules) {
			String ruleName = rule.getSearchedText();
            if (ruleName.contains("'")) {
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
                if (ruleText.contains("'")) {
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
        String builder = "/view" + "/time=" + time +
                "/rX=" + rX +
                "/rY=" + rY +
                "/rZ=" + rZ +
                "/tX=" + tX +
                "/tY=" + tY +
                "/scale=" + scale +
                "/dim=" + dim;

        // time

        // rotation

        // translation

        // others

        return builder;
    }

}
