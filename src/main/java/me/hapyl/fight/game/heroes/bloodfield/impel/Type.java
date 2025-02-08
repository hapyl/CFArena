package me.hapyl.fight.game.heroes.bloodfield.impel;

import me.hapyl.eterna.module.util.CollectionUtils;

import javax.annotation.Nonnull;

public enum Type {

    JUMP("JUMP!"),
    SNEAK("SNEAK!"),
    CLICK_UP("CLICK UP!"),
    CLICK_DOWN("CLICK DOWN!"),
    USE_ABILITY("USE TALENT!");

    private final String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public static Type random() {
        return CollectionUtils.randomElement(values(), JUMP);
    }
}
