import sound_type

class SoundConfiguration:
    def __init__(self, filename, description, active, type, terminate, fadein, fadeout, volume, balance):
        self.filename = filename
        self.description = description
        self.active = active
        self.type = sound_type.SoundType.types_to_int.get(type, 0)
        self.terminate = terminate
        self.fadein = fadein
        self.fadeout = fadeout
        self.volume = volume
        self.balance = balance

    def get_volumes(self):
        # Calculate volume for left speaker
        left_volume = self.balance * 2
        if left_volume > 1:
            left_volume = 1
        left_volume = left_volume * self.volume

        # Calculate volume for right speaker
        right_volume = (1 - self.balance) * 2
        if right_volume > 1:
            right_volume = 1
        right_volume = right_volume * self.volume

        return left_volume, right_volume

    def to_json(self):
        json_sound_config = {}
        json_sound_config["filename"] = self.filename
        json_sound_config["description"] = self.description
        json_sound_config["active"] = self.active
        json_sound_config["type"] = sound_type.SoundType.types_to_str.get(self.type, "normal")
        json_sound_config["terminate"] = self.terminate
        json_sound_config["fadein"] = self.fadein
        json_sound_config["fadeout"] = self.fadeout
        json_sound_config["volume"] = self.volume
        json_sound_config["balance"] = self.balance
        return json_sound_config
