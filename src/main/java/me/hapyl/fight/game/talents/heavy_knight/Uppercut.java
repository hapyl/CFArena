package me.hapyl.fight.game.talents.heavy_knight;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.heavy_knight.SwordMaster;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Uppercut extends Talent {

    @DisplayField private final double range = 5.0d;
    @DisplayField private final double height = 3.0d;
    @DisplayField private final double damage = 2.5d;

    public Uppercut(@Nonnull DatabaseKey key) {
        super(key, "Uppercut");

        setDescription("""
                Perform an &nuppercut&7 attack, &bjumping&7 up with &cenemies&7 in front of you.
                
                Hit &cenemies&7 fall down &3slowly&7, &eimpairing&7 their movement.
                """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.RABBIT_FOOT);
        setCooldownSec(6);
        setDuration(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().setY(0.0d);
        final Vector upVelocity = BukkitUtils.vector3Y(height);

        location.add(vector.multiply(3.0d));

        Collect.nearbyEntities(location, range).forEach(entity -> {
            if (entity.isSelfOrTeammateOrHasEffectResistance(player)) {
                return;
            }

            SwordMaster.addSuccessfulTalent(player, this);

            entity.damageNoKnockback(damage, player, EnumDamageCause.UPPERCUT);
            entity.addEffect(Effects.SLOW_FALLING, 5, getDuration());
            entity.setVelocity(upVelocity);
            entity.triggerDebuff(player);
        });

        player.setVelocity(upVelocity);
        player.addEffect(Effects.SLOW_FALLING, 5, getDuration());

        location.add(0, 0.2d, 0);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);

        Geometry.drawPolygon(location, 6, range, new WorldParticle(Particle.CRIT));

        return Response.OK;
    }
}
