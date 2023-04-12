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

    public DamageInput(Player player, @Nullable LivingEntity entity, @Nullable EnumDamageCause damageCause, double originalDamage) {
        this.player = player;
        this.entity = entity;
        this.damage = originalDamage;
        this.cause = damageCause;
    }

    public DamageInput(Player player, @Nullable LivingEntity entity, double originalDamage) {
        this(player, entity, null, originalDamage);
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
}
