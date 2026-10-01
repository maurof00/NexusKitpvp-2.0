package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.Gang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("gang|clan")
public final class GangCommand extends BaseCommand {
    private final KitPvPCore plugin;
    public GangCommand(KitPvPCore plugin) { this.plugin = plugin; }
    @Default public void help(Player p) { p.sendMessage(plugin.getMessages().prefix()+"&f/gang create <name> &7| &finvite <player> &7| &fjoin <name> &7| &fleave &7| &finfo &7| &fdisband"); }
    @Subcommand("create") public void create(Player p, String name) { plugin.getGangs().create(p, name); }
    @Subcommand("invite") @CommandCompletion("@players") public void invite(Player p, String targetName) { Player target=Bukkit.getPlayerExact(targetName); if(target==null){plugin.getMessages().send(p,"player-not-found");return;} plugin.getGangs().invite(p,target); }
    @Subcommand("join") public void join(Player p, String name) { plugin.getGangs().join(p,name); }
    @Subcommand("leave") public void leave(Player p) { plugin.getGangs().leave(p); }
    @Subcommand("disband") public void disband(Player p) { plugin.getGangs().disband(p); }
    @Subcommand("info") public void info(Player p) { Gang g=plugin.getGangs().byPlayer(p.getUniqueId()); if(g==null){plugin.getMessages().send(p,"gang-not-in");return;} p.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("gang-info-header",java.util.Collections.singletonMap("gang",g.getName()))); p.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("gang-info-leader",java.util.Collections.singletonMap("leader",name(g.getLeader())))); p.sendMessage(plugin.getMessages().prefix()+plugin.getMessages().get("gang-info-members",java.util.Collections.singletonMap("members",String.valueOf(g.getMembers().size())))); }
    private String name(java.util.UUID uuid){ Player p=Bukkit.getPlayer(uuid); return p==null?uuid.toString():p.getName(); }
}
