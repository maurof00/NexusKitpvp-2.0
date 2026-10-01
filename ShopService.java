package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import it.maurof00.xeonkitpvp.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ShopService {
    public static final class ShopItem {
        public final String key,type,id,name; public final int slot; public final long price; public final ItemStack icon;
        ShopItem(String key,String type,String id,String name,int slot,long price,ItemStack icon){this.key=key;this.type=type;this.id=id;this.name=name;this.slot=slot;this.price=price;this.icon=icon;}
    }
    private final KitPvPCore plugin; private final File file; private YamlConfiguration yaml; private final Map<Integer,ShopItem> bySlot=new LinkedHashMap<Integer,ShopItem>();
    public ShopService(KitPvPCore plugin){this.plugin=plugin;this.file=new File(plugin.getDataFolder(),"shop.yml");reload();}
    public void reload(){yaml=YamlConfiguration.loadConfiguration(file);bySlot.clear();ConfigurationSection s=yaml.getConfigurationSection("shop.items");if(s==null)return;for(String key:s.getKeys(false)){String base="shop.items."+key;Material mat=ItemUtil.match(yaml.getString(base+".material","CHEST"));if(mat==null)continue;ItemStack item=new ItemStack(mat,Math.max(1,yaml.getInt(base+".amount",1)));ItemMeta meta=item.getItemMeta();String name=ColorUtil.color(yaml.getString(base+".name",key));if(meta!=null){meta.setDisplayName(name);List<String> lore=yaml.getStringList(base+".lore");List<String> out=new ArrayList<String>();for(String line:lore)out.add(ColorUtil.color(line.replace("%price%",String.valueOf(yaml.getLong(base+".price",0L)))));meta.setLore(out);item.setItemMeta(meta);}ShopItem si=new ShopItem(key,yaml.getString(base+".type","").toUpperCase(),yaml.getString(base+".id",key),name,yaml.getInt(base+".slot",0),Math.max(0L,yaml.getLong(base+".price",0L)),item);bySlot.put(si.slot,si);}}
    public boolean enabled(){return yaml.getBoolean("shop.enabled",true)&&plugin.getPluginConfig().shopEnabled();}
    public String title(){return ColorUtil.color(yaml.getString("shop.title","&8&lXeon Shop"));}
    public Inventory createInventory(Player player){int size=Math.max(9,Math.min(54,yaml.getInt("shop.size",54)));Inventory inv=Bukkit.createInventory(null,size,title());for(ShopItem si:bySlot.values()){if(si.slot>=size)continue;ItemStack item=si.icon.clone();ItemMeta meta=item.getItemMeta();if(meta!=null){List<String> lore=meta.getLore()==null?new ArrayList<String>():new ArrayList<String>(meta.getLore());PlayerProfile p=plugin.getPlayers().get(player);boolean owned=owns(p,si);lore.add(owned?ChatColor.GREEN+"Posseduto / selezionabile":ChatColor.YELLOW+"Clicca per acquistare");meta.setLore(lore);item.setItemMeta(meta);}inv.setItem(si.slot,item);}return inv;}
    public ShopItem bySlot(int slot){return bySlot.get(slot);}
    public boolean purchaseOrSelect(Player player,ShopItem item){if(item==null)return false;PlayerProfile p=plugin.getPlayers().get(player);if(owns(p,item)){if("TAG".equals(item.type)) {plugin.getTags().select(player,item.id);plugin.getMessages().send(player,"shop.selected",Collections.singletonMap("item",item.name));} else if("KILL_EFFECT".equals(item.type)){plugin.getKillEffects().select(player,item.id);plugin.getMessages().send(player,"shop.selected",Collections.singletonMap("item",item.name));} return true;}if(!plugin.getEconomy().take(player,item.price)) {plugin.getMessages().send(player,"not-enough-coins");return false;}boolean ok=false;if("TAG".equals(item.type))ok=plugin.getTags().unlock(p,item.id);else if("KILL_EFFECT".equals(item.type))ok=plugin.getKillEffects().unlock(p,item.id);if(!ok){p.addCoins(item.price);plugin.getMessages().send(player,"shop.invalid");return false;}plugin.getPlayers().saveProfile(p);plugin.getMessages().send(player,"shop.purchased",map(item.name,String.valueOf(item.price)));return true;}
    private boolean owns(PlayerProfile p,ShopItem item){if("TAG".equals(item.type))return p.getOwnedTags().contains(item.id.toLowerCase());if("KILL_EFFECT".equals(item.type))return p.getOwnedKillEffects().contains(item.id.toLowerCase());return false;}
    private Map<String,String> map(String a,String b){Map<String,String> m=new java.util.HashMap<String,String>();m.put("item",a);m.put("price",b);return m;}
}
