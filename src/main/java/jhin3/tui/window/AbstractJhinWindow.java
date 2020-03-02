package jhin3.tui.window;

import java.util.Arrays;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public abstract class AbstractJhinWindow extends BasicWindow
		implements
			JhinWindow {

	protected MutableBoolean guiRunning;

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
			if (key.getCharacter() == '\t') {
				// Switch window
				getTextGUI().cycleActiveWindow(false);
				return true;
			}
		} else if (!key.isCtrlDown() && !key.isAltDown() && !key.isShiftDown()
				&& key.getKeyType() != null) {
			if (key.getKeyType() == KeyType.Escape) {
				// Terminate application
				if (guiRunning != null) {
					guiRunning.setFalse();
				}
				return true;
			}

		}

		return super.handleInput(key);
	}

}
