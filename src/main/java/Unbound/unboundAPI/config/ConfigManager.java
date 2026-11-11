package Unbound.unboundAPI.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ConfigManager {

    public ConfigFile create(JavaPlugin owner, String fileName) {
        return new ConfigFile(Objects.requireNonNull(owner, "owner"), fileName);
    }

    public ConfigFile load(JavaPlugin owner, String fileName) {
        return create(owner, fileName);
    }
}
