package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.entity.Player;

import java.util.Collections;

public final class LevelService {
    private final KitPvPCore plugin;
    private final PluginConfig config;
    private final MessageService messages;

    public LevelService(KitPvPCore plugin, PluginConfig config, MessageService messages) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    public long requiredXp(int level, int prestige) {
        long base = config.baseXp() + Math.max(0, level - 1) * config.xpPerLevel();
        double multiplier = config.prestigeXpMultiplier();
        if (multiplier < 1.0D) multiplier = 1.0D;
        double result = base * Math.pow(multiplier, Math.max(0, prestige));
        return Math.max(1L, Math.min(Long.MAX_VALUE, (long) result));
    }

    public long requiredXp(PlayerProfile profile) { return requiredXp(profile.getLevel(), profile.getPrestige()); }

    public void addXp(Player player, long amount) {
        if (player == null || amount <= 0L || !config.levelingEnabled()) return;
        PlayerProfile profile = plugin.getPlayers().get(player);
        addXp(profile, amount, player);
    }

    public void addXp(PlayerProfile profile, long amount) { addXp(profile, amount, plugin.getServer().getPlayer(profile.getUniqueId())); }

    private void addXp(PlayerProfile profile, long amount, Player online) {
        if (profile == null || amount <= 0L || !config.levelingEnabled()) return;
        long xp = safeAdd(profile.getXp(), amount);
        profile.setXp(xp);
        while (profile.getLevel() < config.maxLevel() && profile.getXp() >= requiredXp(profile)) {
            long needed = requiredXp(profile);
            profile.setXp(profile.getXp() - needed);
            profile.setLevel(profile.getLevel() + 1);
            if (online != null && online.isOnline()) {
                messages.send(online, "level-up", Collections.singletonMap("level", String.valueOf(profile.getLevel())));
            }
        }
        while (config.prestigeEnabled() && profile.getLevel() >= config.maxLevel()
                && profile.getXp() >= requiredXp(profile)
                && profile.getPrestige() < config.maxPrestige()) {
            long needed = requiredXp(profile);
            if (config.prestigeResetXp()) profile.setXp(0L); else profile.setXp(profile.getXp() - needed);
            profile.setLevel(1);
            profile.setPrestige(profile.getPrestige() + 1);
            long reward = config.prestigeRewardCoins();
            if (reward > 0L) profile.addCoins(reward);
            if (online != null && online.isOnline() && config.prestigeAnnounce()) {
                java.util.Map<String,String> map = new java.util.HashMap<String,String>();
                map.put("prestige", String.valueOf(profile.getPrestige()));
                map.put("coins", String.valueOf(reward));
                messages.send(online, "prestige-up", map);
            }
        }
        if (profile.getPrestige() >= config.maxPrestige() && profile.getLevel() >= config.maxLevel()) {
            profile.setXp(Math.min(profile.getXp(), requiredXp(profile)));
        }
    }

    public String progressBar(PlayerProfile p, int width) {
        long required = requiredXp(p);
        if (required <= 0L) return "";
        double ratio = Math.max(0D, Math.min(1D, (double)p.getXp() / (double)required));
        int filled = (int)Math.round(ratio * width);
        StringBuilder out = new StringBuilder();
        for (int i=0;i<width;i++) out.append(i < filled ? '|' : '.');
        return out.toString();
    }

    private long safeAdd(long a, long b) {
        if (Long.MAX_VALUE - a < b) return Long.MAX_VALUE;
        return a + b;
    }
}
