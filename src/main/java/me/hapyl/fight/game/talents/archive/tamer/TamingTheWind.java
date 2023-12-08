package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class TamingTheWind extends InputTalent implements TamerTalent {

    @DisplayField private final double impairRadius = 5.0d;
    @DisplayField private final short maxEnemies = 5;

    public TamingTheWind() {
        super("Taming the Wind");

        setDescription("""
                Equip concentrated wind.
                """);

        leftData.setAction("Lift Enemies");
        leftData.setDescription("""
                Lift up to &b{maxEnemies}&7 nearby &cenemies&7 up into the air, &eimpairing&7 their movement.
                """);
        leftData.setType(Type.IMPAIR);
        leftData.setDurationSec(2);
        leftData.setCooldownSec(20);

        rightData.setAction("Lift Yourself");
        rightData.setDescription("""
                Lift &nyourself&7 up into the air to traverse map terrain.
                """);
        rightData.setType(Type.ENHANCE);
        rightData.copyDurationAndCooldownFrom(leftData);

        setItem(Material.FEATHER);
    }

    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final int duration = getDuration(player);

        int liftedEnemies = 0;

        for (LivingGameEntity entity : Collect.nearbyEntities(location, impairRadius)) {
            if (player.isSelfOrTeammate(entity)) {
                continue;
            }

            if (liftedEnemies++ >= maxEnemies) {
                break;
            }

            new EntityLevitate<>(entity, duration);
        }

        // Fx
        playSwirlFx(location, false);
        player.playWorldSound(location, Sound.ENTITY_WITHER_SHOOT, 0.75f);

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final int duration = getDuration(player);
        final double y = location.getY();

        new EntityLevitate<>(player, duration) {
            @Override
            public void onStart() {
                player.addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, duration + 20, true);
            }

            @Override
            public void onTick() {
                final Location location = player.getLocation();
                location.setY(y);

                riptide.teleport(location);
            }
        };

        // Fx
        playSwirlFx(location, true);

        return Response.OK;
    }

    private void playSwirlFx(Location location, boolean reverse) {
        new TimedGameTask(10) {
            private final int swirls = 16;
            private final double d = impairRadius / maxTick;

            private double radius = reverse ? impairRadius : 1.0d;
            private double theta = 0.0d;

            @Override
            public void run(int tick) {
                for (int i = 0; i < swirls; i++) {
                    final double x = Math.sin(theta + 0.2 * (i + tick)) * radius;
                    final double z = Math.cos(theta + 0.2 * (i + tick)) * radius;

                    location.add(x, 0, z);
                    PlayerLib.spawnParticle(location, Particle.SPELL, 1);
                    location.subtract(x, 0, z);

                    theta += (Math.PI * 2 / swirls);
                }

                radius = Numbers.clamp(reverse ? radius - d : radius + d, 1, impairRadius);
            }
        }.runTaskTimer(0, 1);
    }
}
