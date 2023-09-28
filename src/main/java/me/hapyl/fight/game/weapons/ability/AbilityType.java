package me.hapyl.fight.game.weapons.ability;

import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.event.block.Action;

import javax.annotation.Nullable;

public enum AbilityType {

    RIGHT_CLICK {
        private final Action[] clicks = { Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR };

        @Nullable
        @Override
        public Action[] getClickTypes() {
            return clicks;
        }
    },
    LEFT_CLICK {
        private final Action[] clicks = { Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR };

        @Nullable
        @Override
        public Action[] getClickTypes() {
            return clicks;
        }
    },
    @Deprecated HELD;

    @Override
    public String toString() {
        return Chat.capitalize(this).toUpperCase();
    }

    @Nullable
    public Action[] getClickTypes() {
        return null;
    }
}
