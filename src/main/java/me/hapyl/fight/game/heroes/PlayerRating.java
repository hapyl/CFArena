package me.hapyl.fight.game.heroes;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Described;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public enum PlayerRating implements Described {

    ONE("&41", "Terrible!"),
    TWO("&42", "Very Bad!"),
    THREE("&43", "Disappointing."),
    FOUR("&c4", "Below Average."),
    FIVE("&65", "Average."),
    SIX("&e6", "Okay."),
    SEVEN("&27", "Enjoyable"),
    EIGHT("&a8", "Good!"),
    NINE("&a9", "Very good!"),
    TEN("&a&l10", "Amazing!");

    private static final Map<Integer, PlayerRating> byInt;

    static {
        byInt = Maps.newHashMap();

        for (PlayerRating rating : values()) {
            byInt.put(rating.toInt(), rating);
        }
    }

    private final String name;
    private final String description;

    PlayerRating(String name, String description) {
        this.name = "&8[" + name + "&8]&7";
        this.description = description;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public int toInt() {
        return ordinal() + 1;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nullable
    public static PlayerRating fromInt(@Range(from = 1, to = 10) int i) {
        return byInt.get(i);
    }
}
