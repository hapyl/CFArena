package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.gui.ExcludeInFilter;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.eterna.module.util.UpsideDownText;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.color.GradientColor;
import me.hapyl.fight.store.Purchasable;
import me.hapyl.fight.util.FormattedEnum;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum Rarity implements RandomDrop, Purchasable, FormattedEnum {

    @ExcludeInFilter
    UNSET(Color.ERROR, "NOT SET", -1, 0.0f) {
        @Override
        public String toString() {
            return "";
        }
    },

    COMMON(
            Color.of("#b6dbd1"),
            "ᴄᴏᴍᴍᴏɴ",
            1_000, 0.35f
    ),

    UNCOMMON(
            Color.of("#12e63d"),
            "ᴜɴᴄᴏᴍᴍᴏɴ",
            2_500, 0.25f
    ),

    RARE(
            Color.of("#1283db"),
            "ʀᴀʀᴇ",
            5_000, 0.20f
    ),

    EPIC(
            new GradientColor("#e314b6", "#ad0789"),
            "ᴇᴘɪᴄ",
            10_000, 0.10f
    ),

    LEGENDARY(
            new GradientColor("#faa61e", "#fa7a1e"),
            "ʟᴇɢᴇɴᴅᴀʀʏ",
            20_000, 0.06f
    ),

    MYTHIC(
            new GradientColor("#8df7ad", "#02a602"),
            "ᴍʏᴛʜɪᴄᴀʟ",
            50_000, 0.03f
    ) {
        private final String PREFIX = Color.of("#007d25") + "&k1 ";
        private final String SUFFIX = Color.of("#30f26a") + " &k1&r";

        @Nonnull
        @Override
        public String prefix() {
            return PREFIX;
        }

        @Nonnull
        @Override
        public String suffix() {
            return SUFFIX;
        }
    },

    CURSED(
            new GradientColor("#a62017", "#cf4b42"),
            "pǝsɹnɔ",
            100_000, 0.01f
    ) {
        @Nonnull
        @Override
        public String prefix() {
            return "&8&l&k||| ";
        }

        @Nonnull
        @Override
        public String suffix() {
            return " &8&l&k|||";
        }

        @Nonnull
        @Override
        public String toString(@Nonnull String suffix) {

            return prefix() + color.color(makeSuffix(suffix) + " " + name) + suffix();
        }

        @Nonnull
        @Override
        public String makeSuffix(@Nonnull String string) {
            return UpsideDownText.format(Chat.reverseString(string));
        }

    };

    protected final Color color;
    protected final String name;

    private final long defaultPrice;
    private final float dropChance;

    Rarity(Color color, String name, long defaultPrice, float dropChance) {
        this.color = color;
        this.name = name;
        this.defaultPrice = defaultPrice;
        this.dropChance = dropChance;
    }

    @Override
    @Deprecated // legacy
    public float getDropChance() {
        return dropChance;
    }

    @Nonnull
    @Override
    public Currency getCurrency() {
        return Currency.COINS;
    }

    @Override
    public long getPrice() {
        return defaultPrice;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }

    @Nonnull
    public String prefix() {
        return "";
    }

    @Nonnull
    public String suffix() {
        return "";
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return name;
    }

    @Override
    public String toString() {
        return prefix() + color.color(name) + suffix();
    }

    @Nonnull
    public String toString(@Nonnull String suffix) {
        return prefix() + color.color(name + " " + makeSuffix(suffix)) + suffix();
    }

    @Nonnull
    public String makeSuffix(@Nonnull String string) {
        return SmallCaps.format(string);
    }

    @Nonnull
    @Override
    public String getFormatted() {
        return toString();
    }

    public boolean isFlexible() {
        return this == EPIC || this == LEGENDARY || this == MYTHIC;
    }

    @Nonnull
    public ChatColor getBukkitColor() {
        return switch (this) {
            case COMMON -> ChatColor.GRAY;
            case UNCOMMON -> ChatColor.DARK_GREEN;
            case RARE -> ChatColor.BLUE;
            case EPIC -> ChatColor.LIGHT_PURPLE;
            case LEGENDARY -> ChatColor.GOLD;
            case MYTHIC -> ChatColor.GREEN;
            default -> ChatColor.WHITE;
        };
    }

    @Deprecated
    public long getCompensation() {
        return defaultPrice / 10;
    }
}
