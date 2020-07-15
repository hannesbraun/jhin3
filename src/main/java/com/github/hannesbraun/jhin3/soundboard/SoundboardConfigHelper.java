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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.json.JSONObject;

public class SoundboardConfigHelper
{
	private JSONObject json;

	public SoundboardConfigHelper(String path)
	{
		String jsonString = "{\"sounds\":{}}";

		if (path != null) {
			try {
				jsonString = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
			} catch (IOException e) {
				// Use the "empty" JSON string -> no sounds
				e.printStackTrace();
			}
		}

		json = new JSONObject(jsonString);
	}

	public Map<Character, Sound> getSounds()
	{
		Map<Character, Sound> sounds = Collections.synchronizedMap(new HashMap<Character, Sound>());

		JSONObject jsonSounds = json.getJSONObject("sounds");
		Iterator<String> keys = jsonSounds.keys();
		String key;
		JSONObject jsonSound;

		while (keys.hasNext()) {
			key = keys.next();
			if (key.length() != 1) {
				// Key not supported
				continue;
			}

			jsonSound = jsonSounds.getJSONObject(key);
			try {
				if (jsonSound.getBoolean("active")) {
					Sound sound = new Sound();
					sound.setDescription(jsonSound.getString("description"));
					sound.setTerminate(jsonSound.getBoolean("terminate"));
					sound.setFadein((long) (1000.0 * jsonSound.getDouble("fadein")));
					sound.setFadeout((long) (1000.0 * jsonSound.getDouble("fadeout")));
					sound.setVolume(jsonSound.getDouble("volume"));
					sound.setPan(jsonSound.getDouble("pan"));
					sound.setFile(new File(json.getString("resource_directory"), jsonSound.getString("filename")));

					String typeString = jsonSound.getString("type");
					if (typeString.equals("loop")) {
						// Loop
						sound.setType(SoundType.LOOP);
					} else if (typeString.equals("oneshot")) {
						// One shot
						sound.setType(SoundType.ONE_SHOT);
					} else {
						// Normal
						sound.setType(SoundType.NORMAL);
					}

					sound.load();

					sounds.put(key.toCharArray()[0], sound);
				}
			} catch (UnsupportedAudioFileException | IOException e) {
				e.printStackTrace();
			}
		}

		return sounds;
	}
}
