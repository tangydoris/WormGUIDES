package wormguides;

import java.util.ArrayList;

import wormguides.view.Window3DSubScene;

public class URLLoader {
	
	private Window3DSubScene window3D;
	
	public URLLoader(Window3DSubScene subscene) {
		window3D = subscene;
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
		
		/*
		for (String arg : ruleArgs)
			System.out.println(arg);
		for (String arg : viewArgs)
			System.out.println(arg);
		*/
		
		// process arguments
		parseRules(ruleArgs);
		parseViewArgs(viewArgs);
	}
	
	private void parseRules(ArrayList<String> rules) {
		
	}
	
	private void parseViewArgs(ArrayList<String> viewArgs) {
		
	}
}
