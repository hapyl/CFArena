package me.hapyl.fight.game.weapons;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BowWeapon extends Weapon {

    private int cooldown;

    public BowWeapon() {
        this("", "", 1.0d);
    }

    public BowWeapon(@Nonnull String name, @Nonnull String about, double damage) {
        super(Material.BOW, name, about, damage);

        this.cooldown = Weapon.DEFAULT_BOW_COOLDOWN;
    }

    public int getShotCooldown() {
        return cooldown;
    }

    public BowWeapon setShotCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    @Override
    public void appendLore(@Nonnull ItemBuilder builder) {
        builder.addLore();
        builder.addLore("&e&lᴀᴛᴛʀɪʙᴜᴛᴇs:");

        addDynamicLore(builder, " ғɪʀᴇ ʀᴀᴛᴇ: &f&l%s", cooldown, t -> Tick.round(t.intValue()) + "s");
    }
}
