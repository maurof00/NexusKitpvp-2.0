package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import it.maurof00.xeonkitpvp.KitPvPCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("kitpvp|kp|kpcore")
public final class KitPvPCommand extends BaseCommand {
    private final KitPvPCore plugin;
    public KitPvPCommand(KitPvPCore plugin) { this.plugin = plugin; }

    @Default
    public void help(CommandSender sender) {
        for (String line : plugin.getMessages().list("help")) sender.sendMessage(line);
        if (sender.hasPermission("kitpvp.admin")) sender.sendMessage("&b&lADMIN &7-> &f/kitpvp admin help");
    }

    @Subcommand("reload")
    @CommandPermission("kitpvp.admin")
    public void reload(CommandSender sender) { plugin.reloadAll(); plugin.getMessages().send(sender, "reload"); }

    @Subcommand("admin")
    public void adminHelp(CommandSender sender) {
        sender.sendMessage(plugin.getMessages().prefix()+"&b&lXeonKitPvP Admin &7- &f/kitpvp admin help");
    }

    @Subcommand("setspawn")
    @CommandPermission("kitpvp.admin")
    public void setSpawn(Player sender) { plugin.getSpawn().setSpawn(sender); }
}
