package me.hapyl.fight.game.attribute;

import java.util.Random;

public class Attributes {

    protected double baseHealth;
    protected int baseAttack;
    protected int baseDefense;
    protected int baseSpeed;

    protected double criticalChance;
    protected double criticalDamage;

    public Attributes() {
        baseHealth = 100.0d;
        baseAttack = 100;
        baseDefense = 100;
        baseSpeed = 100;

        criticalChance = 20.0d;
        criticalDamage = 60.0d;
    }

    public double calculateDamage(double damage, Attributes damagerAttributes) {

        return damage;
    }

    public boolean isCrit() {
        final double random = new Random().nextDouble(0.0d, 100.0d);

        return random >= criticalDamage;
    }

    public double getBaseHealth() {
        return baseHealth;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public double getCriticalDamage() {
        return criticalDamage;
    }
}
