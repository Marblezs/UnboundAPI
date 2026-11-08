package Unbound.unboundAPI.menu;

import Unbound.unboundAPI.UnboundAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class MenuManager {

    private final UnboundAPI plugin;
    private final Set<Menu> menus = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public MenuManager(UnboundAPI plugin) {
        this.plugin = plugin;
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MenuListener(), plugin);
    }

    public MenuBuilder create(String title, int rows) {
        return new MenuBuilder(this, title, rows);
    }

    public Menu build(MenuBuilder builder) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Menus must be created from the server thread.");
        }
        return new Menu(this, builder);
    }

    void track(Menu menu) {
        menus.add(menu);
    }

    void handleClose(Menu menu) {
        if (!plugin.isEnabled()) {
            menus.remove(menu);
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (menu.getInventory().getViewers().isEmpty()) {
                menus.remove(menu);
            }
        });
    }

    public void closeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder(false) instanceof Menu) {
                player.closeInventory();
            }
        }
        menus.clear();
    }

    public int trackedMenus() {
        return menus.size();
    }
}
