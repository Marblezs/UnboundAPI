package Unbound.unboundAPI.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class PlayerUtil {

    private PlayerUtil() {
    }

    public static Optional<Player> findOnline(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return (Optional<Player>) Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static Optional<Player> findOnline(UUID uniqueId) {
        return Optional.ofNullable(Bukkit.getPlayer(uniqueId));
    }

    public static int ping(Player player) {
        return player == null || !player.isOnline() ? -1 : player.getPing();
    }
}
