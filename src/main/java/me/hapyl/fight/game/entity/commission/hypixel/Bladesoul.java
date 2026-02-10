package me.hapyl.fight.game.entity.commission.hypixel;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.MultiPartCommissionEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import org.bukkit.Location;
import org.bukkit.entity.WitherSkeleton;

import javax.annotation.Nonnull;

public class Bladesoul extends CommissionEntityType {

    public Bladesoul(@Nonnull Key key) {
        super(key, "Bladesoul");

        setType(EntityType.MINIBOSS);
    }

    @Override
    public double getHologramOffset() {
        return 1.5d;
    }

    @Nonnull
    @Override
    public BladesoulEntity create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.WITHER_SKELETON, BladesoulEntity::new);
    }

    public class BladesoulEntity extends MultiPartCommissionEntity {

        public BladesoulEntity(@Nonnull WitherSkeleton entity) {
            super(Bladesoul.this, entity);

            createPart(
                    Entities.BLAZE, self -> {
                        self.flip();
                        self.setAI(false);

                        BladesoulEntity.this.addPassenger(self.entity);
                    }
            );
        }

        @Nonnull
        @Override
        public WitherSkeleton entity() {
            return (WitherSkeleton) super.entity();
        }
    }
}
