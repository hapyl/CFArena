package me.hapyl.fight.game.damage;

import me.hapyl.fight.game.attribute.AttributeType;

public enum DamageType {

    /**
     * A normal melee attack.
     * <br>
     * Is considered as "direct" damage and boosted by {@link AttributeType#DIRECT_DAMAGE_BONUS}.
     */
    DIRECT_MELEE {
        @Override
        public boolean isDirect() {
            return true;
        }
    },

    /**
     * A normal projectile attack or a ray-cast attack.
     * <br>
     * Is considered as "direct" damage and boosted by {@link AttributeType#DIRECT_DAMAGE_BONUS}.
     * <br>
     * Realistically, there isn't a difference between melee and range damage other that range damage shows the distance on kill.
     */
    DIRECT_RANGE {
        @Override
        public boolean isDirect() {
            return true;
        }
    },

    /**
     * An attack via talent, e.g.: Shock Dart explosion, Triple Shot hit, etc.
     * <br>
     * Boosted by {@link AttributeType#TALENT_DAMAGE_BONUS}.
     */
    TALENT,

    /**
     * An attack via ultimate, e.g.: BOOM BOW explosion, Feel the Breeze, etc.
     * <br>
     * Boosted by {@link AttributeType#ULTIMATE_DAMAGE_BONUS}.
     */
    ULTIMATE,

    /**
     * An environment attack, e.g.: Fall, Lava, etc.
     */
    ENVIRONMENT {
        @Override
        public boolean isEnvironment() {
            return true;
        }
    },

    /**
     * @deprecated Do not use this for actual damage causes, only for development and testing!
     */
    @Deprecated
    OTHER;

    public boolean isDirect() {
        return false;
    }

    public boolean isEnvironment() {
        return false;
    }

}
