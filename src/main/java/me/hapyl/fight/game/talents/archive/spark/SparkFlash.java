package me.hapyl.fight.game.talents.archive.spark;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SparkFlash extends Talent {

    @DisplayField private final int flashDuration = 60;
    @DisplayField private final int windupTime = 20;

    public SparkFlash() {
        super(
                "Blinding Fire",
                "Throw an energy blast filled with blinding energy that curves up and explodes after a short delay blinding anyone who is looking at it.",
                Type.COMBAT
        );

        setItem(Material.WHITE_DYE);
        setCooldown(600);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getEyeLocation();
        if (location.getWorld() == null) {
            return Response.error("world is null");
        }

        final Item item = location.getWorld().dropItem(location, new ItemStack(Material.WHITE_DYE));
        item.setPickupDelay(50000);
        item.setTicksLived(5900);
        item.setVelocity(location.getDirection().add(new Vector(0.0d, 0.75d, 0.0d)));

        new GameTask() {
            private int tick = windupTime;

            @Override
            public void run() {
                // Explode
                final Location itemLocation = item.getLocation();
                if (tick-- < 0) {
                    CF.getAlivePlayers().forEach(victim -> {
                        final Player victimPlayer = victim.getPlayer();

                        // Check for dot instead of line of sight
                        final Vector playerDirection = item.getLocation().subtract(victimPlayer.getLocation()).toVector().normalize();
                        final Vector vector = victim.getPlayer().getLocation().getDirection().normalize();

                        final double dotProduct = vector.dot(playerDirection);
                        final double distance = victimPlayer.getLocation().distance(item.getLocation());

                        if ((dotProduct >= 0.4f && distance <= 50) && victimPlayer.hasLineOfSight(item)) {
                            PlayerLib.addEffect(victimPlayer, PotionEffectType.BLINDNESS, flashDuration, 1);
                            PlayerLib.playSoundAndCut(victimPlayer, Sound.ITEM_ELYTRA_FLYING, 2.0f, flashDuration);
                        }
                    });

                    // Fx
                    PlayerLib.playSound(itemLocation, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.0f);
                    PlayerLib.spawnParticle(itemLocation, Particle.FLASH, 2, 0, 0, 0, 0);

                    cancel();
                    return;
                }

                // Fx
                PlayerLib.spawnParticle(itemLocation, Particle.ELECTRIC_SPARK, 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(0, 1);

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_ARROW_SHOOT, 1.5f);

        return Response.OK;
    }
}
