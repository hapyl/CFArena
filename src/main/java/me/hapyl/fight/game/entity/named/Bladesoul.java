package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.MultiPartLivingGameEntity;
import org.bukkit.Location;
import org.bukkit.entity.WitherSkeleton;

import javax.annotation.Nonnull;

public class Bladesoul extends NamedEntityType {

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
    public NamedGameEntity<?> create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.WITHER_SKELETON, Entity::new);
    }

    private class Entity extends MultiPartLivingGameEntity<WitherSkeleton> {

        public Entity(@Nonnull WitherSkeleton entity) {
            super(Bladesoul.this, entity);

            createPart(Entities.BLAZE, self -> {
                self.flip();
                self.setAI(false);

                Entity.this.addPassenger(self.entity);
            });
        }
    }
}
