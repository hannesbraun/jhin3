from pygame import mixer as pymixer
import sound_type

class ConcreteSound:

    max_channel_num = 32

    def __init__(self, sound_configuration, sound, channel_number):
        self.sound_configuration = sound_configuration
        self.channel = pymixer.Channel(channel_number)
        self.sound = sound

        self.channel_list = []
        self.channel_list.append(self.channel)

        if self.sound_configuration.type == sound_type.SoundType.types_to_int["oneshot"]:
            # One Shot: more channels needed
            current_num_channels = pymixer.get_num_channels()
            pymixer.set_num_channels(current_num_channels + self.max_channel_num - 1)
            for i in range(0, self.max_channel_num - 1):
                self.channel_list.append(pymixer.Channel(current_num_channels + i))


    def trigger(self):
        if self.sound_configuration.type == sound_type.SoundType.types_to_int["normal"]:
            self.trigger_normal_or_loop(is_loop_type=False)
        elif self.sound_configuration.type == sound_type.SoundType.types_to_int["loop"]:
            self.trigger_normal_or_loop(is_loop_type=True)
        elif self.sound_configuration.type == sound_type.SoundType.types_to_int["oneshot"]:
            self.trigger_oneshot()

    def trigger_normal_or_loop(self, is_loop_type):
        # Check for loop type
        if is_loop_type:
            loops = -1
        else:
            loops = 0

        if not self.channel.get_busy():
            # Not currently playing
            # Start from the beginning
            volumes = self.sound_configuration.get_volumes()
            self.channel.play(self.sound, loops=loops, fade_ms=int(self.sound_configuration.fadein * 1000))

            self.channel.set_volume(volumes[0], volumes[1])
        else:
            # Is currently playing
            if self.sound_configuration.fadeout != 0:
                # Fadeout playback
                self.sound.fadeout(int(self.sound_configuration.fadeout * 1000))
            else:
                # Stop playback
                self.sound.stop()

    def trigger_oneshot(self):
        volumes = self.sound_configuration.get_volumes()

        if self.sound_configuration.terminate:
            # Restart playing sound
            self.channel.play(self.sound)
            self.channel.set_volume(volumes[0], volumes[1])

        else:
            # Start another playing instance of the sound
            counter = 0
            while self.channel_list[counter].get_busy():
                counter = counter + 1

                if counter >= len(self.channel_list):
                    # No channel is free: trigger not successful
                    break
            else:
                # Free channel found: play
                self.channel_list[counter].play(self.sound)
                self.channel_list[counter].set_volume(volumes[0], volumes[1])

    def is_playing(self):
        playing = False
        for channel in self.channel_list:
            if channel.get_busy():
                playing = True
        return playing
