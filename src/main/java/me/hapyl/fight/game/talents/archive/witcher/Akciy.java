package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Akciy extends Talent {

    public Akciy() {
        super("Axii", """
                Stuns your target opponent for {duration} or until they get hit.
                                
                Stunned opponent is immovable and cannot use their abilities.
                """);

        setDuration(100);
        setCooldownSec(40);
        setItem(Material.SLIME_BALL);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntity(player, 50.0d, 0.8d, e -> !e.equals(player));

        if (target == null) {
            return Response.error("No valid target!");
        }

        target.addEffect(GameEffectType.STUN, getDuration());
        target.sendMessage("&c%s stunned you!", player.getName());

        player.sendMessage("&aStunned %s!", target.getName());
        return Response.OK;
    }
}
