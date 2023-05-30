package me.hapyl.fight.game.talents.storage.doctor;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfusionPotion extends Talent {

    @DisplayField private final int explosionDelay = 20;

    public ConfusionPotion() {
        super(
                "Dr. Ed's Amnesia Extract Serum",
                """
                        Swiftly throw a potion in the air that explodes and creates an aura for &b{duration}&7.
                                                
                        Opponents within range will be affected by Amnesia; This effect will persist for additional &b1s &7after player leaves the aura.
                                                
                        Dr. Ed is immune to his own amnesia
                        """,
                Type.COMBAT
        );

        setDuration(200);
        setItem(Material.POTION);
        setCooldownSec(30);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation();
        final ArmorStand entity = Entities.ARMOR_STAND.spawn(location.add(0.0d, 1.0d, 0.0d), me -> {
            me.setSilent(true);
            me.setMarker(true);
            me.setVisible(false);
            if (me.getEquipment() != null) {
                me.getEquipment().setHelmet(new ItemStack(Material.POTION));
            }
        });

        PlayerLib.playSound(location, Sound.ENTITY_CHICKEN_EGG, 0.0f);

        // Fx
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                final Location location = entity.getLocation();
                if (tick++ >= explosionDelay) {

                    PlayerLib.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.75f);
                    PlayerLib.spawnParticle(location, Particle.CLOUD, 5, 0.1, 0.05, 0.1, 0.02f);

                    entity.remove();
                    cancel();
                    return;
                }

                entity.teleport(location.clone().add(0.0d, (0.18d / (tick / Math.PI)), 0.0d));
                entity.setHeadPose(entity.getHeadPose().add(0.15d, 0.0d, 0.0d));

            }
        }.runTaskTimer(0, 1);

        GameTask.runDuration(this, i -> {
            Geometry.drawCircle(location, 3.5d, Quality.HIGH, new WorldParticle(Particle.END_ROD, 0.0d, 0.0d, 0.0d, 0.01f));
            Utils.getPlayersInRange(location, 3.5d).forEach(target -> {
                if (player == target) {
                    return;
                }

                GamePlayer.getPlayer(target).addEffect(GameEffectType.AMNESIA, 20, true);
            });
        }, explosionDelay, 1);

        return Response.OK;
    }
}
