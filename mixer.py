from pygame import mixer as pymixer
import concrete_sound

class Mixer:
    def __init__(self, sound_configurations, frequency=44100):
        if pymixer.get_init() is not None:
            pymixer.quit()
        pymixer.init(frequency=frequency, channels=2)
        pymixer.set_num_channels(len(sound_configurations))

        # Create concrete sounds
        self.concrete_sounds = {}
        current_channel_number = 0
        for key, config in sound_configurations.items():
            sound = pymixer.Sound(config.filename)
            channel_number = current_channel_number
            self.concrete_sounds[key] = concrete_sound.ConcreteSound(config, sound, channel_number)

            current_channel_number = current_channel_number + 1

    def trigger(self, key):
        if self.concrete_sounds.get(key, None) is not None:
            self.concrete_sounds.get(key).trigger()

    def is_key_playing(self, key):
        if self.concrete_sounds.get(key, None) is not None:
            return self.concrete_sounds.get(key).is_playing()
        else:
            return False