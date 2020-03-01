package jhin3.tui.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.graphics.Theme;

public class ThemeLoader {

	private String name;

	private Theme theme;

	/**
	 * Constructs a new theme loader. This will create the theme object
	 * instantly.
	 * 
	 * If no name was provided, a default theme will be used. If the selected
	 * theme is not available, some fallback options will be used.
	 * 
	 * @param name
	 *            the theme name
	 */
	public ThemeLoader(String name) {
		if (name != null && name != "") {
			this.name = name;
		} else {
			// No theme was set
			// Use default theme for Jhin3
			this.name = "2019";
		}

		// Try loading the custom theme (may fail)
		loadCustomTheme();

		if (this.theme == null) {
			// No jhin3 theme found
			// Try searching for a lanterna theme
			this.theme = LanternaThemes.getRegisteredTheme(name);
			if (this.theme == null) {
				// Lanterna theme not found
				// Using "businessmachine" as default theme
				this.theme = LanternaThemes
						.getRegisteredTheme("businessmachine");
			}
		}
	}

	/**
	 * Loads a custom made theme for jhin3 from the resources folder. If the
	 * theme is not available, {@link ThemeLoader#theme} will not be changed. In
	 * case of an error while loading the theme, the lanterna theme
	 * "businessmachine" will be applied (if possible).
	 */
	private void loadCustomTheme() {
		Properties properties = new Properties();

		try {
			// Load properties
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			InputStream resourceAsStream = classLoader.getResourceAsStream(
					"themes/" + name + "-theme.properties");
			if (resourceAsStream != null) {
				properties.load(resourceAsStream);
				resourceAsStream.close();

				// Create theme
				this.theme = new PropertyTheme(properties);
			}
		} catch (IllegalArgumentException | IOException e) {
			// Something's wrong, just try the businessmachine theme
			this.theme = LanternaThemes.getRegisteredTheme("businessmachine");
		}
	}

	public Theme getTheme() {
		return theme;
	}
}
