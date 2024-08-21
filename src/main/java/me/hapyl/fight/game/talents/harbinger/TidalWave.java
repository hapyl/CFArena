package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.harbinger.Harbinger;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TidalWave extends TimedGameTask implements HeroReference<Harbinger> {

    private static final Particle.DustTransition innerColor = new Particle.DustTransition(
            Color.fromRGB(72, 209, 204),
            Color.fromRGB(143, 188, 143),
            2
    );

    private static final Particle.DustTransition outerColor = new Particle.DustTransition(
            Color.fromRGB(64, 224, 208),
            Color.fromRGB(0, 128, 128),
            1
    );

    private final TidalWaveTalent talent;
    private final GamePlayer player;
    private final Vector direction;
    private final Vector pushVector;
    private final Location location;
    private final DirectionalMatrix matrix;
    private final Harbinger hero;

    private double d;

    public TidalWave(@Nonnull TidalWaveTalent talent, @Nonnull GamePlayer player) {
        super(talent);

        this.talent = talent;
        this.location = player.getEyeLocation();
        this.direction = location.getDirection().normalize().multiply(talent.speed);
        this.pushVector = direction.clone().multiply(0.75d);
        this.player = player;
        this.matrix = player.getLookAlongMatrix();
        this.hero = getHero();

        runTaskTimer(0, 1);
    }

    @Override
    public void run(int tick) {
        location.add(direction);

        // Affect
        Collect.nearbyEntities(location, talent.distance).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            hero.addRiptide(player, entity, talent.riptideDuration, false);

            // Push
            if (entity.hasEffectResistanceAndNotify(player)) {
                return;
            }

            entity.setVelocity(pushVector);
            entity.triggerDebuff(player);
        });

        // Sfx
        if (modulo(10)) {
            player.playWorldSound(location, Sound.AMBIENT_UNDERWATER_ENTER, 1.25f);
            player.playWorldSound(location, Sound.AMBIENT_UNDERWATER_EXIT, 0.75f);
            player.playWorldSound(location, Sound.ENTITY_COD_FLOP, 0.0f);
        }

        // Fx
        final double innerSpread = Math.PI * 2 / 4;
        final double horizontalSpread = talent.horizontalSpread - talent.innerToOuterSpread;
        final double verticalSpread = talent.verticalSpread - talent.innerToOuterSpread;

        // Spawn 4 inner "vortex splashes"
        for (int index = 1; index <= 4; index++) {
            final double x = Math.sin(d + innerSpread * index) * horizontalSpread;
            final double y = Math.cos(d + innerSpread * index) * verticalSpread;
            final double z = 0;

            matrix.transformLocation(location, x, y, z, then -> {
                player.spawnWorldParticle(
                        then,
                        Particle.DUST_COLOR_TRANSITION,
                        2,
                        0.05d,
                        0.05d,
                        0.05d,
                        innerColor
                );
            });
        }

        final double outerSpread = Math.PI * 2 / 6;

        // Spawn 6 outer "vortex splashes"
        for (int index = 1; index <= 6; index++) {
            final double y = Math.sin(d + outerSpread * index) * talent.verticalSpread;
            final double x = Math.cos(d + outerSpread * index) * talent.horizontalSpread;
            final double z = d / 20;

            final Vector vector = matrix.transform(x, y, z);
            location.add(vector);

            player.spawnWorldParticle(
                    location,
                    Particle.DUST_COLOR_TRANSITION,
                    2,
                    0.05d,
                    0.05d,
                    0.05d,
                    outerColor
            );

            location.subtract(vector);
        }

        d += Math.PI / 12;
    }

    @Nonnull
    @Override
    public Harbinger getHero() {
        return HeroRegistry.HARBINGER;
    }
}
