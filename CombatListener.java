package it.maurof00.xeonkitpvp.listener;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.KitDefinition;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CombatListener implements Listener {
    private final KitPvPCore plugin; public CombatListener(KitPvPCore plugin){this.plugin=plugin;}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true) public void onDamage(EntityDamageByEntityEvent event){if(!(event.getEntity() instanceof Player))return;Player victim=(Player)event.getEntity();if(!plugin.getPluginConfig().isKitPvPWorld(victim.getWorld().getName()))return;Player damager=resolveDamager(event.getDamager());if(damager==null)return;if(!plugin.getPluginConfig().pvpEnabled()){event.setCancelled(true);return;}if(!plugin.getGangs().canDamage(damager,victim)){event.setCancelled(true);return;}plugin.getCombat().recordDamage(victim,damager);}
    private Player resolveDamager(Entity entity){if(entity instanceof Player)return(Player)entity;if(entity instanceof Projectile){Object shooter=((Projectile)entity).getShooter();return shooter instanceof Player?(Player)shooter:null;}return null;}
    @EventHandler(ignoreCancelled=true) public void onDeath(PlayerDeathEvent event){Player victim=event.getEntity();if(!plugin.getPluginConfig().isKitPvPWorld(victim.getWorld().getName()))return;if(plugin.getPluginConfig().noDrops())event.getDrops().clear();plugin.getKills().handleDeath(victim,victim.getKiller());}
    @EventHandler(ignoreCancelled=true) public void onBuild(BlockPlaceEvent event){if(plugin.getPluginConfig().isKitPvPWorld(event.getBlock().getWorld().getName())&&plugin.getPluginConfig().blockBuild()&&!event.getPlayer().hasPermission("kitpvp.bypass"))event.setCancelled(true);}
    @EventHandler(ignoreCancelled=true) public void onBreak(BlockBreakEvent event){if(plugin.getPluginConfig().isKitPvPWorld(event.getBlock().getWorld().getName())&&plugin.getPluginConfig().blockBreak()&&!event.getPlayer().hasPermission("kitpvp.bypass"))event.setCancelled(true);}
    @EventHandler(ignoreCancelled=true) public void onHunger(FoodLevelChangeEvent event){if(!(event.getEntity() instanceof Player))return;Player p=(Player)event.getEntity();if(plugin.getPluginConfig().isKitPvPWorld(p.getWorld().getName())&&plugin.getPluginConfig().noHunger())event.setCancelled(true);}
    @EventHandler(ignoreCancelled=true) public void onSoup(PlayerInteractEvent event){if(!plugin.getPluginConfig().isKitPvPWorld(event.getPlayer().getWorld().getName()))return;if(event.getAction()!=Action.RIGHT_CLICK_AIR&&event.getAction()!=Action.RIGHT_CLICK_BLOCK)return;ItemStack item=event.getItem();if(item==null||item.getType()!=Material.MUSHROOM_SOUP)return;Player p=event.getPlayer();double heal=plugin.getPluginConfig().soupHealHalfHearts();if(p.getHealth()<p.getMaxHealth()){p.setHealth(Math.min(p.getMaxHealth(),p.getHealth()+heal));item.setType(Material.BOWL);event.setCancelled(true);p.updateInventory();}}
    @EventHandler(ignoreCancelled=true) public void onAbility(PlayerInteractEvent event){if(event.getItem()==null||(event.getAction()!=Action.RIGHT_CLICK_AIR&&event.getAction()!=Action.RIGHT_CLICK_BLOCK))return;Player p=event.getPlayer();if(!plugin.getPluginConfig().isKitPvPWorld(p.getWorld().getName()))return;KitDefinition kit=plugin.getKits().get(plugin.getPlayers().get(p).getSelectedKit());if(kit==null||kit.getAbility()==null)return;ItemMeta meta=event.getItem().getItemMeta();if(meta==null||meta.getDisplayName()==null)return;if(!meta.getDisplayName().equals(kit.getAbility().getDisplayName()))return;if(plugin.getKits().useAbility(p,kit))event.setCancelled(true);}
    @EventHandler(ignoreCancelled=true) public void onMove(PlayerMoveEvent event){if(!plugin.getPluginConfig().isKitPvPWorld(event.getPlayer().getWorld().getName()))return;if(event.getTo().getY()<=0&&plugin.getPluginConfig().preventVoidDeath()){plugin.getSpawn().teleport(event.getPlayer());event.getPlayer().setHealth(event.getPlayer().getMaxHealth());}}
}
