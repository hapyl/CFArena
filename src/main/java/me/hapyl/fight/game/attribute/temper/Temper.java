package me.hapyl.fight.game.attribute.temper;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.SmallCapsDescriber;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Used as a name for a temper.
 *
 * @see #newInstance()
 * @see TemperInstance
 * @see AttributeTemperTable
 */
public enum Temper implements SmallCapsDescriber {

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
    ETERNAL_FREEZE(false),
    SHADOWSTRIKE,
    SLOWING_AURA,
    STONE_CASTLE,
    MECHA_INDUSTRY,
    TAMING_THE_TIME(false),
    CONTAINMENT(false),
    ALCHEMIST(false),
    WITCHER(false),
    TAMER_WOLF(false),
    TAMER_LASER,
    CHILL_AURA,
    HACKED,
    CIPHER_LOCK,
    LOCKDOWN,
    ORC_GROWL,
    MALEDICTION_VEIL,
    SOUL_CRY,
    BLADE_BARRAGE,
    YRDED,
    SHARK(false),
    SABOTEUR,
    SPIRITUAL_CLEANSING,
    SLIME_GUNK,
    SHAMANS_MARK,
    ULTIMATE_SACRIFICE,
    POWER_SLASH(false),
    SECOND_WIND,
    EXTRA_CUTS,
    THROWING_KNIFE,
    SMOKE_BOMB,
    ICY_SHARDS,
    DARKNESS,
    AURORA_BUFF_1(false),
    AURORA_BUFF_2(false),
    AURORA_BUFF_3(false),
    DIVINE_INTERVENTION(false),
    VOID_SHIELD,
    NYX(false),
    LEGION(false),

    /* Keep semicolon on this line for easier addition. */;

    private final String smallCaps;
    private final boolean isDisplay;

    Temper() {
        this(true);
    }

    Temper(boolean isDisplay) {
        this.smallCaps = toSmallCaps(this);
        this.isDisplay = isDisplay;
    }

    @Nonnull
    @Override
    public String getNameSmallCaps() {
        return smallCaps;
    }

    /**
     * Gets if a display should be spawned upon tempering.
     *
     * @return true if display should be spawned; false otherwise.
     */
    public boolean isDisplay() {
        return isDisplay;
    }

    @Nonnull
    public TemperInstance newInstance() {
        return isDisplay ? newInstance(smallCaps) : newAnonymousInstance();
    }

    @Nonnull
    public TemperInstance newInstance(@Nonnull String name) {
        return new TemperInstance(this, name);
    }

    @Nonnull
    public TemperInstance newAnonymousInstance() {
        return new TemperInstance(this, null);
    }

    /**
     * @deprecated prefer providing applier {@link #temper(LivingGameEntity, AttributeType, double, int, LivingGameEntity)}
     */
    @Deprecated
    public void temper(@Nonnull LivingGameEntity entity, @Nonnull AttributeType type, double value, int duration) {
        temper(entity, type, value, duration, null);
    }

    public void temper(@Nonnull LivingGameEntity entity, @Nonnull AttributeType type, double value, int duration, @Nullable LivingGameEntity applier) {
        entity.getAttributes().increaseTemporary(this, type, value, duration, applier);
    }

}
