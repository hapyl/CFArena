package me.hapyl.fight.util;

import me.hapyl.eterna.module.chat.Chat;

import javax.annotation.Nonnull;

public interface StyledString {

    @Nonnull
    String toString();

    @Nonnull
    default String toStringLowerCase() {
        return toString().toLowerCase();
    }

    @Nonnull
    default String toStringUpperCase() {
        return toString().toUpperCase();
    }

    @Nonnull
    default String toStringCapitalise() {
        return Chat.capitalize(toStringLowerCase());
    }

}
