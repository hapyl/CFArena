package me.hapyl.fight.game.damage;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.DamageFormat;
import me.hapyl.fight.game.DeathMessage;
import me.hapyl.fight.util.Copyable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;

public class DamageCause implements Copyable {

    public static final DamageCause EMPTY = new DamageCause("null", "null");

    // this is the same as vanilla 20 ticks
    public static final int DEFAULT_DAMAGE_TICKS = 10;

    private final DeathMessage deathMessage;
    private final Set<DamageFlag> flags;

    private DamageFormat damageFormat;
    private int damageTicks;

    private DamageCause(@Nonnull DeathMessage message) {
        this.deathMessage = message;
        this.flags = Sets.newHashSet(DamageFlag.CAN_CRIT, DamageFlag.CUSTOM);
        this.damageFormat = DamageFormat.DEFAULT;
        this.damageTicks = DEFAULT_DAMAGE_TICKS;
    }

    private DamageCause(String string, String suffix) {
        this(new DeathMessage(string, suffix));
    }

    @Nonnull
    public DeathMessage getDeathMessage() {
        return deathMessage;
    }

    @Nonnull
    public DamageFormat getDamageFormat() {
        return damageFormat;
    }

    public DamageCause setDamageFormat(@Nonnull DamageFormat damageFormat) {
        this.damageFormat = damageFormat;
        return this;
    }

    public final int getDamageTicks() {
        if (hasFlag(DamageFlag.IGNORES_DAMAGE_TICKS)) {
            // This value should really be 1, not 0,
            // since 0 is rapid damage, like RAPID damage
            return 1;
        }

        return damageTicks;
    }

    public DamageCause setDamageTicks(final int damageTicks) {
        this.damageTicks = Math.clamp(damageTicks, 1, 100);
        return this;
    }

    public boolean hasFlag(@Nonnull DamageFlag flag) {
        return flags.contains(flag);
    }

    public DamageCause addFlags(@Nonnull DamageFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));

        return this;
    }

    public DamageCause removeFlags(@Nonnull DamageFlag... flags) {
        for (DamageFlag flag : flags) {
            this.flags.remove(flag);
        }

        return this;
    }

    public DamageCause modifyFlag(@Nonnull DamageFlag flag, boolean isAdd) {
        if (isAdd) {
            addFlags(flag);
        }
        else {
            removeFlags(flag);
        }

        return this;
    }

    public DamageCause setTrueDamage() {
        return addFlags(DamageFlag.TRUE_DAMAGE);
    }

    public boolean isCustom() {
        return hasFlag(DamageFlag.CUSTOM);
    }

    public DamageCause setCustom(boolean custom) {
        return modifyFlag(DamageFlag.CUSTOM, custom);
    }

    public boolean isTrueDamage() {
        return hasFlag(DamageFlag.TRUE_DAMAGE);
    }

    public boolean isCanCrit() {
        return hasFlag(DamageFlag.CAN_CRIT);
    }

    public DamageCause setCanCrit(boolean canCrit) {
        return modifyFlag(DamageFlag.CAN_CRIT, canCrit);
    }

    public DamageCause setProjectile(boolean projectile) {
        return modifyFlag(DamageFlag.PROJECTILE, projectile);
    }

    @Nonnull
    public Set<DamageFlag> getFlags() {
        return flags;
    }

    @Nonnull
    @Override
    public DamageCause createCopy() {
        final DamageCause cause = new DamageCause(this.deathMessage);

        cause.flags.addAll(this.flags);
        cause.damageFormat = this.damageFormat;

        return cause;
    }

    /**
     * Creates a DamageCause that cannot crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    public static DamageCause nonCrit(@Nonnull String message, @Nonnull String suffix) {
        return of(message, suffix).setCanCrit(false);
    }

    /**
     * Creates a DamageCause that cannot crit.
     *
     * @param message - Message.
     */
    public static DamageCause nonCrit(@Nonnull String message) {
        return nonCrit(message, "");
    }

    /**
     * Creates a DamageCause that can crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    public static DamageCause of(@Nonnull String message, @Nonnull String suffix) {
        return new DamageCause(message, suffix);
    }

    /**
     * Creates a DamageCause that can crit.
     *
     * @param message - Message.
     */
    public static DamageCause of(@Nonnull String message) {
        return of(message, "by");
    }

    /**
     * Creates a DamageCause that are minecraft vanilla and cannot crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    public static DamageCause minecraft(@Nonnull String message, @Nonnull String suffix) {
        return new DamageCause(message, suffix).setCustom(false).setCanCrit(false);
    }

    /**
     * Creates a DamageCause that are minecraft vanilla and cannot crit.
     *
     * @param message - Message.
     */
    public static DamageCause minecraft(@Nonnull String message) {
        return minecraft(message, "");
    }
}
