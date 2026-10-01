package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.entity.Player;

import java.util.Collections;

public final class BountyService {
    private final PlayerService players;
    private final PluginConfig config;
    private final MessageService messages;
    public BountyService(PlayerService players, PluginConfig config, MessageService messages) { this.players = players; this.config = config; this.messages = messages; }

    public boolean set(Player setter, Player target, long amount) {
        if (!config.bountyEnabled() || target.equals(setter)) return false;
        if (amount < config.bountyMin() || amount > config.bountyMax()) return false;
        PlayerProfile s = players.get(setter);
        if (!take(s, amount)) return false;
        players.get(target).setBounty(players.get(target).getBounty() + amount);
        messages.send(setter, "bounty-set", map("amount", String.valueOf(amount), "player", target.getName()));
        messages.send(target, "bounty-received", map("amount", String.valueOf(amount), "player", setter.getName()));
        return true;
    }

    public long claim(Player killer, Player victim) {
        PlayerProfile v = players.get(victim);
        long bounty = v.getBounty();
        if (bounty <= 0L) return 0L;
        v.setBounty(0L);
        long reward = bounty * config.bountyRewardPercent() / 100L;
        players.get(killer).addCoins(reward);
        messages.send(killer, "killed-bounty", map("amount", String.valueOf(reward), "victim", victim.getName()));
        return reward;
    }

    public boolean take(PlayerProfile profile, long amount) {
        if (profile.getCoins() < amount) return false;
        profile.setCoins(profile.getCoins() - amount); return true;
    }

    public java.util.Map<String,String> map(String a, String av, String b, String bv) {
        java.util.Map<String,String> m = new java.util.HashMap<String,String>(); m.put(a,av); m.put(b,bv); return m;
    }
}
