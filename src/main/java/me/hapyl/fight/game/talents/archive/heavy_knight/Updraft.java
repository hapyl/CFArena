package me.hapyl.fight.game.talents.archive.heavy_knight;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Updraft extends Talent {

    @DisplayField private final Vector pushDownVelocity = new Vector(0.0d, -0.75d, 0.0d);
    @DisplayField private final int smashDelay = 10;

    public Updraft() {
        super("Updraft", "Leap into the air and smash down players lifted by Uppercut.");

        setItem(Material.RABBIT_FOOT);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector velocity = player.getVelocity();

        player.setVelocity(BukkitUtils.vector3Y(3.0d).setX(velocity.getX()).setZ(velocity.getZ()));
        player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(21, 1));

        GameTask.runLater(() -> {
            final Location location = player.getLocation();
            final Vector direction = location.getDirection();

            direction.setY(0.0d);
            location.add(direction.normalize().multiply(2.0d));

            Collect.nearbyEntities(location, 3.0d, entity -> entity.isValid(player))
                    .forEach(entity -> {
                        entity.setVelocity(pushDownVelocity);
                        Debug.info("pushing down " + entity);
                    });

        }, smashDelay);

        return Response.OK;
    }
}
