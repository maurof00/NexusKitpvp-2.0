package it.maurof00.xeonkitpvp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public final class ItemUtil {
    private ItemUtil() { }

    public static ItemStack parse(String spec) {
        if (spec == null || spec.trim().isEmpty()) return null;
        String[] parts = spec.split(":");
        Material material = match(parts[0]);
        if (material == null) return null;
        short data = 0;
        int amount = 1;
        if (parts.length >= 2) {
            try { data = Short.parseShort(parts[1]); } catch (NumberFormatException ignored) { }
        }
        if (parts.length >= 3) {
            try { amount = Integer.parseInt(parts[2]); } catch (NumberFormatException ignored) { }
        }
        return new ItemStack(material, Math.max(1, amount), data);
    }

    public static ItemStack named(ItemStack source, String name) {
        ItemStack item = source == null ? null : source.clone();
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.color(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    @SuppressWarnings("deprecation")
    public static Material match(String raw) {
        String name = raw.trim().toUpperCase(Locale.ENGLISH);
        Material material = Material.matchMaterial(name);
        if (material != null) return material;
        if (name.startsWith("MINECRAFT:")) name = name.substring("MINECRAFT:".length());
        return Material.matchMaterial(name);
    }
}
