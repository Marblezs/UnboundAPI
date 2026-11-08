package Unbound.unboundAPI.menu;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

final class MenuButton {

    private final ItemStack item;
    private final Consumer<MenuClick> action;

    MenuButton(ItemStack item, Consumer<MenuClick> action) {
        this.item = item == null ? null : item.clone();
        this.action = action;
    }

    ItemStack item() {
        return item == null ? null : item.clone();
    }

    Consumer<MenuClick> action() {
        return action;
    }
}
