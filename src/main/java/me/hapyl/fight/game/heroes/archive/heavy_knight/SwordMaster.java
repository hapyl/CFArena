package me.hapyl.fight.game.heroes.archive.heavy_knight;

import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.heavy_knight.Slash;
import me.hapyl.fight.game.talents.archive.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.archive.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SwordMaster extends Hero {

    /**
     * BIG ASS SWORD
     * <p>
     * Ability 1:
     * - Uppercut pushes up
     * <p>
     * Ability 2:
     * - Jump up, pushes down enemies
     * <p>
     * Ability 3:
     * - Splash front AoE 3 blocks large knockback
     * <p>
     * Weapon Ability:
     * - Dash
     * <p>
     * If used 1->2->3 (all hit):
     * - Slow, weakness target
     * - 3 ability reset CD
     * <p>
     * General:
     * - Very slow base speed
     * <p>
     * ULTIMATE ():
     * - Removes armor
     * - Gains speed
     * - Strength
     * - All abilities has 1 tick cooldown
     * - Takes x2 damage
     * + Duration 3s
     */

    public SwordMaster(@Nonnull Heroes handle) {
        super(handle, "Heavy Knight");

        setArchetype(Archetype.DAMAGE);

        setItem("4b2a75f05437ba2e28fb2a7d0eb6697a6e091ce91072b5c4ff1945295b092");

        final HeroAttributes attributes = getAttributes();
        attributes.setSpeed(75);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Material.NETHERITE_CHESTPLATE);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setWeapon(new Weapon(Material.NETHERITE_SWORD).setName("Basta").setDamage(12.0d).setAttackSpeed(-0.2d));
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onStart(player);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        // attribute
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return UltimateCallback.OK;
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
    public Slash getThirdTalent() {
        return (Slash) Talents.SLASH.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
