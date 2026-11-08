package Unbound.unboundAPI.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

final class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(InventoryOpenEvent event) {
        if (!event.isCancelled() && event.getPlayer() instanceof Player player
                && event.getInventory().getHolder(false) instanceof Menu menu) {
            menu.opened(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder(false) instanceof Menu menu)) {
            return;
        }

        Inventory clicked = event.getClickedInventory();
        if (clicked == top && menu.cancelClicks()) {
            event.setCancelled(true);
        } else if (clicked != top && !menu.allowPlayerInventory()) {
            event.setCancelled(true);
        }

        if (clicked == top && event.getWhoClicked() instanceof Player player && event.getRawSlot() < top.getSize()) {
            menu.click(new MenuClick(menu, player, event.getRawSlot(), event.getClick(), event.getAction()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder(false) instanceof Menu menu)) {
            return;
        }

        boolean touchesTop = event.getRawSlots().stream().anyMatch(slot -> slot < top.getSize());
        boolean touchesPlayerInventory = event.getRawSlots().stream().anyMatch(slot -> slot >= top.getSize());
        if ((touchesTop && menu.cancelClicks()) || (touchesPlayerInventory && !menu.allowPlayerInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder(false) instanceof Menu menu && event.getPlayer() instanceof Player player) {
            menu.closed(player);
        }
    }
}
