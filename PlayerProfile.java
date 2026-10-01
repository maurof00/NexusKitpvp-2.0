package it.maurof00.xeonkitpvp.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerProfile {
    private final UUID uniqueId;
    private String lastName;
    private long coins;
    private long kills;
    private long deaths;
    private long xp;
    private int level;
    private int prestige;
    private long streak;
    private long bestStreak;
    private long bounty;
    private String selectedKit;
    private final Set<String> ownedKits = new HashSet<String>();
    private String gang;
    private String selectedTag = "default";
    private String selectedKillEffect = "none";
    private final Set<String> ownedTags = new HashSet<String>();
    private final Set<String> ownedKillEffects = new HashSet<String>();
    private String questDay = "";
    private String questWeek = "";
    private final java.util.Map<String, Long> questProgress = new java.util.HashMap<String, Long>();
    private final Set<String> completedQuests = new HashSet<String>();

    public PlayerProfile(UUID uniqueId, String lastName) {
        this.uniqueId = uniqueId;
        this.lastName = lastName == null ? "Unknown" : lastName;
        this.level = 1;
        this.selectedKit = "warrior";
    }

    public UUID getUniqueId() { return uniqueId; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { if (lastName != null) this.lastName = lastName; }
    public long getCoins() { return coins; }
    public void setCoins(long coins) { this.coins = Math.max(0L, coins); }
    public void addCoins(long amount) { setCoins(coins + amount); }
    public long getKills() { return kills; }
    public void setKills(long kills) { this.kills = Math.max(0L, kills); }
    public void addKill() { kills++; }
    public long getDeaths() { return deaths; }
    public void setDeaths(long deaths) { this.deaths = Math.max(0L, deaths); }
    public void addDeath() { deaths++; }
    public long getXp() { return xp; }
    public void setXp(long xp) { this.xp = Math.max(0L, xp); }
    public void addXp(long amount) { xp = Math.max(0L, xp + amount); }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public int getPrestige() { return prestige; }
    public void setPrestige(int prestige) { this.prestige = Math.max(0, prestige); }
    public long getStreak() { return streak; }
    public void setStreak(long streak) { this.streak = Math.max(0L, streak); }
    public long getBestStreak() { return bestStreak; }
    public void setBestStreak(long bestStreak) { this.bestStreak = Math.max(0L, bestStreak); }
    public long getBounty() { return bounty; }
    public void setBounty(long bounty) { this.bounty = Math.max(0L, bounty); }
    public String getSelectedKit() { return selectedKit; }
    public void setSelectedKit(String selectedKit) { this.selectedKit = selectedKit == null ? "warrior" : selectedKit; }
    public Set<String> getOwnedKits() { return ownedKits; }
    public String getGang() { return gang; }
    public void setGang(String gang) { this.gang = gang; }
    public String getSelectedTag() { return selectedTag; }
    public void setSelectedTag(String selectedTag) { this.selectedTag = selectedTag == null ? "default" : selectedTag; }
    public String getSelectedKillEffect() { return selectedKillEffect; }
    public void setSelectedKillEffect(String selectedKillEffect) { this.selectedKillEffect = selectedKillEffect == null ? "none" : selectedKillEffect; }
    public Set<String> getOwnedTags() { return ownedTags; }
    public Set<String> getOwnedKillEffects() { return ownedKillEffects; }
    public String getQuestDay() { return questDay; }
    public void setQuestDay(String questDay) { this.questDay = questDay == null ? "" : questDay; }
    public String getQuestWeek() { return questWeek; }
    public void setQuestWeek(String questWeek) { this.questWeek = questWeek == null ? "" : questWeek; }
    public java.util.Map<String, Long> getQuestProgress() { return questProgress; }
    public Set<String> getCompletedQuests() { return completedQuests; }

    public double getKd() {
        if (deaths <= 0) return kills;
        return ((double) kills) / ((double) deaths);
    }
}
