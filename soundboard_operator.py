import mixer
import sys
import curses

class SoundboardOperator:
    def __init__(self, sound_configurations):
        self.sound_configurations = sound_configurations
        self.operating_mixer = mixer.Mixer(sound_configurations)
        self.line_keys = {}
        counter = 0
        for key, config in self.sound_configurations.items():
            if config.active:
                self.line_keys[key] = counter
                counter += 1


    def curses_operate(self, stdscr):
        # Print config
        stdscr.leaveok(True)
        stdscr.clear()
        self.print_keys(stdscr)

        # Initial read
        try:
            char_read = stdscr.getkey()
            read_success = True
        except:
            print("Error while reading key")
            char_read = chr(0)
            read_success = False

        # Repeat until esc key is hit
        while char_read != chr(27):
            # Trigger mixer
            if read_success:
                self.operating_mixer.trigger(char_read)
                read_success = False

            # Update console
            # Store cursor position
            #cursor_position = stdscr.getyx()

            for key, line_number in self.line_keys.items():
                if self.operating_mixer.is_key_playing(key):
                    status_str = ">"
                else:
                    status_str = " "
                stdscr.addstr(line_number, 0, status_str)

            # Restore cursor position
            #stdscr.move(cursor_position[0], cursor_position[1])

            # Read next char
            try:
                char_read = stdscr.getkey()
                read_success = True
            except:
                print("Error while reading key")

    def operate(self):
        curses.wrapper(self.curses_operate)

    def print_keys(self, stdscr):
        stdscr.addstr("   Key  Description" + "\n")
        stdscr.addstr("________________________________________________________________________________\n")
        for key, config in self.sound_configurations.items():
            if config.active:
                self.line_keys[key] = stdscr.getyx()[0]
                stdscr.addstr("   " + key + "    " + config.description + "\n")
