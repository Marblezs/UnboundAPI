package Unbound.unboundAPI.command;

import Unbound.unboundAPI.UnboundAPI;
import Unbound.unboundAPI.chat.ChatManager;
import Unbound.unboundAPI.config.ConfigFile;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CommandManager implements CommandExecutor, TabCompleter {

    private static final List<String> TESTS = List.of(
            "chat", "menu", "animation", "item", "task", "cooldown", "config", "player", "sound"
    );

    private final UnboundAPI api;

    public CommandManager(UnboundAPI api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    public boolean registerMainCommand() {
        PluginCommand command = api.getCommand("unboundapi");
        if (command == null) {
            api.getLogger().severe("Command 'unboundapi' is missing from plugin.yml.");
            return false;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
        return true;
    }

    public boolean register(JavaPlugin owner, String commandName, CommandExecutor executor) {
        return register(owner, commandName, executor, null);
    }

    public boolean register(JavaPlugin owner, String commandName, CommandExecutor executor,
                            TabCompleter tabCompleter) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(commandName, "commandName");
        Objects.requireNonNull(executor, "executor");

        PluginCommand command = owner.getCommand(commandName);
        if (command == null) {
            owner.getLogger().warning("Command '" + commandName + "' is not declared in plugin.yml.");
            return false;
        }

        command.setExecutor(executor);
        if (tabCompleter != null) {
            command.setTabCompleter(tabCompleter);
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            api.chat().send(sender, "&cYou don't have permission to use this command.");
            return true;
        }

        String subCommand = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);

        if (!api.getConfig().getBoolean("commands.enabled", true) && !subCommand.equals("reload")) {
            api.chat().send(sender, "&cUnboundAPI commands are disabled in config.yml.");
            return true;
        }

        switch (subCommand) {
            case "help" -> sendHelp(sender);
            case "reload" -> {
                api.reloadSettings();
                api.chat().send(sender, "&#7CFF6BUnboundAPI configuration reloaded.");
            }
            case "tests" -> sendTests(sender);
            case "test" -> runTest(sender, args);
            default -> sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!hasPermission(sender)) {
            return List.of();
        }
        if (args.length == 1) {
            return filter(List.of("help", "tests", "test", "reload"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("test")
                && api.getConfig().getBoolean("commands.tests.enabled", true)) {
            return filter(TESTS.stream().filter(this::isTestEnabled).toList(), args[1]);
        }
        return List.of();
    }

    private void runTest(CommandSender sender, String[] args) {
        if (!api.getConfig().getBoolean("commands.tests.enabled", true)) {
            api.chat().send(sender, "&cAPI tests are disabled in config.yml.");
            return;
        }
        if (args.length < 2) {
            api.chat().send(sender, "&eUsage: /unboundapi test <module>");
            sendTests(sender);
            return;
        }

        String module = args[1].toLowerCase(Locale.ROOT);
        if (!TESTS.contains(module)) {
            api.chat().send(sender, "&cUnknown test. Use /unboundapi tests.");
            return;
        }
        if (!isTestEnabled(module)) {
            api.chat().send(sender, "&cThe '" + module + "' test is disabled in config.yml.");
            return;
        }
        if (!(sender instanceof Player player)) {
            api.chat().send(sender, "&cThis test must be executed in-game.");
            return;
        }

        switch (module) {
            case "chat" -> testChat(player);
            case "menu" -> testMenu(player);
            case "animation" -> testAnimation(player);
            case "item" -> testItem(player);
            case "task" -> testTask(player);
            case "cooldown" -> testCooldown(player);
            case "config" -> testConfig(player);
            case "player" -> testPlayer(player);
            case "sound" -> testSound(player);
            default -> api.chat().send(player, "&cThis test is not implemented.");
        }
    }

    private void testChat(Player player) {
        ChatManager chat = api.chat();
        chat.send(player, "&#7CFF6B&lChatUtil &8» &fRGB: &#58A6FFBlue &7| &aLegacy green");
        chat.actionBar(player, "&#7CFF6BActionBar &8- &fChat test");
        chat.title(player, "&#7CFF6BChatUtil", "&fTitle and subtitle test");
        chat.broadcast("&#7CFF6B&lUnboundAPI", "&fBroadcast with a custom prefix.");
        chat.broadcast("&fBroadcast without a prefix.");
        chat.send(player, chat.gradient("Gradient test", "#7CFF6B", "#58A6FF"));
    }

    private void testMenu(Player player) {
        ItemStack filler = api.items().of(Material.GRAY_STAINED_GLASS_PANE)
                .name("&r ").build();
        ItemStack testItem = api.items().of(Material.CHEST)
                .name("&#7CFF6B&lMenu API")
                .lore("&7Click to test the callback.", "&8UnboundAPI")
                .glow()
                .build();

        api.menus().create("&#7CFF6B&lUnboundAPI Menu", 3)
                .fill(filler)
                .item(13, testItem, click -> {
                    api.chat().send(click.player(), "&#7CFF6BMenu callback works!");
                    click.close();
                })
                .open(player);
    }

    private void testAnimation(Player player) {
        api.animations().create()
                .actionBar(20L, "&#7CFF6BAnimation test.")
                .sound(1L, Sound.UI_BUTTON_CLICK, 0.8F, 1.2F)
                .actionBar(20L, "&#58A6FFAnimation test..")
                .title(30L, "&#7CFF6BUnboundAPI", "&fAnimation completed")
                .repeat(2)
                .onComplete(context -> api.chat().send(context.player(), "&#7CFF6BAnimation finished."))
                .play(player);
        api.chat().send(player, "&7Animation started.");
    }

    private void testItem(Player player) {
        ItemStack item = api.items().of(Material.DIAMOND)
                .amount(1)
                .name("&#58A6FF&lUnboundAPI Test Item")
                .lore("&7Created with ItemBuilder.", "&7Glow, lore and RGB enabled.")
                .glow()
                .build();
        player.getInventory().addItem(item).values().forEach(remaining ->
                player.getWorld().dropItemNaturally(player.getLocation(), remaining));
        api.chat().send(player, "&#7CFF6BItemBuilder test completed.");
    }

    private void testTask(Player player) {
        api.chat().send(player, "&7A task has been scheduled for 2 seconds.");
        api.tasks().later(40L, () -> {
            if (player.isOnline()) {
                api.chat().send(player, "&#7CFF6BTaskManager works!");
            }
        });
    }

    private void testCooldown(Player player) {
        String key = "unboundapi-test";
        if (api.cooldowns().has(player, key)) {
            api.chat().send(player, "&cCooldown active: &f" + api.cooldowns().remainingSeconds(player, key) + "s");
            return;
        }
        api.cooldowns().set(player, key, java.time.Duration.ofSeconds(5));
        api.chat().send(player, "&#7CFF6BCooldown set for 5 seconds. Run the test again to check it.");
    }

    private void testConfig(Player player) {
        ConfigFile config = api.configs().create(api, "api-test.yml");
        config.get().set("tests.last-run", System.currentTimeMillis());
        config.get().set("tests.last-player", player.getName());
        config.save();
        api.chat().send(player, "&#7CFF6BConfigFile saved api-test.yml successfully.");
    }

    private void testPlayer(Player player) {
        Optional<Player> found = api.players().findOnline(player.getName());
        int ping = api.players().ping(player);
        api.chat().send(player, "&#7CFF6BPlayerUtil &8» &fFound: " + found.isPresent() + " &7| Ping: &f" + ping + "ms");
    }

    private void testSound(Player player) {
        api.sounds().play(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        api.chat().send(player, "&#7CFF6BSoundUtil test played.");
    }

    private void sendHelp(CommandSender sender) {
        api.chat().send(sender, "&#7CFF6B&lUnboundAPI &8- &7Commands");
        api.chat().send(sender, "&f/unboundapi tests &8- &7List enabled tests");
        api.chat().send(sender, "&f/unboundapi test <module> &8- &7Run a module test");
        api.chat().send(sender, "&f/unboundapi reload &8- &7Reload config.yml");
    }

    private void sendTests(CommandSender sender) {
        if (!api.getConfig().getBoolean("commands.tests.enabled", true)) {
            api.chat().send(sender, "&cAPI tests are disabled.");
            return;
        }
        String enabled = TESTS.stream()
                .filter(this::isTestEnabled)
                .collect(Collectors.joining("&7, &f"));
        api.chat().send(sender, "&#7CFF6BEnabled tests: &f" + (enabled.isBlank() ? "none" : enabled));
        for (String test : TESTS) {
            api.chat().send(sender, (isTestEnabled(test) ? "&a✓ " : "&c✗ ") + "&f" + test);
        }
    }

    private boolean isTestEnabled(String module) {
        return api.getConfig().getBoolean("commands.tests." + module, true);
    }

    private boolean hasPermission(CommandSender sender) {
        String permission = api.getConfig().getString("commands.permission", "unboundapi.admin");
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

    private List<String> filter(List<String> values, String input) {
        String prefix = input == null ? "" : input.toLowerCase(Locale.ROOT);
        return values.stream().filter(value -> value.startsWith(prefix)).toList();
    }
}
