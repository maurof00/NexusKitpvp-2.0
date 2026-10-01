package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.entity.Player;

@CommandAlias("bounty|taglia")
public final class BountyCommand extends BaseCommand {
    private final KitPvPCore plugin;
    public BountyCommand(KitPvPCore plugin) { this.plugin = plugin; }

    @Default
    @CommandCompletion("@players")
    public void execute(Player player, @Optional String targetName, @Optional Long amount) {
        if (targetName == null) {
            PlayerProfile own = plugin.getPlayers().get(player);
            player.sendMessage(plugin.getMessages().prefix()+"&6La tua taglia: &e"+own.getBounty()+" coin");
            return;
        }
        Player target = plugin.getServer().getPlayerExact(targetName);
        if (target == null) { plugin.getMessages().send(player, "player-not-found"); return; }
        if (amount == null) {
            PlayerProfile p = plugin.getPlayers().get(target);
            plugin.getMessages().send(player, "bounty-info", java.util.Collections.singletonMap("player", target.getName()));
            player.sendMessage(plugin.getMessages().prefix() + "&6Taglia: &e" + p.getBounty()); return;
        }
        if (!plugin.getBounties().set(player, target, amount)) {
            if (amount < plugin.getPluginConfig().bountyMin() || amount > plugin.getPluginConfig().bountyMax()) plugin.getMessages().send(player, "bounty-invalid");
            else plugin.getMessages().send(player, "not-enough-coins");
        }
    }
}
