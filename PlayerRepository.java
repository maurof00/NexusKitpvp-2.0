package it.maurof00.xeonkitpvp.repository;

import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public final class PlayerRepository {
    private final File file;
    private YamlConfiguration yaml;

    public PlayerRepository(File dataFolder) {
        this.file = new File(dataFolder, "players.yml");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerProfile load(UUID uuid, String name, long defaultCoins) {
        String base = path(uuid);
        PlayerProfile p = new PlayerProfile(uuid, name);
        p.setLastName(yaml.getString(base + ".name", name));
        p.setCoins(yaml.getLong(base + ".coins", defaultCoins));
        p.setKills(yaml.getLong(base + ".kills", 0L));
        p.setDeaths(yaml.getLong(base + ".deaths", 0L));
        p.setXp(yaml.getLong(base + ".xp", 0L));
        p.setLevel(yaml.getInt(base + ".level", 1));
        p.setPrestige(yaml.getInt(base + ".prestige", 0));
        p.setStreak(yaml.getLong(base + ".streak", 0L));
        p.setBestStreak(yaml.getLong(base + ".best-streak", 0L));
        p.setBounty(yaml.getLong(base + ".bounty", 0L));
        p.setSelectedKit(yaml.getString(base + ".selected-kit", "warrior"));
        p.getOwnedKits().addAll(yaml.getStringList(base + ".owned-kits"));
        p.setGang(yaml.getString(base + ".gang", null));
        p.setSelectedTag(yaml.getString(base + ".selected-tag", "default"));
        p.setSelectedKillEffect(yaml.getString(base + ".selected-kill-effect", "none"));
        p.getOwnedTags().addAll(yaml.getStringList(base + ".owned-tags"));
        p.getOwnedKillEffects().addAll(yaml.getStringList(base + ".owned-kill-effects"));
        p.setQuestDay(yaml.getString(base + ".quest-day", ""));
        p.setQuestWeek(yaml.getString(base + ".quest-week", ""));
        org.bukkit.configuration.ConfigurationSection questProgress = yaml.getConfigurationSection(base + ".quest-progress");
        if (questProgress != null) {
            for (String key : questProgress.getKeys(false)) p.getQuestProgress().put(key, questProgress.getLong(key));
        }
        p.getCompletedQuests().addAll(yaml.getStringList(base + ".completed-quests"));
        p.getOwnedKits().add("warrior");
        p.getOwnedTags().add("default");
        p.getOwnedKillEffects().add("none");
        return p;
    }

    public void save(PlayerProfile p) {
        String base = path(p.getUniqueId());
        yaml.set(base + ".name", p.getLastName());
        yaml.set(base + ".coins", p.getCoins());
        yaml.set(base + ".kills", p.getKills());
        yaml.set(base + ".deaths", p.getDeaths());
        yaml.set(base + ".xp", p.getXp());
        yaml.set(base + ".level", p.getLevel());
        yaml.set(base + ".prestige", p.getPrestige());
        yaml.set(base + ".streak", p.getStreak());
        yaml.set(base + ".best-streak", p.getBestStreak());
        yaml.set(base + ".bounty", p.getBounty());
        yaml.set(base + ".selected-kit", p.getSelectedKit());
        yaml.set(base + ".owned-kits", new ArrayList<String>(p.getOwnedKits()));
        yaml.set(base + ".gang", p.getGang());
        yaml.set(base + ".selected-tag", p.getSelectedTag());
        yaml.set(base + ".selected-kill-effect", p.getSelectedKillEffect());
        yaml.set(base + ".owned-tags", new ArrayList<String>(p.getOwnedTags()));
        yaml.set(base + ".owned-kill-effects", new ArrayList<String>(p.getOwnedKillEffects()));
        yaml.set(base + ".quest-day", p.getQuestDay());
        yaml.set(base + ".quest-week", p.getQuestWeek());
        yaml.set(base + ".quest-progress", p.getQuestProgress());
        yaml.set(base + ".completed-quests", new ArrayList<String>(p.getCompletedQuests()));
    }

    public void saveAll(Iterable<PlayerProfile> profiles) {
        for (PlayerProfile p : profiles) save(p);
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save players.yml", e);
        }
    }

    public UUID findUuidByName(String name) {
        ConfigurationSection section = yaml.getConfigurationSection("players");
        if (section == null) return null;
        for (String key : section.getKeys(false)) {
            String storedName = yaml.getString("players." + key + ".name", "");
            if (storedName.equalsIgnoreCase(name)) {
                try { return UUID.fromString(key); } catch (IllegalArgumentException ignored) { }
            }
        }
        return null;
    }

    public PlayerProfile loadByName(String name, long defaultCoins) {
        UUID uuid = findUuidByName(name);
        return uuid == null ? null : load(uuid, name, defaultCoins);
    }

    public void reload() {
        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public java.util.List<PlayerProfile> loadAll(long defaultCoins) {
        java.util.List<PlayerProfile> result = new java.util.ArrayList<PlayerProfile>();
        ConfigurationSection section = yaml.getConfigurationSection("players");
        if (section == null) return result;
        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                result.add(load(uuid, yaml.getString("players." + key + ".name", "Unknown"), defaultCoins));
            } catch (IllegalArgumentException ignored) { }
        }
        return result;
    }

    private String path(UUID uuid) { return "players." + uuid.toString(); }
}
