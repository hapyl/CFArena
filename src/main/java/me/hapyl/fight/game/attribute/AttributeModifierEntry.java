package me.hapyl.fight.game.attribute;

import javax.annotation.Nonnull;

public record AttributeModifierEntry(@Nonnull AttributeType attributeType, @Nonnull ModifierType modifierType, double value) {
}
