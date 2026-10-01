package it.maurof00.xeonkitpvp.model;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KitDefinition {
    private final String id;
    private final String displayName;
    private final ItemStack icon;
    private final long price;
    private final String permission;
    private final long cooldownSeconds;
    private final List<ItemStack> items;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final List<String> effects;
    private final Ability ability;

    public KitDefinition(String id, String displayName, ItemStack icon, long price, String permission,
                         long cooldownSeconds, List<ItemStack> items, ItemStack helmet, ItemStack chestplate,
                         ItemStack leggings, ItemStack boots, List<String> effects, Ability ability) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.price = Math.max(0L, price);
        this.permission = permission == null ? "" : permission;
        this.cooldownSeconds = Math.max(0L, cooldownSeconds);
        this.items = Collections.unmodifiableList(new ArrayList<ItemStack>(items));
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.effects = Collections.unmodifiableList(new ArrayList<String>(effects));
        this.ability = ability;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public ItemStack getIcon() { return icon; }
    public long getPrice() { return price; }
    public String getPermission() { return permission; }
    public long getCooldownSeconds() { return cooldownSeconds; }
    public List<ItemStack> getItems() { return items; }
    public ItemStack getHelmet() { return helmet; }
    public ItemStack getChestplate() { return chestplate; }
    public ItemStack getLeggings() { return leggings; }
    public ItemStack getBoots() { return boots; }
    public List<String> getEffects() { return effects; }
    public Ability getAbility() { return ability; }

    public static final class Ability {
        private final String type;
        private final ItemStack item;
        private final String displayName;
        private final long cooldownSeconds;

        public Ability(String type, ItemStack item, String displayName, long cooldownSeconds) {
            this.type = type == null ? "NONE" : type.toUpperCase();
            this.item = item;
            this.displayName = displayName;
            this.cooldownSeconds = Math.max(0L, cooldownSeconds);
        }

        public String getType() { return type; }
        public ItemStack getItem() { return item; }
        public String getDisplayName() { return displayName; }
        public long getCooldownSeconds() { return cooldownSeconds; }
    }
}
