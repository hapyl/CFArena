package me.hapyl.fight.game.ui.display;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.spigotutils.module.util.BukkitUtils;

public class AttributeDisplay extends StringDisplay {

    public static final DisplayAnimation ANIMATION_BUFF = DisplayAnimation.relative(0.0d, BukkitUtils.GRAVITY / 2, 0.0d);
    public static final DisplayAnimation ANIMATION_DEBUFF = DisplayAnimation.relative(0.0d, -(BukkitUtils.GRAVITY / 2), 0.0d);

    public AttributeDisplay(final AttributeType type, boolean isBuff) {
        super(40);

        this.initTransformation = transformationScale(isBuff ? 0.75f : 0.5f);
        this.animation = isBuff ? ANIMATION_BUFF : ANIMATION_DEBUFF;
        this.string = isBuff ? ("&a↑ %s &aIncrease".formatted(type)) : ("&c↓ %s &cDecrease".formatted(type));
    }

}
