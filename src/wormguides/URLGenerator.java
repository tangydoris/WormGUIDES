package wormguides;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import wormguides.model.ColorRule;

public class URLGenerator {
	
	private enum UrlType {
		IOS, ANDROID, WEB;
	}
	
	public static String generateIOSURL(ArrayList<ColorRule> rules, int time, double rX, 
							double rY, double rZ, double tX, double tY, double scale, double dim) {
		
		return null;
	}
	
	public static String generateAndroidURL(ArrayList<ColorRule> rules, int time, double rX, 
							double rY, double rZ, double tX, double tY, double scale, double dim) {
		
		return null;
	}

	public static String generateWebURL(ArrayList<ColorRule> rules, int time, double rX, 
							double rY, double rZ, double tX, double tY, double scale, double dim) {
		
		return null;
	}
	
	private static String generateSetParameters(UrlType type, ArrayList<ColorRule> rules) {
		StringBuilder builder = new StringBuilder();
		for (ColorRule rule : rules) {
			String name = rule.getSearchedText();
			Color color = rule.getColor();
		}
		
		return builder.toString();
	}
	
	private static String generateViewParameters(UrlType type, int time, double rX, double rY, 
								double rZ, double tX, double tY, double scale, double dim) {
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
		
		// platform
		switch (type) {
			case IOS :		builder.append("/iOS/");
							break;
			case ANDROID : 	builder.append("/Android/");
							break;
			case WEB :		builder.append("/browser/");
							break;
		}
		
		return builder.toString();
	}
	
	private static final String IOSPREFIX = "wormguides://wormguides/testurlscript?/set/";
	private static final String ANDROIDPREFIX = "http://scene.wormguides.org/wormguides/testurlscript?/set/";
	private static final String WEBPREFIX = "http://scene.wormguides.org/wormguides/testurlscript?/set/";
	
	private static final String SYSTEMATIC 	= "-s",
								FUNCTIONAL 	= "-n",
								DESCRIPTION	= "-d";
}
