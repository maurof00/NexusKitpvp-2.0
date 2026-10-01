package it.maurof00.xeonkitpvp.listener;

import it.maurof00.xeonkitpvp.KitPvPCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class WorldProtectionListener implements Listener {
    private final KitPvPCore plugin;
    public WorldProtectionListener(KitPvPCore plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player p=(Player)event.getEntity(); if (!plugin.getPluginConfig().isKitPvPWorld(p.getWorld().getName())) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && plugin.getCombat().remainingSeconds(p) == 0) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        // Reserved extension point for NPCs, shops and server-selector items.
    }
}
