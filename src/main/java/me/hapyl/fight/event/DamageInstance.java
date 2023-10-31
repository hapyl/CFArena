package me.hapyl.fight.event;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageInstance {

    protected final LivingGameEntity entity;
    public double damage;

    @Nullable protected GameEntity damager;
    @Nullable protected EnumDamageCause cause;
    protected boolean isCrit;

    private boolean finalized;
    private boolean cancel;

    public DamageInstance(LivingGameEntity entity, double damage) {
        this.entity = entity;
        this.damage = damage;
    }

    public void setFinalized() {
        this.finalized = true;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void fromOutput(@Nullable DamageOutput output) {
        if (output == null || finalized) {
            return;
        }

        finalized = output.isFinalized();
        damage = output.getDamage();

        if (!cancel) {
            cancel = output.isCancelDamage();
        }
    }

    public boolean isCancel() {
        return cancel;
    }

    public LivingGameEntity getEntity() {
        return entity;
    }

    @Nullable
    public GameEntity getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    @Nullable
    public EnumDamageCause getCause() {
        return cause;
    }

    public boolean isCrit() {
        return isCrit;
    }

    @Nonnull
    public DamageInput toInput() {
        return new DamageInput(entity, damager, cause, damage, isCrit);
    }

    @Nonnull
    public GameDamageEvent toEvent() {
        return new GameDamageEvent(entity, damager, damage, cause, isCrit);
    }
}
