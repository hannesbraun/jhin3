package jhin3.soundboard;

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

public class SoundboardConfigHelper {

	private JSONObject json;

	public SoundboardConfigHelper(String path) {
		String jsonString = "{}";

		try {
			jsonString = Files.readString(Paths.get(path),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// Use the empty JSON string -> no sounds
			e.printStackTrace();
		}

		json = new JSONObject(jsonString);
	}

	public Map<Character, Sound> getSounds() {
		Map<Character, Sound> sounds = Collections
				.synchronizedMap(new HashMap<Character, Sound>());

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
					sounds.put(key.toCharArray()[0], new Sound(jsonSound,
							json.getString("resource_folder")));
				}
			} catch (UnsupportedAudioFileException | IOException e) {
				e.printStackTrace();
			}
		}

		return sounds;
	}
}
