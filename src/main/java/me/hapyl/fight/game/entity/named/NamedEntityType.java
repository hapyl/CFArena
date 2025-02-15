package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.attribute.LowAttributes;
import me.hapyl.fight.game.entity.ConsumerFunction;
import me.hapyl.fight.game.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

/**
 * Represents a named game entity type.
 */
public abstract class NamedEntityType implements Keyed {

    private final Key key;
    private final String name;
    private final BaseAttributes attributes;

    private EntityType type;

    public NamedEntityType(@Nonnull Key key, @Nonnull String name) {
        this.key = key;
        this.name = name;
        this.type = EntityType.FRIENDLY;
        this.attributes = new LowAttributes();

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    /**
     * Gets the attributes for this type.
     * <p>
     * Upon creating an NamedGameEntity, the attributes are cloned to the entity created.
     *
     * @return the attributes for this type.
     */
    @Nonnull
    public BaseAttributes getAttributes() {
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
    public void setType(@Nonnull EntityType type) {
        this.type = type;
    }

    /**
     * @see CF#createEntity(Location, Entities, ConsumerFunction)
     */
    @Nonnull
    public abstract NamedGameEntity<?> create(@Nonnull Location location);

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

}
