package me.hapyl.fight.game.talents.vortex;

import me.hapyl.eterna.module.reflect.glowing.GlowingColor;

import javax.annotation.Nonnull;

public enum StarState {

    BEING_ATTACKED {
        @Override
        void onSet(@Nonnull AstralStar star) {
            star.setColor(GlowingColor.RED);
        }
    },
    LINKING {
        @Override
        void onSet(@Nonnull AstralStar star) {
            star.setColorGlobal(GlowingColor.GOLD);
        }
    },
    EXPLODING {
        @Override
        void onSet(@Nonnull AstralStar star) {
            star.setColorGlobal(GlowingColor.DARK_RED);
        }
    };

    void onSet(@Nonnull AstralStar star) {
    }

    void onUnSet(@Nonnull AstralStar star) {
    }

}
