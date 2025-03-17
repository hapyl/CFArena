package me.hapyl.fight.game.talents.spark;


import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
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
    @DisplayField private final double fireDamage = 3.0d;
    @DisplayField(scaleFactor = 100, suffix = "% of Max Health", suffixSpace = false) private final double fireHealing = 0.4d;
    @DisplayField private final int fireInterval = 5;

    private final Vector downVelocity = new Vector(0.0d, -0.25d, 0.0d);

    public Molotov(@Nonnull Key key) {
        super(key, "Hot Hands");

        setDescription("""
                Throw a &6fireball&7 in front of you that lands after &b{maximumAirTime}&7 or upon hitting a block.
                
                Upon landing, set the ground of &efire&7, &cdamaging&7 enemies and &ahealing&7 yourself.
                """
        );

        setType(TalentType.DAMAGE);
        setItem(Material.FIRE_CHARGE);

        setCooldown(700);
        setDuration(100);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().add(new Vector(0.0d, 0.25, 0.0d));

        final Item item = player.getWorld().dropItem(
                location, new ItemStack(Material.HONEYCOMB), self -> {
                    self.setPickupDelay(5000);
                    self.setTicksLived(5800);
                    self.setVelocity(vector.multiply(1.5d));
                }
        );

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= maximumAirTime) {
                    item.setVelocity(downVelocity);
                }

                // Collision detection
                if (item.isDead() || item.isOnGround()) {
                    item.remove();
                    createMolotov(item.getLocation(), player);
                    cancel();
                }

                // Fx
                player.spawnWorldParticle(item.getLocation(), Particle.FLAME, 1);
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_ARROW_SHOOT, 0.0f);
        return Response.OK;
    }

    public void createMolotov(@Nonnull Location location, @Nonnull GamePlayer player) {
        final Location centre = location.clone();

        // Calculate healing
        final double maxHealth = player.getMaxHealth();
        final double healingPerTick = maxHealth * fireHealing / ((double) getDuration() / fireInterval);

        new TickingGameTask() {
            private double theta;

            @Override
            public void run(int tick) {
                if (tick > getDuration()) {
                    cancel();
                    return;
                }

                // Damage
                if (modulo(fireInterval)) {
                    Collect.nearbyEntities(location, fireRadius, entity -> !player.isTeammate(entity))
                           .forEach(entity -> {
                               // Heal
                               if (player.equals(entity)) {
                                   player.heal(healingPerTick);
                               }
                               else {
                                   entity.damageNoKnockback(fireDamage, player, DamageCause.FIRE_MOLOTOV);
                               }
                           });

                    // Play sound at intervals
                    player.playWorldSound(location, Sound.BLOCK_FIRE_AMBIENT, 2.0f);
                }

                // Draw outline with lava particles
                for (double d = 0; d < Math.PI * 2; d += Math.PI / 64) {
                    final double x = Math.sin(d) * fireRadius;
                    final double z = Math.cos(d) * fireRadius;

                    LocationHelper.offset(location, x, 0.1d, z, () -> {
                        player.spawnWorldParticle(location, Particle.FALLING_LAVA, 1);
                    });
                }

                final double offset = Math.PI * 2 / 4;

                for (int i = 0; i < 16; i++) {
                    final double x = Math.sin(theta + offset * i) * fireRadius;
                    final double z = Math.cos(theta + offset * i) * fireRadius;

                    LocationHelper.offset(
                            location, x, 0.1d, z, () -> {
                                final Vector vector = Vectors.directionTo(centre, location);

                                player.spawnWorldParticle(
                                        location,
                                        Particle.FLAME, 0,
                                        vector.getX() * 0.4d,
                                        0.1d,
                                        vector.getZ() * 0.4d,
                                        0.4f
                                );
                            }
                    );

                    theta += Math.PI / 32;
                }

            }
        }.runTaskTimer(0, 1);
    }

}
