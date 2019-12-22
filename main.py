import sys

from consolemenu import *
from consolemenu.items import *

import configuration
import soundboard_operator

if __name__ == "__main__":
    if len(sys.argv) != 3:
        # Terminate: not enough parameters
        print("Insufficient parameters given.")
        print(sys.argv[0], " <operation> [arguments]")
        print("To open a configuration file (config.json) use for example:")
        print(sys.argv[0], " open config.json")
        exit()

    current_configuration = configuration.Configuration(sys.argv[2])
    working_operator = soundboard_operator.SoundboardOperator(current_configuration.sound_configurations)

    # Creating the menu
    menu = ConsoleMenu("swan10c", current_configuration.get_soundboard_name())

    sound_menu_items = []
    for key, config in current_configuration.sound_configurations.items():
        if config.active:
            sound_menu_items.append(MenuItem("Key '" + key + "': " + config.description))

    start_operating_mode_item = FunctionItem("Start operating mode", working_operator.operate)

    # Once we're done creating them, we just add the items to the menu
    menu.append_item(start_operating_mode_item)
    for item in sound_menu_items:
        menu.append_item(item)

    # Showing the menu
    menu.show()
