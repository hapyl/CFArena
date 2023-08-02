package me.hapyl.fight.event;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageInstance {

    protected final LivingGameEntity entity;
    @Nullable protected GameEntity damager;
    protected double damage;
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

    @Nonnull
    public DamageInput toInput() {
        return new DamageInput(entity, damager, cause, damage, isCrit);
    }
}
