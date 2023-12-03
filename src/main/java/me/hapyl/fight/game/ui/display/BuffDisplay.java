package me.hapyl.fight.game.ui.display;

import me.hapyl.spigotutils.module.util.BukkitUtils;

import javax.annotation.Nonnull;

public class BuffDisplay extends StringDisplay {

    public static final DisplayAnimation ANIMATION_BUFF = DisplayAnimation.relative(0.0d, BukkitUtils.GRAVITY / 2, 0.0d);

    public BuffDisplay(@Nonnull String string, int stay) {
        super(string, stay);

        this.initTransformation = transformationScale(0.75f);
        this.animation = ANIMATION_BUFF;
    }
}
