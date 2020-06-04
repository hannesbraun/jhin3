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
