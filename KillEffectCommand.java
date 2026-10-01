package it.maurof00.xeonkitpvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import it.maurof00.xeonkitpvp.KitPvPCore;
import it.maurof00.xeonkitpvp.model.PlayerProfile;
import org.bukkit.entity.Player;

@CommandAlias("killeffect|killfx|kill-effect")
public final class KillEffectCommand extends BaseCommand {
    private final KitPvPCore plugin; public KillEffectCommand(KitPvPCore plugin){this.plugin=plugin;}
    @Default @CommandCompletion("@killeffects") public void execute(Player player,@Optional String id){PlayerProfile p=plugin.getPlayers().get(player);if(id==null){plugin.getMessages().send(player,"shop.effect-list",java.util.Collections.singletonMap("effects",String.join(", ",p.getOwnedKillEffects())));return;}if(!plugin.getKillEffects().exists(id)){plugin.getMessages().send(player,"shop.invalid");return;}if(!plugin.getKillEffects().select(player,id)){plugin.getMessages().send(player,"no-permission");return;}plugin.getMessages().send(player,"shop.selected",java.util.Collections.singletonMap("item",plugin.getKillEffects().display(id)));}
}
