package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.service.LeaderboardService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandAlias("top|leaderboard|lb")
public final class TopCommand extends BaseCommand {
    private final KitPvPCore plugin;
    private final LeaderboardService leaderboard;
    public TopCommand(KitPvPCore plugin, LeaderboardService leaderboard) { this.plugin = plugin; this.leaderboard = leaderboard; }

    @Default
    @CommandCompletion("kills|coins|streak|level|xp|prestige")
    public void execute(Player player, @Optional String stat, @Optional Integer page) {
        String actual = stat == null ? "kills" : stat;
        int actualPage = page == null || page < 1 ? 1 : page;
        List<PlayerProfile> top = leaderboard.top(actual);
        if (top.isEmpty()) { player.sendMessage(plugin.getMessages().prefix() + "&cNessun dato disponibile."); return; }
        int start = (actualPage - 1) * 10;
        if (start >= top.size()) { player.sendMessage(plugin.getMessages().prefix()+"&cPagina non disponibile."); return; }
        Map<String,String> base = new HashMap<String,String>(); base.put("type", actual); base.put("page", String.valueOf(actualPage));
        player.sendMessage(plugin.getMessages().prefix() + plugin.getMessages().get("top-header", base));
        int rank = start + 1;
        int printed = 0;
        for (PlayerProfile p : top) {
            if (rank <= start) { rank++; continue; }
            if (printed >= 10) break;
            long value = value(p, actual);
            Map<String,String> m = new HashMap<String,String>(); m.put("rank", String.valueOf(rank)); m.put("player", p.getLastName()); m.put("value", String.valueOf(value));
            player.sendMessage(plugin.getMessages().prefix() + plugin.getMessages().get("top-line", m)); rank++; printed++;
        }
    }
    private long value(PlayerProfile p, String stat) {
        if ("coins".equalsIgnoreCase(stat)) return p.getCoins();
        if ("streak".equalsIgnoreCase(stat)) return p.getBestStreak();
        if ("level".equalsIgnoreCase(stat)) return p.getLevel();
        if ("xp".equalsIgnoreCase(stat)) return p.getXp();
        if ("prestige".equalsIgnoreCase(stat)) return p.getPrestige();
        return p.getKills();
    }
}
