package me.hapyl.fight.game.heroes.bloodfield;

import me.hapyl.eterna.module.util.CollectionUtils;

import javax.annotation.Nonnull;

public enum ImpelType {

    JUMP("JUMP!"),
    SNEAK("SNEAK!"),
    CLICK_UP("CLICK UP!"),
    CLICK_DOWN("CLICK DOWN!"),
    USE_ABILITY("USE TALENT!");

    private final String name;

    ImpelType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public static ImpelType random() {
        return CollectionUtils.randomElement(values(), JUMP);
    }
}
