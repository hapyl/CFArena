package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.EntityMemory;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MemoryKey;
import org.bukkit.attribute.Attribute;

import javax.annotation.Nonnull;

public class Immovable extends Effect {

    private final MemoryKey key = new MemoryKey("immovable_kb");

    public Immovable() {
        super("Immovable", EffectType.NEUTRAL);

        setDescription("""
                Entities aren't affected by knockback.
                """);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        final EntityMemory memory = entity.getMemory();

        memory.remember(key, entity.getAttributeValue(Attribute.KNOCKBACK_RESISTANCE));
        entity.setAttributeValue(Attribute.KNOCKBACK_RESISTANCE, 1.0d);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        final EntityMemory memory = entity.getMemory();
        final double value = memory.forget(key, Double.class, 0.0d);

        entity.setAttributeValue(Attribute.KNOCKBACK_RESISTANCE, value);
    }
}
