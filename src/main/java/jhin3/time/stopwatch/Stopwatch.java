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
