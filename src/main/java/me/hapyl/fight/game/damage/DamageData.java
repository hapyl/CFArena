package me.hapyl.fight.game.damage;

import me.hapyl.fight.annotate.Important;
import me.hapyl.fight.game.EnumDamageCause;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageData {

    private final LivingEntity entity;

    @Nullable
    public LivingEntity lastDamager;
    @Nullable
    public EnumDamageCause lastDamageCause;

    public double lastDamage;
    public boolean isCrit;

    @Important("Notifies the event that the damage is custom, not vanilla.")
    protected boolean wasHit;

    public DamageData(LivingEntity entity) {
        this.entity = entity;
    }

    public boolean isCustomDamage() {
        return wasHit;
    }

    public boolean isNativeDamage() {
        return !wasHit;
    }

    public double getLastDamage() {
        return lastDamage;
    }

    @Nullable
    public EnumDamageCause getLastDamageCause() {
        return lastDamageCause;
    }

    @Nonnull
    public EnumDamageCause getLastDamageCauseNonNull() {
        return lastDamageCause == null ? EnumDamageCause.ENTITY_ATTACK : lastDamageCause;
    }

    @Nullable
    public LivingEntity getLastDamager() {
        return lastDamager;
    }

    /**
     * Roots to the actual entity damager.
     */
    @Nullable
    public LivingEntity rootLastDamager() {
        if (lastDamager == null) {
            return null;
        }

        if (lastDamager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof LivingEntity living) {
                return living;
            }

            return null;
        }

        return lastDamager;
    }

    @Nullable
    public <T> T getLastDamager(Class<T> cast) {
        final LivingEntity living = rootLastDamager();

        if (cast.isInstance(living)) {
            return cast.cast(living);
        }

        return null;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setLastDamageCauseIfNative(EntityDamageEvent.DamageCause cause) {
        if (isNativeDamage()) {
            lastDamageCause = EnumDamageCause.getFromCause(cause);
        }
    }

    public void setLastDamagerIfNative(LivingEntity living) {
        if (isNativeDamage()) {
            lastDamager = living;
        }
    }

    @Override
    public String toString() {
        return "DamageData{" +
                "entity=" + entity +
                ", lastDamager=" + lastDamager +
                ", lastDamageCause=" + lastDamageCause +
                ", lastDamage=" + lastDamage +
                ", wasHit=" + wasHit +
                '}';
    }
}
