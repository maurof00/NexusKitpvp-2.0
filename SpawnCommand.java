package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import it.maurof00.xeonkitpvp.KitPvPCore;
import org.bukkit.entity.Player;

@CommandAlias("spawn|hub")
public final class SpawnCommand extends BaseCommand {
    private final KitPvPCore plugin;
    public SpawnCommand(KitPvPCore plugin) { this.plugin = plugin; }
    @Default
    public void execute(Player player) {
        if (plugin.getCombat().isTagged(player) && !player.hasPermission("kitpvp.bypass")) { plugin.getMessages().send(player, "spawn-in-combat"); return; }
        plugin.getSpawn().teleport(player);
    }
}
