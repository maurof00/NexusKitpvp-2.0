package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.entity.Player;

@CommandAlias("level|lvl|prestige")
public final class LevelCommand extends BaseCommand {
    private final KitPvPCore plugin; public LevelCommand(KitPvPCore plugin){this.plugin=plugin;}
    @Default public void execute(Player player){PlayerProfile p=plugin.getPlayers().get(player);java.util.Map<String,String>m=new java.util.HashMap<String,String>();m.put("player",player.getName());m.put("prestige",String.valueOf(p.getPrestige()));m.put("level",String.valueOf(p.getLevel()));m.put("xp",String.valueOf(p.getXp()));m.put("nextxp",String.valueOf(plugin.getLevels().requiredXp(p)));player.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("level.header",m));player.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("level.line",m));if(p.getLevel()>=plugin.getPluginConfig().maxLevel())player.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("level.max"));}
}
