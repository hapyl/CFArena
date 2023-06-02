package me.hapyl.fight.game.ui.display;

import me.hapyl.spigotutils.module.util.BukkitUtils;

import javax.annotation.Nonnull;

public class DebuffDisplay extends StringDisplay {

    public static final DisplayAnimation ANIMATION_DEBUFF = DisplayAnimation.relative(0.0d, -(BukkitUtils.GRAVITY / 2), 0.0d);

    public DebuffDisplay(@Nonnull String string, int stay) {
        super(string, stay);

        this.initTransformation = transformationScale(0.5f);
        this.animation = ANIMATION_DEBUFF;
    }
}
