package me.hapyl.fight.game.talents.archive.spark;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Molotov extends Talent implements Listener {

    @DisplayField private final int maximumAirTime = 60;
    @DisplayField(suffix = "blocks") private final double fireRadius = 3.0d;
    @DisplayField private final int fireDuration = 100;
    @DisplayField private final double fireDamage = 3.0d;
    @DisplayField(suffix = "&f❤/&fInterval") private final double fireHealing = 1.0d;
    @DisplayField private final int fireInterval = 5;

    public Molotov() {
        super(
                "Hot Hands", """
                        Throw a fireball in front of you that flies for maximum of &b{maximumAirTime}&7.
                                                
                        Upon landing, set the ground of &efire&7, &cdamaging&7 enemies and &ahealing&7 yourself.
                        """
        );

        setType(Type.DAMAGE);
        setItem(Material.FIRE_CHARGE);
        setCooldown(700);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().add(new Vector(0.0d, 0.25, 0.0d));

        final Item item = player.getWorld().dropItem(location, new ItemStack(Material.HONEYCOMB), self -> {
            self.setPickupDelay(5000);
            self.setTicksLived(5800);
            self.setVelocity(vector.multiply(1.5d));
        });

        new GameTask() {
            private int flightTick = maximumAirTime;

            @Override
            public void run() {
                // Fly down if in the air for 3s or more
                if (flightTick-- <= 0) {
                    item.setVelocity(new Vector(0.0d, -0.25d, 0.0d));
                }

                // Spawn molotov
                if (item.isDead() || item.isOnGround()) {
                    item.remove();
                    startMolotovTask(item.getLocation(), player);
                    cancel();
                    return;
                }

                // Fx
                PlayerLib.spawnParticle(item.getLocation(), Particle.FLAME, 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(0, 1);

        // fx
        PlayerLib.playSound(location, Sound.ENTITY_ARROW_SHOOT, 0.0f);
        return Response.OK;
    }

    private void startMolotovTask(Location location, GamePlayer player) {
        new GameTask() {
            private int molotovTime = fireDuration / fireInterval;

            @Override
            public void run() {
                if (molotovTime-- < 0) {
                    cancel();
                    return;
                }

                Collect.nearbyEntities(location, fireRadius).forEach(entity -> {
                    if (entity.equals(player)) {
                        entity.heal(fireHealing);
                    }
                    else {
                        entity.damage(fireDamage, player, EnumDamageCause.FIRE_MOLOTOV);
                    }
                });

                // Fx
                PlayerLib.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 2.0f);
                PlayerLib.spawnParticle(location, Particle.FLAME, 15, fireRadius / 2.0d, 0.1d, fireRadius / 2.0f, 0.05f);
                Geometry.drawCircle(location, fireRadius, Quality.HIGH, new WorldParticle(Particle.FLAME));
            }
        }.runTaskTimer(0, fireInterval);
    }

}
