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

public class FadeCalculator
{
	private long startTime;

	private long duration;

	/* Linear volume */
	private double linearVolume;

	private double dBVolume;

	private boolean running;

	public FadeCalculator(long duration, double volume)
	{
		this.duration = duration;
		this.linearVolume = dBToLinear(volume);
		this.dBVolume = volume;
		this.running = false;
	}

	public void start()
	{
		startTime = System.currentTimeMillis();
		this.running = true;
	}

	public boolean isRunning()
	{
		return running;
	}

	public float fadeoutVolume()
	{
		float calculatedVolume = (float) linearToDB((1.0 - ((double) getElapsed() / (double) duration)) * linearVolume);

		return limit(calculatedVolume);
	}

	public float fadeinVolume()
	{
		float calculatedVolume = (float) linearToDB(((double) getElapsed() / (double) duration) * linearVolume);

		return limit(calculatedVolume);
	}

	public boolean isFinished()
	{
		return getElapsed() >= duration;
	}

	private long getElapsed()
	{
		return System.currentTimeMillis() - startTime;
	}

	public double linearToDB(double linear)
	{
		if (linear == 0.0) {
			// Log with zero is not possible
			return -79.99f;
		} else {
			return Math.log10(linear) * 20.0;
		}
	}

	public double dBToLinear(double dB)
	{
		return Math.pow(10, dB / 20.0);
	}

	private float limit(float in)
	{
		if (in > (float) dBVolume) {
			// Too loud
			return (float) dBVolume;
		} else if (in <= -80.0f) {
			// Too quiet
			return -79.99f;
		} else {
			// Valid input
			return in;
		}
	}
}
