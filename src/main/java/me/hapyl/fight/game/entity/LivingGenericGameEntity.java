package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.attribute.BaseAttributes;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class LivingGenericGameEntity<T extends LivingEntity> extends LivingGameEntity {

    public final T entity;

    public LivingGenericGameEntity(@Nonnull T entity) {
        this(entity, new BaseAttributes(entity));
    }

    public LivingGenericGameEntity(@Nonnull T entity, @Nonnull BaseAttributes attributes) {
        super(entity, attributes);

        this.entity = entity;
    }
}
