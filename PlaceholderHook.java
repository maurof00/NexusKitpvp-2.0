package it.maurof00.xeonkitpvp.hook;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.Locale;

public final class PlaceholderHook extends PlaceholderExpansion {
    private final KitPvPCore plugin;

    public PlaceholderHook(KitPvPCore plugin) { this.plugin = plugin; }
    @Override public String getIdentifier() { return "xeonkitpvp"; }
    @Override public String getAuthor() { return "maurof00"; }
    @Override public String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) return "";
        PlayerProfile profile = plugin.getPlayers().get(player.getUniqueId(), player.getName());
        String key = params.toLowerCase(Locale.ENGLISH);
        if ("player".equals(key)) return profile.getLastName();
        if ("kills".equals(key)) return String.valueOf(profile.getKills());
        if ("deaths".equals(key)) return String.valueOf(profile.getDeaths());
        if ("kd".equals(key)) return plugin.formatKd(profile.getKd());
        if ("coins".equals(key) || "money".equals(key)) return String.valueOf(profile.getCoins());
        if ("xp".equals(key)) return String.valueOf(profile.getXp());
        if ("level".equals(key)) return String.valueOf(profile.getLevel());
        if ("prestige".equals(key)) return String.valueOf(profile.getPrestige());
        if ("nextxp".equals(key)) return String.valueOf(plugin.getLevels().requiredXp(profile));
        if ("streak".equals(key)) return String.valueOf(profile.getStreak());
        if ("beststreak".equals(key)) return String.valueOf(profile.getBestStreak());
        if ("bounty".equals(key)) return String.valueOf(profile.getBounty());
        if ("gang".equals(key)) return profile.getGang() == null ? "-" : profile.getGang();
        if ("tag".equals(key)) return plugin.getTags().display(profile.getSelectedTag());
        if ("killeffect".equals(key)) return plugin.getKillEffects().display(profile.getSelectedKillEffect());
        if ("combat".equals(key)) return plugin.getCombat().isTagged(player.getUniqueId()) ? "yes" : "no";
        if ("online".equals(key)) return String.valueOf(plugin.getServer().getOnlinePlayers().size());
        return null;
    }
}
