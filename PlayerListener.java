package it.maurof00.xeonkitpvp.listener;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.KitDefinition;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PlayerListener implements Listener {
    private final KitPvPCore plugin; public PlayerListener(KitPvPCore plugin){this.plugin=plugin;}
    @EventHandler public void onJoin(PlayerJoinEvent event){final Player player=event.getPlayer();plugin.getPlayers().load(player);PlayerProfile p=plugin.getPlayers().get(player);plugin.getQuests().sync(p);Bukkit.getScheduler().runTask(plugin,()->{if(!player.isOnline())return;if(plugin.getPluginConfig().isKitPvPWorld(player.getWorld().getName())){KitDefinition kit=plugin.getKits().get(p.getSelectedKit());if(kit==null)kit=plugin.getKits().get(plugin.getPluginConfig().joinKit());if(kit!=null)plugin.getKits().give(player,kit);}plugin.getScoreboard().update(player);plugin.getTablist().update(player);});}
    @EventHandler public void onQuit(PlayerQuitEvent event){Player player=event.getPlayer();if(plugin.getCombat().isTagged(player)&&plugin.getPluginConfig().combatLogoutPenalty()&&plugin.getPluginConfig().combatLogKillAttacker()){java.util.UUID opponentId=plugin.getCombat().getOpponent(player);Player opponent=opponentId==null?null:Bukkit.getPlayer(opponentId);plugin.getKills().handleCombatLogout(player,opponent);}else plugin.getCombat().clearQuiet(player);plugin.getScoreboard().remove(player);plugin.getPlayers().unload(player,plugin.getPluginConfig().autoSaveOnQuit());}
    @EventHandler public void onRespawn(final PlayerRespawnEvent event){final Player player=event.getPlayer();if(!plugin.getPluginConfig().isKitPvPWorld(player.getWorld().getName()))return;Location spawn=plugin.getSpawn().getSpawn();if(spawn!=null)event.setRespawnLocation(spawn);Bukkit.getScheduler().runTaskLater(plugin,()->{if(!player.isOnline())return;KitDefinition kit=plugin.getKits().get(plugin.getPlayers().get(player).getSelectedKit());if(kit!=null)plugin.getKits().give(player,kit);plugin.getScoreboard().update(player);plugin.getTablist().update(player);},plugin.getPluginConfig().respawnDelayTicks());}
    @EventHandler public void onTeleport(PlayerTeleportEvent event){if(event.getTo()==null)return;boolean oldWorld=plugin.getPluginConfig().isKitPvPWorld(event.getFrom().getWorld().getName());boolean newWorld=plugin.getPluginConfig().isKitPvPWorld(event.getTo().getWorld().getName());if(!newWorld&&oldWorld)plugin.getCombat().clear(event.getPlayer());if(newWorld&& !oldWorld)plugin.getCombat().clear(event.getPlayer());if(newWorld){final Player player=event.getPlayer();Bukkit.getScheduler().runTaskLater(plugin,()->{plugin.getScoreboard().update(player);plugin.getTablist().update(player);},1L);}}
}
