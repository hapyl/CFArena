package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.annotate.ExcludeInSort;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.color.ColorFlag;
import me.hapyl.fight.game.color.GradientColor;
import me.hapyl.fight.util.FormattedEnum;

import javax.annotation.Nonnull;

public enum Rarity implements RandomDrop, FormattedEnum {

    @ExcludeInSort
    UNSET(Color.ERROR, "NOT SET", 0, -1, -1),

    COMMON(
            new Color("#b6dbd1"),
            "ᴄᴏᴍᴍᴏɴ",
            100, 1, 0.35f
    ),
    UNCOMMON(
            new Color("#12e63d"),
            "ᴜɴᴄᴏᴍᴍᴏɴ",
            200, 2, 0.25f
    ),
    RARE(
            new Color("#1283db"),
            "ʀᴀʀᴇ",
            500, 5, 0.20f
    ),
    EPIC(
            new GradientColor("#e314b6", "#ad0789").setFlags(ColorFlag.BOLD),
            "ᴇᴘɪᴄ",
            1_000, 10, 0.10f
    ),
    LEGENDARY(
            new GradientColor("#faa61e", "#fa7a1e").setFlags(ColorFlag.BOLD),
            "ʟᴇɢᴇɴᴅᴀʀʏ",
            2_000, 20, 0.07f
    ),
    MYTHIC(
            new GradientColor("#8df7ad", "#02a602").setFlags(ColorFlag.BOLD),
            "ᴍʏᴛʜɪᴄᴀʟ",
            5_000, 50, 0.03f
    ) {
        private final String PREFIX = new Color("#007d25") + "&k1 ";
        private final String SUFFIX = new Color("#30f26a") + " &k1&r";

        @Override
        public String prefix() {
            return PREFIX;
        }

        @Override
        public String suffix() {
            return SUFFIX;
        }
    };

    private final Color color;
    private final String name;
    private final long coinCompensation;
    private final long dustCompensation;
    private final float dropChance;

    Rarity(Color color, String name, long coinCompensation, long dustCompensation, float dropChance) {
        this.color = color;
        this.name = name;
        this.coinCompensation = coinCompensation;
        this.dustCompensation = dustCompensation;
        this.dropChance = dropChance;
    }

    @Override
    public float getDropChance() {
        return dropChance;
    }

    public long getCoinCompensation() {
        return coinCompensation;
    }

    public long getDustCompensation() {
        return dustCompensation;
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

    public String prefix() {
        return "";
    }

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
    @Override
    public String getFormatted() {
        return toString();
    }

    public boolean isFlexible() {
        return this == EPIC || this == LEGENDARY || this == MYTHIC;
    }

    public String getCompensationString() {
        return "%s%,d %s &8& %s%,d%s".formatted(
                Currency.COINS.getColor(), coinCompensation, Currency.COINS.getPrefixColored(),
                Currency.CHEST_DUST.getColor(), dustCompensation, Currency.CHEST_DUST.getPrefix()
        );
    }
}
