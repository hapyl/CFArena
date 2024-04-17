package me.hapyl.fight.game.talents.bloodfiend.candlebane;

import me.hapyl.spigotutils.module.util.CollectionUtils;

import javax.annotation.Nonnull;

public enum ClickType {

    LEFT("&e&lLEFT"),
    RIGHT("&6&lRIGHT");

    public final String string;

    ClickType(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    @Nonnull
    public static ClickType random() {
        return CollectionUtils.randomElement(values(), ClickType.LEFT);
    }
}
