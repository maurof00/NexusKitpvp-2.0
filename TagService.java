package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TagService {
    private final KitPvPCore plugin; private final File file; private YamlConfiguration yaml; private final Map<String,String> tags=new LinkedHashMap<String,String>();
    public TagService(KitPvPCore plugin){this.plugin=plugin;this.file=new File(plugin.getDataFolder(),"cosmetics.yml");reload();}
    public void reload(){yaml=YamlConfiguration.loadConfiguration(file);tags.clear();if(yaml.isConfigurationSection("tags"))for(String id:yaml.getConfigurationSection("tags").getKeys(false))tags.put(id.toLowerCase(),ColorUtil.color(yaml.getString("tags."+id+".display",id)));}
    public boolean exists(String id){return id!=null&&tags.containsKey(id.toLowerCase());}
    public String display(String id){String v=tags.get(id==null?"default":id.toLowerCase());return v==null?"":v;}
    public String display(Player player){return display(plugin.getPlayers().get(player).getSelectedTag());}
    public boolean unlock(PlayerProfile p,String id){id=id.toLowerCase();if(!exists(id))return false;p.getOwnedTags().add(id);return true;}
    public boolean select(Player player,String id){PlayerProfile p=plugin.getPlayers().get(player);id=id.toLowerCase();if(!exists(id))return false;if(!p.getOwnedTags().contains(id)&&!player.hasPermission("kitpvp.tag.*"))return false;p.setSelectedTag(id);plugin.getPlayers().saveProfile(p);return true;}
    public List<String> ids(){return Collections.unmodifiableList(new ArrayList<String>(tags.keySet()));}
}
