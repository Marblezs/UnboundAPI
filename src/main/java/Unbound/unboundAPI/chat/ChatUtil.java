package Unbound.unboundAPI.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.regex.Pattern;

public final class ChatUtil {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final Pattern RGB_CODE = Pattern.compile("(?i)\u00a7x(?:\u00a7[0-9a-f]){6}");
    private static final Pattern LEGACY_CODE = Pattern.compile("(?i)\u00a7[0-9a-fk-or]");

    private ChatUtil() {
    }

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder(message.length() + 16);
        for (int i = 0; i < message.length(); i++) {
            char current = message.charAt(i);

            if (current == '<' && i + 8 < message.length() && message.charAt(i + 1) == '#'
                    && isHex(message, i + 2, 6) && message.charAt(i + 8) == '>') {
                appendHex(result, message.substring(i + 2, i + 8));
                i += 8;
                continue;
            }

            if ((current == '&' || current == '\u00a7') && i + 1 < message.length()) {
                char next = message.charAt(i + 1);

                if (next == '#' && i + 7 < message.length() && isHex(message, i + 2, 6)) {
                    appendHex(result, message.substring(i + 2, i + 8));
                    i += 7;
                    continue;
                }

                if ((next == 'x' || next == 'X') && hasRepeatedHex(message, i)) {
                    result.append('\u00a7').append('x');
                    for (int part = 0; part < 6; part++) {
                        result.append('\u00a7').append(message.charAt(i + 3 + part * 2));
                    }
                    i += 13;
                    continue;
                }

                if (isLegacyCode(next)) {
                    result.append('\u00a7').append(Character.toLowerCase(next));
                    i++;
                    continue;
                }
            }

            result.append(current);
        }
        return result.toString();
    }

    public static Component component(String message) {
        return LEGACY.deserialize(color(message));
    }

    public static void send(CommandSender sender, String message) {
        if (sender != null) {
            sender.sendMessage(component(message));
        }
    }

    public static void send(Player player, String message) {
        send((CommandSender) player, message);
    }

    public static void actionBar(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendActionBar(component(message));
        }
    }

    public static void title(Player player, String title, String subtitle) {
        title(player, title, subtitle, 10, 60, 10);
    }

    public static void title(Player player, String title, String subtitle,
                             int fadeIn, int stay, int fadeOut) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Title.Times times = Title.Times.times(
                Duration.ofMillis(Math.max(0, fadeIn) * 50L),
                Duration.ofMillis(Math.max(0, stay) * 50L),
                Duration.ofMillis(Math.max(0, fadeOut) * 50L)
        );
        player.showTitle(Title.title(component(title), component(subtitle), times));
    }

    public static void broadcast(String message) {
        Component formatted = component(message);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(formatted));
        Bukkit.getConsoleSender().sendMessage(formatted);
    }

    public static String strip(String message) {
        String formatted = color(message);
        return LEGACY_CODE.matcher(RGB_CODE.matcher(formatted).replaceAll(""))
                .replaceAll("");
    }

    private static boolean isLegacyCode(char value) {
        return "0123456789abcdefklmnorx".indexOf(Character.toLowerCase(value)) >= 0;
    }

    private static boolean isHex(String value, int start, int length) {
        if (start + length > value.length()) {
            return false;
        }
        for (int i = start; i < start + length; i++) {
            if (Character.digit(value.charAt(i), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasRepeatedHex(String value, int start) {
        if (start + 13 >= value.length()) {
            return false;
        }
        for (int part = 0; part < 6; part++) {
            int separator = start + 2 + part * 2;
            if (value.charAt(separator) != '&' && value.charAt(separator) != '\u00a7') {
                return false;
            }
            if (Character.digit(value.charAt(separator + 1), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    private static void appendHex(StringBuilder output, String hex) {
        output.append('\u00a7').append('x');
        for (int i = 0; i < hex.length(); i++) {
            output.append('\u00a7').append(hex.charAt(i));
        }
    }
}
