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
package com.github.hannesbraun.jhin3.tui.window;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.Arrays;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class AbstractJhinWindow extends BasicWindow implements JhinWindow
{
	protected MutableBoolean guiRunning;

	public AbstractJhinWindow(String name, TerminalSize terminalSize)
	{
		super(name);

		setHints(Arrays.asList(Window.Hint.NO_POST_RENDERING, Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));

		updatePosition(terminalSize);
		updateSize(terminalSize);
	}

	@Override
	public boolean handleInput(KeyStroke key)
	{
		if (key.isCtrlDown() && key.getCharacter() != null) {
			if (key.getCharacter() == 'f') {
				// Switch window
				getTextGUI().cycleActiveWindow(false);
				return true;
			}
		} else if (!key.isCtrlDown() && !key.isAltDown() && !key.isShiftDown() && key.getKeyType() != null) {
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
