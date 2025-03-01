package me.hapyl.fight.game.talents.inferno;

import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.inferno.InfernoData;
import me.hapyl.fight.game.heroes.inferno.InfernoDemonType;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class DemonSplitTalentTyphoeus extends DemonSplitTalent {

    @DisplayField private final double repeatDamageMultiplier = 1.3d;
    @DisplayField private final double repeatSwipeRadius = 2.5d;
    @DisplayField private final int repeatDuration = 60;

    @DisplayField(percentage = true) private final double trailOfFireDamage = 0.1d;

    @DisplayField private final int trailOfFireDamagePeriod = 16;
    @DisplayField private final int trailOfFireExtinguishPeriod = 8;

    private final ParticleBuilder particle = ParticleBuilder.dustTransition(Color.fromRGB(96, 0, 0), Color.fromRGB(212, 17, 17), 1);

    public DemonSplitTalentTyphoeus(@Nonnull Key key) {
        super(key, InfernoDemonType.TYPHOEUS, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);

        setType(TalentType.DAMAGE);
    }

    @Nonnull
    @Override
    public String describe() {
        return """
                &6Ability: Trail of Fire
                The demon leaves a trail of &4fire&7 behind it, dealing &c{trailOfFireDamage}&7 of %s as %s.
                """.formatted(AttributeType.MAX_HEALTH, EnumTerm.TRUE_DAMAGE);
    }

    @Nonnull
    @Override
    public ReformDescription describeReform() {
        return new ReformDescription(
                "Repeat", """
                &nrepeat&7 the &4damage&7 you dealt in the last &b%ss&7 multiplied by &cx%.1f&7.
                """.formatted(Tick.round(repeatDuration), repeatDamageMultiplier)
        );
    }

    @Override
    @Nonnull
    public DemonInstance newInstance(@Nonnull GamePlayer player) {
        return new DemonInstance() {
            private final Queue<Block> fireLocations = new LinkedList<>();

            @Override
            public void onForm(@Nonnull GamePlayer player, @Nonnull InfernoData data) {
                data.typhoeusDamage.clear();

                drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.FLAME, 1), 2.0d);
            }

            @Override
            public void onReform(@Nonnull GamePlayer player, @Nonnull InfernoData data) {
                final Set<InfernoData.DamageData> damageDealt = data.typhoeusDamage;

                damageDealt.removeIf(damageData -> {
                    final long timePassedSinceDamage = System.currentTimeMillis() - damageData.dealtAt();

                    return timePassedSinceDamage > (repeatDuration * 50L);
                });

                final double damage = damageDealt.stream().mapToDouble(InfernoData.DamageData::damage).sum();
                final double finalDamage = damage * repeatDamageMultiplier;

                // Don't do anything if the damage was lower than 0.0
                if (damage <= 0.0d) {
                    return;
                }

                final Location location = player.getLocationInFrontFromEyes(1d);

                Collect.nearbyEntities(location, repeatSwipeRadius, player::isNotSelfOrTeammate)
                       .forEach(entity -> entity.damage(finalDamage, player, DamageCause.REPEAT));

                // Fx
                final double x = player.random.nextDouble(repeatSwipeRadius - 1, repeatSwipeRadius);
                final double z = player.random.nextDouble(repeatSwipeRadius - 1, repeatSwipeRadius);

                final Location from = location.clone().add(x, 0.75d, z);
                final Location to = location.clone().subtract(x, 0.0d, z);

                Geometry.drawLine(
                        from, to, 0.2d, loc -> {
                            particle.display(loc);
                            player.spawnWorldParticle(location, Particle.FLAME, 1);
                        }
                );

                player.playWorldSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_HURT, 0.75f);
                player.playWorldSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.25f);
            }

            @Override
            public void onTick(@Nonnull GamePlayer player, @Nonnull InfernoData data, int tick) {
                final Location location = player.getLocation();
                final Block block = location.getBlock();

                // Convert to fire
                final Block down = block.getRelative(BlockFace.DOWN);

                if (block.getType() == Material.AIR && down.isSolid()) {
                    fireLocations.add(block);
                    block.setType(Material.FIRE, false);
                }

                if (tick > 0 && tick % trailOfFireExtinguishPeriod == 0) {
                    final Block last = fireLocations.poll();

                    if (last != null) {
                        last.setType(Material.AIR, false);
                    }
                }

                if (tick > 0 && tick % trailOfFireDamagePeriod == 0) {
                    // Damage
                    fireLocations.forEach(fire -> {
                        final Location fireLocation = fire.getLocation();

                        Collect.nearbyEntities(fireLocation, 1.5d, player::isNotSelfOrTeammate)
                               .forEach(entity -> {
                                   final BoundingBox boundingBox = entity.boundingBox();

                                   if (!boundingBox.overlaps(fire.getBoundingBox())) {
                                       return;
                                   }

                                   final double damage = trailOfFireDamage * entity.getMaxHealth();

                                   entity.damageNoKnockback(damage, player, DamageCause.FIRE_PIT);
                               });
                    });
                }
            }

            @Override
            public void remove() {
                CFUtils.clearCollectionAnd(
                        fireLocations, block -> {
                            if (block.getType() == Material.FIRE) {
                                block.setType(Material.AIR, false);
                            }
                        }
                );
            }
        };
    }

}
