package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Indicates that this component can return additional information to be displayed in the player's UI.
 */
public interface UIComplexComponent extends UIComponent {

    @Nullable
    List<String> getStrings(@Nonnull GamePlayer player);

    @Override
    default @Nonnull String getString(@Nonnull GamePlayer player) {
        final List<String> strings = getStrings(player);
        final StringBuilder builder = new StringBuilder();

        if (strings == null) {
            return "";
        }

        int i = 0;
        for (String string : strings) {
            if (string == null || string.isEmpty()) {
                continue;
            }

            if (i++ != 0) {
                builder.append(" %s ".formatted(UIFormat.DIV));
            }

            builder.append(string);
        }

        return builder.toString();
    }
}
