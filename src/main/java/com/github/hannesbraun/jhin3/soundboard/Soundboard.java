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
package com.github.hannesbraun.jhin3.soundboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Soundboard
{
	private Map<Character, Sound> sounds;

	public Soundboard(String file)
	{
		SoundboardConfigHelper configHelper = new SoundboardConfigHelper(file);
		sounds = configHelper.getSounds();
	}

	public boolean toggle(char key)
	{
		Sound sound = sounds.get(key);
		if (sound != null) {
			sound.toggle();
			return true;
		} else {
			// Key not found
			return false;
		}
	}

	public void kill()
	{
		for (Map.Entry<Character, Sound> entry : sounds.entrySet()) {
			entry.getValue().kill();
		}
	}

	public List<Character> getKeyList()
	{
		List<Character> keyList = new ArrayList<Character>();
		for (Map.Entry<Character, Sound> entry : sounds.entrySet()) {
			keyList.add(entry.getKey());
		}

		// Alphabetically ordered list
		keyList.sort((Character c1, Character c2) -> c1.compareTo(c2));

		return keyList;
	}

	public Sound getSound(char key)
	{
		return sounds.get(key);
	}
}
