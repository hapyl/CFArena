package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.SmallCaps;

import javax.annotation.Nonnull;

public interface SmallCapsDescriber {

    @Nonnull
    String getNameSmallCaps();

    @Nonnull
    default String toSmallCaps(@Nonnull Enum<?> anEnum) {
        return toSmallCaps(Chat.capitalize(anEnum).toLowerCase());
    }

    @Nonnull
    default String toSmallCaps(@Nonnull String string) {
        return SmallCaps.format(string);
    }

}
