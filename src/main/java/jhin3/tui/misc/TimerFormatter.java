package jhin3.tui.misc;

public class TimerFormatter {
	public static String format(long milliseconds) {
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
