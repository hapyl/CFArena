package me.hapyl.fight.game.talents.vortex;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum StarState {

    BEING_ATTACKED {
        @Override
        void onSet(@Nonnull AstralStar star) {
            star.setColor(ChatColor.RED);
        }
    },
    LINKING {
        @Override
        void onSet(@Nonnull AstralStar star) {
            star.setColorGlobal(ChatColor.GOLD);
        }
    },
    EXPLODING {
        @Override
        void onSet(@Nonnull AstralStar star) {
            star.setColorGlobal(ChatColor.DARK_RED);
        }
    };

    void onSet(@Nonnull AstralStar star) {
    }

    void onUnSet(@Nonnull AstralStar star) {
    }

}
