package me.hapyl.fight.game.talents.orc;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
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


    public OrcGrowl(@Nonnull DatabaseKey key) {
        super(key, "Growl of a Beast");

        setDescription("""
                Growl with your &a&lbeautiful&7 and &4dealy&7 voice, scaring enemies in moderate range, &eimpairing&7 and &3slowing&7 them down.
                """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.GOAT_HORN);
        setDurationSec(5);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        player.addPotionEffect(PotionEffectType.SLOWNESS, 3, 20);

        Collect.nearbyEntities(location, distance).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            temperInstance.temper(entity, debuffDuration, player);
        });

        // Fx
        Geometry.drawCircleAnchored(location, distance, Quality.HIGH, new WorldParticle(Particle.ENCHANTED_HIT, 3, 0, 0, 0, 0.025f), 1.0d);
        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);

        return Response.OK;
    }
}
