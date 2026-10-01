package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class KillEffectService {
    private final KitPvPCore plugin; private final File file; private YamlConfiguration yaml; private final Map<String,Definition> effects=new LinkedHashMap<String,Definition>();
    private static final class Definition { String display,effect; int data; Definition(String display,String effect,int data){this.display=display;this.effect=effect;this.data=data;} }
    public KillEffectService(KitPvPCore plugin){this.plugin=plugin;this.file=new File(plugin.getDataFolder(),"cosmetics.yml");reload();}
    public void reload(){yaml=YamlConfiguration.loadConfiguration(file);effects.clear();if(yaml.isConfigurationSection("kill-effects"))for(String id:yaml.getConfigurationSection("kill-effects").getKeys(false)) { String base="kill-effects."+id; effects.put(id.toLowerCase(),new Definition(yaml.getString(base+".display",id),yaml.getString(base+".effect","NONE").toUpperCase(Locale.ENGLISH),yaml.getInt(base+".data",0))); }}
    public boolean exists(String id){return id!=null&&effects.containsKey(id.toLowerCase());}
    public String display(String id){Definition d=effects.get(id==null?"none":id.toLowerCase());return d==null?"":org.bukkit.ChatColor.translateAlternateColorCodes('&',d.display);}
    public String display(Player player){return display(plugin.getPlayers().get(player).getSelectedKillEffect());}
    public List<String> ids(){return Collections.unmodifiableList(new ArrayList<String>(effects.keySet()));}
    public boolean unlock(PlayerProfile p,String id){id=id.toLowerCase();if(!exists(id))return false;p.getOwnedKillEffects().add(id);return true;}
    public boolean select(Player p,String id){PlayerProfile profile=plugin.getPlayers().get(p);id=id.toLowerCase();if(!exists(id))return false;if(!profile.getOwnedKillEffects().contains(id)&&!p.hasPermission("kitpvp.killeffect.*"))return false;profile.setSelectedKillEffect(id);plugin.getPlayers().saveProfile(profile);return true;}
    public void play(Player killer,Player victim){if(!plugin.getPluginConfig().killEffectsEnabled()||killer==null||victim==null)return;PlayerProfile p=plugin.getPlayers().get(killer);Definition d=effects.get(p.getSelectedKillEffect().toLowerCase());if(d==null||"NONE".equals(d.effect))return;Location loc=victim.getLocation();World world=loc.getWorld();if(world==null)return;
        if("LIGHTNING".equals(d.effect)){world.strikeLightningEffect(loc);return;}
        try { Effect effect=Effect.valueOf(d.effect); world.playEffect(loc,effect,d.data); } catch (IllegalArgumentException ignored) { }
    }
}
