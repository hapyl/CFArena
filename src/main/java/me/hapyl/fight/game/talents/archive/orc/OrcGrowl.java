package me.hapyl.fight.game.talents.archive.orc;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class OrcGrowl extends Talent {

    @DisplayField private final int debuffDuration = Tick.fromSecond(6);
    @DisplayField private final double distance = 8.0;

    public OrcGrowl() {
        super("Growl of a Beast");

        setDescription("""
                Growl with your beautiful and deadly voice, scaring opponents in moderate range, slowing and weakening them.
                """);

        setItem(Material.GOAT_HORN);
        setDurationSec(5);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        player.addPotionEffect(PotionEffectType.SLOW.createEffect(20, 3));

        Collect.nearbyEntities(location, distance).forEach(victim -> {
            if (victim.equals(player)) {
                return;
            }

            victim.addEffect(GameEffectType.ORC_GROWL, debuffDuration, true);
        });

        // Fx
        Geometry.drawCircleAnchored(location, distance, Quality.HIGH, new WorldParticle(Particle.CRIT_MAGIC), 1.0d);
        PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);

        return Response.OK;
    }
}
