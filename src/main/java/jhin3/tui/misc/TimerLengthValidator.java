package jhin3.tui.misc;

import com.googlecode.lanterna.gui2.dialogs.TextInputDialogResultValidator;

public class TimerLengthValidator implements TextInputDialogResultValidator {

	@Override
	public String validate(String content) {
		if (content.matches("^[0-9]{2}(:[0-5][0-9]){2}$")) {
			return null;
		} else if (content.matches("^[0-9]{2}(:[0-9]{2}){2}$")) {
			return "Invalid value for minutes and/or seconds";
		} else {
			return "Invalid format, must be HH:mm:ss";
		}
	}

}
