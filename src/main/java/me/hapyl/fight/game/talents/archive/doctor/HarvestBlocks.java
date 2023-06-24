package me.hapyl.fight.game.talents.archive.doctor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.archive.doctor.ElementType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Cuboid;
import me.hapyl.spigotutils.module.math.nn.DoubleDouble;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HarvestBlocks extends Talent {

    private final int TASK_PERIOD = 2;
    @DisplayField private final short maximumBlocks = 10;
    @DisplayField private final int collectDelay = 30;

    public HarvestBlocks() {
        super("Block Harvest");

        setDescription("""
                Quickly gather resources from up to {maximumBlocks} nearby blocks, then combine them in one big pile before throwing it at your enemies.
                                        
                &b;;The damage is based on the number of blocks gathered.
                """);

        setCooldownSec(30);
        setPoint(5);

        setItem(Material.IRON_PICKAXE);
    }

    @Override
    public Response execute(Player player) {
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

        blockMap.forEach((b, e) -> {
            entities.add(Entities.ARMOR_STAND_MARKER.spawn(b.getLocation().add(0.5, 0.5, 0.5), entity -> {
                entity.setMarker(true);
                entity.setGravity(false);
                entity.setInvulnerable(true);
                //entity.setGlowing(true);
                entity.setInvisible(true);
                Nulls.runIfNotNull(entity.getEquipment(), eq -> eq.setHelmet(ItemBuilder.of(b.getType()).asIcon()));
            }));

            damage.addAndGet(e.getElement().getDamage() / 2.0d);
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
                        this.cancel();
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
                    this.cancel();
                    return;
                }

                PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f + (tick * (1.8f / collectDelay)));
                tick -= TASK_PERIOD;
            }
        }.runTaskTimer(0, 2);

        GameTask.runLater(() -> launchProjectile(player, damage.get()), collectDelay);
        PlayerLib.addEffect(player, PotionEffectType.SLOW, collectDelay, 10);

        return Response.OK;
    }

    private Location getPlayerLocation(Player player) {
        return LocationHelper.getInFront(player.getLocation(), 1.0d).subtract(0.0d, 0.5d, 0.0d);
    }

    public void launchProjectile(Player player, double damage) {
        final Location location = getPlayerLocation(player);
        final ArmorStand entity = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            Nulls.runIfNotNull(self.getEquipment(), eq -> {
                eq.setHelmet(ItemBuilder.of(Material.MAGMA_BLOCK).asIcon());
            });

            self.setInvisible(true);
        });

        new GameTask() {
            private double distanceTravelled = 0.0d;
            private final double maxDistance = 20.0d;

            @Override
            public void run() {
                if (entity.isDead() || distanceTravelled >= maxDistance) {
                    entity.remove();
                    this.cancel();
                    return;
                }

                final LivingEntity nearestEntity = Collect.nearestLivingEntity(location, 1.5d, player);
                final Location fixedLocation = entity.getLocation().add(0.0d, 1.5d, 0.0d);

                if (fixedLocation.getBlock().getType().isOccluding()) {
                    entity.remove();
                    this.cancel();
                    return;
                }

                if (nearestEntity != null) {
                    GamePlayer.damageEntity(nearestEntity, damage, player, EnumDamageCause.GRAVITY_GUN);
                    entity.remove();
                    this.cancel();
                    return;
                }

                // Move
                entity.teleport(location.add(location.getDirection().multiply(1)));
                PlayerLib.spawnParticle(location, Particle.LAVA, 10, 0.2d, 0.2d, 0.2d, 0.01f);

                distanceTravelled += 1.0d;
            }
        }.runTaskTimer(0, 1);

    }
}
