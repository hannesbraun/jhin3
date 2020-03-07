package jhin3;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jhin3.soundboard.Sound;
import jhin3.tui.MainTUI;

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
				float bufferSize = Float.parseFloat(cmd.getOptionValue('b'));
				if (bufferSize <= 0.0f) {
					System.err.println(
							"Error: the buffer size can't be zero or less.");
					error = true;
				} else if (bufferSize > 3600000.0f) {
					System.out
							.println("Limiting the buffer size to 3600000 ms");
					Sound.setBufferLength(3600000.0f);
				} else {
					Sound.setBufferLength(bufferSize);
				}
			}

			MainTUI tui = new MainTUI();

			if (cmd.hasOption('t')) {
				// Set theme
				tui.setTheme(cmd.getOptionValue('t'));
			}

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

}
