package it.maurof00.xeonkitpvp.model;

public final class QuestDefinition {
    public enum Period { DAILY, WEEKLY }

    private final String id;
    private final Period period;
    private final String display;
    private final String description;
    private final String type;
    private final long target;
    private final long rewardCoins;
    private final long rewardXp;

    public QuestDefinition(String id, Period period, String display, String description, String type,
                           long target, long rewardCoins, long rewardXp) {
        this.id = id;
        this.period = period;
        this.display = display;
        this.description = description;
        this.type = type;
        this.target = Math.max(1L, target);
        this.rewardCoins = Math.max(0L, rewardCoins);
        this.rewardXp = Math.max(0L, rewardXp);
    }
    public String getId() { return id; }
    public Period getPeriod() { return period; }
    public String getDisplay() { return display; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public long getTarget() { return target; }
    public long getRewardCoins() { return rewardCoins; }
    public long getRewardXp() { return rewardXp; }
    public String key() { return period.name().toLowerCase() + ":" + id; }
}
