package me.hapyl.fight.database.rank;

import me.hapyl.fight.game.talents.InsteadOfNull;

import javax.annotation.Nonnull;

public interface RankFormatter {

    @Nonnull
    @InsteadOfNull("Empty String")
    String prefix();

    @Nonnull
    @InsteadOfNull("Empty String")
    String nameColor();

    @Nonnull
    @InsteadOfNull("Empty String")
    String textColor();

    boolean allowFormatting();

    static RankFormatter of(String prefix) {
        return of(prefix, prefix, "&f", false);
    }

    static RankFormatter of(String prefix, String nameColor) {
        return of(prefix, nameColor, "&f", false);
    }

    static RankFormatter of(String prefix, String nameColor, String textColor) {
        return of(prefix, nameColor, textColor, false);
    }

    static RankFormatter of(String prefix, String nameColor, String textColor, boolean allowFormat) {
        return new RankFormatter() {
            @Nonnull
            @Override
            public String prefix() {
                return prefix;
            }

            @Nonnull
            @Override
            public String nameColor() {
                return nameColor;
            }

            @Nonnull
            @Override
            public String textColor() {
                return textColor;
            }

            @Override
            public boolean allowFormatting() {
                return allowFormat;
            }
        };
    }

}
