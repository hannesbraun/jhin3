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

public class ProcessingState
{
	public enum State { FADEIN, PLAYING, FADEOUT, STOPPED }

	private State state;

	public ProcessingState(State state)
	{
		setState(state);
	}

	public boolean isFadingIn()
	{
		return state == State.FADEIN;
	}

	public boolean isPlaying()
	{
		return state == State.PLAYING;
	}

	public boolean isFadingOut()
	{
		return state == State.FADEOUT;
	}

	public boolean isStopped()
	{
		return state == State.STOPPED;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public State getState()
	{
		return state;
	}
}
