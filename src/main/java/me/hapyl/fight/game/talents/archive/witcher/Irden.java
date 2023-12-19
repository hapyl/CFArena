package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Irden extends Talent {

    @DisplayField private final double radius = 3.5d;

    public Irden() {
        super(
                "Yrden",
                "Creates Yrden aura at your current location. Opponents inside the aura are &bslowed&7, &binvulnerable&7, &bweakened &7and aren't affected by knockback."
        );

        setDuration(200);
        setCooldownSec(25);
        setItem(Material.POPPED_CHORUS_FRUIT);
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

                affect(player, location, tick);
            }
        }.runTaskTimer(0, 1);
        return Response.OK;
    }

    public void affect(GamePlayer player, Location location, int tick) {
        if (tick % 20 == 0 || tick == (getDuration() - 1)) {
            Geometry.drawCircle(location, radius, Quality.HIGH, new WorldParticle(Particle.SPELL_WITCH));
        }

        Collect.nearbyPlayers(location, radius).forEach(target -> {
            if (target.equals(player)) {
                return;
            }

            target.addPotionEffect(PotionEffectType.SLOW, 5, 3);
            target.addPotionEffect(PotionEffectType.WEAKNESS, 5, 0);
            target.addEffect(GameEffectType.VULNERABLE, 5, true);
            target.addEffect(GameEffectType.IMMOVABLE, 5, true);
        });
    }

}
