package it.maurof00.xeonkitpvp.service;

import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.config.PluginConfig;
import it.maurof00.xeonkitpvp.util.MessageService;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class AntiFarmService {
    private static final class PairKey {
        private final UUID a, b;
        PairKey(UUID a, UUID b) {
            if (a.toString().compareTo(b.toString()) <= 0) { this.a = a; this.b = b; }
            else { this.a = b; this.b = a; }
        }
        @Override public int hashCode() { return a.hashCode() * 31 + b.hashCode(); }
        @Override public boolean equals(Object obj) { if (!(obj instanceof PairKey)) return false; PairKey k=(PairKey)obj; return a.equals(k.a)&&b.equals(k.b); }
    }
    private final KitPvPCore plugin;
    private final PluginConfig config;
    private final MessageService messages;
    private final Map<PairKey, Deque<Long>> history = new HashMap<PairKey, Deque<Long>>();

    public AntiFarmService(KitPvPCore plugin, PluginConfig config, MessageService messages) { this.plugin=plugin; this.config=config; this.messages=messages; }

    public boolean isAllowed(Player killer, Player victim) {
        if (!config.antiFarmEnabled() || killer == null || victim == null || killer.hasPermission(config.antiFarmBypassPermission())) return true;
        long now=System.currentTimeMillis();
        PairKey key=new PairKey(killer.getUniqueId(), victim.getUniqueId());
        Deque<Long> timestamps=history.get(key);
        if (timestamps == null) { timestamps=new ArrayDeque<Long>(); history.put(key,timestamps); }
        prune(timestamps, now);
        if (!timestamps.isEmpty() && now - timestamps.peekLast() < config.antiFarmMinimumDelaySeconds()*1000L) return false;
        if (timestamps.size() >= config.antiFarmMaxKills()) return false;
        timestamps.addLast(now);
        return true;
    }

    public void notifyBlocked(Player player) { if (config.antiFarmNotify() && player != null) messages.send(player, "anti-farm-block"); }
    public void clear() { history.clear(); }
    public void cleanup() { long now=System.currentTimeMillis(); java.util.Iterator<Map.Entry<PairKey,Deque<Long>>> it=history.entrySet().iterator(); while(it.hasNext()){Deque<Long> q=it.next().getValue(); prune(q,now); if(q.isEmpty()) it.remove();} }
    public int trackedPairs() { return history.size(); }
    private void prune(Deque<Long> q,long now) { long cutoff=now-config.antiFarmWindowSeconds()*1000L; while(!q.isEmpty() && q.peekFirst()<cutoff) q.removeFirst(); }
}
