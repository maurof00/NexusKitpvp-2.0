package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KillService {
    private final KitPvPCore plugin;
    public KillService(KitPvPCore plugin){this.plugin=plugin;}
    public void handleDeath(Player victim, Player killer){
        if(victim==null)return;
        PlayerProfile vp=plugin.getPlayers().get(victim);vp.addDeath();vp.setStreak(0L);plugin.getLevels().addXp(victim,plugin.getPluginConfig().xpPerDeath());plugin.getQuests().onDeath(victim);
        if(plugin.getPluginConfig().noDrops()) victim.getWorld();
        if(killer!=null&&killer.isOnline()&&!killer.equals(victim)) processKill(killer,victim);
        else plugin.getMessages().send(victim,"death",Collections.singletonMap("xp",String.valueOf(plugin.getPluginConfig().xpPerDeath())));
        plugin.getCombat().clear(victim);if(killer!=null)plugin.getCombat().clear(killer);
    }
    public void handleCombatLogout(Player victim, Player killer){
        if(victim==null)return;
        PlayerProfile vp=plugin.getPlayers().get(victim);vp.addDeath();vp.setStreak(0L);plugin.getLevels().addXp(vp,plugin.getPluginConfig().xpPerDeath());plugin.getQuests().onDeath(victim);
        if(plugin.getPluginConfig().combatLogBroadcast()) plugin.getMessages().broadcast("quit-combat",Collections.singletonMap("player",victim.getName()));
        if(killer!=null&&killer.isOnline()&&!killer.equals(victim))processKill(killer,victim);
        plugin.getCombat().clear(victim);if(killer!=null)plugin.getCombat().clear(killer);
    }
    private void processKill(Player killer,Player victim){
        PlayerProfile kp=plugin.getPlayers().get(killer);boolean rewarded=plugin.getAntiFarm().isAllowed(killer,victim);
        if(rewarded||!plugin.getPluginConfig().antiFarmBlockStatistics()){kp.addKill();kp.setStreak(kp.getStreak()+1L);if(kp.getStreak()>kp.getBestStreak())kp.setBestStreak(kp.getStreak());}
        if(!rewarded){plugin.getAntiFarm().notifyBlocked(killer);plugin.getMessages().broadcast("killed-no-reward",map("victim",victim.getName(),"killer",killer.getName()));if(!plugin.getPluginConfig().antiFarmBlockStatistics())plugin.getLevels().addXp(killer,0L);return;}
        long reward=plugin.getPluginConfig().killCoins();kp.addCoins(reward);plugin.getLevels().addXp(killer,plugin.getPluginConfig().xpPerKill());plugin.getQuests().onKill(killer);plugin.getBounties().claim(killer,victim);plugin.getKillEffects().play(killer,victim);
        for(Player assist:plugin.getCombat().getAssistPlayers(victim,killer)){ PlayerProfile ap=plugin.getPlayers().get(assist); ap.addCoins(plugin.getPluginConfig().assistCoins()); plugin.getQuests().onAssist(assist); }
        Map<String,String> m=map("victim",victim.getName(),"killer",killer.getName());m.put("coins",String.valueOf(reward));plugin.getMessages().broadcast("killed",m);plugin.getMessages().send(victim,"death",Collections.singletonMap("xp",String.valueOf(plugin.getPluginConfig().xpPerDeath())));rewardStreak(killer,kp.getStreak());
    }
    public void recordAssist(Player player){plugin.getQuests().onAssist(player);}
    private void rewardStreak(Player player,long streak){if(!plugin.getPluginConfig().killstreakEnabled())return;String key="killstreak.rewards."+streak;if(!plugin.getConfig().isConfigurationSection(key))return;long coins=plugin.getConfig().getLong(key+".coins",0L);if(coins>0)plugin.getPlayers().get(player).addCoins(coins);List<String> commands=plugin.getConfig().getStringList(key+".commands");for(String cmd:commands)Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%player%",player.getName()));plugin.getQuests().onStreak(player,streak);if(plugin.getPluginConfig().streakAnnouncements())plugin.getMessages().broadcast("streak",Collections.singletonMap("player",player.getName()));}
    private Map<String,String> map(String a,String b,String c,String d){Map<String,String>m=new HashMap<String,String>();m.put(a,b);m.put(c,d);return m;}
}
