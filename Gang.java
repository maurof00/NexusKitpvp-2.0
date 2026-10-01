package it.maurof00.xeonkitpvp.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class Gang {
    private final String name;
    private final UUID leader;
    private final Set<UUID> members = new LinkedHashSet<UUID>();

    public Gang(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members.add(leader);
    }

    public String getName() { return name; }
    public UUID getLeader() { return leader; }
    public Set<UUID> getMembers() { return members; }
}
