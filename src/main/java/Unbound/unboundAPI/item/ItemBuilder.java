package Unbound.unboundAPI.item;

import Unbound.unboundAPI.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ItemBuilder {

    private final ItemStack item;
    private String name;
    private List<String> lore = new ArrayList<>();
    private boolean glow;
    private boolean unbreakable;
    private boolean hideAttributes;
    private Integer customModelData;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final Set<ItemFlag> flags = new HashSet<>();

    private ItemBuilder(Material material) {
        this.item = new ItemStack(Objects.requireNonNull(material, "material"));
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(Math.max(1, Math.min(item.getMaxStackSize(), amount)));
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = ChatUtil.color(name);
        return this;
    }

    public ItemBuilder lore(String... lines) {
        this.lore = new ArrayList<>(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        this.lore = new ArrayList<>(lines);
        return this;
    }

    public ItemBuilder addLore(String line) {
        this.lore.add(line);
        return this;
    }

    public ItemBuilder glow() {
        this.glow = true;
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        enchantments.put(Objects.requireNonNull(enchantment, "enchantment"), Math.max(1, level));
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder hideAttributes() {
        this.hideAttributes = true;
        return this;
    }

    public ItemBuilder customModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemBuilder tag(NamespacedKey key, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() {
        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            return result;
        }

        if (name != null) {
            meta.customName(ChatUtil.component(name));
        }
        if (!lore.isEmpty()) {
            meta.lore(lore.stream().map(ChatUtil::component).toList());
        }
        enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (glow) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (!flags.isEmpty()) {
            meta.addItemFlags(flags.toArray(ItemFlag[]::new));
        }
        if (unbreakable) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        if (hideAttributes) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        if (customModelData != null) {
            meta.setCustomModelData(customModelData);
        }

        result.setItemMeta(meta);
        return result;
    }
}
