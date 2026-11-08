package Unbound.unboundAPI.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtil {

    private SoundUtil() {
    }

    public static void play(Player player, Sound sound) {
        play(player, sound, 1.0F, 1.0F);
    }

    public static void play(Player player, Sound sound, float volume, float pitch) {
        if (player != null && player.isOnline() && sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}
