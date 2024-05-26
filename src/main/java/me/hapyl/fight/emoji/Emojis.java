package me.hapyl.fight.emoji;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public enum Emojis {

    // Default
    PEACE(":peace:", "&2✌"),
    CLOWN(":clown:", "&d\uD83E\uDD21"),

    // Vip
    SMILE(":)", "&a🙂", PlayerRank.VIP),
    FROWN(":(", "&c🙁", PlayerRank.VIP),
    HEART("<3", "&c❤", PlayerRank.VIP),
    LOVE(":love:", "&d🥰", PlayerRank.VIP),
    HI("o/", "&6( &bﾟ&6◡&bﾟ&6)&e/", PlayerRank.VIP),

    // Premium
    SNOWFLAKE(":snowflake:", "&b❄", PlayerRank.PREMIUM),
    SHRUG(":shrug:", "&6¯\\_(&e&lツ&6)_/¯", PlayerRank.PREMIUM),
    CROWN(":crown:", "&6👑", PlayerRank.PREMIUM),
    BLUSH(":blush:", "&6(&d✿&b&l◡&6‿&b&l◡&6)", PlayerRank.PREMIUM),
    NEUTRAL_FACE("._.", "&6(&a&l。&6_&a&l。&6)", PlayerRank.PREMIUM),

    // Special
    RELIC(":relic:", Color.DIAMOND + "💎") {
        @Override
        public boolean canUse(@Nonnull PlayerProfile profile) {
            final CollectibleEntry collectibleEntry = profile.getDatabase().collectibleEntry;

            return collectibleEntry.hasFoundAll();
        }
    },

    ;

    private final String text;
    private final String emoji;
    private final PlayerRank rank;

    Emojis(String text, String emoji) {
        this(text, emoji, PlayerRank.DEFAULT);
    }

    Emojis(String text, String emoji, PlayerRank rank) {
        this.text = text;
        // We need to replace the color char to allow non-admins to send colored messages
        this.emoji = emoji.replace('&', ChatColor.COLOR_CHAR);
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

    public boolean canUse(@Nonnull PlayerProfile profile) {
        return profile.getRank().isOrHigher(this.rank);
    }

    /**
     * Gets an {@link Emojis} by text.
     * <br>
     * Emojis are case-sensitive!
     *
     * @param text - Text
     * @return an emoji or null.
     */
    @Nullable
    public static Emojis byText(@Nonnull String text) {
        return byText(text, null);
    }

    /**
     * Gets an {@link Emojis} by text.
     * This will also check if player can use the emoji.
     * <br>
     * Emojis are case-sensitive!
     *
     * @param text - Text
     * @return an emoji or null.
     */
    @Nullable
    public static Emojis byText(@Nonnull String text, @Nullable PlayerProfile profile) {
        for (Emojis emoji : values()) {
            if (emoji.text.equals(text)) {
                if (profile != null && !emoji.canUse(profile)) {
                    continue;
                }

                return emoji;
            }
        }

        return null;
    }

    /**
     * Replaces all emoji occurrences in the message with an associated emoji.
     *
     * @param message - Message to replace.
     * @param profile - Profile.
     * @return a new message.
     */
    @Nonnull
    public static String replaceEmojis(@Nonnull String message, @Nonnull PlayerProfile profile) {
        final String[] words = message.split(" ");
        final StringBuilder builder = new StringBuilder();

        for (String word : words) {
            final Emojis emoji = byText(word, profile);

            if (emoji == null) {
                builder.append(word);
            }
            else {
                builder.append(emoji.emoji).append(profile.getRank().getFormat().textColor());
            }

            builder.append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Gets all available {@link Emojis} to the give {@link PlayerProfile}.
     *
     * @param profile - Player.
     * @return the available emojis.
     */
    @Nonnull
    public static List<Emojis> getAvailable(@Nonnull PlayerProfile profile) {
        final List<Emojis> emojis = Lists.newArrayList();

        for (Emojis emoji : values()) {
            if (emoji.canUse(profile)) {
                emojis.add(emoji);
            }
        }

        return emojis;
    }

}
