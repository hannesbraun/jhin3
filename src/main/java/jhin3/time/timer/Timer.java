package jhin3.time.timer;

import jhin3.time.stopwatch.Stopwatch;

public class Timer {

	public static final int progressMax = 1048576;

	private Stopwatch stopwatch;

	private long duration;

	public Timer(long milliseconds) {
		stopwatch = new Stopwatch();
		duration = milliseconds;
	}

	public synchronized void toggle() {
		stopIfFinished();
		if (!isFinished()) {
			stopwatch.toggle();
		}
	}

	public synchronized void reset() {
		stopwatch.reset();
	}

	private synchronized void stopIfFinished() {
		if (stopwatch.isRunning() && stopwatch.getElapsed() >= duration) {
			stopwatch.toggle();
		}
	}

	public synchronized long getElapsed() {
		stopIfFinished();
		long elapsed = stopwatch.getElapsed();

		if (elapsed > duration) {
			return duration;
		} else {
			return elapsed;
		}
	}

	public synchronized long getRemaining() {
		stopIfFinished();
		return duration - getElapsed();
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long milliseconds) {
		this.duration = milliseconds;
	}

	public synchronized int getProgress() {
		stopIfFinished();

		double progress;

		if (duration != 0.0) {
			progress = (double) getElapsed() / (double) duration;
		} else {
			progress = 0.0;
		}

		return (int) (progress * progressMax);
	}

	public synchronized boolean isFinished() {
		stopIfFinished();
		return getRemaining() == 0;
	}
}
