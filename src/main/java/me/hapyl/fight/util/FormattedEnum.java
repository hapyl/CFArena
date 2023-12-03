package me.hapyl.fight.util;

import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface FormattedEnum extends Formatted {

    @Nonnull
    Color getColor();

    @Nonnull
    String getPrefix();

    @Nonnull
    String getName();

    @Nonnull
    default String getNameColored() {
        return getColor() + getName();
    }

    @Nonnull
    default String getPrefixColored() {
        return getColor() + getPrefix();
    }

    @Nonnull
    default String getFormatted() {
        return getPrefixColored() + " " + getName();
    }

    @Nonnull
    default String format(@Nonnull Function<FormattedEnum, String> consumer) {
        return consumer.apply(this);
    }

}
