package Unbound.unboundAPI.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public final class MenuClick {

    private final Menu menu;
    private final Player player;
    private final int slot;
    private final ClickType clickType;
    private final InventoryAction action;

    MenuClick(Menu menu, Player player, int slot, ClickType clickType, InventoryAction action) {
        this.menu = menu;
        this.player = player;
        this.slot = slot;
        this.clickType = clickType;
        this.action = action;
    }

    public Menu menu() {
        return menu;
    }

    public Player player() {
        return player;
    }

    public int slot() {
        return slot;
    }

    public ClickType clickType() {
        return clickType;
    }

    public InventoryAction action() {
        return action;
    }

    public void close() {
        player.closeInventory();
    }
}
