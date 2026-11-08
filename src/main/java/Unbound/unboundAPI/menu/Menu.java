package Unbound.unboundAPI.menu;

import Unbound.unboundAPI.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public final class Menu implements InventoryHolder {

    private final MenuManager manager;
    private final String title;
    private final Inventory inventory;
    private final Map<Integer, MenuButton> buttons;
    private final boolean cancelClicks;
    private final boolean allowPlayerInventory;
    private final Consumer<Menu> onOpen;
    private final Consumer<Menu> onClose;

    Menu(MenuManager manager, MenuBuilder builder) {
        this.manager = manager;
        this.title = builder.menuTitle();
        this.buttons = builder.menuButtons();
        this.cancelClicks = builder.shouldCancelClicks();
        this.allowPlayerInventory = builder.canUsePlayerInventory();
        this.onOpen = builder.openAction();
        this.onClose = builder.closeAction();
        this.inventory = Bukkit.createInventory(this, builder.size(), ChatUtil.component(title));

        ItemStack filler = builder.fillerItem();
        if (filler != null) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                inventory.setItem(slot, filler.clone());
            }
        }

        ItemStack border = builder.borderItem();
        if (border != null) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                int row = slot / 9;
                int column = slot % 9;
                if (row == 0 || row == (inventory.getSize() / 9) - 1 || column == 0 || column == 8) {
                    inventory.setItem(slot, border.clone());
                }
            }
        }

        buttons.forEach((slot, button) -> inventory.setItem(slot, button.item()));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public String title() {
        return title;
    }

    public int size() {
        return inventory.getSize();
    }

    public void open(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Menus must be opened from the server thread.");
        }
        if (player != null && player.isOnline()) {
            player.openInventory(inventory);
        }
    }

    public void close() {
        inventory.close();
    }

    public void setItem(int slot, ItemStack item) {
        checkSlot(slot);
        inventory.setItem(slot, item == null ? null : item.clone());
    }

    public ItemStack getItem(int slot) {
        checkSlot(slot);
        ItemStack item = inventory.getItem(slot);
        return item == null ? null : item.clone();
    }

    public void refresh() {
        inventory.getViewers().forEach(viewer -> {
            if (viewer instanceof Player player) {
                player.updateInventory();
            }
        });
    }

    boolean cancelClicks() {
        return cancelClicks;
    }

    boolean allowPlayerInventory() {
        return allowPlayerInventory;
    }

    void opened(Player player) {
        manager.track(this);
        if (onOpen != null) {
            onOpen.accept(this);
        }
    }

    void closed(Player player) {
        if (onClose != null) {
            onClose.accept(this);
        }
        manager.handleClose(this);
    }

    void click(MenuClick click) {
        MenuButton button = buttons.get(click.slot());
        if (button != null && button.action() != null) {
            button.action().accept(click);
        }
    }

    private void checkSlot(int slot) {
        if (slot < 0 || slot >= inventory.getSize()) {
            throw new IllegalArgumentException("Invalid menu slot: " + slot);
        }
    }
}
