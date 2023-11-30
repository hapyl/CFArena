package me.hapyl.fight.game.talents.archive.spark;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class SparkFlash extends Talent {

    @DisplayField private final int flashDuration = 60;
    @DisplayField private final double maxDistance = 50;
    @DisplayField private final int windupTime = 15;
    @DisplayField private final double fireDamage = 2;

    public SparkFlash() {
        super(
                "Blinding Curve", """
                        Throw an energy blast filled with blinding energy that curves up and explodes after a short delay, blinding anyone looking at it.
                                                
                        Enemies also receive small fire damage.
                        &8;;You know, their eyes hurt!
                        """
        );

        setType(Type.IMPAIR);
        setItem(Material.WHITE_DYE);
        setCooldown(300);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();

        final Item item = player.getWorld().dropItem(location, new ItemStack(Material.WHITE_DYE), self -> {
            self.setPickupDelay(50000);
            self.setTicksLived(5900);
            self.setVelocity(location.getDirection().normalize().setY(1.25f));
        });

        new TimedGameTask(windupTime) {
            @Override
            public void run(int tick) {
                final Location itemLocation = item.getLocation();

                // Fx
                PlayerLib.spawnParticle(itemLocation, Particle.ELECTRIC_SPARK, 1, 0, 0, 0, 0);

                if (tick % 2 == 0) {
                    PlayerLib.playSound(itemLocation, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0f);
                }
            }

            @Override
            public void onLastTick() {
                final Location itemLocation = item.getLocation();

                CF.getAlivePlayers().forEach(victim -> {
                    // Check for dot instead of line of sight
                    final Vector playerDirection = itemLocation.clone().subtract(victim.getLocation()).toVector().normalize();
                    final Vector vector = victim.getPlayer().getLocation().getDirection().normalize();

                    final double dotProduct = vector.dot(playerDirection);
                    final double distance = victim.getLocation().distance(itemLocation);

                    if ((dotProduct >= 0.4f && distance <= maxDistance) && victim.hasLineOfSight(item)) {
                        victim.addPotionEffect(PotionEffectType.BLINDNESS, flashDuration, 1);
                        victim.playSoundAndCut(Sound.ITEM_ELYTRA_FLYING, 2.0f, flashDuration);
                        victim.damage(fireDamage, player, EnumDamageCause.FIRE_TICK);
                        victim.setFireTicks(10);
                    }
                });

                // Fx
                PlayerLib.playSound(itemLocation, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.0f);
                PlayerLib.spawnParticle(itemLocation, Particle.FLASH, 2, 0, 0, 0, 0);

                item.remove();
            }
        }.runTaskTimer(0, 1);

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_ARROW_SHOOT, 1.5f);

        return Response.OK;
    }
}
