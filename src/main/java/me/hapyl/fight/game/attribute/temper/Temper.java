package me.hapyl.fight.game.attribute.temper;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public enum Temper {

    COMMAND, // for testing
    FLOWER_BREEZE,
    BERSERK_MODE,
    POISON_IVY,
    RADIATION,
    WYVERN_HEART,
    ENDER_TELEPORT,
    NIGHTMARE_BUFF,
    DARK_COVER,
    SHADOW_CLONE,
    BACKSTAB,
    ICE_CAGE,
    ETERNAL_FREEZE {
        @Override
        public boolean isDisplay() {
            return false;
        }
    },
    SHADOWSTRIKE,
    WITHERBORN {
        @Override
        public boolean isDisplay() {
            return false;
        }
    },
    STONE_CASTLE,

    MECHA_INDUSTRY;

    /**
     * Gets if a display should be spawned upon tempering.
     *
     * @return true if display should be spawned; false otherwise.
     */
    public boolean isDisplay() {
        return true;
    }

    @Nonnull
    public TemperInstance newInstance(@Nonnull String name) {
        return new TemperInstance(this, name);
    }

    @Nonnull
    public TemperInstance newAnonymousInstance() {
        return new TemperInstance(this, null);
    }

    public void temper(LivingGameEntity entity, AttributeType type, double value, int duration) {
        temper(entity.getAttributes(), type, value, duration);
    }

    public void temper(EntityAttributes attributes, AttributeType type, double value, int duration) {
        attributes.increaseTemporary(this, type, value, duration);
    }
}
