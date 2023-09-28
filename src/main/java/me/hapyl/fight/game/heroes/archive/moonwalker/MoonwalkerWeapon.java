package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.LeftClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
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
    public void onLeftClick(@Nonnull Player player, @Nonnull ItemStack item) {
        if (player.hasCooldown(Material.BOW)) {
            return;
        }

        final Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setDamage(this.getDamage() / 2.0d);
        arrow.setCritical(false);
        arrow.setShooter(player);

        GamePlayer.setCooldown(player, Material.BOW, 20);

        // fx
        PlayerLib.playSound(player, Sound.ENTITY_ARROW_SHOOT, 1.25f);
    }
}
