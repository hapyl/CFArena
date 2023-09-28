package me.hapyl.fight.infraction;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.FormattedEnum;

import javax.annotation.Nonnull;

public enum InfractionType implements FormattedEnum {

    CHAT_MUTE,
    TEMPORARY_BAN,
    PERMANENT_BAN;

    @Nonnull
    @Override
    public Color getColor() {
        return Color.ERROR;
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return "";
    }

    @Nonnull
    @Override
    public String getName() {
        return "";
    }
}
