package me.hapyl.fight.game.talents.archive.orc;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class OrcGrowl extends Talent {

    @DisplayField private final int debuffDuration = Tick.fromSecond(6);
    @DisplayField private final double distance = 8.0;
    @DisplayField(scaleFactor = 100) private final double attackDecrease = 0.2d;
    @DisplayField(scaleFactor = 500) private final double speedDecrease = 0.1d;

    private final TemperInstance temperInstance = Temper.ORC_GROWL.newInstance("&7\uD83D\uDC7B &f&lsᴄᴀʀᴇᴅ")
            .decrease(AttributeType.ATTACK, attackDecrease)
            .decrease(AttributeType.SPEED, speedDecrease); // 50% ~the same as the slowness


    public OrcGrowl() {
        super("Growl of a Beast");

        setDescription("""
                Growl with your &a&lbeautiful&7 and &4dealy&7 void, scaring enemies in moderate range, &eimpairing&7 and &3slowing&7 them down.
                """);

        setType(Type.IMPAIR);
        setItem(Material.GOAT_HORN);
        setDurationSec(5);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        player.addPotionEffect(PotionEffectType.SLOW, 3, 20);

        Collect.nearbyEntities(location, distance).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            temperInstance.temper(entity, debuffDuration);
        });

        // Fx
        Geometry.drawCircleAnchored(location, distance, Quality.HIGH, new WorldParticle(Particle.CRIT_MAGIC, 3, 0, 0, 0, 0.025f), 1.0d);
        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);

        return Response.OK;
    }
}
