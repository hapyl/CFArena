package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.entity.Piglin;

import javax.annotation.Nonnull;

public class AngryPiglin extends NamedEntityType {
    public AngryPiglin(@Nonnull Key key) {
        super(key, "Angry Piglin");

        final Attributes attributes = getAttributes();
        attributes.setMaxHealth(100);
        attributes.setAttack(50);

        setType(EntityType.HOSTILE);
    }

    @Nonnull
    @Override
    public NamedGameEntity<?> create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.PIGLIN, Entity::new);
    }

    private class Entity extends NamedGameEntity<Piglin> {

        private static final int MAX_TICK = Tick.fromSecond(33);

        public Entity(Piglin entity) {
            super(AngryPiglin.this, entity);
        }

        @Override
        public void onSpawn() {
            entity.setImmuneToZombification(true);
        }

        @Override
        public void onTick(int tick) {
            attributes.addSilent(AttributeType.ATTACK, 0.025);

            if (getTick() >= MAX_TICK) {
                forceRemove();
                return;
            }
        }

        @Override
        public String[] getExtraHologramLines() {
            return new String[] {
                    "&4Attack Boost &l" + AttributeType.ATTACK.toString(attributes.get(AttributeType.ATTACK)) + "%",
                    "&aTime Left: &l" + ((MAX_TICK - getTick()) / 20) + "s"
            };
        }
    }
}
