package it.maurof00.xeonkitpvp;

import co.aikar.commands.BukkitCommandManager;
import it.maurof00.xeonkitpvp.command.BountyCommand;
import it.maurof00.xeonkitpvp.command.GangCommand;
import it.maurof00.xeonkitpvp.command.KitCommand;
import it.maurof00.xeonkitpvp.command.KitPvPCommand;
import it.maurof00.xeonkitpvp.command.LevelCommand;
import it.maurof00.xeonkitpvp.command.QuestCommand;
import it.maurof00.xeonkitpvp.command.ShopCommand;
import it.maurof00.xeonkitpvp.command.SpawnCommand;
import it.maurof00.xeonkitpvp.command.StatsCommand;
import it.maurof00.xeonkitpvp.command.TagCommand;
import it.maurof00.xeonkitpvp.command.KillEffectCommand;
import it.maurof00.xeonkitpvp.command.TopCommand;
import it.maurof00.xeonkitpvp.command.admin.AdminCommand;
import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.hook.HookManager;
import it.maurof00.xeonkitpvp.listener.ChatListener;
import it.maurof00.xeonkitpvp.listener.CombatCommandListener;
import it.maurof00.xeonkitpvp.listener.CombatListener;
import it.maurof00.xeonkitpvp.listener.MenuListener;
import it.maurof00.xeonkitpvp.listener.PlayerListener;
import it.maurof00.xeonkitpvp.listener.WorldProtectionListener;
import it.maurof00.xeonkitpvp.repository.PlayerRepository;
import it.maurof00.xeonkitpvp.service.AntiFarmService;
import it.maurof00.xeonkitpvp.service.BountyService;
import it.maurof00.xeonkitpvp.service.CombatService;
import it.maurof00.xeonkitpvp.service.EconomyService;
import it.maurof00.xeonkitpvp.service.GangService;
import it.maurof00.xeonkitpvp.service.KillEffectService;
import it.maurof00.xeonkitpvp.service.KillService;
import it.maurof00.xeonkitpvp.service.KitService;
import it.maurof00.xeonkitpvp.service.LeaderboardService;
import it.maurof00.xeonkitpvp.service.LevelService;
import it.maurof00.xeonkitpvp.service.PlayerService;
import it.maurof00.xeonkitpvp.service.QuestService;
import it.maurof00.xeonkitpvp.service.ShopService;
import it.maurof00.xeonkitpvp.service.SpawnService;
import it.maurof00.xeonkitpvp.service.TagService;
import it.maurof00.xeonkitpvp.ui.ScoreboardService;
import it.maurof00.xeonkitpvp.ui.TablistService;
import it.maurof00.xeonkitpvp.util.ColorUtil;
import it.maurof00.xeonkitpvp.util.MessageService;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.HashMap;

public final class KitPvPCore extends JavaPlugin {
    private PluginConfig pluginConfig; private MessageService messages; private PlayerRepository playerRepository; private PlayerService players;
    private EconomyService economy; private LevelService levels; private CombatService combat; private AntiFarmService antiFarm; private QuestService quests;
    private TagService tags; private KillEffectService killEffects; private ShopService shop; private KillService kills;
    private KitService kits; private GangService gangs; private SpawnService spawn; private BountyService bounties; private LeaderboardService leaderboard;
    private HookManager hooks; private ScoreboardService scoreboard; private TablistService tablist; private BukkitCommandManager commandManager; private int saveTask=-1;

    @Override public void onEnable() {
        saveDefaultConfig(); saveResourceIfMissing("lang.yml"); saveResourceIfMissing("kits.yml"); saveResourceIfMissing("quests.yml"); saveResourceIfMissing("cosmetics.yml"); saveResourceIfMissing("shop.yml");
        pluginConfig=new PluginConfig(getConfig()); messages=new MessageService(this); playerRepository=new PlayerRepository(getDataFolder());
        players=new PlayerService(this,pluginConfig,playerRepository,messages); economy=new EconomyService(players); levels=new LevelService(this,pluginConfig,messages);
        combat=new CombatService(pluginConfig,messages); antiFarm=new AntiFarmService(this,pluginConfig,messages); quests=new QuestService(this,messages);
        tags=new TagService(this); killEffects=new KillEffectService(this); shop=new ShopService(this); kills=new KillService(this);
        kits=new KitService(this,players,pluginConfig,messages); spawn=new SpawnService(this,messages); gangs=new GangService(this,players,economy,pluginConfig,messages);
        bounties=new BountyService(players,pluginConfig,messages); leaderboard=new LeaderboardService(players,playerRepository); hooks=new HookManager(this); hooks.enable();
        scoreboard=new ScoreboardService(this); tablist=new TablistService(this);
        registerListeners(); registerCommands(); scoreboard.start(); tablist.start(); combat.start(this); scheduleSave();
        getLogger().info("XeonKitPvP 2.1.0 enabled | MC="+Bukkit.getVersion()+" | Java="+System.getProperty("java.version"));
        getLogger().info("Modules: ACF=enabled | Quests="+quests.all().size()+" | Tags="+tags.ids().size()+" | KillEffects="+killEffects.ids().size()+" | Shop="+shop.enabled()+" | AntiFarm="+pluginConfig.antiFarmEnabled()+" | CombatLog="+pluginConfig.combatLogEnabled());
    }
    private void registerCommands(){
        commandManager=new BukkitCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("kits",c->kits.ids());
        commandManager.getCommandCompletions().registerCompletion("tags",c->tags.ids());
        commandManager.getCommandCompletions().registerCompletion("killeffects",c->killEffects.ids());
        commandManager.registerCommand(new KitPvPCommand(this)); commandManager.registerCommand(new AdminCommand(this)); commandManager.registerCommand(new KitCommand(this));
        commandManager.registerCommand(new StatsCommand(this)); commandManager.registerCommand(new TopCommand(this,leaderboard)); commandManager.registerCommand(new BountyCommand(this));
        commandManager.registerCommand(new SpawnCommand(this)); commandManager.registerCommand(new GangCommand(this)); commandManager.registerCommand(new LevelCommand(this));
        commandManager.registerCommand(new QuestCommand(this)); commandManager.registerCommand(new ShopCommand(this)); commandManager.registerCommand(new TagCommand(this)); commandManager.registerCommand(new KillEffectCommand(this));
    }
    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this),this); Bukkit.getPluginManager().registerEvents(new CombatListener(this),this); Bukkit.getPluginManager().registerEvents(new CombatCommandListener(this),this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this),this); Bukkit.getPluginManager().registerEvents(new MenuListener(this),this); Bukkit.getPluginManager().registerEvents(new WorldProtectionListener(this),this);
    }
    private void scheduleSave(){long ticks=pluginConfig.saveIntervalSeconds()*20L;saveTask=Bukkit.getScheduler().scheduleSyncRepeatingTask(this,()->{saveAll();antiFarm.cleanup();},ticks,ticks);}
    public void reloadAll(){reloadConfig();messages.reload();pluginConfig.reload(getConfig());kits.reload();players.reload();gangs.reload();quests.reload();tags.reload();killEffects.reload();shop.reload();antiFarm.clear();scoreboard.stop();scoreboard.start();tablist.stop();tablist.start();}
    private void saveAll(){players.saveAll();gangs.save();}
    @Override public void onDisable(){if(saveTask!=-1)Bukkit.getScheduler().cancelTask(saveTask);if(scoreboard!=null)scoreboard.stop();if(tablist!=null)tablist.stop();if(combat!=null)combat.stop();if(players!=null&&gangs!=null)saveAll();}
    private void saveResourceIfMissing(String name){java.io.File f=new java.io.File(getDataFolder(),name);if(!f.exists())saveResource(name,false);}
    public String formatKd(double kd){return String.format(java.util.Locale.US,"%.2f",kd);}
    public String replacePlaceholders(String text,Player player,PlayerProfile p){String value=text==null?"":text;value=value.replace("%player%",player.getName()).replace("%kills%",String.valueOf(p.getKills())).replace("%deaths%",String.valueOf(p.getDeaths())).replace("%kd%",formatKd(p.getKd())).replace("%coins%",String.valueOf(p.getCoins())).replace("%money%",String.valueOf(p.getCoins())).replace("%xp%",String.valueOf(p.getXp())).replace("%level%",String.valueOf(p.getLevel())).replace("%prestige%",String.valueOf(p.getPrestige())).replace("%nextxp%",String.valueOf(levels.requiredXp(p))).replace("%streak%",String.valueOf(p.getStreak())).replace("%beststreak%",String.valueOf(p.getBestStreak())).replace("%bounty%",String.valueOf(p.getBounty())).replace("%gang%",p.getGang()==null?"-":p.getGang()).replace("%tag%",tags.display(p.getSelectedTag())).replace("%killeffect%",killEffects.display(p.getSelectedKillEffect())).replace("%combat%",combat.isTagged(player)?String.valueOf(combat.remainingSeconds(player))+"s":"-").replace("%online%",String.valueOf(getServer().getOnlinePlayers().size()));if(hooks!=null&&hooks.getPlaceholder()!=null)try{value=me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player,value);}catch(Throwable ignored){}return ColorUtil.color(value);}
    public PluginConfig getPluginConfig(){return pluginConfig;} public MessageService getMessages(){return messages;} public PlayerService getPlayers(){return players;} public EconomyService getEconomy(){return economy;} public LevelService getLevels(){return levels;} public CombatService getCombat(){return combat;} public AntiFarmService getAntiFarm(){return antiFarm;} public QuestService getQuests(){return quests;} public TagService getTags(){return tags;} public KillEffectService getKillEffects(){return killEffects;} public ShopService getShop(){return shop;} public KillService getKills(){return kills;} public KitService getKits(){return kits;} public GangService getGangs(){return gangs;} public SpawnService getSpawn(){return spawn;} public BountyService getBounties(){return bounties;} public LeaderboardService getLeaderboard(){return leaderboard;} public HookManager getHooks(){return hooks;} public ScoreboardService getScoreboard(){return scoreboard;} public TablistService getTablist(){return tablist;} public BukkitCommandManager getCommandManager(){return commandManager;}
}
