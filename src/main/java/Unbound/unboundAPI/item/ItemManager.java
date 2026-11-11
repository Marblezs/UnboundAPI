package Unbound.unboundAPI.item;

import org.bukkit.Material;

public final class ItemManager {

    public ItemBuilder of(Material material) {
        return ItemBuilder.of(material);
    }

    public ItemBuilder create(Material material) {
        return ItemBuilder.of(material);
    }
}
