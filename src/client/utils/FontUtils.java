package client.utils;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class FontUtils
{
	private static double fontScaling = 1.0;
	
	public static void setFont(JComponent comp, int size) {
		Font font = comp.getFont();
		font = font.deriveFont(font.getStyle(), calcSize(size));
		comp.setFont(font);
	}

	public static double getFontScaling() {
		return fontScaling;
	}

	public static void setFontScaling(double fontScaling) {
		FontUtils.fontScaling = fontScaling;
	}

	public static Font getFont(int size) {
		Font tmpFont = new JLabel("").getFont();
		return tmpFont.deriveFont(tmpFont.getStyle(), calcSize(size));
	}
	
	private static int calcSize(int size) {
		int temp = (int) (size * fontScaling);
		return temp >= 8 ? temp : 8;
	}
}

