package Unbound.unboundAPI.util;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class PlayerManager {

    public Optional<Player> findOnline(String name) {
        return PlayerUtil.findOnline(name);
    }

    public Optional<Player> findOnline(UUID uniqueId) {
        return PlayerUtil.findOnline(uniqueId);
    }

    public int ping(Player player) {
        return PlayerUtil.ping(player);
    }
}
