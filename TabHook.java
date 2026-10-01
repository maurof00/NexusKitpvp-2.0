package it.maurof00.xeonkitpvp.hook;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/** Optional reflective integration so the core does not hard-depend on a TAB build that dropped old MC patches. */
public final class TabHook {
    private final KitPvPCore plugin;
    private boolean available;
    private Object tabApi;

    public TabHook(KitPvPCore plugin) { this.plugin = plugin; }

    public boolean enable() {
        if (plugin.getServer().getPluginManager().getPlugin("TAB") == null) return false;
        try {
            Class<?> apiClass;
            try { apiClass = Class.forName("me.neznamy.tab.api.TabAPI"); }
            catch (ClassNotFoundException e) { apiClass = Class.forName("me.neznamy.tab.api.TABAPI"); }
            Method getInstance = apiClass.getMethod("getInstance");
            tabApi = getInstance.invoke(null);
            available = tabApi != null;
        } catch (Throwable ignored) { available = false; }
        return available;
    }

    public boolean isAvailable() { return available; }

    public void update(Player player) {
        if (!available || player == null) return;
        try {
            PlayerProfile profile = plugin.getPlayers().get(player);
            String header = replace(plugin.getConfig().getString("tablist.header", ""), player, profile);
            String footer = replace(plugin.getConfig().getString("tablist.footer", ""), player, profile);
            Method hfManagerMethod = tabApi.getClass().getMethod("getHeaderFooterManager");
            Object manager = hfManagerMethod.invoke(tabApi);
            if (manager == null) return;
            Object tabPlayer = findTabPlayer(player);
            if (tabPlayer == null) return;
            Method combined = findCompatible(manager.getClass(), "setHeaderAndFooter", 3);
            if (combined != null) {
                combined.invoke(manager, tabPlayer, header, footer);
                return;
            }
            Method setHeader = findCompatible(manager.getClass(), "setHeader", 2);
            Method setFooter = findCompatible(manager.getClass(), "setFooter", 2);
            if (setHeader != null) setHeader.invoke(manager, tabPlayer, header);
            if (setFooter != null) setFooter.invoke(manager, tabPlayer, footer);
        } catch (Throwable ignored) { }
    }

    private Object findTabPlayer(Player player) throws Exception {
        Method getPlayer = null;
        for (Method m : tabApi.getClass().getMethods()) {
            if ("getPlayer".equals(m.getName()) && m.getParameterTypes().length == 1) { getPlayer = m; break; }
        }
        if (getPlayer == null) return null;
        Class<?> t = getPlayer.getParameterTypes()[0];
        if (t == java.util.UUID.class) return getPlayer.invoke(tabApi, player.getUniqueId());
        return getPlayer.invoke(tabApi, player.getName());
    }

    private Method findCompatible(Class<?> type, String name, int params) {
        for (Method m : type.getMethods()) if (name.equals(m.getName()) && m.getParameterTypes().length == params) return m;
        return null;
    }

    private String replace(String value, Player player, PlayerProfile p) {
        return ColorUtil.color(value.replace("%player%", player.getName())
                .replace("%coins%", String.valueOf(p.getCoins()))
                .replace("%kills%", String.valueOf(p.getKills()))
                .replace("%level%", String.valueOf(p.getLevel()))
                .replace("%prestige%", String.valueOf(p.getPrestige()))
                .replace("%tag%", plugin.getTags().display(p.getSelectedTag()))
                .replace("%killeffect%", plugin.getKillEffects().display(p.getSelectedKillEffect()))
                .replace("%online%", String.valueOf(plugin.getServer().getOnlinePlayers().size())));
    }
}
