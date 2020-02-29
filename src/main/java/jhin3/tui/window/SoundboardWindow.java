package jhin3.tui.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;

import jhin3.soundboard.ProcessingState.State;
import jhin3.soundboard.Sound;
import jhin3.soundboard.Soundboard;

public class SoundboardWindow extends AbstractJhinWindow {

	private Soundboard soundboard;

	private Table<String> table;

	private List<Character> keys;

	private static String fadeinString = String
			.valueOf(Symbols.TRIANGLE_UP_POINTING_BLACK);

	private static String playingString = String
			.valueOf(Symbols.TRIANGLE_RIGHT_POINTING_BLACK);

	private static String fadeoutString = String
			.valueOf(Symbols.TRIANGLE_DOWN_POINTING_BLACK);

	public SoundboardWindow(TerminalSize terminalSize, Soundboard soundboard) {
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

		new Thread(() -> mainUpdateLoop()).start();
	}

	@Override
	public void updatePosition(TerminalSize newSize) {
		setPosition(TerminalPosition.TOP_LEFT_CORNER);
	}

	@Override
	public void updateSize(TerminalSize newSize) {
		int rows = newSize.getRows() - WindowLayoutHelper.getTimeHeight() - 4;
		setSize(new TerminalSize(newSize.getColumns() - 2, rows));
	}

	@Override
	public boolean handleInput(KeyStroke key) {
		if (!key.isAltDown() && !key.isCtrlDown() && !key.isShiftDown()
				&& key.getCharacter() != null) {
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

	private void mainUpdateLoop() {
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
					table.getTableModel().setCell(1, index,
							stateToString(currentStates[index]));
				}

				// Progress
				if (currentStates[index] != State.STOPPED) {
					table.getTableModel().setCell(3, index, String
							.format("%.2f %%", sound.getProgress() * 100));
				} else if (oldStates[index] != State.STOPPED) {
					// Now stopped
					table.getTableModel().setCell(3, index, "");
				}

				oldStates[index] = currentStates[index];
				index++;
			}

			try {
				Thread.sleep(420);
			} catch (InterruptedException e) {
				// "This is fine"
				e.printStackTrace();
			}

		} while (true);
	}

	private String stateToString(State state) {
		switch (state) {
			case FADEIN :
				return fadeinString;
			case FADEOUT :
				return fadeoutString;
			case PLAYING :
				return playingString;
			case STOPPED :
			default :
				return "";
		}
	}
}
