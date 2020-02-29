package jhin3.time.stopwatch;

public class Stopwatch {

	private long elapsed;

	private boolean running;

	private long startTimestamp;

	public Stopwatch() {
		elapsed = 0;
		running = false;
	}

	public void toggle() {
		if (running) {
			elapsed = elapsed + (System.currentTimeMillis() - startTimestamp);
		} else {
			startTimestamp = System.currentTimeMillis();
		}

		running = !running;
	}

	public void reset() {
		elapsed = 0;
		running = false;
	}

	public long getElapsed() {
		if (running) {
			return elapsed + (System.currentTimeMillis() - startTimestamp);
		} else {
			return elapsed;
		}
	}

	public boolean isRunning() {
		return running;
	}
}
