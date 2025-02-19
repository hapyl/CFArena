package me.hapyl.fight.game;

import me.hapyl.fight.annotate.ConstantField;
import me.hapyl.fight.game.attribute.temper.AttributeTemper;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.ChatColor;

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

    @ConstantField(
            description = "A constant for cooldown to display 'indefinite'.",
            applicableTo = { Talent.class }
    )
    int MAX_COOLDOWN = 12000;

    @ConstantField(
            description = "A constant for very large cooldown.",
            applicableTo = { Talent.class }
    )
    int INDEFINITE_COOLDOWN = 999999;

    @ConstantField(
            description = "The default color used in lore.",
            applicableTo = { String.class }
    )
    String DEFAULT_LORE_COLOR = ChatColor.GRAY.toString();
}
