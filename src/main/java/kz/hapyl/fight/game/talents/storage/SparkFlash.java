package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.player.PlayerLib;
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
    private final int flashDuration = 60;

    public SparkFlash() {
        super(
                "Blinding Fire",
                "Throw an energy blast filled with blinding energy that curves up and explodes after a short delay blinding anyone who has line of sight with it.",
                Type.COMBAT
        );
        this.setItem(Material.WHITE_DYE);
        this.setCd(600);
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
            private int windupTime = 20;

            @Override
            public void run() {
                // Explode
                final Location itemLocation = item.getLocation();
                if (windupTime-- < 0) {

                    Manager.current().getCurrentGame().getAlivePlayers().forEach(victim -> {
                        final Player victimPlayer = victim.getPlayer();
                        if (!victimPlayer.hasLineOfSight(item)) {
                            return;
                        }

                        PlayerLib.addEffect(victimPlayer, PotionEffectType.BLINDNESS, flashDuration, 1);
                        PlayerLib.playSoundAndCut(player, Sound.ITEM_ELYTRA_FLYING, 2.0f, flashDuration);

                    });

                    // fx
                    PlayerLib.playSound(itemLocation, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.0f);
                    PlayerLib.spawnParticle(itemLocation, Particle.FLASH, 2, 0, 0, 0, 0);

                    this.cancel();
                    return;
                }

                // fx
                PlayerLib.spawnParticle(itemLocation, Particle.ELECTRIC_SPARK, 1, 0, 0, 0, 0);

            }
        }.runTaskTimer(0, 1);

        // fx
        PlayerLib.playSound(location, Sound.ENTITY_ARROW_SHOOT, 1.5f);

        return Response.OK;
    }
}
