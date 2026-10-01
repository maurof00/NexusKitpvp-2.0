package it.maurof00.xeonkitpvp.ui;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class TablistService {
    private final KitPvPCore plugin;
    private int taskId = -1;
    public TablistService(KitPvPCore plugin) { this.plugin = plugin; }

    public void start() {
        stop();
        if (!plugin.getPluginConfig().tablistEnabled()) return;
        taskId = org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override public void run() { for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) update(p); }
        }, 1L, 40L);
    }
    public void stop() { if (taskId != -1) org.bukkit.Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }

    public void update(Player player) {
        if (!plugin.getPluginConfig().tablistEnabled()) return;
        if (plugin.getHooks().getTab() != null && plugin.getHooks().getTab().isAvailable()) {
            plugin.getHooks().getTab().update(player); return;
        }
        // Native fallback where Bukkit exposes Player#setPlayerListHeaderFooter. Reflection keeps compilation on 1.8.8.
        try {
            PlayerProfile p = plugin.getPlayers().get(player);
            String header = plugin.replacePlaceholders(plugin.getPluginConfig().tabHeader(), player, p);
            String footer = plugin.replacePlaceholders(plugin.getPluginConfig().tabFooter(), player, p);
            Method m = player.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class);
            m.invoke(player, ColorUtil.color(header), ColorUtil.color(footer));
        } catch (Throwable ignored) { }
    }
}
