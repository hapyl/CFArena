package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.LowAttributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

/**
 * Represents a custom game entity type.
 *
 * @param <T> - Type of the entity to spawn.
 *            <i>For multi-part entities, it's the main type of the entity.</i>
 */
public class GameEntityType<T extends LivingEntity> {

    private final String name;
    private final Class<T> clazz;
    private final Attributes attributes;

    private EntityType type;

    public GameEntityType(@Nonnull String name, @Nonnull Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
        this.type = EntityType.FRIENDLY;
        this.attributes = new LowAttributes();

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    /**
     * Gets the attributes for this type.
     * <p>
     * Upon creating an NamedGameEntity, the attributes are cloned to the entity created.
     *
     * @return the attributes for this type.
     */
    @Nonnull
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Gets the type of this entity.
     *
     * @return the type of this entity.
     * @see EntityType
     */
    @Nonnull
    public EntityType getType() {
        return type;
    }

    /**
     * Sets the type of this entity.
     *
     * @param type - New type.
     */
    @Nonnull
    public GameEntityType<T> setType(@Nonnull EntityType type) {
        this.type = type;
        return this;
    }

    /**
     * Called upon spawning the bukkit entity (or "handle") of this entity.
     * Useful for entity prepare, like putting armor, adding effects, etc.
     *
     * @param entity - Entity that will be spawned.
     */
    @Event
    public void onSpawn(@Nonnull T entity) {
    }

    /**
     * Called upon spawning this game entity.
     *
     * @param entity - This entity.
     */
    @Event
    public void onSpawn(@Nonnull NamedGameEntity<T> entity) {
    }

    /**
     * Declares how or what entity to create.
     * <p>
     * By default, a {@link NamedGameEntity} is created.
     *
     * @param bukkitEntity - Bukkit entity, or "handle" of this entity.
     * @return A newly created game entity.
     */
    @Nonnull
    public NamedGameEntity<T> create(@Nonnull T bukkitEntity) {
        return new NamedGameEntity<>(this, bukkitEntity);
    }

    /**
     * Gets the hologram offset from entity's eyes to put the hologram at.
     * Override it for taller or multi-layer entities.
     *
     * @return the hologram offset from entity's eyes.
     */
    public double getHologramOffset() {
        return 0.25d;
    }

    /**
     * Gets the name of this entity.
     *
     * @return the name of this entity.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets the name of this entity.
     *
     * @return the name of this entity.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the name of this entity with type formatter applied to it.
     *
     * @return the name of this entity with type formatter applied to it.
     */
    public String getNameFormatted() {
        return type.formatName(name);
    }

    /**
     * Spawns the entity.
     *
     * @param location - Where to spawn.
     * @return The new game entity.
     */
    @Nonnull
    public final NamedGameEntity<T> spawn(@Nonnull Location location) {
        final T spawn = bukkitSpawn(location);

        return Manager.current().createEntity(spawn, new ConsumerFunction<>() {
            @Nonnull
            @Override
            public NamedGameEntity<T> apply(LivingEntity entity) {
                return create(spawn);
            }

            @Override
            public void andThen(NamedGameEntity<T> entity) {
                onSpawn(entity);
            }
        });
    }

    protected T bukkitSpawn(@Nonnull Location location) {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("cannot spawn in an unloaded world");
        }

        return world.spawn(location, clazz, entity -> {
            Manager.current().addIgnored(entity);
            onSpawn(entity);
        });
    }

    public static <T extends LivingEntity> GameEntityType<T> of(@Nonnull String name, @Nonnull Class<T> clazz) {
        return new GameEntityType<>(name, clazz);
    }

    public static <T extends LivingEntity> GameEntityType<T> of(@Nonnull Class<T> clazz) {
        return new GameEntityType<>("", clazz);
    }
}
