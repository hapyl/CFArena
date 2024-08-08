package me.hapyl.fight.game.talents.shark;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Whirlpool extends Talent {

    private final double range = 4.0d;

    public Whirlpool() {
        super("Whirlpool", "Create a whirlpool at your current location that pulls nearby enemies towards the center.");

        setItem(Material.HEART_OF_THE_SEA);

        setDurationSec(4);
        setCooldownSec(16);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        new GameTask() {
            private int tick = getDuration();

            @Override
            public void run() {
                if (tick-- <= 0) {
                    this.cancel();
                    return;
                }

                // Pull every b
                if (tick % 20 == 0) {
                    for (int i = 0; i < 10; i++) {
                        createWhirlpool(location, range - (i / 2d), i + 4);
                    }

                    // Pull enemies towards the center
                    Collect.nearbyEntities(location, range).forEach(entity -> {
                        if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                            return;
                        }

                        final Location entityLocation = entity.getLocation();

                        Vector direction = location.clone().subtract(entityLocation).toVector().normalize().multiply(0.5);
                        entity.addEffect(Effects.SLOW, 20);
                        entity.setVelocity(direction);
                    });

                    // Fx
                    PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f);
                    PlayerLib.playSound(location, Sound.AMBIENT_UNDERWATER_ENTER, 0.75f);
                }

                Geometry.drawCircleAnchored(location, range, Quality.VERY_HIGH, new WorldParticle(Particle.SPLASH), 0.5d);
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    public void createWhirlpool(Location location, double size, int delay) {
        GameTask.runLater(() -> {
            Geometry.drawCircle(location, size, Quality.VERY_HIGH, new WorldParticle(Particle.BUBBLE));
            Geometry.drawCircle(location, size, Quality.VERY_HIGH, new WorldParticle(Particle.BUBBLE_POP));
        }, delay);
    }
}
