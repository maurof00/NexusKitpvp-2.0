package it.maurof00.xeonkitpvp.ui;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScoreboardService {
    private final KitPvPCore plugin;
    private final Map<Player, Scoreboard> scoreboards = new HashMap<Player, Scoreboard>();
    private int taskId = -1;

    public ScoreboardService(KitPvPCore plugin) { this.plugin = plugin; }

    public void start() {
        if (!plugin.getPluginConfig().scoreboardEnabled()) return;
        stop();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override public void run() { updateAll(); }
        }, 1L, plugin.getPluginConfig().scoreboardRefreshTicks());
    }

    public void stop() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1; scoreboards.clear();
    }

    public void remove(Player player) { scoreboards.remove(player); }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) update(player);
    }

    public void update(Player player) {
        if (player == null || !plugin.getPluginConfig().scoreboardEnabled()) return;
        if (!plugin.getPluginConfig().isKitPvPWorld(player.getWorld().getName())) return;
        Scoreboard board = scoreboards.get(player);
        if (board == null) { board = Bukkit.getScoreboardManager().getNewScoreboard(); scoreboards.put(player, board); player.setScoreboard(board); }
        Objective objective = board.getObjective("xeon");
        if (objective != null) objective.unregister();
        objective = board.registerNewObjective("xeon", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ColorUtil.color(plugin.getPluginConfig().scoreboardTitle()));
        PlayerProfile p = plugin.getPlayers().get(player);
        List<String> raw = plugin.getPluginConfig().scoreboardLines();
        List<String> lines = new ArrayList<String>();
        for (String line : raw) lines.add(plugin.replacePlaceholders(line, player, p));
        // Bukkit scoreboards require unique entry strings. Invisible suffixes keep duplicates valid on 1.8.
        for (int i = 0; i < lines.size() && i < 15; i++) {
            String line = lines.get(i);
            String unique = line;
            while (contains(board, unique)) unique += org.bukkit.ChatColor.values()[i % org.bukkit.ChatColor.values().length].toString();
            objective.getScore(unique).setScore(lines.size() - i);
        }
    }

    private boolean contains(Scoreboard board, String line) {
        for (String entry : board.getEntries()) if (entry.equals(line)) return true;
        return false;
    }
}
