package me.hapyl.fight.game.damage;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

        // Don't reassign the damage if self damage!
        // That's the whole point of the system to
        // award the last damager even if player killed themselves.
        if (damager != null && entity != damager) {
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

    public static List<LivingEntity> damageAoE(@Nonnull Location location, double radius, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<LivingEntity> predicate) {
        final List<LivingEntity> entities = Utils.getEntitiesInRangeValidateRange(location, radius);
        entities.removeIf(predicate);

        for (LivingEntity entity : entities) {
            damage(entity, damage, damager, cause);
        }

        return entities;
    }

    // helpers
    @Nonnull
    public static DamageData getDamageData(@Nonnull LivingEntity entity) {
        return DAMAGE_DATA.computeIfAbsent(entity, DamageData::new);
    }

    public static void notifyChatIncoming(Player player, DamageData data) {
        final double damage = data.lastDamage;

        final String prefix = "&7[&c⚔&7] &f";
        String message = "&l%.2f &ffrom &l%s".formatted(damage, Chat.capitalize(data.getLastDamageCauseNonNull()));

        final LivingEntity lastDamager = data.rootLastDamager();

        if (lastDamager != null) {
            message += " &fby &l" + lastDamager.getName();
        }

        if (data.isCrit) {
            message += " &b&lCRITICAL";
        }

        Chat.sendMessage(player, prefix + message);
    }

    public static void notifyChatOutgoing(Player player, DamageData data) {
        final double damage = data.lastDamage;

        final String prefix = "&7[&a⚔&7] &f";

        Chat.sendMessage(
                player,
                prefix + "&l%.2f &fusing &l%s &fto &l%s%s".formatted(
                        damage,
                        Chat.capitalize(data.getLastDamageCauseNonNull()),
                        data.getEntity().getName(),
                        data.isCrit ? " &b&lCRITICAL" : ""
                )
        );
    }
}
