package me.hapyl.fight.database.rank;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.talents.InsteadOfNull;

import javax.annotation.Nonnull;

public interface RankFormatter {

    @Nonnull
    @InsteadOfNull("Empty String")
    String prefix();

    @Nonnull
    @InsteadOfNull("Empty String")
    Color nameColor();

    @Nonnull
    @InsteadOfNull("Empty String")
    Color textColor();

    boolean allowFormatting();

    static RankFormatter of(@Nonnull String prefix) {
        return of(prefix, Color.WHITE, Color.WHITE, false);
    }

    static RankFormatter of(@Nonnull String prefix, @Nonnull Color nameColor) {
        return of(prefix, nameColor, Color.WHITE, false);
    }

    static RankFormatter of(@Nonnull String prefix, @Nonnull Color nameColor, @Nonnull Color textColor) {
        return of(prefix, nameColor, textColor, false);
    }

    static RankFormatter of(@Nonnull String prefix, @Nonnull Color nameColor, @Nonnull Color textColor, boolean allowFormat) {
        return new RankFormatter() {
            @Nonnull
            @Override
            public String prefix() {
                return prefix;
            }

            @Nonnull
            @Override
            public Color nameColor() {
                return nameColor;
            }

            @Nonnull
            @Override
            public Color textColor() {
                return textColor;
            }

            @Override
            public boolean allowFormatting() {
                return allowFormat;
            }
        };
    }

}
