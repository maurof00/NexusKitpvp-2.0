package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import it.maurof00.xeonkitpvp.KitPvPCore;
import org.bukkit.entity.Player;

@CommandAlias("shop|store|negozio")
public final class ShopCommand extends BaseCommand {
    private final KitPvPCore plugin; public ShopCommand(KitPvPCore plugin){this.plugin=plugin;}
    @Default public void execute(Player player){if(!plugin.getShop().enabled()){plugin.getMessages().send(player,"shop.disabled");return;}player.openInventory(plugin.getShop().createInventory(player));}
}
