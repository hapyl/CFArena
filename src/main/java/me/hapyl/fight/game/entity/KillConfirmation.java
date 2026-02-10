package me.hapyl.fight.game.entity;

import org.bukkit.Sound;

import javax.annotation.Nonnull;

public enum KillConfirmation {

    KILL("&4⚔") {
        @Override
        public void onDisplay(@Nonnull GamePlayer player) {
            player.playSound(Sound.ITEM_SHIELD_BLOCK, 1.75f);
        }
    },
    ASSIST("&a\uD83C\uDF3F") {
        @Override
        public void onDisplay(@Nonnull GamePlayer player) {
            player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f);
        }
    };

    private final String prefix;

    KillConfirmation(@Nonnull String prefix) {
        this.prefix = prefix;
    }

    @Nonnull
    public String prefix() {
        return prefix;
    }

    public void onDisplay(@Nonnull GamePlayer player) {
    }
}
