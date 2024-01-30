package me.hapyl.fight.game.talents.archive.heavy_knight;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Updraft extends Talent implements Listener {

    @DisplayField private final Vector pushDownVelocity = new Vector(0.0d, -0.75d, 0.0d);
    @DisplayField private final int smashDelay = 10;
    @DisplayField private final double radius = 4.0d;
    @DisplayField private final double damage = 7.5d;

    public Updraft() {
        super("Updraft");

        setDescription("""
                Leap into the air, then smash &cenemies&7 down, dealing &cdamage&7.
                """);

        setType(Type.DAMAGE);
        setItem(Material.RABBIT_FOOT);
        setCooldownSec(8);
        setDuration(21);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector velocity = player.getVelocity();

        player.setVelocity(BukkitUtils.vector3Y(3.0d).setX(velocity.getX()).setZ(velocity.getZ()));
        player.addEffect(Effects.SLOW_FALLING, 1, getDuration());

        GameTask.runLater(() -> {
            final Location location = player.getLocation();
            final Vector direction = location.getDirection();

            direction.setY(0.0d);
            location.add(direction.normalize().multiply(2.0d));

            Collect.nearbyEntities(location, radius, entity -> entity.isValid(player))
                    .forEach(entity -> {
                        SwordMaster.addSuccessfulTalent(player, this);

                        entity.setVelocity(pushDownVelocity);
                        entity.damageNoKnockback(damage, player);
                    });

        }, smashDelay);

        return Response.OK;
    }
}
