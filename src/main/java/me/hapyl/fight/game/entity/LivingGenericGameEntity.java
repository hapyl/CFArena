package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.attribute.Attributes;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class LivingGenericGameEntity<T extends LivingEntity> extends LivingGameEntity {

    public final T entity;

    public LivingGenericGameEntity(@Nonnull T entity) {
        this(entity, new Attributes(entity));
    }

    public LivingGenericGameEntity(@Nonnull T entity, @Nonnull Attributes attributes) {
        super(entity, attributes);

        this.entity = entity;
    }
}
