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
package jhin3.soundboard;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableDouble;

import jhin3.soundboard.ProcessingState.State;

public class Sound {

	private SoundType type;

	private File file;

	private String description;

	private boolean terminate;

	private long fadein;

	private long fadeout;

	private double volume;

	private double pan;

	private MutableDouble progress;

	private ProcessingState state;

	private MutableBoolean kill;

	private boolean loaded;

	private byte[] audioData;

	private AudioFormat audioFormat;

	private static float bufferLength = 200.0f;

	public static void setBufferLength(float ms) {
		if (ms > 0.0f) {
			// Negative value not possible
			bufferLength = ms;
		}
	}

	public Sound() {
		this.description = "";
		this.terminate = true;
		this.type = SoundType.NORMAL;

		this.progress = new MutableDouble(0.0);
		this.state = new ProcessingState(State.STOPPED);
		this.kill = new MutableBoolean(false);

		loaded = false;
	}

	public String getDescription() {
		return description;
	}

	public double getProgress() {
		return progress.getValue();
	}

	public State getState() {
		return state.getState();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	public void setFadein(long fadein) {
		if (!loaded) {
			this.fadein = fadein;
		}
	}

	public void setFadeout(long fadeout) {
		if (!loaded) {
			this.fadeout = fadeout;
		}
	}

	public void setVolume(double volume) {
		if (!loaded) {
			this.volume = volume;
		}
	}

	public void setPan(double pan) {
		if (!loaded) {
			this.pan = pan;
		}
	}

	public void setFile(File file) {
		if (!loaded) {
			this.file = file;
		}
	}

	public void setType(SoundType type) {
		if (!loaded) {
			this.type = type;
		}
	}

	/**
	 * Must be called before first use.
	 * 
	 * After the sound is loaded, modification of some parameters is not
	 * possible anymore to avoid unwanted behavior while a sound is playing.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws UnsupportedAudioFileException
	 *             if the file does not point to valid audio data recognized by
	 *             the system
	 */
	public void load() throws UnsupportedAudioFileException, IOException {
		if (file != null) {

			validateConfig();

			// Buffer audio data
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			audioFormat = stream.getFormat();
			audioData = stream.readAllBytes();

			loaded = true;
		}
	}

	/**
	 * Adjusts fadein, fadeout, volume and pan in case of invalid values.
	 */
	private void validateConfig() {
		if (fadein < 0) {
			fadein = 0;
		}

		if (fadeout < 0) {
			fadeout = 0;
		}

		if (volume < -79.99) {
			volume = -79.99;
		}

		if (pan > 1.0) {
			pan = 1.0;
		} else if (pan < -1.0) {
			pan = -1.0;
		}
	}

	public void toggle() {
		if (loaded) {
			if (type == SoundType.ONE_SHOT || state.isStopped()
					|| state.isFadingOut()) {
				// Start
				start();
			} else {
				// Stop
				stop();
			}
		}
	}

	private void start() {
		if (type == SoundType.ONE_SHOT && terminate) {
			kill();
		}

		progress = new MutableDouble(0.0);

		if (fadein > 0) {
			state = new ProcessingState(State.FADEIN);
		} else {
			state = new ProcessingState(State.PLAYING);
		}

		Thread processingThread = new Thread(
				() -> process(state, kill, progress));
		processingThread.setPriority(Thread.MAX_PRIORITY);
		processingThread.start();
	}

	private void stop() {
		if (fadeout > 0) {
			state.setState(State.FADEOUT);
		} else {
			state.setState(State.STOPPED);
		}
	}

	public void kill() {
		kill.setTrue();
		state.setState(State.STOPPED);
		kill = new MutableBoolean(false);
	}

	private void process(ProcessingState state, MutableBoolean kill,
			MutableDouble progress) {
		try {
			SourceDataLine sourceLine = AudioSystem
					.getSourceDataLine(audioFormat);
			FadeCalculator fadeinCalculator = new FadeCalculator(fadein,
					volume);
			FadeCalculator fadeoutCalculator = null;

			// Buffer size
			int bufferSize = (int) (bufferLength
					/ (1000.0f / audioFormat.getSampleRate()));
			bufferSize = matchFrameSize(bufferSize);
			int fadeBufferSize = bufferSize / 2;
			fadeBufferSize = matchFrameSize(fadeBufferSize);
			int offset = 0;
			int len = bufferSize;

			sourceLine.open(audioFormat, bufferSize);
			sourceLine.start();

			// Set pan
			FloatControl panControl = (FloatControl) sourceLine
					.getControl(FloatControl.Type.PAN);
			panControl.setValue((float) pan);

			// Set gain
			FloatControl gainControl = (FloatControl) sourceLine
					.getControl(FloatControl.Type.MASTER_GAIN);
			if (state.isPlaying()) {
				gainControl.setValue((float) volume);
			} else if (state.isFadingIn()) {
				// Start fadein calculator
				fadeinCalculator.start();
			}

			while ((len > 0 || type == SoundType.LOOP) && !state.isStopped()
					&& kill.isFalse()) {
				len = bufferSize;

				if (state.isFadingIn()) {
					// Fade in
					gainControl.setValue(fadeinCalculator.fadeinVolume());
					len = fadeBufferSize;

					if (fadeinCalculator.isFinished()) {
						state.setState(State.PLAYING);
					}

				} else if (state.isFadingOut()) {
					// Fade out
					if (fadeoutCalculator == null) {
						// Setup fadeout calculator
						fadeoutCalculator = new FadeCalculator(fadeout,
								gainControl.getValue());
						fadeoutCalculator.start();
					}

					gainControl.setValue(fadeoutCalculator.fadeoutVolume());

					if (fadeoutCalculator.isFinished()) {
						// Fadeout finished
						state.setState(State.STOPPED);
					} else {
						len = fadeBufferSize;
					}
				}

				// Limit
				if (len > audioData.length - offset) {
					len = audioData.length - offset;
					len = matchFrameSize(len);
				}

				if (len > 0) {
					sourceLine.write(audioData, offset, len);
					offset += len;

					progress.setValue(
							(double) offset / (double) audioData.length);
				} else {
					if (type == SoundType.LOOP) {
						// Loop: play again
						offset = 0;
					}
				}
			}

			if (!state.isStopped() && kill.isFalse()) {
				// Finished naturally (without user interaction)
				// Wait until everything is played
				sourceLine.drain();
				state.setState(State.STOPPED);
			}

			sourceLine.stop();
			sourceLine.close();
		} catch (LineUnavailableException e) {
			state.setState(State.STOPPED);
			e.printStackTrace();
		}
	}

	private int matchFrameSize(int value) {
		return value - (value % audioFormat.getFrameSize());
	}
}
