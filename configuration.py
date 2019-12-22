import json

import sound_configuration

class Configuration:

    def __init__(self, file_name):
        # Store filename
        self.file_name = file_name

        # Get JSON data
        with open(file_name) as config_file:
            self.json_data = json.load(config_file)

        # Create dictionary with sound configurations
        self.sound_configurations = {}
        for key, json_sound_config in self.json_data["sounds"].items():
            if json_sound_config is not None and len(key) == 1:
                self.sound_configurations[key] = sound_configuration.SoundConfiguration(json_sound_config["filename"],
                                                                                        json_sound_config["description"],
                                                                                        json_sound_config["active"],
                                                                                        json_sound_config["type"],
                                                                                        json_sound_config["terminate"],
                                                                                        json_sound_config["fadein"],
                                                                                        json_sound_config["fadeout"],
                                                                                        json_sound_config["volume"],
                                                                                        json_sound_config["balance"])

    def get_soundboard_name(self):
        return self.json_data["soundboard_name"]

    def get_resource_folder(self):
        return self.json_data["resource_folder"]

    def save_changes(self):
        # Update sound configurations in json data
        self.json_data["sounds"] = {}
        for key, sound_config in self.sound_configurations.items():
            self.json_data["sounds"][key] = sound_config.to_json()

        with open(self.file_name, "w") as config_file:
            config_file.write(json.dumps(self.json_data, indent=4))

    def add_sound(self, key, sound_configuration):
        if len(key) == 1:
            self.sound_configurations[key] = sound_configuration
            return True
        else:
            return False
