package me.hapyl.fight.event;

import me.hapyl.fight.game.EnumDamageCause;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Represents wrapped damage input.
 */
public class DamageInput {

    private final Player player;
    private final LivingEntity entity;
    private final double damage;
    private final EnumDamageCause cause;
    private final boolean isCrit;

    public DamageInput(Player player, @Nullable LivingEntity entity, @Nullable EnumDamageCause damageCause, double originalDamage, boolean isCrit) {
        this.player = player;
        this.entity = entity;
        this.damage = originalDamage;
        this.cause = damageCause;
        this.isCrit = isCrit;
    }

    /**
     * Returns entity who is being damaged. (Victim)
     *
     * @return entity who is being damage. (Victim)
     */
    @Nullable
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Returns cause of the damage.
     *
     * @return cause of the damage.
     */
    public EnumDamageCause getDamageCause() {
        return cause;
    }

    /**
     * Returns player who damaged the entity.
     *
     * @return player who damaged the entity.
     */
    public Player getPlayer() {
        return player;
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
}
