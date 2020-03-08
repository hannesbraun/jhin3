package jhin3;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jhin3.soundboard.Sound;
import jhin3.tui.MainTUI;
import jhin3.tui.misc.ThemeLoader;

public class Main {

	public static void main(String[] args) {

		printHeader();

		Options options = new Options();
		options.addRequiredOption("c", "config", true, "the Jhin3 config file");
		options.addOption("b", "buffer", true, "buffer size in ms");
		options.addOption("t", "theme", true, "color theme to use");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;

		boolean error = false;

		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption('b')) {
				// Set buffer length for audio
				error |= setBufferLength(cmd.getOptionValue('b'));
			}

			MainTUI tui = new MainTUI();

			if (cmd.hasOption('t')) {
				checkThemeSupport(cmd.getOptionValue('t'));

				// Set theme
				tui.setTheme(cmd.getOptionValue('t'));
			}

			error |= errorCheckConfigFile(cmd.getOptionValue('c'));

			if (!error) {
				tui.exec(cmd.getOptionValue('c'));
			}

		} catch (MissingOptionException e) {
			// No config file: starting application impossible
			System.err.println("Please provide a Jhin3 config file.");
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static void printHeader() {
		System.out.println(
				"________________________________________________________________________________");
		System.out.println();
		System.out.println(
				"                               _ _     _       _____\n"
						+ "                              | | |__ (_)_ __ |___ /\n"
						+ "                           _  | | '_ \\| | '_ \\  |_ \\\n"
						+ "                          | |_| | | | | | | | |___) |\n"
						+ "                           \\___/|_| |_|_|_| |_|____/");
		System.out.println();
		System.out.println(
				"________________________________________________________________________________");

		System.out.println();

		System.out.println("Jhin3 version " + JhinMetadata.VERSION);
		System.out.println("Copyright 2020 Hannes Braun");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
		System.out.println(
				"This is free software, and you are welcome to redistribute it\n"
						+ "under certain conditions.");
		System.out.println(
				"Fore more information see the GNU General Public License version 3.");

		System.out.println();
	}

	private static boolean errorCheckConfigFile(String path) {
		boolean error = false;
		File configFile = new File(path);

		// File has to exist and has to be a file
		error |= !configFile.exists();
		error |= !configFile.isFile();

		if (error) {
			System.err.println("Error: no such file \"" + path + "\"");
		}

		return error;
	}

	private static boolean setBufferLength(String length) {
		boolean error = false;
		float bufferSize = Float.parseFloat(length);

		if (bufferSize <= 0.0f) {
			System.err.println("Error: the buffer size can't be zero or less.");
			error = true;
		} else if (bufferSize > 3600000.0f) {
			System.out.println("Limiting the buffer size to 3600000 ms");
			Sound.setBufferLength(3600000.0f);
		} else {
			Sound.setBufferLength(bufferSize);
		}

		return error;
	}

	private static void checkThemeSupport(String themeName) {
		if (!ThemeLoader.isThemeSupported(themeName)) {
			System.err.println(
					"Warning: this theme is not supported and the user interface may not\n"
							+ "  look as expected. Consider using another (supported) theme.");
		}
	}

}
