package me.hapyl.fight.game.talents.doctor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.doctor.ElementType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.math.nn.DoubleDouble;
import me.hapyl.eterna.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class HarvestBlocks extends Talent {

    private final int TASK_PERIOD = 2;

    @DisplayField private final short maximumBlocks = 10;
    @DisplayField private final double maxDistance = 20.0d;
    @DisplayField private final int collectDelay = 30;

    public HarvestBlocks(@Nonnull DatabaseKey key) {
        super(key, "Block Harvest");

        setDescription("""
                Quickly gather resources from up to &b{maximumBlocks}&7 nearby blocks.
                
                Then combine them in one big pile before throwing it at your enemies.
                
                &8;;The damage is based on the number of blocks gathered.
                """
        );

        setItem(Material.IRON_PICKAXE);
        setCooldownSec(30);
        setPoint(5);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location start = player.getLocation().add(3, 3, 3);
        final Location end = player.getLocation().subtract(3, 3, 3);

        // Get blocks around the players
        final Cuboid cuboid = new Cuboid(start, end);
        final Map<Block, ElementType> blockMap = Maps.newHashMap();
        final List<Block> blocks = cuboid.getBlocks()
                .stream()
                .filter(b -> ElementType.getElement(b.getType()) != ElementType.NULL)
                .collect(Collectors.toList());

        Collections.shuffle(blocks);

        blocks.stream().limit(maximumBlocks).forEach(b -> {
            final ElementType element = ElementType.getElement(b.getType());
            if (element != ElementType.NULL) {
                blockMap.put(b, element);
            }
        });

        final Set<Entity> entities = Sets.newHashSet();
        final DoubleDouble damage = new DoubleDouble(0.0d);

        blockMap.forEach((block, elementType) -> {
            entities.add(Entities.ARMOR_STAND_MARKER.spawn(block.getLocation().add(0.5, 0.5, 0.5), entity -> {
                entity.setMarker(true);
                entity.setGravity(false);
                entity.setInvulnerable(true);
                entity.setInvisible(true);
                entity.setHelmet(new ItemStack(block.getType()));
            }));

            damage.addAndGet(elementType.getElement().getDamage() / 2.0d);
        });

        for (Entity entity : entities) {
            final double totalDistance = entity.getLocation().distance(player.getLocation());
            final double distancePerTick = totalDistance / (collectDelay - 10);

            new GameTask() {
                private int tick = 0;

                @Override
                public void run() {
                    if (tick >= collectDelay) {
                        entities.forEach(Entity::remove);
                        cancel();
                        return;
                    }

                    Location entityLocation = entity.getLocation();
                    Location playerLocation = getPlayerLocation(player);

                    double deltaX = playerLocation.getX() - entityLocation.getX();
                    double deltaY = playerLocation.getY() - entityLocation.getY();
                    double deltaZ = playerLocation.getZ() - entityLocation.getZ();

                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                    if (distance > distancePerTick) {
                        double ratio = distancePerTick / distance;
                        double x = (entityLocation.getX() + deltaX * ratio) + ThreadRandom.nextDouble(-0.1, 0.1);
                        double y = (entityLocation.getY() + deltaY * ratio) + ThreadRandom.nextDouble(-0.1, 0.1);
                        double z = (entityLocation.getZ() + deltaZ * ratio) + ThreadRandom.nextDouble(-0.1, 0.1);
                        entity.teleport(new Location(entity.getWorld(), x, y, z, entityLocation.getYaw(), entityLocation.getPitch()));
                    }
                    else {
                        entity.teleport(playerLocation.clone()
                                .add(
                                        ThreadRandom.nextDouble(-0.1, 0.1),
                                        ThreadRandom.nextDouble(-0.1, 0.1),
                                        ThreadRandom.nextDouble(-0.1, 0.1)
                                ));
                    }

                    tick += TASK_PERIOD;
                }
            }.runTaskTimer(0, TASK_PERIOD);
        }

        // Fx task
        new GameTask() {
            private int tick = collectDelay;

            @Override
            public void run() {
                if (tick <= 0) {
                    cancel();
                    return;
                }

                player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f + (tick * (1.8f / collectDelay)));
                tick -= TASK_PERIOD;
            }
        }.runTaskTimer(0, 2);

        player.schedule(() -> launchProjectile(player, damage.get()), collectDelay);
        player.addEffect(Effects.SLOW, 10, collectDelay);

        return Response.OK;
    }

    public void launchProjectile(GamePlayer player, double damage) {
        final Location location = getPlayerLocation(player);
        final ArmorStand entity = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setHelmet(new ItemStack(Material.MAGMA_BLOCK));
            self.setInvisible(true);
        });

        new GameTask() {
            private double distanceTravelled = 0.0d;

            @Override
            public void run() {
                if (entity.isDead() || distanceTravelled >= maxDistance) {
                    removeCancelExplode(entity.getLocation());
                    return;
                }

                final LivingGameEntity nearestEntity = Collect.nearestEntity(location, 1.5d, player);
                final Location fixedLocation = entity.getLocation().add(0.0d, 1.5d, 0.0d);

                if (nearestEntity != null || fixedLocation.getBlock().getType().isSolid()) {
                    removeCancelExplode(fixedLocation);
                    return;
                }

                // Move
                entity.teleport(location.add(location.getDirection().multiply(1)));
                player.spawnWorldParticle(location, Particle.LAVA, 10, 0.2d, 0.2d, 0.2d, 0.01f);

                distanceTravelled += 1.0d;
            }

            private void removeCancelExplode(Location location) {
                entity.remove();
                cancel();

                Collect.nearbyEntities(location, 3.0d).forEach(entity -> {
                    if (player.isTeammate(entity)) { // Damage self but not teammates
                        return;
                    }

                    entity.damage(damage, player, EnumDamageCause.GRAVITY_GUN);
                });

                player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);
                player.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.0f);
            }

        }.runTaskTimer(0, 1);

    }

    private Location getPlayerLocation(GamePlayer player) {
        return player.getLocationInFront(1.0d).subtract(0.0d, 0.5d, 0.0d);
    }
}
