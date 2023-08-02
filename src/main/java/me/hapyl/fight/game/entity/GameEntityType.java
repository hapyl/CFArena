package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.LowAttributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

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

    public Attributes getAttributes() {
        return attributes;
    }

    public EntityType getType() {
        return type;
    }

    public GameEntityType<T> setType(EntityType type) {
        this.type = type;
        return this;
    }

    @Event
    public void onSpawn(@Nonnull LivingEntity entity) {
    }

    @Event
    public void onSpawn(@Nonnull NamedGameEntity<T> entity) {
    }

    @Nonnull
    public NamedGameEntity<T> create(@Nonnull T entity) {
        return new NamedGameEntity<>(this, entity);
    }

    public double getHologramOffset() {
        return 0.25d;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getNameFormatted() {
        return type.formatName(name);
    }

    @Nonnull
    public final NamedGameEntity<T> create0(Location location) {
        final T spawn = spawn(location);

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

    protected T spawn(Location location) {
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
