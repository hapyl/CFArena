package me.hapyl.fight.game.talents.doctor;


import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ConfusionPotion extends Talent {

    @DisplayField private final int explosionDelay = 20;
    @DisplayField private final int persistDuration = 20;
    @DisplayField private final double radius = 3.5d;

    public ConfusionPotion(@Nonnull Key key) {
        super(key, "Dr. Ed's Amnesia Extract Serum");

        setDescription("""
                Swiftly throw a potion in the air that flips and spills a special &9serum&7 field for {duration}.
                
                Enemies who step in the field forget how to move properly.
                &8&o;;The effect persists for additional %ss after leaving the field.
                """.formatted(Tick.round(persistDuration))
        );

        setType(TalentType.IMPAIR);
        setItem(Material.POTION, builder -> {
            builder.setPotionColor(Color.fromRGB(208, 207, 252));
        });
        setDuration(200);
        setCooldownSec(30);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocationAnchored();
        final Location centre = location.clone();

        final ArmorStand entity = Entities.ARMOR_STAND.spawn(
                location.add(0.0d, 1.0d, 0.0d),
                self -> {
                    self.setSilent(true);
                    self.setMarker(true);
                    self.setVisible(false);
                    self.getEquipment().setHelmet(getItem());
                }
        );

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= getDuration()) {
                    cancel();
                    return;
                }

                // Animation
                if (tick < explosionDelay) {
                    final double y = Math.sin(Math.toRadians(tick * 5)) * 1.75d;

                    LocationHelper.offset(
                            location, 0.0d, y, 0.0d, () -> {
                                entity.teleport(location);
                                entity.setHeadPose(entity.getHeadPose().add(0.15d, 0.0d, 0.0d));
                            }
                    );
                }
                // Create field
                else if (tick == explosionDelay) {
                    player.playWorldSound(location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.75f);
                    player.spawnWorldParticle(location, Particle.CLOUD, 5, 0.1d, 0.05d, 0.1d, 0.02f);

                    entity.remove();
                }
                // Affect
                else {
                    Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammateOrHasEffectResistance)
                           .forEach(entity -> {
                               entity.addEffect(EffectType.AMNESIA, persistDuration);
                               entity.triggerDebuff(player);
                           });

                    for (double d = 0; d <= Math.PI * 2; d += Math.PI / 24) {
                        final double x = Math.sin(d) * radius;
                        final double z = Math.cos(d) * radius;

                        LocationHelper.offset(
                                location, x, -0.5d, z, () -> {
                                    final Vector vector = centre.toVector().subtract(location.toVector()).normalize();

                                    player.spawnWorldParticle(
                                            location, Particle.CLOUD, 0,
                                            vector.getX() * 0.75d,
                                            -0.1d,
                                            vector.getZ() * 0.75d,
                                            0.2f
                                    );
                                }
                        );
                    }

                    if (modulo(10)) {
                        player.playWorldSound(location, Sound.ENTITY_HORSE_BREATHE, (0.75f + (0.5f * (tick % 10))));
                    }
                }
            }
        }.runTaskTimer(0, 1);

        player.playWorldSound(location, Sound.ENTITY_CHICKEN_EGG, 0.0f);

        return Response.OK;
    }
}
