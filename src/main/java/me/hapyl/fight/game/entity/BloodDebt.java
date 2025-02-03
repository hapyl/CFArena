package me.hapyl.fight.game.entity;

import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BloodDebt {

    private final LivingGameEntity entity;
    private double amount;

    public BloodDebt(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.amount = 0.0d;
    }

    public void increment(double amount) {
        this.amount = Math.min(this.amount + amount, entity.getMaxHealth());

        // Fx
        entity.playSound(Sound.ENTITY_ZOMBIE_INFECT, 0.75f);
        entity.playSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
    }

    public void decrement(double amount) {
        this.amount = Math.max(this.amount - amount, 0);
    }

    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }

    public double amount() {
        return this.amount;
    }

    public boolean hasDebt() {
        return this.amount > 0.0d;
    }

    public void reset() {
        this.amount = 0.0d;
    }
}
