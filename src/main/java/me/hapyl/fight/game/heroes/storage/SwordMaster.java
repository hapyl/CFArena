package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SwordMaster extends Hero {

    /**
     * BIG ASS SWORD
     *
     * Ability 1:
     * - Uppercut pushes up
     *
     * Ability 2:
     * - Jump up
     *
     * Ability 3:
     * - Splash
     */

    public SwordMaster() {
        super("Heavy Knight");

        setItem("4b2a75f05437ba2e28fb2a7d0eb6697a6e091ce91072b5c4ff1945295b092");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(Material.NETHERITE_CHESTPLATE);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setWeapon(new Weapon(Material.NETHERITE_SWORD).setName("Basta").setDamage(12.0d).setAttackSpeed(-0.2d));
    }

    @Override
    public void onRespawn(Player player) {
        onStart(player);
    }

    @Override
    public void onStart(Player player) {
        PlayerLib.addEffect(player, PotionEffectType.SLOW, 999999, 1);
    }

    @Override
    public void useUltimate(Player player) {

    }

    @Override
    public Talent getFirstTalent() {
        return null;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
