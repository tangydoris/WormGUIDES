package wormguides.view;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppFont {
	
	private static final Font font;
	
	private static final Font bolderFont;
	private static final Font boldFont;
	
	private static final Font billboardFont;
	
	
	static {
		font = new Font(14);
		
		bolderFont = Font.font("System", FontWeight.EXTRA_BOLD, 14);
		boldFont = Font.font("System", FontWeight.SEMI_BOLD, 14);
		
		billboardFont = Font.font("System", FontWeight.SEMI_BOLD, 6);
	}
	
	
	public static Font getBillboardFont() {
		return billboardFont;
	}
	
	
	public static Font getFont() {
		return font;
	}
	
	
	public static Font getBolderFont() {
		return bolderFont;
	}
	
	
	public static Font getBoldFont() {
		return boldFont;
	}
	
}
