package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.LeftClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class MoonwalkerWeapon extends Weapon implements LeftClickable {
    public MoonwalkerWeapon() {
        super(Material.BOW);

        setName("Stinger");
        setDescription("""
                A unique bow made of unknown materials... seems to have two firing modes.
                """);
        setDamage(4.5d);
        setId("MOON_WEAPON");

        setAbility(AbilityType.LEFT_CLICK, Ability.of("Quick Shot", """
                Shoot a quick arrow that deals &b50%&7 of the normal damage.
                """, this));
    }

    @Override
    public void onLeftClick(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        if (player.hasCooldown(Material.BOW)) {
            return;
        }

        player.launchProjectile(Arrow.class, self -> {
            self.setDamage(getDamage() / 2);
            self.setCritical(false);
            self.setShooter(player.getPlayer());
        });

        player.setCooldown(Material.BOW, 20);
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
    }
}
