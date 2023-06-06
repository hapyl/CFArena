package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

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
    public Response execute(Player player) {
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

    public void affect(Player player, Location location, int tick) {
        if (tick % 20 == 0 || tick == (getDuration() - 1)) {
            Geometry.drawCircle(location, radius, Quality.HIGH, new WorldParticle(Particle.SPELL_WITCH));
        }

        Utils.getPlayersInRange(location, radius).forEach(target -> {
            if (target == player) {
                return;
            }

            PlayerLib.addEffect(target, PotionEffectType.SLOW, 5, 3);
            PlayerLib.addEffect(target, PotionEffectType.WEAKNESS, 5, 0);
            GamePlayer.getPlayer(target).addEffect(GameEffectType.VULNERABLE, 5, true);
            GamePlayer.getPlayer(target).addEffect(GameEffectType.IMMOVABLE, 5, true);
        });
    }

}
