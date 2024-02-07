package me.hapyl.fight.game.weapons;

import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BowWeapon extends Weapon {

    private int cooldown;

    public BowWeapon() {
        super(Material.BOW);

        this.cooldown = Weapon.DEFAULT_BOW_COOLDOWN;
    }

    public BowWeapon(@Nonnull String name, @Nonnull String about, double damage) {
        super(Material.BOW, name, about, damage);
    }

    public int getShotCooldown() {
        return cooldown;
    }

    public BowWeapon setShotCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

}
