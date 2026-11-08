package Unbound.unboundAPI.menu;

import Unbound.unboundAPI.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class MenuBuilder {

    private final MenuManager manager;
    private String title;
    private final int rows;
    private final Map<Integer, MenuButton> buttons = new HashMap<>();
    private ItemStack filler;
    private ItemStack border;
    private boolean cancelClicks = true;
    private boolean allowPlayerInventory;
    private Consumer<Menu> onOpen;
    private Consumer<Menu> onClose;

    MenuBuilder(MenuManager manager, String title, int rows) {
        this.manager = manager;
        this.title = title;
        this.rows = Math.max(1, Math.min(6, rows));
    }

    public MenuBuilder title(String title) {
        this.title = title;
        return this;
    }

    public MenuBuilder item(int slot, ItemStack item) {
        return item(slot, item, null);
    }

    public MenuBuilder item(int slot, ItemBuilder item) {
        return item(slot, item.build(), null);
    }

    public MenuBuilder item(int slot, ItemStack item, Consumer<MenuClick> action) {
        checkSlot(slot);
        buttons.put(slot, new MenuButton(item, action));
        return this;
    }

    public MenuBuilder item(int slot, ItemBuilder item, Consumer<MenuClick> action) {
        return item(slot, item.build(), action);
    }

    public MenuBuilder fill(ItemStack item) {
        this.filler = item == null ? null : item.clone();
        return this;
    }

    public MenuBuilder fill(Material material) {
        return fill(new ItemStack(material));
    }

    public MenuBuilder border(ItemStack item) {
        this.border = item == null ? null : item.clone();
        return this;
    }

    public MenuBuilder border(Material material) {
        return border(new ItemStack(material));
    }

    public MenuBuilder cancelClicks(boolean cancelClicks) {
        this.cancelClicks = cancelClicks;
        return this;
    }

    public MenuBuilder allowPlayerInventory(boolean allow) {
        this.allowPlayerInventory = allow;
        return this;
    }

    public MenuBuilder onOpen(Consumer<Menu> action) {
        this.onOpen = action;
        return this;
    }

    public MenuBuilder onClose(Consumer<Menu> action) {
        this.onClose = action;
        return this;
    }

    public Menu build() {
        return manager.build(this);
    }

    public Menu open(org.bukkit.entity.Player player) {
        Menu menu = build();
        menu.open(player);
        return menu;
    }

    int size() {
        return rows * 9;
    }

    String menuTitle() {
        return title;
    }

    Map<Integer, MenuButton> menuButtons() {
        return new HashMap<>(buttons);
    }

    ItemStack fillerItem() {
        return filler == null ? null : filler.clone();
    }

    ItemStack borderItem() {
        return border == null ? null : border.clone();
    }

    boolean shouldCancelClicks() {
        return cancelClicks;
    }

    boolean canUsePlayerInventory() {
        return allowPlayerInventory;
    }

    Consumer<Menu> openAction() {
        return onOpen;
    }

    Consumer<Menu> closeAction() {
        return onClose;
    }

    private void checkSlot(int slot) {
        if (slot < 0 || slot >= size()) {
            throw new IllegalArgumentException("Slot " + slot + " is outside this menu (0-" + (size() - 1) + ").");
        }
    }
}
