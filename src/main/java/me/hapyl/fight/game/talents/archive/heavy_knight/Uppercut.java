package me.hapyl.fight.game.talents.archive.heavy_knight;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Uppercut extends Talent {

    @DisplayField private final double range = 5.0d;
    @DisplayField private final double height = 3.0d;

    public Uppercut() {
        super("Uppercut");

        setDescription("""
                Knock all &cenemies&7 in front of you up into the &3sky&7.
                
                Hit &cenemies&7 fall down slowly, &eimpairing&7 their movement.
                """);

        setType(Type.IMPAIR);
        setItem(Material.IRON_BLOCK);
        setCooldownSec(6);
        setDuration(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().setY(0.0d);

        location.add(vector.multiply(3.0d));

        Collect.nearbyEntities(location, range).forEach(entity -> {
            if (entity.isSelfOrTeammateOrHasEffectResistance(player)) {
                return;
            }

            SwordMaster.addSuccessfulTalent(player, this);

            entity.addEffect(Effects.SLOW_FALLING, 5, getDuration());
            entity.setVelocity(BukkitUtils.vector3Y(height));
        });


        location.add(0, 0.2d, 0);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);

        Geometry.drawPolygon(location, 6, range, new WorldParticle(Particle.CRIT));

        return Response.OK;
    }
}
