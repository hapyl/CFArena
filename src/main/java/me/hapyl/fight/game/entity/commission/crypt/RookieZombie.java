package me.hapyl.fight.game.entity.commission.crypt;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.LowAttributes;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import javax.annotation.Nonnull;

public class RookieZombie extends CommissionEntityType {
    public RookieZombie(@Nonnull Key key) {
        super(
                key, "Rookie",
                new LowAttributes()
                        .put(AttributeType.MAX_HEALTH, 10)
                        .put(AttributeType.ATTACK, 2)
        );
    }

    @Nonnull
    @Override
    public CommissionEntity create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.ZOMBIE, RookieZombieEntity::new);
    }

    public class RookieZombieEntity extends CommissionEntity {

        public RookieZombieEntity(@Nonnull LivingEntity entity) {
            super(RookieZombie.this, entity);
        }

        @Nonnull
        @Override
        public Zombie entity() {
            return (Zombie) super.entity();
        }
    }
}
