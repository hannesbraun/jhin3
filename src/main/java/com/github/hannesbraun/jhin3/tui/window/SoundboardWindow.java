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

import com.github.hannesbraun.jhin3.soundboard.ProcessingState.State;
import com.github.hannesbraun.jhin3.soundboard.Sound;
import com.github.hannesbraun.jhin3.soundboard.Soundboard;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class SoundboardWindow extends AbstractJhinWindow
{
	private Soundboard soundboard;

	private Table<String> table;

	private List<Character> keys;

	private static String fadeinString = String.valueOf(Symbols.TRIANGLE_UP_POINTING_BLACK);

	private static String playingString = String.valueOf(Symbols.TRIANGLE_RIGHT_POINTING_BLACK);

	private static String fadeoutString = String.valueOf(Symbols.TRIANGLE_DOWN_POINTING_BLACK);

	public SoundboardWindow(TerminalSize terminalSize, Soundboard soundboard, MutableBoolean guiRunning)
	{
		super("Soundboard", terminalSize);
		this.soundboard = soundboard;
		this.keys = soundboard.getKeyList();

		table = new Table<>("Key", "State", "Sound", "Progress");
		table.setEnabled(false);

		Iterator<Character> keyIterator = keys.iterator();
		while (keyIterator.hasNext()) {
			char key = keyIterator.next();
			Sound sound = soundboard.getSound(key);
			List<String> row = new ArrayList<String>();

			// Key
			row.add(String.valueOf(key));

			// State
			row.add(stateToString(sound.getState()));

			// Description
			row.add(sound.getDescription());

			// Progress (nothing for now)
			row.add("");

			table.getTableModel().addRow(row);
		}

		setComponent(table);

		if (guiRunning != null) {
			this.guiRunning = guiRunning;
		} else {
			this.guiRunning = new MutableBoolean(false);
		}

		new Thread(() -> mainUpdateLoop()).start();
	}

	@Override
	public void updatePosition(TerminalSize newSize)
	{
		setPosition(TerminalPosition.TOP_LEFT_CORNER);
	}

	@Override
	public void updateSize(TerminalSize newSize)
	{
		int rows = newSize.getRows() - WindowLayoutHelper.getTimeHeight() - 4;
		setSize(new TerminalSize(newSize.getColumns() - 2, rows));
	}

	@Override
	public boolean handleInput(KeyStroke key)
	{
		if (!key.isAltDown() && !key.isCtrlDown() && !key.isShiftDown() && key.getCharacter() != null) {
			if (soundboard.toggle(key.getCharacter())) {
				// Key existing and toggled
				return true;
			} else {
				// Key not found in soundboard
				return super.handleInput(key);
			}
		} else {
			// Not a soundboard input
			return super.handleInput(key);
		}
	}

	private void mainUpdateLoop()
	{
		State[] currentStates = new State[keys.size()];
		State[] oldStates = new State[keys.size()];
		Arrays.fill(currentStates, State.STOPPED);
		Arrays.fill(oldStates, State.STOPPED);

		int index;
		Iterator<Character> keyIterator;

		do {
			index = 0;
			keyIterator = keys.iterator();

			while (keyIterator.hasNext()) {
				char key = keyIterator.next();
				Sound sound = soundboard.getSound(key);

				// State
				currentStates[index] = sound.getState();
				if (currentStates[index] != oldStates[index]) {
					table.getTableModel().setCell(1, index, stateToString(currentStates[index]));
				}

				// Progress
				if (currentStates[index] != State.STOPPED) {
					table.getTableModel().setCell(3, index, String.format("%.2f %%", sound.getProgress() * 100));
				} else if (oldStates[index] != State.STOPPED) {
					// Now stopped
					table.getTableModel().setCell(3, index, "");
				}

				oldStates[index] = currentStates[index];
				index++;
			}

			try {
				Thread.sleep(96);
			} catch (InterruptedException e) {
				// "This is fine"
				e.printStackTrace();
			}

		} while (guiRunning.isTrue());

		// Close window (gui is not running anymore)
		close();
	}

	private String stateToString(State state)
	{
		switch (state) {
		case FADEIN:
			return fadeinString;
		case FADEOUT:
			return fadeoutString;
		case PLAYING:
			return playingString;
		case STOPPED:
		default:
			return "";
		}
	}
}
