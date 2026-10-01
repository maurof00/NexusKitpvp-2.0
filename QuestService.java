package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.model.QuestDefinition;
import it.maurof00.xeonkitpvp.model.QuestDefinition.Period;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class QuestService {
    private final KitPvPCore plugin;
    private final MessageService messages;
    private final File file;
    private YamlConfiguration yaml;
    private final List<QuestDefinition> quests = new ArrayList<QuestDefinition>();

    public QuestService(KitPvPCore plugin, MessageService messages) {
        this.plugin=plugin; this.messages=messages; this.file=new File(plugin.getDataFolder(),"quests.yml"); reload();
    }
    public void reload() {
        yaml=YamlConfiguration.loadConfiguration(file); quests.clear(); loadPeriod(Period.DAILY,"quests.daily"); loadPeriod(Period.WEEKLY,"quests.weekly");
    }
    private void loadPeriod(Period period,String path) {
        ConfigurationSection section=yaml.getConfigurationSection(path); if(section==null)return;
        for(String id:section.getKeys(false)) {
            String base=path+"."+id;
            quests.add(new QuestDefinition(id,period,
                    ColorUtil.color(yaml.getString(base+".display",id)),
                    ColorUtil.color(yaml.getString(base+".description","")),
                    yaml.getString(base+".type","KILLS").toUpperCase(Locale.ENGLISH),
                    yaml.getLong(base+".target",1L),yaml.getLong(base+".reward.coins",0L),yaml.getLong(base+".reward.xp",0L)));
        }
    }
    public List<QuestDefinition> all() { return Collections.unmodifiableList(quests); }
    public List<QuestDefinition> byPeriod(Period p) { List<QuestDefinition> out=new ArrayList<QuestDefinition>(); for(QuestDefinition q:quests) if(q.getPeriod()==p)out.add(q); return out; }
    public void sync(PlayerProfile profile) {
        String day=LocalDate.now().toString();
        String week=yearWeek();
        if(!day.equals(profile.getQuestDay())) { profile.setQuestDay(day); removePeriod(profile,Period.DAILY); }
        if(!week.equals(profile.getQuestWeek())) { profile.setQuestWeek(week); removePeriod(profile,Period.WEEKLY); }
    }
    private String yearWeek() { LocalDate now=LocalDate.now(); WeekFields wf=WeekFields.ISO; return now.get(wf.weekBasedYear())+"-W"+now.get(wf.weekOfWeekBasedYear()); }
    private void removePeriod(PlayerProfile p,Period period) {
        String prefix=period.name().toLowerCase()+":";
        java.util.Iterator<String> it=p.getQuestProgress().keySet().iterator(); while(it.hasNext()) if(it.next().startsWith(prefix)) it.remove();
        it=p.getCompletedQuests().iterator(); while(it.hasNext()) if(it.next().startsWith(prefix)) it.remove();
    }
    public void onKill(Player player) { progress(player,"KILLS",1L); }
    public void onDeath(Player player) { progress(player,"DEATHS",1L); }
    public void onAssist(Player player) { progress(player,"ASSISTS",1L); }
    public void onStreak(Player player,long streak) { progress(player,"STREAK",streak); }
    public void progress(Player player,String type,long amount) {
        if(player==null || !plugin.getPluginConfig().questsEnabled() || amount<=0)return;
        PlayerProfile p=plugin.getPlayers().get(player); sync(p);
        for(QuestDefinition q:quests) {
            if(!q.getType().equalsIgnoreCase(type) || p.getCompletedQuests().contains(q.key())) continue;
            long current=p.getQuestProgress().containsKey(q.key())?p.getQuestProgress().get(q.key()):0L;
            long next=Math.min(q.getTarget(), current+amount); p.getQuestProgress().put(q.key(),next);
            if(plugin.getPluginConfig().questNotifyProgress()) {
                String threshold = next >= q.getTarget() ? "" : String.valueOf(next);
                if(next >= q.getTarget() || amount==1L) {
                    Map<String,String> m=new HashMap<String,String>(); m.put("display",q.getDisplay());m.put("progress",threshold);m.put("target",String.valueOf(q.getTarget()));
                    messages.send(player,"quest.progress",m);
                }
            }
            if(next>=q.getTarget()) complete(player,p,q);
        }
    }
    private void complete(Player player,PlayerProfile p,QuestDefinition q) {
        p.getCompletedQuests().add(q.key());
        if(plugin.getPluginConfig().questAutoReward()) {
            p.addCoins(q.getRewardCoins()); plugin.getLevels().addXp(p,q.getRewardXp());
            if(player != null) { Map<String,String> m=new HashMap<String,String>();m.put("display",q.getDisplay());m.put("coins",String.valueOf(q.getRewardCoins()));m.put("xp",String.valueOf(q.getRewardXp())); messages.send(player,"quest.reward",m); }
        }
    }
    public void reset(PlayerProfile p) { p.getQuestProgress().clear(); p.getCompletedQuests().clear(); sync(p); }
    public boolean markComplete(PlayerProfile p,String id,Period period) {
        QuestDefinition q=find(id,period); if(q==null)return false; sync(p); p.getQuestProgress().put(q.key(),q.getTarget()); complete(plugin.getServer().getPlayer(p.getUniqueId()),p,q); return true;
    }
    public QuestDefinition find(String id,Period p) { for(QuestDefinition q:quests) if(q.getId().equalsIgnoreCase(id)&&q.getPeriod()==p)return q; return null; }
}
