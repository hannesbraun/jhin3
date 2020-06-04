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
