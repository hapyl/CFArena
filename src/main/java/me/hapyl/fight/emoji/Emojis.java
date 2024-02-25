package me.hapyl.fight.emoji;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.rank.PlayerRank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public enum Emojis {

    // Default
    PEACE("peace", "&2✌"),

    // Vip
    SMILE("smile", "&a🙂", PlayerRank.VIP),
    FROWN("frown", "&c🙁", PlayerRank.VIP),
    HEART("heart", "&c❤", PlayerRank.VIP),
    LOVE("love", "&d🥰", PlayerRank.VIP),

    // Premium
    SNOWFLAKE("snowflake", "&b❄", PlayerRank.PREMIUM),

    ;

    private static final String prefixChar = ":";

    private final String text;
    private final String emoji;
    private final PlayerRank rank;

    Emojis(String text, String emoji) {
        this(text, emoji, PlayerRank.DEFAULT);
    }

    Emojis(String text, String emoji, PlayerRank rank) {
        this.text = prefixChar + text + prefixChar;
        this.emoji = emoji;
        this.rank = rank;
    }

    @Nonnull
    public String getText() {
        return text;
    }

    @Nonnull
    public String getEmoji() {
        return emoji;
    }

    @Nonnull
    public PlayerRank getRank() {
        return rank;
    }

    @Nullable
    public static Emojis byText(@Nonnull String text, @Nonnull PlayerRank rank) {
        for (Emojis emoji : values()) {
            if (emoji.text.equals(text) && rank.isOrHigher(emoji.rank)) {
                return emoji;
            }
        }

        return null;
    }

    @Nonnull
    public static String replaceEmojis(@Nonnull String message, @Nonnull PlayerRank rank) {
        final String[] words = message.split(" ");
        final StringBuilder builder = new StringBuilder();

        for (String word : words) {
            final Emojis emoji = byText(word, rank);

            if (emoji == null) {
                builder.append(word);
            }
            else {
                builder.append(emoji.emoji).append(rank.getFormat().textColor());
            }

            builder.append(" ");
        }

        return builder.toString().trim();
    }

    @Nonnull
    public static List<Emojis> byRank(@Nonnull PlayerRank rank) {
        final List<Emojis> emojis = Lists.newArrayList();

        for (Emojis emoji : values()) {
            if (rank.isOrHigher(emoji.rank)) {
                emojis.add(emoji);
            }
        }

        return emojis;
    }
}
