package Unbound.unboundAPI.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ChatManager {

    public String color(String message) {
        return ChatUtil.color(message);
    }

    public Component component(String message) {
        return ChatUtil.component(message);
    }

    public void send(CommandSender sender, String message) {
        ChatUtil.send(sender, message);
    }

    public void send(Player player, String message) {
        ChatUtil.send(player, message);
    }

    public void actionBar(Player player, String message) {
        ChatUtil.actionBar(player, message);
    }

    public void title(Player player, String title, String subtitle) {
        ChatUtil.title(player, title, subtitle);
    }

    public void title(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        ChatUtil.title(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    public void broadcast(String pluginPrefix, String message) {
        ChatUtil.broadcast(pluginPrefix, message);
    }

    public void broadcast(String message) {
        ChatUtil.broadcast(message);
    }

    public String rgb(String hex) {
        return ChatUtil.rgb(hex);
    }

    public String rgb(String hex, String message) {
        return ChatUtil.rgb(hex, message);
    }

    public String gradient(String message, String startHex, String endHex) {
        return ChatUtil.gradient(message, startHex, endHex);
    }

    public String strip(String message) {
        return ChatUtil.strip(message);
    }
}
