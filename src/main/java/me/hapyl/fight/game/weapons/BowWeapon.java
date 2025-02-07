package me.hapyl.fight.game.weapons;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BowWeapon extends Weapon {

    private int cooldown;

    public BowWeapon(@Nonnull Key key) {
        super(Material.BOW, key);

        this.cooldown = DEFAULT_BOW_COOLDOWN;
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

    @Nonnull
    public static BowWeapon of(@Nonnull Key key, @Nonnull String name, @Nonnull String description, double damage) {
        final BowWeapon weapon = new BowWeapon(key);
        weapon.setName(name);
        weapon.setDescription(description);
        weapon.setDamage(damage);

        return weapon;
    }
}
