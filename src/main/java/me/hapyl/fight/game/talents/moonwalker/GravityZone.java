package me.hapyl.fight.game.talents.moonwalker;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.Compute;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GravityZone extends Talent {

    @DisplayField private final double radius = 3.0d;
    @DisplayField private final double radiusY = 7.0d;
    @DisplayField private final double damagePerTick = 2.0d;

    public GravityZone(@Nonnull Key key) {
        super(key, "Gravity Pull");

        setDescription("""
                Create a gravity zone at the &etarget&7 location that will charge over time.
                
                While charging, pull all enemies within range up and slow them down.
                
                When charged, slam all enemies within range down and deal damage to them.
                &8;;The damage is scaled with how long an enemy was in the gravity pull.
                """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.PURPLE_DYE);
        setDuration(80);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Block block = player.getTargetBlockExact(10);

        if (block == null) {
            return Response.error("No valid block in sight!");
        }

        final Location center = block.getLocation().add(0.5d, 0.5d, 0.5d);
        final Location location = center.clone();

        new GameTask() {
            private final Map<LivingGameEntity, Integer> ticksInPull = Maps.newHashMap();
            private int tick = getDuration();
            private double theta = 0.0d;
            private double y = 0.0d;

            @Override
            public void run() {
                if (tick-- < 0) {
                    cancel();

                    // Push down
                    getInRange(center, 1.0d).forEach(entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return;
                        }

                        if (entity.hasEffectResistanceAndNotify(player)) {
                            return;
                        }

                        // Damage
                        final int ticksInPull = this.ticksInPull.getOrDefault(entity, 1);
                        entity.damage(ticksInPull * damagePerTick, player, EnumDamageCause.GRAVITY);

                        final Vector velocity = entity.getVelocity();
                        entity.setVelocity(new Vector(velocity.getX() / 2, -2.0d, velocity.getZ() / 2));
                        entity.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 60, true);
                    });

                    ticksInPull.clear();
                    return;
                }

                // Pull Up
                getInRange(center).forEach(entity -> {
                    if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                        return;
                    }

                    final Vector velocity = entity.getVelocity();

                    entity.setVelocity(new Vector(velocity.getX() / 2, 0.3d, velocity.getZ() / 2));
                    ticksInPull.compute(entity, Compute.intAdd());
                });

                // Fx
                modifyLocationAnd(location, Math.sin(theta) * radius, y, Math.cos(theta) * radius, loc -> {
                    PlayerLib.spawnParticle(location, Particle.WITCH, 5);
                });

                // Fx backwards
                modifyLocationAnd(location, Math.cos(theta) * radius, y, Math.sin(theta) * radius, loc -> {
                    PlayerLib.spawnParticle(location, Particle.WITCH, 5);
                });

                // Fx
                PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f - (float) tick / getDuration());
                PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f - (float) tick / getDuration());

                // Progress y and theta
                y += radiusY / getDuration();
                theta = (theta >= Math.PI * 2) ? 0 : theta + (Math.PI / 16);
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    public void modifyLocationAnd(Location location, double x, double y, double z, Consumer<Location> andThen) {
        location.add(x, y, z);
        andThen.accept(location);
        location.subtract(x, y, z);
    }

    @Nonnull
    public Set<LivingGameEntity> getInRange(@Nonnull Location location) {
        return getInRange(location, 0.0d);
    }

    public Set<LivingGameEntity> getInRange(@Nonnull Location location, double y) {
        final World world = location.getWorld();
        final Set<LivingGameEntity> hashSet = Sets.newHashSet();

        if (world == null) {
            return hashSet;
        }

        world.getNearbyEntities(location, radius, radiusY + y, radius).forEach(entity -> {
            if (!(entity instanceof LivingEntity living)) {
                return;
            }

            final LivingGameEntity gameEntity = CF.getEntity(living);
            if (gameEntity == null || !gameEntity.isValid()) {
                return;
            }

            hashSet.add(gameEntity);
        });

        return hashSet;
    }

}
