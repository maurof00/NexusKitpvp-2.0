package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.model.Gang;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.repository.GangRepository;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class GangService {
    private final KitPvPCore plugin;
    private final PlayerService players;
    private final EconomyService economy;
    private final PluginConfig config;
    private final MessageService messages;
    private final GangRepository repository;
    private final Map<String, Gang> gangs = new HashMap<String, Gang>();
    private final Map<UUID, String> invites = new HashMap<UUID, String>();

    public GangService(KitPvPCore plugin, PlayerService players, EconomyService economy, PluginConfig config, MessageService messages) {
        this.plugin = plugin; this.players = players; this.economy = economy; this.config = config; this.messages = messages;
        this.repository = new GangRepository(plugin.getDataFolder()); repository.loadInto(gangs);
    }

    public Collection<Gang> all() { return Collections.unmodifiableCollection(gangs.values()); }
    public Gang get(String name) { return name == null ? null : gangs.get(name.toLowerCase(Locale.ENGLISH)); }
    public Gang byPlayer(UUID uuid) {
        for (Gang gang : gangs.values()) if (gang.getMembers().contains(uuid)) return gang;
        return null;
    }

    public boolean create(Player leader, String name) {
        if (!config.gangEnabled()) return false;
        if (name == null || !name.matches("[A-Za-z0-9_]{3,16}")) { messages.send(leader, "gang-exists"); return false; }
        if (get(name) != null) { messages.send(leader, "gang-exists"); return false; }
        if (byPlayer(leader.getUniqueId()) != null) { messages.send(leader, "gang-already"); return false; }
        if (!economy.take(players.get(leader), config.gangCreateCost())) { messages.send(leader, "not-enough-coins"); return false; }
        Gang gang = new Gang(name, leader.getUniqueId()); gangs.put(name.toLowerCase(Locale.ENGLISH), gang);
        players.get(leader).setGang(name);
        messages.send(leader, "gang-created", Collections.singletonMap("gang", name));
        return true;
    }

    public void invite(Player leader, Player target) {
        Gang gang = byPlayer(leader.getUniqueId());
        if (gang == null) { messages.send(leader, "gang-not-in"); return; }
        if (!gang.getLeader().equals(leader.getUniqueId())) { messages.send(leader, "gang-not-leader"); return; }
        if (gang.getMembers().size() >= config.maxGangMembers()) return;
        invites.put(target.getUniqueId(), gang.getName());
        messages.send(leader, "gang-invited", map("player", target.getName(), "gang", gang.getName()));
        messages.send(target, "gang-invite-received", map("player", leader.getName(), "gang", gang.getName()));
    }

    public void join(Player player, String name) {
        Gang gang = get(name);
        String invited = invites.get(player.getUniqueId());
        if (gang == null) { messages.send(player, "gang-not-found"); return; }
        if (invited == null || !invited.equalsIgnoreCase(gang.getName())) { messages.send(player, "gang-no-invite"); return; }
        if (byPlayer(player.getUniqueId()) != null) { messages.send(player, "gang-already"); return; }
        if (gang.getMembers().size() >= config.maxGangMembers()) return;
        gang.getMembers().add(player.getUniqueId()); players.get(player).setGang(gang.getName()); invites.remove(player.getUniqueId());
        messages.send(player, "gang-joined", Collections.singletonMap("gang", gang.getName()));
    }

    public void leave(Player player) {
        Gang gang = byPlayer(player.getUniqueId());
        if (gang == null) { messages.send(player, "gang-not-in"); return; }
        if (gang.getLeader().equals(player.getUniqueId())) { messages.send(player, "gang-leader-cannot-leave"); return; }
        gang.getMembers().remove(player.getUniqueId()); players.get(player).setGang(null); messages.send(player, "gang-left");
    }

    public void disband(Player player) {
        Gang gang = byPlayer(player.getUniqueId());
        if (gang == null) { messages.send(player, "gang-not-in"); return; }
        if (!gang.getLeader().equals(player.getUniqueId())) { messages.send(player, "gang-not-leader"); return; }
        for (UUID member : gang.getMembers()) {
            org.bukkit.entity.Player online = plugin.getServer().getPlayer(member);
            if (online != null) players.get(online).setGang(null);
            else players.get(member, null).setGang(null);
        }
        gangs.remove(gang.getName().toLowerCase(Locale.ENGLISH)); messages.send(player, "gang-disbanded");
    }

    public void save() { repository.saveAll(gangs.values()); }
    public void reload() { repository.reload(gangs); }

    public boolean canDamage(Player first, Player second) {
        if (config.friendlyFire()) return true;
        Gang a = byPlayer(first.getUniqueId());
        return a == null || !a.getMembers().contains(second.getUniqueId());
    }

    private Map<String, String> map(String a, String av, String b, String bv) { Map<String, String> m = new HashMap<String, String>(); m.put(a, av); m.put(b, bv); return m; }
}
