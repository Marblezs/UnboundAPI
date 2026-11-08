package Unbound.unboundAPI;

import org.bukkit.plugin.java.JavaPlugin;

public final class UnboundAPI extends JavaPlugin {

    private static UnboundAPI instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("UnboundAPI enabled. Shared services are ready.");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static UnboundAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UnboundAPI is not enabled or has not loaded yet.");
        }
        return instance;
    }

    public static boolean isAvailable() {
        return instance != null && instance.isEnabled();
    }
}
