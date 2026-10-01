package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CombatService {
    private static final class Tag { long until; UUID opponent; Tag(long until,UUID opponent){this.until=until;this.opponent=opponent;} }
    private final PluginConfig config; private final MessageService messages;
    private final Map<UUID,Tag> tagged=new HashMap<UUID,Tag>();
    private final Map<UUID,Map<UUID,Long>> damage=new HashMap<UUID,Map<UUID,Long>>();
    private int taskId=-1;
    public CombatService(PluginConfig config,MessageService messages){this.config=config;this.messages=messages;}
    public void start(org.bukkit.plugin.Plugin plugin){stop();if(!config.combatLogEnabled())return;taskId=Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){public void run(){tick();}},20L,20L);}
    public void stop(){if(taskId!=-1)Bukkit.getScheduler().cancelTask(taskId);taskId=-1;tagged.clear();damage.clear();}
    public void tag(Player player,Player opponent){
        if(!config.combatLogEnabled()||player==null)return;
        long seconds=config.combatLogDuration()>0?config.combatLogDuration():config.combatTagSeconds();
        long until=System.currentTimeMillis()+seconds*1000L;Tag old=tagged.get(player.getUniqueId());tagged.put(player.getUniqueId(),new Tag(until,opponent==null?null:opponent.getUniqueId()));
        if(old==null||old.until<=System.currentTimeMillis())messages.send(player,"combat-tagged",java.util.Collections.singletonMap("seconds",String.valueOf(seconds)));
    }
    public void recordDamage(Player victim,Player attacker){if(victim==null||attacker==null||victim.equals(attacker))return;Map<UUID,Long> contributors=damage.get(victim.getUniqueId());if(contributors==null){contributors=new HashMap<UUID,Long>();damage.put(victim.getUniqueId(),contributors);}contributors.put(attacker.getUniqueId(),System.currentTimeMillis());tag(attacker,victim);tag(victim,attacker);}
    public List<Player> getAssistPlayers(Player victim,Player killer){List<Player> out=new ArrayList<Player>();if(victim==null)return out;Map<UUID,Long> contributors=damage.get(victim.getUniqueId());if(contributors==null)return out;long cutoff=System.currentTimeMillis()-config.assistWindowSeconds()*1000L;for(Map.Entry<UUID,Long> e:contributors.entrySet()){if(e.getValue()<cutoff)continue;Player p=Bukkit.getPlayer(e.getKey());if(p!=null&&p.isOnline()&&(killer==null||!p.equals(killer))&&!p.equals(victim))out.add(p);}return out;}
    public boolean isTagged(Player player){return player!=null&&isTagged(player.getUniqueId());}
    public boolean isTagged(UUID uuid){Tag t=tagged.get(uuid);if(t==null)return false;if(t.until<=System.currentTimeMillis()){tagged.remove(uuid);return false;}return true;}
    public long remainingSeconds(Player player){Tag t=player==null?null:tagged.get(player.getUniqueId());if(t==null)return 0L;return Math.max(0L,(t.until-System.currentTimeMillis()+999L)/1000L);}
    public UUID getOpponent(UUID uuid){Tag t=tagged.get(uuid);return t==null?null:t.opponent;}
    public UUID getOpponent(Player player){return player==null?null:getOpponent(player.getUniqueId());}
    public void clear(Player player){if(player==null)return;Tag removed=tagged.remove(player.getUniqueId());damage.remove(player.getUniqueId());if(removed!=null&&removed.until>System.currentTimeMillis())messages.send(player,"combat-ended");}
    public void clearQuiet(Player player){if(player!=null){tagged.remove(player.getUniqueId());damage.remove(player.getUniqueId());}}
    private void tick(){long now=System.currentTimeMillis();Iterator<Map.Entry<UUID,Tag>> it=tagged.entrySet().iterator();while(it.hasNext()){Map.Entry<UUID,Tag> e=it.next();if(e.getValue().until<=now){Player p=Bukkit.getPlayer(e.getKey());it.remove();if(p!=null)messages.send(p,"combat-ended");}}Iterator<Map.Entry<UUID,Map<UUID,Long>>> dit=damage.entrySet().iterator();long cutoff=now-config.assistWindowSeconds()*1000L;while(dit.hasNext()){Map<UUID,Long> m=dit.next().getValue();Iterator<Map.Entry<UUID,Long>> mi=m.entrySet().iterator();while(mi.hasNext())if(mi.next().getValue()<cutoff)mi.remove();if(m.isEmpty())dit.remove();}}
}
