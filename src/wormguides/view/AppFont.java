package wormguides.view;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppFont {
	
	private static final Font font;
	private static final Font boldFont;
	
	
	static {
		font = new Font(14);
		boldFont = Font.font("System", FontWeight.BOLD, 14);
	}
	
	
	public static Font getFont() {
		return font;
	}
	
	
	public static Font getBoldFont() {
		return boldFont;
	}
	
}
