package me.hapyl.fight.game.talents.km;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class LaserEye extends Talent {

    public LaserEye() {
        super("Laser Eye");

        setDescription("Become immovable and activate laser for {duration} that rapidly damages enemies.");

        setDuration(60);
        setItem(Material.ENDER_EYE);
        setCooldownSec(15);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final int duration = getDuration();

        player.addEffect(Effects.IMMOVABLE, duration, true);
        player.addEffect(Effects.JUMP_BOOST, 250, duration);
        player.addEffect(Effects.SLOW, 255, duration);

        GameTask.runTaskTimerTimes((task, tick) -> {
            CFUtils.rayTraceLine(player, 50, 0.5d, 0.0d, move -> {
                if (move.getBlock().isPassable()) {
                    ParticleBuilder.redstoneDust(Color.RED, 1).display(move);
                }
            }, entity -> {
                entity.damageTick(1.0d, player, EnumDamageCause.LASER, 10);
                PlayerLib.spawnParticle(entity.getLocation(), Particle.LAVA, 2, 0, 0, 0, 0);
            });

            if (tick == 0) {
                PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
            }
        }, 1, duration);

        // Fx
        player.playWorldSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.25f);

        return Response.OK;
    }
}
