/*******************************************************************************
 * Copyright 2020 Hannes Braun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.github.hannesbraun.jhin3.tui.misc;

import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.PropertyTheme;
import com.googlecode.lanterna.graphics.Theme;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ThemeLoader
{
	private String name;

	private final String defaultThemeName = "2019";

	private Theme theme;

	public static boolean isThemeSupported(String name)
	{
		switch (name) {
		case "blaster":
		case "defrost":
			// These themes require more space for some elements.
			// This results in a user interface with missing elements.
			return false;
		default:
			return true;
		}
	}

	/**
	 * Constructs a new theme loader. This will create the theme object instantly.
	 *
	 * If no name was provided, a default theme will be used. If the selected theme
	 * is not available, some fallback options will be used.
	 *
	 * @param name the theme name
	 */
	public ThemeLoader(String name)
	{
		if (name != null && name != "") {
			this.name = name;
		} else {
			// No theme was set
			// Use default theme for Jhin3
			this.name = defaultThemeName;
		}

		// Try loading the custom theme (may fail)
		loadCustomTheme();

		if (this.theme == null) {
			// No jhin3 theme found
			// Try searching for a lanterna theme
			this.theme = LanternaThemes.getRegisteredTheme(this.name);
			if (this.theme == null) {
				// Lanterna theme not found
				// Try using the default theme
				this.name = defaultThemeName;
				loadCustomTheme();
			}
		}
	}

	/**
	 * Loads a custom made theme for jhin3 from the resources directory. If the
	 * theme is not available, {@link ThemeLoader#theme} will not be changed. In
	 * case of an error while loading the theme, the lanterna theme
	 * "businessmachine" will be applied (if possible).
	 */
	private void loadCustomTheme()
	{
		Properties properties = new Properties();

		try {
			// Load properties
			InputStream resourceAsStream = getClass().getResourceAsStream("/themes/" + name + "-theme.properties");
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

	public Theme getTheme()
	{
		return theme;
	}
}
