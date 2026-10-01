package it.maurof00.xeonkitpvp.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public final class PluginConfig {
    private FileConfiguration config;
    public PluginConfig(FileConfiguration config) { this.config = config; }
    public void reload(FileConfiguration config) { this.config = config; }
    public List<String> enabledWorlds() { return config.getStringList("game.enabled-worlds"); }
    public boolean isKitPvPWorld(String world) { List<String> worlds=enabledWorlds(); return worlds.isEmpty() || worlds.contains(world); }
    public boolean pvpEnabled() { return config.getBoolean("game.allow-pvp",true); }
    public boolean blockBuild() { return config.getBoolean("game.block-build",false); }
    public boolean blockBreak() { return config.getBoolean("game.block-break",false); }
    public boolean noDrops() { return config.getBoolean("game.no-item-drops",true); }
    public boolean noHunger() { return config.getBoolean("game.no-hunger",true); }
    public int soupHealHalfHearts() { return config.getInt("game.soup-heal-half-hearts",6); }
    public double spawnProtectionRadius() { return config.getDouble("game.spawn-protection-radius",10D); }
    public int combatTagSeconds() { return Math.max(0,config.getInt("game.combat-tag-seconds",15)); }
    public boolean combatLogoutPenalty() { return config.getBoolean("game.combat-logout-penalty",true); }
    public String combatLogoutCommand() { return config.getString("game.combat-logout-command",""); }
    public boolean combatLogEnabled() { return config.getBoolean("combat-log.enabled",true); }
    public int combatLogDuration() { return Math.max(0,config.getInt("combat-log.duration-seconds",combatTagSeconds())); }
    public boolean combatLogKillAttacker() { return config.getBoolean("combat-log.kill-attacker-on-logout",true); }
    public boolean combatLogBroadcast() { return config.getBoolean("combat-log.broadcast-logout",true); }
    public boolean combatLogBlockCommands() { return config.getBoolean("combat-log.block-commands",true); }
    public List<String> combatBlockedCommands() { return config.getStringList("combat-log.blocked-commands"); }
    public int assistWindowSeconds() { return Math.max(1,config.getInt("combat-log.assist-window-seconds",10)); }
    public boolean preventVoidDeath() { return config.getBoolean("game.prevent-void-death",true); }
    public int respawnDelayTicks() { return Math.max(0,config.getInt("game.respawn-delay-ticks",2)); }
    public String joinKit() { return config.getString("game.join-kit","warrior"); }
    public long joinCoins() { return Math.max(0L,config.getLong("economy.join-coins",250L)); }
    public long killCoins() { return Math.max(0L,config.getLong("economy.kill-coins",25L)); }
    public long streakCoins() { return Math.max(0L,config.getLong("economy.streak-coins",10L)); }
    public long xpPerKill() { return Math.max(0L,config.getLong("economy.xp-per-kill",25L)); }
    public long xpPerDeath() { return Math.max(0L,config.getLong("economy.xp-per-death",5L)); }
    public long assistCoins() { return Math.max(0L,config.getLong("economy.assist-coins",10L)); }
    public int maxLevel() { return Math.max(1,config.getInt("leveling.max-level",100)); }
    public long baseXp() { return Math.max(1L,config.getLong("leveling.base-xp",100L)); }
    public long xpPerLevel() { return Math.max(1L,config.getLong("leveling.xp-per-level",75L)); }
    public boolean levelingEnabled() { return config.getBoolean("leveling.enabled",true); }
    public boolean prestigeEnabled() { return config.getBoolean("leveling.prestige-enabled",true); }
    public int maxPrestige() { return Math.max(0,config.getInt("leveling.max-prestige",25)); }
    public double prestigeXpMultiplier() { return config.getDouble("leveling.prestige-xp-multiplier",1.10D); }
    public long prestigeRewardCoins() { return Math.max(0L,config.getLong("leveling.prestige-reward-coins",2500L)); }
    public boolean prestigeResetXp() { return config.getBoolean("leveling.prestige-reset-xp",true); }
    public boolean prestigeAnnounce() { return config.getBoolean("leveling.prestige-announce",true); }
    public boolean killstreakEnabled() { return config.getBoolean("killstreak.enabled",true); }
    public boolean streakAnnouncements() { return config.getBoolean("killstreak.announcements",true); }
    public boolean scoreboardEnabled() { return config.getBoolean("scoreboard.enabled",true); }
    public int scoreboardRefreshTicks() { return Math.max(1,config.getInt("scoreboard.refresh-ticks",20)); }
    public String scoreboardTitle() { return config.getString("scoreboard.title","&b&lXEON KITPVP"); }
    public List<String> scoreboardLines() { return config.getStringList("scoreboard.lines"); }
    public boolean tablistEnabled() { return config.getBoolean("tablist.enabled",true); }
    public String tabHeader() { return config.getString("tablist.header",""); }
    public String tabFooter() { return config.getString("tablist.footer",""); }
    public boolean bountyEnabled() { return config.getBoolean("bounty.enabled",true); }
    public long bountyMin() { return Math.max(0L,config.getLong("bounty.min",50L)); }
    public long bountyMax() { return Math.max(bountyMin(),config.getLong("bounty.max",100000L)); }
    public int bountyRewardPercent() { return Math.max(0,Math.min(100,config.getInt("bounty.killer-reward-percent",100))); }
    public boolean gangEnabled() { return config.getBoolean("gang.enabled",true); }
    public boolean friendlyFire() { return config.getBoolean("gang.friendly-fire",false); }
    public int maxGangMembers() { return Math.max(1,config.getInt("gang.max-members",15)); }
    public long gangCreateCost() { return Math.max(0L,config.getLong("gang.create-cost",1000L)); }
    public boolean antiFarmEnabled() { return config.getBoolean("anti-farm.enabled",true); }
    public long antiFarmWindowSeconds() { return Math.max(1L,config.getLong("anti-farm.pair-window-seconds",600L)); }
    public int antiFarmMaxKills() { return Math.max(1,config.getInt("anti-farm.max-rewarded-kills-per-window",3)); }
    public long antiFarmMinimumDelaySeconds() { return Math.max(0L,config.getLong("anti-farm.minimum-kill-delay-seconds",20L)); }
    public boolean antiFarmBlockStatistics() { return config.getBoolean("anti-farm.block-statistics",false); }
    public boolean antiFarmNotify() { return config.getBoolean("anti-farm.notify-player",true); }
    public String antiFarmBypassPermission() { return config.getString("anti-farm.ignore-permission","kitpvp.antifarm.bypass"); }
    public boolean questsEnabled() { return config.getBoolean("quests.enabled",true); }
    public boolean questNotifyProgress() { return config.getBoolean("quests.notify-progress",true); }
    public boolean questAutoReward() { return config.getBoolean("quests.auto-reward",true); }
    public boolean shopEnabled() { return config.getBoolean("shop.enabled",true); }
    public boolean shopUpdateOnPurchase() { return config.getBoolean("shop.menu-update-on-purchase",true); }
    public boolean chatTagsEnabled() { return config.getBoolean("chat-tags.enabled",true); }
    public String chatFormat() { return config.getString("chat-tags.format","%tag% &f%player% &8» &7%message%"); }
    public boolean killEffectsEnabled() { return config.getBoolean("kill-effects.enabled",true); }
    public int killEffectsRadius() { return Math.max(1,config.getInt("kill-effects.radius",24)); }
    public boolean vaultEnabled() { return config.getBoolean("hooks.vault",true); }
    public boolean placeholderEnabled() { return config.getBoolean("hooks.placeholderapi",true); }
    public boolean tabHookEnabled() { return config.getBoolean("hooks.tab",true); }
    public int saveIntervalSeconds() { return Math.max(5,config.getInt("core.save-interval-seconds",30)); }
    public boolean autoSaveOnQuit() { return config.getBoolean("core.auto-save-on-quit",true); }
}
