package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.model.KitDefinition;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import it.maurof00.xeonkitpvp.util.ItemUtil;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class KitService {
    private final KitPvPCore plugin;
    private final PlayerService players;
    private final PluginConfig config;
    private final MessageService messages;
    private final Map<String, KitDefinition> kits = new LinkedHashMap<String, KitDefinition>();
    private final Map<String, Long> cooldowns = new HashMap<String, Long>();

    public KitService(KitPvPCore plugin, PlayerService players, PluginConfig config, MessageService messages) {
        this.plugin = plugin; this.players = players; this.config = config; this.messages = messages;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "kits.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        kits.clear();
        ConfigurationSection section = yaml.getConfigurationSection("kits");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            String base = "kits." + id;
            ItemStack icon = ItemUtil.parse(yaml.getString(base + ".material", "CHEST"));
            String name = ColorUtil.color(yaml.getString(base + ".name", id));
            long price = yaml.getLong(base + ".price", 0L);
            String permission = yaml.getString(base + ".permission", "");
            long cooldown = yaml.getLong(base + ".cooldown", 0L);
            java.util.List<ItemStack> items = new ArrayList<ItemStack>();
            for (String raw : yaml.getStringList(base + ".items")) {
                ItemStack item = ItemUtil.parse(raw);
                if (item != null) items.add(item);
            }
            ItemStack helmet = ItemUtil.parse(yaml.getString(base + ".armor.helmet", "AIR"));
            ItemStack chest = ItemUtil.parse(yaml.getString(base + ".armor.chestplate", "AIR"));
            ItemStack legs = ItemUtil.parse(yaml.getString(base + ".armor.leggings", "AIR"));
            ItemStack boots = ItemUtil.parse(yaml.getString(base + ".armor.boots", "AIR"));
            java.util.List<String> effects = yaml.getStringList(base + ".effects");
            KitDefinition.Ability ability = null;
            if (yaml.isConfigurationSection(base + ".ability")) {
                String type = yaml.getString(base + ".ability.type", "NONE");
                ItemStack abilityItem = ItemUtil.parse(yaml.getString(base + ".ability.material", "FEATHER"));
                String abilityName = ColorUtil.color(yaml.getString(base + ".ability.name", "Ability"));
                long abilityCooldown = yaml.getLong(base + ".ability.cooldown", 10L);
                ability = new KitDefinition.Ability(type, abilityItem, abilityName, abilityCooldown);
            }
            kits.put(id.toLowerCase(Locale.ENGLISH), new KitDefinition(id.toLowerCase(Locale.ENGLISH), name, icon, price,
                    permission, cooldown, items, helmet, chest, legs, boots, effects, ability));
        }
    }

    public int size() { return kits.size(); }
    public java.util.Collection<KitDefinition> all() { return Collections.unmodifiableCollection(kits.values()); }
    public java.util.Collection<String> ids() { return Collections.unmodifiableCollection(kits.keySet()); }
    public KitDefinition get(String id) { return id == null ? null : kits.get(id.toLowerCase(Locale.ENGLISH)); }

    public boolean canUse(Player player, KitDefinition kit) {
        if (!kit.getPermission().isEmpty() && !player.hasPermission(kit.getPermission()) && !player.hasPermission("kitpvp.kit.*")) return false;
        PlayerProfile profile = players.get(player);
        return profile.getOwnedKits().contains(kit.getId());
    }

    public boolean unlock(Player player, KitDefinition kit) {
        PlayerProfile p = players.get(player);
        if (p.getOwnedKits().contains(kit.getId())) return true;
        if (!player.hasPermission("kitpvp.kit.*") && !kit.getPermission().isEmpty() && !player.hasPermission(kit.getPermission())) return false;
        if (p.getCoins() < kit.getPrice()) return false;
        p.setCoins(p.getCoins() - kit.getPrice());
        p.getOwnedKits().add(kit.getId());
        return true;
    }

    public boolean select(Player player, String id) {
        KitDefinition kit = get(id);
        if (kit == null) { messages.send(player, "kit-not-found", singleton("kit", id)); return false; }
        if (!canUse(player, kit)) {
            if (players.get(player).getCoins() >= kit.getPrice() && kit.getPrice() > 0L) {
                if (!unlock(player, kit)) { messages.send(player, "not-enough-coins"); return false; }
                messages.send(player, "kit-purchased", map("kit", kit.getId(), "price", String.valueOf(kit.getPrice())));
            } else {
                messages.send(player, "kit-locked", singleton("kit", kit.getId())); return false;
            }
        }
        String cdKey = player.getUniqueId().toString() + ":select:" + kit.getId();
        long remaining = remaining(cdKey);
        if (remaining > 0L) { messages.send(player, "kit-cooldown", singleton("seconds", String.valueOf(remaining))); return false; }
        give(player, kit);
        players.get(player).setSelectedKit(kit.getId());
        cooldowns.put(cdKey, System.currentTimeMillis() + kit.getCooldownSeconds() * 1000L);
        messages.send(player, "kit-selected", singleton("kit", kit.getDisplayName()));
        return true;
    }

    public void give(Player player, KitDefinition kit) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
        for (ItemStack item : kit.getItems()) player.getInventory().addItem(item.clone());
        if (kit.getHelmet() != null) player.getInventory().setHelmet(kit.getHelmet().clone());
        if (kit.getChestplate() != null) player.getInventory().setChestplate(kit.getChestplate().clone());
        if (kit.getLeggings() != null) player.getInventory().setLeggings(kit.getLeggings().clone());
        if (kit.getBoots() != null) player.getInventory().setBoots(kit.getBoots().clone());
        for (String raw : kit.getEffects()) applyEffect(player, raw);
        if (kit.getAbility() != null && kit.getAbility().getItem() != null) {
            player.getInventory().addItem(ItemUtil.named(kit.getAbility().getItem(), kit.getAbility().getDisplayName()));
        }
        player.updateInventory();
    }

    private void applyEffect(Player player, String raw) {
        String[] parts = raw.split(":");
        if (parts.length < 2) return;
        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase(Locale.ENGLISH));
        if (type == null) return;
        int amplifier = parseInt(parts[1], 0);
        int duration = parts.length >= 3 ? parseInt(parts[2], 200000000) : 200000000;
        player.addPotionEffect(new PotionEffect(type, duration, amplifier, true, false), true);
    }

    public boolean useAbility(Player player, KitDefinition kit) {
        KitDefinition.Ability ability = kit.getAbility();
        if (ability == null || "NONE".equals(ability.getType())) return false;
        String key = player.getUniqueId().toString() + ":ability:" + kit.getId();
        long left = remaining(key);
        if (left > 0L) { messages.send(player, "ability-cooldown", singleton("seconds", String.valueOf(left))); return false; }
        if (!executeAbility(player, ability.getType())) return false;
        cooldowns.put(key, System.currentTimeMillis() + ability.getCooldownSeconds() * 1000L);
        messages.send(player, "ability-used", singleton("ability", ability.getDisplayName()));
        return true;
    }

    private boolean executeAbility(Player player, String rawType) {
        String type = rawType.toUpperCase(Locale.ENGLISH);
        if ("HEAL".equals(type)) {
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 8.0D));
            return true;
        }
        if ("DASH".equals(type)) {
            org.bukkit.util.Vector velocity = player.getLocation().getDirection().normalize().multiply(1.65D);
            velocity.setY(Math.max(0.35D, velocity.getY() + 0.25D));
            player.setVelocity(velocity);
            return true;
        }
        if ("BLINK".equals(type)) {
            org.bukkit.Location from = player.getLocation();
            org.bukkit.util.Vector dir = from.getDirection().normalize();
            org.bukkit.Location target = from.clone();
            for (int i = 0; i < 10; i++) {
                org.bukkit.Location next = target.clone().add(dir);
                if (!isPassable(next.getBlock().getType()) || !isPassable(next.clone().add(0, 1, 0).getBlock().getType())) break;
                target = next;
            }
            player.teleport(target);
            return true;
        }
        if ("GRAPPLE".equals(type)) {
            org.bukkit.util.Vector velocity = player.getLocation().getDirection().normalize().multiply(2.0D);
            velocity.setY(Math.max(0.65D, velocity.getY() + 0.45D));
            player.setVelocity(velocity);
            return true;
        }
        if ("FIREBALL".equals(type)) {
            player.launchProjectile(org.bukkit.entity.SmallFireball.class);
            return true;
        }
        return false;
    }

    private boolean isPassable(Material material) {
        return material == Material.AIR || material == Material.LONG_GRASS || material == Material.DEAD_BUSH || material == Material.TORCH;
    }

    private long remaining(String key) {
        Long until = cooldowns.get(key);
        if (until == null) return 0L;
        long left = until - System.currentTimeMillis();
        if (left <= 0L) { cooldowns.remove(key); return 0L; }
        return (left + 999L) / 1000L;
    }

    private int parseInt(String value, int fallback) { try { return Integer.parseInt(value); } catch (NumberFormatException e) { return fallback; } }
    private Map<String, String> singleton(String key, String value) { return Collections.singletonMap(key, value == null ? "" : value); }
    private Map<String, String> map(String k1, String v1, String k2, String v2) { Map<String, String> m = new HashMap<String, String>(); m.put(k1, v1); m.put(k2, v2); return m; }
}
