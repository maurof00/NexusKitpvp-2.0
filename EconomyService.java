package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.entity.Player;

public final class EconomyService {
    private final PlayerService players;
    public EconomyService(PlayerService players) { this.players = players; }
    public long balance(Player player) { return players.get(player).getCoins(); }
    public void set(PlayerProfile p, long amount) { p.setCoins(amount); }
    public void give(PlayerProfile p, long amount) { p.addCoins(Math.max(0L, amount)); }
    public boolean take(Player player, long amount) { return take(players.get(player), amount); }

    public boolean take(PlayerProfile p, long amount) {
        amount = Math.max(0L, amount);
        if (p.getCoins() < amount) return false;
        p.setCoins(p.getCoins() - amount); return true;
    }
}
