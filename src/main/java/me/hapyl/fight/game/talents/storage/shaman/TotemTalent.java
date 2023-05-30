package me.hapyl.fight.game.talents.storage.shaman;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.extra.ActiveTotem;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.entity.Player;

public class TotemTalent extends Talent {

    private final ResonanceType type;

    public TotemTalent(ResonanceType type, int cd) {
        super(
                type.getName(),
                "",
                type.getMaterial()
        );

        setDescription("""
                Target placed totem and use to switch it to {name}.
                                
                """);

        addDescription(type.getAbout());

        this.type = type;

        addAttributeDescription("Aura Range &l%s", type.getRange() + " blocks");
        addAttributeDescription("Aura Interval &l%s", BukkitUtils.roundTick(type.getInterval()) + "s");

        setCooldownSec(cd);
    }

    @Override
    public Response execute(Player player) {
        final ActiveTotem totem = Talents.TOTEM.getTalent(Totem.class).getTargetTotem(player);
        if (totem == null) {
            return Response.error("Not targeting totem.");
        }

        totem.setResonanceType(type);
        Chat.sendMessage(player, "Switched totem to %s.", type.getName());

        return Response.OK;
    }
}
