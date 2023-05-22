package me.hapyl.fight.game.damage;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * A very workaround of a bukkit system, but won't work any other way.
 * <p>
 * Static because there is no reason for this to not be static.
 */
public final class DamageHandler {

    private static final Map<LivingEntity, DamageData> DAMAGE_DATA = Maps.newConcurrentMap();

    public static void clearAll() {
        DAMAGE_DATA.clear();
    }

    @Super
    public static void damage(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        final DamageData data = getDamageData(entity);

        if (damager != null) {
            data.lastDamager = damager;
        }

        if (cause != null) {
            data.lastDamageCause = cause;
        }

        data.lastDamage = damage;

        // Call the damage event
        data.wasHit = true; // This tag is VERY important for calculations
        entity.damage(damage, damager);
        data.wasHit = false;
    }

    public static void damage(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager) {
        damage(entity, damage, damager, null);
    }

    public static void damage(@Nonnull LivingEntity entity, double damage) {
        damage(entity, damage, null, null);
    }

    @Super
    public static void damageTick(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, int tick) {
        final int maximumNoDamageTicks = entity.getMaximumNoDamageTicks();
        tick = Numbers.clamp(tick, 0, maximumNoDamageTicks);

        entity.setMaximumNoDamageTicks(tick);
        damage(entity, damage, damager, cause == null ? EnumDamageCause.ENTITY_ATTACK : cause);
        entity.setMaximumNoDamageTicks(maximumNoDamageTicks);
    }

    public static void damageTick(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager, int tick) {
        damageTick(entity, damage, damager, null, tick);
    }

    public static void damageTick(@Nonnull LivingEntity entity, double damage, int tick) {
        damageTick(entity, damage, null, null, tick);
    }

    @Nonnull
    public static DamageData getDamageData(@Nonnull LivingEntity entity) {
        return DAMAGE_DATA.computeIfAbsent(entity, DamageData::new);
    }

}
