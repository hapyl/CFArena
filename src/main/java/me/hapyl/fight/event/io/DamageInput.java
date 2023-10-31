package me.hapyl.fight.event.io;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents wrapped damage input.
 */
// FIXME (hapyl): 031, Oct 31: Delete all bukkit player casts!
public class DamageInput {

    private final LivingGameEntity entity;
    private final GameEntity damager;
    private final double damage;
    private final EnumDamageCause cause;
    private final boolean isCrit;

    public DamageInput(LivingGameEntity entity, @Nullable GameEntity damager, @Nullable EnumDamageCause damageCause, double originalDamage, boolean isCrit) {
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
     * Gets the entity that is being damaged.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    /**
     * @return Gets the <b>entity</b> as a player <b>if the entity is a player</b>, throws {@link IllegalArgumentException} otherwise.
     */
    @Nonnull
    public GamePlayer getEntityAsPlayer() {
        if (entity instanceof GamePlayer gamePlayer) {
            return gamePlayer;
        }

        throw new IllegalArgumentException(entity + " is not a player");
    }

    /**
     * Gets the entity as a bukkit player, throws {@link IllegalArgumentException} if entity is not a player.
     */
    @Nonnull
    public Player getBukkitPlayer() {
        return getEntityAsPlayer().getPlayer();
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

    /**
     * Gets the damager as a living game entity if present, null otherwise.
     */
    @Nullable
    public LivingGameEntity getDamagerAsLiving() {
        if (damager instanceof LivingGameEntity living) {
            return living;
        }

        return null;
    }

    /**
     * Gets the damager as a GamePlayer if it is a GamePlayer, null otherwise.
     */
    @Nullable
    public GamePlayer getDamagerAsPlayer() {
        if (damager instanceof GamePlayer player) {
            return player;
        }

        return null;
    }

    @Nullable
    public Player getDamagerAsBukkitPlayer() {
        final GamePlayer player = getDamagerAsPlayer();
        return player == null ? null : player.getPlayer();
    }

    @Nonnull
    public Optional<GameEntity> getDamagerOptional() {
        return damager == null ? Optional.empty() : Optional.of(damager);
    }

    public static DamageInput clone(DamageInput data, double newDamage) {
        return new DamageInput(data.entity, data.damager, data.cause, newDamage, data.isCrit);
    }
}
