package me.hapyl.fight.game.attribute;

import javax.annotation.Nonnull;
import java.util.Random;

public class AttributeRandom extends Random {

    private final BaseAttributes attributes;

    public AttributeRandom(BaseAttributes attributes) {
        this.attributes = attributes;
    }

    public boolean checkBound(@Nonnull AttributeType type) {
        final double value = type.get(attributes);

        return checkBound(value);
    }

    public boolean checkBound(double bound) {
        if (bound < 0.0d) {
            return false;
        }
        else if (bound >= 1.0d) {
            return true;
        }

        final double value = nextDouble(0.0d, 1.0d);
        return value < bound;
    }

}
