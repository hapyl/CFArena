package me.hapyl.fight.game.ui.display;

import javax.annotation.Nonnull;

public class BuffDisplay extends StringDisplay {

    public BuffDisplay(@Nonnull String string, int stay) {
        super(string, stay);

        this.initTransformation = transformationScale(0.75f);
        this.animation = AscendingDisplay.ANIMATION;
    }
}
