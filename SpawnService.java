package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;

public final class SpawnService {
    private final KitPvPCore plugin;
    private final MessageService messages;
    public SpawnService(KitPvPCore plugin, MessageService messages) { this.plugin = plugin; this.messages = messages; }

    public Location getSpawn() {
        FileConfiguration c = plugin.getConfig();
        World world = Bukkit.getWorld(c.getString("spawn.world", "kitpvp"));
        if (world == null) return null;
        return new Location(world, c.getDouble("spawn.x", 0.5D), c.getDouble("spawn.y", 100.0D),
                c.getDouble("spawn.z", 0.5D), (float)c.getDouble("spawn.yaw", 0.0D), (float)c.getDouble("spawn.pitch", 0.0D));
    }

    public boolean teleport(Player player) {
        Location spawn = getSpawn();
        if (spawn == null) { messages.send(player, "no-spawn"); return false; }
        player.teleport(spawn); messages.send(player, "spawned"); return true;
    }

    public void setSpawn(Player player) {
        Location l = player.getLocation();
        FileConfiguration c = plugin.getConfig();
        c.set("spawn.world", l.getWorld().getName()); c.set("spawn.x", l.getX()); c.set("spawn.y", l.getY()); c.set("spawn.z", l.getZ());
        c.set("spawn.yaw", l.getYaw()); c.set("spawn.pitch", l.getPitch());
        plugin.saveConfig(); messages.send(player, "admin.setspawn");
    }
}
