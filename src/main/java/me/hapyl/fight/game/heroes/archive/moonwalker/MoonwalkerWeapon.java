package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoonwalkerWeapon extends Weapon {
    public MoonwalkerWeapon() {
        super(Material.BOW);

        setName("Stinger");
        setDescription("""
                A unique bow made of unknown materials... seems to have two firing modes.
                """);
        setDamage(4.5d);
        setId("MOON_WEAPON");

        setAbility(AbilityType.LEFT_CLICK, new QuickShot());
    }

    public class QuickShot extends Ability {

        public QuickShot() {
            super("Quick Shot", """
                    Shoot a quick arrow that deals &b50%&7 of the normal damage.
                    """);

            setCooldown(20);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            player.launchProjectile(Arrow.class, self -> {
                self.setDamage(getDamage() / 2);
                self.setCritical(false);
                self.setShooter(player.getPlayer());
            });

            player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
            return Response.OK;
        }
    }

}
