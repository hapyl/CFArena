package me.hapyl.fight.game.talents.juju;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class PoisonZone extends Talent {

    @DisplayField private final double radius = 5.0d;
    @DisplayField private final double damagePerTick = 1.0d;
    @DisplayField private final int damageTick = 5;
    @DisplayField(scaleFactor = 100) private final double defenseReduction = 0.7d;
    @DisplayField private final int defenseReductionDuration = Tick.fromSecond(5);

    public PoisonZone() {
        super("Poison Zone");

        setDurationSec(6);
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        return execute(player, player.getLocation());
    }

    public Response execute(GamePlayer player, Location location) {
        new TimedGameTask(this) {
            private double theta = 0.0d;

            @Override
            public void run(int tick) {
                Collect.nearbyEntities(location, radius).forEach(living -> {
                    living.setLastDamager(player);
                    living.damage(damagePerTick, EnumDamageCause.POISON_IVY);

                    final EntityAttributes attributes = living.getAttributes();
                    attributes.decreaseTemporary(Temper.POISON_IVY, AttributeType.DEFENSE, defenseReduction, defenseReductionDuration, player);

                    living.addEffect(Effects.POISON, 1, defenseReductionDuration);
                });

                // Fx
                for (int i = 1; i <= 5; i++) {
                    final double x = Math.sin(theta + 2 * i) * radius;
                    final double z = Math.cos(theta + 2 * i) * radius;

                    location.add(x, 0, z);
                    player.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 3, 0.1d, 0.05d, 0.1d, 0.025f);
                    player.spawnWorldParticle(location, Particle.HAPPY_VILLAGER, 1, 0.1d, 0.1d, 0.1d, 0);
                    location.subtract(x, 0, z);
                }

                // Scatter particles
                player.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 10, radius, 0.25d, radius, 0.1f);

                // Theta
                theta += Math.PI / 16;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
