/*
 * Bao Lab 2016
 */

package wormguides;

import java.util.List;

import wormguides.models.Rule;

/**
 * Generator for a color scheme and scene parameters URL.
 */
public class URLGenerator {

    public static String generateIOS(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return "wormguides://wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim)
                + "/iOS/";
    }

    public static String generateAndroid(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim)
                + "/Android/";
    }

    public static String generateWeb(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim)
                + "/browser/";
    }

    public static String generateInternal(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateInternalParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dim)
                + "/browser/";
    }

    private static String generateParameterString(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return generateSetParameters(rules) + generateViewParameters(time, rX, rY, rZ, tX, tY, scale, dim);
    }

    private static String generateInternalParameterString(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return generateInternalSetParameters(rules) + generateViewParameters(time, rX, rY, rZ, tX, tY, scale, dim);
    }

    private static String generateInternalSetParameters(final List<Rule> rules) {
        final StringBuilder builder = new StringBuilder("/set");

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
                if (rule.isDescendantSelected()) {
                    builder.append("<");
                }
                // cell
                if (rule.isCellSelected()) {
                    builder.append("$");
                }
                // cell body
                if (rule.isCellBodySelected()) {
                    builder.append("@");
                }
                // ancestor
                if (rule.isAncestorSelected()) {
                    builder.append(">");
                }
            }

            // color
            String color = rule.getColor().toString();
            color = color.substring(color.indexOf("x") + 1, color.length() - 2);
            builder.append("+#ff").append(color);
        }
        return builder.toString();
    }

    private static String generateSetParameters(final List<Rule> rules) {
        final StringBuilder builder = new StringBuilder("/set");
        for (Rule rule : rules) {
            if (!rule.isMulticellularStructureRule()) {
                String ruleText = rule.getSearchedText();
                if (ruleText.contains("'")) {
                    ruleText = ruleText.substring(0, ruleText.lastIndexOf("'"));
                    ruleText = ruleText.substring(ruleText.indexOf("'") + 1, ruleText.length());
                }
                builder.append("/").append(ruleText);

                // search types
                if (rule.getSearchType() == null) {
                    System.out.println(rule.toStringFull());
                }
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
                // descendant (<)
                if (rule.isDescendantSelected()) {
                    builder.append("<");
                }
                // cell ($)
                if (rule.isCellSelected()) {
                    builder.append("$");
                }
                // ancestor (>)
                if (rule.isAncestorSelected()) {
                    builder.append(">");
                }

                // color
                String color = rule.getColor().toString();
                color = color.substring(color.indexOf("x") + 1, color.length() - 2);
                builder.append("+#ff").append(color);
            }
        }

        return builder.toString();
    }

    private static String generateViewParameters(
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dim) {

        return "/view"
                + "/time=" + time
                + "/rX=" + rX
                + "/rY=" + rY
                + "/rZ=" + rZ
                + "/tX=" + tX
                + "/tY=" + tY +
                "/scale=" + scale
                + "/dim=" + dim;
    }

}
