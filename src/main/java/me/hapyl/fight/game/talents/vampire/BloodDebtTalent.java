package me.hapyl.fight.game.talents.vampire;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class BloodDebtTalent extends Talent {

    private static final double TWO_PI = Math.PI * 2;

    @DisplayField(suffix = "of total blood debt", scaleFactor = 100, percentage = true)
    public final double damageIncreaseMultiplier = 0.03d;

    @DisplayField
    private final double graspRadius = 10.0d;

    @DisplayField(suffix = "% of max health", scaleFactor = 100, suffixSpace = false)
    private final double bloodDebtBaseAmount = 0.4d;

    @DisplayField(suffix = "% of max health", scaleFactor = 100, suffixSpace = false)
    private final double bloodDebtPerEnemyMarked = 0.1d;

    @DisplayField(suffix = "% of max health", scaleFactor = 100, suffixSpace = false)
    private final double maxBloodDebt = 0.9d;

    private final int fxBatCount = 6;
    private final double castingTime = Math.PI * 1.5;

    public BloodDebtTalent(@Nonnull Key key) {
        super(key, "Grasp");

        setDescription("""
                Command several bats to swirl around the battlefield to &bmark&7 and &epull nearby&7 &cenemies&7 towards you.
                
                Apply %s to yourself based on the number of &bmarked&7 enemies.
                """.formatted(Named.BLOOD_DEBT));

        setType(TalentType.ENHANCE);
        setItem(Material.FLINT);

        setCooldownSec(30); // The cooldown is big because the ultimate refreshes it
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getMidpointLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        final List<Bat> bats = Lists.newArrayList();

        for (int i = 0; i < fxBatCount; i++) {
            bats.add(spawnBat(location));
        }

        final double offset = TWO_PI / bats.size();
        double bloodDebtAmount = bloodDebtBaseAmount;

        // Pull enemies
        for (LivingGameEntity entity : Collect.nearbyEntities(
                location,
                graspRadius,
                player::isNotSelfOrTeammateOrHasEffectResistance
        )) {
            // Increment debt
            bloodDebtAmount += bloodDebtPerEnemyMarked;

            final double distance = location.distance(entity.getLocation());
            final double distanceScaled = distance * 0.2;

            // Pull enemy
            final Vector vector = location.toVector()
                                          .subtract(entity.getLocation().toVector())
                                          .normalize()
                                          .multiply(distanceScaled)
                                          .setY(0.25);

            entity.setVelocity(vector);
        }

        // Apply blood debt
        final double maxHealth = player.getMaxHealth();
        final double bloodDebt = Math.min(maxHealth * bloodDebtAmount, maxHealth * maxBloodDebt);

        player.bloodDebt().increment(bloodDebt);

        // Fx
        player.playWorldSound(Sound.ENTITY_BAT_TAKEOFF, 0.0f);

        new TickingGameTask() {
            private final double speed = Math.PI / 14;
            private final double radiusDecrement = (graspRadius - 3) * speed / castingTime;

            private double theta = 0.0d;
            private double radius = graspRadius;

            @Override
            public void onTaskStop() {
                CFUtils.clearCollection(bats);

                // Fx
                player.playWorldSound(Sound.ENTITY_BAT_DEATH, 0.75f);
            }

            @Override
            public void run(int tick) {
                // Affect as soon as the bats made a full circle
                if (theta >= castingTime) {
                    this.cancel();
                    return;
                }

                // Circle bats
                for (int i = 0; i < bats.size(); i++) {
                    final Bat bat = bats.get(i);

                    final double x = Math.sin(theta + offset * i) * radius;
                    final double y = Math.sin(tick * 10) * 0.25d;
                    final double z = Math.cos(theta + offset * i) * radius;

                    location.setYaw(bat.getYaw() + (5 + (2 * i)));

                    LocationHelper.offset(
                            location, x, y, z, () -> {
                                bat.teleport(location);

                                // Fx
                                player.spawnWorldParticle(location, Particle.SMOKE, 3, 0.1, 0.1, 0.1, 0.05f);
                            }
                    );
                }

                radius -= radiusDecrement;
                theta += Math.PI / 14;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    private Bat spawnBat(Location location) {
        return Entities.BAT.spawn(
                location, self -> {
                    self.setInvulnerable(true);
                    self.setSilent(true);
                    self.setAwake(true);
                }
        );
    }
}
