package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Aard extends Talent {

    @DisplayField private final double radius = 4.0d;

    public Aard() {
        super("Aard", "Creates a &nsmall explosion&7 in front of you that &bpushes &cenemies&7 away.");

        setType(Type.IMPAIR);
        setItem(Material.HEART_OF_THE_SEA);
        setCooldownSec(5);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector vector = player.getLocation().getDirection().setY(0.125d).multiply(2.0d);
        final Location inFront = player.getLocation().add(vector);

        Collect.nearbyEntities(inFront, radius).forEach(entity -> {
            if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                return;
            }

            entity.setVelocity(vector);
        });

        // Fx
        player.playWorldSound(inFront, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f);
        player.spawnWorldParticle(inFront, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);

        return Response.OK;
    }
}
