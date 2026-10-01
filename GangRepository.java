package it.maurof00.xeonkitpvp.repository;

import it.maurof00.xeonkitpvp.model.Gang;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public final class GangRepository {
    private final File file;
    private YamlConfiguration yaml;

    public GangRepository(File dataFolder) {
        this.file = new File(dataFolder, "gangs.yml");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public void loadInto(java.util.Map<String, Gang> target) {
        target.clear();
        if (yaml.getConfigurationSection("gangs") == null) return;
        for (String id : yaml.getConfigurationSection("gangs").getKeys(false)) {
            String base = "gangs." + id;
            String leaderRaw = yaml.getString(base + ".leader", null);
            if (leaderRaw == null) continue;
            try {
                Gang gang = new Gang(id, UUID.fromString(leaderRaw));
                for (String member : yaml.getStringList(base + ".members")) {
                    try { gang.getMembers().add(UUID.fromString(member)); } catch (IllegalArgumentException ignored) { }
                }
                target.put(id.toLowerCase(), gang);
            } catch (IllegalArgumentException ignored) { }
        }
    }

    public void saveAll(Iterable<Gang> gangs) {
        yaml.set("gangs", null);
        for (Gang gang : gangs) {
            String base = "gangs." + gang.getName();
            yaml.set(base + ".leader", gang.getLeader().toString());
            java.util.List<String> members = new ArrayList<String>();
            for (UUID member : gang.getMembers()) members.add(member.toString());
            yaml.set(base + ".members", members);
        }
        try { yaml.save(file); }
        catch (IOException e) { throw new IllegalStateException("Could not save gangs.yml", e); }
    }

    public void reload(java.util.Map<String, Gang> target) {
        yaml = YamlConfiguration.loadConfiguration(file);
        loadInto(target);
    }
}
