package me.hapyl.fight.game.attribute;

import javax.annotation.Nonnull;

public interface AttributeModifierHandler {
    
    void handle(@Nonnull AttributeModifier modifier);
    
}
