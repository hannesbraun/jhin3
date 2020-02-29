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
import org.json.JSONObject;

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

	private final byte[] audioData;

	private AudioFormat audioFormat;

	private static float bufferLength = 200.0f;

	public static void setBufferLength(float ms) {
		bufferLength = ms;
	}

	public Sound(JSONObject json, String resourcePath)
			throws UnsupportedAudioFileException, IOException {
		this.description = json.getString("description");
		this.terminate = json.getBoolean("terminate");
		this.fadein = (long) (1000.0 * json.getDouble("fadein"));
		this.fadeout = (long) (1000.0 * json.getDouble("fadeout"));
		this.volume = json.getDouble("volume");
		this.pan = json.getDouble("pan");
		file = new File(resourcePath, json.getString("filename"));

		String typeString = json.getString("type");
		if (typeString.equals("loop")) {
			// Loop
			this.type = SoundType.LOOP;
		} else if (typeString.equals("oneshot")) {
			// One shot
			this.type = SoundType.ONE_SHOT;
		} else {
			// Normal
			this.type = SoundType.NORMAL;
		}

		validateConfig();

		this.progress = new MutableDouble(0.0);
		this.state = new ProcessingState(State.STOPPED);
		this.kill = new MutableBoolean(false);

		// Buffer audio data
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		audioFormat = stream.getFormat();
		audioData = stream.readAllBytes();
	}

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

	public String getDescription() {
		return description;
	}

	public double getProgress() {
		return progress.getValue();
	}

	public State getState() {
		return state.getState();
	}

	public void toggle() {
		if (type == SoundType.ONE_SHOT || state.isStopped()
				|| state.isFadingOut()) {
			// Start
			start();
		} else {
			// Stop
			stop();
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
