package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.repository.PlayerRepository;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class LeaderboardService {
    private final PlayerService players;
    private final PlayerRepository repository;
    public LeaderboardService(PlayerService players, PlayerRepository repository) { this.players = players; this.repository = repository; }

    public List<PlayerProfile> top(String stat) {
        List<PlayerProfile> all = new ArrayList<PlayerProfile>(repository.loadAll(0L));
        for (PlayerProfile cached : players.allProfiles()) {
            boolean replaced = false;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getUniqueId().equals(cached.getUniqueId())) { all.set(i, cached); replaced = true; break; }
            }
            if (!replaced) all.add(cached);
        }
        Collections.sort(all, new Comparator<PlayerProfile>() {
            @Override public int compare(PlayerProfile a, PlayerProfile b) { return Long.compare(getValue(b, stat), getValue(a, stat)); }
        });
        return all;
    }
    private long getValue(PlayerProfile p, String stat) {
        if ("kills".equalsIgnoreCase(stat)) return p.getKills();
        if ("coins".equalsIgnoreCase(stat)) return p.getCoins();
        if ("streak".equalsIgnoreCase(stat)) return p.getBestStreak();
        if ("level".equalsIgnoreCase(stat)) return p.getLevel();
        if ("xp".equalsIgnoreCase(stat)) return p.getXp();
        if ("prestige".equalsIgnoreCase(stat)) return p.getPrestige();
        return 0L;
    }
}
