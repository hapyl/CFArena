package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class Irden extends Talent {

    @DisplayField private final double radius = 3.5d;
    @DisplayField private final double defenseReduction = 0.5d;
    @DisplayField private final double speedReduction = 0.04d; // 20%
    @DisplayField private final int impairDuration = 10;

    private final TemperInstance temperInstance = Temper.YRDED.newInstance()
            .decrease(AttributeType.DEFENSE, defenseReduction)
            .decrease(AttributeType.SPEED, speedReduction);

    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            Color.fromRGB(140, 65, 125),
            Color.fromRGB(59, 2, 47),
            1
    );

    public Irden() {
        super("Yrden", """
                Creates &dYrden&7 aura at your current location.
                                
                &cEnemies&7 &ninside&7 the aura are &eimpaired&7 and aren't affected by &3knockback&7.
                """);

        setType(Type.IMPAIR);
        setItem(Material.POPPED_CHORUS_FRUIT);
        setDurationSec(10);
        setCooldownSec(25);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        new TimedGameTask(this) {
            @Override
            public void run(int tick) {
                affect(player, location, tick);
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2);

        return Response.OK;
    }

    public void affect(@Nonnull GamePlayer player, @Nonnull Location location, int tick) {
        if (tick % 20 == 0 || tick == (getDuration() - 1)) {
            Geometry.drawCircle(location, radius, Quality.HIGH, loc -> {
                player.spawnWorldParticle(loc, Particle.SPELL_WITCH, 3, 0.1d, 0.1d, 0.1d, 0.05f);
                player.spawnWorldParticle(loc, Particle.DUST_COLOR_TRANSITION, 3, 0.1d, 0.1d, 0.1d, dustTransition);
            });
        }

        Collect.nearbyEntities(location, radius).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            temperInstance.temper(entity, impairDuration);

            entity.addEffect(GameEffectType.VULNERABLE, impairDuration, true);
            entity.addEffect(GameEffectType.IMMOVABLE, impairDuration, true);
        });
    }

}
