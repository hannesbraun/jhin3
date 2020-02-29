package jhin3.tui.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;

import jhin3.tui.window.AbstractJhinWindow;

public class JhinTerminalResizeListener extends SimpleTerminalResizeListener {

	private List<AbstractJhinWindow> windows;

	public JhinTerminalResizeListener(TerminalSize terminalSize) {
		super(terminalSize);

		this.windows = new ArrayList<AbstractJhinWindow>();
	}

	public void addWindow(AbstractJhinWindow window) {
		windows.add(window);
	}

	@Override
	public synchronized void onResized(Terminal terminal,
			TerminalSize newSize) {
		super.onResized(terminal, newSize);

		Iterator<AbstractJhinWindow> i = windows.iterator();
		AbstractJhinWindow window;

		while (i.hasNext()) {
			// Update window positions and sizes
			window = i.next();
			window.updatePosition(newSize);
			window.updateSize(newSize);
			window.invalidate();
		}
	}

}
