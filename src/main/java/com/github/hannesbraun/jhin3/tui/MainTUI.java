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
package com.github.hannesbraun.jhin3.tui;

import com.github.hannesbraun.jhin3.soundboard.Soundboard;
import com.github.hannesbraun.jhin3.tui.misc.JhinTerminalResizeListener;
import com.github.hannesbraun.jhin3.tui.misc.ThemeLoader;
import com.github.hannesbraun.jhin3.tui.window.SoundboardWindow;
import com.github.hannesbraun.jhin3.tui.window.TimeWindow;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class MainTUI
{
	private String theme;

	public void exec(String configFile)
	{
		Soundboard soundboard = new Soundboard(configFile);

		// GUI running flag: used for closing the windows
		MutableBoolean guiRunning = new MutableBoolean(true);

		DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
		Terminal terminal;
		TerminalScreen screen;
		WindowBasedTextGUI gui;

		ThemeLoader themeLoader = new ThemeLoader(theme);

		SoundboardWindow soundboardWindow;
		TimeWindow timeWindow;
		JhinTerminalResizeListener resizeListener;

		try {
			// Create basic tui elements
			terminal = defaultTerminalFactory.createTerminal();
			screen = new TerminalScreen(terminal);
			gui = new MultiWindowTextGUI(screen, TextColor.ANSI.BLACK);
			screen.startScreen();

			gui.setTheme(themeLoader.getTheme());

			soundboardWindow = new SoundboardWindow(terminal.getTerminalSize(), soundboard, guiRunning);
			timeWindow = new TimeWindow(terminal.getTerminalSize(), guiRunning);

			// Enable resizing of the inner windows
			resizeListener = new JhinTerminalResizeListener(gui.getScreen().getTerminalSize());
			resizeListener.addWindow(soundboardWindow);
			resizeListener.addWindow(timeWindow);
			terminal.addResizeListener(resizeListener);

			// Add windows to tui
			gui.addWindow(timeWindow);
			gui.addWindow(soundboardWindow);

			// Start operating mode
			soundboardWindow.waitUntilClosed();

			screen.stopScreen();
		} catch (IOException e) {
			e.printStackTrace();
		}

		soundboard.kill();
	}

	public void setTheme(String theme)
	{
		this.theme = theme;
	}
}
