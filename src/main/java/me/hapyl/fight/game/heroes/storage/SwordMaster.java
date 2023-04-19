package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.storage.heavy_knight.Uppercut;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SwordMaster extends Hero implements DisabledHero {

    /**
     * BIG ASS SWORD
     *
     * Ability 1:
     * - Uppercut pushes up
     *
     * Ability 2:
     * - Jump up, pushes down enemies
     *
     * Ability 3:
     * - Splash front AoE 3 blocks large knockback
     *
     * Weapon Ability:
     * - Dash
     *
     * If used 1->2->3 (all hit):
     * - Slow, weakness target
     * - 3 ability reset CD
     *
     * General:
     * - Very slow base speed
     *
     * ULTIMATE ():
     * - Removes armor
     * - Gains speed
     * - Strength
     * - All abilities has 1 tick cooldown
     * - Takes x2 damage
     * + Duration 3s
     */

    public SwordMaster() {
        super("Heavy Knight");

        setItem("4b2a75f05437ba2e28fb2a7d0eb6697a6e091ce91072b5c4ff1945295b092");

        final HeroEquipment equipment = getEquipment();
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
    public Uppercut getFirstTalent() {
        return (Uppercut) Talents.UPPERCUT.getTalent();
    }

    @Override
    public Updraft getSecondTalent() {
        return (Updraft) Talents.UPDRAFT.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
