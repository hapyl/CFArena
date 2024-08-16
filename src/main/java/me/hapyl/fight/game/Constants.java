package me.hapyl.fight.game;

import me.hapyl.fight.annotate.ConstantField;
import me.hapyl.fight.game.attribute.temper.AttributeTemper;
import me.hapyl.fight.game.effect.Effect;

/**
 * A utility class with project constant values.
 * <br>
 * A constant field must be annotated with {@link ConstantField}.
 */
public interface Constants {

    @ConstantField(
            description = "A constant for infinite duration.",
            applicableTo = { Effect.class, AttributeTemper.class }
    )
    int INFINITE_DURATION = -1;

}
