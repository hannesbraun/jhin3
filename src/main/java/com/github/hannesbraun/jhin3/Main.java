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
package com.github.hannesbraun.jhin3;

import com.github.hannesbraun.jhin3.soundboard.Sound;
import com.github.hannesbraun.jhin3.tui.MainTUI;
import com.github.hannesbraun.jhin3.tui.misc.ThemeLoader;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main
{
	public static void main(String[] args)
	{
		printHeader();

		Options options = new Options();
		options.addOption("c", "config", true, "the Jhin3 config file");
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

			if (cmd.hasOption('c')) {
				error |= errorCheckConfigFile(cmd.getOptionValue('c'));
			}

			if (!error) {
				tui.exec(cmd.getOptionValue('c'));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void printHeader()
	{
		System.out.println("________________________________________________________________________________");
		System.out.println();
		System.out.println("                               _ _     _       _____\n"
						   + "                              | | |__ (_)_ __ |___ /\n"
						   + "                           _  | | '_ \\| | '_ \\  |_ \\\n"
						   + "                          | |_| | | | | | | | |___) |\n"
						   + "                           \\___/|_| |_|_|_| |_|____/");
		System.out.println();
		System.out.println("________________________________________________________________________________");

		System.out.println();

		System.out.println("Jhin3 version " + JhinMetadata.VERSION);
		System.out.println("Copyright 2020 Hannes Braun");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
		System.out.println("This is free software, and you are welcome to redistribute it\n"
						   + "under certain conditions.");
		System.out.println("Fore more information see the GNU General Public License version 3.");

		System.out.println();
	}

	private static boolean errorCheckConfigFile(String path)
	{
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

	private static boolean setBufferLength(String length)
	{
		boolean error = false;
		float bufferSize = Float.parseFloat(length);

		if (bufferSize <= 0.0f) {
			System.err.println("Error: the buffer size can't be zero or less.");
			error = true;
		} else if (bufferSize > 3600000.0f) {
			System.out.println("Warning: limiting the buffer size to 3600000 ms");
			Sound.setBufferLength(3600000.0f);
		} else {
			Sound.setBufferLength(bufferSize);
		}

		return error;
	}

	private static void checkThemeSupport(String themeName)
	{
		if (!ThemeLoader.isThemeSupported(themeName)) {
			System.err.println("Warning: this theme is not supported and the user interface may not\n"
							   + "  look as expected. Consider using another (supported) theme.");
		}
	}
}
