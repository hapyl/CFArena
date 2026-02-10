package me.hapyl.fight.game.entity.commission.crypt;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class MegaGolem extends CommissionEntityType {
    public MegaGolem(@Nonnull Key key) {
        super(
                key, "Mega Golem",
                new BaseAttributes()
                        .put(AttributeType.MAX_HEALTH, 150)
                        .put(AttributeType.DEFENSE, 150)
                        .put(AttributeType.ATTACK, 150)
        );
    }

    @Nonnull
    @Override
    public CommissionEntity create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.IRON_GOLEM, MegaGolemEntity::new);
    }

    public class MegaGolemEntity extends CommissionEntity {
        public MegaGolemEntity(@Nonnull LivingEntity entity) {
            super(MegaGolem.this, entity);

        }
    }
}
