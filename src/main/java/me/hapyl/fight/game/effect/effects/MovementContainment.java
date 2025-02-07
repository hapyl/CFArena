package me.hapyl.fight.game.effect.effects;

import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.EntityMemory;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MemoryKey;
import org.bukkit.attribute.Attribute;

import javax.annotation.Nonnull;

public class MovementContainment extends Effect {

    private final MemoryKey jumpStrength = new MemoryKey("jump_strength");
    private final MemoryKey movementSpeed = new MemoryKey("move_speed");

    private final double speedConstant = 100;

    public MovementContainment(@Nonnull String string) {
        super(string, EffectType.NEGATIVE);
    }

    public MovementContainment() {
        this("Movement Containment");
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        final EntityMemory memory = entity.getMemory();
        memory.remember(jumpStrength, entity.getAttributeValue(Attribute.JUMP_STRENGTH));
        memory.remember(movementSpeed, entity.getAttributeValue(Attribute.MOVEMENT_SPEED));

        entity.setAttributeValue(Attribute.MOVEMENT_SPEED, 0.0d);
        entity.setAttributeValue(Attribute.JUMP_STRENGTH, 0.0d);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        final EntityMemory memory = entity.getMemory();

        entity.setAttributeValue(Attribute.JUMP_STRENGTH, memory.forget(jumpStrength, 0.41999998688697815d));
        entity.setAttributeValue(Attribute.MOVEMENT_SPEED, memory.forget(movementSpeed, 0.2d));
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        entity.setVelocity(Vectors.DOWN);
    }
}
