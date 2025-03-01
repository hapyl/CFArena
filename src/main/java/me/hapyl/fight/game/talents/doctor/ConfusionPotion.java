package me.hapyl.fight.game.talents.doctor;


import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ConfusionPotion extends Talent {

    @DisplayField private final int explosionDelay = 20;

    public ConfusionPotion(@Nonnull Key key) {
        super(key, "Dr. Ed's Amnesia Extract Serum");

        setDescription("""
                Swiftly throw a potion in the air that explodes and creates an aura for &b{duration}&7.
                
                Amnesia will affect opponents within range; This effect will persist for additional &b1s &7after player leaves the aura.
                
                &8;;Dr. Ed is immune to his own amnesia
                """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.POTION);
        setDuration(200);
        setCooldownSec(30);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final ArmorStand entity = Entities.ARMOR_STAND.spawn(
                location.add(0.0d, 1.0d, 0.0d),
                self -> {
                    self.setSilent(true);
                    self.setMarker(true);
                    self.setVisible(false);
                    self.setHelmet(new ItemStack(Material.POTION));
                }
        );

        player.playWorldSound(location, Sound.ENTITY_CHICKEN_EGG, 0.0f);

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
            Collect.nearbyPlayers(location, 3.5d).forEach(target -> {
                if (player.isSelfOrTeammateOrHasEffectResistance(target)) {
                    return;
                }

                target.addEffect(EffectType.AMNESIA, 20);
                target.triggerDebuff(player);
            });

            Geometry.drawCircleAnchored(location, 3.5d, Quality.HIGH, new WorldParticle(Particle.END_ROD, 0.0d, 0.0d, 0.0d, 0.01f));
        }, explosionDelay, 1);

        return Response.OK;
    }
}
