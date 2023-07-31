package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.Manager;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import javax.annotation.Nonnull;

// Stores both custom and vanilla entity types
public enum GameEntities {

    // Custom


    // Vanilla
    ZOMBIE(GameEntityType.of(Zombie.class)),
    ;

    private final GameEntityType<?> type;

    GameEntities(GameEntityType<?> clazz) {
        this.type = clazz;
    }

    @Nonnull
    public final GameEntity spawn(@Nonnull Location location) {
        final LivingEntity entity = type.spawn(location);

        return Manager.current().createEntity(entity, new ConsumerFunction<LivingEntity, LivingGameEntity>() {
            @Nonnull
            @Override
            public LivingGameEntity apply(LivingEntity entity) {
                return new LivingGameEntity(entity);
            }

            @Override
            public void andThen(LivingGameEntity livingGameEntity) {
                type.onSpawn(livingGameEntity);
            }
        });
    }

}
