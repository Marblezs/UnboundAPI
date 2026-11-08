package Unbound.unboundAPI.cooldown;

import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CooldownManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public void set(Player player, String key, Duration duration) {
        set(player.getUniqueId(), key, duration);
    }

    public void set(UUID playerId, String key, Duration duration) {
        long expiresAt = System.currentTimeMillis() + Math.max(0L, duration.toMillis());
        cooldowns.computeIfAbsent(playerId, ignored -> new ConcurrentHashMap<>()).put(key, expiresAt);
    }

    public boolean has(Player player, String key) {
        return has(player.getUniqueId(), key);
    }

    public boolean has(UUID playerId, String key) {
        return remainingMillis(playerId, key) > 0L;
    }

    public long remainingMillis(Player player, String key) {
        return remainingMillis(player.getUniqueId(), key);
    }

    public long remainingMillis(UUID playerId, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return 0L;
        }

        Long expiresAt = playerCooldowns.get(key);
        if (expiresAt == null) {
            return 0L;
        }

        long remaining = expiresAt - System.currentTimeMillis();
        if (remaining <= 0L) {
            playerCooldowns.remove(key);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(playerId);
            }
            return 0L;
        }
        return remaining;
    }

    public long remainingSeconds(Player player, String key) {
        long millis = remainingMillis(player, key);
        return (millis + 999L) / 1000L;
    }

    public void remove(Player player, String key) {
        remove(player.getUniqueId(), key);
    }

    public void remove(UUID playerId, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return;
        }
        playerCooldowns.remove(key);
        if (playerCooldowns.isEmpty()) {
            cooldowns.remove(playerId);
        }
    }

    public void clear(Player player) {
        clear(player.getUniqueId());
    }

    public void clear(UUID playerId) {
        cooldowns.remove(playerId);
    }

    public void clearAll() {
        cooldowns.clear();
    }
}
