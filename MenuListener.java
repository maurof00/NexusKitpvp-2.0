package it.maurof00.xeonkitpvp.listener;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.KitDefinition;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class MenuListener implements Listener {
    private final KitPvPCore plugin; public MenuListener(KitPvPCore plugin){this.plugin=plugin;}
    @EventHandler(ignoreCancelled=true) public void onClick(InventoryClickEvent event){if(!(event.getWhoClicked() instanceof Player))return;Player player=(Player)event.getWhoClicked();String title=ColorUtil.strip(event.getView().getTitle());
        if(title.equalsIgnoreCase("KitPvP Kits")){event.setCancelled(true);if(event.getCurrentItem()==null||event.getCurrentItem().getItemMeta()==null)return;String name=event.getCurrentItem().getItemMeta().getDisplayName();if(name==null)return;for(KitDefinition kit:plugin.getKits().all())if(ColorUtil.strip(kit.getDisplayName()).equalsIgnoreCase(ColorUtil.strip(name))){if(plugin.getCombat().isTagged(player)&&!player.hasPermission("kitpvp.bypass")){plugin.getMessages().send(player,"kit-in-combat");return;}if(plugin.getKits().select(player,kit.getId()))player.closeInventory();return;}}
        else if(title.equalsIgnoreCase(ColorUtil.strip(plugin.getShop().title()))){event.setCancelled(true);if(event.getClickedInventory()==null||event.getClickedInventory()!=event.getView().getTopInventory())return;ShopClick(player,event.getSlot());}
    }
    private void ShopClick(Player player,int slot){it.maurof00.xeonkitpvp.service.ShopService.ShopItem item=plugin.getShop().bySlot(slot);if(item==null)return;plugin.getShop().purchaseOrSelect(player,item);if(plugin.getPluginConfig().shopUpdateOnPurchase())player.getOpenInventory().getTopInventory().setContents(plugin.getShop().createInventory(player).getContents());}
    @EventHandler(ignoreCancelled=true) public void onDrag(InventoryDragEvent event){String title=ColorUtil.strip(event.getView().getTitle());if(title.equalsIgnoreCase("KitPvP Kits")||title.equalsIgnoreCase(ColorUtil.strip(plugin.getShop().title())))event.setCancelled(true);}
}
