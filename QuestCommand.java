package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.model.QuestDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("quests|quest|missioni")
public final class QuestCommand extends BaseCommand {
    private final KitPvPCore plugin;
    public QuestCommand(KitPvPCore plugin){this.plugin=plugin;}
    @Default public void execute(Player player){PlayerProfile p=plugin.getPlayers().get(player);plugin.getQuests().sync(p);player.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("quest.header"));sendPeriod(player,p,QuestDefinition.Period.DAILY);sendPeriod(player,p,QuestDefinition.Period.WEEKLY);}
    private void sendPeriod(Player player,PlayerProfile p,QuestDefinition.Period period){player.sendMessage(plugin.getMessages().prefix()+"&7--- "+period.name()+" ---");boolean any=false;for(QuestDefinition q:plugin.getQuests().byPeriod(period)){any=true;String key=q.key();long progress=p.getQuestProgress().containsKey(key)?p.getQuestProgress().get(key):0L;boolean done=p.getCompletedQuests().contains(key);java.util.Map<String,String> m=new java.util.HashMap<String,String>();m.put("status",done?plugin.getMessages().get("quest.completed"):plugin.getMessages().get("quest.active"));m.put("display",q.getDisplay());m.put("progress",String.valueOf(progress));m.put("target",String.valueOf(q.getTarget()));m.put("coins",String.valueOf(q.getRewardCoins()));m.put("xp",String.valueOf(q.getRewardXp()));player.sendMessage(plugin.getMessages().prefix()+replace(plugin.getMessages().get("quest.entry"),m));player.sendMessage("  "+ChatColor.GRAY+q.getDescription());}if(!any)player.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("quest.none"));}
    private String replace(String s,java.util.Map<String,String>m){for(java.util.Map.Entry<String,String>e:m.entrySet())s=s.replace("%"+e.getKey()+"%",e.getValue());return s;}
}
