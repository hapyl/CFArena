package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.EntityMemory;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MemoryKey;
import me.hapyl.fight.util.Vectors;
import org.bukkit.attribute.Attribute;

import javax.annotation.Nonnull;

public class MovementContainment extends Effect {

    private static final MemoryKey jumpStrength = new MemoryKey("jump_strength");

    private final double speedConstant = 100;

    public MovementContainment(@Nonnull String string) {
        super(string, EffectType.NEGATIVE);
    }

    public MovementContainment() {
        this("Movement Containment");
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.subtractSilent(AttributeType.SPEED, speedConstant);

        final EntityMemory memory = entity.getMemory();
        memory.remember(jumpStrength, entity.getAttributeValue(Attribute.GENERIC_JUMP_STRENGTH));

        entity.setAttributeValue(Attribute.GENERIC_JUMP_STRENGTH, 0.0d);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.addSilent(AttributeType.SPEED, speedConstant);

        entity.setAttributeValue(Attribute.GENERIC_JUMP_STRENGTH, entity.getMemory().forget(jumpStrength, 0.41999998688697815d));
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        entity.setVelocity(Vectors.DOWN);
    }
}
