package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.entity.Player;

@CommandAlias("tag|tags")
public final class TagCommand extends BaseCommand {
    private final KitPvPCore plugin; public TagCommand(KitPvPCore plugin){this.plugin=plugin;}
    @Default @CommandCompletion("@tags") public void execute(Player player,@Optional String id){PlayerProfile p=plugin.getPlayers().get(player);if(id==null){plugin.getMessages().send(player,"shop.tag-list",java.util.Collections.singletonMap("tags",String.join(", ",p.getOwnedTags())));return;}if(!plugin.getTags().exists(id)){plugin.getMessages().send(player,"shop.invalid");return;}if(!plugin.getTags().select(player,id)){plugin.getMessages().send(player,"no-permission");return;}plugin.getMessages().send(player,"shop.selected",java.util.Collections.singletonMap("item",plugin.getTags().display(id)));}
}
