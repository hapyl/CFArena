package me.hapyl.fight.game.entity.commission;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.attribute.LowAttributes;
import me.hapyl.fight.game.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents a named game entity type.
 */
@AutoRegisteredListener
public abstract class CommissionEntityType implements Keyed, Described {

    private final Key key;
    private final String name;

    @Nonnull protected String description;
    @Nonnull protected BaseAttributes attributes;

    @Nullable protected EntityEquipment equipment;

    private EntityType type;

    public CommissionEntityType(@Nonnull Key key, @Nonnull String name) {
        this(key, name, new LowAttributes());
    }

    public CommissionEntityType(@Nonnull Key key, @Nonnull String name, @Nonnull BaseAttributes attributes) {
        this.key = key;
        this.name = name;
        this.type = EntityType.HOSTILE; // Most entities are hostile
        this.attributes = attributes;
        this.description = "";

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    public boolean isAttributesScalable() {
        return true;
    }

    public void equipment(@Nullable EntityEquipment equipment) {
        this.equipment = equipment;
    }

    @Nullable
    public EntityEquipment equipment() {
        return equipment;
    }

    /**
     * Gets the attributes for this type.
     * <p>
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
     * Creates a new {@link CommissionEntity} at the given level.
     *
     * @param location - The location to create the entity at.
     * @param level    - The level of the entity.
     * @return a new commission entity.
     */
    @Nonnull
    public final CommissionEntity create(@Nonnull Location location, int level) {
        final CommissionEntity entity = create(location);
        entity.level(level);

        return entity;
    }

    /**
     * Creates a new {@link CommissionEntity} based on this blueprint.
     *
     * @param location - The location to create the entity at.
     * @return a new commission entity.
     */
    @Nonnull
    public abstract CommissionEntity create(@Nonnull Location location);

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

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(@Nonnull String description) {
        this.description = description;
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
    @Nonnull
    public String getNameFormatted() {
        return type.formatName(name);
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CommissionEntityType that = (CommissionEntityType) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(key);
    }
}
