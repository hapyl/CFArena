package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.EntityMemory;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MemoryKey;
import org.bukkit.attribute.Attribute;

import javax.annotation.Nonnull;

// FIXME -> Replace with attribute
public class Immovable extends GameEffect {

    private final MemoryKey key = new MemoryKey("immovable_kb");

    public Immovable() {
        super("Immovable");
        setDescription("Players are not affected by knockback.");
        setPositive(false); // I mean kinda positive but kinda not you know but there is only one character that can do this, and it's their ability that you know kinda to make enemies like bad, so you can like easier hit them and deal damaje :|
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        final EntityMemory memory = entity.getMemory();

        memory.remember(key, entity.getAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE));
        entity.setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 1.0d);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        final EntityMemory memory = entity.getMemory();
        final double value = memory.forget(key, Double.class, 0.0d);

        entity.setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, value);
    }
}
