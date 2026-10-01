package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.NumberUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("stats|stat")
public final class StatsCommand extends BaseCommand {
    private final KitPvPCore plugin; public StatsCommand(KitPvPCore plugin){this.plugin=plugin;}
    @Default @CommandCompletion("@players") public void execute(Player sender,@Optional String targetName){PlayerProfile profile=targetName==null?plugin.getPlayers().get(sender):plugin.getPlayers().getByName(targetName);if(profile==null){plugin.getMessages().send(sender,"player-not-found");return;}send(sender,profile);}
    public void send(Player sender,PlayerProfile p){Map<String,String>m=new HashMap<String,String>();m.put("player",p.getLastName());m.put("kills",String.valueOf(p.getKills()));m.put("deaths",String.valueOf(p.getDeaths()));m.put("kd",NumberUtil.decimal(p.getKd()));m.put("streak",String.valueOf(p.getStreak()));m.put("beststreak",String.valueOf(p.getBestStreak()));m.put("prestige",String.valueOf(p.getPrestige()));m.put("level",String.valueOf(p.getLevel()));m.put("xp",String.valueOf(p.getXp()));m.put("nextxp",String.valueOf(plugin.getLevels().requiredXp(p)));m.put("coins",String.valueOf(p.getCoins()));m.put("bounty",String.valueOf(p.getBounty()));m.put("tag",plugin.getTags().display(p.getSelectedTag()));m.put("killeffect",plugin.getKillEffects().display(p.getSelectedKillEffect()));m.put("gang",p.getGang()==null?"-":p.getGang());sender.sendMessage(plugin.getMessages().prefix()+replace(plugin.getMessages().get("stats-header",m),m));for(String line:plugin.getMessages().list("stats-lines"))sender.sendMessage(plugin.getMessages().prefix()+replace(line,m));}
    private String replace(String s,Map<String,String>m){for(Map.Entry<String,String>e:m.entrySet())s=s.replace("%"+e.getKey()+"%",e.getValue());return s;}
}
