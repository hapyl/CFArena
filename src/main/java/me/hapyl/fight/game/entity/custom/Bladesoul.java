package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.MultiPartLivingGameEntity;
import me.hapyl.fight.game.entity.NamedGameEntity;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.entity.WitherSkeleton;

import javax.annotation.Nonnull;

public class Bladesoul extends GameEntityType<WitherSkeleton> {

    public Bladesoul() {
        super("Bladesoul", WitherSkeleton.class);

        setType(EntityType.MINIBOSS);
    }

    @Override
    public double getHologramOffset() {
        return 1.5d;
    }

    @Nonnull
    @Override
    public NamedGameEntity<WitherSkeleton> create(@Nonnull WitherSkeleton entity) {
        return new BladesoulEntity(entity);
    }

    private class BladesoulEntity extends MultiPartLivingGameEntity<WitherSkeleton> {

        public BladesoulEntity(@Nonnull WitherSkeleton entity) {
            super(Bladesoul.this, entity);

            createPart(Entities.BLAZE, self -> {
                self.flip();
                self.setAI(false);

                BladesoulEntity.this.addPassenger(self.entity);
            });
        }
    }
}
