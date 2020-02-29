package jhin3;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jhin3.soundboard.Sound;
import jhin3.tui.MainTUI;

public class Main {

	public static void main(String[] args) {

		Options options = new Options();
		options.addRequiredOption("c", "config", true, "the Jhin3 config file");
		options.addOption("b", "buffer", true, "buffer size in ms");
		options.addOption("t", "theme", true, "color theme to use");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);

			// Set buffer length for audio
			if (cmd.hasOption('b')) {
				Sound.setBufferLength(
						Float.parseFloat(cmd.getOptionValue('b')));
			}

			if (cmd.hasOption('c')) {
				MainTUI tui = new MainTUI();
				tui.exec(cmd.getOptionValue('c'));
			} else {
				// No config file: starting application impossible
				System.out.println("Please provide a Jhin3 config file.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
