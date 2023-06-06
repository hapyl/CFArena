package me.hapyl.fight.game.talents.archive.tamer;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Represents a pack of entities that are tamed by a player.
 */
public class TamerPack {

    // Max distance the minion can be from the player, if exceeded, it will be teleported.
    private final double maxDistance = 30;

    // Represents XZ offsets from the player's location.
    private final double[][] relativeOffsets = {
            { -1.0d, 0.0d }, //
            { 1.0d, 0.0d },
            { 0.0d, -1.0d },
            { 0.0d, 1.0d },
            { 1.0d, 1.0d },
            { -1.0d, 1.0d },
            { 1.0d, -1.0d },
            { -1.0d, -1.0d }
    };

    protected final Player player;
    private final Pack pack;
    private final Set<LivingEntity> entities;

    public TamerPack(Pack pack, Player player) {
        this.player = player;
        this.pack = pack;
        this.entities = Sets.newConcurrentHashSet();

        new GameTask() {
            @Override
            public void run() {
                if (!exists()) {
                    this.cancel();
                    return;
                }

                pack.onTick(player, TamerPack.this);
            }
        }.runTaskTimer(0, 1);
    }

    /**
     * Returns true if the pack exists.
     *
     * @return true if the pack exists.
     */
    public boolean exists() {
        return !entities.isEmpty();
    }

    /**
     * Returns true any of the entities are alive.
     *
     * @return true if any of the entities are alive.
     */
    public boolean isAlive() {
        for (LivingEntity entity : entities) {
            if (!entity.isDead()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all entities in the pack, no matter if they are dead or not.
     *
     * @return all entities in the pack.
     */
    public Set<LivingEntity> getEntities() {
        return entities;
    }

    /**
     * Returns the name of the pack.
     *
     * @return the name of the pack.
     */
    public String getName() {
        return pack.getName();
    }

    public boolean isInPack(LivingEntity entity) {
        return entities.contains(entity);
    }

    public final void updateEntitiesNames(Player player) {
        entities.forEach(entity -> {
            if (entity.isDead()) {
                return;
            }

            entity.setCustomName(Chat.format(
                    "&a%s's %s &8| &c%s❤&8",
                    player.getName(),
                    Chat.capitalize(entity.getType()),
                    BukkitUtils.decimalFormat(entity.getHealth())
            ));
        });
    }

    public void spawn() {
        final Location location = player.getLocation();

        for (int i = 0; i < pack.spawnAmount(); i++) {
            final Location relative = addRelativeOffset(location, i);
            pack.spawnEntity(player, relative, this);
        }
    }

    /**
     * Removes entities.
     */
    public final void removeAll() {
        entities.forEach(Entity::remove);
        entities.clear();
    }

    /**
     * Removes entity from the pack.
     *
     * @param entity entity to remove.
     */
    public final void remove(LivingEntity entity) {
        entities.remove(entity);

        if (!entity.isDead()) {
            entity.remove();
        }
    }

    /**
     * Removes entities with effect.
     */
    public final void recall() {
        entities.forEach(entity -> {
            final Location location = entity.getLocation().add(0.0d, 1.0d, 0.0d);
            PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 10, 0.5d, 0.5d, 0.5d, 0.2f);
            PlayerLib.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.2f);
            entity.remove();
        });
        entities.clear();
    }

    protected final Location addRelativeOffset(Location location, int offset) {
        final double[] doubles = relativeOffsets[offset >= relativeOffsets.length ? 0 : offset];
        return location.clone().add(doubles[0], 0.0d, doubles[1]);
    }

    protected <T extends LivingEntity> T createEntity(Location location, Entities<T> entity) {
        return createEntity(location, entity, null);
    }

    protected <T extends LivingEntity> T createEntity(Location location, Entities<T> entity, @Nullable Consumer<T> consumer) {
        return entity.spawn(location, self -> {
            self.setMaxHealth(64.0d);
            self.setHealth(64.0d);
            self.setCustomNameVisible(true);

            // Add helmet if possible
            Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                equipment.setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setUnbreakable()
                        .setLeatherArmorColor(Color.fromRGB(509659))
                        .build());
            });

            // Apply consumer if not null
            Nulls.runIfNotNull(consumer, cons -> {
                cons.accept(self);
            });

            // Tag to identify pack entities
            self.addScoreboardTag("pack_entity");

            entities.add(self);
        });
    }

    /**
     * Returns the nearest target for this pack.
     *
     * @return the nearest target or null if none found.
     */
    @Nullable
    public LivingEntity findNearestTarget() {
        LivingEntity nearest = Utils.getNearestPlayer(getLocation(), maxDistance, player);

        // If not player nearby then find the nearest mob
        if (nearest == null) {
            nearest = Utils.getNearestLivingEntity(getLocation(), maxDistance, entity -> !isInPack(entity) && entity != player);
        }

        return nearest;
    }

    /**
     * Get <b>FIRST</b> entity in a pack with a specific type.
     *
     * @param type - Type.
     * @return the entity or null if none found.
     */
    @Nullable
    public LivingEntity getEntity(EntityType type) {
        for (LivingEntity entity : entities) {
            if (entity.getType() == type) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Gets all entities in a pack with a specific type.
     *
     * @param type - Type.
     * @return the entities or empty set if none found.
     */
    @Nonnull
    public Set<LivingEntity> getEntities(EntityType type) {
        final Set<LivingEntity> set = Sets.newHashSet();

        for (LivingEntity entity : entities) {
            if (entity.getType() == type) {
                set.add(entity);
            }
        }

        return set;
    }

    public Location getLocation() {
        for (LivingEntity entity : entities) {
            return entity.getLocation();
        }

        return BukkitUtils.defLocation(0.0d, 0.0d, 0.0d);
    }

    public Pack getPack() {
        return pack;
    }
}

