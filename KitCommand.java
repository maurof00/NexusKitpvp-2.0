package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.KitDefinition;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import it.maurof00.xeonkitpvp.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("kit|kits")
public final class KitCommand extends BaseCommand {
    private final KitPvPCore plugin;
    public KitCommand(KitPvPCore plugin) { this.plugin = plugin; }

    @Default
    public void open(Player player) { openMenu(player); }

    @CommandCompletion("@kits")
    public void select(Player player, String kit) {
        if (plugin.getCombat().isTagged(player) && !player.hasPermission("kitpvp.bypass")) {
            plugin.getMessages().send(player, "kit-in-combat"); return;
        }
        plugin.getKits().select(player, kit);
    }

    public void openMenu(Player player) {
        List<KitDefinition> kits = new ArrayList<KitDefinition>(plugin.getKits().all());
        if (kits.isEmpty()) { plugin.getMessages().send(player, "kit-none"); return; }
        int size = Math.min(54, Math.max(9, ((kits.size() + 8) / 9) * 9));
        Inventory inv = Bukkit.createInventory(null, size, ColorUtil.color("&8&lKitPvP Kits"));
        PlayerProfile profile = plugin.getPlayers().get(player);
        for (int i = 0; i < kits.size() && i < size; i++) {
            KitDefinition kit = kits.get(i);
            ItemStack item = kit.getIcon() == null ? ItemUtil.parse("CHEST") : kit.getIcon().clone();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName(kit.getDisplayName());
            List<String> lore = new ArrayList<String>();
            boolean owned = profile.getOwnedKits().contains(kit.getId()) || player.hasPermission("kitpvp.kit.*") ||
                    (!kit.getPermission().isEmpty() && player.hasPermission(kit.getPermission()));
            lore.add(ChatColor.GRAY + "Prezzo: " + ChatColor.GOLD + kit.getPrice());
            lore.add(owned ? ChatColor.GREEN + "Sbloccato" : ChatColor.RED + "Clicca per acquistare");
            if (kit.getAbility() != null) lore.add(ChatColor.AQUA + "Ability: " + ChatColor.WHITE + ChatColor.stripColor(kit.getAbility().getDisplayName()));
            meta.setLore(lore); item.setItemMeta(meta); inv.setItem(i, item);
        }
        player.openInventory(inv);
    }
}
