package me.hapyl.fight.game.attribute;

import javax.annotation.Nonnull;

public interface AttributeFormat {
    
    AttributeFormat FLAT = "%,.0f"::formatted;
    AttributeFormat PERCENTAGE = "%,.0f%%"::formatted;
    
    @Nonnull
    String format(double value);
    
}
