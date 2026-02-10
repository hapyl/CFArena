package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;

public class MovementContainmentEffect extends Effect {
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("movement_containment"), true);
    
    MovementContainmentEffect(@Nonnull Key key) {
        super(key, "?", "Movement Containment", Color.SLATE_GRAY, Type.NEGATIVE);
        
        setDescription("""
                       Prevents the entity from moving.
                       """);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().getAttributes().addModifier(
                modifierSource, Constants.INFINITE_DURATION, effect.applier(), modifier -> modifier
                        .of(AttributeType.SPEED, ModifierType.FLAT, -1_000)
                        .of(AttributeType.JUMP_STRENGTH, ModifierType.FLAT, -1_000)
        );
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        effect.entity().getAttributes().removeModifier(modifierSource);
    }
}
