package jhin3.tui;

import java.io.IOException;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import jhin3.soundboard.Soundboard;
import jhin3.tui.misc.JhinTerminalResizeListener;
import jhin3.tui.misc.ThemeLoader;
import jhin3.tui.window.SoundboardWindow;
import jhin3.tui.window.TimeWindow;

public class MainTUI {

	private String theme;

	public void exec(String configFile) {

		Soundboard soundboard = new Soundboard(configFile);

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

			soundboardWindow = new SoundboardWindow(terminal.getTerminalSize(),
					soundboard);
			timeWindow = new TimeWindow(terminal.getTerminalSize());

			// Enable resizing of the inner windows
			resizeListener = new JhinTerminalResizeListener(
					gui.getScreen().getTerminalSize());
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

	public void setTheme(String theme) {
		this.theme = theme;
	}
}
