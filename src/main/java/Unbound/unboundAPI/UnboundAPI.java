package Unbound.unboundAPI;

import Unbound.unboundAPI.cooldown.CooldownManager;
import Unbound.unboundAPI.menu.MenuManager;
import Unbound.unboundAPI.task.TaskManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UnboundAPI extends JavaPlugin {

    private static UnboundAPI instance;

    private MenuManager menus;
    private TaskManager tasks;
    private CooldownManager cooldowns;

    @Override
    public void onEnable() {
        instance = this;
        menus = new MenuManager(this);
        tasks = new TaskManager(this);
        cooldowns = new CooldownManager();

        getLogger().info("UnboundAPI enabled. Shared services are ready.");
    }

    @Override
    public void onDisable() {
        if (menus != null) {
            menus.closeAll();
        }
        if (tasks != null) {
            tasks.cancelAll();
        }
        if (cooldowns != null) {
            cooldowns.clearAll();
        }
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

    public MenuManager menus() {
        return menus;
    }

    public TaskManager tasks() {
        return tasks;
    }

    public CooldownManager cooldowns() {
        return cooldowns;
    }
}
