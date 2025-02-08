package me.hapyl.fight.game.entity;

import me.hapyl.fight.event.BloodDebtChangeEvent;
import org.bukkit.Sound;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;

public class BloodDebt {

    private final LivingGameEntity entity;
    private double amount;

    public BloodDebt(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.amount = 0.0d;
    }

    public void increment(double amount) {
        final double newAmount = Math.clamp(this.amount + amount, 0, entity.getMaxHealth());
        final boolean isNew = this.amount == 0 && newAmount > 0;

        // Call event
        if (new BloodDebtChangeEvent(entity, this.amount, newAmount).call()) {
            return;
        }

        this.amount = newAmount;

        // Play fx is we had no debt before
        if (isNew) {
            entity.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 0.75f);
            entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
        }
    }

    public void decrement(double amount) {
        increment(-amount);
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

    public void incrementOfMaxHealth(@Range(from = 0, to = 1) double percentage) {
        increment(entity.getMaxHealth() * percentage);
    }

}
