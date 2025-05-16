package me.hapyl.fight.game.attribute;

import javax.annotation.Nonnull;
import java.util.Random;

public class AttributeRandom extends Random {
    
    private final BaseAttributes attributes;
    
    AttributeRandom(@Nonnull BaseAttributes attributes) {
        this.attributes = attributes;
    }
    
    public boolean checkBound(@Nonnull AttributeType type) {
        return checkBound(attributes.normalized(type));
    }
    
    public boolean checkBound(double chance) {
        return chance >= 1 || (chance > 0 && nextDouble() < chance);
    }
    
}
