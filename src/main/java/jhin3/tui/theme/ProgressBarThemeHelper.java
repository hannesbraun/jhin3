package jhin3.tui.theme;

import java.util.Properties;

import com.googlecode.lanterna.TextColor;

public class ProgressBarThemeHelper {

	public static Properties getProperties(TextColor foreground,
			TextColor background) {
		Properties properties = new Properties();

		properties.setProperty(
				"com.googlecode.lanterna.gui2.ProgressBar.foreground[ACTIVE]",
				getColorString(foreground));
		properties.setProperty(
				"com.googlecode.lanterna.gui2.ProgressBar.background[ACTIVE]",
				getColorString(background));

		return properties;
	}

	private static String getColorString(TextColor color) {
		if (color == TextColor.ANSI.RED) {
			return "red";
		} else if (color == TextColor.ANSI.GREEN) {
			return "green";
		} else if (color == TextColor.ANSI.BLUE) {
			return "blue";
		} else if (color == TextColor.ANSI.YELLOW) {
			return "yellow";
		} else if (color == TextColor.ANSI.CYAN) {
			return "cyan";
		} else if (color == TextColor.ANSI.BLACK) {
			return "black";
		} else if (color == TextColor.ANSI.MAGENTA) {
			return "magenta";
		} else if (color == TextColor.ANSI.WHITE) {
			return "white";
		} else {
			return "black";
		}
	}

}
