package me.hapyl.fight.game.talents.km;


import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LaserEye extends Talent {

    @DisplayField private final double damage = 1.5d;
    @DisplayField private final short maxDistance = 30;

    private final ParticleBuilder particleMove = ParticleBuilder.redstoneDust(Color.RED, 1);
    private final ParticleBuilder particleHit = ParticleBuilder.particle(Particle.LAVA);

    public LaserEye(@Nonnull Key key) {
        super(key, "Laser Eye");

        setDescription("""
                Become immovable and activate a deadly laser for {duration} that rapidly damages enemies.
                """);

        setType(TalentType.DAMAGE);
        setMaterial(Material.ENDER_EYE);

        setDurationSec(2);
        setCooldownSec(12);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final int duration = getDuration();

        // player.addEffect(EffectType.MOVEMENT_CONTAINMENT, duration);

        new PlayerTickingGameTask(player) {
            @Override
            public void run(int tick) {
                if (tick >= duration) {
                    PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
                    cancel();
                    return;
                }

                CFUtils.rayTraceLine(player, maxDistance, 0.75d, 0.0d,
                        location -> {
                            if (location.getBlock().isPassable()) {
                                particleMove.display(location);
                            }
                        },
                        entity -> {
                            entity.damage(damage, player, DamageCause.LASER);
                            particleHit.display(entity.getMidpointLocation());
                        }
                );
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.25f);
        return Response.OK;
    }
}
