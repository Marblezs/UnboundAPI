package Unbound.unboundAPI.animation;

import Unbound.unboundAPI.chat.ChatUtil;
import Unbound.unboundAPI.menu.Menu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AnimationContext {

    private final Player player;
    private final Menu menu;

    AnimationContext(Player player, Menu menu) {
        this.player = player;
        this.menu = menu;
    }

    public Player player() {
        return player;
    }

    public Menu menu() {
        return menu;
    }

    public void send(String message) {
        ChatUtil.send(player, message);
    }

    public void actionBar(String message) {
        ChatUtil.actionBar(player, message);
    }

    public void title(String title, String subtitle) {
        ChatUtil.title(player, title, subtitle);
    }

    public void title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        ChatUtil.title(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    public void sound(Sound sound, float volume, float pitch) {
        if (player.isOnline()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void setItem(int slot, ItemStack item) {
        if (menu == null) {
            throw new IllegalStateException("This animation was not started with a Menu.");
        }
        menu.setItem(slot, item);
    }

    public void closeMenu() {
        player.closeInventory();
    }
}
