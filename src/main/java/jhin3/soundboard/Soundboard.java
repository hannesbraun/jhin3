package jhin3.soundboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Soundboard {

	private Map<Character, Sound> sounds;

	public Soundboard(String file) {
		SoundboardConfigHelper configHelper = new SoundboardConfigHelper(file);
		sounds = configHelper.getSounds();
	}

	public boolean toggle(char key) {
		Sound sound = sounds.get(key);
		if (sound != null) {
			sound.toggle();
			return true;
		} else {
			// Key not found
			return false;
		}
	}

	public void kill() {
		for (Map.Entry<Character, Sound> entry : sounds.entrySet()) {
			entry.getValue().kill();
		}
	}

	public List<Character> getKeyList() {
		List<Character> keyList = new ArrayList<Character>();
		for (Map.Entry<Character, Sound> entry : sounds.entrySet()) {
			keyList.add(entry.getKey());
		}

		// Alphabetically ordered list
		keyList.sort((Character c1, Character c2) -> c1.compareTo(c2));

		return keyList;
	}

	public Sound getSound(char key) {
		return sounds.get(key);
	}
}
