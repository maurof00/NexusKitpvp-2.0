package it.maurof00.xeonkitpvp.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class MessageService {
    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yaml;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "lang.yml");
        reload();
    }

    public void reload() { yaml = YamlConfiguration.loadConfiguration(file); }

    public String get(String key) { return color(yaml.getString("messages." + key, key)); }
    public String get(String key, Map<String, String> placeholders) {
        String message = get(key);
        if (placeholders != null) for (Map.Entry<String, String> e : placeholders.entrySet()) message = message.replace("%" + e.getKey() + "%", e.getValue());
        return message;
    }
    public List<String> list(String key) {
        List<String> list = yaml.getStringList("messages." + key);
        for (int i = 0; i < list.size(); i++) list.set(i, color(list.get(i)));
        return list;
    }
    public String prefix() { return get("prefix"); }
    public void send(CommandSender sender, String key) { sender.sendMessage(prefix() + get(key)); }
    public void send(CommandSender sender, String key, Map<String, String> placeholders) { sender.sendMessage(prefix() + get(key, placeholders)); }
    public void broadcast(String key, Map<String, String> placeholders) { plugin.getServer().broadcastMessage(prefix() + get(key, placeholders)); }

    private String color(String s) { return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s); }
}
