package me.hapyl.fight.game.entity;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.util.Rangef;
import me.hapyl.spigotutils.module.math.Numbers;

public class Shield {

    private final float strength;
    private final double maxCapacity;
    private final ShieldType type;

    private double capacity;

    public Shield(double maxCapacity, @Rangef(max = 1.0f, insured = true) float strength) {
        this.maxCapacity = maxCapacity;
        this.strength = Numbers.clamp01(strength);
        this.type = ShieldType.DAMAGE;
        this.capacity = maxCapacity;
    }

    public void regenerate() {
        regenerate(maxCapacity);
    }

    public void regenerate(double amount) {
        capacity = Math.min(capacity + amount, maxCapacity);
    }

    public DamageInput takeHit(DamageInput data) {
        double damage = data.getDamage();

        if (type == ShieldType.HITS) {
            capacity--;
        }
        else {
            capacity -= damage;
        }

        if (capacity <= 0) {
            onBreak();
        }

        // Reduce damage
        damage -= (damage * strength);

        return DamageInput.clone(data, damage);
    }

    public void onBreak() {
    }

}
