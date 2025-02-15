package me.hapyl.fight.game.entity.cooldown;

/**
 * Keep in mind cooldowns are in <b>millis, <i>not ticks</i></b>!
 */
public enum Cooldown {

    DWARF_LAVA(1000),
    BEAM_TOUCH(500),
    FEROCITY(100),
    NO_DAMAGE,
    PLAYER_PING(500),
    WITCH_POTION,
    CC_MESSAGE(1000),
    AMNESIA,
    JAPAN_BOOSTER(300),
    PORTAL(1000),
    INTERACT(50),
    ;

    public final long duration;

    Cooldown() {
        this(0);
    }

    Cooldown(long durationMillis) {
        this.duration = durationMillis;
    }
}
