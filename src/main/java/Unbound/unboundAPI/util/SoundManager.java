package Unbound.unboundAPI.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundManager {

    public void play(Player player, Sound sound) {
        SoundUtil.play(player, sound);
    }

    public void play(Player player, Sound sound, float volume, float pitch) {
        SoundUtil.play(player, sound, volume, pitch);
    }
}
