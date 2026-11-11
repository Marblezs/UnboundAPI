package Unbound.unboundAPI;

import Unbound.unboundAPI.animation.AnimationManager;
import Unbound.unboundAPI.chat.ChatManager;
import Unbound.unboundAPI.command.CommandManager;
import Unbound.unboundAPI.config.ConfigManager;
import Unbound.unboundAPI.cooldown.CooldownManager;
import Unbound.unboundAPI.item.ItemManager;
import Unbound.unboundAPI.menu.MenuManager;
import Unbound.unboundAPI.task.TaskManager;
import Unbound.unboundAPI.util.PlayerManager;
import Unbound.unboundAPI.util.SoundManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UnboundAPI extends JavaPlugin {

    private static volatile UnboundAPI instance;

    private ChatManager chat;
    private MenuManager menus;
    private AnimationManager animations;
    private ItemManager items;
    private TaskManager tasks;
    private CooldownManager cooldowns;
    private ConfigManager configs;
    private PlayerManager players;
    private SoundManager sounds;
    private CommandManager commands;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        applyConfigurationDefaults();

        chat = new ChatManager();
        menus = new MenuManager(this);
        animations = new AnimationManager(this);
        items = new ItemManager();
        tasks = new TaskManager(this);
        cooldowns = new CooldownManager();
        configs = new ConfigManager();
        players = new PlayerManager();
        sounds = new SoundManager();
        commands = new CommandManager(this);
        commands.registerMainCommand();

        getLogger().info("UnboundAPI enabled. Public API services are ready.");
    }

    @Override
    public void onDisable() {
        if (animations != null) {
            animations.cancelAll();
        }
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
        UnboundAPI current = instance;
        if (current == null || !current.isEnabled()) {
            throw new IllegalStateException("UnboundAPI is not enabled or has not loaded yet.");
        }
        return current;
    }

    public static UnboundAPI getAPI() {
        return getInstance();
    }

    public static boolean isAvailable() {
        return instance != null && instance.isEnabled();
    }

    public ChatManager chat() {
        return chat;
    }

    public ChatManager chatUtil() {
        return chat;
    }

    public MenuManager menus() {
        return menus;
    }

    public AnimationManager animations() {
        return animations;
    }

    public ItemManager items() {
        return items;
    }

    public TaskManager tasks() {
        return tasks;
    }

    public CooldownManager cooldowns() {
        return cooldowns;
    }

    public ConfigManager configs() {
        return configs;
    }

    public PlayerManager players() {
        return players;
    }

    public SoundManager sounds() {
        return sounds;
    }

    public CommandManager commands() {
        return commands;
    }

    public void reloadSettings() {
        reloadConfig();
        applyConfigurationDefaults();
    }

    private void applyConfigurationDefaults() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
