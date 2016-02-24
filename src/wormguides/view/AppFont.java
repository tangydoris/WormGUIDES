package wormguides.view;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppFont {

	private static final Font font;

	private static final Font bolderFont;
	private static final Font boldFont;

	private static final Font billboardFont;
	private static final Font spriteAndOverlayFont;

	static {
		font = new Font(14);

		bolderFont = Font.font("System", FontWeight.EXTRA_BOLD, 14);
		boldFont = Font.font("System", FontWeight.SEMI_BOLD, 14);

		billboardFont = Font.font("System", FontWeight.BOLD, 10);
		spriteAndOverlayFont = Font.font("System", FontWeight.BOLD, 16);
	}

	public static Font getSpriteAndOverlayFont() {
		return spriteAndOverlayFont;
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
