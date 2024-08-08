package me.hapyl.fight.fastaccess;

import me.hapyl.eterna.module.chat.Chat;

public enum Category {

    SELECT_HERO,
    SELECT_MAP,
    SELECT_MODE,
    JOIN_TEAM,
    TOGGLE_SETTING,
    SELECT_GADGET,
    OTHER;

    private final String name;

    Category() {
        this.name = Chat.capitalize(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
