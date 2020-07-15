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
package com.github.hannesbraun.jhin3.tui.misc;

public class TimerFormatter
{
	public static String format(long milliseconds)
	{
		int hours;
		int minutes;
		int seconds;
		int tmp;

		// Calculate hours, minutes and seconds
		hours = (int) (milliseconds / 3600000);
		tmp = (int) (milliseconds % 3600000);
		minutes = tmp / 60000;
		tmp = tmp % 60000;
		seconds = tmp / 1000;
		if (tmp % 1000 >= 500) {
			seconds++;
		}

		if (seconds == 60) {
			seconds = 0;
			minutes++;
			if (minutes == 60) {
				minutes = 0;
				hours++;
			}
		}

		// The following is equal to this:
		// return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		// Preferred because of better performance
		StringBuilder stringBuilder = new StringBuilder();
		if (hours < 10) {
			stringBuilder.append("0");
		}
		stringBuilder.append(hours);
		stringBuilder.append(":");
		if (minutes < 10) {
			stringBuilder.append("0");
		}
		stringBuilder.append(minutes);
		stringBuilder.append(":");
		if (seconds < 10) {
			stringBuilder.append("0");
		}
		stringBuilder.append(seconds);
		return stringBuilder.toString();
	}
}
