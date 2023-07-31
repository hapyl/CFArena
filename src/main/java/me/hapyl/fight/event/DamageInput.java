package me.hapyl.fight.event;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents wrapped damage input.
 */
public class DamageInput {

    private final GameEntity entity;
    private final GameEntity damager;
    private final double damage;
    private final EnumDamageCause cause;
    private final boolean isCrit;

    public DamageInput(GameEntity entity, @Nullable GameEntity damager, @Nullable EnumDamageCause damageCause, double originalDamage, boolean isCrit) {
        this.entity = entity;
        this.damager = damager;
        this.damage = originalDamage;
        this.cause = damageCause;
        this.isCrit = isCrit;
    }

    /**
     * Gets the entity who is either being hit, or damaging based on the event.
     */
    @Nullable
    public GameEntity getDamager() {
        return damager;
    }

    /**
     * Returns cause of the damage.
     *
     * @return cause of the damage.
     */
    @Nullable
    public EnumDamageCause getDamageCause() {
        return cause;
    }

    @Nonnull
    public EnumDamageCause getDamageCauseOr(@Nonnull EnumDamageCause def) {
        return cause == null ? def : cause;
    }

    /**
     * Gets the player, either the player who got hit or the player who is damaging based on the event.
     */
    @Nonnull
    public GameEntity getEntity() {
        return entity;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        if (entity instanceof GamePlayer gamePlayer) {
            return gamePlayer;
        }

        throw new IllegalArgumentException(entity + " is not a player");
    }

    @Nonnull
    public Player getBukkitPlayer() {
        return getPlayer().getPlayer();
    }

    /**
     * Returns final amount of damage.
     * By <b>final</b> I mean that all calculations have been done.
     *
     * @return final amount of damage.
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Returns true if the cause of this damage was an {@link EnumDamageCause#ENTITY_ATTACK} or {@link EnumDamageCause#ENTITY_ATTACK_NON_CRIT}.
     *
     * @return true if the cause of this damage was an {@link EnumDamageCause#ENTITY_ATTACK} or {@link EnumDamageCause#ENTITY_ATTACK_NON_CRIT}.
     */
    public boolean isEntityAttack() {
        return cause == EnumDamageCause.ENTITY_ATTACK || cause == EnumDamageCause.ENTITY_ATTACK_NON_CRIT;
    }

    /**
     * Returns true if the damage was critical; false otherwise.
     *
     * @return true if the damage was critical; false otherwise.
     */
    public boolean isCrit() {
        return isCrit;
    }

    public static DamageInput clone(DamageInput data, double newDamage) {
        return new DamageInput(data.entity, data.damager, data.cause, newDamage, data.isCrit);
    }
}
