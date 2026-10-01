package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.repository.PlayerRepository;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerService {
    private final KitPvPCore plugin;
    private final PluginConfig config;
    private final PlayerRepository repository;
    private final MessageService messages;
    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<UUID, PlayerProfile>();

    public PlayerService(KitPvPCore plugin, PluginConfig config, PlayerRepository repository, MessageService messages) {
        this.plugin = plugin; this.config = config; this.repository = repository; this.messages = messages;
    }

    public PlayerProfile get(Player player) { return get(player.getUniqueId(), player.getName()); }
    public PlayerProfile get(UUID uuid, String name) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) {
            profile = repository.load(uuid, name, config.joinCoins());
            profiles.put(uuid, profile);
        }
        profile.setLastName(name);
        return profile;
    }

    public PlayerProfile getByName(String name) {
        Player online = plugin.getServer().getPlayerExact(name);
        if (online != null) return get(online);
        PlayerProfile loaded = repository.loadByName(name, config.joinCoins());
        return loaded;
    }

    public void load(Player player) { get(player); }
    public void unload(Player player, boolean save) {
        PlayerProfile profile = profiles.remove(player.getUniqueId());
        if (save && profile != null) {
            repository.saveAll(java.util.Collections.singleton(profile));
        }
    }
    public Collection<PlayerProfile> allProfiles() { return profiles.values(); }
    public void saveAll() { repository.saveAll(profiles.values()); }
    public void saveProfile(PlayerProfile profile) { repository.saveAll(java.util.Collections.singleton(profile)); }
    public void reload() { repository.reload(); }
    public void removeCached(UUID uuid) { profiles.remove(uuid); }

    public void setStat(PlayerProfile p, String stat, long value) {
        if ("kills".equalsIgnoreCase(stat)) p.setKills(value);
        else if ("deaths".equalsIgnoreCase(stat)) p.setDeaths(value);
        else if ("coins".equalsIgnoreCase(stat) || "money".equalsIgnoreCase(stat)) p.setCoins(value);
        else if ("xp".equalsIgnoreCase(stat)) p.setXp(value);
        else if ("level".equalsIgnoreCase(stat)) p.setLevel((int) value);
        else if ("prestige".equalsIgnoreCase(stat)) p.setPrestige((int) value);
        else if ("streak".equalsIgnoreCase(stat)) p.setStreak(value);
        else if ("beststreak".equalsIgnoreCase(stat) || "best-streak".equalsIgnoreCase(stat)) p.setBestStreak(value);
        else if ("bounty".equalsIgnoreCase(stat)) p.setBounty(value);
        else throw new IllegalArgumentException("Unknown stat: " + stat);
    }

    public long getStat(PlayerProfile p, String stat) {
        if ("kills".equalsIgnoreCase(stat)) return p.getKills();
        if ("deaths".equalsIgnoreCase(stat)) return p.getDeaths();
        if ("coins".equalsIgnoreCase(stat) || "money".equalsIgnoreCase(stat)) return p.getCoins();
        if ("xp".equalsIgnoreCase(stat)) return p.getXp();
        if ("level".equalsIgnoreCase(stat)) return p.getLevel();
        if ("prestige".equalsIgnoreCase(stat)) return p.getPrestige();
        if ("streak".equalsIgnoreCase(stat)) return p.getStreak();
        if ("beststreak".equalsIgnoreCase(stat) || "best-streak".equalsIgnoreCase(stat)) return p.getBestStreak();
        if ("bounty".equalsIgnoreCase(stat)) return p.getBounty();
        throw new IllegalArgumentException("Unknown stat: " + stat);
    }
}
