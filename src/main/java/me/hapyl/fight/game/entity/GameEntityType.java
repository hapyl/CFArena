package me.hapyl.fight.game.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class GameEntityType<T extends LivingEntity> {

    private final Class<T> clazz;

    public GameEntityType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void onSpawn(@Nonnull T entity) {
    }

    public void onSpawn(@Nonnull LivingGameEntity entity) {
    }

    protected T spawn(Location location) {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("cannot spawn in an unloaded world");
        }

        return world.spawn(location, clazz, this::onSpawn);
    }

    public static <T extends LivingEntity> GameEntityType<T> of(@Nonnull Class<T> clazz) {
        return new GameEntityType<>(clazz);
    }
}
