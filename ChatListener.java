package it.maurof00.xeonkitpvp.listener;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatListener implements Listener {
    private final KitPvPCore plugin; public ChatListener(KitPvPCore plugin){this.plugin=plugin;}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true) public void onChat(AsyncPlayerChatEvent event){if(!plugin.getPluginConfig().chatTagsEnabled())return;Player p=event.getPlayer();String format=plugin.getPluginConfig().chatFormat().replace("%tag%",plugin.getTags().display(plugin.getPlayers().get(p).getSelectedTag())).replace("%player%",p.getName()).replace("%message%","%2$s");event.setFormat(ColorUtil.color(format));}
}
