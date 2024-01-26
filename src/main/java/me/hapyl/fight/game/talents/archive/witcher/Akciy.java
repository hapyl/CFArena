package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Akciy extends Talent {

    public Akciy() {
        super("Axii", """
                Stun the &etarget&7 &cenemy for {duration} or until they &nget&7 hit.
                """);

        setType(Type.IMPAIR);
        setItem(Material.SLIME_BALL);
        setDuration(100);
        setCooldownSec(40);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityDot(player, 50.0d, 0.8d, entity -> !player.isSelfOrTeammate(entity));

        if (target == null) {
            return Response.error("No valid target!");
        }

        target.addEffect(Effects.STUN, getDuration());

        target.sendMessage("&c%s stunned you!", player.getName());
        player.sendMessage("&aStunned %s!", target.getName());
        return Response.OK;
    }
}
