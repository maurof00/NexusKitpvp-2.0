package it.maurof00.xeonkitpvp.listener;

import it.maurof00.xeonkitpvp.KitPvPCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Locale;

public final class CombatCommandListener implements Listener {
    private final KitPvPCore plugin; public CombatCommandListener(KitPvPCore plugin){this.plugin=plugin;}
    @EventHandler(ignoreCancelled=true) public void onCommand(PlayerCommandPreprocessEvent event){if(!plugin.getPluginConfig().combatLogBlockCommands()||!plugin.getCombat().isTagged(event.getPlayer())||event.getPlayer().hasPermission("kitpvp.bypass"))return;String raw=event.getMessage().trim().toLowerCase(Locale.ENGLISH);for(String configured:plugin.getPluginConfig().combatBlockedCommands()){String cmd=configured.trim().toLowerCase(Locale.ENGLISH);if(cmd.isEmpty())continue;if(cmd.charAt(0)!='/')cmd='/'+cmd;if(raw.equals(cmd)||raw.startsWith(cmd+" ")){event.setCancelled(true);plugin.getMessages().send(event.getPlayer(),"combat-command-blocked");return;}}}
}
