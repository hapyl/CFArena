package me.hapyl.fight.database.rank;

import me.hapyl.fight.game.talents.InsteadOfNull;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public interface RankFormatter {

    @Nonnull
    @InsteadOfNull("Empty String")
    String prefix();

    @Nonnull
    @InsteadOfNull("Empty String")
    ChatColor nameColor();

    @Nonnull
    @InsteadOfNull("Empty String")
    String textColor();

    boolean allowFormatting();

    static RankFormatter of(@Nonnull String prefix) {
        return of(prefix, ChatColor.WHITE, "&f", false);
    }

    static RankFormatter of(@Nonnull String prefix, @Nonnull ChatColor nameColor) {
        return of(prefix, nameColor, "&f", false);
    }

    static RankFormatter of(@Nonnull String prefix, @Nonnull ChatColor nameColor, @Nonnull String textColor) {
        return of(prefix, nameColor, textColor, false);
    }

    static RankFormatter of(@Nonnull String prefix, @Nonnull ChatColor nameColor, @Nonnull String textColor, boolean allowFormat) {
        return new RankFormatter() {
            @Nonnull
            @Override
            public String prefix() {
                return prefix;
            }

            @Nonnull
            @Override
            public ChatColor nameColor() {
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
