package wormguides;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import wormguides.model.ColorRule;
import wormguides.view.Window3DSubScene;

public class URLLoader {
	
	private Window3DSubScene window3D;
	private ObservableList<ColorRule> rulesList;
	
	public URLLoader(Window3DSubScene window3D) {
		this.window3D = window3D;
		rulesList = window3D.getObservableRulesList();
	}
	
	public void parseURL(String url) {
		// TODO add error message
		if (window3D==null)
			return;
		
		if (!url.contains("testurlscript?/"))
			return;
		
		String[] args = url.split("/");
		ArrayList<String> ruleArgs = new ArrayList<String>();
		ArrayList<String> viewArgs = new ArrayList<String>();
		
		// add rules and view parameters to their ArrayList's
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("set")) {
				// do not need the 'set' String
				i++;
				// iterate through set parameters until we hit the view parameters
				while (!args[i].equals("view")) {
					ruleArgs.add(args[i].trim());
					i++;
				}
				
				// iterate through view parameters
				// do not need the 'view' String
				i++;
				while (!args[i].equals("iOS") && !args[i].equals("Android") 
											&& !args[i].equals("browser")) {
					viewArgs.add(args[i]);
					i++;
				}
			}
			else
				i++;
		}
		
		for (String arg : ruleArgs)
			System.out.println(arg);
		for (String arg : viewArgs)
			System.out.println(arg);
		
		// process arguments
		parseRules(ruleArgs);
		parseViewArgs(viewArgs);
	}
	
	private void parseRules(ArrayList<String> rules) {
		rulesList.clear();
		for (String rule : rules) {
			try {
				String name = rule.substring(0, rule.indexOf("-"));
				String colorString = rule.substring(rule.indexOf("+")+6, rule.length());
				
				ArrayList<SearchOption> options = new ArrayList<SearchOption>();
				if (rule.contains("%3C"))
					options.add(SearchOption.ANCESTOR);
				if (rule.contains("$"))
					options.add(SearchOption.CELL);
				if (rule.contains("%3E"))
					options.add(SearchOption.DESCENDANT);
				
				if (rule.contains("-s")) {
					rulesList.add(new ColorRule(name, Color.web(colorString), 
											options, SearchType.SYSTEMATIC));
				}
				if (rule.contains("-n")) {
					rulesList.add(new ColorRule("'"+name+"' "+SearchType.FUNCTIONAL.getDescription(), 
									Color.web(colorString), options, SearchType.FUNCTIONAL));
				}
				if (rule.contains("-d")) {
					rulesList.add(new ColorRule("'"+name+"' "+SearchType.DESCRIPTION.getDescription(), 
									Color.web(colorString), options, SearchType.DESCRIPTION));
				}
				if (rule.contains("-g")) {
					rulesList.add(new ColorRule("'"+name+"' "+SearchType.GENE.getDescription(), 
									Color.web(colorString), options, SearchType.GENE));
				}
				
				Search.addCellsToEmptyRules();
			}
			catch (StringIndexOutOfBoundsException e) {
				System.out.println("invalid color rule format.");
				e.printStackTrace();
			}
		}
	}
	
	private void parseViewArgs(ArrayList<String> viewArgs) {
		
	}
}
