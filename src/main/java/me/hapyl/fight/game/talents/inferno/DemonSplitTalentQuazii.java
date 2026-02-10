package me.hapyl.fight.game.talents.inferno;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.Decay;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.inferno.InfernoData;
import me.hapyl.fight.game.heroes.inferno.InfernoDemonType;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class DemonSplitTalentQuazii extends DemonSplitTalent {

    @DisplayField(percentage = true) private final double decayPercent = 0.25d;
    @DisplayField private final int decayDuration = Tick.fromSeconds(8);

    @DisplayField(percentage = true) private final double healingPerEnemyHit = 0.3d;

    @DisplayField private final double beamHeight = 3;
    @DisplayField private final double beamLength = 5;
    @DisplayField private final int beamGrownDelay = 40;

    public DemonSplitTalentQuazii(@Nonnull Key key) {
        super(key, InfernoDemonType.QUAZII, Material.WITHER_SKELETON_SPAWN_EGG);

        setType(TalentType.IMPAIR);
    }

    @Nonnull
    @Override
    public String describe() {
        return """
                &6Ability: ROFLcopter
                A deadly beam that spins around the demon.
                
                Colliding with the beam applies %s worth &b{decayPercent}&7 of %s.
                """.formatted(Named.DECAY, AttributeType.MAX_HEALTH);
    }

    @Override
    @Nonnull
    public ReformDescription describeReform() {
        return new ReformDescription(
                "Wrath", """
                &aheal&7 for &c{healingPerEnemyHit}&7 of %s per enemy hit with the beam.
                """.formatted(AttributeType.MAX_HEALTH)
        );
    }

    @Override
    @Nonnull
    public DemonInstance newInstance(@Nonnull GamePlayer player) {
        final QuaziiBeam beam = new QuaziiBeam(player.getLocation());

        return new DemonInstance() {
            @Override
            public void onForm(@Nonnull GamePlayer player, @Nonnull InfernoData data) {
                data.quaziiBeamHits.clear();

                drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.SMOKE, 1), 2.3d);
            }

            @Override
            public void onReform(@Nonnull GamePlayer player, @Nonnull InfernoData data) {
                // Heal
                final int hitEnemies = data.quaziiBeamHits.size();
                final double healing = player.getMaxHealth() * (healingPerEnemyHit * hitEnemies);

                if (healing > 0.0d) {
                    player.heal(healing, player);
                    player.sendMessage("&4\uD83D\uDC7F %s &ahealed for &a&l%.0f &c❤&a!".formatted(type().getName(), healing));
                }
            }

            @Override
            public void onTick(@Nonnull GamePlayer player, @Nonnull InfernoData data, int tick) {
                final Location location = player.getLocation().add(0d, 0.2d, 0d);
                final double beamLength = DemonSplitTalentQuazii.this.beamLength * Math.min(1.0d, ((double) tick / beamGrownDelay));

                // Move the beam
                for (int i = 0; i < beam.entities.length; i++) {
                    final LivingEntity entity = beam.entities[i];
                    final boolean isGuardian = i % 2 == 0;

                    final double d = isGuardian ? 0.2d : beamLength;
                    final double r = Math.toRadians(tick * 5);

                    final double x = Math.sin(r) * d;
                    final double y = i * 0.3d - (!isGuardian ? 0.1d : 0.0d); // Gotta fix Y offset because the entities are different height
                    final double z = Math.cos(r) * d;

                    LocationHelper.offset(
                            location, x, y, z, () -> entity.teleport(location)
                    );

                    // Raycast
                    if (isGuardian) {
                        rayCast(entity.getLocation(), beam.entities[i + 1].getLocation(), data);
                    }

                    // Update scale here because Spigot is annoying
                    CFUtils.setAttributeValue(entity, Attribute.SCALE, 0.01d);
                }
            }

            private void rayCast(@Nonnull Location from, @Nonnull Location to, InfernoData data) {
                final Location location = from.clone();

                final double distance = from.distance(to);
                final Vector vector = to.toVector().subtract(from.toVector()).normalize().multiply(1.0d);

                for (double i = 0; i < distance; i += 1.0d) {
                    location.add(vector);

                    Collect.nearbyEntities(location, 0.5d, player::isNotSelfOrTeammate)
                           .forEach(entity -> {
                               entity.setDecay(new Decay(entity.getMaxHealth() * decayPercent, decayDuration));
                               data.quaziiBeamHits.add(entity);
                           });
                }
            }

            @Override
            public void remove() {
                beam.remove();
            }
        };
    }

    private class QuaziiBeam implements Removable {

        private final LivingEntity[] entities;

        public QuaziiBeam(@Nonnull Location location) {
            final int beamHeight = (int) (DemonSplitTalentQuazii.this.beamHeight * 2);
            this.entities = new LivingEntity[beamHeight];

            // Create entities
            for (int i = 0; i < beamHeight; i++) {
                final Squid squid = spawn(
                        location, Entities.SQUID, self -> {
                        }
                );

                final Guardian guardian = spawn(
                        location, Entities.GUARDIAN, self -> {
                            self.setTarget(squid);
                            self.setLaser(true);
                        }
                );

                entities[i] = guardian;
                entities[++i] = squid;
            }
        }


        @Override
        public void remove() {
            for (Entity entity : entities) {
                entity.remove();
            }
        }

        private static <T extends LivingEntity> T spawn(Location location, Entities<T> type, Consumer<T> consumer) {
            return type.spawn(
                    location, self -> {
                        self.setInvulnerable(true);
                        self.setSilent(true);
                        self.setGravity(false);
                        self.setInvisible(true);
                        self.setAI(false);

                        EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
                        consumer.accept(self);
                    }
            );
        }

    }
}
