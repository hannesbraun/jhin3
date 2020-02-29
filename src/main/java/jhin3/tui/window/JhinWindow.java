package jhin3.tui.window;

import com.googlecode.lanterna.TerminalSize;

public interface JhinWindow {
	public void updatePosition(TerminalSize newSize);

	public void updateSize(TerminalSize newSize);
}
