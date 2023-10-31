package me.hapyl.fight.game.talents.archive.shaman;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.util.BukkitUtils;

import javax.annotation.Nonnull;

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
    public Response execute(@Nonnull GamePlayer player) {
        final ActiveTotem totem = Talents.TOTEM.getTalent(Totem.class).getTargetTotem(player);
        if (totem == null) {
            return Response.error("Not targeting totem.");
        }

        totem.setResonanceType(type);
        player.sendMessage("Switched totem to %s.", type.getName());

        return Response.OK;
    }
}
