package jhin3.tui.window;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;

public abstract class AbstractJhinWindow extends BasicWindow
		implements
			JhinWindow {

	public AbstractJhinWindow(String name, TerminalSize terminalSize) {
		super(name);

		setHints(Arrays.asList(Window.Hint.NO_POST_RENDERING,
				Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));

		updatePosition(terminalSize);
		updateSize(terminalSize);
	}

	@Override
	public boolean handleInput(KeyStroke key) {
		if (key.isCtrlDown() && key.getCharacter() != null) {
			if (key.getCharacter().equals('\t')) {
				getTextGUI().cycleActiveWindow(false);
				return true;
			}
		}

		return super.handleInput(key);
	}

}
