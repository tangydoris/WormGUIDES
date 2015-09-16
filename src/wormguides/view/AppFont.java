package wormguides.view;

import javafx.scene.text.Font;

public class AppFont {
	
	private static final Font font;
	
	static {
		font = new Font(14);
	}
	
	public static Font getFont() {
		return font;
	}
}
